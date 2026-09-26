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
 */
class EContract extends Model
{
    const STATUS_PENDING = 'pending';
    const STATUS_APPROVED = 'approved';
    const STATUS_REJECTED = 'rejected';

    const STATUS_LABELS = [
        self::STATUS_PENDING => 'در انتظار بررسی',
        self::STATUS_APPROVED => 'تایید شده',
        self::STATUS_REJECTED => 'رد شده',
    ];

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
        ];
    }

    /**
     * The contract as the admin sees it, with the user's values highlighted. Null when the
     * wording has changed since this contract was signed; the stored contract_text is then
     * the only faithful copy and is shown instead.
     */
    public function highlightedSections(): ?array
    {
        if ($this->template_version !== EContractTemplate::VERSION) {
            return null;
        }

        return EContractTemplate::fillHtml($this->templateValues());
    }
}
