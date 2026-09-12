<?php

namespace App\Http\Controllers;

use App\Models\Ads;
use App\Models\AdsPhoto;
use App\Models\AdsPlan;
use App\Models\EmploysAdsPhoto;
use App\Models\EstateAdsPhoto;
use App\Models\VehicleAdsPhoto;
use Illuminate\Http\Request;
use App\Libraries\Image;

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
        $data['photo_formats'] = AdsPhoto::FORMATS;
        $data['photo_max_mb'] = AdsPhoto::MAX_KB / 1024;
        $data['video_url'] = Ads::videoUrl($ads->video);
        $data['video_formats'] = Ads::VIDEO_FORMATS;
        $data['video_max_bytes'] = Ads::maxVideoUploadBytes();
        return view('admin.ads_photo')->with($data);
    }

    public function uploadInAdminPanel(Request  $request , Ads $ads){
        $request->validate([
            'photo' => 'required|array',
            'photo.*' => 'file|mimes:' . implode(',' , AdsPhoto::FORMATS) . '|max:' . AdsPhoto::MAX_KB,
        ] , [
            'photo.required' => 'هیچ تصویری انتخاب نشده است.',
            'photo.*.uploaded' => 'آپلود تصویر ناموفق بود؛ احتمالاً حجم فایل بیشتر از حد مجاز سرور است.',
            'photo.*.mimes' => 'فرمت تصویر پشتیبانی نمی شود. فرمت های مجاز: ' . implode('، ' , AdsPhoto::FORMATS),
            'photo.*.max' => 'حجم هر تصویر حداکثر ' . (AdsPhoto::MAX_KB / 1024) . ' مگابایت است.',
        ]);
        // The page only offers the free slots, but nothing stopped a request from sending more.
        $available = AdsPlan::find($ads->ads_plan_id)->max_number_of_photos - $ads->photo()->count();
        $photos = array_slice(array_values($request->file('photo')) , 0 , max(0 , $available));
        foreach($photos as $photo){
            // Name the file after its content rather than the client's name: a JPEG called
            // '.jfif', or a file with no extension, leaves the encoder no format to save as.
            $imgName = uniqid() . '.' . $photo->guessExtension();
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
        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز';
        $msg->msg = 'تصاویر آگهی با موفقیت آپلود شد.';
        return redirect()->route('showAdsListInAdminPanel')->with('success_msg' , $msg);
    }
}
