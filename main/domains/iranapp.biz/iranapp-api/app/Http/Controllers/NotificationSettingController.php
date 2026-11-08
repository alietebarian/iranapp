<?php

namespace App\Http\Controllers;

use App\Models\notificationSetting;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class NotificationSettingController extends Controller
{
    /**
     * Registers a device's FCM token. Since 2.0.3 the app also sends old_token, the token the
     * server last had for this device: FCM issues a new token whenever it rotates one (and every
     * device got one when the app moved to the iranapp-fc361 Firebase project), and pushes to
     * the old one can never arrive. The device keeps its city and choices, and a logged-in user
     * holding the old token gets the new one.
     */
    public function save(Request $request){
        if (empty($request->token)) {
            return response()->json(['status' => 401 , 'errors' => ['توکن الزامی است.']]);
        }

        $oldToken = $request->old_token;
        if (! empty($oldToken) && $oldToken !== $request->token) {
            User::where('fcm_token', $oldToken)->update(['fcm_token' => $request->token]);

            if (notificationSetting::where('fcm_token', $request->token)->exists()) {
                notificationSetting::where('fcm_token', $oldToken)->delete();
            } else {
                notificationSetting::where('fcm_token', $oldToken)->update(['fcm_token' => $request->token]);
            }
        }

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
        if (empty($token)) {
            return response()->json(['status' => 401 , 'errors' => ['توکن الزامی است.']]);
        }
        // A device whose token never reached save() (e.g. a token rotated before 2.0.3) used to
        // get a 500 here; registering it now is what the user asked for anyway.
        $row = notificationSetting::where('fcm_token' , $token)->first();
        if (! $row) {
            $row = new notificationSetting();
            $row->fcm_token = $token;
        }
        $row->send_ads_notifications = $request->send_ads_notifications;
        $row->send_news_notifications = $request->send_news_notifications;
        $row->city_id = $request->city_id;
        $row->save();
        return response()->json(['status' => 204]);
    }
}
