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
                                        getRegionsByCityId(cityId , null);
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
                                    <label for="education_level">میزان تحصیلات</label>
                                    <select name="education_level" id="education_level" class="form-control">
                                        <option value="all">:: بدون تفاوت ::</option>
                                        <option {{ request()->input('education_level') == 'underdiploma' ? 'selected' : '' }} value="underdiploma">زیر دیپلم</option>
                                        <option {{ request()->input('education_level') == 'diploma' ? 'selected' : '' }} value="diploma">دیپلم</option>
                                        <option {{ request()->input('education_level') == 'tact' ? 'selected' : '' }} value="tact">کاردانی</option>
                                        <option {{ request()->input('education_level') == 'expertise' ? 'selected' : '' }} value="expertise">کارشناسی</option>
                                        <option {{ request()->input('education_level') == 'masterdegree' ? 'selected' : '' }} value="masterdegree">کارشناسی ارشد</option>
                                        <option {{ request()->input('education_level') == 'doctoral' ? 'selected' : '' }} value="doctoral">دکتری و بالاتر</option>
                                    </select>
                                </div>
                            </div>{{--میزان تحصیلات--}}
                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label for="agremment_type">نوع قرارداد</label>
                                    <select name="agremment_type" id="agremment_type" class="form-control">
                                        <option value="all">:: بدون تفاوت ::</option>
                                        <option {{ request()->input('agremment_type') == 'tamamvaght' ? 'selected' : '' }} value="tamamvaght">تمام وقت</option>
                                        <option {{ request()->input('agremment_type') == 'parevaght' ? 'selected' : '' }} value="parevaght">پاره وقت</option>
                                        <option {{ request()->input('agremment_type') == 'moshaveri' ? 'selected' : '' }} value="moshaveri">مشاوره ای</option>
                                        <option {{ request()->input('agremment_type') == 'projei' ? 'selected' : '' }} value="projei">پروژه ای</option>
                                    </select>
                                </div>
                            </div>{{--نوع قرارداد--}}
                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label for="brand">تخصص:</label>
                                    <select name="specialty" id="specialty" class="form-control">
                                        <option value="all">:: بدون تفاوت ::</option>
                                        @foreach($specialtys as $specialty)
                                            <option value="{{ $specialty->id }}">{{ $specialty->name }}</option>
                                        @endforeach
                                    </select>
                                </div>
                            </div>{{--تخصص--}}
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
                            </div>{{--وضعیت--}}
                            <div class="col-xs-6 col-md-4">
                                <div style="margin-top:31px;">
                                    <label>نوع آگهی</label>

                                    <label class="radio-inline" for="karjoo_radio_button">
                                        <input id="karjoo_radio_button" class="btnn" type="radio" value="karjoo" name="type">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; آماده به کار
                                    </label>
                                    <label class="radio-inline" for="forsatshoghli_radio_button">
                                        <input id="forsatshoghli_radio_button" class="btnn" value="forsatshoghli" type="radio" name="type">&nbsp;&nbsp;&nbsp;&nbsp;  استخدام
                                    </label>
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-4">
                                <div class="form-group">
                                    <label for="person-or-company-in-filter">
                                        شرکت / شخص:
                                        <b class="input-field-errors">*</b>
                                    </label>
                                    <select name="person_or_company" id="person-or-company-in-filter" class="form-control">
                                        <option  value="all">:: انتخاب کنید ::</option>
                                        <option {{ request()->input('person_or_company') ==  'company' ? 'selected' : ''  }} value="company">شرکت / فروشگاه</option>
                                        <option {{ request()->input('person_or_company') ==  'person' ? 'selected' : ''  }} value="person">شخص</option>
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
                                    <td>میزان تحصیلات</td>
                                    <td>نوع قرارداد</td>
                                    <td>تخصص</td>
                                    <th>نوع</th>
                                    <th>وضعیت اعتبار</th>
                                    <th>استان - شهر - منطقه</th>
                                    <th>ثبت کننده</th>
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
                                            @if($ad->education_level == 'underdiploma')
                                                <span>زیر دیپلم</span>
                                            @elseif($ad->education_level == 'diploma')
                                                <span>دیپلم</span>
                                            @elseif($ad->education_level == 'tact')
                                                <span>کاردانی</span>
                                            @elseif($ad->education_level == 'expertise')
                                                <span>کارشناسی</span>
                                            @elseif($ad->education_level == 'masterdegree')
                                                <span>کارشناسی ارشد</span>
                                            @elseif($ad->education_level == 'doctoral')
                                                <span>دکتری و بالاتر</span>
                                            @endif
                                        </td>{{--میزان تحصیلات--}}
                                        <td>
                                            @if($ad->agremment_type == 'tamamvaght')
                                                <span>تمام وقت</span>
                                            @elseif($ad->agremment_type == 'parevaght')
                                                <span>پاره وقت</span>
                                            @elseif($ad->agremment_type == 'moshaveri')
                                                <span>مشاوره ای</span>
                                            @elseif($ad->agremment_type == 'projei')
                                                <span>پروژه ای</span>
                                            @endif
                                        </td>{{--نوع قرارداد--}}
                                        <td>
                                            <span>{{ $ad->specialty }}</span>
                                        </td>{{--تخصص--}}
                                        <td>

                                            @if($ad->type == 'karjoo')
                                                <span>آماده به کار </span>
                                            @elseif($ad->type == 'forsatshoghli')
                                                <span>استخدام</span>
                                            @endif
                                        </td>{{--نوع --}}

                                        <td>
                                            @if(\Carbon\Carbon::createFromFormat('Y-m-d' , $ad->valid_until)->getTimestamp() > \Carbon\Carbon::now()->getTimestamp())
                                                <span class="text-success">معتبر تا تاریخ
                                                    {{ \App\Libraries\jdf::jdate('j-F-Y' , \Carbon\Carbon::createFromFormat('Y-m-d' , $ad->valid_until)->getTimestamp()) }}
                                                    </span>
                                            @else
                                                <span class="text-danger">منقضی شده
                                                </span>
                                            @endif
                                        </td>{{--معتبر--}}
                                        <td>{{ $ad->province_name }} - {{ $ad->city_name }}- {{ $ad->region_name }}</td>
                                        <td>{{ $ad->user_first_name }} {{ $ad->user_last_name }}</td>
                                        <td>
                                            <a href="{{ route('employs.ads.delete' , $ad->id) }}"
                                               class="text-primary">حذف آگهی</a><br>
                                            <a href="{{ route('employsAds.update.show' , $ad->id) }}"
                                               class="text-primary">مشاهده / ویرایش آگهی</a><br>
                                            <a href="{{ route('uploadEmploysAdsPhotoInAdminPanel' , $ad->id) }}"
                                               class="text-primary">تصاویر آگهی</a>
                                            <br>
                                            @php
                                                $vipExists = \App\VipAds::where('ads_id' , $ad->id)->count();
                                            if($vipExists){
                                            $vip = \App\VipAds::where('ads_id' , $ad->id)->first();
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
                                    <a href="{{ route('showAllEmploysAdsInAdminPanel' , [
                                    'page' => $ads->currentPage() - 1,
                                    'province_id' => request()->input('province_id'),
                                    'city_id' => request()->input('city_id'),
                                    'region_id' => request()->input('region_id'),
                                    'vadie_from' => request()->input('vadie_from'),
                                    'vadie_to' => request()->input('vadie_to'),
                                    'status' => request()->input('status'),

                                    'type' => request()->input('type'),
                                    'specialty' => request()->input('specialty'),
                                    'education_level' => request()->input('education_level'),
                                    'agremment_type' => request()->input('agremment_type'),

                                    ]) }}"><i class="fa fa-angle-left"></i></a>
                                </li>
                            @endif
                            @php
                                $ellipsisWrote = false;
                            @endphp
                            @for($i = 1 ; $i <= $ads->lastPage() ; $i++)
                                @if( ($i >= 1 && $i <=3) || ( $i <= $ads->lastPage() && $i >= $ads->lastPage() - 2) || ($i >= $ads->currentPage() && $i <= $ads->currentPage() + 2) || ($i <= $ads->currentPage() &&  $i >= $ads->currentPage() - 2) )
                                    <li class="{{ $i == $ads->currentPage()  ? 'active' : '' }}">
                                        <a href="{{ route('showAllEmploysAdsInAdminPanel' , [
                                    'page' => $i,
                                    'province_id' => request()->input('province_id'),
                                    'city_id' => request()->input('city_id'),
                                    'region_id' => request()->input('region_id'),
                                    'vadie_from' => request()->input('vadie_from'),
                                    'vadie_to' => request()->input('vadie_to'),
                                    'status' => request()->input('status'),

                                    'type' => request()->input('type'),
                                    'specialty' => request()->input('specialty'),
                                    'education_level' => request()->input('education_level'),
                                    'agremment_type' => request()->input('agremment_type'),

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
                                    <a href="{{ route('showAllEmploysAdsInAdminPanel' , [
                                    'page' => $ads->currentPage() + 1,
                                    'province_id' => request()->input('province_id'),
                                    'city_id' => request()->input('city_id'),
                                    'region_id' => request()->input('region_id'),
                                    'vadie_from' => request()->input('vadie_from'),
                                    'vadie_to' => request()->input('vadie_to'),
                                    'status' => request()->input('status'),

                                    'type' => request()->input('type'),
                                    'specialty' => request()->input('specialty'),
                                    'education_level' => request()->input('education_level'),
                                    'agremment_type' => request()->input('agremment_type'),

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