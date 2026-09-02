<?php

namespace App\Http\Controllers;

use App\News;
use App\NewsPhoto;
use Illuminate\Http\Request;

class NewsPhotoController extends Controller
{
    public function showByNewsId(Request $request , News $news){
        $photos = $news->photo;
        $maximum_photo_to_upload = 10;
        $uploadedPhotos = count($photos);
        $availablePhotosCount = $maximum_photo_to_upload - $uploadedPhotos;
        $data['available_photo_to_upload'] = $availablePhotosCount;
        $data['news'] = $news;
        $data['photos'] = $photos;
        return view('admin.news_photo')->with($data);
    }

    public function upload(Request $request , News $news){
        if($request->hasFile('photo')){
            $photos = $request->photo;
            foreach($photos as $photo){
                $imgName = uniqid() . '.' . $photo->getClientOriginalExtension();
                $photo->move(public_path('/news_photo') , $imgName);
                $row = new NewsPhoto();
                $row->file_name = $imgName;
                $row->news_id = $news->id;
                $row->save();
            }
        }
        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز';
        $msg->msg = 'تصاویر خبر با موفقیت آپلود شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }

    public function deleteById(Request $request , NewsPhoto $newsPhoto){
        $newsPhoto->delete();
        $msg = new \stdClass();
        $msg->title = 'حذف موفقیت آمیز';
        $msg->msg = 'تصویر خبر با موفقیت حذف شد.';
        return redirect()->back()->with('success_msg' ,$msg);
    }
}
