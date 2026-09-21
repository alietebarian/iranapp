@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    @if ($errors->any())
                        <div class="alert alert-danger">
                            <ul>
                                @foreach ($errors->all() as $error)
                                    <li>{{ $error }}</li>
                                @endforeach
                            </ul>
                        </div>
                    @endif
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">ثبت آگهی</h1>
                    <form id="update-form-to-validate" action="{{ route('updateAdInAdminPanel' , $ads->id) }}"
                          method="post">
                        <input type="hidden" name="_method" value="PUT">
                        {{ csrf_field() }}
                        <div class="row">
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="ads-title">عنوان:</label>
                                    <input type="text" value="{{ $ads->title }}" name="title" id="ads-title"
                                           class="form-control">
                                    @if($errors->has('title'))
                                        <b class="text-danger">{{ $errors->first('title') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="province">استان:</label>
                                    <select class="selectpicker" id="province" data-live-search="true"
                                            data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        @foreach($provinces as $prv)
                                            <option {{ $prv->id == $current_province->id ? 'selected' : '' }} value="{{ $prv->id }}">{{ $prv->name }}</option>
                                        @endforeach
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="cities">شهر:</label>
                                    <select class="selectpicker" name="city_id" id="cities" data-live-search="true"
                                            data-style="btn-white">
                                        @foreach($cities as $city)
                                            <option value="{{ $city->id }}">{{ $city->name }}</option>
                                        @endforeach
                                    </select>
                                    @if($errors->has('city_id'))
                                        <b class="text-danger">{{ $errors->first('city_id') }}</b>
                                    @endif
                                </div>
                                <script>
                                    $(document).ready(function () {
                                                @php
                                                    $province = \App\Models\City::find($ads->city_id)->province;
                                                @endphp
                                        var provinceId = {{ $province->id }};
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
                                                var cityId = {{ $ads->city_id }};
                                                cities.val(cityId);
                                                cities.selectpicker('render');
                                                cities.selectpicker('refresh');
                                            }
                                        });
                                    });
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
                            </div>

                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="category">دسته بندی:</label>
                                    <select class="selectpicker" id="category" data-live-search="true"
                                            data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        @foreach($categories as $category)
                                            <option {{ $current_category->id == $category->id ? 'selected' : '' }} value="{{ $category->id }}">{{ $category->name }}</option>
                                        @endforeach
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="subcategory">زیر دسته بندی:</label>
                                    <select class="selectpicker" name="sub_category_id" id="subcategory"
                                            data-live-search="true"
                                            data-style="btn-white">
                                        @foreach($sub_categories as $subcategory)
                                            <option value="{{ $subcategory->id }}">{{ $subcategory->name }}</option>
                                        @endforeach
                                    </select>
                                    @if($errors->has('sub_category_id'))
                                        <b class="text-danger">{{ $errors->first('sub_category_id') }}</b>
                                    @endif
                                </div>
                            </div>
                            <script>
                                $(document).ready(function () {
                                            @php
                                                $category = \App\Models\SubCategory::find($ads->sub_category_id)->category;
                                            @endphp
                                    var categoryId = {{ $category->id }};
                                    $.ajax({
                                        type: 'get',
                                        url: '{{ URL::to('api/categories/') }}/' + categoryId + '/subcategories/all',
                                        data: {},
                                        success: function (response) {
                                            var list = response.list;
                                            var subcategories = $('#subcategory');
                                            subcategories.html('');
                                            for (var i = 0; i < list.length; i++) {
                                                subcategories.append('<option value="' + list[i].id + '">' + list[i].name + '</option>');
                                            }
                                            var subcategoryId = {{ $ads->sub_category_id }};
                                            subcategories.val(subcategoryId);
                                            subcategories.selectpicker('render');
                                            subcategories.selectpicker('refresh');
                                        }
                                    });
                                });
                                $('#category').change(function () {
                                    var $this = $(this);
                                    var categoryId = $this.val();
                                    $.ajax({
                                        type: 'get',
                                        url: '{{ URL::to('api/categories/') }}/' + categoryId + '/subcategories/all',
                                        data: {},
                                        success: function (response) {
                                            var list = response.list;
                                            var subcategories = $('#subcategory');
                                            subcategories.html('');
                                            for (var i = 0; i < list.length; i++) {
                                                subcategories.append('<option value="' + list[i].id + '">' + list[i].name + '</option>');
                                            }
                                            subcategories.selectpicker('render');
                                            subcategories.selectpicker('refresh');
                                        }
                                    });
                                });
                            </script>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="user">نام کاربر</label>
                                    <select name="user_id[]" id="user"
                                            class="form-control selectpicker js-example-basic-multiple"
                                            multiple="multiple" data-live-search="true">
                                        @foreach($users as $usr)
                                            <option {{ in_array($usr->id ,$user_ads) ? 'selected' : '' }} value="{{ $usr->id }}">{{ $usr->first_name }} {{ $usr->last_name }}</option>
                                        @endforeach
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="address">آدرس:</label>
                                    <input type="text" value="{{ $ads->address }}" name="address" id="address"
                                           class="form-control"
                                           placeholder="آدرس را وارد کنید...">
                                    @if($errors->has('address'))
                                        <b class="text-danger">{{ $errors->first('address') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="email">ایمیل:</label>
                                    <input type="text" value="{{ $ads->email }}" name="email" id="email"
                                           class="form-control"
                                           placeholder="ایمیل را وارد کنید...">
                                    @if($errors->has('email'))
                                        <b class="text-danger">{{ $errors->first('email') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="type">نوع آگهی:</label>
                                    <select class="form-control" name="type" id="type">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        <option {{ $ads->type == 'discount' ? 'selected' : '' }} value="discount">
                                            تخفیفات
                                        </option>
                                        <option {{ $ads->type == 'need' ? 'selected' : '' }} value="need">نیازمندی ها
                                        </option>
                                    </select>
                                    @if($errors->has('type'))
                                        <b class="text-danger">{{ $errors->first('type') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="discount-price">میزان تخفیف:</label>
                                    <input type="text" value="{{ ($ads->type == 'discount') ?  $ads->discount : ''  }}"
                                           name="discount" id="discount-price"
                                           class="form-control">
                                    @if($errors->has('discount'))
                                        <b class="text-danger">{{ $errors->first('discount') }}</b>
                                    @endif
                                </div>
                            </div>
                            <script>
                                $(document).ready(function () {
                                    var type = $('#type');
                                    if (type.val() == 'need') {
                                        $('#discount-price').attr('disabled', 'disabled');
                                    }
                                });

                                $('#type').change(function () {
                                    var $this = $(this);
                                    var price = $('#discount-price');
                                    if ($this.val() == 'discount') {
                                        price.removeAttr('disabled');
                                    } else {
                                        price.attr('disabled', 'disabled');
                                    }
                                });
                            </script>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="mobile">تلفن همراه:</label>
                                    <input type="text" value="{{ $ads->mobile }}" name="mobile" id="mobile"
                                           class="form-control">
                                    @if($errors->has('mobile'))
                                        <b class="text-danger">{{ $errors->first('mobile') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="tel1">تلفن تماس 1:</label>
                                    <input type="text" value="{{ $ads->tel1 }}" name="tel1" id="tel1"
                                           class="form-control">
                                    @if($errors->has('tel1'))
                                        <b class="text-danger">{{ $errors->first('tel1') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="tel2">تلفن تماس 2:</label>
                                    <input type="text" value="{{ $ads->tel2  }}" name="tel2" id="tel2"
                                           class="form-control">
                                    @if($errors->has('tel2'))
                                        <b class="text-danger">{{ $errors->first('tel2') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="link">لینک:</label>
                                    <input type="text" value="{{ $ads->link }}" name="link" id="link"
                                           class="form-control">
                                    @if($errors->has('link'))
                                        <b class="text-danger">{{ $errors->first('link') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="working_time">ساعت کاری:</label>
                                    <input type="text" value="{{ $ads->working_time }}" name="working_time"
                                           id="working_time" class="form-control">
                                    @if($errors->has('working_time'))
                                        <b class="text-danger">{{ $errors->first('working_time') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="telegram">آدرس تلگرام:</label>
                                    <input type="text" value="{{ $ads->telegram }}" name="telegram" id="telegram"
                                           class="form-control" placeholder="فقط آیدی، مثلاً iranapp یا @iranapp">
                                    @if($errors->has('telegram'))
                                        <b class="text-danger">{{ $errors->first('telegram') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="instagram">آدرس اینستاگرام:</label>
                                    <input type="text" value="{{ $ads->instagram }}" name="instagram" id="instagram"
                                           class="form-control" placeholder="فقط آیدی، مثلاً iranapp یا @iranapp">
                                    @if($errors->has('instagram'))
                                        <b class="text-danger">{{ $errors->first('instagram') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="status">وضعیت آگهی:</label>
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
                            </div>
                            @include('admin.terms_acceptance_box', ['adType' => \App\Support\TermsConsent::TYPE_BUSINESS])
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="ads_plan_id">پلن آگهی:</label>
                                    <select name="ads_plan_id" id="ads_plan_id" class="form-control">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        @foreach($plans as $plan)
                                            <option {{ $ads->ads_plan_id == $plan->id ? 'selected' : '' }} value="{{ $plan->id }}">{{ $plan->plan_title }}</option>
                                        @endforeach
                                    </select>
                                    @if($errors->has('ads_plan_id'))
                                        <b class="text-danger">{{ $errors->first('ads_plan_id') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="ads_owner_name">نام صاحب آگهی:</label>
                                    <input type="text" name="ads_owner_name" value="{{ $ads->ads_owner_name }}"
                                           id="ads_owner_name" class="form-control">
                                    @if($errors->has('ads_plan_id'))
                                        <b class="text-danger">{{ $errors->first('ads_plan_id') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="shaba">شماره شبا</label>
                                    <input type="text" name="shaba" value="{{ $ads->shaba }}"
                                           id="shaba" class="form-control">
                                    @if($errors->has('shaba'))
                                        <b class="text-danger">{{ $errors->first('shaba') }}</b>
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="notes">توضیحات:</label>
                                    <textarea name="notes" id="notes" cols="30" rows="6"
                                              class="form-control">{{ $ads->notes }}</textarea>
                                    @if($errors->has('notes'))
                                        <b class="text-danger">{{ $errors->first('notes') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="add-update-counter">
                                        <input type="checkbox" name="add_update_counter" id="add-update-counter">
                                        افزایش تعداد به روز رسانی آگهی</label>

                                </div>
                                <div class="form-group">
                                    <label for="send-notification-in-update">
                                        <input type="checkbox" checked name="send_notification"
                                               id="send-notification-in-update">
                                        ارسال نوتیفیکیشن
                                    </label>

                                </div>
                            </div>
                        </div>
                        <div class="col-xs-12">
                            <div class="form-group">
                                <button id="remove-location"  type="button" class="btn btn-danger btn-xs">حذف Location</button>
                            </div>
                        </div>
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
                        </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="ads-photo-wrapper">
                                    @foreach($photos as $photo)
                                        <div class="col-sm-4" style="position:relative;">
                                            <a href="{{ route('deleteAdsPhotoInAdminPanel' , $photo->id) }}"
                                               class="btn btn-danger btn-xs"
                                               style="position:absolute;top:25px;left:25px;">حذف</a>
                                            <img src="{{ URL::to('/ads_photo') }}/{{ $photo->file_name }}"
                                                 alt="ads photo"
                                                 class="img-responsive img-thumbnail" width="400"/>
                                        </div>
                                    @endforeach
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <h1 style="font-size:14px;">ویدیو آگهی</h1>
                                @if($video_url)
                                    <video src="{{ $video_url }}" controls preload="metadata"
                                           style="max-width:480px;width:100%;background:#000;"></video>
                                    <br>
                                    <a href="{{ route('deleteAdsVideoInAdminPanel' , $ads->id) }}"
                                       onclick="return confirm('ویدیو این آگهی حذف شود؟');"
                                       class="btn btn-danger btn-xs">حذف ویدیو</a>
                                @else
                                    <span class="text-muted">این آگهی ویدیو ندارد.</span>
                                @endif
                                <a href="{{ route('showAdsPhotoById' , $ads->id) }}"
                                   class="btn btn-default btn-xs">{{ $video_url ? 'جایگزینی ویدیو' : 'افزودن ویدیو' }}</a>
                            </div>
                        </div>
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
@endsection

@section('js')
    <script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/js/select2.min.js"></script>
    <script>
        $(document).ready(function () {
            $('.js-example-basic-multiple').select2();
        });
    </script>
@endsection
