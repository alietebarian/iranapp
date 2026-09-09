<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\DB;

class EstateAds extends Model
{
    protected $table = 'estates_ads';
    private $query;
    const FIELDS = 'estates_ads.* , region.name as region_name , city.id as city_id , city.name as city_name,
    province.id as province_id , province.name as province_name , users.first_name as user_first_name , users.last_name as user_last_name,
    estate_categories.id as category_id , estate_categories.name as category_name , estate_categories.parent_id as category_parent_id';

    public function __construct(array $attributes = [])
    {
        parent::__construct($attributes);
        $this->query = DB::table($this->table)
            ->join('region' , 'region.id' , '=' , 'estates_ads.region_id')
            ->join('city' , 'city.id' , '=' , 'region.city_id')
            ->join('province' , 'province.id' , '=' , 'city.province_id')
            ->join('users' , 'users.id' , '=' , 'estates_ads.user_id')
            ->join('estate_categories' , 'estate_categories.id' , '=' , 'estates_ads.category_id');
    }

    public function selectFields($fields){
        $this->query = $this->query->selectRaw($fields);
        return $this->query;
    }

    public function getQuery()
    {
        return $this->query;
    }

    public function photo(){
        return $this->hasMany(EstateAdsPhoto::class , 'estates_ads_id');
    }

    public function favorite(){
        return $this->belongsToMany(User::class , 'vip_ads_favorite' , 'user_id' , 'ads_id');
    }
}
