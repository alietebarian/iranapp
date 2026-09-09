<?php

namespace App\Http\Controllers;

use App\Models\City;
use App\Http\Requests\Admin\Regions\Save;
use App\Http\Requests\Admin\Regions\Update;
use App\Models\Region;
use Illuminate\Database\QueryException;
use Illuminate\Http\Request;

class RegionController extends Controller
{
    public function getByCityId(Request $request , City $city){
        $regions = $city->region()->orderBy('name' , 'asc')->get();
        return response()->json(['status' => 200 , 'list' => $regions]);
    }

    public function showInAdminPanel(Request $request , City $city){
        $list = $city->region()->orderBy('name' , 'asc')->paginate(15);
        $data['list'] = $list;
        $data['city'] = $city;
        return view('admin.regions')->with($data);
    }

    public function saveInAdminPanel(Save $request , City $city){
        $city->region()->create([
            'name' => $request->name
        ]);
        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز';
        $msg->msg = 'منطقه با موفقیت ثبت شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }

    public function deleteRegion(Request $request , Region $region){
        try{
            $region->delete();
        }catch (QueryException $exception){
            if($exception->errorInfo[1] == 1451){
                $msg = new \stdClass();
                $msg->title = 'خطا';
                $msg->msg = 'امکان حذف شهر به دلیل وجود رکوردهای مرتبط وجود ندارد.';
                return redirect()->back()->with('error_msg' , $msg);
            }
        }
        $msg = new \stdClass();
        $msg->title = 'حذف موفقیت آمیز';
        $msg->msg = 'منطقه با موفقیت حذف شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }

    public function updateInAdminPanel(Update $request , Region $region){
        $region->name = $request->name;
        $region->save();

        $msg = new \stdClass();
        $msg->title = 'به روز رسانی موفقیت آمیز';
        $msg->msg = 'منطقه با موفقیت به روز رسانی شد.';

        return redirect()->back()->with('success_msg' , $msg);
    }
}
