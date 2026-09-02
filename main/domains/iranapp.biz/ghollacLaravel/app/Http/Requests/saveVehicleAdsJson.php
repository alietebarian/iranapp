<?php

namespace App\Http\Requests;

use App\Brand;
use Illuminate\Contracts\Validation\Validator;
use Illuminate\Foundation\Http\FormRequest;
use Tymon\JWTAuth\Facades\JWTAuth;

class saveVehicleAdsJson extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     *
     * @return bool
     */
    public function authorize()
    {
        $user = JWTAuth::parseToken()->authenticate();
        if($user){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Get the validation rules that apply to the request.
     *
     * @return array
     */
    public function rules()
    {
        return [
            'ads_title' => 'required|string|max:200',
            'region_id' => 'required|numeric|exists:region,id',
            'thumbnail_photo' => 'nullable|image',
            'address' => 'required|string',
            'telephone1' => 'nullable|string|max:200',
            'telephone2' => 'nullable|string|max:200',
            'ads_owner_name' => 'nullable|string|max:200',
            'description' => 'nullable|string',
            'latitude' => 'nullable|numeric',
            'longitude' => 'nullable|numeric',
            'type' => 'required|string|in:khodro,motorcycle,khodroclasic,khordrosorn,lavazem,other',
            'brand' => 'nullable|numeric|exists:brand,id',
            'model' => 'nullable|numeric|exists:model,id',
            'price' => 'nullable',
            'chassis_type' => 'nullable|string|in:savari,hachback,shasiboland,vanet,krook,van,cupe,station,other',
            'cylinder_volume' => 'nullable|numeric|exists:vehicles_cylinder_volume,id',
            'kilometre' => 'nullable|numeric',
            'production_year' => 'nullable|numeric',
            'neworold' => 'required|string|in:new,old',
        ];
    }

    public function messages()
    {
        return [
            'ads_title.required' => 'وارد کردن عنوان آگهی الزامی است.',
            'ads_title.max' => 'عنوان آگهی طولانی تر از حد مجاز است.',
            'region_id.required' => 'انتخاب استان، شهر و منطقه الزامی است.',
            'region_id.numeric' => 'منطقه نامعتبر است.',
            'region_id.exists' => 'منطقه نامعتبر است.',
            'thumbnail_photo.image' => 'تصویر بندانگشتی نامعتبر است.',
            'telephone1.max' => 'تلفن تماس 1 طولانی تر از حد مجاز است.',
            'telephone2.max' => 'تلفن تماس 2 طولانی تر از حد مجاز است.',
            'ads_owner_name.max' => 'نام صاحب آگهی طولانی تر از حد مجاز است.',
            'latitude.numeric' => 'عرض جغرافیایی نامعتبر است.',
            'longitude' => 'طول جغرافیایی نامعتبر است.',
            'type.required' => 'وارد کردن گروهبندی الزامی است.',
            'address.required' => 'وارد کردن آدرس الزامی است.',
            'type.in' => 'گروهبندی تنها می تواند یکی از مقادیر در خودرو، موتورسیکلت ، خودرو کلاسیک ، خودرو سنگین یا نمیه سنگین ، لوازم وسایل نقلیه یا سایر را داشته باشد.',
            'brand.numeric' => 'برند نامعتبر است.',
            'brand.exists' => 'برند نامعتبر است.',
            'model.numeric' => 'مدل نامعتبر است.',
            'model.exists' => 'مدل نامعتبر است.',
            'chassis_type.in' => 'نوع شاسی تنها می تواند یکی از مقادیر سواری، هاچ بک ،شاسی بلند، وانت، کروک، ون، کوپه، استیشن و دیگر را داشته باشد.',
            'neworold.required' => 'انتخاب نو یا کارکرده الزامی است.',
            'neworold.in' => 'انتخاب نو یا کارکرده تنها میتواند یکی از مقادیر نو یا کارکرده را داشته باشد.',
            'cylinder_volume.numeric' => 'حجم موتور نامعتبر است.',
            'cylinder_volume.exists' => 'حجم موتور نامعتبر است.'
        ];
    }


    public function formatErrors(Validator $validator)
    {
        $errors = $validator->errors()->all();
        $arr = [];
        foreach($errors as $error){
            $obj = new \stdClass();
            $obj->error = $errors;
            $arr[] = $obj;
        }

        return $arr;
    }

    public function response(array $errors)
    {
        return response()->json(['status' => 422 , 'errors' => $errors]);
    }
}
