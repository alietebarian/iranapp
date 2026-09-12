<?php

namespace App\Models;

use App\Libraries\jdf;
use Carbon\Carbon;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\URL;

class VipAds extends Model
{

    const FIELDS = 'vip_ads.id as vip_ads_id , vip_ads.photo as vip_ads_photo , ads_plan.num_of_updates as max_number_of_updates , ads.* ,
    vip_ads.show_in_country , vip_ads.show_in_province , vip_ads.show_in_city, vip_ads.show_in_main_page , vip_ads.show_in_category , vip_ads.show_in_subcategory,
    category.name as category_name , sub_category.name as sub_category_name , city.name as city_name , province.name as province_name';
    protected $table = 'vip_ads';
    private $query;
    public $timestamps = false;
    public function __construct(array $attributes = [])
    {
        parent::__construct($attributes);
        $this->query = DB::table($this->table)
            ->join('ads' , 'ads.id' , '=' , 'vip_ads.ads_id')
            ->join('ads_plan' , 'ads_plan.id' , '=' , 'ads.ads_plan_id')
            ->join('city' , 'city.id' , '=' , 'ads.city_id')
            ->join('province' , 'province.id' , '=' , 'city.province_id')
            ->join('sub_category' , 'sub_category.id' , '=' , 'ads.sub_category_id')
            ->join('category' , 'category.id' , '=' , 'sub_category.category_id');
    }
    public function selectFields($fieldsList){
        $this->query = $this->query->selectRaw($fieldsList);
        return $this;
    }
    public function getAdsInSpecificCityAndSubCategory($cityId , $categoryId){
//        $SubCategory = SubCategory::find($subCategoryId);
        $city = City::find($cityId);
//        $categoryId = $SubCategory->category_id;
        $provinceId = $city->province_id;
        $this->query = $this->query
            ->whereRaw('(
                ( ads.city_id = '. $cityId.' and vip_ads.show_in_city = 1 ) or 
                ( ads.city_id in ( select city.id from city where city.province_id = '. $provinceId .' ) and vip_ads.show_in_province = 1 ) or 
                ( vip_ads.show_in_country = 1 )
            ) and vip_ads.show_in_main_page = 0 and (
                ( ads.sub_category_id in (select sub_category.id from sub_category where sub_category.category_id = '. $categoryId .' )  and vip_ads.show_in_category = 1)
            )
            ');
        return $this;
    }
    public function getValidAds(){
        $this->query = $this->query
            ->where('ads.valid_since' , '<=' , Carbon::now()->toDateTimeString())
            ->where('ads.valid_until' , '>=' , Carbon::now()->toDateTimeString());
        return $this;
    }
    public function getApproved(){
        $this->query = $this->query->where('ads.status' , '=' , 'approved');
        return $this;
    }
    public function getPending(){
        $this->query = $this->query->where('ads.status' , '=' , 'pending');
        return $this;
    }
    public function getRejected(){
        $this->query = $this->query->where('ads.status' , '=' , 'rejected');
        return $this;
    }
    public function getByCityId($cityId){
        $this->query = $this->query->where('ads.city_id' , '=' , $cityId);
        return $this;
    }
    public function getBySubCategoryId($subCategoryId){
        $this->query = $this->query->where('ads.sub_category_id' , '=' , $subCategoryId);
        return $this;
    }
    public function getQuery(){
        return $this->query;
    }
    public static  function outputJson($input){
        if(is_object($input)){
            $input->photos = Ads::find($input->id)->photo;
            foreach($input->photos as $index => $value){
                if($value->file_name){
                    $input->photos[$index]->file_name = URL::to('/ads_photo') . '/' . $value->file_name;
                }
            }
            if($input->vip_ads_photo){
                $input->vip_ads_photo = URL::to('/vip_ads_photo') . '/' . $input->vip_ads_photo ;
            }
            $input->video_url = Ads::videoUrl($input->video);
            $input->created_at = jdf::jdate('j F Y' , Carbon::createFromFormat('Y-m-d H:i:s' , $input->created_at)->getTimestamp());
            $input->updated_at = jdf::jdate('j F Y' , Carbon::createFromFormat('Y-m-d H:i:s' , $input->updated_at)->getTimestamp());
            $input->valid_since = jdf::jdate('j F Y' , Carbon::createFromFormat('Y-m-d' , $input->valid_since)->getTimestamp());
            $input->valid_until = jdf::jdate('j F Y' , Carbon::createFromFormat('Y-m-d' , $input->valid_until)->getTimestamp());
        }elseif(is_array($input)){
            foreach($input as $index => $row){
                $input[$index]->photos = Ads::find($input->id)->photo;
                foreach($input[$index]->photos as $pindex => $prow){
                    if($prow->file_name){
                        $input[$index]->photos[$pindex]->file_name = URL::to('/ads_photo') . '/' . $prow->file_name;
                    }
                }
                if($input[$index]->vip_ads_photo){
                    $input[$index]->vip_ads_photo = URL::to('/vip_ads_photo') . '/' . $row->vip_ads_photo ;
                }

                $input[$index]->created_at = jdf::jdate('j F Y' , Carbon::createFromFormat('Y-m-d H:i:s' , $input[$index]->created_at)->getTimestamp());
                $input[$index]->updated_at = jdf::jdate('j F Y' , Carbon::createFromFormat('Y-m-d H:i:s' , $input[$index]->updated_at)->getTimestamp());
                $input[$index]->valid_since = jdf::jdate('j F Y' , Carbon::createFromFormat('Y-m-d H:i:s' , $input[$index]->valid_since)->getTimestamp());
                $input[$index]->valid_until = jdf::jdate('j F Y' , Carbon::createFromFormat('Y-m-d H:i:s' , $input[$index]->valid_until)->getTimestamp());
            }

        }
        return $input;
    }

    public function ads(){
        return $this->belongsTo(Ads::class);
    }
}
