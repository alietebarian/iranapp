<?php

namespace App\Http\Controllers;

use App\Category;
use App\City;
use App\SubCategory;
use App\VipAds;
use Carbon\Carbon;
use Illuminate\Database\QueryException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\URL;

class SubCategoryController extends Controller
{
    public function getByCategoryId(Request $request , $categoryId){
        $subcategories = DB::table('sub_category')->whereRaw('(
        (select count(*) from ads where ads.city_id = '. $request->city_id .' and ads.sub_category_id = sub_category.id and ads.status = "approved" and ads.valid_since <= "'. Carbon::now()->toDateString() .'" and ads.valid_until >= "'. Carbon::now()->toDateString() .'")
        )')->orderBy('sub_category.ordering_factor' , 'asc')->where('sub_category.category_id' , '=' , $categoryId)->get();
        $subcategories->each(function($row , $item) use($subcategories){
            if($row->icon){
                $subcategories[$item]->icon = URL::to('/icon') . '/' . $row->icon;
            }
        });
        $cityId = $request->city_id;
        $province = City::find($cityId)->province;
        $vipAdsObj = new VipAds();
        $vipAds = $vipAdsObj->selectFields(VipAds::FIELDS)
//            ->getAdsInSpecificCityAndSubCategory($cityId , $categoryId)
            ->getValidAds()
            ->getQuery()
            ->whereRaw('((
            (vip_ads.show_in_country = 1) or 
            (vip_ads.show_in_city = 1 and ads.city_id = '. $cityId .') or 
            (vip_ads.show_in_province = 1 and ads.city_id in ( select city.id from city where province_id = '. $province->id .' )  )
            ) and (
            (vip_ads.show_in_category = 1 and ads.sub_category_id in ( select id from sub_category where sub_category.category_id = '. $categoryId .' )  )
            ))')
            ->orderBy(DB::raw('RAND()'))
            ->first();
        $vipAds = VipAds::outputJson($vipAds);
        return response()->json(['status' => 200 , 'list' => $subcategories , 'vip_ad' => $vipAds]);
    }

    public function getByCategoryId2(Request $request , $categoryId){
        /*$subCategories = DB::table('sub_category')
            ->whereRaw('sub_category.id in (
                select distinct ads.sub_category_id from ads where ads.city_id = '. $request->city_id .'
                 and ads.valid_since <= "'. date('Y-m-d') .'" and ads.valid_until >= "'. date('Y-m-d') .'"
                 and ads.status = "approved"
            )')
            ->select(DB::raw('*'))
            ->get();*/
        $subCategories =  SubCategory::where('category_id' , $categoryId)->get();
        $subCategories->each(function($row , $index) use($subCategories){
            if($row->icon){
                $subCategories[$index]->icon = URL::to('/icon') . '/' . $row->icon;
            }
        });
        return response()->json(['status' => 200 , 'list' => SubCategory::where('category_id' , $categoryId)->get()]);
    }

    public function showAllInAdminPanel(Request $request , Category $category){
        $data['category'] = $category;
        $data['sub_categories'] = DB::table('sub_category')->where('category_id' , '=' , $category->id)->paginate(15);

        return view('admin.sub_categories')->with($data);
    }

    public function save(Request $request , Category $category){
        $this->validate($request , [
            'name' => 'required|string|max:200',
            'icon' => 'nullable|image|max:1024'
        ] , [
            'name.required' => 'نام دسته الزامی است.',
            'name.max' => 'نام دسته طولانی تر از حد مجاز است.',
            'icon.image' => 'تصویر بندانگشتی نامعتبر است.',
            'icon.max' => 'حجم تصویر حداکثر می تواند 1 مگابایت باشد.'
        ]);
        $subCategory = new SubCategory();
        $subCategory->category_id = $category->id;
        $subCategory->name = $request->name;
        if($request->hasFile('icon')){
            $imgName = uniqid() . '.' . $request->icon->getClientOriginalExtension();
            $request->icon->move(public_path('icon') , $imgName);
            $subCategory->icon = $imgName;
        }
        $subCategory->save();
        $msg = new \stdClass();
        $msg->title = 'ثبت موفقیت آمیز';
        $msg->msg = 'زیر دسته مورد نظر با موفقیت ثبت شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }

    public function delete(Request $request , SubCategory $subCategory){
        try{
            $subCategory->delete();
        }catch (QueryException $exception){
            if($exception->errorInfo[1] == 1451){
                $msg = new \stdClass();
                $msg->title = 'خطا';
                $msg->msg = 'امکان حذف زیر دسته به دلیل وجود رکوردهای مرتبط وجود ندارد.';
                return redirect()->back()->with('error_msg' , $msg);
            }
        }
        $msg = new \stdClass();
        $msg->title = 'حذف موفقیت آمیز';
        $msg->msg = 'زیر دسته مورد نظر حذف شد.';
        return redirect()->back()->with('success_msg' , $msg);

    }

    public function updateOrders(Request $request){
        if($request->has('id') && $request->has('ordering_factor')){
            foreach($request->id as $index => $value){
                $cat = SubCategory::find($value);
                $cat->ordering_factor = $request->ordering_factor[$index];
                $cat->save();
            }
        }
        return response()->json(['status' => 204]);
    }

    public function update(Request $request , SubCategory $subCategory){
        $this->validate($request , [
            'name' => 'required|string|max:200',
            'icon' => 'nullable|image'
        ] , [
            'name.required' => 'نام زیر دسته الزامی است.',
            'name.max' => 'نام زیر دسته طولانی تر از حد مجاز است.',
            'icon.image' => 'آیکون نامعتبر است.'
        ]);
        $subCategory->name = $request->name;
        if($request->hasFile('icon')){
            $imgName = uniqid() . '.' . $request->icon->getClientOriginalExtension();
            $request->icon->move(public_path('/icon') , $imgName);
            $subCategory->icon = $imgName;
        }
        $subCategory->save();

        $msg = new \stdClass();
        $msg->title = 'ویرایش موفقیت آمیز';
        $msg->msg = 'زیر دسته با موفقیت ویرایش شد.';
        return redirect()->back()->with('success_msg' , $msg);
    }
}
