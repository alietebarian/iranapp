<?php

namespace App\Http\Controllers;

use App\Models\Ads;
use Illuminate\Http\Request;

class AdsVideoController extends Controller
{
    public function uploadInAdminPanel(Request $request , Ads $ads){
        $request->validate([
            'video' => 'required|file|mimes:' . implode(',' , Ads::VIDEO_FORMATS) . '|max:' . Ads::VIDEO_MAX_KB,
        ] , [
            'video.required' => 'فایل ویدیو انتخاب نشده است.',
            'video.uploaded' => 'آپلود ویدیو ناموفق بود؛ احتمالاً حجم فایل بیشتر از حد مجاز سرور است.',
            'video.mimes' => 'فرمت ویدیو پشتیبانی نمی شود. فرمت های مجاز: ' . implode('، ' , Ads::VIDEO_FORMATS),
            'video.max' => 'حجم ویدیو بیشتر از حد مجاز است.',
        ]);

        $ads->replaceVideo($request->file('video'));

        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز';
        $msg->msg = 'ویدیو آگهی با موفقیت آپلود شد.';
        return redirect()->route('showAdsPhotoById' , $ads->id)->with('success_msg' , $msg);
    }

    public function deleteInAdminPanel(Ads $ads){
        $ads->removeVideo();
        $msg = new \stdClass();
        $msg->title = 'حذف موفقیت آمیز';
        $msg->msg = 'ویدیو آگهی با موفقیت حذف شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }
}
