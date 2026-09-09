<?php

namespace App\Http\Controllers;

use App\Models\Ads;
use App\Libraries\jdf;
use App\Libraries\PrNotification;
use App\Models\User;
use App\Models\UserAdsNotification;
use Carbon\Carbon;
use Illuminate\Database\QueryException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class UserAdsNotificationController extends Controller
{
    public function add(Request $request)
    {
        $user = auth('sanctum')->user();

        $adsUser = new Ads();
        $adsUserQuery = $adsUser->selectFields(Ads::FIELDS)
            ->where('ads.id', '=', $request->adsId)
            ->where('status', '=', 'approved')
            ->first();
        if (!$adsUserQuery) {
            return response()->json(['status' => 406, 'error' => 'ads_not_found']);
        }
        $appLatitude = $request->latitude;
        $appLongitude = $request->longitude;
        $adsLatitude = $adsUserQuery->latitude;
        $adsLongitude = $adsUserQuery->longitude;
        $latFrom = deg2rad($appLatitude);
        $lonFrom = deg2rad($appLongitude);
        $latTo = deg2rad($adsLatitude);
        $lonTo = deg2rad($adsLongitude);

        $latDelta = $latTo - $latFrom;
        $lonDelta = $lonTo - $lonFrom;
        $userAdsNotification = new UserAdsNotification();
        $userAdsNotification->user_id = $user->id;
        $userAdsNotification->ads_id = $request->adsId;
        $userAdsNotification->save();

        $angle = 2 * asin(sqrt(pow(sin($latDelta / 2), 2) + cos($latFrom) * cos($latTo) * pow(sin($lonDelta / 2), 2)));
        $distance = $angle * 6371000; // get distance between two points in meters
        if ($distance < 250) {
            if ($adsUserQuery->latitude && $adsUserQuery->longitude) {

                $fcm_tokens = User::whereNotNull('users.fcm_token')
                    ->whereRaw('(
                        users.id in (
                            select user_ads.user_id
                            from user_ads
                            where user_ads.ads_id = '. $request->adsId .' 
                        )
                    )')
                    ->get()
                    ->toArray();
                if (count($fcm_tokens) != 0) {
                    $body = " {$adsUserQuery->title} ، ” {$user->first_name} {$user->last_name} “ جهت استفاده از تخفیف آن مرکز معرفی می گردد.  ";

                    $notification = new PrNotification();
                    $notification->setBody($body)
                        ->setUserArray($fcm_tokens)
                        ->addData('msg', $body)
                        ->addData('status', '10')
                        ->send();
                    $userAdsNotification = new UserAdsNotification();
                    $userAdsNotification->user_id = $user->id;
                    $userAdsNotification->ads_id = $request->adsId;
                    $userAdsNotification->save();
                    return response()->json(['status' => 200]);
                } else {
                    return response()->json(['status' => 405]);
                }

            } else {
                return response()->json(['status' => 405]);
            }
        } else {
            return response()->json(['status' => 400]);
        }
    }

    public function index(Request $request)
    {
        $refers = new UserAdsNotification();
        $refersQuery = $refers->dbSelect(UserAdsNotification::FIELDS);

        if ($request->has('from_date_ts')) {
            $date = Carbon::createFromTimestamp($request->from_date_ts)->toDateString();
            $refersQuery = $refersQuery->whereDate('user_ads_notification.created_at', '>+', $date);
        }
        if ($request->has('to_date_ts')) {
            $date = Carbon::createFromTimestamp($request->to_date_ts)->toDateString();
            $refersQuery = $refersQuery->whereDate('user_ads_notification.created_at', '<', $date);
        }
        if ($request->has('ads_title')) {
            $refersQuery = $refersQuery->where('ads.id', '=', $request->ads_title);
        }
        if ($request->has('user_id') && $request->user_id != 'all') {
            $refersQuery = $refersQuery->where('user_ads_notification.user_id', '=', $request->user_id);
        }
        $refersQuery = $refersQuery->paginate(15);
        foreach ($refersQuery as $index => $refer) {
            $refersQuery[$index]->created_at_fa = jdf::jdate('j F Y ساعت H:i', Carbon::createFromFormat('Y-m-d H:i:s', $refer->created_at)->getTimestamp());
        }
        $data['list'] = $refersQuery;
        $data['users'] = User::all();
        return view('admin.business-refers.index')->with($data);
    }

    public function destroy(Request $request , UserAdsNotification $item){
        try{
            $item->delete();
        }catch (QueryException $e){
            if($e->errorInfo[1] == 1451){
                $msg = new \stdClass();
                $msg->title = 'خطا';
                $msg->msg = 'امکان حذف این آیتم به دلیل وجود رکوردهای مرتبط وجود ندارد.';
                return redirect()->back()->with('error_msg' , $msg);
            }
        }
        $msg = new \stdClass();
        $msg->title = 'حذف موفقیت آمیز';
        $msg->msg = 'آیتم مورد نظر با موفقیت ثبت شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }
}
