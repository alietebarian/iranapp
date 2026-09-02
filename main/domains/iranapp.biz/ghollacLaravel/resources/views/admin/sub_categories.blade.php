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
                            <h4 class="m-t-0 header-title"><b> افزودن زیر دسته جدید به دسته <span
                                            class="text-danger">{{ $category->name }}</span></b></h4>
                            <form role="form" action="{{ route('saveSubCategoryInAdminPanel' , $category->id) }}"
                                  method="post"
                                  enctype="multipart/form-data">
                                {{ csrf_field() }}
                                <div class="form-group">
                                    <label for="category_name_in_add_new">نام دسته</label>
                                    <input name="name" value="{{ old('name') }}" type="text" class="form-control"
                                           id="category_name_in_add_new"
                                           placeholder="نام زیر دسته را وارد کنید...">
                                </div>
                                <div class="form-group">
                                    <label for="category_icon_in_add_new">تصویر بندانگشتی (حداکثر 1 مگابایت)</label>
                                    <input name="icon" type="file" class="form-control" id="category_icon_in_add_new">
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
                                    <h4 class="m-t-0 header-title"><b>زیر دسته های دسته بندی <span
                                                    class="text-danger">{{ $category->name }}</span></b></h4>
                                    <p class="text-muted font-13">
                                    </p>

                                    <div class="p-20">
                                        @if(count($sub_categories) > 0)
                                            <form action="" id="order-form">
                                                <table class="table table-striped m-0">
                                                    <thead>
                                                    <tr>
                                                        <th>#</th>
                                                        <th>نام دسته</th>
                                                        <th>آیکون</th>
                                                        <th>اختیارات</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody id="sortable-list">
                                                    @foreach($sub_categories as $item)
                                                        <tr>
                                                            <th scope="row">
                                                                {{ $item->id }}
                                                                <input type="hidden" name="id[]" value="{{ $item->id }}">
                                                                <input type="hidden" data-order name="ordering_factor[]"
                                                                       value="{{ $item->ordering_factor }}">
                                                            </th>
                                                            <td>{{ $item->name }}</td>
                                                            <td>
                                                                <img src="{{ URL::to('icon') }}/{{ $item->icon }}"
                                                                     style="max-width:200px;" alt="">
                                                            </td>
                                                            <td>
                                                                <a class="text-primary" href="" data-toggle="modal"
                                                                   data-target="#update-category-{{ $item->id }}-modal">ویرایش</a>
                                                                &nbsp;&nbsp;&nbsp;
                                                                <a class="text-danger"
                                                                   href="{{ route('deleteSubCategoryInAdminPanel' , $item->id) }}">حذف</a>
                                                            </td>
                                                        </tr>
                                                    @endforeach
                                                    </tbody>
                                                </table>
                                            </form>
                                        @else
                                            <div class="text-pink text-center">زیر دسته ای یافت نشد!</div>
                                        @endif
                                    </div>

                                </div>
                            </div>
                            <script>
                                var foo = document.getElementById("sortable-list");
                                var fooJq = $('#sortable-list');
                                Sortable.create(foo, {
                                    group: "omega",
                                    onUpdate: function (evt) {
                                        var index = 0;
                                        fooJq.find('tr').each(function () {
                                            var $this = $(this);
                                            $this.find('input[data-order]').val(index);
                                            index += 1;
                                        });
                                        $.ajax({
                                            type: 'get',
                                            url: '{{ route('updateSubCategoriesOrderingFactors') }}',
                                            data: $('#order-form').serialize(),
                                            success: function (response) {

                                            }
                                        });
                                    }
                                });

                            </script>
                            @if(count($sub_categories) > 0)
                                <div class="row">
                                    <div class="col-xs-12">
                                        <ul class="pagination pagination-split">
                                            @if($sub_categories->currentPage() != 1)
                                                <li>
                                                    <a href="{{ $sub_categories->previousPageUrl() }}"><i
                                                                class="fa fa-angle-left"></i></a>
                                                </li>
                                            @endif
                                            @for($i =1 ; $i <= $sub_categories->lastPage() ; $i++)
                                                <li class="{{ $i == $sub_categories->currentPage() ? 'active' : '' }}">
                                                    <a href="{{ $sub_categories->url($i) }}">{{ $i }}</a>
                                                </li>
                                            @endfor
                                            @if($sub_categories->currentPage() != $sub_categories->lastPage())
                                                <li>
                                                    <a href="{{ $sub_categories->nextPageUrl() }}"><i
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
    @foreach($sub_categories as $item)
        <div id="update-category-{{ $item->id }}-modal" class="modal fade" tabindex="-1" role="dialog"
             aria-hidden="true" style="display: none;">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                        <h4 class="modal-title">ویرایش دسته</h4>
                    </div>
                    <form action="{{ route('updateSubCategoryById' , $item->id) }}" method="post"
                          enctype="multipart/form-data">
                        {{ csrf_field() }}
                        <input type="hidden" name="_method" value="PUT">
                        <div class="modal-body">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label for="category-{{ $item->id }}-name-field" class="control-label">نام
                                            دسته</label>
                                        <input name="name" value="{{ $item->name }}" type="text" class="form-control"
                                               id="category-{{ $item->id }}-name-field">
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label for="category-{{ $item->id }}-icon-field" class="control-label">تصویر
                                            بندانگشتی</label>
                                        <input name="icon" type="file" class="form-control"
                                               id="category-{{ $item->id }}-icon-field">
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