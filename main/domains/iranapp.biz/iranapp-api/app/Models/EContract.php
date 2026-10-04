<?php

namespace App\Models;

use App\Support\EContractTemplate;
use App\Support\JalaliDate;
use Illuminate\Database\Eloquent\Model;

/**
 * An electronic contract a user submitted from the app (منو > دیگر > قرارداد الکترونیک).
 *
 * pending → approved (the user becomes pro) or rejected (with a reason; the user corrects the
 * form and submits again, which creates a new row).
 *
 * The admin approves a contract either free, or paid: then they send an amount and a card number
 * (awaiting_payment), the user pays outside the app and confirms it in the app
 * (payment_submitted), and the admin gives the final approval — or says the payment was not
 * received, which sends it back to awaiting_payment with a reason.
 */
class EContract extends Model
{
    const STATUS_PENDING = 'pending';
    const STATUS_AWAITING_PAYMENT = 'awaiting_payment';
    const STATUS_PAYMENT_SUBMITTED = 'payment_submitted';
    const STATUS_APPROVED = 'approved';
    const STATUS_REJECTED = 'rejected';

    const STATUS_LABELS = [
        self::STATUS_PENDING => 'در انتظار بررسی',
        self::STATUS_AWAITING_PAYMENT => 'در انتظار پرداخت کاربر',
        self::STATUS_PAYMENT_SUBMITTED => 'پرداخت شده، در انتظار تایید',
        self::STATUS_APPROVED => 'تایید شده',
        self::STATUS_REJECTED => 'رد شده',
    ];

    /** Statuses that wait for the admin; counted in the sidebar badge. */
    const NEEDS_ADMIN = [self::STATUS_PENDING, self::STATUS_PAYMENT_SUBMITTED];

    const BILLING_FREE = 'free';
    const BILLING_PAID = 'paid';

    const BILLING_LABELS = [
        self::BILLING_FREE => 'رایگان',
        self::BILLING_PAID => 'پولی',
    ];

    /** A reminder goes to the user and the admin when this many days (or fewer) are left. */
    const EXPIRY_REMINDER_DAYS = 15;

    const BUSINESS_TYPES = [
        'company' => 'شرکت',
        'institute' => 'موسسه',
        'complex' => 'مجموعه',
        'store' => 'فروشگاه',
    ];

    const MANAGER_TITLES = [
        'mr' => 'آقای',
        'mrs' => 'خانم',
    ];

    /** Contract terms offered in the app, in months. The API accepts any value in between. */
    const DURATIONS = [3, 6, 12, 24, 36];
    const MIN_DURATION = 1;
    const MAX_DURATION = 60;

    protected $table = 'e_contracts';

    protected $fillable = [
        'user_id',
        'status',
        'business_type',
        'business_name',
        'manager_title',
        'manager_name',
        'national_code',
        'phone',
        'mobile',
        'address',
        'subject',
        'start_date',
        'end_date',
        'duration_months',
        'starts_on',
        'ends_on',
        'discount_percent',
        'contract_date',
        'template_version',
        'contract_text',
        'terms_accepted_at',
        'ip_address',
        'app_version',
        'user_agent',
    ];

    protected function casts(): array
    {
        return [
            'duration_months' => 'integer',
            'discount_percent' => 'integer',
            'starts_on' => 'date:Y-m-d',
            'ends_on' => 'date:Y-m-d',
            'terms_accepted_at' => 'datetime',
            'reviewed_at' => 'datetime',
            'amount' => 'integer',
            'payment_requested_at' => 'datetime',
            'payment_submitted_at' => 'datetime',
            'expiry_notified_at' => 'datetime',
        ];
    }

    public function user()
    {
        return $this->belongsTo(User::class, 'user_id');
    }

    public function reviewer()
    {
        return $this->belongsTo(Admin::class, 'reviewed_by');
    }

    public function isPending(): bool
    {
        return $this->status === self::STATUS_PENDING;
    }

    public function isPaid(): bool
    {
        return $this->billing === self::BILLING_PAID;
    }

    public function billingLabel(): ?string
    {
        return self::BILLING_LABELS[$this->billing] ?? null;
    }

    /** Tehran calendar days until the contract ends: 0 on its last day, negative once it has ended. */
    public function daysLeft(): ?int
    {
        if (! $this->ends_on) {
            return null;
        }
        $today = \Carbon\Carbon::now('Asia/Tehran')->startOfDay();
        $end = \Carbon\Carbon::createFromFormat('Y-m-d', $this->ends_on->format('Y-m-d'), 'Asia/Tehran')->startOfDay();

        return (int) $today->diffInDays($end, false);
    }

    /** "6037 9918 1234 5678" */
    public function formattedCardNumber(): ?string
    {
        return $this->card_number ? trim(chunk_split($this->card_number, 4, ' ')) : null;
    }

    /** "1,500,000" */
    public function formattedAmount(): ?string
    {
        return $this->amount !== null ? number_format($this->amount) : null;
    }

    public function statusLabel(): string
    {
        return self::STATUS_LABELS[$this->status] ?? $this->status;
    }

    public function businessTypeLabel(): string
    {
        return self::BUSINESS_TYPES[$this->business_type] ?? $this->business_type;
    }

    public function managerTitleLabel(): string
    {
        return self::MANAGER_TITLES[$this->manager_title] ?? $this->manager_title;
    }

    /** "12 ماه" */
    public static function durationLabel(int $months): string
    {
        return $months . ' ماه';
    }

    /** Values for EContractTemplate's placeholders. */
    public function templateValues(): array
    {
        return [
            'business_type' => $this->businessTypeLabel(),
            'business_name' => $this->business_name,
            'manager_title' => $this->managerTitleLabel(),
            'manager_name' => $this->manager_name,
            'national_code' => $this->national_code,
            'phone' => $this->phone,
            'mobile' => $this->mobile,
            'address' => $this->address,
            'subject' => $this->subject,
            'start_date' => $this->start_date,
            'end_date' => $this->end_date,
            'duration' => self::durationLabel((int) $this->duration_months),
            'discount_percent' => $this->discount_percent,
        ];
    }

    /** The shape the app receives. */
    public function toApiArray(): array
    {
        return [
            'id' => $this->id,
            'status' => $this->status,
            'status_label' => $this->statusLabel(),
            'business_type' => $this->business_type,
            'business_name' => $this->business_name,
            'manager_title' => $this->manager_title,
            'manager_name' => $this->manager_name,
            'national_code' => $this->national_code,
            'phone' => $this->phone,
            'mobile' => $this->mobile,
            'address' => $this->address,
            'subject' => $this->subject,
            'start_date' => $this->start_date,
            'end_date' => $this->end_date,
            'duration_months' => $this->duration_months,
            'discount_percent' => $this->discount_percent,
            'contract_date' => $this->contract_date,
            'contract_text' => $this->contract_text,
            'rejection_reason' => $this->rejection_reason,
            'submitted_at' => JalaliDate::fromTimestamp($this->created_at, 'Y/m/d - H:i'),
            'reviewed_at' => JalaliDate::fromTimestamp($this->reviewed_at, 'Y/m/d - H:i'),
            'billing' => $this->billing,
            'amount' => $this->amount,
            'amount_formatted' => $this->formattedAmount(),
            'card_number' => $this->card_number,
            'card_number_formatted' => $this->formattedCardNumber(),
            'card_holder' => $this->card_holder,
            'payment_message' => $this->payment_message,
            'payment_reference' => $this->payment_reference,
            'payment_submitted_at' => JalaliDate::fromTimestamp($this->payment_submitted_at, 'Y/m/d - H:i'),
            'payment_rejection_reason' => $this->payment_rejection_reason,
            'days_left' => $this->status === self::STATUS_APPROVED ? $this->daysLeft() : null,
            'expiry_reminder_days' => self::EXPIRY_REMINDER_DAYS,
        ];
    }

    /** The wording this contract was signed under: {version, title, sections}, or null if unknown. */
    public function signedTemplate(): ?array
    {
        return EContractTemplate::forVersion($this->template_version);
    }

    /**
     * The contract as the admin sees it: the wording it was signed under, with the user's values
     * highlighted. Null if that version cannot be found; the stored contract_text is then shown.
     */
    public function highlightedSections(): ?array
    {
        $template = $this->signedTemplate();

        return $template ? EContractTemplate::fillHtml($template['sections'], $this->templateValues()) : null;
    }
}
