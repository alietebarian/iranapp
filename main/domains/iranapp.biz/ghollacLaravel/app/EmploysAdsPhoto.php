<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class EmploysAdsPhoto extends Model
{
    protected $table = 'employs_ads_photo';
    protected $fillable = [
        'file_name'
    ];
}
