<?php

namespace App\Support;

use App\Models\AppInstall;
use Carbon\Carbon;
use Illuminate\Database\Query\Builder;
use Illuminate\Support\Facades\DB;

/**
 * Install counts for the card on the app's landing page and the admin panel's install report.
 *
 * Installs are counted from two tables:
 *  - app_installs: every install of an app version that reports itself on first launch, with a
 *    random ID of its own. This works whatever store the APK came from and whether or not
 *    Firebase is reachable.
 *  - notification_setting: the old method, one row per Firebase token. It is only used for the
 *    time before the first app_installs row (the "cutover"), so that the history is kept and
 *    nothing is counted twice. Devices that already had an older version and then updated report
 *    themselves with is_upgrade and are not counted again either.
 *
 * Day boundaries are Tehran's: "today" starts at midnight Tehran time, not UTC.
 */
class InstallStats
{
    const TIMEZONE = 'Asia/Tehran';

    /** @return array{total: int, month: int, today: int} */
    public static function summary(): array
    {
        return [
            'total' => self::count(),
            'month' => self::count(Carbon::now()->subDays(30)),
            'today' => self::count(self::startOfTehranDay()),
        ];
    }

    /** New installs plus pre-cutover legacy installs, created at or after $since. */
    public static function count(?Carbon $since = null): int
    {
        return self::newInstalls($since)->count() + self::legacyInstalls($since)->count();
    }

    /** The moment the first install was reported by the new mechanism; null until then. */
    public static function cutover(): ?Carbon
    {
        $first = DB::table('app_installs')->min('created_at');

        return $first ? Carbon::parse($first, config('app.timezone')) : null;
    }

    public static function newInstalls(?Carbon $since = null): Builder
    {
        $query = DB::table('app_installs')->where('is_upgrade', false);
        if ($since) {
            $query->where('created_at', '>=', self::db($since));
        }

        return $query;
    }

    public static function legacyInstalls(?Carbon $since = null): Builder
    {
        $query = DB::table('notification_setting');
        if ($cutover = self::cutover()) {
            $query->where('created_at', '<', self::db($cutover));
        }
        if ($since) {
            $query->where('created_at', '>=', self::db($since));
        }

        return $query;
    }

    /**
     * Per source: total installs and installs in the last 30 days. Only new installs know their
     * source; legacy installs are listed as their own row.
     *
     * @return array<int, array{source: string, label: string, total: int, month: int}>
     */
    public static function bySource(): array
    {
        $monthAgo = self::db(Carbon::now()->subDays(30));
        $rows = DB::table('app_installs')
            ->where('is_upgrade', false)
            ->select('source', DB::raw('count(*) as total'), DB::raw('sum(created_at >= ' . DB::getPdo()->quote($monthAgo) . ') as month'))
            ->groupBy('source')
            ->orderByDesc('total')
            ->get();

        $result = [];
        foreach ($rows as $row) {
            $result[] = [
                'source' => $row->source,
                'label' => AppInstall::sourceLabel($row->source),
                'total' => (int) $row->total,
                'month' => (int) $row->month,
            ];
        }

        $legacyTotal = self::legacyInstalls()->count();
        if ($legacyTotal > 0) {
            $result[] = [
                'source' => 'legacy',
                'label' => 'نصب های قبلی (پیش از ثبت منبع نصب)',
                'total' => $legacyTotal,
                'month' => self::legacyInstalls(Carbon::now()->subDays(30))->count(),
            ];
        }

        return $result;
    }

    /**
     * Installs per Tehran calendar day, oldest first, including days with none.
     *
     * @return array<int, array{date: string, jalali: string, count: int}>
     */
    public static function daily(int $days = 30): array
    {
        $from = self::startOfTehranDay()->subDays($days - 1);
        // Stored times are in the app's timezone; shift them to Tehran before taking the date.
        // Iran has had no daylight saving time since 2022, so one fixed offset fits the whole range.
        $offset = Carbon::now(self::TIMEZONE)->utcOffset() - Carbon::now(config('app.timezone'))->utcOffset();
        $localDay = 'DATE(DATE_ADD(created_at, INTERVAL ' . (int) $offset . ' MINUTE))';

        $counts = [];
        foreach ([self::newInstalls($from), self::legacyInstalls($from)] as $query) {
            $rows = $query->select(DB::raw($localDay . ' as day'), DB::raw('count(*) as total'))
                ->groupBy('day')
                ->pluck('total', 'day');
            foreach ($rows as $day => $total) {
                $counts[$day] = ($counts[$day] ?? 0) + (int) $total;
            }
        }

        $result = [];
        $day = $from->copy()->setTimezone(self::TIMEZONE);
        for ($i = 0; $i < $days; $i++) {
            $key = $day->toDateString();
            $result[] = [
                'date' => $key,
                'jalali' => JalaliDate::fromTimestamp($day->copy()->setTime(12, 0)),
                'count' => $counts[$key] ?? 0,
            ];
            $day->addDay();
        }

        return $result;
    }

    /**
     * Reported installs per app version (upgrades included, so this shows which versions
     * people are on, as far as they have reported).
     *
     * @return array<int, array{version: string, total: int}>
     */
    public static function byVersion(): array
    {
        return DB::table('app_installs')
            ->select('app_version', DB::raw('count(*) as total'))
            ->groupBy('app_version')
            ->orderByDesc('total')
            ->get()
            ->map(fn ($row) => ['version' => $row->app_version ?: 'نامشخص', 'total' => (int) $row->total])
            ->all();
    }

    public static function startOfTehranDay(): Carbon
    {
        return Carbon::now(self::TIMEZONE)->startOfDay();
    }

    /** A moment as the wall-clock string the database stores (the app's timezone). */
    private static function db(Carbon $moment): string
    {
        return $moment->copy()->setTimezone(config('app.timezone'))->toDateTimeString();
    }
}
