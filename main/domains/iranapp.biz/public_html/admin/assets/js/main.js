
$('#form-to-validate').validate({
    rules: {
        title: {
            required: true
        },
        province_id: {
            required: true
        },
        city_id: {
            required: true
        },
        category_id: {
            required: true
        },
        sub_category_id: {
            required: true
        },
        user_id: {
            required: true
        },
        email: {
            required: false,
            email: true
        },
        type: {
            required: true
        },
        link: {
            url: true
        },
        telegram: {
            required: false,
            url: true
        },
        instagram: {
            required: false,
            url: true
        },
        status: {
            required: true
        },
        ads_plan_id: {
            required: true
        }
    },
    messages: {
        title: {
            required: 'عنوان آگهی الزامی است.'
        },
        province_id: {
            required: 'انتخاب استان الزامی است.'
        },
        city_id: {
            required: 'انتخاب شهر الزامی است.'
        },
        category_id: {
            required: 'انتخاب دسته بندی الزامی است.'
        },
        sub_category_id: {
            required: 'انتخاب زیر دسته بندی الزامی است.'
        },
        user_id: {
            required: 'انتخاب کاربر الزامی است.'
        },
        email: {
            email: 'ایمیل نامعتبر است.'
        },
        type: {
            required: 'انتخاب نوع آگهی الزامی است.'
        },
        link: {
            url: 'لینک نامعتبر است.'
        },
        telegram: {
            url: 'آدرس تلگرام نامعتبر است.'
        },
        instagram: {
            url: 'آدرس اینستاگرام نامعتبر است.'
        },
        status: {
            required: 'انتخاب وضعیت آگهی الزامی است.'
        },
        ads_plan_id: {
            required: 'انتخاب پلن آگهی الزامی است.'
        }
    },
    errorClass: 'input-field-errors'
});


$('#update-form-to-validate').validate({
    rules: {
        title: {
            required: true
        },
        province_id: {
            required: true
        },
        city_id: {
            required: true
        },
        category_id: {
            required: true
        },
        sub_category_id: {
            required: true
        },
        user_id: {
            required: true
        },
        email: {
            required: false,
            email: true
        },
        type: {
            required: true
        },
        link: {
            url: true
        },
        telegram: {
            required: false,
            url: true
        },
        instagram: {
            required: false,
            url: true
        },
        status: {
            required: true
        },
        ads_plan_id: {
            required: true
        }
    },
    messages: {
        title: {
            required: 'عنوان آگهی الزامی است.'
        },
        province_id: {
            required: 'انتخاب استان الزامی است.'
        },
        city_id: {
            required: 'انتخاب شهر الزامی است.'
        },
        category_id: {
            required: 'انتخاب دسته بندی الزامی است.'
        },
        sub_category_id: {
            required: 'انتخاب زیر دسته بندی الزامی است.'
        },
        user_id: {
            required: 'انتخاب کاربر الزامی است.'
        },
        email: {
            email: 'ایمیل نامعتبر است.'
        },
        type: {
            required: 'انتخاب نوع آگهی الزامی است.'
        },
        link: {
            url: 'لینک نامعتبر است.'
        },
        telegram: {
            url: 'آدرس تلگرام نامعتبر است.'
        },
        instagram: {
            url: 'آدرس اینستاگرام نامعتبر است.'
        },
        status: {
            required: 'انتخاب وضعیت آگهی الزامی است.'
        },
        ads_plan_id: {
            required: 'انتخاب پلن آگهی الزامی است.'
        }
    },
    errorClass: 'input-field-errors'
});


$('#save-estate-ads-form').validate({
    rules: {
        ads_title: {
            required: true
        },
        province_id: {
            required: true
        },
        city_id: {
            required: true
        },
        region_id: {
            required: true
        },
        user_id: {
            required: true
        },
        address: {
            required: false
        },
        user_type: {
            required: true
        },
        is_in_hoome: {
            required: true
        },
        sell_or_buy: {
            required: true
        },
        ejare_or_kharid: {
            required: true
        },
        meters: {
            required: true
        },
        type_karbari: {
            required: true
        },
        sanad_edari: {
            required: false
        },
        status: {
            required: true
        },
        ads_owner_name: {
            required: false
        },
        description: {
            required: false
        },
        latitude: {
            required: false
        },
        longitude: {
            required: false
        }
    },
    messages: {
        ads_title: {
            required: 'عنوان آگهی الزامی است.'
        },
        province_id: {
            required: 'انتخاب استان الزامی است.'
        },
        city_id: {
            required: 'انتخاب شهر الزامی است.'
        },
        region_id: {
            required: 'انتخاب منطقه الزامی است.'
        },
        user_id: {
            required: 'انتخاب نام کاربر الزامی است.'
        },
        user_type: {
            required: 'انتخاب نوع کاربر ثبت کننده الزامی است.'
        },
        is_in_hoome: {
            required: 'انتخاب این گزینه الزامی است.'
        },
        sell_or_buy: {
            required: 'انتخاب این گزینه الزامی است.'
        },
        ejare_or_kharid: {
            required: 'انتخاب این گزینه الزامی است.'
        },
        rooms_count: {
            required: 'تعداد اتاق خواب الزامی است.'
        },
        meters: {
            required: 'وارد کردن متراژ الزامی است'
        },
        type_karbari:{
            required: 'انتخاب نوع کاربری الزامی است.'
        },
        status: {
            required: 'انتخاب وضعیت آگهی الزامی است.'
        }
    },
    errorClass: 'input-field-errors'
});

$('#saveEmployAdsForm').validate({
    rules: {
        ads_title: {
            required: true
        },
        specialty: {
            required: true
        },
        agremment_type: {
            required: true
        },
        province_id: {
            required: true
        },
        city_id:{
            required: true
        },
        region_id: {
            required: true
        },
        education_level: {
            required: true
        },
        user_id: {
            required: true
        },
        status: {
            required: true
        },
        type: {
            required: true
        }
    },
    messages:{
        ads_title: {
            required: 'عنوان آگهی الزامی است.'
        },
        specialty:{
            required: 'انتخاب تخصص الزامی است.'
        },
        agremment_type:{
            required: 'انتخاب نوع قرارداد الزامی است.'
        },
        province_id:{
            required: 'انتخاب استان الزامی است.'
        },
        city_id: {
            required: 'انتخاب شهر الزامی است.'
        },
        region_id: {
            required: 'انتخاب منطقه الزامی است.'
        },
        education_level: {
            required: 'انتخاب میزان تخصیلات الزامی است.'
        },
        user_id:{
            required: 'انتخاب کاربر الزامی است.'
        },
        status: {
            required: 'انتخاب وضعیت آگهی الزامی است.'
        },
        type: {
            required: 'انتخاب نوع آگهی الزامی است.'
        }
    },
    errorClass: 'input-field-errors'

});

$('#UpdateEmployAdsForm').validate({
    rules: {
        ads_title: {
            required: true
        },
        specialty: {
            required: true
        },
        agremment_type: {
            required: true
        },
        province_id: {
            required: true
        },
        city_id:{
            required: true
        },
        region_id: {
            required: true
        },
        education_level: {
            required: true
        },
        user_id: {
            required: true
        },
        status: {
            required: true
        },
        type: {
            required: true
        }
    },
    messages:{
        ads_title: {
            required: 'عنوان آگهی الزامی است.'
        },
        specialty:{
            required: 'انتخاب تخصص الزامی است.'
        },
        agremment_type:{
            required: 'انتخاب نوع قرارداد الزامی است.'
        },
        province_id:{
            required: 'انتخاب استان الزامی است.'
        },
        city_id: {
            required: 'انتخاب شهر الزامی است.'
        },
        region_id: {
            required: 'انتخاب منطقه الزامی است.'
        },
        education_level: {
            required: 'انتخاب میزان تخصیلات الزامی است.'
        },
        user_id:{
            required: 'انتخاب کاربر الزامی است.'
        },
        status: {
            required: 'انتخاب وضعیت آگهی الزامی است.'
        },
        type: {
            required: 'انتخاب نوع آگهی الزامی است.'
        }
    },
    errorClass: 'input-field-errors'

});
jQuery.validator.addMethod('productionYearValidator' , function(value , element) {
    var type = element.closest('form').find('select[name="type"]');
    if(type.val() == 'khodro' || type.val() == 'motorcycle'){
        if(!value){
            return false;
        }
        return true;
    }else{
        return true;
    }
} , 'سال تولید الزامی است.');
$('#saveVehicleAdsForm').validate({
    rules: {
        description: {
            required: true
        },
        production_year:{
            productionYearValidator: true,
            number: true
        },
        ads_owner_name:{
            required: true
        },
        telephone1:{
            required: true
        },
        kilometre:{
            required: true,
            number: true
        },
        address: {
            required: true
        },
        price:{
            required: true,
            number: true
        },
        type: {
            required: true
        },
        province_id:{
            required: true
        },
        ads_title:{
            required: true
        },
        city_id:{
            required: true
        },
        region_id:{
            required: true
        }
    },
    messages:{
        description:{
            required: 'توضیحات آگهی الزامی است.'
        },
        production_year:{
            required: 'سال تولید الزامی است.',
            number: 'سال تولید باید عددی باشد.'
        },
        ads_owner_name: {
            required: 'نام صاحب آگهی الزامی است.'
        },
        telephone1:{
            required: 'تلفن تماس ۱ الزامی است.'
        },
        kilometre:{
            required: 'کیلومتر الزامی است.',
            number: 'کیلومتر باید به صورت عددی وارد شود.'
        },
        address:{
            required: 'آدرس الزامی است.'
        },
        price: {
            required: 'قیمت الزامی است.',
            number: 'قیمت باید به صورت عددی وارد شود.'
        },
        type:{
            required: 'انتخاب گروه بندی الزامی است.'
        },
        province_id:{
            required: 'انتخاب استان الزامی است.'
        },
        city_id: {
            required: 'انتخاب شهر الزامی است.'
        },
        region_id:{
            required: 'انتخاب منطقه الزامی است.'
        },
        ads_title:{
            required: 'عنوان آگهی الزامی است.'
        }
    },
    errorClass: 'input-field-errors'

});