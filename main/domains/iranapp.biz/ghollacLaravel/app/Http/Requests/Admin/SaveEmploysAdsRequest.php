<?php

namespace App\Http\Requests\Admin;

use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Contracts\Validation\Validator;

class SaveEmploysAdsRequest extends FormRequest
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
            'type' => 'required|in:karjoo,forsatshoghli',
            'optradio' => 'nullable|string',
            'specialty' => 'required|numeric|exists:employs_ads_specialty,id',
            'agremment_type' => 'required|string|in:tamamvaght,parevaght,moshaveri,projei',
            'education_level' => 'required|string|in:underdiploma,diploma,tact,expertise,masterdegree,doctoral',
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
            'address.required' => 'وارد کردن آدرس الزامی است.',
            'type.required' => 'انتخاب نوع آگهی الزامی است.',
            'type.in' => 'نوع آگهی تنها می تواند آماده به کار یا استخدام باشد.',
            'specialty.required' =>  'وارد کردن تخصص آگهی الزامی است.',
            'specialty.numeric' => 'تخصص نامعتبر است.',
            'specialty.exists' => 'تخصص نامعتبر است.',
            'agremment_type.required' => 'وارد کردن نوع قرارداد الزامی است.',
            'agremment_type.in' => 'نوع قرارداد تنها می تواند یکی از مقادیر تمام وقت، پاره وقت ،مشاوره ای و پروژه ای را داشته باشد.',
            'education_level.required' => 'وارد کردن میزان تحصیلات الزامی است.',
            'education_level.in' => 'انتخاب میزان تحصیلات تنها میتواند یکی از مقادیر زیردیپلم، دیپلم، کاردانی، کارشناسی، کارشناسی ارشد یا دکتری و بالاتر را داشته باشد.',

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
}
