<?php

namespace App;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\DB;

class FavoriteAds extends Model
{
    protected $table = 'favorite_ads';
    private $query;
    const FIELDS = '
    ads.id as ads_id , ads.* , users.id as user_id , users.first_name as user_first_name , users.last_name as user_last_name,
    city.name as city_name , province.id as province_id , province.name as province_name , sub_category.id as category_id,
    sub_category.name as sub_category_name , category.id as category_id , category.name as category_name , 
    ads_plan.num_of_stars , ads_plan.ordering_factor, ads_plan.max_number_of_photos,
    ads_plan.price as plan_price , ads_plan.plan_title , ads_plan.interval_days as plan_interval_days,
    ads_plan.max_number_of_photos , ads_plan.num_of_updates as max_number_of_update
     ';
    public function __construct(array $attributes = [])
    {
        parent::__construct($attributes);
        $this->query = DB::table($this->table)
            ->join('ads' , 'ads.id' , '=' , 'favorite_ads.ads_id')
            ->join('users' , 'users.id' , '=' , 'favorite_ads.user_id')
            ->join('city' , 'ads.city_id' , '=' , 'city.id')
            ->join('province' , 'province.id' , '=' , 'city.province_id')
            ->join('sub_category' , 'sub_category.id' , '=' , 'ads.sub_category_id')
            ->join('category' , 'category.id' , '=' , 'sub_category.category_id')
            ->join('ads_plan' , 'ads_plan.id' , '=' , 'ads.ads_plan_id');
    }

    public function selectFields($fields){
        $this->query = $this->query->selectRaw($fields);
        return $this->query;
    }
}
