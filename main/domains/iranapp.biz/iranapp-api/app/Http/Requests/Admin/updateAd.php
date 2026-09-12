<?php

namespace App\Http\Requests\admin;

use App\Http\Requests\Admin\Concerns\NormalizesSocialLinks;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Support\Facades\Auth;

class updateAd extends FormRequest
{
    use NormalizesSocialLinks;

    /**
     * Determine if the user is authorized to make this request.
     *
     * @return bool
     */
    public function authorize()
    {
        return Auth::guard('admin')->check();
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
            'type' => 'required|string|in:discount,need',
            'sub_category_id' => 'required|numeric|exists:sub_category,id',
            'email' => 'nullable|email',
            'mobile' => 'nullable|string|max:200',
            'tel1' => 'nullable|string|max:200',
            'tel2' => 'nullable|string|max:200',
            'link' => 'nullable|string|max:500',
            'discount' => 'required_if:type,discount|max:300',
            'working_time' => 'nullable|string|max:500',
            ...$this->socialLinkRules(),
            'notes' => 'nullable|string',
            'status' => 'required|in:pending,approved,rejected',
            'ads_plan_id' => 'required|exists:ads_plan,id',
            'user_id' => 'required|exists:users,id'
        ];
    }

    public function messages()
    {
        return [
            'title.required' => 'عنوان آگهی الزامی است.',
            'title.max' => 'عنوان آگهی طولانی تر از حد مجاز است.',
            'latitude.numeric' => 'طول جغرافیایی نامعتبر است.',
            'longitude.numeric' => 'عرض جغرافیایی نامعتبر است.',
            'city_id.required' => 'انتخاب شهر الزامی است.',
            'city_id.numeric' => 'شهر نامعتبر است.',
            'city_id.exists' => 'شهر نامعتبر است.',
            'address.string' => 'آدرس نامعتبر است.',
            'type.required' => 'انتخاب نوع آگهی الزامی است.',
            'type.in' => 'نوع آگهی تنها می تواند یکی از مقادیر تخفیفات یا نیازمندی ها باشد.',
            'sub_category_id.required' => 'انتخاب دسته و زیر دسته  الزامی است.',
            'sub_category_id.numeric' => 'دسته یا زیر دسته نامعتبر است.',
            'sub_category_id.exists' => 'دسته یا زیر دسته نامعتبر است.',
            'email.email' => 'ایمیل نامعتبر است.',
            'mobile.max' => 'تلفن همراه طولانی تر از حد مجاز است.',
            'tel1.max' => 'تلفن تماس 1 طولانی تز از حد مجاز است.',
            'tel2.max' => 'تلفن تماس 2 طولانی تز از حد مجاز است.',
            'link.max' => 'لینک طولانی تز از حد مجاز است.',
            'discount.required_if' => 'وارد کردن میزان تخفیف الزامی است.',
            'discount.max' => 'میزان تخفیف طولانی تر از حد مجاز است.',
            'working_time.max' => 'ساعت کاری طولانی تر از حد مجاز است.',
            ...$this->socialLinkMessages(),
            'status.required' => 'انتخاب وضعیت آگهی الزامی است.',
            'status.in' => 'وضعیت آگهی تنها می تواند یکی از مقادیر رد شده، تایید شده یا در انتظار تایید را داشته باشد.',
            'ads_plan_id.required' => 'انتخاب پلن آگهی الزامی است.',
            'ads_plan_id.exists' => 'پلن آگهی نامعتبر است.',
            'user_id.required' => 'انتخاب کاربر الزامی است.',
            'user_id.exists' => 'کاربر انتخاب شده نامعتبر است.'
        ];
    }
}
