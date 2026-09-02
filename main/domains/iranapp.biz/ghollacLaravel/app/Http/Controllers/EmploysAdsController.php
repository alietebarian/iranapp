<?php

namespace App\Http\Controllers;

use App\City;
use App\EmploysAds;
use App\EmploysAdsPhoto;
use App\EmploysAdsSpecialty;
use App\Http\Requests\Admin\SaveEmploysAdsRequest;
use App\Http\Requests\Api\Employs\SaveJson;
use App\Http\Requests\Api\Employs\UpdateJson;
use App\Process\PrEmploysAds;
use App\Process\PrVehicleAds;
use App\Province;
use App\Region;
use App\User;
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

class EmploysAdsController extends Controller
{
    public function showAllInAdmin(Request $request)
    {

        $ads = PrEmploysAds::adminPanelEmploysAds()->orderBy('employs_ads.created_at', 'desc');

        if ($request->has('region_id') && $request->region_id != 'all') {
            $ads = $ads->where('employs_ads.region_id', '=', $request->region_id);
        } else {/*region_id*/
            if ($request->has('city_id') && $request->city_id != 'all') {
                $ads = $ads->whereRaw('employs_ads.region_id in (
                    select region.id from region where region.city_id = ' . $request->city_id . '
                )');
            } else {/*city_id*/
                if ($request->has('province_id') && $request->province_id != 'all') {
                    $ads = $ads->whereRaw('employs_ads.region_id in (
                        select region.id from region where region.city_id in (
                            select city.id from city where city.province_id = ' . $request->province_id . '
                        )
                    )');
                }/*province_id*/
            }
        }
        if($request->has('person_or_company') && $request->person_or_company != 'all'){
            $ads = $ads->where('employs_ads.person_or_company' , '=' , $request->person_or_company);
        }
        if ($request->has('type')) {
            $ads = $ads->where('employs_ads.type' , '=' , $request->type);
        }/*type*/

        if($request->has('specialty') && $request->specialty != 'all'){
            $ads = $ads->where('employs_ads.specialty_id' , '=' , $request->specialty);
        }/*brand*/

        if($request->has('agremment_type') && $request->agremment_type != 'all'){
            $ads = $ads->where('employs_ads.agremment_type' , '=' , $request->agremment_type);
        }/*chassis_type*/

        if($request->has('education_level') && $request->education_level != 'all'){
            $ads = $ads->where('employs_ads.education_level' , '=' , $request->education_level);
        }/*neworold*/

        if($request->has('status') && $request->status != 'all'){
            $ads = $ads->where('employs_ads.status' , '=' , $request->status);
        }
        if($request->has('keyword')){
            $ads = $ads->whereRaw('( MATCH(employs_ads.ads_title , employs_ads.description) AGAINST("'. $request->keyword.'" IN NATURAL LANGUAGE MODE) )');
        }
        $ads = $ads->paginate(15);
        $data['ads'] = $ads;
        $data['provinces'] = Province::all();
        $data['specialtys'] = DB::table('employs_ads_specialty')->get();
        return view('admin.employs.list')->with($data);
    }/*showAllInAdmin*/

    public function deleteById(Request $request, EmploysAds $ads)
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
        $data['specialtys'] = DB::table('employs_ads_specialty')->get();
        return view('admin.employs.addPage')->with($data);
    }/*showCreatePage*/

    public function save(SaveEmploysAdsRequest $request)
    {
        $ads = new EmploysAds();

        $ads->ads_title = $request->ads_title;
        $ads->specialty_id = $request->specialty;
        $ads->agremment_type = $request->agremment_type;
        $ads->region_id = $request->region_id;
        $ads->education_level = $request->education_level;
        $ads->user_id = $request->user_id;
        $ads->address = $request->address;
        $ads->telephone1 = $request->telephone1;
        $ads->telephone2 = $request->telephone2;
        $ads->status = $request->status;
        $ads->ads_owner_name = $request->ads_owner_name;
        $ads->person_or_company = $request->person_or_company;
        $ads->type = $request->type;
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
        return redirect()->route('employsAds.photos.show', $ads->id)->with('success_msg', $msg);
    }/*save*/

    public function showUpdatePage(Request $request, EmploysAds $ads)
    {
        $data['provinces'] = Province::all();
        $data['users'] = User::all();
        $data['Specialtys'] = EmploysAdsSpecialty::all();
        $data['ads'] = $ads;
        $currentCity = Region::find($ads->region_id)->city;
        $currentProvince = $currentCity->province;
        $data['currentProvince'] = $currentProvince;
        $data['currentCity'] = $currentCity;
        $data['cities'] = $currentProvince->city;
        $data['regions'] = $currentCity->region;
        $data['photos'] = $ads->photo;
        return view('admin.employs.update')->with($data);
    }/*showUpdatePage*/

    public function update(Request $request, EmploysAds $ads)
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

        $ads->specialty_id = $request->specialty;
        $ads->agremment_type = $request->agremment_type;
        $ads->education_level = $request->education_level;
        $ads->person_or_company = $request->person_or_company;
        $ads->type = $request->type;
        $ads->save();

        $msg = new \stdClass();
        $msg->title = 'ویرایش موفقیت آمیز';
        $msg->msg = 'آگهی با موفقیت ویرایش شد.';
        if (count($ads->photo) == 5) {
            return redirect()->route('showAllEmploysAdsInAdminPanel')->with('success_msg', $msg);
        }
        return redirect()->route('employsAds.photos.show', $ads->id)->with('success_msg', $msg);
    }

    public function getJsonByCityId(Request $request , City $city){
        $adsObj = new EmploysAds();
        $list = $adsObj->selectFields(EmploysAds::FIELDS)
            ->where('city.id' , '=' , $city->id)
            ->where('employs_ads.status' , '=' , 'approved')
            ->orderBy('employs_ads.created_at' , 'desc')
            ->where('employs_ads.valid_since' , '<=' , Carbon::now()->toDateString())
            ->where('employs_ads.valid_until' , '>=' , Carbon::now()->toDateString());
        if($request->has('type') && in_array($request->type , ['karjoo' , 'forsatshoghli'])){
            $list = $list->where('employs_ads.type' , '=' , $request->type);
        }
        $offset = $request->has('offset') ? $request->offset :  0;
        $limit = $request->has('limit') ? $request->limit : 1;
        $list = $list->offset($offset)->limit($limit)->get();

        foreach($list as $index => $row){
            $photos = EmploysAdsPhoto::where('employs_ads_id' , '=' , $row->id)->get();
            foreach($photos as $pIndex => $pRow){
                $photos[$pIndex]->file_name = url()->to('/ads_photo') . '/' . $pRow->file_name;
            }
            $list[$index]->photos = $photos;

            $list[$index]->fa_created_at = convertDateTimeToJalali($row->created_at_2);
            $list[$index]->elapsed_time = getElapsedTime($row->created_at_2);

            if($row->thumbnail_photo){
                $list[$index]->thumbnail_photo = url()->to('/ads_photo') . '/' . $row->thumbnail_photo;
            }
        }

        return response()->json(['status' => 200 , 'list' => $list]);
    }

    public function saveJson(SaveJson $request){
        $user = JWTAuth::parseToken()->authenticate();

        $ads = new EmploysAds();
        $ads->ads_title = $request->ads_title;
        $ads->specialty_id = $request->specialty;
        $ads->agremment_type = $request->agremment_type;
        $ads->region_id = $request->region_id;
        $ads->education_level = $request->education_level;
        $ads->user_id = $user->id;
        $ads->address = $request->address;
        $ads->telephone1 = $request->telephone1;
        $ads->telephone2 = $request->telephone2;
        $ads->person_or_company = $request->person_or_company;
        $ads->status = 'pending';
        $ads->ads_owner_name = $request->ads_owner_name;
        $ads->type = $request->type;
        if ($request->hasFile('thumbnail_photo')) {
            $imgName = uniqid() . '.' . $request->thumbnail_photo->getClientOriginalExtension();
            $request->thumbnail_photo->move(public_path('/ads_photo'), $imgName);
            $ads->thumbnail_photo = $imgName;

            $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
            $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png') , 'bottom-left' , 0 , 25);
            $imageToAddWatermark->save();
        }
        $ads->description = $request->description;
        $ads->latitude = $request->latitude;
        $ads->longitude = $request->longitude;
        $ads->created_at_2 = Carbon::now()->toDateTimeString();
        $ads->valid_since = Carbon::now()->toDateString();
        $ads->valid_until = Carbon::now()->addYear(1)->toDateString();
        $ads->save();

        if($request->hasFile('photos')){
            $photosArr = $request->photos;
            foreach($photosArr as $photo){
                $imgName = uniqid() . '.' . $photo->getClientOriginalExtension();
                $photo->move(public_path('/ads_photo') , $imgName);
                $adPhoto = new EmploysAdsPhoto();
                $adPhoto->file_name = $imgName;
                $adPhoto->employs_ads_id = $ads->id;
                $adPhoto->save();

                $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
                $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png') , 'bottom-left' , 0 , 25);
                $imageToAddWatermark->save();
            }
        }

        return response()->json(['status' => 200]);
    }

    public function getJsonByUser(Request $request){
        $user = JWTAuth::parseToken()->authenticate();
        $adsObj = new EmploysAds();
        $offset = $request->has('offset') ? $request->offset : 0;
        $limit = $request->has('limit') ? $request->limit : 1;
        $list = $adsObj->selectFields(EmploysAds::FIELDS)
            ->where('employs_ads.status' , '!=' , 'deleted')
            ->where('employs_ads.user_id' , '=' , $user->id)
            ->orderBy('employs_ads.created_at' , 'desc')
            ->offset($offset)->limit($limit)
            ->where('employs_ads.valid_since' , '<=' , Carbon::now()->toDateString())
            ->where('employs_ads.valid_until' , '>=' , Carbon::now()->toDateString())
            ->get();

        foreach($list as $index => $row){
            $photos = EmploysAdsPhoto::where('employs_ads_id' , '=' , $row->id)->get();
            foreach($photos as $pIndex => $pRow){
                $photos[$pIndex]->file_name = url()->to('/ads_photo') . '/' . $pRow->file_name;
            }
            $list[$index]->photos = $photos;

            $list[$index]->fa_created_at = convertDateTimeToJalali($row->created_at_2);
            $list[$index]->elapsed_time = getElapsedTime($row->created_at_2);

            if($row->thumbnail_photo){
                $list[$index]->thumbnail_photo = url()->to('/ads_photo') . '/' . $row->thumbnail_photo;
            }
        }

        return response()->json(['status' => 200 , 'list' => $list]);
    }

    public function deleteJson(Request $request , EmploysAds $ads){
        $ads->status = 'deleted';
        $ads->save();

        return response()->json(['status' => 200]);
    }

    public function updateJson(UpdateJson $request , EmploysAds $ads){
        $user = JWTAuth::parseToken()->authenticate();
        $ads->ads_title = $request->ads_title;
        $ads->specialty_id = $request->specialty;
        $ads->agremment_type = $request->agremment_type;
        $ads->region_id = $request->region_id;
        $ads->education_level = $request->education_level;
        $ads->address = $request->address;
        $ads->telephone1 = $request->telephone1;
        $ads->telephone2 = $request->telephone2;
        $ads->status = 'pending';
        $ads->ads_owner_name = $request->ads_owner_name;
        $ads->person_or_company = $request->person_or_company;
        $ads->type = $request->type;
        if($request->has('delete_thumbnail')){
            $ads->thumbnail_photo = null;
        }
        if ($request->hasFile('thumbnail_photo')) {
            $imgName = uniqid() . '.' . $request->thumbnail_photo->getClientOriginalExtension();
            $request->thumbnail_photo->move(public_path('/ads_photo'), $imgName);
            $ads->thumbnail_photo = $imgName;

            $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
            $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png') , 'bottom-left' , 0 , 25);
            $imageToAddWatermark->save();
        }

        $ads->description = $request->description;
        $ads->latitude = $request->latitude;
        $ads->longitude = $request->longitude;

        $ads->valid_since = Carbon::now()->toDateString();
        $ads->valid_until = Carbon::now()->addYear(1)->toDateString();
        $ads->save();

        if($request->hasFile('photos')){
            $photosArr = $request->photos;
            foreach($photosArr as $photo){
                $imgName = uniqid() . '.' . $photo->getClientOriginalExtension();
                $photo->move(public_path('ads_photo') , $imgName);
                $adPhoto = new EmploysAdsPhoto();
                $adPhoto->file_name = $imgName;
                $adPhoto->employs_ads_id = $ads->id;
                $adPhoto->save();

                $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
                $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png') , 'bottom-left' , 0 , 25);
                $imageToAddWatermark->save();
            }
        }

        if($request->has('photo_to_delete')){
            $photos = $request->photo_to_delete;
            foreach($photos as $id){
                $photo = EmploysAdsPhoto::find($id);
                $photo->delete();
            }
        }

        return response()->json(['status' => 200]);
    }
    public function searchJson(Request $request){
        $adsObj = new EmploysAds();
        $list = $adsObj->selectFields(EmploysAds::FIELDS)
            ->where('employs_ads.status' , '=' , 'approved')
            ->orderBy('employs_ads.created_at' , 'desc')
            ->where('employs_ads.valid_since' , '<=' , Carbon::now()->toDateString())
            ->where('employs_ads.valid_until' , '>=' , Carbon::now()->toDateString());
        if($request->has('region_id')){
            $list = $list->where('region.id' , '=' , $request->region_id);
        }else{
            if($request->has('city_id')){
                $list = $list->where('city.id' , '=' , $request->city_id);
            }else{
                if($request->has('province_id')){
                    $list = $list->where('province.id' , '=' , $request->province_id);
                }
            }
        }

        if($request->has('keyword')){
            $list = $list->whereRaw('( MATCH(employs_ads.ads_title , employs_ads.description) AGAINST("'. $request->keyword .'" IN NATURAL LANGUAGE MODE) )');
        }
        if($request->has('type') && in_array($request->type , ['karjoo' , 'forsatshoghli'])){
            $list = $list->where('employs_ads.type' , '=' , $request->type);
        }
        if($request->has('education_level')){
            $list = $list->where('employs_ads.education_level' , '=' , $request->education_level);
        }
        if($request->has('speciality_id')){
            $list = $list->where('employs_ads.specialty_id' , '=' , $request->speciality_id);
        }
        if($request->has('agreement_type')){
            $list = $list->where('employs_ads.agremment_type' , '=' , $request->agreement_type);
        }
        $offset = $request->has('offset') ? $request->offset : 0;
        $limit = $request->has('limit') ? $request->limit : 1;
        $list = $list->offset($offset)->limit($limit)->get();


        foreach($list as $index => $row){
            $photos = EmploysAdsPhoto::where('employs_ads_id' , $row->id)->get();

            foreach($photos as $pIndex => $pRow){
                $photos[$pIndex]->file_name = url()->to('/ads_photo') . '/' . $pRow->file_name;
            }
            $list[$index]->photos = $photos;

            $list[$index]->elapsed_time = getElapsedTime($row->created_at_2);
            $list[$index]->fa_created_at = convertDateTimeToJalali($row->created_at_2);
        }

        return response()->json(['status' => 200 , 'list' => $list]);
    }

    public function deleteThumbnailPhoto(Request $request , EmploysAds $ads){
        $ads->thumbnail_photo = null;
        $ads->delete();

        $ads->save();
        $msg = new \stdClass();
        $msg->title = 'حذف تصویر';
        $msg->msg = 'تصویر بندانگشتی با موفقیت حذف شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }
}
