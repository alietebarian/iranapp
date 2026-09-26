<?php

namespace App\Jobs;

use App\Libraries\PrNotification;
use App\Models\EContract;
use Illuminate\Foundation\Bus\Dispatchable;

/**
 * Tells the user their electronic contract was approved or rejected.
 *
 * Dispatched with dispatchAfterResponse() so an FCM request that hangs (as can happen from the
 * Iranian host) never delays the admin's redirect.
 */
class SendEContractReviewPushNotification
{
    use Dispatchable;

    /** The app's NotificationDialog opens the contract screen for this `status`. */
    const PUSH_STATUS = '20';

    public function __construct(private int $contractId)
    {
    }

    public function handle(): void
    {
        $contract = EContract::with('user')->find($this->contractId);
        if (! $contract || ! $contract->user || empty($contract->user->fcm_token)) {
            return;
        }

        $body = $contract->status === EContract::STATUS_APPROVED
            ? 'قرارداد الکترونیک شما تایید شد و حساب شما به کاربر پرو ارتقا یافت.'
            : 'قرارداد الکترونیک شما نیاز به اصلاح دارد. لطفا دلیل آن را ببینید و دوباره ارسال کنید.';

        PrNotification::sendToTokens([$contract->user->fcm_token], 'ایران اپ', $body, [
            'status' => self::PUSH_STATUS,
            'content_id' => $contract->id,
        ]);
    }
}
