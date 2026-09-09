<?php
namespace App\Process;

use App\Models\EstateAds;
use Carbon\Carbon;

class PrEstateAds{
    public static function adminPanelEstateAds(){
        $ads = new EstateAds();
        $query = $ads->selectFields(EstateAds::FIELDS)->where('estates_ads.status' , '!=' , 'deleted');
        return $query;
    }
    public static function apiEstateAds(){
        $ads = new EstateAds();
        $query = $ads->selectFields(EstateAds::FIELDS)
            ->where('estates_ads.valid_since' , '<=' , Carbon::now()->toDateString())
            ->where('estates_ads.valid_until' , '>=' , Carbon::now()->toDateString())
            ->where('estates_ads.status' , '!=' , 'deleted');
        return $query;
    }
}