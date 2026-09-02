<?php

namespace App\Http\Controllers;

use App\CylinderVolumes;
use App\Http\Requests\Admin\CylinderVolumes\Save;
use Illuminate\Http\Request;

class CylinderVolumesController extends Controller
{
    public function showJson(Request $request){
        $volumes = CylinderVolumes::all();

        return response()->json(['status' => 200 , 'list' => $volumes]);
    }

    public function showAllInAdminPanel(Request $request){
        $list = CylinderVolumes::paginate(15);
        $data['list'] = $list;

        return view('admin.cylinder_volumes')->with($data);
    }

    public function saveInAdminPanel(Save $request){
        $volume = new CylinderVolumes();
        $volume->value = $request->value;
        $volume->save();

        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز';
        $msg->msg = 'حجم موتور با موفقیت ثبت شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }

    public function update(Save $request , CylinderVolumes $volume){
        $volume->value = $request->value;
        $volume->save();

        $msg = new \stdClass();
        $msg->title = 'ویرایش موفقیت آمیز';
        $msg->msg = 'حجم موتور با موفقیت ویرایش شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }

    public function delete(Request $request , CylinderVolumes $volume){
        $volume->delete();

        $msg = new \stdClass();
        $msg->title = 'حذف موفقیت آمیز';
        $msg->msg = 'حجم موتور با موفقیت حذف شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }
}
