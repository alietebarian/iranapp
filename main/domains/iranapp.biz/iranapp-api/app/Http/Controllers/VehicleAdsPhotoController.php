<?php

namespace App\Http\Controllers;

use App\Http\Requests\Admin\UploadVehicleAdsPhotoRequest;
use App\Models\VehicleAds;
use App\Models\VehicleAdsPhoto;
use Illuminate\Http\Request;
use App\Libraries\Image;

class VehicleAdsPhotoController extends Controller
{
    private $maxNumbersOfPhotoToUpload = 4;

    public function __construct()
    {
        view()->share('max_numbers_of_photo_to_upload', $this->maxNumbersOfPhotoToUpload);
    }

    public function showByAdsId(Request $request, VehicleAds $ads)
    {
        $data['photos'] = $ads->photos;
        $data['ad'] = $ads;
        $available_photo_to_upload = $this->maxNumbersOfPhotoToUpload - count($ads->photos);
        $data['available_photo_to_upload'] = $available_photo_to_upload;
        return view('admin.vehicles.show_upload_photo_page')->with($data);
    }

    public function upload(Request $request, VehicleAds $ads)
    {

        foreach ($request->photos as $photo) {
            $imgName = uniqid() . '.' . $photo->getClientOriginalExtension();
            $photo->move(public_path('/ads_photo'), $imgName);
            $ads->photos()->create([
                'file_name' => $imgName
            ]);

            $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
            $imageToAddWatermark->resize(440 , null , function($constraint){
                $constraint->aspectRatio();
            });
            $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png') , 'bottom-left' , 0 , 20);
            $imageToAddWatermark->save();
        }
        $uploadedPhotosCount = count($ads->photos);
        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز';
        $msg->msg = 'تصاویر آگهی با موفقیت آپلود شد.';
        return redirect()->route('showAllVehicleAdsInAdminPanel')->with('success_msg', $msg);
    }

    public function delete(Request $request, VehicleAdsPhoto $photo)
    {
        $photo->delete();

        $msg = new \stdClass();
        $msg->title = 'حذف تصویر';
        $msg->msg = 'تصویر با موفقیت حذف شد.';

        return redirect()->back()->with('success_msg', $msg);
    }
}
