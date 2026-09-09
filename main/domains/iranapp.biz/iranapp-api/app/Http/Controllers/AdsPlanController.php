<?php

namespace App\Http\Controllers;

use App\Models\AdsPlan;
use App\Http\Requests\Admin\SaveAdsPlan;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class AdsPlanController extends Controller
{
    public function getAll(Request $request){
        $plans = DB::table('ads_plan')->orderBy('ordering_factor' , 'asc')
            ->where('ads_plan.deleted' , '!=' , 1)
            ->get();

        return response()->json(['status' => 200 , 'list' => $plans]);
    }

    public function showAllInAdminPanel(Request $request){
        $plans = DB::table('ads_plan')
            ->orderBy('ordering_factor' , 'asc')
            ->where('ads_plan.deleted' , '!=' , 1)
            ->get();
        $data['plans'] = $plans;
        return view('admin.ads_plans_list')->with($data);
    }

    public function updatePlansOrder(Request $request){
        if($request->has('plan_id') && $request->has('order')){
            foreach($request->plan_id as $index => $value){
                $plan = AdsPlan::find($value);
                $plan->ordering_factor = $request->order[$index];
                $plan->save();
            }
        }
        return response()->json(['status' => 204]);
    }

    public function save(SaveAdsPlan $request){
        $maxOrderingFactor = DB::table('ads_plan')->max('ordering_factor');
        $plan = new AdsPlan();
        $plan->num_of_stars = $request->stars;
        $plan->max_number_of_photos = $request->max_photo;
        $plan->price = $request->price;
        $plan->plan_title = $request->name;
        $plan->num_of_updates = $request->updates_count;
        $plan->interval_days = $request->interval_days;
        $plan->ordering_factor = $maxOrderingFactor + 1;
        $plan->save();

        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز';
        $msg->msg = 'پلن مورد نظر با موفقیت ثبت شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }

    public function update(SaveAdsPlan $request , AdsPlan $adsPlan){
        $adsPlan->num_of_stars = $request->stars;
        $adsPlan->max_number_of_photos = $request->max_photo;
        $adsPlan->price = $request->price;
        $adsPlan->plan_title = $request->name;
        $adsPlan->num_of_updates = $request->updates_count;
        $adsPlan->interval_days = $request->interval_days;
        $adsPlan->save();

        $msg = new \stdClass();
        $msg->title = 'ویرایش موفقیت آمیز';
        $msg->msg = 'پلن مورد نظر با موفقیت ویرایش شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }

    public function deleteById(Request $request , AdsPlan $adsPlan){
        $adsPlan->deleted = 1;
        $adsPlan->save();
        $msg = new \stdClass();
        $msg->title = 'حذف موفقیت آمیز';
        $msg->msg = 'پلن مورد نظر با موفقیت حذف شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }
}
