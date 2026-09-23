<?php

namespace App\Jobs;

use App\Libraries\PrNotification;
use App\Models\News;
use App\Models\Notification;
use Illuminate\Foundation\Bus\Dispatchable;
use Illuminate\Support\Facades\DB;

/**
 * Pushes a published news item to every user who wants news notifications and records the outcome.
 *
 * Dispatched with dispatchAfterResponse() for the same reason as SendAdPushNotification: a send
 * to many devices can outlast PHP's execution-time limit, and that must not cut the request short.
 */
class SendNewsPushNotification
{
    use Dispatchable;

    public function __construct(private int $newsId)
    {
    }

    public function handle(): void
    {
        $news = News::find($this->newsId);
        if (! $news) {
            return;
        }

        $tokens = DB::table('users')
            ->where('send_news_notifications', 1)
            ->pluck('fcm_token')
            ->all();
        if (empty(array_filter($tokens))) {
            return;
        }

        // The browser already has its response, so a long send must not be cut short.
        if (function_exists('set_time_limit')) {
            @set_time_limit(0);
        }

        $result = PrNotification::sendToTokens($tokens, 'ایران اپ', $news->title, ['status' => '1', 'content_id' => $news->id]);

        $notification = new Notification();
        $notification->news_id = $news->id;
        $notification->msg_text = $news->title;
        $notification->successfully_sent = $result->numberSuccess();
        $notification->failures_on_send = $result->numberFailure();
        $notification->save();
    }
}
