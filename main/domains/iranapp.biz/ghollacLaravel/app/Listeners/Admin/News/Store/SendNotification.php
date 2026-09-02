<?php

namespace App\Listeners\Admin\News\Store;

use App\Events\Admin\News\Store;
use App\Notification;
use Illuminate\Queue\InteractsWithQueue;
use Illuminate\Contracts\Queue\ShouldQueue;
use LaravelFCM\Facades\FCM;
use LaravelFCM\Message\OptionsBuilder;
use LaravelFCM\Message\PayloadDataBuilder;
use LaravelFCM\Message\PayloadNotificationBuilder;

class SendNotification
{
    /**
     * Create the event listener.
     *
     * @return void
     */
    public function __construct()
    {
        //
    }

    public function handle(Store $event)
    {
        if(count($event->fcmTokens) > 0 ){
            $optionBuilder = new OptionsBuilder();
            $optionBuilder->setTimeToLive(60 * 20);

            $notificationBuilder = new PayloadNotificationBuilder('ایران اپ');
            $notificationBuilder->setBody($event->news->title)
                ->setSound('default');

            $dataBuilder = new PayloadDataBuilder();
            $dataBuilder->addData(['status' => '1' , 'content_id' => $event->news->id]);
            $data = $dataBuilder->build();
            $option = $optionBuilder->build();
            $notification = $notificationBuilder->build();

            $downstreamResponse = FCM::sendTo($event->fcmTokens, $option, $notification , $data);

            $downstreamResponse->numberFailure();
            $downstreamResponse->numberModification();
            $downstreamResponse->numberSuccess();


            $notification = new Notification();
            $notification->news_id = $event->news->id;
            $notification->msg_text = $event->news->title;
            $notification->successfully_sent = $downstreamResponse->numberSuccess();
            $notification->failures_on_send = $downstreamResponse->numberFailure();
            $notification->save();
        }
    }
}
