<?php

namespace App\Support;

use App\Jobs\SendEContractReviewPushNotification;
use App\Models\EContract;
use Carbon\Carbon;
use Illuminate\Support\Facades\Cache;
use Illuminate\Support\Facades\DB;

/**
 * Reminds the user (push notification) and the admin (the panel's bell) once, when an approved
 * electronic contract has EContract::EXPIRY_REMINDER_DAYS days or fewer left.
 *
 * No cron runs on the host, so this is called from app and admin requests (SendEContractExpiryReminders)
 * and, if a cron is ever set up, from the scheduler. Each contract is claimed with a conditional
 * UPDATE on expiry_notified_at before anything is sent, so two requests cannot both remind.
 */
class EContractExpiry
{
    /** Requests look for due reminders at most this often. */
    const CHECK_EVERY_SECONDS = 300;

    public static function sendDueRemindersThrottled(): void
    {
        if (Cache::add('e_contract_expiry_check', true, self::CHECK_EVERY_SECONDS)) {
            self::sendDueReminders();
        }
    }

    /** @return int how many contracts were reminded */
    public static function sendDueReminders(): int
    {
        $today = Carbon::now('Asia/Tehran')->startOfDay();
        $due = EContract::query()
            ->where('status', EContract::STATUS_APPROVED)
            ->whereNull('expiry_notified_at')
            ->where('ends_on', '>=', $today->toDateString())
            ->where('ends_on', '<=', $today->copy()->addDays(EContract::EXPIRY_REMINDER_DAYS)->toDateString())
            ->pluck('id');

        $sent = 0;
        foreach ($due as $id) {
            $claimed = EContract::whereKey($id)->whereNull('expiry_notified_at')->update(['expiry_notified_at' => Carbon::now()]);
            if (! $claimed) {
                continue;
            }

            $contract = EContract::find($id);
            DB::table('admin_notifications')->insertOrIgnore([
                'e_contract_id' => $contract->id,
                'valid_until' => $contract->ends_on->format('Y-m-d'),
                'created_at' => Carbon::now(),
            ]);
            SendEContractReviewPushNotification::dispatchAfterResponse($contract->id, SendEContractReviewPushNotification::EVENT_EXPIRY);
            $sent++;
        }

        return $sent;
    }
}
