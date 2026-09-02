<?php

namespace App;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\DB;

class UserAds extends Model
{
    protected $table = 'user_ads';
    public $timestamps = false;

    const FIELDS = 'ads.* , city.name as city_name , province.id as province_id ,  province.name as province_name , sub_category.name as sub_category_name,
    category.id as category_id , category.name as category_name , ads_plan.num_of_stars , ads_plan.num_of_updates as max_number_of_update , ads_plan.ordering_factor, ads_plan.max_number_of_photos,
    ads_plan.price as plan_price , ads_plan.plan_title , ads_plan.interval_days as plan_interval_days , 
     ( select count(*) from ads_like where ads_like.ads_id = ads.id and like_type = "like" ) as likes,
     ( select count(*) from ads_like where ads_like.ads_id = ads.id and like_type = "dislike"  ) as dislikes , user_ads.user_id as userAds_userId';
    private $query;

    public function __construct(array $attributes = [])
    {
        parent::__construct($attributes);
        $this->query = DB::table($this->table)
            ->join('ads' , 'ads.id' , '=' ,'user_ads.ads_id')
            ->join('city' , 'city.id' , '=' , 'ads.city_id')
            ->join('province' , 'province.id' , '=' , 'city.province_id')
            ->join('sub_category' , 'sub_category.id' , '=' , 'ads.sub_category_id')
            ->join('category' , 'category.id' , '=' , 'sub_category.category_id')
            ->join('ads_plan' , 'ads_plan.id' , '=' , 'ads.ads_plan_id');

    }
    public function getQuery(){
        return $this->query;
    }

    public function selectFields($fields){
        $this->query = $this->query->selectRaw($fields);
        return $this;
    }
}
