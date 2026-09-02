<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class EstateAdsPhoto extends Model
{
    protected $table = 'estates_ads_photo';
    protected $fillable = [
        'file_name'
    ];
}
