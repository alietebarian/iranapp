<?php

namespace App\Http\Controllers;

use App\Libraries\PaginationTrait;
use App\Models\Province;
use Illuminate\Database\QueryException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Validator;

class ProvinceController extends Controller
{
    use PaginationTrait;
    public function showAll(Request $request)
    {
        $provinces = DB::table('province')->orderBy('name' , 'asc')->get();
        return response()->json(['status' => 200, 'list' => $provinces]);
    }

    public function showAllInAdminPanel(Request $request)
    {
        $provinces = DB::table('province')->orderBy('name' , 'asc')->paginate($this->rowsCount1);
        $data['provinces'] = $provinces;
        return view('admin.provinces')->with($data);
    }

    public function update(Request $request, Province $province)
    {
        $validator = Validator::make($request->all(), [
            'name' => 'required|string|max:100'
        ], [
            'name.required' => 'نام استان الزامی است.',
            'name.max' => 'نام استان طولانی تر از حد مجاز است.'
        ]);
        if ($validator->fails()) {
            $msg = new \stdClass();
            $msg->title = 'خطا';
            $msg->msg = implode($validator->errors()->all(), '\r\n');
            return redirect()->back()->with('error_msg', $msg)
                ->with('validation_error' , true);
        }
        $province->name = $request->name;
        $success  = $province->save();
        if($success){
            $msg = new \stdClass();
            $msg->title = 'به روز رسانی موفقیت آمیز';
            $msg->msg = 'استان مورد نظر با موفقیت به روز رسانی شد.';
            return redirect()->back()->with('success_msg', $msg);
        }else{
            $msg = new \stdClass();
            $msg->title = 'خطا';
            $msg->msg = 'در ثبت اطلاعات خطایی رخ داده است. لطفا مجددا تلاش کنید.';
            return redirect()->back()->with('error_msg', $msg);
        }

    }

    public function delete(Request $request, Province $province)
    {
        try {
            $province->delete();
        } catch (QueryException $exception) {
            if ($exception->errorInfo[1] == 1451) {
                $msg = new \stdClass();
                $msg->title = 'خطا';
                $msg->msg = 'امکان حذف استان به دلیل وجود رکوردهای مرتبط وجود ندارد. ';
                return redirect()->back()->with('error_msg', $msg);
            }
        }
        $msg = new \stdClass();
        $msg->title = 'حذف موفقیت آمیز';
        $msg->msg = 'استان مورد نظر با حذف شد.';
        return redirect()->back()->with('success_msg', $msg);
    }

    public function save(Request $request){
        $validator = Validator::make($request->all(), [
            'name' => 'required|string|max:100'
        ], [
            'name.required' => 'نام استان الزامی است.',
            'name.max' => 'نام استان طولانی تر از حد مجاز است.'
        ]);
        if($validator->fails()){
            $msg = new \stdClass();
            $msg->title = 'خطا';
            $msg->msg = implode($validator->errors()->all() , '\r\n');
            return redirect()->back()->with('error_msg', $msg)
                ->with('validation_error' , true);
        }
        $province = new Province();
        $province->name = $request->name;
        $success = $province->save();
        if($success){
            $msg = new \stdClass();
            $msg->title = 'ثبت موفقیت آمیز';
            $msg->msg = 'استان مورد نظر با موفقیت ثبت شد.';
            return redirect()->back()->with('success_msg', $msg);
        }else{
            $msg = new \stdClass();
            $msg->title = 'خطا';
            $msg->msg = 'در ثبت اطلاعات خطایی رخ داده است. لطفا مجددا تلاش کنید.';
            return redirect()->back()->with('error_msg', $msg);
        }


    }
}
