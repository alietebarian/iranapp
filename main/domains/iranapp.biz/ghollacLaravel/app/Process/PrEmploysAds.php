<?php
namespace App\Process;

use App\EmploysAds;
use Carbon\Carbon;

class PrEmploysAds{
    public static function adminPanelEmploysAds(){
        $ads = new EmploysAds();
        $query = $ads->selectFields(EmploysAds::FIELDS)->where('employs_ads.status' , '!=' , 'deleted');
        return $query;
    }

    public static function apiEmploysAds(){
        $ads = new EmploysAds();
        $query = $ads->selectFields(EmploysAds::FIELDS)
            ->where('employs_ads.valid_since' , '<=' , Carbon::now()->toDateString())
            ->where('employs_ads.valid_until' , '>=' , Carbon::now()->toDateString())
            ->where('employs_ads.status' , '!=' , 'deleted');
        return $query;
    }
}