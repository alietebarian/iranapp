@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">فیلتر</h1>
                    <form action="{{ url()->current() }}" method="get">
                        <div class="row">
                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label for="province_in_filter">استان:</label>
                                    <select data-live-search="true" name="province_id" id="province_in_filter"
                                            class="form-control selectpicker btn-default">
                                        <option {{ request()->input('province_id') == 'all' ? 'selected' : '' }} value="all">
                                            :: بدون انتخاب ::
                                        </option>
                                        @foreach($provinces as $prv)
                                            <option {{ request()->input('province_id') == $prv->id ? 'selected' : '' }} value="{{ $prv->id }}">{{ $prv->name  }}</option>
                                        @endforeach
                                    </select>
                                </div>
                            </div>{{--استان--}}
                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label for="city_in_filter">شهر:</label>
                                    <select data-live-search="true" name="city_id" id="city_in_filter"
                                            class="form-control selectpicker btn-default">
                                        <option value="all">:: انتخاب کنید ::</option>
                                    </select>
                                </div>
                            </div>{{--شهر--}}
                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label for="region_in_filter">منطقه:</label>
                                    <select data-live-search="true" name="region_id" id="region_in_filter"
                                            class="form-control selectpicker btn-default">
                                        <option value="all">:: انتخاب کنید ::</option>
                                    </select>
                                </div>
                            </div>{{--منطقه--}}
                            <script>
                                function getCitiesByProvinceId(provinceId, defaultCity) {
                                    $.ajax({
                                        type: 'get',
                                        url: '{{ url()->to('/api/provinces/') }}/' + provinceId + '/cities',
                                        success: function (response) {
                                            var citySelect = $('#city_in_filter');
                                            citySelect.html('<option value="all">:: انتخاب کنید ::</option>');
                                            var list = response.list;
                                            for (var i = 0; i < list.length; i++) {
                                                citySelect.append('<option value="' + list[i].id + '">' + list[i].name + '</option>');
                                            }
                                            citySelect.selectpicker('render');
                                            citySelect.selectpicker('refresh');
                                            if (defaultCity != null) {
                                                citySelect.val(defaultCity).trigger('change');
                                            }

                                        }
                                    });
                                }

                                function getRegionsByCityId(cityId, defaultRegion) {
                                    $.ajax({
                                        type: 'get',
                                        url: '{{ url()->to('/api/provinces/cities/') }}/' + cityId + '/regions',
                                        success: function (response) {
                                            var regionsSelect = $('#region_in_filter');
                                            regionsSelect.html('<option value="all">:: انتخاب کنید ::</option>');
                                            var list = response.list;
                                            for (var i = 0; i < list.length; i++) {
                                                regionsSelect.append('<option value="' + list[i].id + '">' + list[i].name + '</option>');
                                            }
                                            regionsSelect.selectpicker('render');
                                            regionsSelect.selectpicker('refresh');
                                            if (defaultRegion != null) {
                                                regionsSelect.val(defaultRegion).trigger('change');
                                            }
                                        }
                                    });
                                }

                                $('#province_in_filter').change(function () {
                                    var provinceId = $(this).val();
                                    getCitiesByProvinceId(provinceId, null);
                                });
                                $('#city_in_filter').change(function () {
                                    var cityId = $(this).val();

                                    @if(request()->has('region_id') && request()->input('region_id') != 'all' )
                                        getRegionsByCityId(cityId, {{ request()->input('region_id') }});
                                    @else
                                        getRegionsByCityId(cityId, null);
                                    @endif
                                });
                                $(document).ready(function () {
                                            @if( request()->has('province_id') && request()->input('province_id') != 'all')
                                    var provinceId = {{ request()->input('province_id') }};
                                            @if(request()->has('city_id') && request()->input('city_id') != 'all')
                                    var cityId = {{ request()->input('city_id') }};
                                    getCitiesByProvinceId(provinceId, cityId);
                                    @else
                                        getCitiesByProvinceId(provinceId, null);
                                    @endif
                                    @endif
                                });
                            </script>
                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label for="type">نوع</label>
                                    <select name="type" id="type" class="form-control">
                                        <option value="all">:: بدون تفاوت ::</option>
                                        <option {{ request()->input('type') == 'khodro' ? 'selected' : '' }} value="khodro">
                                            خودرو
                                        </option>
                                        <option {{ request()->input('type') == 'motorcycle' ? 'selected' : '' }} value="motorcycle">
                                            موتور سیکلت
                                        </option>
                                        <option {{ request()->input('type') == 'khodroclasic' ? 'selected' : '' }} value="khodroclasic">
                                            خودرو کلاسیک
                                        </option>
                                        <option {{ request()->input('type') == 'khordrosorn' ? 'selected' : '' }} value="khordrosorn">
                                            خودرو سنگین و نیمه سنگین
                                        </option>
                                        <option {{ request()->input('type') == 'lavazem' ? 'selected' : '' }} value="lavazem">
                                            لوازم وسایل نقلیه
                                        </option>
                                        <option {{ request()->input('type') == 'other' ? 'selected' : '' }} value="other">
                                            سایر وسایل نقلیه
                                        </option>
                                    </select>
                                </div>
                            </div>{{--نوع--}}
                            <script>
                                $('#type').change(function () {
                                    var chassis_type = $('#chassis_type  , #brand ,#model , #production_year , #kilometre');
                                    var cylinder_volume = $('#cylinder_volume');
                                    var $this = $(this);
                                    if ($this.val() == 'motorcycle') {
//                                        window.alert('hi');
                                        chassis_type.attr('disabled', 'disabled');
                                        cylinder_volume.removeAttr('disabled');
                                    } else if ($this.val() == 'khodro') {
                                        cylinder_volume.attr('disabled', 'disabled');
                                        chassis_type.removeAttr('disabled');
                                    }
                                });
                            </script>
                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label for="brand">برند:</label>
                                    <select name="brand" id="brand" class="form-control">
                                        <option value="all">:: بدون تفاوت ::</option>
                                        @foreach($brands as $brand)
                                            <option {{ request()->input('brand') == $brand->id ? 'selected' : '' }} value="{{ $brand->id }}">{{ $brand->name }}</option>
                                        @endforeach
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label for="model">مدل:</label>
                                    <select name="model" id="model" class="form-control">
                                        <option value="all">:: بدون تفاوت ::</option>
                                    </select>
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
                                                for (var i = 0; i < response.list.length; i++) {
                                                    model.append('<option value="' + response.list[i].id + '">' + response.list[i].name + '</option>');
                                                }
                                            }
                                        });
                                    } else {
                                        $('#model').html('<option value="all">:: بدون تفاوت ::</option>');
                                    }

                                });


                                $(document).ready(function () {
                                    var brandId = $('#brand').val();
                                    if (brandId != 'all') {
                                        $.ajax({
                                            type: 'GET',
                                            url: '{{ url()->to('/api/vehicles/brands') }}/' + brandId + '/models',
                                            data: {},
                                            success: function (response) {
                                                var model = $('#model');
                                                model.html('<option value="all">:: بدون تفاوت ::</option>');
                                                for (var i = 0; i < response.list.length; i++) {
                                                    model.append('<option value="' + response.list[i].id + '">' + response.list[i].name + '</option>');
                                                }
                                                @if(request()->has('model'))
                                                    model.val({{ request()->input('model') }});
                                                @endif
                                            }
                                        });
                                    }
                                });
                            </script>
                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label for="chassis_type">نوع شاسی:</label>
                                    <select name="chassis_type" id="chassis_type" class="form-control">
                                        <option value="all">:: بدون تفاوت ::</option>
                                        <option {{ request()->input('chassis_type') == 'savari' ? 'selected' : '' }} value="savari">
                                            سدان (سواری)
                                        </option>
                                        <option {{ request()->input('chassis_type') == 'hachback' ? 'selected' : '' }} value="hachback">
                                            هاچ بک
                                        </option>
                                        <option {{ request()->input('chassis_type') == 'shasiboland' ? 'selected' : '' }} value="shasiboland">
                                            شاسی بلند
                                        </option>
                                        <option {{ request()->input('chassis_type') == 'vanet' ? 'selected' : '' }} value="vanet">
                                            وانت
                                        </option>
                                        <option {{ request()->input('chassis_type') == 'krook' ? 'selected' : '' }} value="krook">
                                            کروک
                                        </option>
                                        <option {{ request()->input('chassis_type') == 'van' ? 'selected' : '' }} value="van">
                                            ون
                                        </option>
                                        <option {{ request()->input('chassis_type') == 'cupe' ? 'selected' : '' }} value="cupe">
                                            کوپه
                                        </option>
                                        <option {{ request()->input('chassis_type') == 'station' ? 'selected' : '' }} value="station">
                                            استیشن
                                        </option>
                                        <option {{ request()->input('chassis_type') == 'other' ? 'selected' : '' }} value="other">
                                            دیگر
                                        </option>
                                    </select>
                                </div>
                            </div>{{--نوع شاسی--}}
                            <div class="col-xs-6 col-md-2">
                                <div class="form-group">
                                    <label for="cylinder_volume">حجم موتور:</label>
                                    <select class="form-control" name="cylinder_volume" id="cylinder_volume">
                                        <option value="all">:: بدون انتخاب ::</option>
                                        @foreach($cylinder_volumes as $volume)
                                            <option {{ $volume->id == request()->input('cylinder_volume') ? 'selected' : '' }} value="{{ $volume->id }}">{{ $volume->value }}</option>
                                        @endforeach
                                    </select>
                                </div>
                            </div>{{--حجم موتور--}}
                            <div class="col-xs-6 col-md-2">
                                <div class="form-group">
                                    <label for="price" data-toggle="tooltip"
                                           title="در صورت توافقی بودن قیمت آن را صفر وارد کنید.">قیمت:</label>
                                    <input value="{{ request()->input('price') }}" type="number" name="price" id="price"
                                           class="form-control">
                                </div>
                            </div>{{--قیمت--}}
                            <div class="col-xs-6 col-md-2">
                                <div class="form-group">
                                    <label for="production_year">سال تولید:</label>
                                    <input value="{{ request()->input('production_year') }}" type="number"
                                           name="production_year" id="production_year" class="form-control">
                                </div>
                            </div>{{--سال تولید--}}
                            <div class="col-xs-6 col-md-2">
                                <div class="form-group">
                                    <label for="kilometre">کیلومتر:</label>
                                    <input value="{{ request()->input('kilometre') }}" type="number" name="kilometre"
                                           id="kilometre" class="form-control">
                                </div>
                            </div>{{--کیلومتر--}}
                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label for="neworold">نو یا کارکرده:</label>
                                    <select name="neworold" id="neworold" class="form-control">
                                        <option value="all">:: بدون تفاوت ::</option>
                                        <option {{ request()->input('neworold') == 'new' ? 'selected' : '' }} value="new">
                                            نو
                                        </option>
                                        <option {{ request()->input('neworold') == 'old' ? 'selected' : '' }} value="old">
                                            کارکرده
                                        </option>
                                    </select>
                                </div>
                            </div>{{--نو یا کارکرده--}}
                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label for="status-in-filter">وضعیت </label>
                                    <select name="status" id="status-in-filter" class="form-control">
                                        <option {{ ( !request()->has('status') || request()->input('status')) == 'all' ? 'selected' : '' }} value="all">
                                            بدون انتخاب
                                        </option>
                                        <option {{ request()->input('status') == 'approved' ? 'selected' : '' }} value="approved">
                                            تایید شده
                                        </option>
                                        <option {{ request()->input('status') == 'rejected' ? 'selected'  : '' }} value="rejected">
                                            رد شده
                                        </option>
                                        <option {{ request()->input('status') == 'pending' ? 'selected' : '' }} value="pending">
                                            در انتظار تایید
                                        </option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label for="person-or-company-in-filter">شرکت / شخص</label>
                                    <select name="person_or_company" id="person-or-company-in-filter" class="form-control">
                                        <option {{ request()->input('person_or_company') == 'all' ? 'selected' : '' }} value="all">:: بدون انتخاب ::</option>
                                        <option {{ request()->input('person_or_company') == 'company' ? 'selected' : '' }} value="company">شرکت / فروشگاه</option>
                                        <option {{ request()->input('person_or_company') == 'person' ? 'selected' : '' }} value="person">شخص</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="keyword-in-filter">کلمه کلیدی:</label>
                                    <input value="{{ request()->input('keyword') }}" type="text"
                                           placeholder="عنوان یا بخشی از توضیحات آگهی را وارد کرده و جستجو کنید ..."
                                           name="keyword" id="keyword-in-filter" class="form-control">
                                </div>
                            </div>
                        </div>{{--کلمه کلیدی--}}
                        <div class="row">
                            <div class="col-xs-12">
                                <button type="submit" class="btn btn-success">فیلتر</button>
                            </div>
                        </div>{{--فیلتر--}}
                    </form>
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">لیست آگهی ها</h1>
                    <div class="table-responsive">
                        @if(count($ads) > 0 )
                            <table class="table m-0">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>عنوان آگهی</th>
                                    <th>وضعیت آگهی</th>
                                    <th>نوع</th>
                                    <th>برند</th>
                                    <th>نوع شاسی</th>
                                    <th>حجم موتور</th>
                                    <th>قیمت</th>
                                    <th>سال تولید</th>
                                    <th>کیلومتر</th>
                                    <th>نوع یا کارکرده</th>
                                    <th>وضعیت اعتبار</th>
                                    <th>ثبت کننده</th>
                                    {{--<th><i class="fa fa-thumbs-up text-primary"></i></th>
                                    <th><i class="fa fa-thumbs-down text-danger"></i></th>--}}
                                    <th>تنظیمات</th>
                                </tr>
                                </thead>
                                <tbody>
                                @foreach($ads as $ad)
                                    <tr>
                                        <td>{{ $ad->id }}</td>
                                        <td>{{ $ad->ads_title }}</td>
                                        <td>
                                            @if($ad->status == 'pending')
                                                <span class="text-warning">در انتظار تایید</span>
                                            @elseif($ad->status == 'approved')
                                                <span class="text-success">تایید شده</span>
                                            @elseif($ad->status == 'rejected')
                                                <span class="text-danger">رد شده</span>
                                            @endif
                                        </td>{{--وضعیت--}}
                                        <td>
                                            @if($ad->type == 'khodro')
                                                <span>خودرو</span>
                                            @elseif($ad->type == 'motorcycle')
                                                <span>موتور سیکلت</span>
                                            @elseif($ad->type == 'khodroclasic')
                                                <span>خودرو کلاسیک</span>
                                            @elseif($ad->type == 'khordrosorn')
                                                <span>خودرو سنگین و نیمه سنگین</span>
                                            @elseif($ad->type == 'lavazem')
                                                <span>لوازم وسایل نقلیه</span>
                                            @elseif($ad->type == 'other')
                                                <span>سایر وسایل نقلیه</span>
                                            @endif
                                        </td>{{--نوع--}}
                                        <td>
                                            <span>{{ $ad->brand }}</span>
                                        </td>{{--برند--}}
                                        <td>
                                            @if($ad->chassis_type == 'savari')
                                                <span>سواری</span>
                                            @elseif($ad->chassis_type == 'hachback')
                                                <span>هاچ بک</span>
                                            @elseif($ad->chassis_type == 'shasiboland')
                                                <span>شاسی بلند</span>
                                            @elseif($ad->chassis_type == 'vanet')
                                                <span>وانت</span>
                                            @elseif($ad->chassis_type == 'krook')
                                                <span>کروک</span>
                                            @elseif($ad->chassis_type == 'van')
                                                <span>ون</span>
                                            @elseif($ad->chassis_type == 'cupe')
                                                <span>کوپه</span>
                                            @elseif($ad->chassis_type == 'station')
                                                <span>استیشن</span>
                                            @elseif($ad->chassis_type == 'other')
                                                <span>دیگر</span>
                                            @endif
                                        </td>{{--نوع شاسی--}}
                                        <td>{{ $ad->cylinder_volume }}</td>{{--حجم موتور--}}
                                        <td>{{ $ad->price }}</td>{{--قیمت--}}
                                        <td>{{ $ad->production_year }}</td>{{--سال تولید--}}
                                        <td>{{ $ad->kilometre }}</td>{{--کیلومتر--}}
                                        <td>
                                            @if($ad->neworold == 'new')
                                                <span>نو</span>
                                            @elseif($ad->neworold == 'old')
                                                <span>کارکرده</span>
                                            @endif
                                        </td>{{--نو یا کارکرده--}}
                                        <td>
                                            @if(\Carbon\Carbon::createFromFormat('Y-m-d' , $ad->valid_until)->getTimestamp() > \Carbon\Carbon::now()->getTimestamp())
                                                <span class="text-success">
                                                معتبر تا تاریخ
                                                    {{ \App\Libraries\jdf::jdate('j-F-Y' , \Carbon\Carbon::createFromFormat('Y-m-d' , $ad->valid_until)->getTimestamp()) }}
                                                    </span>
                                            @else
                                                <span class="text-danger">
                                                منقضی شده
                                                </span>
                                            @endif
                                        </td>{{--وضعیت اعتبار--}}
                                        <td>{{ $ad->user_first_name }} {{ $ad->user_last_name }}</td>{{--ثبت کننده--}}
                                        <td>
                                            <a href="{{ route('vehicle.ads.delete' , $ad->id) }}"
                                               class="text-primary">حذف آگهی</a><br>
                                            <a href="{{ route('vehicleAds.update.show' , $ad->id) }}"
                                               class="text-primary">مشاهده / ویرایش آگهی</a><br>
                                            <a href="{{ route('uploadVehicleAdsPhotoInAdminPanel' , $ad->id) }}"
                                               class="text-primary">تصاویر آگهی</a>
                                            <br>
                                            @php
                                                $vipExists = \App\Models\VipAds::where('ads_id' , $ad->id)->count();
                                            if($vipExists){
                                            $vip = \App\Models\VipAds::where('ads_id' , $ad->id)->first();
                                            }
                                            @endphp
                                            @if($vipExists)
                                                <a href="{{ route('showVipAdsUpdatePage' , $vip->id) }}"
                                                   class="text-primary">ویرایش آگهی ویژه</a>
                                            @endif
                                        </td>{{--تنظیمات--}}
                                    </tr>
                                @endforeach
                                </tbody>
                            </table>
                        @else
                            <div class="alert alert-success text-center">آگهی یافت نشد!</div>
                        @endif
                    </div>
                    @if(count($ads) > 0 )
                        <ul class="pagination pagination-split">
                            @if($ads->currentPage() != 1)
                                <li>
                                    <a href="{{ route('showAllVehicleAdsInAdminPanel' , [
                                    'page' => $ads->currentPage() - 1,
                                    'province_id' => request()->input('province_id'),
                                    'city_id' => request()->input('city_id'),
                                    'region_id' => request()->input('region_id'),
                                    'type' => request()->input('type'),
                                    'brand' => request()->input('brand'),
                                    'model' => request()->input('model'),
                                    'chassis_type' => request()->input('chassis_type'),
                                    'cylinder_volume' => request()->input('cylinder_volume'),
                                    'price' => request()->input('price'),
                                    'production_year' => request()->input('production_year'),
                                    'kilometre' => request()->input('kilometre'),
                                    'neworold' => request()->input('neworold'),
                                    'status' => request()->input('status'),
                                    'keyword' => request()->input('keyword')
                                    ]) }}"><i class="fa fa-angle-left"></i></a>
                                </li>
                            @endif
                            @php
                                $ellipsisWrote = false;
                            @endphp
                            @for($i = 1 ; $i <= $ads->lastPage() ; $i++)
                                @if( ($i >= 1 && $i <=3) || ( $i <= $ads->lastPage() && $i >= $ads->lastPage() - 2) || ($i >= $ads->currentPage() && $i <= $ads->currentPage() + 2) || ($i <= $ads->currentPage() &&  $i >= $ads->currentPage() - 2) )
                                    <li class="{{ $i == $ads->currentPage()  ? 'active' : '' }}">
                                        <a href="{{ route('showAllVehicleAdsInAdminPanel' , [
                                    'page' => $i,
                                    'province_id' => request()->input('province_id'),
                                    'city_id' => request()->input('city_id'),
                                    'region_id' => request()->input('region_id'),
                                    'type' => request()->input('type'),
                                    'brand' => request()->input('brand'),
                                    'model' => request()->input('model'),
                                    'chassis_type' => request()->input('chassis_type'),
                                    'cylinder_volume' => request()->input('cylinder_volume'),
                                    'price' => request()->input('price'),
                                    'production_year' => request()->input('production_year'),
                                    'kilometre' => request()->input('kilometre'),
                                    'neworold' => request()->input('neworold'),
                                    'status' => request()->input('status'),
                                    'keyword' => request()->input('keyword')

                                    ]) }}">{{ $i }}</a>
                                    </li>
                                @else
                                    @if(!$ellipsisWrote)
                                        <li>
                                            <a>...</a>
                                        </li>
                                        @php
                                            $ellipsisWrote = true;
                                        @endphp
                                    @endif
                                @endif
                            @endfor
                            @if($ads->hasMorePages())
                                <li>
                                    <a href="{{ route('showAllVehicleAdsInAdminPanel' , [
                                    'page' => $ads->currentPage() + 1,
                                                                        'province_id' => request()->input('province_id'),
                                    'city_id' => request()->input('city_id'),
                                    'region_id' => request()->input('region_id'),
                                    'type' => request()->input('type'),
                                    'brand' => request()->input('brand'),
                                    'model' => request()->input('model'),
                                    'chassis_type' => request()->input('chassis_type'),
                                    'cylinder_volume' => request()->input('cylinder_volume'),
                                    'price' => request()->input('price'),
                                    'production_year' => request()->input('production_year'),
                                    'kilometre' => request()->input('kilometre'),
                                    'neworold' => request()->input('neworold'),
                                    'status' => request()->input('status'),
                                    'keyword' => request()->input('keyword')

                                    ]) }}"><i class="fa fa-angle-right"></i></a>
                                </li>
                            @endif
                        </ul>
                    @endif
                </div>
            </div>
        </div>
    </div>
    @foreach($ads as $ad)
        {{--<div id="re-validate_ad_{{ $ad->id }}_modal" class="modal fade" tabindex="-1" role="dialog"
             aria-labelledby="myModalLabel"
             aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                        <h4 class="modal-title" id="myModalLabel">تمدید آگهی</h4>
                    </div>
                    <form action="{{ route('reValidateAdsInAdminPanel' , $ad->id) }}" method="post">
                        {{ csrf_field() }}
                        <input type="hidden" name="_method" value="PUT">
                        <div class="modal-body">
                            <h4>تمدید آگهی با عنوان <span class="text-primary">{{ $ad->title }}</span></h4>

                            <label for="re-validate-ad-{{ $ad->id }}-interval-days">آگهی را به مدت چند روز می خواهید
                                تمدید کنید؟</label>
                            <input type="number" value="{{ $plan->interval_days }}" name="days"
                                   id="re-validate-ad-{{ $ad->id }}-interval-days"
                                   class="form-control">
                            مدت پیش فرض تمدید برای این آگهی با این پلن <b
                                    class="text-danger">{{ $plan->interval_days }}</b> روز است.
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default waves-effect" data-dismiss="modal">خروج
                            </button>
                            <button type="submit" class="btn btn-primary waves-effect waves-light">ذخیره تغیرات</button>
                        </div>
                    </form>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div>--}}
    @endforeach
@endsection