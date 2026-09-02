<?php

namespace App\Http\Controllers;

use App\City;
use App\Province;
use Illuminate\Database\QueryException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Validator;

class CityController extends Controller
{
    public function showByProvinceId(Request $request , $provinceId){
        $cities = DB::table('city')->where('province_id' , '=' , $provinceId)->orderBy('name' , 'asc')->get();
        return response()->json(['status' => 200 , 'list' => $cities]);
    }

    public function showAllInAdminPanel(Request $request , Province $province){
        $cities = DB::table('city')->where('province_id' , $province->id)->orderBy('name' , 'asc')->paginate(15);
        $data['cities'] = $cities;
        $data['province'] = $province;
        return view('admin.cities')->with($data);
    }

    public function save(Request $request , Province $province){
        $validator = Validator::make($request->all() , [
            'name' => 'required|string|max:200'
        ] , [
            'name.required' => 'نام شهر الزامی است.',
            'name.max' => 'نام شهر طولانی تر از حد مجاز است.'
        ]);
        if($validator->fails()){
            $msg = new \stdClass();
            $msg->title = 'خطا';
            $msg->msg = implode($validator->errors()->all() , '\r\n');
            return redirect()->back()->with('error_msg' , $msg)
                ->with('validation_error' , true);
        }
        $city = new City();
        $city->province_id = $province->id;
        $city->name = $request->name;
        $success = $city->save();
        if($success){
            $msg = new \stdClass();
            $msg->title = 'ثبت موفقیت آمیز';
            $msg->msg = 'شهر مورد نظر با موفقیت ثبت شد.';
            return redirect()->back()->with('success_msg' , $msg);
        }else{
            $msg = new \stdClass();
            $msg->title = 'خطا';
            $msg->msg = 'خطایی در هنگام ثبت اطلاعات رخ داده است. لطفا مجددا تلاش کنید.';
            return redirect()->back()->with('success_msg' , $msg);
        }

    }

    public function update(Request $request , City $city){
        $validator = Validator::make($request->all() , [
            'name' => 'required|string|max:200'
        ] , [
            'name.required' => 'نام شهر الزامی است.',
            'name.max' => 'نام شهر طولانی تر از حد مجاز است.'
        ]);
        if($validator->fails()){
            $msg = new \stdClass();
            $msg->title = 'خطا';
            $msg->msg = implode($validator->errors()->all() , '\r\n');
            return redirect()->back()->with('error_msg' , $msg)
                ->with('validation_error' , true);
        }
        $city->name = $request->name;
        $success = $city->save();


        if($success){
            $msg = new \stdClass();
            $msg->title = 'به روز رسانی موفقیت آمیز';
            $msg->msg = 'شهر مورد نظر با موفقیت به روز رسانی شد.';
            return redirect()->back()->with('success_msg' , $msg);
        }else{
            $msg = new \stdClass();
            $msg->title = 'خطا';
            $msg->msg = 'خطایی در هنگام ثبت اطلاعات رخ داده است. لطفا مجددا تلاش کنید.';
            return redirect()->back()->with('success_msg' , $msg);
        }
    }

    public function delete(Request $request , City $city){
        try{
            $city->delete();
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
        $msg->msg = 'شهر مورد نظر حذف شد.';
        return redirect()->back()->with('success_msg' , $msg);

    }
}
