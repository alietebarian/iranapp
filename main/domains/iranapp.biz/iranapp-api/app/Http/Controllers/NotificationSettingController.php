<?php

namespace App\Http\Controllers;

use App\Models\notificationSetting;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class NotificationSettingController extends Controller
{
    public function save(Request $request){
        $count = notificationSetting::where('fcm_token' , $request->token)->count();
        if($count == 0){
            $row = new notificationSetting();
            $row->fcm_token = $request->token;
            $row->city_id = 57;
            $row->save();
        }
        return response()->json(['status' => 204]);
    }

    public function update(Request $request){
        $validator = Validator::make($request->all() , [
            'send_ads_notifications' => 'required|in:0,1',
            'send_news_notifications' => 'required|in:0,1',
            'city_id' => 'nullable|numeric|exists:city,id'
        ] , [
            'send_ads_notifications.required' => 'فیلد send_ads_notifications الزامی است.',
            'send_news_notifications.required' => 'فیلد send_news_notifications الزامی است.',
            'send_ads_notifications.in' => 'فیلد send_news_notifications تنها می تواند صفر یا یک باشد.',
            'send_news_notifications.in' => 'فیلد send_news_notifications تنها می تواند صفر یا یک باشد.',
            'city_id.numeric' => 'شهر نامعتبر است.',
            'city_id.exists' => 'شهر نامعتبر است.',
        ]);
        if($validator->fails()){
            return response()->json(['status' => 401 , 'errors' => $validator->errors()->all()]);
        }
        $token = $request->token;
        $row = notificationSetting::where('fcm_token' , $token)->first();
        $row->send_ads_notifications = $request->send_ads_notifications;
        $row->send_news_notifications = $request->send_news_notifications;
        $row->city_id = $request->city_id;
        $row->save();
        return response()->json(['status' => 204]);
    }
}
