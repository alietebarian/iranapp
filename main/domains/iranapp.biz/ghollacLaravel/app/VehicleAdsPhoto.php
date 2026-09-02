<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class VehicleAdsPhoto extends Model
{
    protected $table = 'vehicles_ads_photo';
    protected $fillable = [
        'file_name'
    ];
}
