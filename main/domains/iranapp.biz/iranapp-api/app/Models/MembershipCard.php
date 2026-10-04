<?php

namespace App\Models;

use App\Support\JalaliDate;
use Carbon\Carbon;
use Illuminate\Database\Eloquent\Casts\Attribute;
use Illuminate\Database\Eloquent\Model;

/**
 * A user's membership card (منو > دیگر > کارت عضویت), shown in the app as
 * "کارت هدیه معرفی به مراکز طرف قرارداد".
 *
 * pending → approved (the app shows the card) or rejected (with a note; the user corrects the
 * request and sends it again, which puts the same row back to pending).
 */
class MembershipCard extends Model
{
    const STATUS_PENDING = 'pending';
    const STATUS_APPROVED = 'approved';
    const STATUS_REJECTED = 'rejected';

    const STATUS_LABELS = [
        self::STATUS_PENDING => 'در انتظار بررسی',
        self::STATUS_APPROVED => 'تایید شده',
        self::STATUS_REJECTED => 'رد شده',
    ];

    /** Printed at the top of the card screen in the app. */
    const TITLE = 'کارت هدیه معرفی به مراکز طرف قرارداد';

    /** "1394 0010 1363 1900": the first card's serial; each new user's card is the next number. */
    const FIRST_SERIAL = 1394001013631900;

    const MIN_MEMBERS = 1;
    const MAX_MEMBERS = 6;

    /** A card is valid for this many months from its membership date. */
    const VALIDITY_MONTHS = 12;

    protected $table = 'membership_cards';

    protected $fillable = [
        'user_id',
        'serial_number',
        'status',
        'members',
        'national_code',
        'membership_date',
        'expiry_date',
        'starts_on',
        'expires_on',
        'app_version',
        'submitted_at',
    ];

    protected function casts(): array
    {
        return [
            'serial_number' => 'integer',
            'starts_on' => 'date:Y-m-d',
            'expires_on' => 'date:Y-m-d',
            'submitted_at' => 'datetime',
            'reviewed_at' => 'datetime',
        ];
    }

    /**
     * The member names as a list. Stored as JSON with Persian left readable (the `array` cast
     * would write \u0639...), so the admin panel's search can match names with LIKE.
     */
    protected function members(): Attribute
    {
        return Attribute::make(
            get: fn ($value) => $value === null ? [] : (json_decode($value, true) ?: []),
            set: fn ($value) => json_encode(array_values((array) $value), JSON_UNESCAPED_UNICODE),
        );
    }

    public function user()
    {
        return $this->belongsTo(User::class, 'user_id');
    }

    public function reviewer()
    {
        return $this->belongsTo(Admin::class, 'reviewed_by');
    }

    /** The serial for the next user's first request. */
    public static function nextSerial(): int
    {
        $max = static::max('serial_number');

        return $max ? (int) $max + 1 : self::FIRST_SERIAL;
    }

    /** Sets the membership date to today (Tehran) and the expiry date a year later. */
    public function startMembershipToday(): void
    {
        $start = JalaliDate::parse(JalaliDate::today());
        $end = JalaliDate::addMonths($start, self::VALIDITY_MONTHS);

        $this->membership_date = JalaliDate::format(...$start);
        $this->expiry_date = JalaliDate::format(...$end);
        $this->starts_on = JalaliDate::toCarbon($start)->toDateString();
        $this->expires_on = JalaliDate::toCarbon($end)->toDateString();
    }

    public function isPending(): bool
    {
        return $this->status === self::STATUS_PENDING;
    }

    /** True from the expiry date onwards. */
    public function isExpired(): bool
    {
        return $this->expires_on !== null
            && Carbon::now('Asia/Tehran')->toDateString() >= $this->expires_on->toDateString();
    }

    public function statusLabel(): string
    {
        return self::STATUS_LABELS[$this->status] ?? $this->status;
    }

    /** "1394 0010 1363 1900" */
    public static function formatSerial($serial): string
    {
        return trim(chunk_split((string) $serial, 4, ' '));
    }

    public function formattedSerial(): string
    {
        return self::formatSerial($this->serial_number);
    }

    /** The shape the app receives. */
    public function toApiArray(): array
    {
        return [
            'id' => $this->id,
            'status' => $this->status,
            'status_label' => $this->statusLabel(),
            'serial_number' => $this->formattedSerial(),
            'members' => $this->members,
            // Of the first member, the person the card is issued to.
            'national_code' => $this->national_code,
            'membership_date' => $this->membership_date,
            'expiry_date' => $this->expiry_date,
            'is_expired' => $this->isExpired(),
            'rejection_reason' => $this->status === self::STATUS_REJECTED ? $this->rejection_reason : null,
            'submitted_at' => JalaliDate::fromTimestamp($this->submitted_at, 'Y/m/d - H:i'),
            'reviewed_at' => JalaliDate::fromTimestamp($this->reviewed_at, 'Y/m/d - H:i'),
        ];
    }
}
