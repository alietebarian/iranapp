<?php

namespace App\Http\Requests;

use Illuminate\Foundation\Http\FormRequest;

class saveAdsRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     *
     * @return bool
     */
    public function authorize()
    {
        $user = auth('sanctum')->user();
        if($user){
            return true;
        }
        return false;
    }

    /**
     * Get the validation rules that apply to the request.
     *
     * @return array
     */
    public function rules()
    {
        return [
            'title' => 'required|string|max:300',
            'latitude' => 'nullable|numeric',
            'longitude' => 'nullable|numeric',
            'city_id' => 'required|numeric|exists:city,id',
            'address' => 'nullable|string',
            'type' => 'required:in:discount,need',
            'sub_category_id' => 'required|exists:sub_category,id',
            'email' => 'nullable|email',
            'mobile' => 'required',
            'tel1' => 'nullable',
            'tel2' => 'nullable',
            'link' => 'nullable' ,
            'discount' => 'nullable',
            'working_time' => 'nullable',
            'telegram' => 'nullable',
            'instagram' => 'nullable',
            'notes' => 'nullable',
            'ads_plan_id' => 'required|exists:ads_plan,id'
        ];
    }
    public function messages()
    {
        return [
            'title.required' => 'عنوان آگهی الزامی است.',
            'title.max' => 'عنوان آگهی طولانی تر از حد مجاز است.',
            'latitude.numeric' => 'عرض جغرافیایی نامعتبر است.',
            'longitude.numeric' => 'عرض جغرافیایی نامعتبر است.',
            'city_id.required' => 'انتخاب شهر الزامی است.',
            'city_id.numeric' => 'شهر نامعتبر است.',
            'city_id.exists' => 'شهر نامعتبر است.',
            'type.required' => 'انتخاب نوع آگهی الزامی است..',
            'type.in' => 'نوع آگهی باید تخفیف یا نیازمندی ها باشد.',
            'sub_category_id.required' => 'انتخاب دسته و زیر دسته الزامی است.',
            'sub_category_id.exists' => 'زیر دسته نامعتبر است.',
            'email.email' => 'ایمیل نامعتبر است.',
            'mobile.required' => 'وارد کردن شماره تلفن همراه الزامی است.',
            'ads_plan_id.required' => 'انتخاب پلن آگهی الزامی است.',
            'ads_plan_id.exists' => 'پلن آگهی نامعتبر است.'
        ];
    }
}
