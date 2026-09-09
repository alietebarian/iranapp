<?php

namespace App\Http\Controllers;

use App\Models\Admin;
use App\Libraries\jdf;
use App\Models\Setting;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;

class AdminController extends Controller
{

    public function showDashboard(Request $request){
        $currentYear = (int)jdf::jdate('Y' , time() , '' , 'Asia/Tehran' , 'en');
        $startOfYearGregDateArr = jdf::jalali_to_gregorian($currentYear , 1 , 1);
        $monthNames = [
            1 => 'فروردین',
            2 => 'اردیبهشت',
            3 => 'خرداد',
            4 => 'تیر',
            5 => 'مرداد',
            6 => 'شهریور',
            7 => 'مهر',
            8 => 'آبان',
            9 => 'آذر',
            10 => 'دی',
            11 => 'بهمن',
            12 => 'اسفند'
        ];
        $adsCountArr = [];
        $initialDate = Carbon::create($startOfYearGregDateArr[0] , $startOfYearGregDateArr[1] , $startOfYearGregDateArr[2]);
        for($i = 1 ; $i <= 12 ; $i++){
            $monthStartDate = $initialDate->toDateString();
            $monthEndDate = $initialDate->addMonth(1)->toDateString();
            $adsCount = DB::table('ads')
                ->where('status' , '=' , 'approved')
                ->where('created_at' , '>=' , $monthStartDate)
                ->where('created_at' , '<' , $monthEndDate)
                ->count();
            $adsCountArr[$i]['count'] = $adsCount;
            $adsCountArr[$i]['monthName'] = $monthNames[$i];

        }
        $data['adsChart'] = $adsCountArr;

        $discountAdsCount = DB::table('ads')
            ->where('status' , '=' , 'approved')
            ->where('type' , '=' , 'discount')
            ->count();
        $needAdsCount = DB::table('ads')
            ->where('status' , '=' , 'approved')
            ->where('type' , '=' , 'need')
            ->count();
        $employsResumeAdsCount = DB::table('employs_ads')
            ->where('type' , '=' , 'karjoo')
            ->where('status' , '=' , 'approved')
            ->count();
        $employsOpportunitiesAdsCount = DB::table('employs_ads')
            ->where('type' , '=' , 'forsatshoghli')
            ->where('status' , '=' , 'approved')
            ->count();
        $vehicleAdsCount = DB::table('vehicles_ads')
            ->where('status' , '=' , 'approved')
            ->count();
        $estateAdsCount = DB::table('estates_ads')
            ->where('status' , '=' , 'approved')
            ->count();
        $allAdsCount = $discountAdsCount + $needAdsCount;
        // PHP 8 raises DivisionByZeroError where PHP 7 only warned, so guard the
        // empty case instead of letting an ad-less database break the dashboard.
        $discountPercentage = $allAdsCount > 0 ? $discountAdsCount * 100 / $allAdsCount : 0;
        $needsPercentage = $allAdsCount > 0 ? $needAdsCount * 100 / $allAdsCount : 0;
        $data['discountPercentage'] = round($discountPercentage);
        $data['needsPercentage'] = round($needsPercentage);
        $data['needsAdsCount'] = $needAdsCount;
        $data['discountAdsCount'] = $discountAdsCount;
        $data['allAdsCount'] = $allAdsCount;
        $data['ResumeAdsCount'] = $employsResumeAdsCount;
        $data['OppurtunitiesAdsCount'] = $employsOpportunitiesAdsCount;
        $data['estateAdsCount'] = $estateAdsCount;
        $data['vehicleAdsCount'] = $vehicleAdsCount;
        $vipAdsCount = DB::table('ads')
            ->join('vip_ads' , 'vip_ads.ads_id' , '=' , 'ads.id')
            ->where('ads.status' , '=' , 'approved')
            ->count();
        $data['vipAdsCount'] = $vipAdsCount;
        $vipAdsPercentage = $vipAdsCount / 100 * $allAdsCount;
        $data['vipAdsPercentage'] = $vipAdsPercentage;
        $lastWeekRegisteredUsers = DB::table('users')
            ->where('created_at' , '<=' , Carbon::now()->addDays(1)->toDateString())
            ->where('created_at' , '>=' , Carbon::now()->addWeek(-1)->toDateString())
            ->orderBy('created_at' , 'desc')
            ->get();
        $data['lastWeekRegisteredUsers'] = $lastWeekRegisteredUsers;
        return view('admin.dashboard')->with($data);
    }

    public function showUpdatePage(Request $request){
        $data['admin'] = Auth::guard('admin')->user();
        return view('admin.my_account')->with($data);
    }
    public function update(Request $request){
        $admin = Auth::guard('admin')->user();
        $admin->first_name = $request->first_name;
        $admin->last_name = $request->last_name;
        $admin->save();
        $msg = new \stdClass();
        $msg->title = 'ویرایش موفقیت آمیز';
        $msg->msg = 'اطلاعات حساب کاربری شما با موفقیت ویرایش شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }

    public function showChangePasswordForm(Request $request){
        $data['admin'] = Auth::guard('admin')->user();
        return view('admin.change_password')->with($data);
    }

    public function updatePassword(Request $request){
        $this->validate($request , [
            'password' => 'required|confirmed'
        ] , [
            'password.required' => 'وارد کردن رمز عبور الزامی است.',
            'password.confirmed' => 'رمز های عبور وارد شده مطابقت ندارند'
        ]);
        $admin = Auth::guard('admin')->user();
        $admin->password = Hash::make($request->password);
        $admin->save();

        $msg = new \stdClass();
        $msg->title = 'ویرایش موفقیت آمیز';
        $msg->msg = 'رمز شما با موفقیت تغییر یافت.';
        return redirect()->back()->with('success_msg' , $msg);
    }

    public function logout(Request $request){
        Auth::guard('admin')->logout();

        $msg = new \stdClass();
        $msg->title = 'خروج از حساب کاربری';
        $msg->msg = 'شما با موفقیت از حساب کاربری خود خارج شدید.';
        return redirect()->route('showAdminLoginForm')->with('success_msg' , $msg);
    }

    public function smsPanelInformationShow(Request $request){
        $userName = Setting::where('setting_key' , 'payamak_service_user_name')->first();
        if($userName){
            $userName = $userName->setting_value;
        }else{
            $userName = null;
        }
        $password = Setting::where('setting_key' , 'payamak_service_password')->first();
        if($password){
            $password = $password->setting_value;
        }else{
            $password = null;
        }
        return view('admin.sms_panel_information')->with([
            'user_name' => $userName,
            'password' => $password
        ]);
    }

    public function smsPanelInformationUpdate(Request $request){
        $userName = $request->user_name;
        $password = $request->password;
        $userNameExists = Setting::where('setting_key' , 'payamak_service_user_name')->first();
        if(!$userNameExists){
            $new = new Setting();
            $new->setting_key = 'payamak_service_user_name';
            $new->setting_value = $userName;
            $new->save();
        }else{
            $userNameExists->setting_value = $userName;
            $userNameExists->save();
        }

        $passwordExists = Setting::where('setting_key' , 'payamak_service_password')->first();
        if(!$passwordExists){
            $new = new Setting();
            $new->setting_key = 'payamak_service_password';
            $new->setting_value = $password;
            $new->save();
        }else{
            $passwordExists->setting_value = $password;
            $passwordExists->save();
        }
        $msg = new \stdClass();
        $msg->title = 'به روز رسانی نام کاربری و رمز عبور';
        $msg->msg = 'نام کاربری و رمز عبور پنل پیامک به روز رسانی شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }
}
