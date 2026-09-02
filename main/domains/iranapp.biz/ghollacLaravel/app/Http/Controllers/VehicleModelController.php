<?php

namespace App\Http\Controllers;

use App\Brand;
use App\CarModel;
use Illuminate\Database\QueryException;
use Illuminate\Http\Request;

class VehicleModelController extends Controller
{
    public function getByBrandId(Request $request , Brand $brand){
        $models = $brand->models()->orderBy('name' , 'asc')->get();
        return response()->json(['status' => 200 , 'list' => $models]);
    }

    public function showAllInAdmin(Request $request , Brand $brand){
        $data['brand'] = $brand;
        $data['list'] = $brand->models()->orderBy('name' , 'asc')->paginate(15);
        return view('admin.vehicles.model')->with($data);
    }

    public function save(Request $request , Brand $brand){
        $brand->models()->create([
            'name' => $request->name
        ]);
        $msg = new \stdClass();
        $msg->title = 'ثبت مدل';
        $msg->msg = 'مدل با موفقیت ثبت شد.';

        return redirect()->back()->with('success_msg' , $msg);
    }

    public function update(Request $request ,  CarModel $model){
        $model->name = $request->name;
        $model->save();

        $msg = new \stdClass();
        $msg->title = 'ویرایش مدل';
        $msg->msg = 'مدل با موفقیت ویرایش شد.';

        return redirect()->back()->with('success_msg' , $msg);
    }

    public function delete(Request $request , CarModel $model){
        try{
            $model->delete();
        }catch (QueryException $exception){
            if($exception->errorInfo[1] == 1451){
                $msg = new \stdClass();
                $msg->title = 'خطا';
                $msg->msg = 'امکان حذف مدل به دلیل وجود رکوردهای مرتبط وجود ندارد.';

                return redirect()->back()->with('error_msg' , $msg);
            }
        }
        $msg = new \stdClass();
        $msg->title = 'حذف برند';
        $msg->msg = 'مدل با موفقیت حذف شد.';

        return redirect()->back()->with('success_msg' , $msg);
    }
}
