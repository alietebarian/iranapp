<?php

namespace App\Http\Controllers;

use App\Ads;
use App\City;
use App\Http\Requests\Admin\saveVipAd;
use App\Http\Requests\Admin\updateVipAds;
use App\VipAds;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class VipAdsController extends Controller
{
    public function getForMainPage(Request $request, $cityId)
    {
        $adsObj = new VipAds();
        $province = City::find($cityId)->province;
        $ad = $adsObj
            ->selectFields(VipAds::FIELDS)
            ->getValidAds()
            ->getApproved()
            ->getQuery()
            ->where('vip_ads.show_in_main_page', '=', 1)
            ->whereRaw('(
            (ads.city_id = ' . $cityId . ' and vip_ads.show_in_city = 1 ) or 
            ( vip_ads.show_in_province = 1 and  (ads.city_id in (select city.id from city where city.province_id = ' . $province->id . ' ) )  ) or 
            (vip_ads.show_in_country = 1)
            )')
            ->orderBy(DB::raw('RAND()'))
            ->first();
        $ad = VipAds::outputJson($ad);
        return response()->json(['status' => 200, 'ad' => $ad]);
    }

    public function getForSubcategoryPage(Request $request, $subcategoryId, $cityId)
    {
        $adsObj = new VipAds();
        $ad = $adsObj
            ->selectFields(VipAds::FIELDS)
            ->getValidAds()
            ->getApproved()
            ->getByCityId($cityId)
            ->getQuery()
            ->where('vip_ads.show_in_subcategory', '=', 1)
            ->where('vip_ads.show_in_main_page', '=', 1)
            ->where('ads.sub_category_id', '=', $subcategoryId)
            ->orderBy(DB::raw('RAND()'))
            ->first();
        $ad = VipAds::outputJson($ad);
        return response()->json(['status' => 200, 'ad' => $ad]);
    }

    public function showCreatePage(Request $request, Ads $ads)
    {
        $data['ad'] = $ads;

        return view('admin.show_vip_ads_save_page')->with($data);
    }

    public function save(saveVipAd $request, Ads $ads)
    {
        $vip = new VipAds();
        $vip->ads_id = $ads->id;
        switch ($request->regional_displaying) {
            case 'country':
                $vip->show_in_country = 1;
                $vip->show_in_province = 0;
                $vip->show_in_city = 0;
                break;
            case 'province':
                $vip->show_in_country = 0;
                $vip->show_in_province = 1;
                $vip->show_in_city = 0;
                break;
            case 'city':
                $vip->show_in_country = 0;
                $vip->show_in_province = 0;
                $vip->show_in_city = 1;
                break;
        }
        switch ($request->category_displaying) {
            case 'category':
                $vip->show_in_category = 1;
                $vip->show_in_subcategory = 0;
                break;
            case 'sub_category':
                $vip->show_in_category = 0;
                $vip->show_in_subcategory = 1;
                break;
            case 'all':
                $vip->show_in_category = 1;
                $vip->show_in_subcategory = 1;
                break;
            case 'without_selection' :
                $vip->show_in_category = 0;
                $vip->show_in_subcategory = 0;
                break;
        }
        switch ($request->show_in_homepage) {
            case 'yes':
                $vip->show_in_main_page = 1;
                break;
            case 'no':
                $vip->show_in_main_page = 0;
                break;
        }
        if ($request->hasFile('photo')) {
            $imgName = uniqid() . '.' .  $request->photo->getClientOriginalExtension();
            $request->photo->move(public_path('vip_ads_photo'), $imgName);
            $vip->photo = $imgName;
        }
        $vip->save();
        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز';
        $msg->msg = 'آگهی ویژه با موفقیت ثبت شد.';
        return redirect()->route('showAdsListInAdminPanel')->with('success_msg', $msg);
    }

    public function showUpdatePage(Request $request, VipAds $vipAds)
    {
        $data['vip'] = $vipAds;
        $data['ad'] = Ads::find($vipAds->ads_id);
        return view('admin.update_vip_ads')->with($data);
    }

    public function updateVipAdInAdminPanel(updateVipAds $request, VipAds $vipAds)
    {
        switch ($request->regional_displaying) {
            case 'country':
                $vipAds->show_in_country = 1;
                $vipAds->show_in_province = 0;
                $vipAds->show_in_city = 0;
                break;
            case 'province':
                $vipAds->show_in_country = 0;
                $vipAds->show_in_province = 1;
                $vipAds->show_in_city = 0;
                break;
            case 'city':
                $vipAds->show_in_country = 0;
                $vipAds->show_in_province = 0;
                $vipAds->show_in_city = 1;
                break;
        }
        switch ($request->category_displaying) {
            case 'category':
                $vipAds->show_in_category = 1;
                $vipAds->show_in_subcategory = 0;
                break;
            case 'sub_category':
                $vipAds->show_in_category = 0;
                $vipAds->show_in_subcategory = 1;
                break;
            case 'all':
                $vipAds->show_in_category = 1;
                $vipAds->show_in_subcategory = 1;
                break;
            case 'without_selection' :
                $vipAds->show_in_category = 0;
                $vipAds->show_in_subcategory = 0;
                break;
        }
        switch ($request->show_in_homepage) {
            case 'yes':
                $vipAds->show_in_main_page = 1;
                break;
            case 'no':
                $vipAds->show_in_main_page = 0;
                break;
        }
        if ($request->hasFile('photo')) {
            $imgName = uniqid() . '.' .  $request->photo->getClientOriginalExtension();
            $request->photo->move(public_path('vip_ads_photo'), $imgName);
            $vipAds->photo = $imgName;
        }
        $vipAds->save();
        $msg = new \stdClass();
        $msg->title = 'ویرایش موفقیت آمیز';
        $msg->msg = 'آگهی ویژه با موفقیت ویرایش شد.';
        return redirect()->route('showAdsListInAdminPanel')->with('success_msg', $msg);
    }

    public function showAllInAdminPanel(Request $request)
    {
        $vipAdsObj = new VipAds();
        $vipAds = $vipAdsObj->selectFields(VipAds::FIELDS)
            ->getQuery()
            ->paginate(15);
        $data['ads'] = $vipAds;
        return view('admin.vip_ads_list')->with($data);
    }

    public function deleteById(Request $request, VipAds $vipAds)
    {
        $vipAds->delete();
        $msg = new \stdClass();
        $msg->title = 'حذف موفقیت آمیز';
        $msg->msg = 'آگهی ویژه با موفقیت حذف شد.';

        return redirect()->back()->with('success_msg', $msg);
    }
}
