@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;">ثبت به عنوان آگهی ویژه</h1>
                    <form action="{{ route('saveVipAds' , $ad->id) }}" method="post" enctype="multipart/form-data">
                        {{ csrf_field() }}
                        @if(count($errors->all()) > 0 )
                            <div class="row">
                                <div class="col-xs-12">
                                    <div class="alert alert-danger text-center">
                                        @foreach($errors->all() as $error)
                                            {{ $error }} <br>
                                        @endforeach
                                    </div>
                                </div>
                            </div>
                        @endif
                        <div class="row">
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="ads_title">عنوان آگهی:</label>
                                    <input type="text" value="{{ $ad->title }}" disabled id="ads_title"
                                           class="form-control">
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="show_in_region">نمایش منطقه ای:</label>
                                    <select name="regional_displaying" id="show_in_region" class="form-control">
                                        <option value="city">نمایش در شهر</option>
                                        <option value="province">نمایش در استان</option>
                                        <option value="country">نمایش در کشور</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="show_in_categories">نمایش در دسته ها:</label>
                                    <select name="category_displaying" id="show_in_categories" class="form-control">
                                        <option value="category">نمایش در دسته بندی</option>
                                        <option value="sub_category">نمایش در زیر دسته بندی</option>
                                        <option value="all">نمایش در دسته و زیر دسته</option>
                                        <option value="without_selection">هیچ کدام</option>

                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="show_in_homepage">نمایش در صفحه اصلی</label>
                                    <select name="show_in_homepage" id="show_in_homepage" class="form-control">
                                        <option value="yes">بله</option>
                                        <option value="no">خیر</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="vip-ads-photo">تصویر</label>
                                    <input type="file" name="photo" id="vip-ads-photo" class="form-control">
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <button type="submit" class="btn btn-primary">ثبت</button>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
@endsection