@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">ثبت آگهی وسایل نقلیه
                        (
                        <span class="input-field-errors">فیلد های ستاره دار الزامی هستند.</span>
                        )
                    </h1>
                    <form id="saveVehicleAdsForm" action="{{ route('vehicleAds.update' , $ads->id) }}" method="post"
                          enctype="multipart/form-data">
                        {{ method_field('PUT') }}
                        {{ csrf_field() }}
                        <div class="row">
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="type">انتخاب گروهبندی:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select class="form-control" name="type" id="type">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        <option {{ $ads->type == 'khodro' ? 'selected' : '' }} value="khodro">خودرو
                                        </option>
                                        <option {{ $ads->type == 'motorcycle' ? 'selected' : '' }} value="motorcycle">
                                            موتور سیکلت
                                        </option>
                                        <option {{ $ads->type == 'khodroclasic' ? 'selected' : '' }} value="khodroclasic">
                                            خودرو کلاسیک
                                        </option>
                                        <option {{ $ads->type == 'khordrosorn' ? 'selected' : '' }} value="khordrosorn">
                                            خودرو سنگین و نیمه سنگین
                                        </option>
                                        <option {{ $ads->type == 'lavazem' ? 'selected' : '' }} value="lavazem">لوازم
                                            وسایل نقلیه
                                        </option>
                                        <option {{ $ads->type == 'other' ? 'selected' : '' }} value="other">سایر وسایل
                                            نقلیه
                                        </option>
                                    </select>
                                    @if($errors->has('type'))
                                        <b class="text-danger">{{ $errors->first('type') }}</b>
                                    @endif
                                </div>
                            </div>{{--گروهبندی--}}
                            <script>
                                $(document).ready(function () {
                                    var type = $('#type').val();
                                    var chassis_type = $('#chassis_type');
                                    var brand_and_model = $('#brand , #model , #kilometre');
                                    var production_year = $('#production_year');
                                    var cylinder_volume = $('#cylinder_volume');
                                    if (type == 'motorcycle') {
                                        chassis_type.attr('disabled', 'disabled');
                                        cylinder_volume.removeAttr('disabled');
                                        brand_and_model.attr('disabled', 'disabled');
                                        production_year.removeAttr('disabled');
                                    } else {
                                        if(type == 'khodroclasic'){
                                            production_year.attr('disabled' , 'disabled');
                                        } else if(type == 'khordrosorn'){
                                            production_year.attr('disabled' , 'disabled');
                                        } else{
                                            production_year.removeAttr('disabled');
                                        }
                                        cylinder_volume.attr('disabled', 'disabled');
                                        if (type == 'khodro') {
                                            brand_and_model.removeAttr('disabled');
                                            chassis_type.removeAttr('disabled');
                                        } else {
                                            chassis_type.attr('disabled', 'disabled');
                                            brand_and_model.attr('disabled', 'disabled');
                                        }
                                    }
                                });
                                $('#type').change(function () {
                                    var $this = $(this);
                                    var chassis_type = $('#chassis_type');
                                    var brand_and_model = $('#brand , #model , #kilometre');
                                    var production_year = $('#production_year');
                                    var cylinder_volume = $('#cylinder_volume');
                                    if ($this.val() == 'motorcycle') {
                                        chassis_type.attr('disabled', 'disabled');
                                        cylinder_volume.removeAttr('disabled');
                                        brand_and_model.attr('disabled', 'disabled');
                                        production_year.removeAttr('disabled');
                                    } else {
                                        if($this.val() == 'khodroclasic'){
                                            production_year.attr('disabled' , 'disabled');
                                        }else if($this.val() == 'khordrosorn'){
                                            production_year.attr('disabled' , 'disabled');
                                        }else{
                                            production_year.removeAttr('disabled');
                                        }
                                        cylinder_volume.attr('disabled', 'disabled');
                                        if ($this.val() == 'khodro') {
                                            brand_and_model.removeAttr('disabled');
                                            chassis_type.removeAttr('disabled');
                                        } else {
                                            chassis_type.attr('disabled', 'disabled');
                                            brand_and_model.attr('disabled', 'disabled');
                                        }
                                    }
                                });
                            </script>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="province">استان:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select class="selectpicker" id="province" data-live-search="true"
                                            name="province_id"
                                            data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        @foreach($provinces as $prv)
                                            <option {{ $prv->id === $currentProvince->id ? 'selected' : '' }} value="{{ $prv->id }}">{{ $prv->name }}</option>
                                        @endforeach
                                    </select>
                                </div>
                            </div>{{--استان--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="ads-title">عنوان:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <input type="text" value="{{ $ads->ads_title }}" name="ads_title" id="ads-title"
                                           class="form-control">
                                    @if($errors->has('ads_title'))
                                        <b class="text-danger">{{ $errors->first('ads_title') }}</b>
                                    @endif
                                </div>
                            </div>{{--عنوان--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="cities">شهر:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select class="selectpicker" name="city_id" id="cities" data-live-search="true"
                                            data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        @foreach($cities as $city)
                                            <option {{ $city->id === $currentCity->id ? 'selected' : '' }} value="{{ $city->id }}">{{ $city->name }}</option>
                                        @endforeach
                                    </select>
                                    @if($errors->has('city_id'))
                                        <b class="text-danger">{{ $errors->first('city_id') }}</b>
                                    @endif
                                </div>
                                <script>
                                    $('#province').change(function () {
                                        var $this = $(this);
                                        var provinceId = $this.val();
                                        $.ajax({
                                            type: 'get',
                                            url: '{{ URL::to('api/provinces/') }}/' + provinceId + '/cities',
                                            data: {},
                                            success: function (response) {
                                                var list = response.list;
                                                var cities = $('#cities');
                                                cities.html('<option disabled selected>:: انتخاب کنید ::</option>');
                                                for (var i = 0; i < list.length; i++) {
                                                    cities.append('<option value="' + list[i].id + '">' + list[i].name + '</option>');
                                                }
                                                var regions = $('#regions');
                                                regions.html('<option disabled selected>:: انتخاب کنید ::</option>');
                                                cities.selectpicker('render');
                                                cities.selectpicker('refresh');
                                                regions.selectpicker('render');
                                                regions.selectpicker('refresh');

                                            }
                                        });
                                    });
                                </script>
                            </div>{{--شهر--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="regions">منطقه:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select class="selectpicker" name="region_id" id="regions" data-live-search="true"
                                            data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        @foreach($regions as $region)
                                            <option {{ $region->id == $ads->region_id ? 'selected' : '' }} value="{{ $region->id }}">{{ $region->name }}</option>
                                        @endforeach
                                    </select>
                                    @if($errors->has('region_id'))
                                        <b class="text-danger">{{ $errors->first('region_id') }}</b>
                                    @endif
                                </div>
                                <script>
                                    $('#cities').change(function () {
                                        var $this = $(this);
                                        var cityId = $this.val();
                                        $.ajax({
                                            type: 'get',
                                            url: '{{ url()->to('/api/provinces/cities/') }}/' + cityId + '/regions',
                                            data: {},
                                            success: function (response) {
                                                var list = response.list;
                                                var cities = $('#regions');
                                                cities.html('<option disabled selected>:: انتخاب کنید ::</option>');
                                                for (var i = 0; i < list.length; i++) {
                                                    cities.append('<option value="' + list[i].id + '">' + list[i].name + '</option>');
                                                }
                                                cities.selectpicker('render');
                                                cities.selectpicker('refresh');
                                            }
                                        });
                                    });
                                </script>
                            </div>{{--منطقه--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="brand">برند:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select name="brand" id="brand" class="form-control">
                                        <option value="all">:: بدون انتخاب ::</option>
                                        @foreach($brands as $brand)
                                            <option value="{{$brand->id}}" {{ $ads->brand_id == $brand->id ? 'selected' : '' }}>{{$brand->name}}</option>
                                        @endforeach
                                    </select>
                                    @if($errors->has('brand'))
                                        <b class="text-danger">{{ $errors->first('brand') }}</b>
                                    @endif
                                </div>
                            </div>{{--برند--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="model">مدل:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select disabled name="model" id="model" class="form-control">
                                        <option value="all">:: بدون انتخاب ::</option>
                                        @if(count($models) > 0)
                                            @foreach($models as $model)
                                                <option {{ $ads->model_id == $model->id ? 'selected' : '' }} value="{{ $model->id }}">{{ $model->name }}</option>
                                            @endforeach
                                        @endif
                                    </select>
                                    @if($errors->has('brand'))
                                        <b class="text-danger">{{ $errors->first('brand') }}</b>
                                    @endif
                                </div>
                            </div>
                            <script>
                                $('#brand').change(function () {
                                    var brandId = $(this).val();
                                    if (brandId != 'all') {
                                        $.ajax({
                                            type: 'GET',
                                            url: '{{ url()->to('/api/vehicles/brands') }}/' + brandId + '/models',
                                            data: {},
                                            success: function (response) {
                                                var model = $('#model');
                                                if(response.list.length == 0 ){
                                                    model.attr('disabled' , 'disabled');
                                                }else{
                                                    model.removeAttr('disabled');
                                                    model.html('<option value="all">:: بدون انتخاب ::</option>');
                                                    for (var i = 0; i < response.list.length; i++) {
                                                        model.append('<option value="' + response.list[i].id + '">' + response.list[i].name + '</option>');
                                                    }
                                                }
                                            }
                                        });
                                    } else {
                                        $('#model').attr('disabled' , 'disabled');
                                        $('#model').html('<option value="all">:: بدون انتخاب ::</option>');
                                    }
                                });
                            </script>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="user">نام کاربر
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select class="selectpicker" name="user_id" id="user" data-live-search="true"
                                            data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        @foreach($users as $user)
                                            <option {{ $ads->user_id == $user->id ? 'selected' : '' }} value="{{ $user->id }}">{{ $user->first_name }} {{ $user->last_name }}</option>
                                        @endforeach
                                    </select>
                                    @if($errors->has('user_id'))
                                        <b class="text-danger">{{ $errors->first('user_id') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="address">آدرس:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <input type="text" value="{{ $ads->address }}" name="address" id="address"
                                           class="form-control"
                                           placeholder="آدرس را وارد کنید...">
                                    @if($errors->has('address'))
                                        <b class="text-danger">{{ $errors->first('address') }}</b>
                                    @endif
                                </div>
                            </div>{{--آدرس--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="price">قیمت :</label>
                                    <input type="number" value="{{ $ads->price }}" name="price"
                                           id="price"
                                           class="form-control">
                                    @if($errors->has('price'))
                                        <b class="text-danger">{{ $errors->first('price') }}</b>
                                    @endif
                                </div>
                            </div>{{--قیمت--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="telephone1">تلفن تماس 1:</label>
                                    <input type="number" value="{{ $ads->telephone1 }}" name="telephone1"
                                           id="telephone1"
                                           class="form-control">
                                    @if($errors->has('telephone1'))
                                        <b class="text-danger">{{ $errors->first('telephone1') }}</b>
                                    @endif
                                </div>
                            </div>{{--تلفن تماس 1--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="telephone2">تلفن تماس 2:</label>
                                    <input type="text" value="{{ $ads->telephone2 }}" name="telephone2" id="telephone2"
                                           class="form-control">
                                    @if($errors->has('telephone2'))
                                        <b class="text-danger">{{ $errors->first('telephone2') }}</b>
                                    @endif
                                </div>
                            </div>{{--تلفن تماس 2--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="status">وضعیت آگهی:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select name="status" id="status" class="form-control">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        <option {{ $ads->status == 'pending' ? 'selected' : '' }} value="pending">در
                                            انتظار تایید
                                        </option>
                                        <option {{ $ads->status == 'approved' ? 'selected' : '' }} value="approved">
                                            تایید شده
                                        </option>
                                        <option {{ $ads->status == 'rejected' ? 'selected' : '' }} value="rejected">رد
                                            شده
                                        </option>
                                    </select>
                                    @if($errors->has('status'))
                                        <b class="text-danger">{{ $errors->first('status') }}</b>
                                    @endif
                                </div>
                            </div>{{--وضعیت آگهی--}}
                            @include('admin.publish_duration_fields')
                            @include('admin.terms_acceptance_box', ['adType' => \App\Support\TermsConsent::TYPE_VEHICLE])
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="ads_owner_name">نام صاحب آگهی:</label>
                                    <input type="text" value="{{ $ads->ads_owner_name }}" name="ads_owner_name"
                                           id="ads_owner_name" class="form-control">
                                    @if($errors->has('ads_owner_name'))
                                        <b class="text-danger">{{ $errors->first('ads_owner_name') }}</b>
                                    @endif
                                </div>
                            </div>{{--نام صاحب آگهی--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="chassis_type">نوع شاسی:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select class="form-control" name="chassis_type" id="chassis_type">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        <option {{ $ads->chassis_type == 'savari' ? 'selected' : '' }} value="savari">
                                            سدان (سواری)
                                        </option>
                                        <option {{ $ads->chassis_type == 'hachback' ? 'selected' : '' }} value="hachback">
                                            هاچ بک
                                        </option>
                                        <option {{ $ads->chassis_type == 'shasiboland' ? 'selected' : '' }} value="shasiboland">
                                            شاسی بلند
                                        </option>
                                        <option {{ $ads->chassis_type == 'vanet' ? 'selected' : '' }} value="vanet">
                                            وانت
                                        </option>
                                        <option {{ $ads->chassis_type == 'krook' ? 'selected' : '' }} value="krook">
                                            کروک
                                        </option>
                                        <option {{ $ads->chassis_type == 'van' ? 'selected' : '' }} value="van">ون
                                        </option>
                                        <option {{ $ads->chassis_type == 'cupe' ? 'selected' : '' }} value="cupe">کوپه
                                        </option>
                                        <option {{ $ads->chassis_type == 'station' ? 'selected' : '' }} value="station">
                                            استیشن
                                        </option>
                                        <option {{ $ads->chassis_type == 'other' ? 'selected' : '' }} value="other">
                                            دیگر
                                        </option>
                                    </select>
                                    @if($errors->has('chassis_type'))
                                        <b class="text-danger">{{ $errors->first('chassis_type') }}</b>
                                    @endif
                                </div>
                            </div>{{--نوع شاسی--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="cylinder_volume">حجم موتور:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select name="cylinder_volume" id="cylinder_volume" class="form-control">
                                        @foreach($cylinder_volumes as $volume)
                                            <option {{ $ads->cylinder_volume === $volume->id ? 'selected' : '' }} value="{{ $volume->id }}">{{ $volume->value }}</option>
                                        @endforeach
                                    </select>
                                    @if($errors->has('cylinder_volume'))
                                        <b class="text-danger">{{ $errors->first('cylinder_volume') }}</b>
                                    @endif
                                </div>
                            </div>{{--حجم موتور--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="kilometre">کیلومتر:
                                        {{--<b class="input-field-errors">*</b>--}}
                                    </label>
                                    <input type="number" value="{{ $ads->kilometre }}" name="kilometre" id="kilometre"
                                           class="form-control">
                                    @if($errors->has('kilometre'))
                                        <b class="text-danger">{{ $errors->first('kilometre') }}</b>
                                    @endif
                                </div>
                            </div>{{--کیلومتر--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="production_year">سال تولید:
                                        {{--<b class="input-field-errors">*</b>--}}
                                    </label>
                                    <input type="number" value="{{ $ads->production_year }}" name="production_year"
                                           id="production_year"
                                           class="form-control">
                                    @if($errors->has('production_year'))
                                        <b class="text-danger">{{ $errors->first('production_year') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="neworold">نو یا کارکرده:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select class="selectpicker" id="neworold" data-live-search="true"
                                            name="neworold"
                                            data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        <option {{ $ads->neworold == 'new' ? 'selected' : '' }} value="new">نو</option>
                                        <option {{ $ads->neworold == 'old' ? 'selected' : '' }} value="old">کارکرده
                                        </option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="person-or-company-in-filter">
                                        شرکت / شخص:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select name="person_or_company" id="person-or-company-in-filter" class="form-control">
                                        <option {{ $ads->person_or_company ==  'company' ? 'selected' : ''  }} value="company">شرکت / فروشگاه</option>
                                        <option {{ $ads->person_or_company ==  'person' ? 'selected' : ''  }} value="person">شخص</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-4">
                                <div class="form-group">
                                    <label for="thumbnail_photo">تصویر بندانگشتی
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <input type="file" name="thumbnail_photo" id="thumbnail_photo" class="form-control">
                                    @if($errors->has('thumbnail_photo'))
                                        <b class="text-danger">{{ $errors->first('thumbnail_photo') }}</b>
                                    @endif
                                </div>
                            </div>{{--تصویر بندانگشتی--}}
                            <div class="col-xs-12 col-md-2">
                                <button type="button" data-toggle="modal" data-target="#thumbnail_photo_modal"
                                        style="margin-top: 26px;" class="btn btn-danger btn-block">مشاهده تصویر
                                </button>
                            </div>{{--مشاهده تصویر--}}
                        </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="description">توضیحات:</label>
                                    <textarea name="description" id="description" cols="30" rows="6"
                                              class="form-control">{{ $ads->description }}</textarea>
                                    @if($errors->has('description'))
                                        <b class="text-danger">{{ $errors->first('description') }}</b>
                                    @endif
                                </div>
                            </div>
                            {{--<div class="col-xs-12">
                                <div class="form-group">
                                    <label for="send-notification">
                                        <input type="checkbox" checked name="send_notification" id="send-notification">ارسال
                                        نوتیفیکیشن</label>

                                </div>
                            </div>--}}
                        </div>{{--توضیحات--}}
                        <div class="col-xs-12">
                            <div class="form-group">
                                <button id="remove-location"  type="button" class="btn btn-danger btn-xs">حذف Location</button>
                            </div>
                        </div>
                        @if(count($photos) > 0 )
                            <div class="row">
                                <div class="col-xs-12">
                                    <div class="ads-image-wrapper">
                                        @foreach($photos as $photo)
                                            <div class="col-xs-12 col-md-3">
                                                <div class="image-box" style="position:relative;">
                                                    <a href="{{ route('deleteVehicleAdsPhoto', $photo->id) }}" style="position:absolute;top:10px;left:10px;" class="btn btn-danger btn-xs">حذف</a>
                                                    <img  src="{{ url()->to('/ads_photo') }}/{{ $photo->file_name }}" alt="" class="img-responsive">
                                                </div>
                                            </div>
                                        @endforeach
                                    </div>
                                </div>
                            </div>
                        @endif
                        <div class="row">
                            <div class="col-xs-12">
                                @if($errors->has('latitude') || $errors->has('longitude'))
                                    <b class="text-danger">طول یا عرض جغرافیایی نامعتبر است.</b>
                                @endif
                                <div>
                                    <input style="margin-top: 5px;" id="pac-input" type="text"
                                           class="controls form-control" placeholder="اسم مکان را جستجو کنید...">
                                </div>
                                <script>
                                    $(document).on('keypress', '#pac-input', function (e) {
                                        if (e.keyCode == 13) {
                                            return false;
                                        }
                                    });
                                </script>
                                <div style="padding:20px;">
                                    <div id="map-canvas" style="height:400px;width:100%;"></div>
                                </div>
                            </div>
                            <input type="hidden" value="{{ $ads->latitude }}" name="latitude" id="latitude-in-add-new">
                            <input type="hidden" value="{{ $ads->longitude }}" name="longitude"
                                   id="longitude-in-add-new">
                            <script>
                                        @if($ads->latitude && $ads->longitude)

                                var center = {
                                        lat: {{ $ads->latitude }},
                                        lng: {{ $ads->longitude }}
                                    };
                                        @else
                                var center = {
                                        lat: 32.650588,
                                        lng: 51.666415
                                    };
                                        @endif
                                var map = new google.maps.Map(document.getElementById('map-canvas'), {
                                        center: center,
                                        zoom: 13
                                    });
                                var removeLocationButton = document.getElementById('remove-location');
                                // Create the search box and link it to the UI element.
                                var input = document.getElementById('pac-input');
                                var searchBox = new google.maps.places.SearchBox(input);
                                map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

                                // Bias the SearchBox results towards current map's viewport.
                                map.addListener('bounds_changed', function () {
                                    searchBox.setBounds(map.getBounds());
                                });
                                var markers = [];
                                // Listen for the event fired when the user selects a prediction and retrieve
                                // more details for that place.
                                searchBox.addListener('places_changed', function () {
                                    var places = searchBox.getPlaces();

                                    if (places.length == 0) {
                                        return;
                                    }

                                    // Clear out the old markers.
                                    markers.forEach(function (marker) {
                                        marker.setMap(null);
                                    });
                                    markers = [];

                                    // For each place, get the icon, name and location.
                                    var bounds = new google.maps.LatLngBounds();
                                    places.forEach(function (place) {
                                        if (!place.geometry) {
                                            console.log("Returned place contains no geometry");
                                            return;
                                        }
                                        var icon = {
                                            url: place.icon,
                                            size: new google.maps.Size(71, 71),
                                            origin: new google.maps.Point(0, 0),
                                            anchor: new google.maps.Point(17, 34),
                                            scaledSize: new google.maps.Size(25, 25)
                                        };

                                        // Create a marker for each place.
                                        markers.push(new google.maps.Marker({
                                            map: map,
                                            icon: icon,
                                            title: place.name,
                                            position: place.geometry.location
                                        }));

                                        if (place.geometry.viewport) {
                                            // Only geocodes have viewport.
                                            bounds.union(place.geometry.viewport);
                                        } else {
                                            bounds.extend(place.geometry.location);
                                        }
                                    });
                                    map.fitBounds(bounds);
                                });
                                var marker;
                                function addMarker(location) {
                                    marker = new google.maps.Marker({
                                        position: location,
                                        map: map
                                    });
                                }
                                function setMapOnAll(map) {
                                    marker.setMap(map);
                                }
                                function clearMarkers() {
                                    setMapOnAll(null);
                                }
                                function showMarkers() {
                                    setMapOnAll(map);
                                }
                                function deleteMarkers() {
                                    clearMarkers();
                                    marker = null;
                                }
                                function initMap() {
                                    @if($ads->latitude && $ads->longitude )
                                        addMarker({
                                        lat: {{ $ads->latitude }},
                                        lng: {{ $ads->longitude }}
                                    });
                                    @endif
                                    map.addListener('click', function (e) {
                                        if (marker != undefined) {
                                            clearMarkers();
                                        }
                                        marker = new google.maps.Marker({
                                            position: e.latLng,
                                            map: map
                                        });
                                        setMapOnAll(map);

                                        document.getElementById('latitude-in-add-new').value = e.latLng.lat();
                                        document.getElementById('longitude-in-add-new').value = e.latLng.lng();
                                    });
                                    removeLocationButton.addEventListener('click' , function(e){
                                        if(marker != undefined){
                                            clearMarkers();
                                        }
                                        document.getElementById('latitude-in-add-new').value = "";
                                        document.getElementById('longitude-in-add-new').value = "";
                                    });
                                }
                                window.onload = initMap;
                            </script>
                        </div>{{--طول و عرض--}}
                        <div class="row">
                            <div class="col-xs-12">
                                <button type="submit" class="btn btn-primary">ثبت آگهی</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <div id="thumbnail_photo_modal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
         aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                    <h4 class="modal-title" id="myModalLabel">تصویر بندانگشتی آگهی</h4>
                </div>
                <div class="modal-body">
                    <img src="{{ url()->to('/ads_photo') }}/{{ $ads->thumbnail_photo }}" class="img-responsive">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default waves-effect" data-dismiss="modal">خروج</button>
                    <a href="{{ route('deleteVehicleAdsThumbnailPhoto' , $ads->id) }}" class="btn btn-danger waves-effect waves-light">حذف تصویر</a>

                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div>

@endsection