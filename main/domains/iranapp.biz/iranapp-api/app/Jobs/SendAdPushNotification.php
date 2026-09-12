<?php

namespace App\Jobs;

use App\Libraries\PrNotification;
use App\Models\Ads;
use App\Models\Notification;
use App\Models\SubCategory;
use Illuminate\Foundation\Bus\Dispatchable;
use Illuminate\Support\Facades\DB;

/**
 * Pushes an approved ad to the devices subscribed to its city and records the outcome.
 *
 * Dispatched with dispatchAfterResponse(): FCM HTTP v1 needs one request per device, so a big
 * city means thousands of calls to Google, which from the Iranian host can be slow or blocked.
 * Run inside the admin's request, that could end in PHP's execution-time limit — a fatal error
 * no try/catch can catch — after the ad was already saved. Now the admin is redirected first.
 */
class SendAdPushNotification
{
    use Dispatchable;

    public function __construct(private int $adId)
    {
    }

    public function handle(): void
    {
        $ad = Ads::find($this->adId);
        if (! $ad) {
            return;
        }

        $tokens = DB::table('notification_setting')
            ->where('send_ads_notifications', 1)
            ->where('city_id', $ad->city_id)
            ->pluck('fcm_token')
            ->all();
        if (empty($tokens)) {
            return;
        }

        // The browser already has its response, so a long send must not be cut short.
        if (function_exists('set_time_limit')) {
            @set_time_limit(0);
        }

        $result = PrNotification::sendToTokens($tokens, 'ایران اپ', $ad->title, ['status' => '0', 'content_id' => $ad->id]);

        $notification = new Notification();
        $notification->category_id = SubCategory::find($ad->sub_category_id)?->category?->id;
        $notification->msg_text = $ad->title;
        $notification->successfully_sent = $result->numberSuccess();
        $notification->failures_on_send = $result->numberFailure();
        $notification->save();
    }
}
