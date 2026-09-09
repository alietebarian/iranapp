@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">تغییر رمز عبور</h1>
                    <form action="{{ route('changeAdminPassword') }}" method="post">
                        <input type="hidden" name="_method" value="PUT">
                        {{ csrf_field() }}
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="first_name">نام:</label>
                                    <input type="text" disabled name="first_name" value="{{ $admin->first_name }}"
                                           id="first_name" class="form-control">
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="last_name">نام خانوادگی:</label>
                                    <input type="text" disabled name="last_name" value="{{ $admin->last_name }}"
                                           id="last_name" class="form-control">
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="password">رمز عبور جدید:</label>
                                    <input type="password" name="password" id="password" class="form-control">
                                    @if($errors->has('password'))
                                        <b class="text-danger">{{ $errors->first('password') }}</b>
                                    @endif
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="password_confirmation">تکرار رمز عبور جدید:</label>
                                    <input type="password" name="password_confirmation" id="password_confirmation"
                                           class="form-control">
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