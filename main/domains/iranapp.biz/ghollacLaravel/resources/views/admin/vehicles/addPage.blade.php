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
                    <form id="saveVehicleAdsForm" action="{{ route('saveVehicleAdsInAdminPanel') }}" method="post"
                          enctype="multipart/form-data">
                        {{ csrf_field() }}
                        <div class="row">

                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="type">انتخاب گروهبندی:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select class="selectpicker" id="type" data-live-search="true" name="type"
                                            data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        <option {{ old('type') == 'khodro' ? 'selected' : '' }} value="khodro">خودرو
                                        </option>
                                        <option {{ old('type') == 'motorcycle' ? 'selected' : '' }} value="motorcycle">
                                            موتور سیکلت
                                        </option>
                                        <option {{ old('type') == 'khodroclasic' ? 'selected' : '' }} value="khodroclasic">
                                            خودرو کلاسیک
                                        </option>
                                        <option {{ old('type') == 'khordrosorn' ? 'selected' : '' }} value="khordrosorn">
                                            خودرو سنگین و نیمه سنگین
                                        </option>
                                        <option {{ old('type') == 'lavazem' ? 'selected' : '' }} value="lavazem">لوازم
                                            وسایل نقلیه
                                        </option>
                                        <option {{ old('type') == 'other' ? 'selected' : '' }} value="other">سایر وسایل
                                            نقلیه
                                        </option>
                                    </select>
                                </div>
                            </div>
                            <script>
                                $(document).ready(function () {
                                    var type = $('#type').val();
                                    var chassis_type = $('#chassis_type');
                                    var brand_and_model = $('#brand , #model , #kilometre');
                                    var production_year = $('#production_year');
                                    var cylinder_volume = $('#cylinder_volume');
                                    if (type == 'motorcycle') {
                                        production_year.removeAttr('disabled');
                                        chassis_type.attr('disabled', 'disabled');
                                        cylinder_volume.removeAttr('disabled');
                                        brand_and_model.attr('disabled', 'disabled');
                                    } else {
                                        if (type == 'khodroclasic') {
                                            production_year.attr('disabled', 'disabled');
                                        } else if(type == 'khordrosorn'){
                                            production_year.attr('disabled' , 'disabled');
                                        } else {
                                            production_year.removeAttr('disabled');
                                        }
                                        if (type != null) {
                                            cylinder_volume.attr('disabled', 'disabled');
                                            if (type == 'khodro') {
                                                brand_and_model.removeAttr('disabled');
                                                chassis_type.removeAttr('disabled');
                                            } else {
                                                chassis_type.attr('disabled', 'disabled');
                                                brand_and_model.attr('disabled', 'disabled');
                                            }
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
                                        if ($this.val() == 'khodroclasic') {
                                            production_year.attr('disabled' , 'disabled');
                                        } else if($this.val() == 'khordrosorn'){
                                            production_year.attr('disabled' , 'disabled');
                                        } else {
                                            production_year.removeAttr('disabled');
                                        }
                                        if ($this.val() != null) {
                                            cylinder_volume.attr('disabled', 'disabled');
                                            if ($this.val() == 'khodro') {
                                                brand_and_model.removeAttr('disabled');
                                                chassis_type.removeAttr('disabled');
                                            } else {
                                                chassis_type.attr('disabled', 'disabled');
                                                brand_and_model.attr('disabled', 'disabled');
                                            }
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
                                            <option value="{{ $prv->id }}">{{ $prv->name }}</option>
                                        @endforeach
                                    </select>
                                    @if($errors->has('province_id'))
                                        <b class="text-danger">{{ $errors->first('province_id') }}</b>
                                    @endif
                                </div>
                            </div>{{--استان--}}

                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="ads-title">عنوان:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <input type="text" value="{{ old('ads_title') }}" name="ads_title" id="ads-title"
                                           class="form-control">
                                    @if($errors->has('ads_title'))
                                        <b class="text-danger">{{ $errors->first('ads_title') }}</b>
                                    @endif
                                </div>
                            </div>{{--عنولن--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="cities">شهر:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select class="selectpicker" name="city_id" id="cities" data-live-search="true"
                                            data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
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
                                                cities.html('');
                                                for (var i = 0; i < list.length; i++) {
                                                    cities.append('<option value="' + list[i].id + '">' + list[i].name + '</option>');
                                                }
                                                cities.selectpicker('render');
                                                cities.selectpicker('refresh');
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
                                    </select>
                                    @if($errors->has('city_id'))
                                        <b class="text-danger">{{ $errors->first('city_id') }}</b>
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
                                                cities.html('');
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
                                    </label>
                                    <select class="selectpicker" id="brand" data-live-search="true" name="brand"
                                            data-style="btn-white">
                                        <option value="all">:: انتخاب کنید ::</option>
                                        @foreach($brands as $brand)
                                            <option value="{{ $brand->id }}">{{ $brand->name }}</option>
                                        @endforeach
                                    </select>
                                    @if($errors->has('brand'))
                                        <b class="text-danger">{{ $errors->first('brand') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="model">مدل:
                                    </label>
                                    <select class="selectpicker" id="model" data-live-search="true" name="model"
                                            disabled
                                            data-style="btn-white">
                                        <option value="all">:: انتخاب کنید ::</option>
                                    </select>
                                    @if($errors->has('model'))
                                        <b class="text-danger">{{ $errors->first('model') }}</b>
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
                                                model.html('<option value="all">:: بدون تفاوت ::</option>');
                                                if (response.list.length == 0) {
                                                    model.attr('disabled', 'disabled');
                                                } else {
                                                    model.removeAttr('disabled');
                                                    for (var i = 0; i < response.list.length; i++) {
                                                        model.append('<option value="' + response.list[i].id + '">' + response.list[i].name + '</option>');
                                                    }
                                                    model.selectpicker('render');
                                                    model.selectpicker('refresh');
                                                }

                                            }
                                        });
                                    } else {
                                        $('#model').attr('disabled', 'disabled');
                                        $('#model').html('<option value="all">:: بدون تفاوت ::</option>');

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
                                            <option {{ old('user_id') == $user->id ? 'selected' : '' }} value="{{ $user->id }}">{{ $user->first_name }} {{ $user->last_name }}</option>
                                        @endforeach
                                    </select>
                                    @if($errors->has('user_id'))
                                        <b class="text-danger">{{ $errors->first('user_id') }}</b>
                                    @endif
                                </div>
                            </div>{{--نام کاربر--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="address">آدرس:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <input type="text" value="{{ old('address') }}" name="address" id="address"
                                           class="form-control"
                                           placeholder="آدرس را وارد کنید...">
                                    @if($errors->has('address'))
                                        <b class="text-danger">{{ $errors->first('address') }}</b>
                                    @endif
                                </div>
                            </div>{{--آدرس--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="price">قیمت:</label>
                                    <input type="number" value="{{ old('price') }}" name="price" id="price"
                                           class="form-control" placeholder="مبلغ به تومان" style="font-size: 11px;">
                                    @if($errors->has('price'))
                                        <b class="text-danger">{{ $errors->first('price') }}</b>
                                    @endif
                                </div>
                            </div>{{--قیمت--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="telephone1">تلفن تماس 1:</label>
                                    <input type="text" value="{{ old('telephone1') }}" name="telephone1" id="telephone1"
                                           class="form-control">
                                    @if($errors->has('telephone1'))
                                        <b class="text-danger">{{ $errors->first('telephone1') }}</b>
                                    @endif
                                </div>
                            </div>{{--تلفن تماس 1--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="telephone2">تلفن تماس 2:</label>
                                    <input type="text" value="{{ old('telephone2') }}" name="telephone2" id="telephone2"
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
                                        <option {{ old('status') == 'pending' ? 'selected' : '' }} value="pending">در
                                            انتظار تایید
                                        </option>
                                        <option {{ old('status') == 'approved' ? 'selected' : '' }} value="approved">
                                            تایید شده
                                        </option>
                                        <option {{ old('status') == 'rejected' ? 'selected' : '' }} value="rejected">رد
                                            شده
                                        </option>
                                    </select>
                                    @if($errors->has('status'))
                                        <b class="text-danger">{{ $errors->first('status') }}</b>
                                    @endif
                                </div>
                            </div>{{--وضعیت آگهی--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="ads_owner_name">نام صاحب آگهی:</label>
                                    <input type="text" value="{{ old('ads_owner_name') }}" name="ads_owner_name"
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
                                    <select class="selectpicker" id="chassis_type" data-live-search="true"
                                            name="chassis_type" data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        <option {{ old('chassis_type') == 'savari' ? 'selected' : '' }} value="savari">
                                            سدان (سواری)
                                        </option>
                                        <option {{ old('chassis_type') == 'hachback' ? 'selected' : '' }} value="hachback">
                                            هاچ بک
                                        </option>
                                        <option {{ old('chassis_type') == 'shasiboland' ? 'selected' : '' }} value="shasiboland">
                                            شاسی بلند
                                        </option>
                                        <option {{ old('chassis_type') == 'vanet' ? 'selected' : '' }} value="vanet">
                                            وانت
                                        </option>
                                        <option {{ old('chassis_type') == 'krook' ? 'selected' : '' }} value="krook">
                                            کروک
                                        </option>
                                        <option {{ old('chassis_type') == 'van' ? 'selected' : '' }} value="van">ون
                                        </option>
                                        <option {{ old('chassis_type') == 'cupe' ? 'selected' : '' }} value="cupe">
                                            کوپه
                                        </option>
                                        <option {{ old('chassis_type') == 'station' ? 'selected' : '' }} value="station">
                                            استیشن
                                        </option>
                                        <option {{ old('chassis_type') == 'other' ? 'selected' : '' }} value="other">
                                            دیگر
                                        </option>
                                    </select>
                                </div>
                            </div>{{--نوع شاسی--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="cylinder_volume">حجم موتور:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select name="cylinder_volume" id="cylinder_volume" class="form-control">
                                        <option value="all">:: بدون انتخاب ::</option>
                                        @foreach($cylinder_volumes as $volume)
                                            <option value="{{ $volume->id }}">{{ $volume->value }}</option>
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
                                    <input type="number" value="{{ old('kilometre') }}" name="kilometre" id="kilometre"
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
                                    <input type="number" value="{{ old('production_year') }}" name="production_year"
                                           id="production_year"
                                           class="form-control">
                                    @if($errors->has('production_year'))
                                        <b class="text-danger">{{ $errors->first('production_year') }}</b>
                                    @endif
                                </div>
                            </div>{{--سال تولید--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="neworold">نو یا کارکرده:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select class="selectpicker" id="neworold" data-live-search="true"
                                            name="neworold"
                                            data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        <option {{ old('neworold') == 'new' ? 'selected' : '' }} value="new">نو</option>
                                        <option {{ old('neworold') == 'old' ? 'selected' : '' }} value="old">کارکرده
                                        </option>
                                    </select>
                                </div>
                            </div>{{--نو یا کارکرده--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="thumbnail_photo">تصویر بندانگشتی
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <input type="file" name="thumbnail_photo" id="thumbnail_photo" class="form-control">
                                    @if($errors->has('thumbnail_photo'))
                                        <b class="text-danger">{{ $errors->first('thumbnail_photo') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="person-or-company-in-filter">
                                        شرکت / شخص:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select name="person_or_company" id="person-or-company-in-filter"
                                            class="form-control">
                                        <option {{ old('person_or_company') ==  'company' ? 'selected' : ''  }} value="company">
                                            شرکت / فروشگاه
                                        </option>
                                        <option {{ old('person_or_company') ==  'person' ? 'selected' : ''  }} value="person">
                                            شخص
                                        </option>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="description">توضیحات:</label>
                                    <textarea name="description" id="description" cols="30" rows="6"
                                              class="form-control">{{ old('description') }}</textarea>
                                    @if($errors->has('description'))
                                        <b class="text-danger">{{ $errors->first('description') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <button id="remove-location"  type="button" class="btn btn-danger btn-xs">حذف Location</button>
                                </div>
                            </div>
                            {{--<div class="col-xs-12">--}}
                            {{--<div class="form-group">--}}
                            {{--<label for="send-notification">--}}
                            {{--<input type="checkbox" name="send_notification" id="send-notification">&nbsp;&nbsp;ارسال--}}
                            {{--نوتیفیکیشن</label>--}}
                            {{--</div>--}}
                            {{--</div>--}}
                        </div>{{--توضیحات--}}
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
                            <input type="hidden" value="{{ old('latitude') }}" name="latitude" id="latitude-in-add-new">
                            <input type="hidden" value="{{ old('longitude') }}" name="longitude"
                                   id="longitude-in-add-new">
                            <script>
                                        @if(old('latitude') && old('longitude'))

                                var center = {
                                        lat: {{ old('latitude') }},
                                        lng: {{ old('longitude') }}
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
                                    @if(old('latitude') && old('longitude'))
                                        addMarker({
                                        lat: {{ old('latitude') }},
                                        lng: {{ old('longitude') }}
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
                        </div>{{--طول و عرض جغرافیایی--}}
                        <div class="row">
                            <div class="col-xs-12">
                                <button type="submit" class="btn btn-primary">ثبت آگهی</button>
                            </div>
                        </div>{{--ثبت آگهی--}}
                    </form>
                </div>
            </div>
        </div>
    </div>
@endsection