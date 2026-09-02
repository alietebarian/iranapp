@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;">ثبت به عنوان آگهی ویژه</h1>
                    <form action="{{ route('updateVipAdsInAdminPanel' , $vip->id) }}" method="post" enctype="multipart/form-data">
                        {{ csrf_field() }}
                        <input type="hidden" name="_method" value="PUT">
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
                                        <option {{ $vip->show_in_city == 1 ? 'selected' : '' }} value="city">نمایش در شهر</option>
                                        <option {{ $vip->show_in_province == 1 ? 'selected' : '' }} value="province">نمایش در استان</option>
                                        <option {{ $vip->show_in_country == 1 ? 'selected' : '' }} value="country">نمایش در کشور</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="show_in_categories">نمایش در دسته ها:</label>
                                    <select name="category_displaying" id="show_in_categories" class="form-control">
                                        <option {{ $vip->show_in_category == 1 ? 'selected' : '' }} value="category">نمایش در دسته بندی</option>
                                        <option {{ $vip->show_in_subcategory == 1 ? 'selected' : '' }} value="sub_category">نمایش در زیر دسته بندی</option>
                                        <option {{ $vip->show_in_subcategory == 1 ? 'all' : '' }} value="all">نمایش در دسته و زیر دسته</option>
                                        <option {{ ( $vip->show_in_subcategory == 0 && $vip->show_in_category == 0 )  ? 'selected' : '' }} value="without_selection">هیچ کدام </option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-6">
                                <div class="form-group">
                                    <label for="show_in_homepage">نمایش در صفحه اصلی</label>
                                    <select name="show_in_homepage" id="show_in_homepage" class="form-control">
                                        <option {{ $vip->show_in_main_page == 1 ? 'selected' : '' }} value="yes">بله</option>
                                        <option {{ $vip->show_in_main_page == 0 ? 'selected' : '' }} value="no">خیر</option>
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
                            <div class="col-sm-6">
                                <img src="{{ URL::to('/vip_ads_photo') }}/{{ $vip->photo }}" alt="image" class="img-responsive img-thumbnail" width="200"/>
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