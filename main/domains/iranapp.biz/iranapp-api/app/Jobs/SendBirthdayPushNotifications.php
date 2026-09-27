<?php

namespace App\Jobs;

use App\Libraries\PrNotification;
use App\Models\Notification;
use App\Support\Birthdays;
use Illuminate\Foundation\Bus\Dispatchable;

/**
 * Wishes a happy birthday to every user whose Jalali birthday is the given day.
 *
 * Dispatched with dispatchAfterResponse() for the same reason as SendAdPushNotification: FCM calls
 * from the Iranian host can be slow, and the request that happened to trigger this must not wait.
 */
class SendBirthdayPushNotifications
{
    use Dispatchable;

    /** The app's NotificationDialog shows the birthday greeting for this `status`. */
    const PUSH_STATUS = '30';

    /** @param  string  $date  Tehran date, 'Y-m-d' */
    public function __construct(private string $date)
    {
    }

    public function handle(): void
    {
        $users = Birthdays::celebrating($this->date);
        if ($users->isEmpty()) {
            return;
        }

        if (function_exists('set_time_limit')) {
            @set_time_limit(0);
        }

        $successes = 0;
        $failures = 0;
        foreach ($users as $user) {
            // One send per user, so the greeting can carry their name.
            $result = PrNotification::sendToTokens([$user->fcm_token], self::title($user->first_name), self::body(), [
                'status' => self::PUSH_STATUS,
                'content_id' => $user->id,
            ]);
            $successes += $result->numberSuccess();
            $failures += $result->numberFailure();
        }

        $notification = new Notification();
        $notification->msg_text = 'تبریک تولد به ' . $users->count() . ' کاربر';
        $notification->successfully_sent = $successes;
        $notification->failures_on_send = $failures;
        $notification->save();
    }

    public static function title(?string $firstName): string
    {
        $firstName = trim((string) $firstName);

        return $firstName === '' ? '🎂 تولدت مبارک!' : "🎂 {$firstName} عزیز، تولدت مبارک!";
    }

    public static function body(): string
    {
        return 'خانواده‌ی ایران اپ سال نو زندگیت رو بهت تبریک می‌گه 🎉 امروز روز توئه؛ '
            . 'یه سر به ایران اپ بزن و ببین چه تخفیف‌ها و آگهی‌های تازه‌ای منتظرته!';
    }
}
