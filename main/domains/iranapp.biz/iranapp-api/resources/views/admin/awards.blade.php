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
                            <h4 class="m-t-0 header-title"><b>افزودن جایزه جدید</b></h4>
                            <form role="form" action="{{ route('admin.award.store') }}" method="post">
                                {{ csrf_field() }}
                                <div class="form-group">
                                    <label for="name">نام جایزه</label>
                                    <input name="name" value="{{ old('name') }}" type="text" class="form-control"
                                           id="name"
                                           placeholder="نام جایزه جدید را وارد کنید...">
                                </div>
                                <div class="form-group">
                                    <p class="alert alert-danger">منظور از قیمت بر اساس واحد میباشد</p>
                                    <label for="price">قیمت</label>
                                    <input name="price" value="{{ old('price') }}" type="text" class="form-control"
                                           id="price"
                                           placeholder="قیمت جایزه جدید را وارد کنید...">
                                </div>
                                <div class="form-group">
                                    <label for="name">توضیحات جایزه</label>
                                    <textarea class="form-control" name="description"
                                              rows="4">{{old('description')}}</textarea>
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
                                    <h4 class="m-t-0 header-title"><b>جایزه ها</b></h4>
                                    <p class="text-muted font-13">
                                    </p>

                                    <div class="p-20">
                                        <form id="order-form" action="">
                                            <table class="table table-striped m-0">
                                                <thead>
                                                <tr>
                                                    <th>#</th>
                                                    <th>نام جایزه</th>
                                                    <th>قیمت</th>
                                                    <th>توضیحات</th>
                                                    <th>عملیات</th>
                                                </tr>
                                                </thead>
                                                <tbody id="sortable-list">
                                                @foreach($awards as $item)
                                                    <tr>
                                                        <td scope="row">{{ $item->id }}
                                                            <input type="hidden" name="id[]" value="{{ $item->id }}">
                                                            <input type="hidden" data-order name="ordering_factor[]"
                                                                   value="{{ $item->ordering_factor }}">
                                                        </td>
                                                        <td>{{ $item->name }}</td>
                                                        <td>{{ $item->price }} واحد </td>
                                                        <td>{{ \Illuminate\Support\Str::limit($item->description) }}</td>
                                                        <td>
                                                            <a class="text-primary" href="" data-toggle="modal"
                                                               data-target="#update-award-{{ $item->id }}-modal">ویرایش</a>
                                                            &nbsp;&nbsp;&nbsp;
                                                            <a class="text-danger"
                                                               href="{{ route('admin.award.delete' , $item->id) }}">حذف</a>
                                                        </td>
                                                    </tr>
                                                @endforeach
                                                </tbody>
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
                                                                url: '{{ route('updateCategoryOrderingFactor') }}',
                                                                data: $('#order-form').serialize(),
                                                                success: function (response) {

                                                                }
                                                            });
                                                        }
                                                    });

                                                </script>
                                            </table>
                                        </form>
                                    </div>

                                </div>
                            </div>
                            {{--<div class="row">
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
                            </div>--}}
                        </div>
                    </div>
                </div>
            </div> <!-- container -->

        </div> <!-- content -->
        @include('admin.footer')
    </div>
    @foreach($awards as $item)
        <div id="update-award-{{ $item->id }}-modal" class="modal fade" tabindex="-1" role="dialog"
             aria-hidden="true" style="display: none;">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                        <h4 class="modal-title">ویرایش جایزه</h4>
                    </div>
                    <form action="{{ route('admin.award.update' , $item->id) }}" method="post">
                        {{ csrf_field() }}
                        <input type="hidden" name="_method" value="PUT">
                        <div class="modal-body">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label for="award-{{ $item->id }}-name-field" class="control-label">نام
                                            جایزه</label>
                                        <input name="name" value="{{ $item->name }}" type="text" class="form-control"
                                               id="award-{{ $item->id }}-name-field">
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label for="award-{{ $item->id }}-price-field" class="control-label">قیمت</label>
                                        <input name="price" type="text" class="form-control"
                                               id="award-{{ $item->id }}-price-field" value="{{$item->price}}">
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="form-group">
                                        <label for="award-{{ $item->id }}-description-field" class="control-label">توضیحات</label>
                                        <textarea name="description" rows="3" class="form-control"
                                                  id="award-{{ $item->id }}-price-field">{{$item->description}}</textarea>
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
