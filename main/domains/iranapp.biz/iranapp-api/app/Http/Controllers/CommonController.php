<?php

namespace App\Http\Controllers;

use App\Models\AppInstall;
use App\Models\Setting;
use App\Support\InstallStats;
use Illuminate\Database\UniqueConstraintViolationException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Cache;
use Illuminate\Support\Facades\Validator;

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
     * Install counts for the app's landing page: total, last 30 days and today (from midnight
     * Tehran time). See App\Support\InstallStats for how installs are counted.
     */
    public function installStats(){
        $stats = Cache::remember('install_stats_v2', now()->addMinutes(10), fn () => InstallStats::summary());
        return response()->json(['status' => 200] + $stats);
    }

    /**
     * Called by the app on first launch with the random install ID it generated, and again
     * whenever its version changes. The first report creates the install; later ones only
     * refresh the version fields.
     */
    public function registerInstall(Request $request){
        $validator = Validator::make($request->all(), [
            'install_id' => ['required', 'string', 'regex:/^[A-Za-z0-9\-]{16,64}$/'],
            'installer_package' => ['nullable', 'string', 'max:150', 'regex:/^[A-Za-z0-9_.]+$/'],
            'is_upgrade' => 'nullable|boolean',
            'app_version' => 'nullable|string|max:40',
            'android_version' => 'nullable|string|max:20',
            'device_model' => 'nullable|string|max:100',
        ]);
        if ($validator->fails()) {
            return response()->json(['status' => 422, 'errors' => $validator->errors()->all()]);
        }

        $versionFields = [
            'app_version' => $request->input('app_version'),
            'android_version' => $request->input('android_version'),
            'device_model' => $request->input('device_model'),
        ];
        $install = AppInstall::where('install_id', $request->input('install_id'))->first();
        if ($install) {
            $install->update($versionFields);

            return response()->json(['status' => 200]);
        }

        try {
            AppInstall::create($versionFields + [
                'install_id' => $request->input('install_id'),
                'source' => AppInstall::sourceFromInstaller($request->input('installer_package')),
                'installer_package' => $request->input('installer_package'),
                'is_upgrade' => $request->boolean('is_upgrade'),
            ]);
        } catch (UniqueConstraintViolationException $e) {
            // The same install reported twice at once (a retried request): already recorded.
            return response()->json(['status' => 200]);
        }

        return response()->json(['status' => 201]);
    }
}
