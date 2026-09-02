<?php

namespace App\Http\Controllers;

use App\Http\Requests\Admin\saveNews;
use App\Libraries\jdf;
use App\News;
use App\NewsPhoto;
use App\Notification;
use Carbon\Carbon;
use Illuminate\Database\QueryException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\URL;
use LaravelFCM\Facades\FCM;
use LaravelFCM\Message\OptionsBuilder;
use LaravelFCM\Message\PayloadDataBuilder;
use LaravelFCM\Message\PayloadNotificationBuilder;

class NewsController extends Controller
{
    public function showAll(Request $request)
    {
        if ($request->has('offset') && $request->has('limit')) {
            $offset = $request->offset;
            $limit = $request->limit;
        } else {
            $offset = 0;
            $limit = 1;
        }
        $news = DB::table('news')->orderBy('created_at' , 'desc')->offset($offset)->limit($limit)->get();
//        $news = News::orderBy('created_at' , 'desc')->offset($offset)->limit($limit)->get();
        foreach($news as $newsIndex =>  $row){
            $photos = NewsPhoto::where('news_id' , $row->id)->get();
            foreach($photos as $index => $value){
                $photos[$index]->file_name = URL::to('/news_photo') . '/' . $value->file_name;
            }
            $news[$newsIndex]->created_at = jdf::jdate('j F Y H:i:s' , Carbon::createFromFormat('Y-m-d H:i:s' , $row->created_at)->getTimestamp());
            $news[$newsIndex]->updated_at = jdf::jdate('j F Y H:i:s' , Carbon::createFromFormat('Y-m-d H:i:s' , $row->updated_at)->getTimestamp());
            $news[$newsIndex]->photos = $photos;
        }
        return response()->json(['status' => 200, 'list' => $news]);
    }

    public function showAllInAdminPanel(Request $request)
    {
        $news = DB::table('news')->orderBy('created_at', 'desc')->get();
        $data['news'] = $news;
        return view('admin.news_list')->with($data);
    }

    public function delete(Request $request, News $news)
    {
        try {
            $news->delete();
        } catch (QueryException $exception) {
            if ($exception->errorInfo[1] == 1451) {
                $msg = new \stdClass();
                $msg->title = 'خطا';
                $msg->msg = 'امکان حذف خبر به دلیل وجود رکوردهای مرتبط وجود ندارد.';
                return redirect()->back()->with('error_msg', $msg);
            }
        }
        $msg = new \stdClass();
        $msg->title = 'حذف موفقیت آمیز';
        $msg->msg = 'خبر با موفقیت حذف شد.';
        return redirect()->back()->with('success_msg', $msg);
    }

    public function getById(Request $request, News $news)
    {
        $createdAtTimestamp = Carbon::createFromFormat('Y-m-d H:i:s', $news->created_at)->getTimestamp();
        $updatedAtTimestamp = Carbon::createFromFormat('Y-m-d H:i:s', $news->updated_at)->getTimestamp();
        $news->created_at_fa = jdf::jdate('j F Y', $createdAtTimestamp);
        $news->updated_at_fa = jdf::jdate('j F Y', $updatedAtTimestamp);
        $photos = NewsPhoto::where('news_id', $news->id)->get();
        $photos->each(function ($item, $index) use ($photos) {
            $photos[$index]->file_name = URL::to('/news_photo') . '/' . $item->file_name;
        });
        $news->photos = $photos;
        return response()->json(['status' => 200, 'news' => $news]);
    }

    public function showInsertFormInAdminPanel(Request $request)
    {
        return view('admin.add_news');
    }

    public function saveNews(saveNews $request)
    {
        $news = new News();
        $news->title = $request->title;
        $news->passage = $request->news_text;
        $news->save();
        if ($request->has('send_notification')) {
            $fcm_tokens = DB::table('users')->where('users.send_news_notifications', '=', 1)->pluck('fcm_token')->toArray();
            event(new \App\Events\Admin\News\Store($fcm_tokens, $news));
        }
//        if($request->has('send_notification')){
////            $fcm_tokens = DB::table('users')->where('users.send_news_notifications' , '=' , 1)->pluck('fcm_token')->toArray();
//            $fcm_tokens = DB::table('notification_setting')->where('send_news_notifications' , '=' , 1)->pluck('fcm_token')->toArray();
//            if(count($fcm_tokens) > 0 ){
//                $optionBuilder = new OptionsBuilder();
//                $optionBuilder->setTimeToLive(60 * 20);
//
//                $notificationBuilder = new PayloadNotificationBuilder('ایران اپ');
//                $notificationBuilder->setBody($news->title)
//                    ->setSound('default');
//
//                $dataBuilder = new PayloadDataBuilder();
//                $dataBuilder->addData(['status' => '1' , 'content_id' => $news->id]);
//                $data = $dataBuilder->build();
//                $option = $optionBuilder->build();
//                $notification = $notificationBuilder->build();
//
//                $downstreamResponse = FCM::sendTo($fcm_tokens, $option, $notification , $data);
//
//                $downstreamResponse->numberFailure();
//                $downstreamResponse->numberModification();
//                $downstreamResponse->numberSuccess();
//
//
//                $notification = new Notification();
//                $notification->news_id = $news->id;
//                $notification->msg_text = $news->title;
//                $notification->successfully_sent = $downstreamResponse->numberSuccess();
//                $notification->failures_on_send = $downstreamResponse->numberFailure();
//                $notification->save();
//            }
//
//        }


        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز خبر';
        $msg->msg = 'خبر با موفقیت ثبت شد.';
        return redirect()->route('showNewsListInAdminPanel')->with('success_msg', $msg);
    }

    public function showNewsUpdatePage(Request $request, News $news)
    {
        $data['news'] = $news;
        return view('admin.news_update')->with($data);
    }

    public function updateNewsInAdminPanel(saveNews $request, News $news)
    {
        $news->title = $request->title;
        $news->passage = $request->news_text;

        $success = $news->save();
        if ($success) {
            $msg = new \stdClass();
            $msg->title = 'ویرایش موفقیت آمیز خبر';
            $msg->msg = 'خبر با موفقیت ویرایش شد.';
            return redirect()
//                ->back()
                ->route('showNewsListInAdminPanel')
                ->with('success_msg', $msg);
        } else {
            $msg = new \stdClass();
            $msg->title = 'خطا';
            $msg->msg = 'خطایی درهنگام ثبت اطلاعات رخ داده است. لطفا مجددا تلاش کنید.';
            return redirect()
//                ->back()
                ->route('showNewsListInAdminPanel')
                ->with('error_msg', $msg);
        }

    }
}
