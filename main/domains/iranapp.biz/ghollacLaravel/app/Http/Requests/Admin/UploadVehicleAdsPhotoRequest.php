<?php

namespace App\Http\Requests\Admin;

use Illuminate\Contracts\Validation\Validator;
use Illuminate\Foundation\Http\FormRequest;

class UploadVehicleAdsPhotoRequest extends FormRequest
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
            'photos' => 'required',
            'photos.*' => 'image'
        ];
    }

    public function messages()
    {
        return [
            'photos.required' => 'ارسال حداقل یک تصویر الزامی است.',
            'photos.*' => [
                'image' => 'image'
            ]
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
