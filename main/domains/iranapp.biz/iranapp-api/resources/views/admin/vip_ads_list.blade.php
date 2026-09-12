@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">لیست آگهی های ویژه</h1>
                    <div class="table-responsive">
                        <table class="table m-0">
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>عنوان آگهی</th>
                                <th>دسته - زیر دسته</th>
                                <th>شهر - استان</th>
                                <th>نمایش منطقه ای</th>
                                <th>نمایش در دسته</th>
                                <th>نمایش در صفحه اصلی</th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            @foreach($ads as $ad)
                                <tr>
                                    <td>{{ $ad->vip_ads_id }}</td>
                                    <td>{{ $ad->title }}</td>
                                    <td>{{ $ad->category_name }} - {{ $ad->sub_category_name }}</td>
                                    <td>{{ $ad->province_name }} - {{ $ad->city_name }}</td>
                                    <td>
                                        @if($ad->show_in_country == 1)
                                            نمایش در کشور
                                        @elseif($ad->show_in_province == 1)
                                            نمایش در استان
                                        @elseif($ad->show_in_city == 1)
                                            نمایش در شهر
                                        @endif
                                    </td>
                                    <td>
                                        @if($ad->show_in_subcategory == 1)
                                            نمایش در زیر دسته
                                        @elseif($ad->show_in_category == 1)
                                            نمایش در دسته
                                        @endif
                                    </td>
                                    <td>
                                        @if($ad->show_in_main_page == 1)
                                            بله
                                        @else
خیر
                                        @endif
                                    </td>
                                    <td>
                                        <a href="{{ route('deleteVipAdsInAdminPanel' , $ad->vip_ads_id) }}" class="text-danger"><b>حذف</b></a><br>
                                        <a href="{{ route('showVipAdsUpdatePage' , $ad->vip_ads_id) }}" class="text-success"><b>ویرایش</b></a>
                                    </td>
                                </tr>
                            @endforeach
                            </tbody>
                        </table>
                    </div>

                    <ul class="pagination pagination-split">
                        @if($ads->currentPage() != 1)
                            <li>
                                <a href="{{ $ads->previousPageUrl() }}"><i class="fa fa-angle-left"></i></a>
                            </li>
                        @endif
                        @php
                            $ellipsisWrote = false;
                        @endphp
                        @for($i = 1 ; $i <= $ads->lastPage() ; $i++)
                            @if( ($i >= 1 && $i <=3) || ( $i <= $ads->lastPage() && $i >= $ads->lastPage() - 2) || ($i >= $ads->currentPage() && $i <= $ads->currentPage() + 2) || ($i <= $ads->currentPage() &&  $i >= $ads->currentPage() - 2) )
                                <li class="{{ $i == $ads->currentPage()  ? 'active' : '' }}">
                                    <a href="{{ $ads->url($i) }}">{{ $i }}</a>
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
                                <a href="{{ $ads->nextPageUrl() }}"><i class="fa fa-angle-right"></i></a>
                            </li>
                        @endif
                    </ul>

                </div>
            </div>
        </div>
    </div>
    @foreach($ads as $ad)
        <div id="re-validate_ad_{{ $ad->id }}_modal" class="modal fade" tabindex="-1" role="dialog"
             aria-labelledby="myModalLabel"
             aria-hidden="true">
            @php
                $plan = \App\Models\AdsPlan::find($ad->ads_plan_id)->first();
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
                            <button type="submit" class="btn btn-primary waves-effect waves-light">ذخیره تغیرات</button>
                        </div>
                    </form>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div>
    @endforeach
@endsection