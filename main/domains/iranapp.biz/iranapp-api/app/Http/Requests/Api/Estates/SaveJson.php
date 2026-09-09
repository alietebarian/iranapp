<?php

namespace App\Http\Requests\Api\Estates;

use Illuminate\Contracts\Validation\Validator;
use Illuminate\Foundation\Http\FormRequest;

class SaveJson extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     *
     * @return bool
     */
    public function authorize()
    {
        $user = auth('sanctum')->user();
        if($user){
            return true;
        }
        return false;
    }

    /**
     * Get the validation rules that apply to the request.
     *
     * @return array
     */
    public function rules()
    {
        return [
            'ads_title' => 'required|string|max:200',
            'region_id' => 'required|numeric|exists:region,id',
            'category_id' => 'required|exists:estate_categories,id',
            'thumbnail_photo' => 'nullable|image',
            'address' => 'nullable|string',
            'user_type' => 'required|in:moshaver_amlak,person',
            'is_in_hoome' => 'nullable|in:0,1',
            'sell_or_buy' => 'required|in:sell,buy',
            'pre_pay_ejare' => 'nullable|numeric',
            'monthly_price_ejare' => 'nullable|numeric',
            'price_kharid' => 'nullable|numeric',
            'sanad_edari' => 'nullable|numeric|in:0,1',
            'telephone1' => 'nullable|string|max:200',
            'telephone2' => 'nullable|string|max:200',
            'ads_owner_name' => 'nullable|string|max:200',
            'description' => 'nullable|string',
            'latitude' => 'nullable|numeric',
            'longitude' => 'nullable|numeric',
        ];
    }

    public function messages()
    {
        return [
            'ads_title.required' => 'عنوان آگهی الزامی است.',
            'ads_title.max' => 'عنوان آگهی طولانی تر از حد مجاز است.',
            'region_id.required' => 'انتخاب منطقه الزامی است.',
            'region_id.exists' => 'منطقه نامعتبر است.',
            'thumbnail_photo.image' => 'تصویر بندانگشتی نامعتبر است.',
            'user_type.required' => 'انتخاب نوع کاربر الزامی است.',
            'user_type.in' => 'نوع کاربر تنها می تواند فرد عادی یا مشاور املاک باشد.',
            'is_in_hoome.in' => 'فیلد حومه شهر نامعتبر است.',
            'sell_or_buy.required' => 'فیلد فروشی یا درخواستی الزامی است.',
            'sell_or_buy.in' => 'فیلد فروشی یا درخواستی نامعتبر است.',
            'pre_pay_ejare.required_if' => 'فیلد ودیعه در صورتی که ملک برای اجاره انتخاب شده باشد الزامی است.',
            'pre_pay_ejare.numeric' => 'فیلد ودیعه باید عددی باشد.',
            'monthly_price_ejare.required_if' => 'فیلد اجاره ماهیانه در صورتی که ملک برای اجاره انتخاب شده باشد الزامی است.',
            'monthly_price_ejare.numeric' => 'اجاره ماهیانه الزامی است.',
            'price_kharid.numeric' => 'فیلد قیمت خرید باید عددی باشد.',
            'sanad_edari.required_if' => 'فیلد دارای سند اداری در صورتی که ملک تجاری یا اداری باشد الزامی است.',
            'sanad_edari.numeric' => 'فیلد دارای سند اداری نامعتبر است.' ,
            'sanad_edari.in' => 'فیلد دارای سند اداری می تواند بله یا خیر باشد.',
            'telephone1.max' => 'تلفن تماس 1 طولانی تر از حد مجاز است.',
            'telephone2.max' => 'تلفن تماس 2 طولانی تر از حد مجاز است.',
            'latitude.numeric' => 'عرض جغرافیایی باید عددی باشد.',
            'longitude.numeric' => 'طول جغرافیایی باید عددی باشد.',
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
