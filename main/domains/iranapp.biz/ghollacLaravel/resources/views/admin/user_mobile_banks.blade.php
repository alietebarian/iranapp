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
                        <ul class="pagination pagination-split">
                            @if($list->currentPage() != 1)
                                <li>
                                    <a href="{{ $list->previousPageUrl() }}"><i class="fa fa-angle-left"></i></a>
                                </li>
                            @endif
                            @php
                                $ellipsisWrote = false;
                            @endphp
                            @for($i = 1 ; $i <= $list->lastPage() ; $i++)
                                @if( ($i >= 1 && $i <=3) || ( $i <= $list->lastPage() && $i >= $list->lastPage() - 2) || ($i >= $list->currentPage() && $i <= $list->currentPage() + 2) || ($i <= $list->currentPage() &&  $i >= $list->currentPage() - 2) )
                                    <li class="{{ $i == $list->currentPage()  ? 'active' : '' }}">
                                        <a href="{{ $list->url($i) }}">{{ $i }}</a>
                                    </li>
                                @else
                                    @if(!$ellipsisWrote)
                                        <li>
                                            <a>...</a>
                                        </li>
                                        @php
                                            $ellipsisWrote = true;
                                        @endphp
                                    @endif
                                @endif
                            @endfor
                            @if($list->hasMorePages())
                                <li>
                                    <a href="{{ $list->nextPageUrl() }}"><i class="fa fa-angle-right"></i></a>
                                </li>
                            @endif
                        </ul>
                    @endif
                </div>
            </div>
        </div>
    </div>
@endsection