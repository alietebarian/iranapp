<?php

namespace App\Http\Requests\Admin;

use Illuminate\Foundation\Http\FormRequest;

class UpdateVisitorRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     *
     * @return bool
     */
    public function authorize()
    {
        return true;
    }

    /**
     * Get the validation rules that apply to the request.
     *
     * @return array
     */
    public function rules()
    {
        return [
            'first_name' => 'required|string',
            'last_name' => 'required|string',
            'password' => 'nullable|string',
            'mobile' => 'required|string|max:50',
        ];
    }

    public function messages()
    {
        return[
            'first_name.required' => 'نام ویزیتور الزامی است.',
            'first_name.string' => 'نام ویزیتور نامعتبر است.',
            'last_name.required' => 'نام خانوادگی ویزیتور الزامی است.',
            'last_name.string' => 'نام خانوادگی ویزیتور نامعتبر است.',
            'password.string' => 'رمز عبور نامعتبر است.',
            'mobile.required' => 'وارد کردن تلفن همراه الزامی است.',
            'mobile.string' => 'تلفن همراه نامعتبر است.',
            'mobile.max' => 'تلفن همراه طولانی تر از حد مجاز است.',
        ];
    }
}
