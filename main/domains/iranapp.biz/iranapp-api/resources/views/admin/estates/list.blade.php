@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">فیلتر</h1>
                    <form action="{{ url()->current() }}" method="get" id="estateSearchForm">
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
                            </div>
                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label for="city_in_filter">شهر:</label>
                                    <select data-live-search="true" name="city_id" id="city_in_filter"
                                            class="form-control selectpicker btn-default">
                                        <option value="all">:: انتخاب کنید ::</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label for="region_in_filter">منطقه:</label>
                                    <select data-live-search="true" name="region_id" id="region_in_filter"
                                            class="form-control selectpicker btn-default">
                                        <option value="all">:: انتخاب کنید ::</option>
                                    </select>
                                </div>
                            </div>
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
                                    <label for="user_type">نوع ثبت کننده</label>
                                    <select name="user_type" id="user_type" class="form-control">
                                        <option value="all">:: بدون تفاوت ::</option>
                                        <option {{ request()->input('user_type') == 'moshaver_amlak' ? 'selected' : '' }} value="moshaver_amlak">
                                            مشاور املاک
                                        </option>
                                        <option {{ request()->input('user_type') == 'person' ? 'selected' : '' }} value="person">
                                            فرد عادی
                                        </option>
                                    </select>
                                </div>
                            </div>
                            <script>
                                function disableKharidOrEjareFields() {
                                    var ejareOrKharid = $('#ejare_or_kharid');
                                    if (ejareOrKharid.val() == 'ejare') {
                                        $('#vadie_from , #vadie_to , #ejare_from , #ejare_to').removeAttr('disabled');
                                        $('#price_from , #price_to').attr('disabled', 'disabled');
                                    } else if (ejareOrKharid.val() == 'kharid') {
                                        $('#vadie_from , #vadie_to , #ejare_from , #ejare_to').attr('disabled', 'disabled');
                                        $('#price_from , #price_to').removeAttr('disabled');
                                    }
                                }
                                $(document).ready(function () {
                                    disableKharidOrEjareFields();
                                });
                                $('#ejare_or_kharid').change(function () {
                                    disableKharidOrEjareFields();
                                });
                            </script>
                            <script>
                                function disableHasSanadEdariField() {
                                    var field = $('#karbari_type');
                                    if (field.val() == 'maskooni') {
                                        $('#sanad_edari').attr('disabled', 'disabled');
                                    } else {
                                        $('#sanad_edari').removeAttr('disabled');
                                    }
                                }
                                $(document).ready(function () {
                                    disableHasSanadEdariField();
                                });
                                $('#karbari_type').change(function () {
                                    disableHasSanadEdariField();
                                });
                            </script>
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
                        </div>
                        <div class="row">
                            <div class="col-xs-4">
                                <div class="form-group">
                                    <label for="category_in_filter">دسته بندی آگهی:</label>
                                    <select name="category_id" id="category_in_filter" class="form-control">
                                        <option value="all">:: بدون انتخاب ::</option>
                                        @foreach($categories as $category)
                                            <option {{ request()->input('category_id') == $category->id ? 'selected' : '' }} value="{{ $category->id }}">{{ $category->name }}</option>
                                        @endforeach
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-4">
                                <div class="form-group">
                                    <label for="sub_category_in_filter">زیر دسته آگهی:</label>
                                    <select name="sub_category_id" id="sub_category_in_filter" class="form-control">
                                        <option value="all">:: بدون انتخاب ::</option>
                                    </select>
                                </div>
                            </div>
                            <script>
                                $('#category_in_filter').change(function () {
                                    var categoryId = $(this).val();
                                    if (categoryId != 'all') {
                                        $.ajax({
                                            type: 'GET',
                                            url: '{{ url()->to('/admin/estates/ads/categories/') }}/' + categoryId + '/children',
                                            success: function (response) {
                                                var list = response.list;
                                                var subCategory = $('#sub_category_in_filter');
                                                subCategory.html('<option value="all">:: بدون انتخاب ::</option>');
                                                for (var i = 0; i < list.length; i++) {
                                                    subCategory.append('<option value="' + list[i].id + '">' + list[i].name + '</option>');
                                                }
                                            }
                                        });
                                    } else {
                                        var subCategory = $('#sub_category_in_filter');
                                        subCategory.html('<option value="all">:: بدون انتخاب ::</option>');
                                    }

                                });
                            </script>
                        </div>
                        @if(request()->input('category_id') == 1)
                            <div class="row estate-options-row" id="optional_row">
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="meters__">متراژ از(متر مربع):</label>
                                        <input value="{{ request()->input('meters_from') }}" type="text"
                                               name="meters_from"
                                               id="meters__" class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="meters__">متراژ تا(متر مربع):</label>
                                        <input value="{{ request()->input('meters_to') }}" type="text" name="meters_to"
                                               id="meters__" class="form-control">
                                    </div>
                                </div>

                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="price__type__in_category_1">نوع قیمت</label>
                                        <select class="form-control" name="price__type" id="price__type__in_category_1">
                                            <option value="all">:: بدون انتخاب ::</option>
                                            <option {{ request()->input('price__type') == 'maghtoo' ? 'selected' : '' }} value="maghtoo">
                                                مقطوع
                                            </option>
                                            <option {{ request()->input('price__type') == 'tavafoghi' ? 'selected' : '' }} value="tavafoghi">
                                                توافقی
                                            </option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="price__from__in_category__1">قیمت از:</label>
                                        <input value="{{ request()->input('price_kharid__from') }}" type="number"
                                               id="price__from__in_category__1"
                                               name="price_kharid__from" class="form-control">
                                    </div>
                                    <script>
                                        $('#price__type__in_category_1').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#price__from__in_category__1 , #price__to__in_category__1').removeAttr('disabled');
                                            } else {
                                                $('#price__from__in_category__1 , #price__to__in_category__1').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="price__to__in_category__1">تا:</label>
                                        <input value="{{ request()->input('price_kharid__to') }}" type="number"
                                               id="price__to__in_category__1"
                                               name="price_kharid__to" class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="rooms__count">تعداد خواب</label>
                                        <input value="{{ request()->input('rooms_count') }}" type="number"
                                               name="rooms_count" min="0" id="rooms__count"
                                               class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="sell__or__buy">نوع آگهی</label>
                                        <select name="sell_or_buy" id="sell__or__buy" class="form-control">
                                            <option value="all">:: بدون انتخاب ::</option>
                                            <option {{ request()->input('sell_or_buy') == 'sell' ? 'selected' : '' }} value="sell">
                                                فروشی
                                            </option>
                                            <option {{ request()->input('sell_or_buy') == 'buy' ? 'selected' : '' }} value="buy">
                                                درخواستی
                                            </option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="is__in__hoome">ملک در حومه شهر واقع است</label>
                                        <select name="is_in_hoome" id="is__in__hoome" class="form-control">
                                            <option value="all">:: بدون انتخاب ::</option>
                                            <option {{ request()->input('is_in_hoome') == 'no' ? 'selected' : '' }} value="no">
                                                خیر
                                            </option>
                                            <option {{ request()->input('is_in_hoome') == 'yes' ? 'selected' : '' }} value="yes">
                                                بله
                                            </option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        @endif
                        @if(request()->input('category_id') == 2)
                            <div class="row estate-options-row" id="optional_row_2">
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="meters___">متراژ از(متر مربع) :</label>
                                        <input value="{{ request()->input('meters_from') }}" type="text"
                                               name="meters_from"
                                               id="meters___" class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="meters___">تا :</label>
                                        <input value="{{ request()->input('meters_to') }}" type="text" name="meters_to"
                                               id="meters___" class="form-control">
                                    </div>
                                </div>

                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="vadiee_type___in_category__2">نوع ودیعه</label>
                                        <select class="form-control" name="price__type"
                                                id="vadiee_type___in_category__2">
                                            <option value="all">:: بدون انتخاب ::</option>
                                            <option {{ request()->input('price__type') == '="maghtoo' ? 'selected' : '' }} value="maghtoo">
                                                مقطوع
                                            </option>
                                            <option {{ request()->input('price__type') == 'tavafoghi' ? 'selected' : '' }} value="tavafoghi">
                                                توافقی
                                            </option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="pre_pay_ejare_to_from___in_category__2">مبلغ ودیعه از:</label>
                                        <input value="{{ request()->input('pre_pay_ejare_to_from___') }}"
                                               name="pre_pay_ejare_to_from___"
                                               type="number" id="pre_pay_ejare_to_from___in_category__2"
                                               class="form-control">
                                    </div>
                                    <script>
                                        $('#vadiee_type___in_category__2').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#pre_pay_ejare_to_from___in_category__2 , #pre_pay_ejare_to_to___in_category__2').removeAttr('disabled');
                                            } else {
                                                $('#pre_pay_ejare_to_from___in_category__2 , #pre_pay_ejare_to_to___in_category__2').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="pre_pay_ejare_to_to___in_category__2">تا:</label>
                                        <input value="{{ request()->input('pre_pay_ejare_to_to___') }}"
                                               name="pre_pay_ejare_to_to___"
                                               type="number" id="pre_pay_ejare_to_to___in_category__2"
                                               class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="ejare__type__in_category__2">نوع اجاره</label>
                                        <select class="form-control" name="ejare__type"
                                                id="ejare__type__in_category__2">
                                            <option value="all">:: بدون انتخاب ::</option>
                                            <option {{ request()->input('ejare__type') == 'maghtoo' ? 'selected' : '' }} value="maghtoo">
                                                مقطوع
                                            </option>
                                            <option {{ request()->input('ejare__type') == 'tavafoghi' ? 'selected' : '' }} value="tavafoghi">
                                                توافقی
                                            </option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="ejare__price_from__in_category__2">مبلغ اجاره از :</label>
                                        <input value="{{ request()->input('monthly_price_ejare_from') }}" type="number"
                                               name="monthly_price_ejare_from" id="ejare__price_from__in_category__2"
                                               class="form-control">
                                    </div>
                                    <script>
                                        $('#ejare__type__in_category__2').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#ejare__price_from__in_category__2 , #ejare__price___to_in_category_2').removeAttr('disabled');
                                            } else {
                                                $('#ejare__price_from__in_category__2 , #ejare__price___to_in_category_2').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="ejare__price___to_in_category_2">تا :</label>
                                        <input value="{{ request()->input('monthly_price_ejare_to') }}" type="number"
                                               name="monthly_price_ejare_to" id="ejare__price___to_in_category_2"
                                               class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="rooms___count">تعداد خواب</label>
                                        <input value="{{ request()->input('rooms_count') }}" name="rooms_count"
                                               type="number" min="0" id="rooms___count"
                                               class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="sell__or__buy__">نوع آگهی</label>
                                        <select name="sell_or_buy" id="sell__or__buy__" class="form-control">
                                            <option value="all">:: بدون انتخاب ::</option>
                                            <option {{ request()->input('sell_or_buy') == 'sell' ? 'selected' : '' }} value="sell">
                                                ارائه
                                            </option>
                                            <option {{ request()->input('sell_or_buy') == 'buy' ? 'selected' : '' }} value="buy">
                                                درخواستی
                                            </option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="is__in__hoome__">ملک در حومه شهر واقع است</label>
                                        <select name="is_in_hoome" id="is__in__hoome__" class="form-control">
                                            <option value="all">:: بدون انتخاب ::</option>
                                            <option {{ request()->input('is_in_hoome') == 'no' ? 'selected' : '' }} value="no">
                                                خیر
                                            </option>
                                            <option {{ request()->input('is_in_hoome') == 'yes' ? 'selected' : '' }} value="yes">
                                                بله
                                            </option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        @endif
                        @if(request()->input("category_id") == 3)
                            <div class="row estate-options-row" id="optional_row_3">
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="meters___">متراژ از(متر مربع) :</label>
                                        <input value="{{ request()->input('meters_from') }}" type="text"
                                               name="meters_from"
                                               id="meters___" class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="meters___">تا:</label>
                                        <input value="{{ request()->input('meters_to') }}" type="text" name="meters_to"
                                               id="meters___" class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="price__type__in_category__3">نوع قیمت</label>
                                        <select class="form-control" name="price__type"
                                                id="price__type__in_category__3">
                                            <option value="all">:: بدون انتخاب ::</option>
                                            <option {{ request()->input('price__type') == 'maghtoo' ? 'selected' : '' }} value="maghtoo">
                                                مقطوع
                                            </option>
                                            <option {{ request()->input('price__type') == 'tavafoghi' ? 'selected' : '' }} value="tavafoghi">
                                                توافقی
                                            </option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="__price___from__in_category__3">قیمت از :</label>
                                        <input value="{{ request()->input('price_kharid_from') }}"
                                               name="price_kharid_from"
                                               type="number" id="__price___from__in_category__3" class="form-control">
                                    </div>
                                    <script>
                                        $('#price__type__in_category__3').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#__price___from__in_category__3 , #__price___to__in_category__3').removeAttr('disabled');
                                            } else {
                                                $('#__price___from__in_category__3 , #__price___to__in_category__3').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="__price___to__in_category__3">تا:</label>
                                        <input value="{{ request()->input('price_kharid_to') }}" name="price_kharid_to"
                                               type="number" id="__price___to__in_category__3" class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="rooms__count">تعداد خواب</label>
                                        <input value="{{ request()->input('rooms_count') }}" name="rooms_count"
                                               type="number" min="0" id="rooms__count"
                                               class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="sell__or__buy">نوع آگهی</label>
                                        <select name="sell_or_buy" id="sell__or__buy" class="form-control">
                                            <option value="all">:: بدون انتخاب ::</option>
                                            <option {{ request()->input('sell_or_buy') == 'sell' }} value="sell">فروشی
                                            </option>
                                            <option {{ request()->input('sell_or_buy') == 'buy' ? 'selected' : '' }} value="buy">
                                                درخواستی
                                            </option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="is__in__hoome">ملک در حومه شهر واقع است</label>
                                        <select name="is_in_hoome" id="is__in__hoome" class="form-control">
                                            <option value="all">:: بدون انتخاب ::</option>
                                            <option {{ request()->input('is_in_hoome') == 'no' ? 'selected' : '' }} value="no">
                                                خیر
                                            </option>
                                            <option {{ request()->input('is_in_hoome') == 'yes' ? 'selected' : '' }} value="yes">
                                                بله
                                            </option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="sanad_edari">دارای سند اداری</label>
                                        <select name="sanad_edari" id="sanad_edari" class="form-control">
                                            <option {{ request()->input('sanad_edari') == 'yes' ? 'selected' : '' }} value="yes">
                                                بله
                                            </option>
                                            <option {{ request()->input('sanad_edari') == 'no' ? 'selected' : '' }} value="no">
                                                خیر
                                            </option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        @endif
                        @if(request()->input('category_id') == 4)
                            <div class="row estate-options-row" id="optional_row_4">
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="____meters___">متراژ از(متر مربع):</label>
                                        <input value="{{ request()->input('meters_from') }}" type="text"
                                               name="meters_from"
                                               id="____meters___" class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="____meters___">تا:</label>
                                        <input value="{{ request()->input('meters_to') }}" type="text" name="meters_to"
                                               id="____meters___" class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="__vadieee_type_in_category__4">نوع ودیعه</label>
                                        <select class="form-control" name="price_type"
                                                id="__vadieee_type_in_category__4">
                                            <option value="all">:: بدون انتخاب ::</option>
                                            <option {{ request()->input('price_type') == 'maghtoo' ? 'selected' : '' }} value="maghtoo">
                                                مقطوع
                                            </option>
                                            <option {{ request()->input('price_type') == 'tavafoghi' ? 'selected' : '' }} value="tavafoghi">
                                                توافقی
                                            </option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="vadieee____price___in_category__4">مبلغ ودیعه از :</label>
                                        <input value="{{ request()->input('pre_pay_ejare_from') }}"
                                               name="pre_pay_ejare_from"
                                               type="number" id="vadieee____price___in_category__4"
                                               class="form-control">
                                    </div>
                                    <script>
                                        $('#__vadieee_type_in_category__4').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#vadieee____price___in_category__4 , #vadieee____to_in_category__4').removeAttr('disabled');
                                            } else {
                                                $('#vadieee____price___in_category__4 , #vadieee____to_in_category__4').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="vadieee____to_in_category__4"> تا:</label>
                                        <input value="{{ request()->input('pre_pay_ejare_to') }}"
                                               name="pre_pay_ejare_to"
                                               type="number" id="vadieee____to_in_category__4"
                                               class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="___ejare__type__in_category__4">نوع اجاره</label>
                                        <select class="form-control" name="ejare__type"
                                                id="___ejare__type__in_category__4">
                                            <option value="all">:: بدون انتخاب ::</option>
                                            <option {{ request()->input('ejare__type')  == 'maghtoo' ? 'selected' : '' }} value="maghtoo">
                                                مقطوع
                                            </option>
                                            <option {{ request()->input('ejare__type') == 'tavafoghi' ? 'selected' : '' }} value="tavafoghi">
                                                توافقی
                                            </option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="__ejare__price__from_in_category__4">مبلغ اجاره از :</label>
                                        <input value="{{ request()->input('monthly_price_ejare_from') }}" type="number"
                                               name="monthly_price_ejare_from" id="__ejare__price__from_in_category__4"
                                               class="form-control">
                                    </div>
                                    <script>
                                        $('#___ejare__type__in_category__4').change(function () {
                                            var val = $(this).val();
                                            if (val == 'maghtoo') {
                                                $('#__ejare__price__from_in_category__4 , #__ejare__price__to_in_category__4').removeAttr('disabled');
                                            } else {
                                                $('#__ejare__price__from_in_category__4 , #__ejare__price__to_in_category__4').attr('disabled', 'disabled');
                                            }
                                        });
                                    </script>
                                </div>
                                <div class="col-xs-3">
                                    <div class="form-group">
                                        <label for="__ejare__price__to_in_category__4">تا :</label>
                                        <input value="{{ request()->input('monthly_price_ejare_to') }}" type="number"
                                               name="monthly_price_ejare_to" id="__ejare__price__to_in_category__4"
                                               class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="__ro__oms___count">تعداد خواب</label>
                                        <input value="{{ request()->input('rooms_count') }}" name="rooms_count"
                                               type="number" min="0" id="__ro__oms___count"
                                               class="form-control">
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="sell__o__r__buy__">نوع آگهی</label>
                                        <select name="sell_or_buy" id="sell__o__r__buy__" class="form-control">
                                            <option value="all">:: بدون انتخاب ::</option>
                                            <option {{ request()->input('sell_or_buy') == 'sell' ? 'selected' : '' }} value="sell">
                                                ارائه
                                            </option>
                                            <option {{ request()->input('sell_or_buy') == 'buy' ? 'selected' : '' }} value="buy">
                                                درخواستی
                                            </option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-xs-6">
                                    <div class="form-group">
                                        <label for="is__in__ho__ome__">ملک در حومه شهر واقع است</label>
                                        <select name="is_in_hoome" id="is__in__ho__ome__" class="form-control">
                                            <option value="all">:: بدون انتخاب ::</option>
                                            <option {{ request()->input('is_in_hoome') == 'no' ? 'selected' : '' }} value="no">
                                                خیر
                                            </option>
                                            <option {{ request()->input('is_in_hoome') == 'yes' ? 'selected' : '' }} value="yes">
                                                بله
                                            </option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        @endif
                        <script>
                            $('#sub_category_in_filter').change(function () {
                                var value = $(this).val();
                                if (!isNaN(value)) {
                                    $('#estateSearchForm').submit();
                                }
                            });

                            @if(request()->has('category_id'))
                                $(document).ready(function () {
                                var categoryId = {{ request()->input('category_id') }};
                                $.ajax({
                                    type: 'GET',
                                    url: '{{ url()->to('/admin/estates/ads/categories/') }}/' + categoryId + '/children',
                                    success: function (response) {
                                        var list = response.list;
                                        var subCategory = $('#sub_category_in_filter');
                                        subCategory.html('<option value="all">:: بدون انتخاب ::</option>');
                                        for (var i = 0; i < list.length; i++) {
                                            subCategory.append('<option value="' + list[i].id + '">' + list[i].name + '</option>');
                                        }
                                        @if(request()->has('sub_category_id'))
                                        subCategory.val({{ request()->input('sub_category_id') }});
                                        @endif
                                    }
                                });
                            });
                            @endif
                        </script>
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="keyword-in-filter">کلمه کلیدی:</label>
                                    <input value="{{ request()->input('keyword') }}" type="text"
                                           placeholder="عنوان یا بخشی از توضیحات آگهی را وارد کرده و جستجو کنید ..."
                                           name="keyword" id="keyword-in-filter" class="form-control">
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <button type="submit" class="btn btn-success">فیلتر</button>
                            </div>
                        </div>
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
                                    <th>نوع ثبت کننده</th>
                                    <th>در حومه شهر</th>
                                    <th>نوع</th>
                                    <th>دسته بندی</th>
                                    <th>تعداد اتاق</th>
                                    <th>متراژ(متر مربع)</th>
                                    <th>استان - شهر - منطقه</th>
                                    <th>وضعیت اعتبار</th>
                                    <th>ثبت کننده</th>
                                    {{--<th><i class="fa fa-thumbs-up text-primary"></i></th>
                                    <th><i class="fa fa-thumbs-down text-danger"></i></th>--}}
                                    <th></th>
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
                                        </td>
                                        <td>
                                            {{ $ad->user_type == 'moshaver_amlak' ? 'مشاور املاک' : 'شخصی' }}
                                        </td>
                                        <td>
                                            {{ $ad->is_in_hoome == 1 ? 'بلی' : 'خیر' }}
                                        </td>
                                        <td>
                                            {{ $ad->sell_or_buy == 'sell' ? 'فروشی' : 'درخواستی' }}
                                        </td>
                                        <td>
                                            @if($ad->category_id == 1 || $ad->category_parent_id == 1)
                                                فروش مسکونی
                                            @elseif($ad->category_id == 2 || $ad->category_parent_id == 2)
                                                اجاره مسکونی
                                            @elseif($ad->category_id == 3 || $ad->category_parent_id == 3)
                                                فروش اداری تجاری
                                            @elseif($ad->category_id == 4 || $ad->category_parent_id == 4)
                                                اجاره اداری تجاری
                                            @elseif($ad->category_id == 5 || $ad->category_parent_id == 5)
                                                خدمات املاک

                                            @endif
                                        </td>
                                        <td>
                                            @if($ad->rooms_count == 0 )
                                                بدون اتاق
                                            @elseif($ad->rooms_count >= 5)
5 اتاق یا بیشتر
                                            @else
{{ $ad->rooms_count }}
                                                اتاق
                                            @endif
                                        </td>
                                        <td>{{ $ad->meters }} متر مربع</td>
                                        <td>{{ $ad->province_name }} - {{ $ad->city_name }}
                                            - {{ $ad->region_name }}</td>
                                        {{--<td>{{ $ad->likes }}</td>
                                        <td>{{ $ad->dislikes }}</td>--}}
                                        <td>
                                            @if(\Carbon\Carbon::createFromFormat('Y-m-d' , $ad->valid_until)->getTimestamp() > \Carbon\Carbon::now()->getTimestamp())
                                                <span class="text-success">معتبر تا تاریخ
                                                    {{ \App\Libraries\jdf::jdate('j-F-Y' , \Carbon\Carbon::createFromFormat('Y-m-d' , $ad->valid_until)->getTimestamp()) }}
                                                    </span>
                                            @else
                                                <span class="text-danger">منقضی شده
                                                </span>
                                            @endif
                                        </td>
                                        <td>{{ $ad->user_first_name }} {{ $ad->user_last_name }}</td>
                                        <td>
                                            <a href="{{ route('estate.ads.delete' , $ad->id) }}"
                                               class="text-primary">حذف آگهی</a><br>
                                            <a href="{{ route('estateAds.update.show' , $ad->id) }}"
                                               class="text-primary">مشاهده / ویرایش آگهی</a>
                                            <br>
                                            <a href="{{ route('estateAds.photos.show' , $ad->id) }}"
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
                                        </td>
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
                                    <a href="{{ route('showAllEstateAdsInAdminPanel' , [
                                    'page' => $ads->currentPage() - 1,
                                    'province_id' => request()->input('province_id'),
                                    'city_id' => request()->input('city_id'),
                                    'region_id' => request()->input('region_id'),
                                    'user_type' => request()->input('user_type'),
                                    'is_in_hoome' => request()->input('is_in_hoome'),
                                    'sell_or_buy' => request()->input('sell_or_buy'),
                                    'ejare_or_kharid' => request()->input('ejare_or_kharid'),
                                    'price_from' => request()->input('price_from'),
                                    'price_to' => request()->input('price_to'),
                                    'vadie_from' => request()->input('vadie_from'),
                                    'vadie_to' => request()->input('vadie_to'),
                                    'ejare_from' => request()->input('ejare_from'),
                                    'ejare_to' => request()->input('ejare_to'),
                                    'meters_from' => request()->input('meters_from'),
                                    'meters_to' => request()->input('meters_to'),
                                    'rooms_count' => request()->input('rooms_count'),
                                    'karbari_type' => request()->input('karbari_type'),
                                    'sanad_edari' => request()->input('sanad_edari'),
                                    'status' => request()->input('status')
                                    ]) }}"><i class="fa fa-angle-left"></i></a>
                                </li>
                            @endif
                            @php
                                $ellipsisWrote = false;
                            @endphp
                            @for($i = 1 ; $i <= $ads->lastPage() ; $i++)
                                @if( ($i >= 1 && $i <=3) || ( $i <= $ads->lastPage() && $i >= $ads->lastPage() - 2) || ($i >= $ads->currentPage() && $i <= $ads->currentPage() + 2) || ($i <= $ads->currentPage() &&  $i >= $ads->currentPage() - 2) )
                                    <li class="{{ $i == $ads->currentPage()  ? 'active' : '' }}">
                                        <a href="{{ route('showAllEstateAdsInAdminPanel' , [
                                    'page' => $i,
                                    'province_id' => request()->input('province_id'),
                                    'city_id' => request()->input('city_id'),
                                    'region_id' => request()->input('region_id'),
                                    'user_type' => request()->input('user_type'),
                                    'is_in_hoome' => request()->input('is_in_hoome'),
                                    'sell_or_buy' => request()->input('sell_or_buy'),
                                    'ejare_or_kharid' => request()->input('ejare_or_kharid'),
                                    'price_from' => request()->input('price_from'),
                                    'price_to' => request()->input('price_to'),
                                    'vadie_from' => request()->input('vadie_from'),
                                    'vadie_to' => request()->input('vadie_to'),
                                    'ejare_from' => request()->input('ejare_from'),
                                    'ejare_to' => request()->input('ejare_to'),
                                    'meters_from' => request()->input('meters_from'),
                                    'meters_to' => request()->input('meters_to'),
                                    'rooms_count' => request()->input('rooms_count'),
                                    'karbari_type' => request()->input('karbari_type'),
                                    'sanad_edari' => request()->input('sanad_edari'),
                                    'status' => request()->input('status')
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
                                    <a href="{{ route('showAllEstateAdsInAdminPanel' , [
                                    'page' => $ads->currentPage() + 1,
                                    'province_id' => request()->input('province_id'),
                                    'city_id' => request()->input('city_id'),
                                    'region_id' => request()->input('region_id'),
                                    'user_type' => request()->input('user_type'),
                                    'is_in_hoome' => request()->input('is_in_hoome'),
                                    'sell_or_buy' => request()->input('sell_or_buy'),
                                    'ejare_or_kharid' => request()->input('ejare_or_kharid'),
                                    'price_from' => request()->input('price_from'),
                                    'price_to' => request()->input('price_to'),
                                    'vadie_from' => request()->input('vadie_from'),
                                    'vadie_to' => request()->input('vadie_to'),
                                    'ejare_from' => request()->input('ejare_from'),
                                    'ejare_to' => request()->input('ejare_to'),
                                    'meters_from' => request()->input('meters_from'),
                                    'meters_to' => request()->input('meters_to'),
                                    'rooms_count' => request()->input('rooms_count'),
                                    'karbari_type' => request()->input('karbari_type'),
                                    'sanad_edari' => request()->input('sanad_edari'),
                                    'status' => request()->input('status')
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