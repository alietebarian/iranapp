<?php

namespace App\Support;

use App\Jobs\SendBirthdayPushNotifications;
use App\Libraries\jdf;
use App\Models\User;
use Carbon\Carbon;
use Illuminate\Support\Facades\DB;

/**
 * Daily birthday greetings for users who gave their birth date at sign-up.
 *
 * Birthdays are celebrated on the Jalali date, so a user born on 1380/05/12 is greeted every
 * 12 Mordad; one born on 30 Esfand is greeted on 29 Esfand in years that have no 30th.
 */
class Birthdays
{
    /** No greeting before this hour (Tehran), so nobody is woken up by it. */
    const SEND_FROM_HOUR = 9;

    /** The `setting` row that remembers the last Tehran date greetings went out. */
    const LAST_SENT_SETTING = 'birthday_greetings_sent_on';

    /**
     * Sends today's greetings once a day.
     *
     * No cron runs on the host, so this is called from app and admin requests. Today is claimed
     * with a conditional write first, so two requests arriving at once cannot both send.
     */
    public static function sendDueGreetings(): void
    {
        $now = Carbon::now('Asia/Tehran');
        if ($now->hour < self::SEND_FROM_HOUR) {
            return;
        }

        $today = $now->format('Y-m-d');
        $claimed = DB::table('setting')
            ->where('setting_key', self::LAST_SENT_SETTING)
            ->where('setting_value', '<>', $today)
            ->update(['setting_value' => $today]);
        if (! $claimed) {
            $claimed = DB::table('setting')->insertOrIgnore([
                'setting_key' => self::LAST_SENT_SETTING,
                'setting_value' => $today,
            ]);
        }

        if ($claimed) {
            SendBirthdayPushNotifications::dispatchAfterResponse($today);
        }
    }

    /**
     * Users whose Jalali birthday falls on the given Tehran date and who can receive a push.
     *
     * @param  string  $date  Gregorian 'Y-m-d'
     * @return \Illuminate\Support\Collection<int, User>
     */
    public static function celebrating(string $date)
    {
        $day = Carbon::createFromFormat('Y-m-d', $date, 'Asia/Tehran')->startOfDay();
        [$jy, $jm, $jd] = array_map('intval', jdf::gregorian_to_jalali($day->year, $day->month, $day->day));
        $isLastDayOfYear = $jm === 12 && $jd === JalaliDate::monthLength($jy, 12);

        // A Jalali date drifts at most a day against the Gregorian one from year to year, so the
        // candidates are narrowed in SQL and the exact Jalali match is checked below.
        $window = [$day->copy()->subDay(), $day, $day->copy()->addDay()];

        return User::query()
            ->whereNotNull('birth_date')
            ->whereNotNull('fcm_token')
            ->where('fcm_token', '<>', '')
            ->where('is_mobile_verified', 1)
            ->where(function ($q) use ($window) {
                foreach ($window as $d) {
                    $q->orWhere(fn ($q) => $q->whereMonth('birth_date', $d->month)->whereDay('birth_date', $d->day));
                }
            })
            ->get()
            ->filter(function (User $user) use ($jm, $jd, $isLastDayOfYear) {
                $b = $user->birth_date;
                [, $bm, $bd] = array_map('intval', jdf::gregorian_to_jalali($b->year, $b->month, $b->day));

                return $bm === $jm && ($bd === $jd || ($isLastDayOfYear && $bd === 30 && $jd === 29));
            })
            ->values();
    }

    /**
     * Turns the Jalali birth date typed in the app ("1380/05/12", Latin or Persian digits) into
     * the Gregorian 'Y-m-d' that is stored; null when it is not a real date or not in the past.
     */
    public static function parseBirthDate(?string $text): ?string
    {
        $jalali = JalaliDate::parse($text);
        if (! $jalali || $jalali[0] < 1300) {
            return null;
        }

        $date = JalaliDate::toCarbon($jalali);

        return $date->lt(Carbon::now('Asia/Tehran')->startOfDay()) ? $date->format('Y-m-d') : null;
    }
}
