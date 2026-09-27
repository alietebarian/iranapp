<?php

namespace App\Jobs;

use App\Libraries\PrNotification;
use App\Models\MembershipCard;
use Illuminate\Foundation\Bus\Dispatchable;

/**
 * Tells the user their membership card was approved or needs correcting.
 *
 * Dispatched with dispatchAfterResponse() for the same reason as
 * SendEContractReviewPushNotification: a hanging FCM request must not delay the admin's redirect.
 */
class SendMembershipCardReviewPushNotification
{
    use Dispatchable;

    /** The app's NotificationDialog opens the membership card screen for this `status`. */
    const PUSH_STATUS = '40';

    public function __construct(private int $cardId)
    {
    }

    public function handle(): void
    {
        $card = MembershipCard::with('user')->find($this->cardId);
        if (! $card || ! $card->user || empty($card->user->fcm_token)) {
            return;
        }

        $body = $card->status === MembershipCard::STATUS_APPROVED
            ? 'کارت عضویت شما تایید شد. برای مشاهده آن به منو > کارت عضویت بروید.'
            : 'درخواست کارت عضویت شما نیاز به اصلاح دارد. لطفا توضیحات را ببینید و دوباره ارسال کنید.';

        PrNotification::sendToTokens([$card->user->fcm_token], 'ایران اپ', $body, [
            'status' => self::PUSH_STATUS,
            'content_id' => $card->id,
        ]);
    }
}
