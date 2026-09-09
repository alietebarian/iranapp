<?php

namespace App\Http\Controllers;

use App\Models\EmploysAds;
use App\Models\EmploysAdsPhoto;
use App\Http\Requests\Admin\UploadVehicleAdsPhotoRequest;
use Illuminate\Http\Request;
use Intervention\Image\Facades\Image;

class EmploysAdsPhotoController extends Controller
{
    private $maxNumbersOfPhotoToUpload = 4;

    public function __construct()
    {
        view()->share('max_numbers_of_photo_to_upload', $this->maxNumbersOfPhotoToUpload);
    }

    public function showByAdsId(Request $request, EmploysAds $ads)
    {
        $data['photos'] = $ads->photo;
        $data['ad'] = $ads;
        $available_photo_to_upload = $this->maxNumbersOfPhotoToUpload - count($ads->photo);
        $data['available_photo_to_upload'] = $available_photo_to_upload;
        return view('admin.employs.show_upload_photo_page')->with($data);
    }

    public function upload(UploadVehicleAdsPhotoRequest $request, EmploysAds $ads)
    {
        foreach ($request->photos as $photo) {
            $imgName = uniqid() . '.' . $photo->getClientOriginalExtension();
            $photo->move(public_path('/ads_photo'), $imgName);
            $ads->photo()->create([
                'file_name' => $imgName
            ]);

            $imageToAddWatermark = (public_path('/ads_photo/') . '/' . $imgName);
            $imageToAddWatermark->resize(440 , null , function($constraint){
                $constraint->aspectRatio();
            });
            $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png') , 'bottom-left' , 0 , 20);
            $imageToAddWatermark->save();

        }
        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز';
        $msg->msg = 'تصاویر آگهی با موفقیت آپلود شد.';
        return redirect()->route('showAllEmploysAdsInAdminPanel')->with('success_msg', $msg);
    }

    public function delete(Request $request, EmploysAdsPhoto $photo)
    {
        $photo->delete();

        $msg = new \stdClass();
        $msg->title = 'حذف تصویر';
        $msg->msg = 'تصویر با موفقیت حذف شد.';

        return redirect()->back()->with('success_msg', $msg);
    }
}
