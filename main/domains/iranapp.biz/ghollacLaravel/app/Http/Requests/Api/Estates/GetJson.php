<?php

namespace App\Http\Requests\Api\Estates;

use Illuminate\Contracts\Validation\Validator;
use Illuminate\Foundation\Http\FormRequest;

class GetJson extends FormRequest
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
            'offset' => 'nullable|numeric',
            'limit' => 'nullable|numeric',
            'category_id' => 'nullable|exists:estate_categories,id'
        ];
    }

    public function messages()
    {
        return [
            'offset.numeric' => 'مقدار offset باید عددی باشد.',
            'limit.numeric' => 'مقدار limit باید عددی باشد.',
            'category_id.exists' => 'دسته بندی نامعتبر است.'
        ];
    }

    public function formatErrors(Validator $validator)
    {
        $errorsArr = $validator->errors()->all();
        $resultArr = [];
        foreach($errorsArr as $error){
            $errorObj = new \stdClass();
            $errorObj->error = $error;
            $resultArr[] = $errorObj;
        }
        return $resultArr;
    }

    public function response(array $errors)
    {
        return response()->json(['status' => 422 , 'errors' => $errors] , 422);
    }
}
