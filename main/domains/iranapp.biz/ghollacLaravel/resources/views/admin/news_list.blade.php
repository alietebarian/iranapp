@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">

                <div class="row">
                    <div class="col-sm-12">
                        <div class="card-box">
                            <div class="row">
                                <div class="col-lg-12">
                                    <h4 class="m-t-0 header-title"><b>اخبار</b></h4>
                                    <p class="text-muted font-13">
                                    </p>

                                    <div class="p-20">
                                        @if(count($news) > 0 )
                                            <div class="table-responsive">
                                                <table class="table m-0">
                                                    <thead>
                                                    <tr>
                                                        <th>#</th>
                                                        <th>عنوان خبر</th>
                                                        <th>متن خبر</th>
                                                        <th style="width:120px;">اختیارات</th>
                                                    </thead>
                                                    <tbody>
                                                    @foreach($news as $row)
                                                        <tr>
                                                            <th scope="row">{{ $row->id }}</th>
                                                            <td>{{ $row->title }}</td>
                                                            <td>{{ str_limit($row->passage , 200)  }}</td>
                                                            <td>
                                                                <a style="display:block;"
                                                                   href="{{ route('deleteNewsInAdminPanel' , $row->id) }}"
                                                                   class="text-danger">حذف</a>
                                                                <a style="display:block;" href="{{ route('showNewsUpdatePage' , $row->id) }}" class="text-success">متن
                                                                    کامل</a>
                                                                <a style="display:block;" href="{{ route('showPhotosOfNewsById' , $row->id) }}" class="text-primary">تصاویر خبر</a>
                                                            </td>
                                                        </tr>
                                                    @endforeach
                                                    </tbody>
                                                </table>
                                            </div>
                                        @else
                                            <div class="alert alert-success text-center">
                                                خبری یافت نشد!
                                            </div>

                                        @endif
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div> <!-- container -->

        </div> <!-- content -->
        @include('admin.footer')
    </div>
@endsection