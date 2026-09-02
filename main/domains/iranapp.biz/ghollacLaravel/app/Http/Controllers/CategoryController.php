<?php

namespace App\Http\Controllers;

use App\Category;
use App\VipAds;
use Illuminate\Database\QueryException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\URL;

class CategoryController extends Controller
{
    public function showAllWithRandomMainPageVipAd(Request $request){
        $vipAdObj = new VipAds();
        $vip = $vipAdObj->selectFields(VipAds::FIELDS)
            ->getApproved()
            ->getQuery()
            ->where('vip_ads.show_in_main_page' , '=' , 1)
            ->orderBy(DB::raw('RAND()'))
            ->first();
        $vip = VipAds::outputJson($vip);
        $categories = DB::table('category')
            ->select('*');
        if($request->has('ads_type') && in_array($request->ads_type , ['discount' , 'need'])){
            if($request->ads_type == 'discount'){
                $categories = $categories->whereRaw('(
            select count(*) from ads where ads.status = "approved" and  ads.type = "discount" and  ads.sub_category_id in (
                  select sub_category.id from sub_category where sub_category.category_id = category.id
            )
            ) > 0');
            }else{
                $categories = $categories->whereRaw('(
            select count(*) from ads where ads.status = "approved" and ads.type = "need" and ads.sub_category_id in (
                  select sub_category.id from sub_category where sub_category.category_id = category.id
            )
            ) > 0');
            }
        }else{
            $categories = $categories->whereRaw('(
            select count(*) from ads where ads.status = "approved" and  ads.sub_category_id in (
                  select sub_category.id from sub_category where sub_category.category_id = category.id
            )
            ) > 0');
        }
        $categories = $categories->get();
        $data = [
            'categories' => $categories,
            'vip_ad' => $vip
        ];
        return response()->json(['status' => 200 , 'data' => $data]);
    }

    public function getAll(Request $request){
        $categories = Category::all();
        $categories->each(function($row , $index)use($categories){
            if($row->icon){
                $categories[$index]->icon = URL::to('/icon') . '/' . $row->icon;

            }
        });
        return response()->json(['status' => 200 , 'list' => $categories]);
    }

    public function getCategoryWithAdsInCity(Request $request , $cityId){
        $visibleCategories = DB::table('ads')
            ->join('sub_category' , 'sub_category.id' , '=' , 'ads.sub_category_id')
            ->join('category' , 'category.id' , '=' , 'sub_category.category_id' )
            ->where('ads.valid_since' , '<=' , date('Y-m-d'))
            ->where('valid_until' , '>=' , date('Y-m-d') )
            ->where('status' , '=' , 'approved')
            ->where('ads.city_id' , '=' , $cityId)
            ->distinct()
            ->pluck('category.id');
        $list = DB::table('category')->select('*')
            ->orderBy('category.ordering_factor' , 'asc')
            ->whereIn('category.id' ,  $visibleCategories)
            ->get();
        $list = collect($list);
        $list->each(function($row , $index) use($list){
            if($row->icon){
                $list[$index]->icon = URL::to('/icon') . '/' . $row->icon;
            }
        });
        return response()->json(['status' => 200 , 'data' => ['categories' => $list]]);
    }

    public function showAllInAdmin(Request $request){
        $categories = DB::table('category')->orderBy('ordering_factor')->get();
        $data['list'] = $categories;
        return view('admin.categories')->with($data);
    }

    public function saveNewInAdmin(Request $request){
        $this->validate($request , [
            'name' => 'required',
            'icon' => 'nullable|image|max:1024'
        ] , [
            'name.required' => 'نام دسته الزامی است.',
            'icon.image' => 'تصویر بندانگشتی نامعتبر است.',
            'icon.max' => 'تصویر بندانگشتی حداکثر می تواند 1 مگابایت باشد.'
        ]);
        $category = new Category();
        $category->name = $request->name;
        if($request->hasFile('icon')){
            $imgName = uniqid() . '.' . $request->icon->getClientOriginalExtension();
            $request->icon->move(public_path('icon') , $imgName);
            $category->icon = $imgName;
        }
        $category->save();
        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز';
        $msg->msg = 'دسته با موفقیت ثبت شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }

    public function delete(Request $request , Category $category){
        try{
            $category->delete();
        }catch (QueryException $exception){
            if($exception->errorInfo[1] == 1451){
                $msg = new \stdClass();
                $msg->title = 'خطا';
                $msg->msg = 'امکان حذف دسته به دلیل وجود رکوردهای مرتبط وجود ندارد.';

                return redirect()->back()->with('error_msg' , $msg);
            }
        }
        $msg = new \stdClass();
        $msg->title =  'حذف موفقیت آمیز';
        $msg->msg = 'دسته مورد نظر حذف شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }

    public function updateByIdInAdminPanel(Request $request , Category $category){
        $this->validate($request , [
            'name' => 'required|string|max:200',
            'icon' => 'nullable|image|max:1024'
        ] , [
            'name.required' => 'نام دسته الزامی است.',
            'name.max' => 'نام دسته طولانی تر از حد مجاز است.',
            'icon.image' => 'تصویر بندانگشتی نامعتبر است.',
            'icon.max' => 'حجم تصویر حداکثر می تواند 1 مگابایت باشد.'
        ]);
        $category->name = $request->name;
        if($request->hasFile('icon')){
            $imgName = uniqid() . '.' . $request->icon->getClientOriginalExtension();
            $request->icon->move(public_path('icon') , $imgName);
            $category->icon = $imgName;
        }
        $category->save();

        $msg = new \stdClass();
        $msg->title = 'ویرایش موفقیت آمیز';
        $msg->msg = 'دسته مورد نظر با موفقیت ویرایش شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }

    public function updateOrders(Request $request){
        if($request->has('id') && $request->has('ordering_factor')){
            foreach($request->id as $index => $value){
                $cat = Category::find($value);
                $cat->ordering_factor = $request->ordering_factor[$index];
                $cat->save();
            }
        }
        return response()->json(['status' => 204]);
    }
}
