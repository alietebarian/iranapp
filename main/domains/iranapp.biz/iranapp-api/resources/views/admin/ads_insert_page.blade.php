@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">ثبت آگهی</h1>
                    <form id="form-to-validate" action="{{ route('saveAdsInAdminPanel') }}" method="post">
                        <div class="row">
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="ads-title">عنوان:</label>
                                    <input type="text" value="{{ old('title') }}" name="title" id="ads-title"
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
                                            name="province_id"
                                            data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        @foreach($provinces as $prv)
                                            <option value="{{ $prv->id }}">{{ $prv->name }}</option>
                                        @endforeach
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="cities">شهر:</label>
                                    <select class="selectpicker" name="city_id" id="cities" data-live-search="true"
                                            data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                    </select>
                                    @if($errors->has('city_id'))
                                        <b class="text-danger">{{ $errors->first('city_id') }}</b>
                                    @endif
                                </div>
                                <script>
                                    // Cities come back sorted by name, so without this the alphabetically first
                                    // city was preselected; the province's same-name center (e.g. اصفهان) is the default.
                                    function normalizePlaceName(name) {
                                        return String(name)
                                            .replace(/ي/g, 'ی')
                                            .replace(/ك/g, 'ک')
                                            .replace(/^\s*استان\s+/, '')
                                            .replace(/\s+/g, ' ')
                                            .trim();
                                    }

                                    $('#province').change(function () {
                                        var $this = $(this);
                                        var provinceId = $this.val();
                                        var provinceName = normalizePlaceName($this.find('option:selected').text());
                                        $.ajax({
                                            type: 'get',
                                            url: '{{ URL::to('api/provinces/') }}/' + provinceId + '/cities',
                                            data: {},
                                            success: function (response) {
                                                var list = response.list;
                                                var cities = $('#cities');
                                                var defaultCityId = null;
                                                cities.html('');
                                                for (var i = 0; i < list.length; i++) {
                                                    cities.append('<option value="' + list[i].id + '">' + list[i].name + '</option>');
                                                    if (defaultCityId === null && normalizePlaceName(list[i].name) === provinceName) {
                                                        defaultCityId = list[i].id;
                                                    }
                                                }
                                                if (defaultCityId !== null) {
                                                    cities.val(defaultCityId);
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
                                            name="category_id"
                                            data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        @foreach($categories as $category)
                                            <option value="{{ $category->id }}">{{ $category->name }}</option>
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
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                    </select>
                                    @if($errors->has('sub_category_id'))
                                        <b class="text-danger">{{ $errors->first('sub_category_id') }}</b>
                                    @endif
                                </div>
                            </div>
                            <script>
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
                                    <select name="user_id[]" id="user" class="form-control selectpicker" multiple="multiple" data-live-search="true">
                                        {{--@foreach($users as $user)--}}
                                            {{--<option value="{{ $user->id }}">{{ $user->first_name }} {{ $user->last_name }}</option>--}}
                                        {{--@endforeach--}}
                                    </select>
                                    @if($errors->has('user_id'))
                                        <b class="text-danger">{{ $errors->first('user_id') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="address">آدرس:</label>
                                    <input type="text" value="{{ old('address') }}" name="address" id="address"
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
                                    <input type="text" value="{{ old('email') }}" name="email" id="email"
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
                                        <option {{ old('type') == 'discount' ? 'selected' : '' }} value="discount">
                                            تخفیفات
                                        </option>
                                        <option {{ old('type') == 'need' ? 'selected' : '' }} value="need">نیازمندی ها
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
                                    <input type="text" value="{{ old('discount') }}" name="discount" id="discount-price"
                                           class="form-control">
                                    @if($errors->has('discount'))
                                        <b class="text-danger">{{ $errors->first('discount') }}</b>
                                    @endif
                                </div>
                            </div>
                            <script>
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
                                    <input type="text" value="{{ old('mobile') }}" name="mobile" id="mobile"
                                           class="form-control">
                                    @if($errors->has('mobile'))
                                        <b class="text-danger">{{ $errors->first('mobile') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="tel1">تلفن تماس 1:</label>
                                    <input type="text" value="{{ old('tel1') }}" name="tel1" id="tel1"
                                           class="form-control">
                                    @if($errors->has('tel1'))
                                        <b class="text-danger">{{ $errors->first('tel1') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="tel2">تلفن تماس 2:</label>
                                    <input type="text" value="{{ old('tel2') }}" name="tel2" id="tel2"
                                           class="form-control">
                                    @if($errors->has('tel2'))
                                        <b class="text-danger">{{ $errors->first('tel2') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="link">لینک:</label>
                                    <input type="text" value="{{ old('link') }}" name="link" id="link"
                                           class="form-control">
                                    @if($errors->has('link'))
                                        <b class="text-danger">{{ $errors->first('link') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="working_time">ساعت کاری:</label>
                                    <input type="text" value="{{ old('working_time') }}" name="working_time"
                                           id="working_time" class="form-control">
                                    @if($errors->has('working_time'))
                                        <b class="text-danger">{{ $errors->first('working_time') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="telegram">آدرس تلگرام:</label>
                                    <input type="text" value="{{ old('telegram') }}" name="telegram" id="telegram"
                                           class="form-control">
                                    @if($errors->has('telegram'))
                                        <b class="text-danger">{{ $errors->first('telegram') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="instagram">آدرس اینستاگرام:</label>
                                    <input type="text" value="{{ old('instagram') }}" name="instagram" id="instagram"
                                           class="form-control">
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
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="ads_plan_id">پلن آگهی:</label>
                                    <select name="ads_plan_id" id="ads_plan_id" class="form-control">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        @foreach($plans as $plan)
                                            <option {{ old('ads_plan_id') == $plan->id ? 'selected' : '' }} value="{{ $plan->id }}">{{ $plan->plan_title }}</option>
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
                                    <input type="text" name="ads_owner_name" id="ads_owner_name" class="form-control">
                                    @if($errors->has('ads_plan_id'))
                                        <b class="text-danger">{{ $errors->first('ads_plan_id') }}</b>
                                    @endif
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="notes">توضیحات:</label>
                                    <textarea name="notes" id="notes" cols="30" rows="6"
                                              class="form-control">{{ old('notes') }}</textarea>
                                    @if($errors->has('notes'))
                                        <b class="text-danger">{{ $errors->first('notes') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="send-notification">
                                        <input type="checkbox" checked name="send_notification" id="send-notification">ارسال نوتیفیکیشن</label>

                                </div>
                            </div>
                        </div>
                        <div class="row">
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
                                }
                                window.onload = initMap;
                            </script>
                        </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <p class="text-muted">پس از ثبت، به صفحه‌ی تصاویر و ویدیو آگهی منتقل می‌شوید تا فایل‌ها را آپلود کنید.</p>
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
        $(document).ready(function() {
            $('.js-example-basic-multiple').select2();
        });

        $('#user').select2({
            placeholder: 'نام کاربر را انتخاب کنید',
            ajax: {
                url: '{{ url()->to('/admin/ajax/users') }}',
                dataType: 'json',
                delay: 250,
                data: function (params) {
                    var query = {
                        q: params.term,
                    }
                    return query;
                },
                processResults: function (data) {
                    console.log(data);
                    return {

                        results: $.map(data, function (item) {
                            return {
                                text: item.first_name + ' ' + item.last_name,
                                id: item.id
                            }
                        })
                    };
                },
                cache: true
            }
        });
    </script>
@endsection