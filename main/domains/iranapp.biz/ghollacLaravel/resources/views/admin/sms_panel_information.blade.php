@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">تغییر اطلاعات پنل پیامک</h1>
                    <form action="{{ route('smsPanelInformationUpdate') }}" method="post">
                        {{ method_field('PUT') }}
                        {{ csrf_field() }}
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="smsServiceName">نام پنل پیامک::</label>
                                    <input type="text" disabled value="نیاز پرداز" id="smsServiceName" class="form-control">
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="user_name">نام کاربری:</label>
                                    <input type="text" name="user_name" value="{{ $user_name }}" id="user_name" class="form-control">
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="password">رمز عبور:</label>
                                    <input type="text" name="password" value="{{ $password }}" id="password" class="form-control">
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