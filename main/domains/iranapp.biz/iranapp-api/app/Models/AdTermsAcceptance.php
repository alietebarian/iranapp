<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

/**
 * One row per ad submission, recording that the user agreed to the terms first.
 * Written by App\Support\TermsConsent; never edited afterwards.
 */
class AdTermsAcceptance extends Model
{
    protected $table = 'ad_terms_acceptances';

    protected $fillable = [
        'user_id',
        'ad_type',
        'ad_id',
        'terms_version',
        'accepted_at',
        'ip_address',
        'app_version',
        'user_agent',
    ];

    public function user()
    {
        return $this->belongsTo(User::class, 'user_id');
    }
}
