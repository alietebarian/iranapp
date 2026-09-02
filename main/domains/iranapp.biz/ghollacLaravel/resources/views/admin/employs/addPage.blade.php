@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">ثبت آگهی استخدامی
                        (
                        <span class="input-field-errors">فیلد های ستاره دار الزامی هستند.</span>
                        )
                    </h1>
                    <form id="saveEmployAdsForm" action="{{ route('saveEmploysAdsInAdminPanel') }}" method="post" enctype="multipart/form-data">
                        {{ csrf_field() }}
                        <div class="row">
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
                                    <label for="specialty">تخصص:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select class="selectpicker" id="specialty" data-live-search="true" name="specialty" data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        @foreach($specialtys as $specialty)
                                            <option value="{{ $specialty->id }}">{{ $specialty->name }}</option>
                                        @endforeach
                                    </select>
                                    @if($errors->has('specialty'))
                                        <b class="text-danger">{{ $errors->first('specialty') }}</b>
                                    @endif
                                </div>
                            </div>{{--تخصص--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="agremment_type">نوع قرارداد:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select class="selectpicker" id="agremment_type" data-live-search="true" name="agremment_type" data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        <option {{ old('agremment_type') == 'tamamvaght' ? 'selected' : '' }} value="tamamvaght">تمام وقت</option>
                                        <option {{ old('agremment_type') == 'parevaght' ? 'selected' : '' }} value="parevaght">پاره وقت</option>
                                        <option {{ old('agremment_type') == 'moshaveri' ? 'selected' : '' }} value="moshaveri">مشاوره ای</option>
                                        <option {{ old('agremment_type') == 'projei' ? 'selected' : '' }} value="projei">پروژه ای</option>
                                    </select>
                                    @if($errors->has('agremment_type'))
                                        <b class="text-danger">{{ $errors->first('agremment_type') }}</b>
                                    @endif
                                </div>
                            </div>{{--نوع قرارداد--}}
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
                                    <label for="education_level">میزان تحصیلات:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select class="selectpicker" id="education_level" data-live-search="true" name="education_level" data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        <option {{ old('education_level') == 'underdiploma' ? 'selected' : '' }} value="underdiploma">زیر دیپلم</option>
                                        <option {{ old('education_level') == 'diploma' ? 'selected' : '' }} value="diploma">دیپلم</option>
                                        <option {{ old('education_level') == 'tact' ? 'selected' : '' }} value="tact">کاردانی</option>
                                        <option {{ old('education_level') == 'expertise' ? 'selected' : '' }} value="expertise">کارشناسی</option>
                                        <option {{ old('education_level') == 'masterdegree' ? 'selected' : '' }} value="masterdegree">کارشناسی ارشد</option>
                                        <option {{ old('education_level') == 'doctoral' ? 'selected' : '' }} value="doctoral">دکتری و بالاتر</option>
                                    </select>
                                    @if($errors->has('education_level'))
                                        <b class="text-danger">{{ $errors->first('education_level') }}</b>
                                    @endif
                                </div>
                            </div>{{--میزان تحصیلات--}}
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
                                            url: '{{ url()->to('/api/provinces/cities/') }}/'+ cityId +'/regions',
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
                                    <label for="address">آدرس:
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
                                    <input type="text" value="{{ old('ads_owner_name') }}" name="ads_owner_name" id="ads_owner_name" class="form-control">
                                    @if($errors->has('ads_owner_name'))
                                        <b class="text-danger">{{ $errors->first('ads_owner_name') }}</b>
                                    @endif
                                </div>
                            </div>{{--نام صاحب آگهی--}}
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group" data-toggle="buttons-radio">
                                    <label>استخدام یا آماده به کار:</label>
                                    <label class="radio-inline" for="karjoo">
                                        <input {{ old('type') == 'karjoo' ? 'checked' : '' }} id="karjoo" class="btnn" type="radio" value="karjoo" name="type">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;آماده به کار
                                    </label>
                                    <label class="radio-inline" for="forsatshoghli">
                                        <input {{ old('type') == 'forsatshoghli' ? 'checked' : '' }} id="forsatshoghli" class="btnn" value="forsatshoghli" type="radio" name="type">&nbsp;&nbsp;&nbsp;&nbsp;  استخدام
                                    </label>
                                </div>
                            </div>
                            <script>
                                $('.btnn').change(function () {
                                    var karjoo = document.getElementById('karjoo');
                                    var forsatshoghli = document.getElementById('forsatshoghli');

                                    if(karjoo.checked == true){
                                        document.getElementById("mokhtasat").style.display = "none";
                                    }else if(forsatshoghli.checked == true){
                                        document.getElementById("mokhtasat").style.display = "block";
                                    }
                                });
                            </script>
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
                                    <select name="person_or_company" id="person-or-company-in-filter" class="form-control">
                                        <option {{ old('person_or_company') ==  'company' ? 'selected' : ''  }} value="company">شرکت / فروشگاه</option>
                                        <option {{ old('person_or_company') ==  'person' ? 'selected' : ''  }} value="person">شخص</option>
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
                            <div class="row">
                                <div class="col-xs-12">
                                    <div class="form-group">
                                        <button id="remove-location" type="button" class="btn btn-danger btn-xs">حذف
                                            Location
                                        </button>
                                    </div>
                                </div>
                            </div>
                            {{--<div class="col-xs-12">
                                <div class="form-group">
                                    <label for="send-notification">
                                        <input type="checkbox" name="send_notification" id="send-notification">&nbsp;&nbsp;ارسال نوتیفیکیشن</label>
                                </div>
                            </div>--}}
                        </div>{{--توضیحات--}}
                        <div class="row" id="mokhtasat">
                            <div class="col-xs-12">
                                @if($errors->has('latitude') || $errors->has('longitude'))
                                    <b class="text-danger">طول یا عرض جغرافیایی نامعتبر است.</b>
                                @endif
                                <div>
                                    <input style="margin-top: 5px;" id="pac-input" type="text" class="controls form-control" placeholder="اسم مکان را جستجو کنید...">
                                </div>
                                <script>
                                    $(document).on('keypress' , '#pac-input' , function (e) {
                                        if(e.keyCode == 13){
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
                                var center =  {
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
                                map.addListener('bounds_changed', function() {
                                    searchBox.setBounds(map.getBounds());
                                });
                                var markers = [];
                                // Listen for the event fired when the user selects a prediction and retrieve
                                // more details for that place.
                                searchBox.addListener('places_changed', function() {
                                    var places = searchBox.getPlaces();

                                    if (places.length == 0) {
                                        return;
                                    }

                                    // Clear out the old markers.
                                    markers.forEach(function(marker) {
                                        marker.setMap(null);
                                    });
                                    markers = [];

                                    // For each place, get the icon, name and location.
                                    var bounds = new google.maps.LatLngBounds();
                                    places.forEach(function(place) {
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

                                    removeLocationButton.addEventListener('click' , function(){
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