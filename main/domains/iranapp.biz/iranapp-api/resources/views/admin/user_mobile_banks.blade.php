@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">لیست کاربران
                        <a href="{{ route('ExportUserMobilesInExcelFormat') }}" class="btn btn-primary">دریافت خروجی اکسل</a>
                    </h1>
                    <div class="table-responsive">
                        @if(count($list) > 0 )
                            <table class="table m-0">
                                <thead>
                                <tr>
                                    <th>نام کامل</th>
                                    <th>تلفن همراه</th>
                                </tr>
                                </thead>
                                <tbody>
                                @foreach($list as $user)
                                    <tr>
                                        <td>{{ $user->first_name }} {{ $user->last_name }}</td>
                                        <td>{{ $user->mobile }}</td>
                                    </tr>
                                @endforeach
                                </tbody>
                            </table>
                        @else
                            <div class="alert alert-success text-center">کاربری یافت نشد!</div>
                        @endif
                    </div>
                    @if(count($list) > 0 )
                        {{ $list->withQueryString()->links() }}
                    @endif
                </div>
            </div>
        </div>
    </div>
@endsection