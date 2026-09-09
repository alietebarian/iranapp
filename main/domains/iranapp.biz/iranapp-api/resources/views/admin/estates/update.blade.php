@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">ویرایش آگهی املاک
                        (
                        <span class="input-field-errors">فیلد های ستاره دار الزامی هستند.</span>
                        )
                    </h1>
                    <form id="save-estate-ads-form" action="{{ route('estateAds.update' , $ads->id) }}" method="post"
                          enctype="multipart/form-data">
                        {{ method_field('PUT') }}
                        {{ csrf_field() }}
                        <div class="row">
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
                            </div>
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
                            </div>
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
                            </div>
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
                            </div>
                            <div class="col-xs-12 col-md-2">
                                <button type="button" data-toggle="modal" data-target="#thumbnail_photo_modal"
                                        style="margin-top: 26px;" class="btn btn-danger btn-block">مشاهده تصویر
                                </button>
                            </div>
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
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="user_type">نوع آگهی دهنده:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select class="form-control" name="user_type" id="user_type">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        <option {{ $ads->user_type == 'moshaver_amlak' ? 'selected' : '' }} value="moshaver_amlak">
                                            مشاور املاک
                                        </option>
                                        <option {{ $ads->user_type == 'person' ? 'selected' : '' }} value="person">
                                            شخص
                                        </option>
                                    </select>
                                    @if($errors->has('user_type'))
                                        <b class="text-danger">{{ $errors->first('user_type') }}</b>
                                    @endif
                                </div>
                            </div>
                            <script>
                                $('#ejare_or_kharid').change(function () {
                                    var kharidPrice = $('#price_kharid');
                                    var ejare = $('#pre_pay_ejare , #monthly_price_ejare');
                                    var $this = $(this);
                                    if ($this.val() == 'ejare') {
                                        kharidPrice.attr('disabled', 'disabled');
                                        ejare.removeAttr('disabled');
                                    } else if ($this.val() == 'kharid') {
                                        ejare.attr('disabled', 'disabled');
                                        kharidPrice.removeAttr('disabled');
                                    }
                                });
                            </script>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="telephone1">تلفن تماس 1:</label>
                                    <input type="text" value="{{ $ads->telephone1 }}" name="telephone1" id="telephone1"
                                           class="form-control">
                                    @if($errors->has('telephone1'))
                                        <b class="text-danger">{{ $errors->first('telephone1') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="telephone2">تلفن تماس 2:</label>
                                    <input type="text" value="{{ $ads->telephone2 }}" name="telephone2" id="telephone2"
                                           class="form-control">
                                    @if($errors->has('telephone2'))
                                        <b class="text-danger">{{ $errors->first('telephone2') }}</b>
                                    @endif
                                </div>
                            </div>
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
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="ads_owner_name">نام صاحب آگهی:</label>
                                    <input type="text" value="{{ $ads->ads_owner_name }}" name="ads_owner_name"
                                           id="ads_owner_name" class="form-control">
                                    @if($errors->has('ads_owner_name'))
                                        <b class="text-danger">{{ $errors->first('ads_owner_name') }}</b>
                                    @endif
                                </div>
                            </div>
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
                        </div>
                        <div class="row">
                            <div class="col-xs-6">
                                <div class="form-group">
                                    <label for="category_in_save_ads">دسته بندی:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select class="selectpicker" id="category_in_save_ads" data-live-search="true"
                                            name="category_id"
                                            data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                        @foreach($categories as $category)
                                            <option {{ $parent_category_id == $category->id ? 'selected' : '' }} value="{{ $category->id }}">{{ $category->name }}</option>
                                        @endforeach
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-6">
                                <div class="form-group">
                                    <label for="subcategory_in_create_page">زیر دسته:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select class="selectpicker" id="subcategory_in_create_page" data-live-search="true"
                                            name="sub_category_id"
                                            data-style="btn-white">
                                        <option disabled selected>:: انتخاب کنید ::</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        @if($parent_category_id == 1)
                            <div class="row estate-options-row" id="optional_row">
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="meters__">متراژ (متر مربع)</label>
                                        <input type="text" name="meters" value="{{ $ads->meters }}" id="meters__" class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="price__type">نوع قیمت</label>
                                        <select class="form-control" name="price__type" id="price__type">
                                            <option {{ ( $ads->price_kharid != 0  and  $ads->price_kharid != -1 ) ? 'selected' : '' }} value="maghtoo">مقطوع</option>
                                            <option {{ $ads->price_kharid == 0 ? 'selected' : '' }} value="tavafoghi">توافقی</option>
                                            <option {{ $ads->price_kharid == -1 ? 'selected' : '' }} value="moavaze">معاوضه</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="price__">قیمت</label>
                                        <input value="{{ ( $ads->price_kharid != 0 and $ads->price_kharid != -1 ) ? $ads->price_kharid : '' }}" type="number" id="price__" name="price_kharid" class="form-control">
                                    </div>
                                    <script>
                                        $('#price__type').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#price__').removeAttr('disabled');
                                            } else {
                                                $('#price__').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="rooms__countـkolangi">تعداد خواب</label>
                                        <select name="rooms_count" id="rooms__countـkolangi" class="form-control">
                                            <option {{ $ads->rooms_count == 0 ? 'selected' : '' }} value="0">بدون اتاق</option>
                                            <option {{ $ads->rooms_count == 1 ? 'selected' : '' }} value="1">یک</option>
                                            <option {{ $ads->rooms_count == 2 ? 'selected' : '' }} value="2">دو</option>
                                            <option {{ $ads->rooms_count == 3 ? 'selected' : '' }} value="3">سه</option>
                                            <option {{ $ads->rooms_count == 4 ? 'selected' : '' }} value="4">چهار</option>
                                            <option {{ $ads->rooms_count == 5 ? 'selected' : '' }} value="5">پنج یا بیشتر</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="sell__or__buy">نوع آگهی</label>
                                        <select name="sell_or_buy" id="sell__or__buy" class="form-control">
                                            <option {{ $ads->sell_or_buy == 'sell' ? 'selected' : '' }} value="sell">فروشی</option>
                                            <option {{ $ads->sell_or_buy == 'buy' ? 'selected' : '' }} value="buy">درخواستی</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="is__in__hoome">ملک در حومه شهر واقع است</label>
                                        <select name="is_in_hoome" id="is__in__hoome" class="form-control">
                                            <option {{ $ads->is_in_hoome == 0 ? 'selected' : '' }} value="no">خیر</option>
                                            <option {{ $ads->is_in_hoome == 1 ? 'selected' : '' }} value="yes">بله</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        @else
                            <div class="row estate-options-row" id="optional_row"
                                 style="display:none;">
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="meters__">متراژ (متر مربع)</label>
                                        <input type="text" name="meters" id="meters__" class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="price__type">نوع قیمت</label>
                                        <select class="form-control" name="price__type" id="price__type">
                                            <option value="maghtoo">مقطوع</option>
                                            <option value="tavafoghi">توافقی</option>
                                            <option value="moavaze">معاوضه</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="price__">قیمت</label>
                                        <input type="number" id="price__" name="price_kharid" class="form-control">
                                    </div>
                                    <script>
                                        $('#price__type').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#price__').removeAttr('disabled');
                                            } else {
                                                $('#price__').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="rooms__countـkolangi">تعداد خواب</label>
                                        <select name="rooms_count" id="rooms__countـkolangi" class="form-control">
                                            <option {{ $ads->rooms_count == 0 ? 'selected' : '' }} value="0">بدون اتاق</option>
                                            <option {{ $ads->rooms_count == 1 ? 'selected' : '' }} value="1">یک</option>
                                            <option {{ $ads->rooms_count == 2 ? 'selected' : '' }} value="2">دو</option>
                                            <option {{ $ads->rooms_count == 3 ? 'selected' : '' }} value="3">سه</option>
                                            <option {{ $ads->rooms_count == 4 ? 'selected' : '' }} value="4">چهار</option>
                                            <option {{ $ads->rooms_count == 5 ? 'selected' : '' }} value="5">پنج یا بیشتر</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="sell__or__buy">نوع آگهی</label>
                                        <select name="sell_or_buy" id="sell__or__buy" class="form-control">
                                            <option value="sell">فروشی</option>
                                            <option value="buy">درخواستی</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="is__in__hoome">ملک در حومه شهر واقع است</label>
                                        <select name="is_in_hoome" id="is__in__hoome" class="form-control">
                                            <option value="no">خیر</option>
                                            <option value="yes">بله</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        @endif
                        @if($parent_category_id == 2)
                            <div class="row estate-options-row" id="optional_row_2">
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="meters___">متراژ (متر مربع)</label>
                                        <input value="{{ $ads->meters }}" type="text" name="meters" id="meters___" class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="price___type">نوع ودیعه</label>
                                        <select class="form-control" name="price__type" id="price___type">
                                            <option {{ ($ads->pre_pay_ejare != 0 and $ads->pre_pay_ejare != 1) ? 'selected' : '' }} value="maghtoo">مقطوع</option>
                                            <option {{ $ads->pre_pay_ejare == 0 ? 'selected' : '' }} value="tavafoghi">توافقی</option>
                                            <option {{ $ads->pre_pay_ejare == -1 ? 'selected' : '' }} value="free">مجانی</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="price___">مبلغ ودیعه</label>
                                        <input value="{{ ($ads->pre_pay_ejare and $ads->pre_pay_ejare != -1) ? $ads->pre_pay_ejare	 : '' }}" name="pre_pay_ejare" type="number" id="price___" class="form-control">
                                    </div>
                                    <script>
                                        $('#price___type').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#price___').removeAttr('disabled');
                                            } else {
                                                $('#price___').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="ejare__type">نوع اجاره</label>
                                        <select class="form-control" name="ejare__type" id="ejare__type">
                                            <option {{ ($ads->monthly_price_ejare != 0 and $ads->monthly_price_ejare != -1) ? 'selected' : '' }} value="maghtoo">مقطوع</option>
                                            <option {{ $ads->monthly_price_ejare == 0 ? 'selected' : '' }} value="tavafoghi">توافقی</option>
                                            <option {{ $ads->monthly_price_ejare == -1 ? 'selected' : '' }} value="free">مجانی</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="ejare__price">مبلغ اجاره</label>
                                        <input value="{{ ($ads->monthly_price_ejare and $ads->monthly_price_ejare != -1) ? $ads->monthly_price_ejare : '' }}" type="number" name="monthly_price_ejare" id="ejare__price"
                                               class="form-control">
                                    </div>
                                    <script>
                                        $('#ejare__type').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#ejare__price').removeAttr('disabled');
                                            } else {
                                                $('#ejare__price').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="rooms___count">تعداد خواب</label>
                                        <select name="rooms_count" id="rooms___count" class="form-control">
                                            <option {{ $ads->rooms_count == 0 ? 'selected' : '' }} value="0">بدون اتاق</option>
                                            <option {{ $ads->rooms_count == 1 ? 'selected' : '' }} value="1">یک</option>
                                            <option {{ $ads->rooms_count == 2 ? 'selected' : '' }} value="2">دو</option>
                                            <option {{ $ads->rooms_count == 3 ? 'selected' : '' }} value="3">سه</option>
                                            <option {{ $ads->rooms_count == 4 ? 'selected' : '' }} value="4">چهار</option>
                                            <option {{ $ads->rooms_count == 5 ? 'selected' : '' }} value="5">پنج یا بیشتر</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="sell__or__buy__">نوع آگهی</label>
                                        <select name="sell_or_buy" id="sell__or__buy__" class="form-control">
                                            <option {{ $ads->sell_or_buy == 'sell' ? 'selected' : '' }} value="sell">ارائه</option>
                                            <option {{ $ads->sell_or_buy == 'buy' ? 'selected' : '' }} value="buy">درخواستی</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="is__in__hoome__">ملک در حومه شهر واقع است</label>
                                        <select name="is_in_hoome" id="is__in__hoome__" class="form-control">
                                            <option {{ $ads->is_in_hoome == 0 ? 'selected' : '' }} value="no">خیر</option>
                                            <option {{ $ads->is_in_hoome == 1 ? 'selected' : '' }} value="yes">بله</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        @else
                            <div class="row estate-options-row" id="optional_row_2"
                                 style="display:none;">
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="meters___">متراژ (متر مربع)</label>
                                        <input type="text" name="meters" id="meters___" class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="price___type">نوع ودیعه</label>
                                        <select class="form-control" name="price__type" id="price___type">
                                            <option value="maghtoo">مقطوع</option>
                                            <option value="tavafoghi">توافقی</option>
                                            <option value="free">مجانی</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="price___">مبلغ ودیعه</label>
                                        <input name="pre_pay_ejare" type="number" id="price___" class="form-control">
                                    </div>
                                    <script>
                                        $('#price___type').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#price___').removeAttr('disabled');
                                            } else {
                                                $('#price___').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="ejare__type">نوع اجاره</label>
                                        <select class="form-control" name="ejare__type" id="ejare__type">
                                            <option value="maghtoo">مقطوع</option>
                                            <option value="tavafoghi">توافقی</option>
                                            <option value="free">مجانی</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="ejare__price">مبلغ اجاره</label>
                                        <input type="number" name="monthly_price_ejare" id="ejare__price"
                                               class="form-control">
                                    </div>
                                    <script>
                                        $('#ejare__type').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#ejare__price').removeAttr('disabled');
                                            } else {
                                                $('#ejare__price').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="rooms___count">تعداد خواب</label>
                                        <select name="rooms_count" id="rooms___count" class="form-control">
                                            <option {{ $ads->rooms_count == 0 ? 'selected' : '' }} value="0">بدون اتاق</option>
                                            <option {{ $ads->rooms_count == 1 ? 'selected' : '' }} value="1">یک</option>
                                            <option {{ $ads->rooms_count == 2 ? 'selected' : '' }} value="2">دو</option>
                                            <option {{ $ads->rooms_count == 3 ? 'selected' : '' }} value="3">سه</option>
                                            <option {{ $ads->rooms_count == 4 ? 'selected' : '' }} value="4">چهار</option>
                                            <option {{ $ads->rooms_count == 5 ? 'selected' : '' }} value="5">پنج یا بیشتر</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="sell__or__buy__">نوع آگهی</label>
                                        <select name="sell_or_buy" id="sell__or__buy__" class="form-control">
                                            <option value="sell">ارائه</option>
                                            <option value="buy">درخواستی</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="is__in__hoome__">ملک در حومه شهر واقع است</label>
                                        <select name="is_in_hoome" id="is__in__hoome__" class="form-control">
                                            <option value="no">خیر</option>
                                            <option value="yes">بله</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        @endif
                        @if($parent_category_id == 3)
                            <div class="row estate-options-row" id="optional_row_3">
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="meters___">متراژ (متر مربع)</label>
                                        <input value="{{ $ads->meters }}" type="text" name="meters" id="meters___" class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="price__type__">نوع قیمت</label>
                                        <select class="form-control" name="price__type" id="price__type__">
                                            <option {{ ($ads->price_kharid != 0 and $ads->price_kharid != -1) ? 'selected' : '' }} value="maghtoo">مقطوع</option>
                                            <option {{ $ads->price_kharid == 0 ? 'selected' : '' }} value="tavafoghi">توافقی</option>
                                            <option {{ $ads->price_kharid == -1 ? 'selected' : '' }} value="moaveze">معاوضه</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="__price__">قیمت</label>
                                        <input value="{{ ($ads->price_kharid and $ads->price_kharid != -1) ? $ads->price_kharid : '' }}" name="price_kharid" type="number" id="__price__" class="form-control">
                                    </div>
                                    <script>
                                        $('#price__type__').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#__price__').removeAttr('disabled');
                                            } else {
                                                $('#__price__').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="rooms__count">تعداد خواب</label>
                                        <select name="rooms_count" id="rooms__count" class="form-control">
                                            <option {{ $ads->rooms_count == 0 ? 'selected' : '' }} value="0">بدون اتاق</option>
                                            <option {{ $ads->rooms_count == 1 ? 'selected' : '' }} value="1">یک</option>
                                            <option {{ $ads->rooms_count == 2 ? 'selected' : '' }} value="2">دو</option>
                                            <option {{ $ads->rooms_count == 3 ? 'selected' : '' }} value="3">سه</option>
                                            <option {{ $ads->rooms_count == 4 ? 'selected' : '' }} value="4">چهار</option>
                                            <option {{ $ads->rooms_count == 5 ? 'selected' : '' }} value="5">پنج یا بیشتر</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="sell__or__buy">نوع آگهی</label>
                                        <select name="sell_or_buy" id="sell__or__buy" class="form-control">
                                            <option {{ $ads->sell_or_buy == 'sell' ? 'selected' : '' }} value="sell">فروشی</option>
                                            <option {{ $ads->sell_or_buy ==  'buy' ? 'selected' : '' }} value="buy">درخواستی</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="is__in__hoome">ملک در حومه شهر واقع است</label>
                                        <select name="is_in_hoome" id="is__in__hoome" class="form-control">
                                            <option {{ $ads->is_in_hoome == 0 ? 'selected' : '' }} value="no">خیر</option>
                                            <option {{ $ads->is_in_hoome == 1 ? 'selected' : '' }} value="yes">بله</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="sanad_edari">دارای سند اداری</label>
                                        <select name="sanad_edari" id="sanad_edari" class="form-control">
                                            <option {{ $ads->sanad_edari == 1 ? 'selected' : '' }} value="yes">بله</option>
                                            <option {{ $ads->sanad_edari == 0 ? 'selected' : '' }} value="no">خیر</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        @else
                            <div class="row estate-options-row" id="optional_row_3"
                                 style="display:none;">
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="meters___">متراژ (متر مربع)</label>
                                        <input type="text" name="meters" id="meters___" class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="price__type__">نوع قیمت</label>
                                        <select class="form-control" name="price__type" id="price__type__">
                                            <option value="maghtoo">مقطوع</option>
                                            <option value="tavafoghi">توافقی</option>
                                            <option value="moaveze">معاوضه</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="__price__">قیمت</label>
                                        <input name="price_kharid" type="number" id="__price__" class="form-control">
                                    </div>
                                    <script>
                                        $('#price__type__').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#__price__').removeAttr('disabled');
                                            } else {
                                                $('#__price__').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="rooms__count">تعداد خواب</label>
                                        <select name="rooms_count" id="rooms__count" class="form-control">
                                            <option {{ $ads->rooms_count == 0 ? 'selected' : '' }} value="0">بدون اتاق</option>
                                            <option {{ $ads->rooms_count == 1 ? 'selected' : '' }} value="1">یک</option>
                                            <option {{ $ads->rooms_count == 2 ? 'selected' : '' }} value="2">دو</option>
                                            <option {{ $ads->rooms_count == 3 ? 'selected' : '' }} value="3">سه</option>
                                            <option {{ $ads->rooms_count == 4 ? 'selected' : '' }} value="4">چهار</option>
                                            <option {{ $ads->rooms_count == 5 ? 'selected' : '' }} value="5">پنج یا بیشتر</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="sell__or__buy">نوع آگهی</label>
                                        <select name="sell_or_buy" id="sell__or__buy" class="form-control">
                                            <option value="sell">فروشی</option>
                                            <option value="buy">درخواستی</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="is__in__hoome">ملک در حومه شهر واقع است</label>
                                        <select name="is_in_hoome" id="is__in__hoome" class="form-control">
                                            <option value="no">خیر</option>
                                            <option value="yes">بله</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="sanad_edari">دارای سند اداری</label>
                                        <select name="sanad_edari" id="sanad_edari" class="form-control">
                                            <option value="yes">بله</option>
                                            <option value="no">خیر</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        @endif
                        @if($parent_category_id == 4)
                            <div class="row estate-options-row" id="optional_row_4">
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="____meters___">متراژ (متر مربع)</label>
                                        <input value="{{ $ads->meters }}" type="text" name="meters" id="____meters___" class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="____price___type">نوع ودیعه</label>
                                        <select class="form-control" name="price_type" id="____price___type">
                                            <option {{ ($ads->pre_pay_ejare != 0 and $ads->pre_pay_ejare != -1) ? 'selected' : '' }} value="maghtoo">مقطوع</option>
                                            <option {{ $ads->pre_pay_ejare == 0 ? 'selected' : '' }} value="tavafoghi">توافقی</option>
                                            <option {{ $ads->pre_pay_ejare == -1 ? 'selected' : '' }} value="free">مجانی</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="vadieee____price___">مبلغ ودیعه</label>
                                        <input value="{{ ($ads->pre_pay_ejare and $ads->pre_pay_ejare != -1) ? $ads->pre_pay_ejare : '' }}" name="pre_pay_ejare" type="number" id="vadieee____price___"
                                               class="form-control">
                                    </div>
                                    <script>
                                        $('#____price___type').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#vadieee____price___').removeAttr('disabled');
                                            } else {
                                                $('#vadieee____price___').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="___ejare__type">نوع اجاره</label>
                                        <select class="form-control" name="ejare__type" id="___ejare__type">
                                            <option {{ ($ads->monthly_price_ejare != 0 and $ads->monthly_price_ejare != -1)  ? 'selected' : '' }} value="maghtoo">مقطوع</option>
                                            <option {{ $ads->monthly_price_ejare == 0 ? 'selected' : '' }} value="tavafoghi">توافقی</option>
                                            <option {{ $ads->monthly_price_ejare == -1 ? 'selected' : '' }} value="free">مجانی</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="__ejare__price">مبلغ اجاره</label>
                                        <input value="{{ ($ads->monthly_price_ejare and $ads->monthly_price_ejare != -1) ? $ads->monthly_price_ejare : '' }}" type="number" name="monthly_price_ejare" id="__ejare__price"
                                               class="form-control">
                                    </div>
                                    <script>
                                        $('#___ejare__type').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#__ejare__price').removeAttr('disabled');
                                            } else {
                                                $('#__ejare__price').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="__ro__oms___count">تعداد خواب</label>
                                        <select name="rooms_count" id="__ro__oms___count" class="form-control">
                                            <option {{ $ads->rooms_count == 0 ? 'selected' : '' }} value="0">بدون اتاق</option>
                                            <option {{ $ads->rooms_count == 1 ? 'selected' : '' }} value="1">یک</option>
                                            <option {{ $ads->rooms_count == 2 ? 'selected' : '' }} value="2">دو</option>
                                            <option {{ $ads->rooms_count == 3 ? 'selected' : '' }} value="3">سه</option>
                                            <option {{ $ads->rooms_count == 4 ? 'selected' : '' }} value="4">چهار</option>
                                            <option {{ $ads->rooms_count == 5 ? 'selected' : '' }} value="5">پنج یا بیشتر</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="sell__o__r__buy__">نوع آگهی</label>
                                        <select name="sell_or_buy" id="sell__o__r__buy__" class="form-control">
                                            <option {{ $ads->sell_or_buy == 'sell' ? 'selected' : '' }} value="sell">ارائه</option>
                                            <option {{ $ads->sell_or_buy == 'buy' ? 'selected' : '' }} value="buy">درخواستی</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="is__in__ho__ome__">ملک در حومه شهر واقع است</label>
                                        <select name="is_in_hoome" id="is__in__ho__ome__" class="form-control">
                                            <option {{ $ads->is_in_hoome == 0 ? 'selected' : '' }} value="no">خیر</option>
                                            <option {{ $ads->is_in_hoome == 1 ? 'selected' : '' }} value="yes">بله</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        @else
                            <div class="row estate-options-row" id="optional_row_4"
                                 style="display:none;">
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="____meters___">متراژ (متر مربع)</label>
                                        <input type="text" name="meters" id="____meters___" class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="____price___type">نوع ودیعه</label>
                                        <select class="form-control" name="price_type" id="____price___type">
                                            <option value="maghtoo">مقطوع</option>
                                            <option value="tavafoghi">توافقی</option>
                                            <option value="free">مجانی</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="vadieee____price___">مبلغ ودیعه</label>
                                        <input name="pre_pay_ejare" type="number" id="vadieee____price___"

                                               class="form-control">
                                    </div>
                                    <script>
                                        $('#____price___type').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#vadieee____price___').removeAttr('disabled');
                                            } else {
                                                $('#vadieee____price___').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="___ejare__type">نوع اجاره</label>
                                        <select class="form-control" name="ejare__type" id="___ejare__type">
                                            <option value="maghtoo">مقطوع</option>
                                            <option value="tavafoghi">توافقی</option>
                                            <option value="free">مجانی</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="__ejare__price">مبلغ اجاره</label>
                                        <input type="number" name="monthly_price_ejare" id="__ejare__price"
                                               class="form-control">
                                    </div>
                                    <script>
                                        $('#___ejare__type').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#__ejare__price').removeAttr('disabled');
                                            } else {
                                                $('#__ejare__price').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="__ro__oms___count">تعداد خواب</label>
                                        <select name="rooms_count" id="__ro__oms___count" class="form-control">
                                            <option {{ $ads->rooms_count == 0 ? 'selected' : '' }} value="0">بدون اتاق</option>
                                            <option {{ $ads->rooms_count == 1 ? 'selected' : '' }} value="1">یک</option>
                                            <option {{ $ads->rooms_count == 2 ? 'selected' : '' }} value="2">دو</option>
                                            <option {{ $ads->rooms_count == 3 ? 'selected' : '' }} value="3">سه</option>
                                            <option {{ $ads->rooms_count == 4 ? 'selected' : '' }} value="4">چهار</option>
                                            <option {{ $ads->rooms_count == 5 ? 'selected' : '' }} value="5">پنج یا بیشتر</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="sell__o__r__buy__">نوع آگهی</label>
                                        <select name="sell_or_buy" id="sell__o__r__buy__" class="form-control">
                                            <option value="sell">ارائه</option>
                                            <option value="buy">درخواستی</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="is__in__ho__ome__">ملک در حومه شهر واقع است</label>
                                        <select name="is_in_hoome" id="is__in__ho__ome__" class="form-control">
                                            <option value="no">خیر</option>
                                            <option value="yes">بله</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        @endif
                        <script>
                            $(document).ready(function () {
                                var categoryId = $('#category_in_save_ads').val();
                                $.ajax({
                                    type: 'GET',
                                    url: '{{ url()->to('/admin/estates/ads/categories/') }}/' + categoryId + '/children',
                                    success: function (response) {
                                        var list = response.list;
                                        var subCategory = $('#subcategory_in_create_page');
                                        subCategory.html('');
                                        for (var i = 0; i < list.length; i++) {
                                            subCategory.append('<option value="' + list[i].id + '">' + list[i].name + '</option>');
                                        }
                                        subCategory.val({{ $ads->category_id }});
                                        subCategory.selectpicker('render');
                                        subCategory.selectpicker('refresh');
                                    }
                                });
                            });
                            $('#category_in_save_ads').change(function () {
                                var categoryId = $(this).val();
                                $.ajax({
                                    type: 'GET',
                                    url: '{{ url()->to('/admin/estates/ads/categories/') }}/' + categoryId + '/children',
                                    success: function (response) {
                                        var list = response.list;
                                        var subCategory = $('#subcategory_in_create_page');
                                        subCategory.html('');
                                        for (var i = 0; i < list.length; i++) {
                                            subCategory.append('<option value="' + list[i].id + '">' + list[i].name + '</option>');
                                        }
                                        subCategory.selectpicker('render');
                                        subCategory.selectpicker('refresh');
                                    }
                                });
                            });
                            $('#category_in_save_ads').change(function () {
                                if($('#category_in_save_ads').val() == 1){
                                    $('#subcategory_in_create_page').change(function () {
                                        var id = $(this).val();
                                        console.log(id);
                                        if(id == 8){
                                            $('#rooms__countـkolangi').attr('disabled' , 'disabled');
                                        }else{
                                            $('#rooms__countـkolangi').removeAttr('disabled');
                                        }
                                    });
                                }
                            });
                            $(document).ready(function () {
                                $('#subcategory_in_create_page').change(function () {
                                    var id = $(this).val();
                                    console.log(id);
                                    if(id == 8){
                                        $('#rooms__countـkolangi').attr('disabled' , 'disabled');
                                    }else{
                                        $('#rooms__countـkolangi').removeAttr('disabled');
                                    }
                                });
                            });


                            $(document).ready(function () {
                                @if($parent_category_id == 1)
                                $('.estate-options-row textarea , .estate-options-row input , .estate-options-row select ').attr('disabled', 'disabled');
                                $('#optional_row textarea , #optional_row  input , #optional_row select ').removeAttr('disabled');

                                @elseif($parent_category_id == 2 )
                                $('.estate-options-row textarea , .estate-options-row input , .estate-options-row select ').attr('disabled', 'disabled');
                                $('#optional_row_2 textarea , #optional_row_2  input , #optional_row_2 select ').removeAttr('disabled');

                                @elseif($parent_category_id == 3)
                                $('.estate-options-row textarea , .estate-options-row input , .estate-options-row select ').attr('disabled', 'disabled');
                                $('#optional_row_3 textarea , #optional_row_3  input , #optional_row_3 select ').removeAttr('disabled');
                                @elseif($parent_category_id == 4)
                                $('.estate-options-row textarea , .estate-options-row input , .estate-options-row select ').attr('disabled', 'disabled');
                                $('#optional_row_4 textarea , #optional_row_4  input , #optional_row_4 select ').removeAttr('disabled');
                                @elseif($parent_category_id == 5)
                                $('.estate-options-row textarea , .estate-options-row input , .estate-options-row select ').attr('disabled', 'disabled');
                                @endif
                            });
                            $('#subcategory_in_create_page , #category_in_save_ads').change(function () {
                                var subCategoryId = $(this).val();
                                var categoryId = $('#category_in_save_ads , #subcategory_in_create_page').val();
                                if (categoryId == 1) {
                                    $('#optional_row').fadeIn(200);
                                    $('#optional_row_2').fadeOut(200);
                                    $('#optional_row_3').fadeOut(200);
                                    $('#optional_row_4').fadeOut(200);
                                    $('.estate-options-row textarea , .estate-options-row input , .estate-options-row select ').attr('disabled', 'disabled');
                                    $('#optional_row  textarea  , #optional_row input , #optional_row select').removeAttr('disabled');
                                } else if (categoryId == 2) {
                                    $('#optional_row').fadeOut(200);
                                    $('#optional_row_2').fadeIn(200);
                                    $('#optional_row_3').fadeOut(200);
                                    $('#optional_row_4').fadeOut(200);

                                    $('.estate-options-row textarea , .estate-options-row input , .estate-options-row select ').attr('disabled', 'disabled');
                                    $('#optional_row_2  textarea  , #optional_row_2 input , #optional_row_2 select').removeAttr('disabled');
                                } else if (categoryId == 3) {
                                    $('#optional_row').fadeOut(200);
                                    $('#optional_row_2').fadeOut(200);
                                    $('#optional_row_3').fadeIn(200);
                                    $('#optional_row_4').fadeOut(200);

                                    $('.estate-options-row textarea , .estate-options-row input , .estate-options-row select ').attr('disabled', 'disabled');
                                    $('#optional_row_3  textarea  , #optional_row_3 input , #optional_row_3 select').removeAttr('disabled');

                                } else if (categoryId == 4) {
                                    $('#optional_row').fadeOut(200);
                                    $('#optional_row_2').fadeOut(200);
                                    $('#optional_row_3').fadeOut(200);
                                    $('#optional_row_4').fadeIn(200);

                                    $('.estate-options-row textarea , .estate-options-row input , .estate-options-row select ').attr('disabled', 'disabled');
                                    $('#optional_row_4  textarea  , #optional_row_4 input , #optional_row_4 select').removeAttr('disabled');

                                } else if (categoryId == 5) {
                                    $('#optional_row').fadeOut(200);
                                    $('#optional_row_2').fadeOut(200);
                                    $('#optional_row_3').fadeOut(200);
                                    $('#optional_row_4').fadeOut(200);

                                    $('.estate-options-row textarea , .estate-options-row input , .estate-options-row select ').attr('disabled', 'disabled');
                                    $('#optional_row_4  textarea  , #optional_row_4 input , #optional_row_4 select').removeAttr('disabled');
                                }
                            });
                        </script>
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <button id="remove-location" type="button" class="btn btn-danger btn-xs">حذف
                                        Location
                                    </button>
                                </div>
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
                        </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="ads-photo-wrapper">
                                    @foreach($photos as $photo)
                                        <div class="col-sm-4" style="position:relative;">
                                            <a href="{{ route('deleteEstateAdsPhoto' , $photo->id) }}" class="btn btn-danger btn-xs" style="position:absolute;top:25px;left:25px;">حذف</a>
                                            <img src="{{ URL::to('/ads_photo') }}/{{ $photo->file_name }}" alt="ads photo"
                                                 class="img-responsive img-thumbnail" width="400"/>
                                        </div>
                                    @endforeach
                                </div>
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
                    <a href="{{ route('deleteEstateAdsThumbnailPhoto' , $ads->id) }}" class="btn btn-danger waves-effect waves-light">حذف تصویر</a>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div>

@endsection