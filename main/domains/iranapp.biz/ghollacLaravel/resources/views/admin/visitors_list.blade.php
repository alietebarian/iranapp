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
                                    <label for="mobile-in-filter">تلفن همراه کاربر </label>
                                    <input type="text" value="{{ request()->input('mobile') }}" name="mobile" id="mobile-in-filter" class="form-control">
                                </div>
                            </div>
                            <div class="col-xs-6 col-md-3">
                                <div class="form-group">
                                    <label for="full_name_in_filter">نام و نام خانوادگی کاربر </label>
                                    <input type="text" value="{{ request()->input('full_name') }}" name="full_name" id="full_name_in_filter" class="form-control">
                                </div>
                            </div>

                        </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <button type="submit" class="btn btn-success">فیلتر</button>
                            </div>
                        </div>
                    </form>
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">لیست کاربران</h1>
                    <div class="table-responsive">
                        @if(count($visitors) > 0 )
                            <table class="table m-0">
                                <thead>
                                <tr>
                                    <th>ردیف</th>
                                    <th>نام کامل</th>
                                    <th>تلفن همراه</th>
                                    <th>وضعیت</th>
                                    <th>تعداد کل آگهی های ثبت شده</th>
                                    <th>تعداد آگهی های ثبت شده در یک ماه</th>
                                    <th>تعداد آگهی ثبت شده ی امروز</th>
                                    <th>تنظیمات</th>
                                </tr>
                                </thead>
                                <tbody>
                                @foreach($visitors as $visitor)
                                    <tr>
                                        <td>{{ $visitor->id }}</td>
                                        <td>{{ $visitor->first_name }} {{ $visitor->last_name }}</td>
                                        <td>{{ $visitor->mobile }}</td>
                                        <td>
                                            <div class="btn-group dropdown changeStatus">
                                                @if($visitor->status == 'active')
                                                    <button type="button" class="btn btn-primary btn-xs waves-effect waves-light">فعال</button>
                                                    <button type="button" class="btn btn-primary btn-xs dropdown-toggle waves-effect waves-light" data-toggle="dropdown" aria-expanded="false"><i class="caret"></i></button>
                                                    <ul class="dropdown-menu" role="menu">
                                                        <li><a href="{{ route('changeStatusOfVisitor' , ['visitor' => $visitor->id , 'status' => 'active']) }}">فعال</a></li>
                                                        <li><a href="{{ route('changeStatusOfVisitor' , ['visitor' => $visitor->id , 'status' => 'deactive']) }}">غیرفعال</a></li>
                                                    </ul>
                                                    @else
                                                    <button type="button" class="btn btn-danger btn-xs waves-effect waves-light">غیر فعال</button>
                                                    <button type="button" class="btn btn-danger btn-xs dropdown-toggle waves-effect waves-light" data-toggle="dropdown" aria-expanded="false"><i class="caret"></i></button>
                                                    <ul class="dropdown-menu" role="menu">
                                                        <li><a href="{{ route('changeStatusOfVisitor' , ['visitor' => $visitor->id , 'status' => 'active']) }}">فعال</a></li>
                                                        <li><a href="{{ route('changeStatusOfVisitor' , ['visitor' => $visitor->id , 'status' => 'deactive']) }}">غیرفعال</a></li>
                                                    </ul>
                                                @endif
                                            </div>
                                        </td>
                                        <td class="text-center">{{ $visitor->ads_count }}</td>
                                        <td class="text-center">{{ $visitor->ads_month_count }}</td>
                                        <td class="text-center">{{ $visitor->ads_day_count }}</td>
                                        <td>
                                            <a style="width:100px;margin-bottom:5px;" href="{{ route('showVisitorUpdatePage' , $visitor->id) }}" class="btn btn-success btn-xs">ویرایش</a><br>
                                            <a style="width:100px;margin-top:5px;" href="{{ route('showAdsVisitor' , $visitor->id) }}" class="btn btn-info btn-xs">مشاهده آگهی ها</a>
                                        </td>
                                    </tr>
                                @endforeach
                                </tbody>
                            </table>
                        @else
                            <div class="alert alert-success text-center">کاربری یافت نشد!</div>
                        @endif
                    </div>
                    @if(count($visitors) > 0 )
                        <ul class="pagination pagination-split">
                            @if($visitors->currentPage() != 1)
                                <li>
                                    <a href="{{ $visitors->previousPageUrl() }}"><i class="fa fa-angle-left"></i></a>
                                </li>
                            @endif
                            @php
                                $ellipsisWrote = false;
                            @endphp
                            @for($i = 1 ; $i <= $visitors->lastPage() ; $i++)
                                @if( ($i >= 1 && $i <=3) || ( $i <= $visitors->lastPage() && $i >= $visitors->lastPage() - 2) || ($i >= $visitors->currentPage() && $i <= $visitors->currentPage() + 2) || ($i <= $visitors->currentPage() &&  $i >= $visitors->currentPage() - 2) )
                                    <li class="{{ $i == $visitors->currentPage()  ? 'active' : '' }}">
                                        <a href="{{ $visitors->url($i) }}">{{ $i }}</a>
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
                            @if($visitors->hasMorePages())
                                <li>
                                    <a href="{{ $visitors->nextPageUrl() }}"><i class="fa fa-angle-right"></i></a>
                                </li>
                            @endif
                        </ul>
                    @endif
                </div>
            </div>
        </div>
    </div>
@endsection