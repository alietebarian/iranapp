<?php

namespace App\Http\Controllers;

use App\Models\Ads;
use App\Models\AdsPhoto;
use App\Models\AdsPlan;
use App\Models\EmploysAdsPhoto;
use App\Models\EstateAdsPhoto;
use App\Models\VehicleAdsPhoto;
use Illuminate\Http\Request;
use Intervention\Image\Facades\Image;

class AdsPhotoController extends Controller
{
    public function remove(Request $request , AdsPhoto $adsPhoto){
        $adsPhoto->delete();

        return response()->json(['status' => 204]);
    }

    public function save(Request $request , Ads $ads){
        $photo = new AdsPhoto();
        $photo->ads_id = $ads->id;
        if($request->hasFile('img')){
            $imgName = uniqid() . '.' . $request->img->getClientOriginalExtension();
            $request->img->move(public_path('ads_photo') , $imgName);
            $photo->file_name = $imgName;

            $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
            $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png') , 'bottom-left' , 10 , 10);
            $imageToAddWatermark->save();

        }
        $photo->save();
        return response()->json(['status' => 200 , 'photo' => $photo]);
    }

    public function deleteInAdminPanel(AdsPhoto $photo){
        $photo->delete();
        $msg = new \stdClass();
        $msg->title = 'حذف موفقیت آمیز';
        $msg->msg = 'تصویر آگهی با موفقیت حذف شد.';
        return redirect()->back()->with('success_msg' ,$msg);
    }

    public function showById(Request $request , Ads $ads){
        $data['ad'] = $ads;
        $photos = $ads->photo;
        $data['photos'] = $photos;
        $data['uploaded_photos_count'] = $photos->count();
        $plan = AdsPlan::find($ads->ads_plan_id);
        $data['plan'] = $plan;
        $maximum_photo_to_upload = $plan->max_number_of_photos;
        $data['max_photo_upload'] = $maximum_photo_to_upload;
        $available_uploading_photo = $maximum_photo_to_upload - $photos->count();
        $data['available_photo_to_upload'] = $available_uploading_photo;
        return view('admin.ads_photo')->with($data);
    }

    public function uploadInAdminPanel(Request  $request , Ads $ads){
        if($request->hasFile('photo')){
            $photos = $request->photo;
            foreach($photos as $photo){
                $imgName = uniqid() . '.' . $photo->getClientOriginalExtension();
                $photo->move(public_path('/ads_photo') , $imgName);
                $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
                $imageToAddWatermark->resize(440 , null , function($constraint){
                    $constraint->aspectRatio();
                });
                $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png') , 'bottom-left' , 0 , 20);
                $imageToAddWatermark->save();

                $row = new AdsPhoto();
                $row->file_name = $imgName;
                $row->ads_id = $ads->id;
                $row->save();
            }
        }
        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز';
        $msg->msg = 'تصاویر آگهی با موفقیت آپلود شد.';
        return redirect()->route('showAdsListInAdminPanel')->with('success_msg' , $msg);
    }
}
