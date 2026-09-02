<?php

namespace App\Http\Requests\Admin;

use App\Brand;
use Illuminate\Contracts\Validation\Validator;
use Illuminate\Foundation\Http\FormRequest;

class SaveVehicleAdsRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     *
     * @return bool
     */
    public function authorize()
    {
        return auth()->guard('admin')->check();
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
            'user_id' => 'required|numeric|exists:users,id',
            'address' => 'required|string',
            'telephone1' => 'nullable|string|max:200',
            'telephone2' => 'nullable|string|max:200',
            'status' => 'required|string|in:pending,approved,rejected',
            'ads_owner_name' => 'nullable|string|max:200',
            'description' => 'nullable|string',
            'latitude' => 'nullable|numeric',
            'longitude' => 'nullable|numeric',
            'type' => 'required|string|in:khodro,motorcycle,khodroclasic,khordrosorn,lavazem,other',
            'price' => 'nullable|numeric',
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
            'user_id.required' => 'انتخاب کاربر الزامی است.',
            'user_id.numeric' => 'کاربر نامعتبر است.',
            'user_id.exists' => 'کاربر نامعتبر است.',
            'telephone1.max' => 'تلفن تماس 1 طولانی تر از حد مجاز است.',
            'telephone2.max' => 'تلفن تماس 2 طولانی تر از حد مجاز است.',
            'status.required' => 'انتخاب وضعیت آگهی الزامی است.',
            'status.in' => 'وضعیت آگهی تنها می تواند یکی از مقادیر در انتظار تایید، تایید شده یا رد شده را داشته باشد.',
            'ads_owner_name.max' => 'نام صاحب آگهی طولانی تر از حد مجاز است.',
            'latitude.numeric' => 'عرض جغرافیایی نامعتبر است.',
            'longitude' => 'طول جغرافیایی نامعتبر است.',
            'type.required' => 'وارد کردن گروهبندی الزامی است.',
            'address.required' => 'وارد کردن آدرس الزامی است.',
            'type.in' => 'گروهبندی تنها می تواند یکی از مقادیر در خودرو، موتورسیکلت ، خودرو کلاسیک ، خودرو سنگین یا نمیه سنگین ، لوازم وسایل نقلیه یا سایر را داشته باشد.',
            'chassis_type.in' => 'نوع شاسی تنها می تواند یکی از مقادیر سواری، هاچ بک ،شاسی بلند، وانت، کروک، ون، کوپه، استیشن و دیگر را داشته باشد.',
            'neworold.required' => 'انتخاب نو یا کارکرده الزامی است.',
            'neworold.in' => 'انتخاب نو یا کارکرده تنها میتواند یکی از مقادیر نو یا کارکرده را داشته باشد.',
            'cylinder_volume.numeric' => 'حجم موتور نامعتبر است.',
            'cylinder_volume.exists' => 'حجم موتور نامعتبر است.',
        ];
    }
    public function formatErrors(Validator $validator)
    {
        return $validator->errors()->all();
    }

    public function response(array $errors)
    {
        $msg = new \stdClass();
        $msg->title = 'خطا';
        $msg->msg = implode('\r\n' , $errors);
        request()->session()->flash('error_msg' , $msg);
    }

    public function withValidator($validator){
        $validator->after(function($validator){
            if( $this->brand && $this->brand != 'all'){
                $brand = Brand::find($this->brand);
                $modelExists = $brand->models()->exists();
                if($modelExists){
                    if($this->model == 'all'){
                        $validator->errors()->add('model' , 'انتخاب مدل الزامی است.');
                    }
                }
            }

        });
    }
}
