<?php

namespace App\Http\Requests\Admin;

use Illuminate\Contracts\Validation\Validator;
use Illuminate\Foundation\Http\FormRequest;

class SaveEstateAdsRequest extends FormRequest
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
            'address' => 'nullable|string',
            'user_type' => 'required|in:moshaver_amlak,person',
            'is_in_hoome' => 'required|in:yes,no',
            'sell_or_buy' => 'required|in:sell,buy',
            'price_kharid' => 'nullable|numeric',
            'pre_pay_ejare' => 'nullable|numeric',
            'monthly_price_ejare' => 'nullable|numeric',
            'rooms_count' => 'nullable|numeric|min:0',
            'meters' => 'nullable|numeric|min:0',
            'sanad_edari' => 'nullable',
            'telephone1' => 'nullable|string|max:200',
            'telephone2' => 'nullable|string|max:200',
            'status' => 'required|string|in:pending,approved,rejected',
            'ads_owner_name' => 'nullable|string|max:200',
            'description' => 'nullable|string',
            'latitude' => 'nullable|numeric',
            'longitude' => 'nullable|numeric'
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
            'user_type.required' => 'انتخاب نوع ثبت کننده الزامی است.',
            'user_type.in' => 'نوع ثبت کننده نامعتبر است.',
            'is_in_hoome.required' => 'فیلد ملک در حومه شهر واقع شده است الزامی است.',
            'is_in_hoome.in' => 'فیلد ملک در حومه شهر واقع شده است نامعتبر است.',
            'sell_or_buy.required' => 'نوع الزامی است.',
            'sell_or_buy.in' => 'نوع نامعتبر است.',
            'price_kharid.numeric' => 'فیلد قیمت خرید نامعتبر است.',
            'pre_pay_ejare.numeric' => 'فیلد ودیعه نامعتبر است.',
            'monthly_price_ejare.numeric' => 'اجاره ماهیانه نامعتبر است.',
            'rooms_count.required' => 'فیلد تعداد خواب الزامی است.',
            'rooms_count.numeric' => 'فیلد تعداد خواب باید به صورت عددی وارد شود.',
            'rooms_count.min' => 'فیلد تعداد خواب نامعتبر است.',
            'meters.required' => 'فیلد متراژ الزامی است.',
            'meters.numeric' => 'متراژ باید به صورت عددی وارد شود.',
            'meters.min' => 'متراژ نامعتبر است.',
            'telephone1.max' => 'تلفن تماس 1 طولانی تر از حد مجاز است.',
            'telephone2.max' => 'تلفن تماس 2 طولانی تر از حد مجاز است.',
            'status.required' => 'انتخاب وضعیت آگهی الزامی است.',
            'status.in' => 'وضعیت آگهی تنها می تواند یکی از مقادیر در انتظار تایید، تایید شده یا رد شده را داشته باشد.',
            'ads_owner_name.max' => 'نام صاحب آگهی طولانی تر از حد مجاز است.',
            'latitude.numeric' => 'عرض جغرافیایی نامعتبر است.',
            'longitude' => 'طول جغرافیایی نامعتبر است.'
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
