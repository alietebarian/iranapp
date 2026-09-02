<?php

namespace App\Http\Controllers;

use App\EmploysAdsSpecialty;
use App\Http\Requests\Admin\Specialities\Save;
use Illuminate\Database\QueryException;
use Illuminate\Http\Request;

class EmploysAdsSpecialityController extends Controller
{
    public function showJson(Request $request){
        $list = EmploysAdsSpecialty::all();

        return response()->json(['status' => 200 , 'list' => $list]);
    }

    public function showAllInAdminPanel(Request $request){
        $list = EmploysAdsSpecialty::paginate(15);
        $data['list'] = $list;
        return view('admin.specialities')->with($data);
    }

    public function saveInAdminPanel(Save $request){
        $speciality = new EmploysAdsSpecialty();
        $speciality->name = $request->name;
        $speciality->save();

        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز';
        $msg->msg = 'تخصص با موفقیت ذخیره شد.';

        return redirect()->back()->with('success_msg' , $msg);
    }

    public function delete(Request $request , EmploysAdsSpecialty $specialty){
        try{
            $specialty->delete();
        }catch (QueryException $exception){
            if($exception->errorInfo[1] == 1451){
                $msg = new \stdClass();
                $msg->title = 'خطا';
                $msg->msg = 'امکان حذف تخصص به دلیل وجود رکوردهای مرتبط وجود ندارد.';

                return redirect()->back()->with('error_msg' , $msg);
            }
        }

        $msg = new \stdClass();
        $msg->title = 'حذف موفقیت آمیز';
        $msg->msg = 'تخصص با موفقیت حذف شد.';

        return redirect()->back()->with('success_msg' , $msg);
    }

    public function updateInAdminPanel(Save $request , EmploysAdsSpecialty $speciality ){
        $speciality->name = $request->name;
        $speciality->save();

        $msg = new \stdClass();
        $msg->title = 'ویرایش موفقیت آمیز';
        $msg->msg = 'تخصص با موفقیت ویرایش شد.';

        return redirect()->back()->with('success_msg' , $msg);
    }
}
