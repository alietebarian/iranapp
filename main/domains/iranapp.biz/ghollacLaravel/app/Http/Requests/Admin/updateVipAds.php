<?php

namespace App\Http\Requests\admin;

use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Support\Facades\Auth;

class updateVipAds extends FormRequest
{
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
            'regional_displaying' => 'required|in:country,province,city',
            'category_displaying' => 'required|in:category,sub_category,without_selection,all',
            'show_in_homepage' => 'required|in:yes,no',
            'photo' => 'file'
        ];
    }

    public function messages()
    {
        return [
            'regional_displaying.required' => 'انتخاب نمایش منطقه ای الزامی است.',
            'regional_displaying.in' => 'نمایش منطقه ای تنها می تواند یکی از مقادیر شهر، استان یا کشور را داشته باشد.',
            'category_displaying.required' => 'انتخاب فیلد نمایش در دسته ها الزامی است.',
            'category_displaying.in' => 'فیلد نمایش در دسته ها می تواند تنها یکی از مقادیر نمایش در دسته بندی یا نمایش در زیر دسته بندی یا هیچ کدام را داشته باشد.',
            'show_in_homepage.required' => 'انتخاب فیلد نمایش در صفحه اصلی الزامی است.',
            'show_in_homepage.in' => 'فیلد نمایش در صفحه اصلی تنها می تواند یکی از مقادیر بله یا خیر را داشته باشد.',
            'photo.file' => 'تصویر نامعتبر است.'
        ];
    }
}
