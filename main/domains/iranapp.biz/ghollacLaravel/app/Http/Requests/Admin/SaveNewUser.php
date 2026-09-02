<?php

namespace App\Http\Requests\Admin;

use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Support\Facades\Auth;

class SaveNewUser extends FormRequest
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
            'first_name' => 'required|string|max:200',
            'last_name' => 'required|string|max:200',
            'mobile' => 'required|unique:users,mobile',
            'password' => 'required'
        ];
    }

    public function messages()
    {
        return [
            'first_name.required' => 'وارد کردن نام الزامی است.',
            'first_name.max' => 'نام طولانی نر از حد مجاز است.',
            'last_name.required' => 'وارد کردن نام خانوادگی الزامی است.',
            'last_name.max' => 'نام خانوادگی طولانی تر از حد مجاز است.',
            'mobile.required' => 'وارد کردن تلفن همراه الزامی است.',
            'mobile.unique' => 'تلفن همراه وارد شده قبلا توسط کاربران دیگر به ثبت رسیده است.',
            'password.required' => 'وارد کردن رمز عبور الزامی است.'
        ];
    }
}
