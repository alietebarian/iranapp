<?php

namespace App\Http\Requests\admin;

use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Support\Facades\Auth;

class saveNews extends FormRequest
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
            'title' => 'required|string|max:400',
            'news_text' => 'required|string'
        ];
    }
    public function messages()
    {
        return [
            'title.required' => 'عنوان خبر الزامی است.',
            'title.max' => 'عنوان خبر طولانی تر از حد مجاز است.',
            'news_text.required' => 'متن خبر الزامی است.'
        ];
    }
}
