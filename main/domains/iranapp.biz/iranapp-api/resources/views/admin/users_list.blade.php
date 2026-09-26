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
                        @if(count($list) > 0 )
                            <table class="table m-0">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>نام کامل</th>
                                    <th>تلفن همراه</th>
                                    <th>نقش</th>
                                    <th>احراز هویت با تلفن همراه</th>
                                    <th>ارسال اخبار با نوتیفیکیشن</th>
                                    <th>ارسال آگهی ها با نوتیفیکیشن</th>
                                    <th></th>
                                </tr>
                                </thead>
                                <tbody>
                                @foreach($list as $user)
                                    <tr>
                                        <td>{{ $user->id }}</td>
                                        <td>{{ $user->first_name }} {{ $user->last_name }}</td>
                                        <td>{{ $user->mobile }}</td>
                                        <td>
                                            @if(($user->role ?? 'normal') == 'pro')
                                                <span class="label label-success">کاربر پرو</span>
                                            @else
                                                <span class="label label-default">کاربر عادی</span>
                                            @endif
                                        </td>
                                        <td>{{ $user->is_mobile_verified == 1 ? 'بله' : 'خیر' }}</td>
                                        <td>{{ $user->send_news_notifications == 1 ? 'بله' : 'خیر' }}</td>
                                        <td>{{ $user->send_ads_notifications == 1 ? 'بله' : 'خیر' }}</td>
                                        <td>
                                            <a style="width:100px;margin-bottom:5px;" href="{{ route('showUserUpdatePage' , $user->id) }}" class="btn btn-success btn-xs">ویرایش</a><br>
                                            <a style="width:100px;margin-top:5px;" href="{{ route('deleteUserByIdInAdminPanel' , $user->id) }}" class="btn btn-danger btn-xs">حذف</a>
                                        </td>
                                    </tr>
                                @endforeach
                                </tbody>
                            </table>
                        @else
                            <div class="alert alert-success text-center">کاربری یافت نشد!</div>
                        @endif
                    </div>
                    {{ $list->links() }}
                </div>
            </div>
        </div>
    </div>
@endsection