@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">تغییر مشخصات کاربری</h1>
                    <form action="{{ route('updateUserInAdminPanel' , $user->id) }}" method="post">
                        <input type="hidden" name="_method" value="PUT">
                        {{ csrf_field() }}
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="first_name">نام:</label>
                                    <input type="text" name="first_name" value="{{ $user->first_name }}" id="first_name" class="form-control">
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="last_name">نام خانوادگی:</label>
                                    <input type="text" name="last_name" value="{{ $user->last_name }}" id="last_name" class="form-control">
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="password">رمز عبور:</label>
                                    <input type="password" name="password"  id="password" class="form-control">
                                </div>
                            </div>

                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="mobile">تلفن همراه</label>
                                    <input type="text" name="mobile" disabled value="{{ $user->mobile }}" id="mobile" class="form-control">
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="send_news_notification">ارسال اخبار با نوتیفیکیشن</label>
                                    <input type="checkbox" {{ $user->send_news_notifications == 1 ? 'checked' : '' }} name="send_news_notifications" id="send_news_notification">
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="send_ads_notification">ارسال آگهی ها  با نوتیفیکیشن</label>
                                    <input type="checkbox" {{ $user->send_ads_notifications == 1 ? 'checked' : '' }} name="send_ads_notifications" id="send_ads_notification">
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <button type="submit" class="btn btn-primary">ثبت</button>
                            </div>
                        </div>
                    </form>

                </div>
            </div>
        </div>
    </div>
@endsection