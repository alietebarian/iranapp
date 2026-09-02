<?php

namespace App\Http\Controllers;

use App\Ads;
use App\CylinderVolumes;
use App\Http\Requests\Admin\SaveVehicleAdsRequest;
use App\Http\Requests\Admin\UpdateVehicleAdsRequest;
use App\Http\Requests\saveVehicleAdsJson;
use App\Libraries\jdf;
use App\Process\PrVehicleAds;
use App\Province;
use App\Region;
use App\User;
use App\Brand;
use App\VehicleAds;
use App\VehicleAdsPhoto;
use Carbon\Carbon;
use Illuminate\Database\QueryException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Intervention\Image\Facades\Image;
use LaravelFCM\Facades\FCM;
use LaravelFCM\Message\OptionsBuilder;
use LaravelFCM\Message\PayloadDataBuilder;
use LaravelFCM\Message\PayloadNotificationBuilder;
use Tymon\JWTAuth\Facades\JWTAuth;

class VehicleAdsController extends Controller
{
    public function showAllInAdmin(Request $request)
    {

        $ads = PrVehicleAds::adminPanelVehicleAds()->orderBy('vehicles_ads.created_at', 'desc');
        if ($request->has('region_id') && $request->region_id != 'all') {
            $ads = $ads->where('vehicles_ads.region_id', '=', $request->region_id);
        } else {
            if ($request->has('city_id') && $request->city_id != 'all') {
                $ads = $ads->whereRaw('vehicles_ads.region_id in (
                    select region.id from region where region.city_id = ' . $request->city_id . '
                )');
            } else {
                if ($request->has('province_id') && $request->province_id != 'all') {
                    $ads = $ads->whereRaw('vehicles_ads.region_id in (
                        select region.id from region where region.city_id in (
                            select city.id from city where city.province_id = ' . $request->province_id . '
                        )
                    )');
                }
            }
        }
        if($request->has('person_or_company') && $request->person_or_company != 'all'){
            $ads = $ads->where('vehicles_ads.person_or_company' , '=' , $request->person_or_company);
        }
        if ($request->has('type') && $request->type != 'all') {
            $ads = $ads->where('vehicles_ads.type', '=', $request->type);
        }/*type*/

        if ($request->has('model') && $request->model != 'all') {
            $ads = $ads->where('vehicles_ads.model_id', '=', $request->model);
        } else {
            if ($request->has('brand') && $request->brand != 'all') {
                $ads = $ads->where('vehicles_ads.brand_id', '=', $request->brand);
            }
        }

        if ($request->has('chassis_type') && $request->chassis_type != 'all') {
            $ads = $ads->where('vehicles_ads.chassis_type', '=', $request->chassis_type);
        }/*chassis_type*/

        if ($request->has('neworold') && $request->neworold != 'all') {
            $ads = $ads->where('vehicles_ads.neworold', '=', $request->neworold);
        }/*neworold*/

        if ($request->has('cylinder_volume') && $request->cylinder_volume != 'all') {
            $ads = $ads->where('vehicles_ads.cylinder_volume', '=', $request->cylinder_volume);
        }/*cylinder_volume*/

        if ($request->has('price')) {
            $ads = $ads->where('vehicles_ads.price', '=', $request->price);
        }/*price*/

        if ($request->has('kilometre')) {
            $ads = $ads->where('vehicles_ads.kilometre', '=', $request->kilometre);
        }/*kilometre*/

        if ($request->has('production_year')) {
            $ads = $ads->where('vehicles_ads.production_year', '=', $request->production_year);
        }/*production_year*/

        if ($request->has('status') && $request->status != 'all') {
            $ads = $ads->where('vehicles_ads.status', '=', $request->status);
        }
        if($request->has('keyword')){
            $ads = $ads->whereRaw('( MATCH(vehicles_ads.ads_title) AGAINST("'. $request->input('keyword').'" IN NATURAL LANGUAGE MODE) )');
        }
        $ads = $ads->paginate(15);
        $data['ads'] = $ads;
        $data['provinces'] = Province::all();
        $data['cylinder_volumes'] = CylinderVolumes::all();
        $data['brands'] = DB::table('brand')->get();
        return view('admin.vehicles.list')->with($data);
    }/*showAllInAdmin*/

    public function deleteById(Request $request, VehicleAds $ads)
    {
        $ads->status = 'deleted';
        $ads->save();

        $msg = new \stdClass();
        $msg->title = 'حذف موفقیت آمیز';
        $msg->msg = 'آگهی مورد نظر با موفقیت حذف شد.';
        return redirect()->back()->with('success_msg', $msg);
    }/*deleteById*/

    public function showCreatePage(Request $request)
    {
        $data['provinces'] = Province::all();
        $data['users'] = User::all();
        $data['brands'] = DB::table('brand')->get();
        $data['cylinder_volumes'] = CylinderVolumes::all();
        return view('admin.vehicles.addPage')->with($data);
    }/*showCreatePage*/

    public function save(SaveVehicleAdsRequest $request)
    {
        $ads = new VehicleAds();

        $ads->ads_title = $request->ads_title;
        $ads->type = $request->type;
        $ads->region_id = $request->region_id;
        if ($request->hasFile('thumbnail_photo')) {
            $imgName = uniqid() . '.' . $request->thumbnail_photo->getClientOriginalExtension();
            $request->thumbnail_photo->move(public_path('/ads_photo'), $imgName);
            $ads->thumbnail_photo = $imgName;

            $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
            $imageToAddWatermark->resize(440 , null , function($constraint){
                $constraint->aspectRatio();
            });
            $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png') , 'bottom-left' , 0 , 20);
            $imageToAddWatermark->save();
        }
        $ads->user_id = $request->user_id;
        $ads->neworold = $request->neworold;
        if(in_array($request->type , ['khodro' , 'motorcycle'])){
            $ads->production_year = $request->production_year;
        }else{
            $ads->production_year =  null;
        }
        $ads->kilometre = $request->kilometre;
        $ads->cylinder_volume = $request->cylinder_volume;
        $ads->chassis_type = $request->chassis_type;
        $ads->address = $request->address;
        if ($request->brand != 'all') {
            $ads->brand_id = $request->brand;
        } else {
            $ads->brand_id = null;
        }
        if ($request->model != 'all') {
            $ads->model_id = $request->model;
        } else {
            $ads->model_id = null;
        }
        $ads->price = $request->price;
        $ads->telephone1 = $request->telephone1;
        $ads->telephone2 = $request->telephone2;
        $ads->person_or_company = $request->person_or_company;
        $ads->status = $request->status;
        $ads->ads_owner_name = $request->ads_owner_name;
        $ads->description = $request->description;
        $ads->latitude = $request->latitude;
        $ads->longitude = $request->longitude;
        $ads->created_at_2 = Carbon::now()->toDateTimeString();
        $ads->valid_since = Carbon::now()->toDateString();
        $ads->valid_until = Carbon::now()->addYear(1)->toDateString();
        $ads->save();

        if ($request->has('send_notification') && $request->status == 'approved') {
            $fcm_tokens = DB::table('notification_setting')->where('send_ads_notifications', '=', 1)->pluck('fcm_token')->toArray();
            if (count($fcm_tokens) > 0) {
                $optionBuilder = new OptionsBuilder();
                $optionBuilder->setTimeToLive(60 * 20);

                $notificationBuilder = new PayloadNotificationBuilder('ایران اپ');
                $notificationBuilder->setBody($ads->ads_title)
                    ->setSound('default');

                $dataBuilder = new PayloadDataBuilder();
                $dataBuilder->addData(['status' => '0', 'content_id' => $ads->id]);
                $data = $dataBuilder->build();
                $option = $optionBuilder->build();
                $notification = $notificationBuilder->build();

                $downstreamResponse = FCM::sendTo($fcm_tokens, $option, $notification, $data);

                $downstreamResponse->numberFailure();
                $downstreamResponse->numberModification();
                $downstreamResponse->numberSuccess();


            }
        }
        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز';
        $msg->msg = 'آگهی با موفقیت ذخیره شد.';
        return redirect()->route('vehicleAds.photos.show', $ads->id)->with('success_msg', $msg);
    }/*save*/

    public function showUpdatePage(Request $request, VehicleAds $ads)
    {
        $data['provinces'] = Province::all();
        $data['users'] = User::all();
        $data['brands'] = Brand::all();
        $data['ads'] = $ads;
        $currentCity = Region::find($ads->region_id)->city;
        $currentProvince = $currentCity->province;
        $data['currentProvince'] = $currentProvince;
        $data['currentCity'] = $currentCity;
        $data['cities'] = $currentProvince->city;
        $data['regions'] = $currentCity->region;
        $data['photos'] = $ads->photos;
        if($ads->brand){
            $data['models'] = $ads->brand->models;
        }else{
            $data['models'] = null;
        }
        $data['cylinder_volumes'] = CylinderVolumes::all();
        return view('admin.vehicles.update')->with($data);
    }/*showUpdatePage*/

    public function update(UpdateVehicleAdsRequest $request, VehicleAds $ads)
    {
        $ads->ads_title = $request->ads_title;
        $ads->region_id = $request->region_id;
        if ($request->hasFile('thumbnail_photo')) {
            $imgName = uniqid() . '.' . $request->thumbnail_photo->getClientOriginalExtension();
            $request->thumbnail_photo->move(public_path('/ads_photo'), $imgName);
            $ads->thumbnail_photo = $imgName;

            $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
            $imageToAddWatermark->resize(440 , null , function($constraint){
                $constraint->aspectRatio();
            });
            $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png') , 'bottom-left' , 0 , 20);
            $imageToAddWatermark->save();
        }
        $ads->user_id = $request->user_id;
        $ads->address = $request->address;
        $ads->telephone1 = $request->telephone1;
        $ads->telephone2 = $request->telephone2;
        $ads->status = $request->status;
        $ads->ads_owner_name = $request->ads_owner_name;
        $ads->description = $request->description;

        if($request->has('latitude')){
            $ads->latitude = $request->latitude;
        }else
            $ads->latitude = null;
        if($request->has('longitude')){
            $ads->longitude = $request->longitude;
        }else
            $ads->longitude = null;

        if($request->brand == 'all'){
            $ads->brand_id = null;
        }else{
            $ads->brand_id = $request->brand;
        }
        if($request->model == 'all'){
            $ads->model_id = null;
        }else{
            $ads->model_id = $request->model;
        }
        $ads->price = $request->price;
        $ads->neworold = $request->neworold;
        if(in_array($request->type , ['khodro' , 'motorcycle'])){
            $ads->production_year = $request->production_year;
        }else{
            $ads->production_year =  null;
        }
        $ads->kilometre = $request->kilometre;
        $ads->cylinder_volume = $request->cylinder_volume;
        $ads->chassis_type = $request->chassis_type;
        $ads->person_or_company = $request->person_or_company;
        $ads->save();

        $msg = new \stdClass();
        $msg->title = 'ویرایش موفقیت آمیز';
        $msg->msg = 'آگهی با موفقیت ویرایش شد.';
        if (count($ads->photo) == 5) {
            return redirect()->route('showAllVehicleAdsInAdminPanel')->with('success_msg', $msg);
        }
        return redirect()->route('vehicleAds.photos.show', $ads->id)->with('success_msg', $msg);
    }

    public function getJson(Request $request)
    {
        $adsObj = new VehicleAds();
        $query = $adsObj->selectFields(VehicleAds::FIELDS)
            ->orderBy('vehicles_ads.created_at' , 'desc')
            ->where('vehicles_ads.status', '=', 'approved')
            ->where('vehicles_ads.valid_since' , '<=' , Carbon::now()->toDateString())
            ->where('vehicles_ads.valid_until' , '>=' , Carbon::now()->toDateString());

        if ($request->has('type')) {
            $query = $query->where('vehicles_ads.type', '=', $request->type);
        }
        if ($request->has('city_id')) {
            $query = $query->where('region.city_id', '=', $request->city_id);
        }
        if ($request->has('brand_id')) {
            $query = $query->where('vehicles_ads.brand_id', '=', $request->brand_id);
        }
        $offset = $request->has('offset') ? $request->offset : 0;
        $limit = $request->has('limit') ? $request->limit : 1;
        $list = $query->offset($offset)->limit($limit)->get();
        foreach ($list as $index => $row) {
            $photo = VehicleAdsPhoto::where('vehicle_ads_id', '=', $row->id)->get();
            foreach ($photo as $pindex => $photoItem) {
                $photo[$pindex]->file_name = url()->to('/ads_photo') . '/' . $photoItem->file_name;
            }
            $list[$index]->photos = $photo;

            if ($row->thumbnail_photo) {
                $list[$index]->thumbnail_photo = url()->to('/ads_photo') . '/' . $row->thumbnail_photo;
            }
            $list[$index]->fa_created_at = jdf::jdate('j F Y', Carbon::createFromFormat('Y-m-d H:i:s', $row->created_at_2)->getTimestamp());

            $list[$index]->passed_time = getElapsedTime($row->created_at_2);
        }
        return response()->json(['status' => 200, 'list' => $list]);
    }

    public function saveJson(saveVehicleAdsJson $request)
    {
        $user = JWTAuth::parseToken()->authenticate();
        $adsObj = new VehicleAds();
        $adsObj->ads_title = $request->ads_title;
        $adsObj->type = $request->type;
        $adsObj->region_id = $request->region_id;
        if ($request->hasFile('thumbnail_photo')) {
            $imgName = uniqid() . '.' . $request->thumbnail_photo->getClientOriginalExtension();
            $request->thumbnail_photo->move(public_path('/ads_photo'), $imgName);
            $adsObj->thumbnail_photo = $imgName;

            $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
            $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png') , 'bottom-left' , 0 , 25);
            $imageToAddWatermark->save();
        }
        $adsObj->user_id = $user->id;
        $adsObj->neworold = $request->neworold;
        $adsObj->production_year = $request->production_year;
        $adsObj->kilometre = $request->kilometre;
        $adsObj->cylinder_volume = $request->cylinder_volume;
        $adsObj->chassis_type = $request->chassis_type;
        $adsObj->address = $request->address;
        $adsObj->brand_id = $request->brand;
        $adsObj->model_id = $request->model;
        $adsObj->price = $request->price;
        $adsObj->telephone1 = $request->telephone1;
        $adsObj->telephone2 = $request->telephone2;
        $adsObj->status = 'pending';
        $adsObj->ads_owner_name = $request->ads_owner_name;
        $adsObj->description = $request->description;
        $adsObj->latitude = $request->latitude;
        $adsObj->longitude = $request->longitude;
        $adsObj->person_or_company = $request->person_or_company;
        $adsObj->created_at_2 = Carbon::now()->toDateTimeString();
        $adsObj->valid_since = Carbon::now()->toDateString();
        $adsObj->valid_until = Carbon::now()->addYear(1)->toDateString();
        $adsObj->save();

        if ($request->hasFile('photos')) {
            foreach ($request->photos as $photo) {
                $imgName = uniqid() . '.' . $photo->getClientOriginalExtension();
                $photo->move(public_path('/ads_photo'), $imgName);
                $adsObj->photos()->create([
                    'file_name' => $imgName
                ]);

                $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
                $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png') , 'bottom-left' , 0 , 25);
                $imageToAddWatermark->save();
            }
        }


        return response()->json(['status' => 200]);

    }

    public function updateJson(Request $request, VehicleAds $ads)
    {
        $user = JWTAuth::parseToken()->authenticate();
        $ads->ads_title = $request->ads_title;
        $ads->type = $request->type;
        $ads->region_id = $request->region_id;
        if ($request->hasFile('thumbnail_photo')) {
            $imgName = uniqid() . '.' . $request->thumbnail_photo->getClientOriginalExtension();
            $request->thumbnail_photo->move(public_path('/ads_photo'), $imgName);
            $ads->thumbnail_photo = $imgName;

            $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
            $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png') , 'bottom-left' , 0 , 25);
            $imageToAddWatermark->save();
        }
        $ads->neworold = $request->neworold;
        $ads->production_year = $request->production_year;
        $ads->kilometre = $request->kilometre;
        $ads->cylinder_volume = $request->cylinder_volume;
        $ads->chassis_type = $request->chassis_type;
        $ads->address = $request->address;
        $ads->brand_id = $request->brand;
        $ads->model_id = $request->model;
        $ads->price = $request->price;
        $ads->telephone1 = $request->telephone1;
        $ads->telephone2 = $request->telephone2;
        $ads->status = 'pending';
        $ads->ads_owner_name = $request->ads_owner_name;
        $ads->description = $request->description;
        $ads->latitude = $request->latitude;
        $ads->longitude = $request->longitude;
        $ads->person_or_company = $request->person_or_company;
        $ads->valid_since = Carbon::now()->toDateString();
        $ads->valid_until = Carbon::now()->addYear(1)->toDateString();
        if ($request->has('delete_thumbnail_photo') && $request->delete_thumbnail_photo == 1) {
            $ads->thumbnail_photo = null;
        }
        $ads->save();

        if ($request->hasFile('photos')) {
            foreach ($request->photos as $photo) {
                $imgName = uniqid() . '.' . $photo->getClientOriginalExtension();
                $photo->move(public_path('/ads_photo'), $imgName);
                $ads->photos()->create([
                    'file_name' => $imgName
                ]);

                $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
                $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png') , 'bottom-left' , 0 , 25);
                $imageToAddWatermark->save();
            }
        }

        if ($request->has('delete_photo')) {
            $photosToDelete = $request->delete_photo;
            foreach ($photosToDelete as $photo) {
                $photoObj = VehicleAdsPhoto::find($photo);
                $photoObj->delete();
            }
        }
        return response()->json(['status' => 200]);
    }

    public function getJsonByUser(Request $request)
    {
        $user = JWTAuth::parseToken()->authenticate();
        $adsObj = new VehicleAds();
        $offset = $request->has('offset') ? $request->offset : 0;
        $limit = $request->has('limit') ? $request->limit : 1;
        $list = $adsObj->selectFields(VehicleAds::FIELDS)
            ->where('vehicles_ads.user_id', '=', $user->id)
            ->where('vehicles_ads.status', '!=', 'deleted')
            ->orderBy('vehicles_ads.created_at', 'desc')
            ->offset($offset)->limit($limit)
            ->where('vehicles_ads.valid_since' , '<=' , Carbon::now()->toDateString())
            ->where('vehicles_ads.valid_until' , '>=' , Carbon::now()->toDateString())
            ->get();
        foreach ($list as $index => $row) {
            $photos = VehicleAdsPhoto::where('vehicle_ads_id', '=', $row->id)->get();
            foreach ($photos as $pindex => $prow) {
                $photos[$pindex]->file_name = url()->to('/ads_photo') . '/' . $prow->file_name;
            }
            $list[$index]->photos = $photos;
            if ($row->thumbnail_photo) {
                $list[$index]->thumbnail_photo = url('/ads_photo') . '/' . $row->thumbnail_photo;
            }
            $list[$index]->fa_created_at = jdf::jdate('j F Y', Carbon::createFromFormat('Y-m-d H:i:s', $row->created_at_2)->getTimestamp());

            $list[$index]->passed_time = getElapsedTime($row->created_at_2);

        }
        return response()->json(['status' => 200, 'list' => $list]);
    }

    public function deleteJsonById(Request $request, VehicleAds $ads)
    {
        $ads->status = 'deleted';
        $ads->save();

        return response(['status' => 200]);
    }

    public function searchJson(Request $request)
    {
        $adsObj = new VehicleAds();
        $list = $adsObj->selectFields(VehicleAds::FIELDS)
            ->where('vehicles_ads.status', '=', 'approved')
            ->where('vehicles_ads.valid_since' , '<=' , Carbon::now()->toDateString())
            ->where('vehicles_ads.valid_until' , '>=' , Carbon::now()->toDateString());
        if($request->has('region_id')){
            $list = $list->where('region.id' , '=' , $request->region_id);
        }else{
            if ($request->has('city_id')) {
                $list = $list->where('city.id', '=', $request->city_id);
            } else {
                if ($request->has('province_id')) {
                    $list = $list->where('province.id', '=', $request->province_id);
                }
            }
        }
        if ($request->has('model_id')) {
            $list = $list->where('model.id', '=', $request->model_id);
        } else {
            if ($request->has('brand_id')) {
                $list = $list->where('brand.id', '=', $request->brand_id);
            } else {
                if ($request->has('type')) {
                    $list = $list->where('vehicles_ads.type', '=', $request->type);
                }
            }
        }
        if ($request->has('ads_title')) {
            $list = $list->whereRaw(' ( MATCH( vehicles_ads.ads_title ) AGAINST( "' . $request->ads_title . '" IN NATURAL LANGUAGE MODE)  ) ');
        }
        if ($request->has('chassis_type')) {
            $list = $list->where('vehicles_ads.chassis_type', '=', $request->chassis_type);
        }
        if ($request->has('production_year_from') && $request->has('production_year_to')) {
            $list = $list
                ->where('vehicles_ads.production_year', '>=', $request->production_year_from)
                ->where('vehicles_ads.production_year', '<=', $request->production_year_to);
        }
        if($request->has('cylinder_volume_id')){
            $list = $list->where('vehicles_ads.cylinder_volume' , '=' , $request->cylinder_volume_id);
        }
        if ($request->has('kilometer_from') && $request->has('kilometer_to')) {
            $list = $list
                ->where('vehicles_ads.kilometre', '>=', $request->kilometer_from)
                ->where('vehicles_ads.kilometre', '<=', $request->kilometer_to);
        }
        if($request->has('ordering')){
            if($request->ordering == 'latest'){
                $list = $list->orderBy('vehicles_ads.created_at' , 'desc');
            }else if($request->ordering == 'highest_price'){
                $list = $list->orderBy('price' , 'desc')
                    ->where('vehicles_ads.price' , '!=' , 0);
            }else if($request->ordering == 'lowest_price'){
                $list = $list->orderBy('price' , 'asc')
                    ->where('vehicles_ads.price' , '!=' , 0);
            }
        }else{
            $list = $list->orderBy('vehicles_ads.created_at' , 'desc');
            $priceFrom = $request->price_from == 0 ? 1 : $request->price_from;
            if ($request->has('price_from') && $request->has('price_to')) {
                $list = $list->where('vehicles_ads.price', '<=', $request->price_to)
                    ->where('vehicles_ads.price', '>=', $priceFrom);
            }
        }
        $list = $list->where('vehicles_ads.status' , '=' , 'approved');
        $offset = $request->has('offset') ? $request->offset : 0;
        $limit = $request->has('limit') ? $request->limit : 1;
        $list = $list->offset($offset)->limit($limit)->get();

        foreach ($list as $index => $row) {
            if ($row->thumbnail_photo) {
                $list[$index]->thumbnail_photo = url()->to('/ads_photo') . '/' . $row->thumbnail_photo;
            }
            $photos = VehicleAdsPhoto::where('vehicle_ads_id', $row->id)->get();
            foreach ($photos as $pIndex => $pRow) {
                $photos[$pIndex]->file_name = url()->to('/ads_photo') . '/' . $pRow->file_name;
            }
            $list[$index]->photos = $photos;

            $list[$index]->fa_created_at = convertDateTimeToJalali($row->created_at_2);
            $list[$index]->passed_time = getElapsedTime($row->created_at_2);
        }

        return response()->json(['status' => 200, 'list' => $list]);
    }

    public function deleteThumbnailInAdminPanel(Request $request , VehicleAds $ads){
        $ads->thumbnail_photo = null;
        $ads->save();
        $msg = new \stdClass();
        $msg->title = 'حذف تصویر';
        $msg->msg = 'تصویر بندانگشتی با موفقیت حذف شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }
}
