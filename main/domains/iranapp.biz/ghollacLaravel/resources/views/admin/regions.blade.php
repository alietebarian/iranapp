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
                            <h4 class="m-t-0 header-title"><b> افزودن منطقه جدید به شهر <span
                                            class="text-danger">{{ $city->name }}</span></b></h4>
                            <form role="form" action="{{ route('saveRegionInAdminPanel' , $city->id) }}"
                                  method="post">
                                {{ csrf_field() }}
                                <div class="form-group">
                                    <label for="category_name_in_add_new">نام منطقه</label>
                                    <input name="name" value="{{ old('name') }}" type="text" class="form-control"
                                           id="category_name_in_add_new"
                                           placeholder="نام منطقه را وارد کنید...">
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
                                    <h4 class="m-t-0 header-title"><b>مناطق شهر <span class="text-danger">{{ $city->name }}</span></b></h4>
                                    <p class="text-muted font-13">
                                    </p>

                                    <div class="p-20">
                                        @if(count($list) > 0)
                                            <table class="table table-striped m-0">
                                                <thead>
                                                <tr>
                                                    <th>#</th>
                                                    <th>نام منطقه</th>
                                                    <th>اختیارات</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                @foreach($list as $item)
                                                    <tr>
                                                        <th scope="row">{{ $item->id }}</th>
                                                        <td>{{ $item->name }}</td>
                                                        <td>
                                                            <a class="text-primary" href="" data-toggle="modal"
                                                               data-target="#update-category-{{ $item->id }}-modal">ویرایش</a>
                                                            &nbsp;&nbsp;&nbsp;
                                                            <a class="text-danger"
                                                               href="{{ route('deleteRegionInAdminPanel' , $item->id) }}">حذف</a>
                                                        </td>
                                                    </tr>
                                                @endforeach
                                                </tbody>
                                            </table>
                                        @else
                                            <div class="text-pink text-center">هیچ شهری یافت نشد!</div>
                                        @endif
                                    </div>

                                </div>
                            </div>
                            @if(count($list) > 0)
                                <div class="row">
                                    <div class="col-xs-12">
                                        <ul class="pagination pagination-split">
                                            @if($list->currentPage() != 1)
                                                <li>
                                                    <a href="{{ $list->previousPageUrl() }}"><i
                                                                class="fa fa-angle-left"></i></a>
                                                </li>
                                            @endif
                                            @for($i =1 ; $i <= $list->lastPage() ; $i++)
                                                <li class="{{ $i == $list->currentPage() ? 'active' : '' }}">
                                                    <a href="{{ $list->url($i) }}">{{ $i }}</a>
                                                </li>
                                            @endfor
                                            @if($list->currentPage() != $list->lastPage())
                                                <li>
                                                    <a href="{{ $list->nextPageUrl() }}"><i
                                                                class="fa fa-angle-right"></i></a>
                                                </li>
                                            @endif
                                        </ul>
                                    </div>
                                </div>
                            @endif
                        </div>
                    </div>
                </div>
            </div> <!-- container -->

        </div> <!-- content -->
        @include('admin.footer')

    </div>
    @foreach($list as $item)
        <div id="update-category-{{ $item->id }}-modal" class="modal fade" tabindex="-1" role="dialog"
             aria-hidden="true" style="display: none;">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                        <h4 class="modal-title">ویرایش منطقه</h4>
                    </div>
                    <form action="{{ route('updateRegionInAdminPanel' , $item->id) }}" method="post"
                          enctype="multipart/form-data">
                        {{ csrf_field() }}
                        <input type="hidden" name="_method" value="PUT">
                        <div class="modal-body">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label for="category-{{ $item->id }}-name-field" class="control-label">نام
                                            منطقه</label>
                                        <input name="name" value="{{ $item->name }}" type="text" class="form-control"
                                               id="category-{{ $item->id }}-name-field">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default waves-effect" data-dismiss="modal">بستن
                            </button>
                            <button type="submit" class="btn btn-info waves-effect waves-light">ذخیره تغیرات</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    @endforeach
@endsection