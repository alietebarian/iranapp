<?php

namespace App\Models;

use App\Webazin\Wallet\Wallet;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;

class User extends Authenticatable
{
    use HasApiTokens, Notifiable;

    /** Everyone starts as a regular user; an approved electronic contract makes them pro. */
    const ROLE_NORMAL = 'normal';
    const ROLE_PRO = 'pro';

    protected $fillable = [
        'first_name',
        'last_name',
        'mobile',
        'email',
        'verify_token',
        'password',
        'remember_token',
        'is_mobile_verified',
        'type',
        'role',
        'pro_since',
        'fcm_token',
        'forget_password_token',
        'send_news_notifications',
        'send_ads_notifications',
        'reset_password_token',
    ];

    protected $hidden = [
        'password',
        'remember_token',
    ];

    protected $appends = ['walletSum'];

    protected function casts(): array
    {
        return [
            'password' => 'hashed',
            'is_mobile_verified' => 'integer',
            'send_news_notifications' => 'integer',
            'send_ads_notifications' => 'integer',
            'pro_since' => 'datetime',
        ];
    }

    public function isPro(): bool
    {
        return $this->role === self::ROLE_PRO;
    }

    public function eContracts()
    {
        return $this->hasMany(EContract::class, 'user_id');
    }

    public function wallet()
    {
        return $this->hasMany(Wallet::class);
    }

    public function getWalletSumAttribute()
    {
        return $this->walletSum();
    }

    public function walletSum()
    {
        return $this->hasMany(Wallet::class)->sum('price');
    }
}
