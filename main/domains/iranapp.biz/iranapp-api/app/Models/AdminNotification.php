<?php

namespace App\Models;

use Carbon\Carbon;
use Illuminate\Database\Eloquent\Collection;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\DB;

/**
 * An admin-panel alert that an approved ad is about to expire.
 *
 * One row per ad and expiry date: renewing an ad (a new valid_until) raises a fresh alert once the
 * new date comes within range, and the alert about the old date is no longer shown.
 */
class AdminNotification extends Model
{
    /** How many days before its valid_until an ad is reported. */
    const EXPIRING_ADS_DAYS = 15;

    const UPDATED_AT = null;

    protected $table = 'admin_notifications';

    /**
     * Records an alert for every approved ad that has come within range and has none yet.
     *
     * The host runs no cron, so this is called whenever an admin page is rendered rather than on a
     * schedule; a single INSERT ... SELECT keeps that cheap.
     */
    public static function syncExpiringAds(): void
    {
        $today = Carbon::now('Asia/Tehran');

        $newlyExpiring = DB::table('ads')
            ->selectRaw('ads.id, ads.valid_until, ?', [Carbon::now()->toDateTimeString()])
            ->where('ads.status', '=', 'approved')
            ->where('ads.valid_until', '>=', $today->toDateString())
            ->where('ads.valid_until', '<=', $today->copy()->addDays(self::EXPIRING_ADS_DAYS)->toDateString())
            // Skipping known rows up front, not only via the unique key, keeps INSERT IGNORE
            // from burning an auto-increment id per ad on every page view.
            ->whereNotExists(function ($query) {
                $query->selectRaw('1')
                    ->from('admin_notifications')
                    ->whereColumn('admin_notifications.ads_id', 'ads.id')
                    ->whereColumn('admin_notifications.valid_until', 'ads.valid_until');
            });

        // Ignore: two admin pages rendered at once may both try to insert the same alert.
        DB::table('admin_notifications')->insertOrIgnoreUsing(['ads_id', 'valid_until', 'created_at'], $newlyExpiring);
    }

    /**
     * Unread alerts, soonest expiry first, with the ad's title. Alerts about a date the ad no
     * longer has (it was renewed) are left out.
     */
    public static function unread(): Collection
    {
        return self::query()
            ->join('ads', function ($join) {
                $join->on('ads.id', '=', 'admin_notifications.ads_id')
                    ->on('ads.valid_until', '=', 'admin_notifications.valid_until');
            })
            ->whereNull('admin_notifications.read_at')
            ->orderBy('admin_notifications.valid_until')
            ->orderBy('admin_notifications.id')
            ->get(['admin_notifications.*', 'ads.title']);
    }

    /** Tehran calendar days until the ad expires; 0 on its last day, negative once expired. */
    public function daysLeft(): int
    {
        $today = Carbon::now('Asia/Tehran')->startOfDay();
        $validUntil = Carbon::createFromFormat('Y-m-d', $this->valid_until, 'Asia/Tehran')->startOfDay();

        return (int) $today->diffInDays($validUntil);
    }
}
