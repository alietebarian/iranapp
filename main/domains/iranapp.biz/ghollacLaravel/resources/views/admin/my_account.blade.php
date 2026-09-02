@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">تغییر مشخصات کاربری</h1>
                    <form action="{{ route('updateAdminMyAccount') }}" method="post">
                        <input type="hidden" name="_method" value="PUT">
                        {{ csrf_field() }}
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="first_name">نام:</label>
                                    <input type="text" name="first_name" value="{{ $admin->first_name }}" id="first_name" class="form-control">
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="last_name">نام خانوادگی:</label>
                                    <input type="text" name="last_name" value="{{ $admin->last_name }}" id="last_name" class="form-control">
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="mobile">تلفن همراه</label>
                                    <input type="text" name="mobile" disabled value="{{ $admin->mobile }}" id="mobile" class="form-control">
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="email">ایمیل </label>
                                    <input type="text" name="email" disabled value="{{ $admin->email }}" id="email" class="form-control">
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