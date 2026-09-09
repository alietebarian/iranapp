<?php

namespace App\Http\Requests\Admin;

use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Support\Facades\Auth;

class SaveAdsPlan extends FormRequest
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
            'name' => 'required|string|max:200',
            'stars' => 'required|numeric|min:0',
            'max_photo' => 'required|numeric|min:0',
            'price' => 'required|numeric|min:0',
            'updates_count' => 'required|numeric|min:0',
            'interval_days' => 'required|numeric|min:1'
        ];
    }

    public function messages()
    {
        return [
            'name.required' => 'نام پلن الزامی است.',
            'name.max' => 'نام پلن طولانی تر از حد مجاز است.',
            'stars.required' => 'تعداد ستاره ها الزامی است.',
            'stars.numeric' => 'تعداد ستاره ها باید عددی باشد.',
            'stars.min' => 'تعداد ستاره ها حداقل می تواند 0 باشد.',
            'max_photo.required' => 'حداکثر تعداد عکس ها الزامی است.',
            'max_photo.numeric' => 'تعداد عکس ها باید عددی باشد.',
            'max_photo.min' => 'تعداد عکس ها حداقل می تواند 0 باشد.',
            'price.required' => 'قیمت الزامی است.',
            'price.numeric' => 'قیمت باید عددی باشد.',
            'price.min' => 'قیمت حداقل می تواند 0 باشد.',
            'updates_count.required' => 'دفعات به روز رسانی الزامی است.',
            'updates_count.numeric' => 'دفعات به روز رسانی باید به صورت عددی وارد شود.',
            'updates_count.min' => 'دفعات به روز رسانی باید حداقل برابر صفر باشد.',
            'interval_days.required' => 'روزهای اعتبار آگهی الزامی است.',
            'interval_days.numeric' => 'روزهای اعتبار باید به صورت عددی وارد شود.',
            'interval_days.min' => 'روزهای اعتبار آگهی باید حداقل یک روز باشد.',
        ];
    }
}
