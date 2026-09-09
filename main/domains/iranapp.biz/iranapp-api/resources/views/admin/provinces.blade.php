@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="row">
                    <div class="col-sm-12">
                        <div class="card-box">
                            @if(count($errors) > 0)
                                <div class="alert alert-danger text-center">
                                    @foreach($errors->all() as $error)
                                        {{ $error }} <br>
                                    @endforeach
                                </div>
                            @endif
                            <h4 class="m-t-0 header-title"><b>افزودن استان</b></h4>
                            <form role="form" action="{{ route('saveProvinceInAdminPanel') }}" method="post"
                                  enctype="multipart/form-data">
                                {{ csrf_field() }}
                                <div class="form-group">
                                    <label for="category_name_in_add_new">نام استان</label>
                                    <input name="name" value="{{ old('name') }}" type="text" class="form-control"
                                           id="category_name_in_add_new"
                                           placeholder="نام استان جدید را وارد کنید...">
                                </div>
                                <button type="submit" class="btn btn-purple waves-effect waves-light">ثبت</button>
                            </form>

                        </div>
                    </div>
                </div>
                <!-- Page-Title -->
                <div class="row">
                    <div class="col-sm-12">
                        <div class="card-box">
                            <div class="row">
                                <div class="col-lg-12">
                                    <h4 class="m-t-0 header-title"><b>استان ها</b></h4>
                                    <p class="text-muted font-13">
                                    </p>

                                    <div class="p-20">
                                        <table class="table table-striped m-0">
                                            <thead>
                                            <tr>
                                                <th>#</th>
                                                <th>نام استان</th>
                                                <th>شهر ها</th>
                                                <th>اختیارات</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            @foreach($provinces as $item)
                                                <tr>
                                                    <th scope="row">{{ $item->id }}</th>
                                                    <td>{{ $item->name }}</td>
                                                    <td>
                                                        <a href="{{ route('showAllCitiesInAdminPanel' , $item->id) }}">مشاهده شهر ها</a>
                                                    </td>
                                                    <td>
                                                        <a class="text-primary" href="" data-toggle="modal" data-target="#update-category-{{ $item->id }}-modal">ویرایش</a>
                                                        &nbsp;&nbsp;&nbsp;
                                                        <a class="text-danger"
                                                           href="{{ route('deleteProvinceInAdminPanel' , $item->id) }}">حذف</a>
                                                    </td>
                                                </tr>
                                            @endforeach
                                            </tbody>
                                        </table>
                                    </div>

                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xs-12">
                                    <ul class="pagination pagination-split">
                                        @if($provinces->currentPage() != 1)
                                            <li>
                                                <a href="{{ $provinces->previousPageUrl() }}"><i
                                                            class="fa fa-angle-left"></i></a>
                                            </li>
                                        @endif
                                        @for($i =1 ; $i <= $provinces->lastPage() ; $i++)
                                            <li class="{{ $i == $provinces->currentPage() ? 'active' : '' }}">
                                                <a href="{{ $provinces->url($i) }}">{{ $i }}</a>
                                            </li>
                                        @endfor
                                        @if($provinces->currentPage() != $provinces->lastPage())
                                            <li>
                                                <a href="{{ $provinces->nextPageUrl() }}"><i
                                                            class="fa fa-angle-right"></i></a>
                                            </li>
                                        @endif
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div> <!-- container -->

        </div> <!-- content -->
        @include('admin.footer')
    </div>
    @foreach($provinces as $item)
        <div id="update-category-{{ $item->id }}-modal" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" style="display: none;">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                        <h4 class="modal-title">ویرایش استان</h4>
                    </div>
                    <form action="{{ route('updateProvinceInAdminPanel' , $item->id) }}" method="post" enctype="multipart/form-data">
                        {{ csrf_field() }}
                        <input type="hidden" name="_method" value="PUT">
                        <div class="modal-body">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label for="category-{{ $item->id }}-name-field" class="control-label">نام استان</label>
                                        <input name="name" value="{{ $item->name }}" type="text" class="form-control" id="category-{{ $item->id }}-name-field">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default waves-effect" data-dismiss="modal">بستن</button>
                            <button type="submit" class="btn btn-info waves-effect waves-light">ذخیره تغیرات</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    @endforeach
@endsection