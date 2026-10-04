<?php

namespace App\Jobs;

use App\Libraries\PrNotification;
use App\Models\EContract;
use Illuminate\Foundation\Bus\Dispatchable;

/**
 * Tells the user something happened to their electronic contract: approved, rejected, a payment
 * requested or not accepted, or (EVENT_EXPIRY) that it ends soon.
 *
 * Dispatched with dispatchAfterResponse() so an FCM request that hangs (as can happen from the
 * Iranian host) never delays the admin's redirect.
 */
class SendEContractReviewPushNotification
{
    use Dispatchable;

    /** The app's NotificationDialog opens the contract screen for this `status`. */
    const PUSH_STATUS = '20';

    /** Default: describe the contract's current status. */
    const EVENT_STATUS = 'status';
    /** The contract ends within EContract::EXPIRY_REMINDER_DAYS days. */
    const EVENT_EXPIRY = 'expiry';

    public function __construct(private int $contractId, private string $event = self::EVENT_STATUS)
    {
    }

    public function handle(): void
    {
        $contract = EContract::with('user')->find($this->contractId);
        if (! $contract || ! $contract->user || empty($contract->user->fcm_token)) {
            return;
        }

        $body = self::message($contract, $this->event);
        if ($body === null) {
            return;
        }

        PrNotification::sendToTokens([$contract->user->fcm_token], 'ایران اپ', $body, [
            'status' => self::PUSH_STATUS,
            'content_id' => $contract->id,
        ]);
    }

    public static function message(EContract $contract, string $event = self::EVENT_STATUS): ?string
    {
        if ($event === self::EVENT_EXPIRY) {
            $days = max(0, (int) $contract->daysLeft());

            return $days === 0
                ? 'قرارداد الکترونیک شما امروز به پایان می رسد. برای تمدید با ایران اپ تماس بگیرید.'
                : 'قرارداد الکترونیک شما ' . $days . ' روز دیگر به پایان می رسد. برای تمدید با ایران اپ تماس بگیرید.';
        }

        switch ($contract->status) {
            case EContract::STATUS_APPROVED:
                return 'قرارداد الکترونیک شما تایید شد و حساب شما به کاربر پرو ارتقا یافت.';
            case EContract::STATUS_REJECTED:
                return 'قرارداد الکترونیک شما نیاز به اصلاح دارد. لطفا دلیل آن را ببینید و دوباره ارسال کنید.';
            case EContract::STATUS_AWAITING_PAYMENT:
                return $contract->payment_rejection_reason
                    ? 'پرداخت هزینه قرارداد شما تایید نشد. لطفا توضیحات را در برنامه ببینید.'
                    : 'درخواست قرارداد شما بررسی شد. لطفا هزینه قرارداد (' . $contract->formattedAmount() . ' تومان) را پرداخت و در برنامه تایید کنید.';
            default:
                return null;
        }
    }
}
