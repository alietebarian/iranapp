<?php

namespace App\Http\Requests\Admin\Regions;

use Illuminate\Contracts\Validation\Validator;
use Illuminate\Foundation\Http\FormRequest;

class Update extends FormRequest
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
            'name' => 'required|string|max:200'
        ];
    }

    public function messages()
    {
        return [
            'name.required' => 'نام منطقه الزامی است.',
            'name.max' => 'نام منطقه طولانی تر از حد مجاز است.'
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
