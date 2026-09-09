<?php

namespace App\Http\Controllers;

use App\Models\Brand;
use Illuminate\Database\QueryException;
use Illuminate\Http\Request;

class BrandController extends Controller
{
    public function showAllJson(Request $request){
        $brands = Brand::orderBy('name' , 'asc')->get();
        return response()->json(['status' => 200 , 'list' => $brands]);
    }

    public function showAllInAdminPanel(Request $request){
        $data['brands'] = Brand::orderBy('name' , 'asc')->paginate(15);
        return view('admin.vehicles.brands')->with($data);
    }

    public function save(Request $request){
        $brand = new Brand();
        $brand->name = $request->name;
        $brand->save();

        $msg = new \stdClass();
        $msg->title = 'ثبت برند';
        $msg->msg = 'برند با موفقیت ثبت شد.';

        return redirect()->back()->with('success_msg' , $msg);
    }

    public function delete(Request $request , Brand $brand){
        try{
            $brand->delete();
        }catch (QueryException $exception){
            if($exception->errorInfo[1] == 1451){
                $msg = new \stdClass();
                $msg->title = 'خطا';
                $msg->msg = 'امکان حذف برند به دلیل وجود رکوردهای مرتبط وجود ندارد.';

                return redirect()->back()->with('error_msg' , $msg);
            }
        }
        $msg = new \stdClass();
        $msg->title = 'حذف برند';
        $msg->msg = 'برند با موفقیت حذف شد.';

        return redirect()->back()->with('success_msg' , $msg);
    }

    public function update(Request $request , Brand $brand){
        $brand->name = $request->name;
        $brand->save();

        $msg = new \stdClass();
        $msg->title = 'ویرایش برند';
        $msg->msg = 'برند با موفقیت ویرایش شد.';

        return redirect()->back()->with('success_msg' , $msg);
    }
}
