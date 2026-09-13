<?php

namespace App\Http\Controllers;

use App\Models\AdminNotification;
use Carbon\Carbon;

/**
 * The bell in the admin panel's top bar.
 */
class AdminNotificationController extends Controller
{
    /** Marks the alert as seen and opens the expiring-ads list, where the ad can be renewed. */
    public function open(AdminNotification $notification)
    {
        $notification->read_at = Carbon::now();
        $notification->save();

        return redirect()->route('showExpiringAds', [
            'filter' => 'doFilter',
            'interval_days' => $notification->daysLeft() < 0 ? 'expired' : AdminNotification::EXPIRING_ADS_DAYS,
        ]);
    }

    public function readAll()
    {
        AdminNotification::whereNull('read_at')->update(['read_at' => Carbon::now()]);

        return redirect()->back();
    }
}
