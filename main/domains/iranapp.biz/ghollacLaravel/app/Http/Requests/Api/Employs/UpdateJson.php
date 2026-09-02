<?php

namespace App\Http\Requests\Api\Employs;

use Illuminate\Contracts\Validation\Validator;
use Illuminate\Foundation\Http\FormRequest;
use Tymon\JWTAuth\Facades\JWTAuth;

class UpdateJson extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     *
     * @return bool
     */
    public function authorize()
    {
        $user = JWTAuth::parseToken()->authenticate();
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
            'thumbnail_photo' => 'nullable|image',
            'address' => 'required|string',
            'telephone1' => 'nullable|string|max:200',
            'telephone2' => 'nullable|string|max:200',
            'ads_owner_name' => 'nullable|string|max:200',
            'description' => 'nullable|string',
            'latitude' => 'nullable|numeric',
            'longitude' => 'nullable|numeric',
            'optradio' => 'nullable|string',
            'specialty' => 'required|numeric|exists:employs_ads_specialty,id',
            'agremment_type' => 'required|string|in:tamamvaght,parevaght,moshaveri,projei',
            'education_level' => 'required|string|in:underdiploma,diploma,tact,expertise,masterdegree,doctoral',
        ];
    }

    public function messages()
    {
        return [
            'ads_title.required' => 'وارد کردن عنوان آگهی الزامی است.',
            'ads_title.max' => 'عنوان آگهی طولانی تر از حد مجاز است.',
            'region_id.required' => 'انتخاب استان، شهر و منطقه الزامی است.',
            'region_id.numeric' => 'منطقه نامعتبر است.',
            'region_id.exists' => 'منطقه نامعتبر است.',
            'thumbnail_photo.image' => 'تصویر بندانگشتی نامعتبر است.',
            'telephone1.max' => 'تلفن تماس 1 طولانی تر از حد مجاز است.',
            'telephone2.max' => 'تلفن تماس 2 طولانی تر از حد مجاز است.',
            'ads_owner_name.max' => 'نام صاحب آگهی طولانی تر از حد مجاز است.',
            'latitude.numeric' => 'عرض جغرافیایی نامعتبر است.',
            'longitude' => 'طول جغرافیایی نامعتبر است.',
            'address.required' => 'وارد کردن آدرس الزامی است.',

            'specialty.required' =>  'وارد کردن تخصص آگهی الزامی است.',
            'specialty.numeric' => 'تخصص نامعتبر است.',
            'specialty.exists' => 'تخصص نامعتبر است.',
            'agremment_type.required' => 'وارد کردن نوع قرارداد الزامی است.',
            'agremment_type.in' => 'نوع قرارداد تنها می تواند یکی از مقادیر تمام وقت، پاره وقت ،مشاوره ای و پروژه ای را داشته باشد.',
            'education_level.required' => 'وارد کردن میزان تحصیلات الزامی است.',
            'education_level.in' => 'انتخاب میزان تحصیلات تنها میتواند یکی از مقادیر زیردیپلم، دیپلم، کاردانی، کارشناسی، کارشناسی ارشد یا دکتری و بالاتر را داشته باشد.',

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
