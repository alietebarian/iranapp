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
                                                        <th>زمان انتشار</th>
                                                        <th style="width:120px;">اختیارات</th>
                                                    </thead>
                                                    <tbody>
                                                    @foreach($news as $row)
                                                        <tr>
                                                            <th scope="row">{{ $row->id }}</th>
                                                            <td>{{ $row->title }}</td>
                                                            <td>{{ \Illuminate\Support\Str::limit($row->passage, 200) }}</td>
                                                            <td>
                                                                {{ \App\Support\NewsPublishTime::format($row->publish_at ?? $row->created_at) }}
                                                                @if($row->publish_at && \Carbon\Carbon::parse($row->publish_at, config('app.timezone'))->isFuture())
                                                                    <br><span class="label label-warning">در انتظار انتشار</span>
                                                                @endif
                                                            </td>
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