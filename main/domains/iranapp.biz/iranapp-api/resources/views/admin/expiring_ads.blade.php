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
                            <div class="col-xs-6 col-md-3">
                                <div class="form-group">
                                    <label for="ads_plan_in_filter">فیلتر زمانی</label>
                                    <select name="interval_days" id="ads_plan_in_filter" class="form-control">
                                        @if(!request()->has('filter'))
                                            <option value="without_selection">:: بدون انتخاب ::</option>
                                        @endif
                                        <option {{ request()->input('interval_days') == 'expired' ? 'selected' : '' }} value="expired">
                                            منقضی شده
                                        </option>
                                        <option {{ request()->input('interval_days') == '10' ? 'selected' : '' }} value="10">
                                            منقضی تا 10 روز آینده
                                        </option>
                                        <option {{ request()->input('interval_days') == '15' ? 'selected' : '' }} value="15">
                                            منقضی تا 15 روز آینده
                                        </option>
                                        <option {{ request()->input('interval_days') == '30' ? 'selected' : '' }} value="30">
                                            منقضی تا 30 روز آینده
                                        </option>
                                        <option {{ request()->input('interval_days') == '90' ? 'selected' : '' }} value="90">
                                            منقضی تا 3 ماه آینده
                                        </option>
                                        <option {{ request()->input('interval_days') == '180' ? 'selected' : '' }} value="180">
                                            منقضی تا 6 ماه آینده
                                        </option>
                                        <option {{ request()->input('interval_days') == '360' ? 'selected' : '' }} value="360">
                                            منقضی تا 12 ماه آینده
                                        </option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <button name="filter" value="doFilter" type="submit" class="btn btn-success">فیلتر
                                </button>
                            </div>
                        </div>
                    </form>
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">لیست آگهی های در حال انقضاء</h1>
                    <form action="{{ route('doSomeActionsOnPost') }}" method="post">
                        {{ csrf_field() }}
                        <div class="table-responsive">
                            @if($filter_set == 0 )
                                <div class="alert alert-success text-center">لطفا برای یافتن آگهی های در حال انقضاء
                                    فیلتر مورد نظر را تنظیم کنید
                                </div>
                            @else
                                @if(count($ads) > 0 )
                                    <table class="table m-0">
                                        <thead>
                                        <tr>
                                            <th></th>
                                            <th>#</th>
                                            <th>عنوان آگهی</th>
                                            <th>پلن آگهی</th>
                                            <th>وضعیت آگهی</th>
                                            <th>استان و شهر</th>
                                            <th>دسته بندی</th>
                                            <th><i class="fa fa-thumbs-up text-primary"></i></th>
                                            <th><i class="fa fa-thumbs-down text-danger"></i></th>
                                            <th>تاریخ انقضای آگهی</th>
                                            <th></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        @foreach($ads as $ad)
                                            <tr>
                                                <td>
                                                    <label for="ads_{{ $ad->id }}">
                                                        <input type="checkbox" name="ads_id[]" id="ads_{{ $ad->id }}"
                                                               value="{{ $ad->id }}">
                                                    </label>
                                                </td>
                                                <td>{{ $ad->id }}</td>
                                                <td>{{ $ad->title }}</td>
                                                <td>{{ $ad->plan_title }}</td>
                                                <td>
                                                    @if($ad->status == 'pending')
                                                        <span class="text-warning">در انتظار تایید</span>
                                                    @elseif($ad->status == 'approved')
                                                        <span class="text-success">تایید شده</span>
                                                    @elseif($ad->status == 'rejected')
                                                        <span class="text-danger">رد شده</span>
                                                    @endif
                                                </td>
                                                <td>{{ $ad->province_name }} - {{ $ad->city_name }}</td>
                                                <td>{{ $ad->category_name }} - {{ $ad->sub_category_name }}</td>
                                                <td>{{ $ad->likes }}</td>
                                                <td>{{ $ad->dislikes }}</td>
                                                <td>
                                                    {{ \App\Libraries\jdf::jdate('j F Y' , \Carbon\Carbon::createFromFormat('Y-m-d' , $ad->valid_until)->getTimestamp()) }}
                                                    <br>
                                                    <a href="" data-toggle="modal"
                                                       data-target="#re-validate_ad_{{ $ad->id }}_modal"
                                                       class="text-success">تمدید آگهی</a>
                                                </td>
                                                <td>
                                                    <a href="{{ route('deleteAdsByIdInAdminPanel' , $ad->id) }}"
                                                       class="text-primary">حذف آگهی</a><br>
                                                    <a href="{{ route('showAdsUpdatePageInAdminPanel' , $ad->id) }}"
                                                       class="text-primary">مشاهده / ویرایش آگهی</a><br>
                                                    <a href="{{ route('showAdsPhotoById' , $ad->id) }}"
                                                       class="text-primary">تصاویر آگهی</a><br>
                                                    @php
                                                        $vipExists = \App\VipAds::where('ads_id' , $ad->id)->count();
                                                    if($vipExists){
                                                    $vip = \App\VipAds::where('ads_id' , $ad->id)->first();
                                                    }
                                                    @endphp
                                                    @if($vipExists)
                                                        <a href="{{ route('showVipAdsUpdatePage' , $vip->id) }}"
                                                           class="text-primary">ویرایش آگهی ویژه</a>
                                                    @else
                                                        <a href="{{ route('showVipAdsCreatePage' , $ad->id) }}"
                                                           class="text-primary">افزودن به آگهی های ویژه</a>
                                                    @endif
                                                </td>
                                            </tr>
                                        @endforeach
                                        </tbody>
                                    </table>
                                @else
                                    <div class="alert alert-success text-center">آگهی یافت نشد!</div>
                                @endif
                            @endif
                        </div>
                    </form>
                    @if($filter_set == 1 )
                        @if(count($ads) > 0 )
                            <ul class="pagination pagination-split">
                                @if($ads->currentPage() != 1)
                                    <li>
                                        <a href="{{ route('showExpiringAds' , [
                                    'page' => $ads->currentPage() - 1,
                                    'filter' => request()->input('filter'),
                                    'interval_days' => request()->input('interval_days')
                                    ]) }}"><i class="fa fa-angle-left"></i></a>
                                    </li>
                                @endif
                                @php
                                    $ellipsisWrote = false;
                                @endphp
                                @for($i = 1 ; $i <= $ads->lastPage() ; $i++)
                                    @if( ($i >= 1 && $i <=3) || ( $i <= $ads->lastPage() && $i >= $ads->lastPage() - 2) || ($i >= $ads->currentPage() && $i <= $ads->currentPage() + 2) || ($i <= $ads->currentPage() &&  $i >= $ads->currentPage() - 2) )
                                        <li class="{{ $i == $ads->currentPage()  ? 'active' : '' }}">
                                            <a href="{{ route('showExpiringAds' , [
                                    'page' => $i,
                                    'filter' => request()->input('filter'),
                                    'interval_days' => request()->input('interval_days')
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
                                        <a href="{{ route('showExpiringAds' , [
                                    'page' => $ads->currentPage() + 1,
                                    'filter' => request()->input('filter'),
                                    'interval_days' => request()->input('interval_days')
                                    ]) }}"><i class="fa fa-angle-right"></i></a>
                                    </li>
                                @endif
                            </ul>
                        @endif
                    @endif
                </div>
            </div>
        </div>
    </div>
    @if($filter_set == 1 && count($ads) > 0 )
        @foreach($ads as $ad)
            <div id="re-validate_ad_{{ $ad->id }}_modal" class="modal fade" tabindex="-1" role="dialog"
                 aria-labelledby="myModalLabel"
                 aria-hidden="true">
                @php
                    $plan = \App\AdsPlan::find($ad->ads_plan_id)->first();
                @endphp
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
                                <button type="submit" class="btn btn-primary waves-effect waves-light">ذخیره تغیرات
                                </button>
                            </div>
                        </form>
                    </div><!-- /.modal-content -->
                </div><!-- /.modal-dialog -->
            </div>
        @endforeach
    @endif
@endsection