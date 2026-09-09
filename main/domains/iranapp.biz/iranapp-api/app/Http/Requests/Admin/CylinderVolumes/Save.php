<?php

namespace App\Http\Requests\Admin\CylinderVolumes;

use Illuminate\Foundation\Http\FormRequest;

class Save extends FormRequest
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
            'value' => 'required|string|max:200'
        ];
    }

    public function messages()
    {
        return [
            'value.required' => 'وارد کردن عنوان الزامی است.',
            'value.max' => 'حجم موتور طولانی تر از حد مجاز است.'
        ];
    }

}
