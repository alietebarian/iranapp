@extends('home.master')
@section('content')
    <div class="col-md-6">
        <div class="registration_container">
            <h2 class="text-center login_section_title">به حساب کاربری خود وارد شوید</h2>
            <form action="{{ route('user.login') }}" method="POST" class="form-horizontal" id="loginForm">
                {{ csrf_field() }}
                <div class="form-group row">
                    <label for="email_for_login" class="control-label col-sm-2">ایمیل:</label>
                    <div class="col-sm-10">
                        <input type="text" name="email_in_login" id="email_for_login" class="form-control ">
                        @if($errors->has('email_in_login'))
                            <b class="input-errors">{{ $errors->first('email_in_login') }}</b>
                        @endif
                    </div>
                </div>
                <div class="form-group row">
                    <label for="password_for_login" class="control-label col-sm-2">رمز عبور</label>
                    <div class="col-sm-10">
                        <input type="password" name="password_in_login" id="password_for_login" class="form-control">
                        @if($errors->has('password_in_login'))
                            <b class="input-errors">{{ $errors->first('password_in_login') }}</b>
                        @endif
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-10 col-sm-offset-2">
                        <button type="submit" class="btn btn-primary p-10-30">ورود</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
    <div class="col-md-6">
        <div class="registration_container">
            <h2 class="text-center registration_section_title">حساب کاربری ندارید؟ ثبت نام کنید</h2>
            <form action="{{ route('register.store') }}" method="POST" class="form-horizontal" id="registerForm">
                {{ csrf_field() }}
                <div class="form-group row">
                    <label for="name_for_register" class="control-label col-sm-3">نام و نام خانوادگی:</label>
                    <div class="col-sm-9">
                        <input type="text" name="name" id="name_for_register" class="form-control">
                        @if($errors->has('name'))
                            <b class="input-errors">{{ $errors->first('name') }}</b>
                        @endif
                    </div>
                </div>
                <div class="form-group row">
                    <label for="email_for_register" class="control-label col-sm-3">ایمیل:</label>
                    <div class="col-sm-9">
                        <input type="text" name="email" id="email_for_register" class="form-control">
                        @if($errors->has('email'))
                            <b class="input-errors">{{ $errors->first('email') }}</b>
                        @endif
                    </div>
                </div>
                <div class="form-group row">
                    <label for="password_for_register" class="control-label col-sm-3">رمز عبور</label>
                    <div class="col-sm-9">
                        <input type="password" name="password" id="password_for_register" class="form-control">
                        @if($errors->has('password'))
                            <b class="input-errors">{{ $errors->first('password') }}</b>
                        @endif
                    </div>
                </div>
                <div class="form-group row">
                    <label for="password_confirmation_for_register" class="control-label col-sm-3">تکرار رمز
                        عبور</label>
                    <div class="col-sm-9">
                        <input type="password" name="password_confirmation" id="password_confirmation_for_register"
                               class="form-control">
                    </div>
                </div>
                <div class="form-group row">
                    <div class="col-sm-9 col-sm-offset-3">
                        <button type="submit" class="btn btn-primary p-10-30">ثبت نام</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
@endsection