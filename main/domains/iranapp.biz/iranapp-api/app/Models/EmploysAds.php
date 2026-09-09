<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\DB;

class EmploysAds extends Model
{
    protected $table = 'employs_ads';
    private $query;
    const FIELDS = 'employs_ads.* , region.name as region_name , city.id as city_id , city.name as city_name,
    province.id as province_id , province.name as province_name , users.first_name as user_first_name , users.last_name as user_last_name , 
    employs_ads_specialty.name as specialty  ,employs_ads_specialty.id as specialty_id';

    public function __construct(array $attributes = [])
    {
        parent::__construct($attributes);
        $this->query = DB::table($this->table)
            ->join('region' , 'region.id' , '=' , 'employs_ads.region_id')
            ->join('city' , 'city.id' , '=' , 'region.city_id')
            ->join('province' , 'province.id' , '=' , 'city.province_id')
            ->join('users' , 'users.id' , '=' , 'employs_ads.user_id')
            ->leftJoin('employs_ads_specialty' , 'employs_ads_specialty.id' , '=' , 'employs_ads.specialty_id');
    }

    public function selectFields($fields){
        $this->query = $this->query->selectRaw($fields);
        return $this->query;
    }

    public function getQuery()
    {
        return $this->query;
    }

    public function photo()
    {
        return $this->hasMany(EmploysAdsPhoto::class);
    }

    public function specialty()
    {
        return $this->hasMany(EmploysAdsSpecialty::class, 'specialty_id');
    }

    public function favorite(){
        return $this->belongsToMany(User::class , 'vip_ads_favorite' , 'user_id' , 'ads_id');
    }
}
