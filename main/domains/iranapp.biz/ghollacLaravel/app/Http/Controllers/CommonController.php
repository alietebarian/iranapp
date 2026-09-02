<?php

namespace App\Http\Controllers;

use App\Setting;
use Illuminate\Http\Request;

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
}
