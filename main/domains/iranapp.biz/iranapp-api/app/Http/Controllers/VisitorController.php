<?php

namespace App\Http\Controllers;

use App\Models\Ads;
use App\Models\AdsPhoto;
use App\Models\AdsPlan;
use App\Models\Category;
use App\Http\Requests\Admin\UpdateVisitorRequest;
use App\Http\Requests\Admin\VisitorRequest;
use App\Models\Province;
use App\Models\User;
use App\Models\Visitor;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\URL;
use Intervention\Image\Facades\Image;

class VisitorController extends Controller
{
    public function showCreatePage()
    {
        return view('admin.visitor_create');
    }

    public function create(VisitorRequest $request)
    {
        $visitor = new Visitor();
        $visitor->first_name = $request->first_name;
        $visitor->last_name = $request->last_name;
        $visitor->mobile = $request->mobile;
        $visitor->password = Hash::make($request->password);
        $visitor->save();

        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز';
        $msg->msg = 'ثبت نام با موفقیت انجام شد.';
        return redirect()->route('showListOfVisitorPage')->with('success_msg', $msg);
    }

    public function showList(Request $request)
    {
        $today = Carbon::today()->toDateString();
        $month = Carbon::now()->addMonths(-1)->toDateString();
        $visitors = DB::table('ads')
            ->rightjoin('visitors' , 'visitors.id' , '=' , 'ads.visitor_id')
            ->select('visitors.first_name' , 'visitors.last_name' , 'visitors.id' , 'visitors.status' , 'visitors.mobile')
            ->addSelect(DB::raw('(
                select count(*) from ads where
                ads.visitor_id = visitors.id
            ) as ads_count'))
            ->addSelect(DB::raw('(
                select count(*) from ads where 
                ads.visitor_id = visitors.id
                and ads.created_at >= "'. $today .'"
            ) as ads_day_count'))
            ->addSelect(DB::raw('(
                select count(*) from ads where 
                ads.visitor_id = visitors.id
                and ads.created_at >= "'. $month .'"
            ) as ads_month_count'))
            ->groupBy('visitors.first_name' , 'visitors.last_name' , 'visitors.id' , 'visitors.status' , 'visitors.mobile');
        if ($request->has('mobile')) {
            $visitors = $visitors->where('visitors.mobile', '=', $request->mobile);
        }
        if ($request->has('full_name')) {
            $visitors = $visitors->whereRaw(' MATCH(first_name , last_name) AGAINST("' . $request->full_name . '" IN NATURAL LANGUAGE MODE)');
        }

        $visitors = $visitors->paginate(15);
        return view('admin.visitors_list' ,compact('visitors'));
    }

    public function updatePage(Visitor $visitor)
    {
        return view('admin.visitor_update' , compact('visitor'));
    }

    public function update(UpdateVisitorRequest $request , Visitor $visitor)
    {
        $visitor->first_name = $request->first_name;
        $visitor->last_name = $request->last_name;
        $visitor->mobile = $request->mobile;
        if ($request->has('password')){
            $visitor->password = Hash::make($request->password);
        }
        $visitor->save();

        $msg = new \stdClass();
        $msg->title = 'ویرایش موفقیت آمیز';
        $msg->msg = 'ویرایش با موفقیت انجام شد.';
        return redirect()->route('showListOfVisitorPage')->with('success_msg', $msg);
    }

    public function showAdsVisitor(Request $request , Visitor $visitor )
    {
        $adsObj = new Ads();
        $ads = $adsObj->selectFields(Ads::FIELDS)
            ->getQuery()
            ->where('ads.visitor_id' , $visitor->id);
        if ($request->has('plan_id') && $request->plan_id != 'all') {
            $ads = $ads->where('ads.ads_plan_id', '=', $request->plan_id);
        }
        if ($request->has('city_id') && $request->city_id != 'all') {
            $ads = $ads->where('ads.city_id', '=', $request->city_id);
        }
        if ($request->has('sub_category_id') && $request->sub_category_id != 'all') {
            $ads = $ads->where('ads.sub_category_id', '=', $request->sub_category_id);
        }
        if ($request->has('status') && $request->status != 'all') {
            $ads = $ads->where('ads.status', '=', $request->status);
        }
        if ($request->has('keyword')) {
            $ads = $ads->whereRaw(' MATCH(ads.title , ads.address , ads.notes) AGAINST("' . $request->keyword . '" IN NATURAL LANGUAGE MODE) ')
                ->addSelect(DB::raw('( MATCH(ads.title , ads.address , ads.notes) AGAINST("' . $request->keyword . '" IN NATURAL LANGUAGE MODE) ) as relevance'))
                ->orderBy('relevance', 'desc');
        } else {
            $ads = $ads->orderBy('ads.created_at', 'desc');
        }
        $ads = $ads->paginate(15)->appends([
            'plan_id' => $request->plan_id,
            'city_id' => $request->city_id,
            'sub_category_id' => $request->sub_category_id,
            'status' => $request->status,
            'keyword' => $request->keyword
        ]);
        $data['ads'] = $ads;
        $data['plans'] = AdsPlan::where('deleted', 0)->get();
        $data['provinces'] = Province::all();
        $data['categories'] = Category::all();

        return view('admin.visitors_ads')->with($data);
    }

    public function changeStatus(Visitor $visitor , Request $request)
    {
        $visitor->status = $request->status;
        $visitor->save();
        return redirect()->back();
    }

    /*///////////////////////////////////////////////////API//////////////////////////////////////////////////////*/

    public function login(Request $request)
    {
        $mobile = $request->mobile;
        $password = $request->password;
        $Visitor = Visitor::where('mobile' , $mobile)->first();
        if($Visitor && Hash::check($password , $Visitor->password)){
            if ($Visitor->status == 'active'){
                $token = $Visitor->createToken('loginToken')->accessToken;
                return response()->json(['token' => $token] , 200);
            }else{
                return response()->json([], 400);
            }
        }else{
            return response()->json([], 401);
        }
        /*if(Auth::guard('visitor_api')->attempt(['mobile' => $mobile, 'password' => $password]))
        {
            $visitor = Auth::guard('visitor')->user();
            if ($visitor->status == 'active')
            {
                $token = $visitor->createToken('MyApp')->accessToken;
                $visitor->token = $token;
                return response()->json($token, 200);
            }
            elseif ($visitor->status == 'deactive')
            {
                return response()->json([] , 401);
            }
        }else{

        }*/
    }

    public function storeAds(Request $request)
    {
        $visitor = Auth::guard('visitor_api')->user();
        if ($visitor->status == 'deactive'){
            return response()->json([] , 400);
        }
        $ads = new Ads();
        $ads->title = $request->title;
        if($request->has('latitude')){
            $ads->latitude = $request->latitude;
        }
        if($request->has('longitude')){
            $ads->longitude = $request->longitude;
        }
        $ads->city_id = $request->city_id;
        $ads->address = $request->address;
        $ads->type = $request->type;
        $ads->sub_category_id = $request->sub_category_id;
        $ads->email = $request->email;
        $ads->mobile = $request->mobile;
        $ads->tel1 = $request->tel1;
        $ads->tel2 = $request->tel2;
        $ads->link = $request->link;
        $ads->discount = $request->discount;
        $ads->working_time = $request->working_time;
        $ads->telegram = $request->telegram;
        $ads->instagram = $request->instagram;
        $ads->notes = $request->notes;
        $ads->status = 'pending';
        $ads->ads_plan_id = $request->ads_plan_id;
        $ads->ads_owner_name = $request->ads_owner_name;

        $plan = AdsPlan::find($request->ads_plan_id);
        $ads->valid_since = Carbon::now()->toDateString();
        $ads->valid_until = Carbon::now()->addDays($plan->interval_days)->toDateString();

        $visitor = Auth::guard('visitor_api')->user();
        $ads->visitor_id = $visitor->id;
        $ads->save();
        $ads->userAds()->attach($request->user_id);

        if ($request->hasFile('photos')) {
            $photos = $request->photos;
            for ($i = 0; $i < $plan->max_number_of_photos; $i++) {
                if (isset($photos[$i])) {
                    $photoItem = $photos[$i];
                    $imgName = uniqid() . '.' . $photoItem->getClientOriginalExtension();
                    $photoItem->move(public_path('ads_photo'), $imgName);

                    $imageToAddWatermark = Image::make(public_path('/ads_photo/') . '/' . $imgName);
                    $imageToAddWatermark->insert(public_path('/ads_photo/watermark2.png'), 'bottom-left', 0, 25);
                    $imageToAddWatermark->save();

                    $photoRow = new AdsPhoto();
                    $photoRow->ads_id = $ads->id;
                    $photoRow->file_name = $imgName;
                    $photoRow->save();
                }
            }
        }
        $adsPhotos = AdsPhoto::where('ads_id', $ads->id)->get();
        $adsPhotos->each(function ($row, $index) use ($adsPhotos) {
            $adsPhotos[$index]->file_name = URL::to('/ads_photo') . '/' . $row->file_name;
        });
        $ads->photos = $adsPhotos;

        return response()->json([] , 200);
    }

    public function showAdsCount()
    {
        $today = Carbon::today()->toDateString();
        $lastMonthDate = Carbon::now()->addMonths(-1)->toDateString();

        $visitor = Auth::guard('visitor_api')->user();
        if ($visitor->status == 'deactive'){
            return response()->json([] , 400);
        }
        $allVisitorAds = Ads::where('visitor_id' , $visitor->id)
            ->count();
        $monthVisitorAds = Ads::where('visitor_id' , $visitor->id)
            ->where('ads.created_at' , '>=' , $lastMonthDate)
            ->count();
        $dayVisitorAds = Ads::where('visitor_id' , $visitor->id)
            ->where('ads.created_at' , '>=' , $today)
            ->count();

        return response()->json(['AllCount' => $allVisitorAds , 'monthCount' => $monthVisitorAds , 'dayCount' => $dayVisitorAds] , 200);
    }

    public function showAds(Request $request)
    {
        $visitor = Auth::guard('visitor_api')->user();
        if ($visitor->status == 'deactive'){
            return response()->json([] , 400);
        }
        $mobile = $request->mobile;
        $user = User::where('mobile' , $mobile)->first();
        if ($user){
            $adsQuery = DB::table('users')
                ->select('users.id' , 'users.first_name' , 'users.last_name')
                ->addSelect(DB::raw('(
                    select count(*) from user_ads
                    where user_ads.user_id = users.id
                ) as adsCount'))
                ->where('users.mobile' , $mobile)
                ->first();
            return response()->json($adsQuery , 200);
        }else{
            return response()->json([] , 404);
        }
    }
}
