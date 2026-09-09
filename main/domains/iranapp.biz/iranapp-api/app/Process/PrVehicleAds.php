<?php
namespace App\Process;

use App\Models\EstateAds;
use App\Models\VehicleAds;
use Carbon\Carbon;

class PrVehicleAds{
    public static function adminPanelVehicleAds(){
        $ads = new VehicleAds();
        $query = $ads->selectFields(VehicleAds::FIELDS)->where('vehicles_ads.status' , '!=' , 'deleted');
        return $query;
    }

    public static function apiVehiclesAds(){
        $ads = new VehicleAds();
        $query = $ads->selectFields(VehicleAds::FIELDS)
            ->where('vehicles_ads.valid_since' , '<=' , Carbon::now()->toDateString())
            ->where('vehicles_ads.valid_until' , '>=' , Carbon::now()->toDateString())
            ->where('vehicles_ads.status' , '!=' , 'deleted');
        return $query;
    }
}