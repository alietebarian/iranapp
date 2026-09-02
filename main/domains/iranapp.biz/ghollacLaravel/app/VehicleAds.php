<?php

namespace App;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\DB;

class VehicleAds extends Model
{
    protected $table = 'vehicles_ads';
    const FIELDS = 'vehicles_ads.* , region.id as region_id , region.name as region_name , city.id as city_id , city.name as city_name ,
    province.id as province_id , province.name as province_name , users.first_name as user_first_name , users.last_name as user_last_name , brand.name as brand,
    model.name as model_name , vehicles_cylinder_volume.id as cylinder_volume_id , vehicles_cylinder_volume.value as cylinder_volume';
    private $dbQuery;

    public function __construct(array $attributes = [])
    {
        parent::__construct($attributes);
        $this->dbQuery = DB::table($this->table)
            ->join('region' , 'region.id' , '=' , 'vehicles_ads.region_id')
            ->join('city' , 'city.id' , '=' , 'region.city_id')
            ->join('province' , 'province.id' , '=' , 'city.province_id')
            ->leftJoin('brand' , 'brand.id' , '=' , 'vehicles_ads.brand_id')
            ->leftJoin('model' , 'model.id' , '=' , 'vehicles_ads.model_id')
            ->join('users' , 'users.id' , '=' , 'vehicles_ads.user_id')
            ->leftJoin('vehicles_cylinder_volume' , 'vehicles_cylinder_volume.id' , '=' , 'vehicles_ads.cylinder_volume');
    }

    public function selectFields($fieldList){
      $this->dbQuery = $this->dbQuery->selectRaw($fieldList);
      return $this->dbQuery;
    }

    public function getQuery()
    {
        return $this->query;
    }

    public function photos(){
        return $this->hasMany(VehicleAdsPhoto::class);
    }

    public function brand()
    {
        return $this->belongsTo(Brand::class);
    }

    public function favorite(){
        return $this->belongsToMany(User::class , 'vip_ads_favorite' , 'user_id' , 'ads_id');
    }

}
