@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">فیلتر</h1>
                    <form action="" method="get">
                        <div class="row">
                            <div class="col-xs-6 col-md-3">
                                <div class="form-group">
                                    <label for="ads_plan_in_filter">پلن آگهی</label>
                                    <select name="plan_id" id="ads_plan_in_filter" class="form-control">
                                        <option {{ (! request()->input('plan_id') || request()->input('plan_id') == 'all') ? 'selected' : '' }} value="all">
                                            بدون انتخاب
                                        </option>
                                        @foreach($plans as $plan)
                                            <option {{ request()->input('plan_id') == $plan->id ? 'selected' : '' }} value="{{ $plan->id }}">{{ $plan->plan_title }}</option>
                                        @endforeach
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-6 col-md-3">
                                <div class="form-group">
                                    <label for="city_in_filter">شهر</label>
                                    <select data-live-search="true" name="city_id" id="city_in_filter"
                                            class="form-control selectpicker btn-default">
                                        <option {{ (! request()->has('city_id') || request()->input('city_id') == 'all') ? 'selected' : '' }} value="all">
                                            بدون انتخاب
                                        </option>
                                        @foreach($provinces as $province)
                                            <optgroup label="استان {{ $province->name }}">
                                                @php
                                                    $cities = \App\Models\Province::find($province->id)->city;
                                                @endphp
                                                @foreach($cities as $city)
                                                    <option {{ $city->id == request()->input('city_id') ? 'selected' : '' }} value="{{ $city->id }}">{{ $city->name }}</option>
                                                @endforeach
                                            </optgroup>
                                        @endforeach
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-6 col-md-3">
                                <div class="form-group">
                                    <label for="category_in_filter">دسته بندی</label>
                                    <select data-live-search="true" name="sub_category_id" id="category_in_filter"
                                            class="form-control selectpicker btn-default">
                                        <option {{ ( !request()->has('sub_category_id') ||  request()->input('sub_category_id') == 'all') ? 'selected' : ''  }} value="all">
                                            بدون انتخاب
                                        </option>
                                        @foreach($categories as $category)
                                            <optgroup label="{{ $category->name }}">
                                                @php
                                                    $subcategories = $category->subCategories;
                                                @endphp
                                                @foreach($subcategories as $subcategory)
                                                    <option {{ $subcategory->id == request()->input('sub_category_id') ? 'selected' : '' }} value="{{ $subcategory->id }}">{{ $subcategory->name }}</option>
                                                @endforeach
                                            </optgroup>
                                        @endforeach
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-6 col-md-3">
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
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="keyword-in-filter">کلمه کلیدی:</label>
                                    <input value="{{ request()->input('keyword') }}" type="text"
                                           placeholder="عنوان، توضیحات یا بخشی از آدرس آگهی را بنویسید و جستجو کنید..."
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
                    <h1 style="font-size:14px;font-weight: bold;"
                        class="text-pink">{{Request::is('admin/ads/list') ? 'لیست آگهی ها': 'لیست تخفیف ها'}}</h1>
                    <form action="{{ route('doSomeActionsOnPost') }}" method="post">
                        {{ csrf_field() }}
                        <div class="table-responsive">
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
                                        @if(Request::is('admin/ads/discount/list'))
                                            <th>میزان تخفیف</th>
                                            <th>شماره موبایل</th>
                                        @endif
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
                                            @if(Request::is('admin/ads/discount/list'))
                                                <th>{{$ad->discount}}%</th>
                                                <th>{{$ad->mobile}}</th>
                                            @endif
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
                                                   class="text-primary">تصاویر و ویدیو آگهی</a><br>
                                                @php
                                                    $vipExists = \App\Models\VipAds::where('ads_id' , $ad->id)->count();
                                                if($vipExists){
                                                $vip = \App\Models\VipAds::where('ads_id' , $ad->id)->first();
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
                        </div>
                        @if(count($ads) > 0 )
                            <div>
                                <div class="row">
                                    <div class="col-xs-1">
                                        <label for="action">عملیات گروهی</label>
                                    </div>
                                    <div class="col-xs-2">
                                        <select name="action" id="action" class="form-control">
                                            <option value="do_nothing">:: انتخاب کنید ::</option>
                                            <option value="delete">حذف</option>
                                        </select>
                                    </div>
                                    <div class="col-xs-1">
                                        <button type="submit" class="btn btn-primary btn-block">انجام بده!</button>
                                    </div>
                                </div>
                            </div>
                        @endif
                    </form>
                    @if(count($ads) > 0 )
                        <ul class="pagination pagination-split">
                            @if($ads->currentPage() != 1)
                                <li>
                                    <a href="{{ route('showAdsListInAdminPanel' , [
                                    'page' => $ads->currentPage() - 1,
                                    'plan_id' => request()->input('plan_id'),
                                    'city_id' => request()->input('city_id'),
                                    'sub_category_id' => request()->input('sub_category_id'),
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
                                        <a href="{{ route('showAdsListInAdminPanel' , [
                                    'page' => $i,
                                    'plan_id' => request()->input('plan_id'),
                                    'city_id' => request()->input('city_id'),
                                    'sub_category_id' => request()->input('sub_category_id'),
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
                                    <a href="{{ route('showAdsListInAdminPanel' , [
                                    'page' => $ads->currentPage() + 1,
                                    'plan_id' => request()->input('plan_id'),
                                    'city_id' => request()->input('city_id'),
                                    'sub_category_id' => request()->input('sub_category_id'),
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
