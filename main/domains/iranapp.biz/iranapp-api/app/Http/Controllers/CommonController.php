<?php

namespace App\Http\Controllers;

use App\Models\notificationSetting;
use App\Models\Setting;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Cache;

class CommonController extends Controller
{
    public function showCurrencies(Request $request)
    {
        return view('currency');
    }
    public function showFootball(Request $request)
    {
        return view('football');
    }
    public function showGulf(Request $request)
    {
        return view('gulf');
    }

    public function appVersionShow(){
        $version = Setting::find('app_version');
        if($version){
            $versionName = $version->setting_value;
        }else{
            $versionName = null;
        }
        return response()->json(['status' => 200 , 'app_version' => (int)$versionName]);
    }

    /**
     * Install counts for the app's landing page. Every install registers its FCM token once
     * (notification-settings/create), so each notification_setting row is one installed device.
     */
    public function installStats(){
        $stats = Cache::remember('install_stats', now()->addMinutes(10), function () {
            return [
                'total' => notificationSetting::count(),
                'month' => notificationSetting::where('created_at', '>=', Carbon::now()->subDays(30))->count(),
                'today' => notificationSetting::where('created_at', '>=', Carbon::today())->count(),
            ];
        });
        return response()->json(['status' => 200] + $stats);
    }
}
