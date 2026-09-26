@extends('admin.master')
@section('style')
    <style>
        .install-bar-row td {
            vertical-align: middle !important;
        }

        .install-bar {
            background: #eef1f5;
            border-radius: 3px;
            height: 16px;
            width: 100%;
        }

        .install-bar span {
            background: #5fbeaa;
            border-radius: 3px;
            display: block;
            height: 16px;
        }

        .install-bar-row.today span {
            background: #f05050;
        }
    </style>
@endsection
@section('content')
    @php
        $sourceTotal = max(1, array_sum(array_column($bySource, 'total')));
        $versionTotal = max(1, array_sum(array_column($byVersion, 'total')));
    @endphp
    <div class="content-page">
        <div class="content">
            <div class="container">

                <div class="row">
                    <div class="col-sm-12">
                        <h4 class="page-title">گزارش نصب ها</h4>
                        <p class="text-muted page-title-alt">همان اعدادی که در صفحه اصلی اپلیکیشن نمایش داده می شود</p>
                    </div>
                </div>

                <div class="row">
                    <div class="col-lg-4 col-sm-6">
                        <div class="widget-panel widget-style-2 bg-white">
                            <i class="md md-file-download text-info"></i>
                            <h2 class="m-0 text-dark font-600">{{ number_format($summary['total']) }}</h2>
                            <div class="text-muted m-t-5">کل نصب ها</div>
                        </div>
                    </div>
                    <div class="col-lg-4 col-sm-6">
                        <div class="widget-panel widget-style-2 bg-white">
                            <i class="md md-today text-success"></i>
                            <h2 class="m-0 text-dark font-600">{{ number_format($summary['month']) }}</h2>
                            <div class="text-muted m-t-5">30 روز اخیر</div>
                        </div>
                    </div>
                    <div class="col-lg-4 col-sm-6">
                        <div class="widget-panel widget-style-2 bg-white">
                            <i class="md md-access-time text-danger"></i>
                            <h2 class="m-0 text-dark font-600">{{ number_format($summary['today']) }}</h2>
                            <div class="text-muted m-t-5">امروز (از ساعت 00:00 به وقت تهران)</div>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6">
                        <div class="card-box">
                            <h4 class="text-dark header-title m-t-0">منبع نصب</h4>
                            @if(count($bySource) > 0)
                                <table class="table m-0">
                                    <thead>
                                    <tr>
                                        <th>منبع</th>
                                        <th>کل</th>
                                        <th>30 روز اخیر</th>
                                        <th style="width: 35%">سهم از کل</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    @foreach($bySource as $row)
                                        @php($share = round($row['total'] * 100 / $sourceTotal))
                                        <tr class="install-bar-row">
                                            <td>{{ $row['label'] }}</td>
                                            <td>{{ number_format($row['total']) }}</td>
                                            <td>{{ number_format($row['month']) }}</td>
                                            <td>
                                                <div class="install-bar"><span style="width: {{ $share }}%"></span></div>
                                                <small class="text-muted">{{ $share }}٪</small>
                                            </td>
                                        </tr>
                                    @endforeach
                                    </tbody>
                                </table>
                            @else
                                <div class="alert alert-info m-0">هنوز نصبی ثبت نشده است.</div>
                            @endif
                        </div>

                        <div class="card-box">
                            <h4 class="text-dark header-title m-t-0">نسخه اپلیکیشن</h4>
                            @if(count($byVersion) > 0)
                                <table class="table m-0">
                                    <thead>
                                    <tr>
                                        <th>نسخه</th>
                                        <th>تعداد دستگاه</th>
                                        <th style="width: 45%"></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    @foreach($byVersion as $row)
                                        @php($share = round($row['total'] * 100 / $versionTotal))
                                        <tr class="install-bar-row">
                                            <td dir="ltr" class="text-right">{{ $row['version'] }}</td>
                                            <td>{{ number_format($row['total']) }}</td>
                                            <td>
                                                <div class="install-bar"><span style="width: {{ $share }}%"></span></div>
                                            </td>
                                        </tr>
                                    @endforeach
                                    </tbody>
                                </table>
                                <p class="text-muted m-t-10 m-b-0">
                                    <small>
                                        فقط نسخه هایی که گزارش نصب ارسال می کنند در این جدول دیده می شوند.
                                        هر دستگاه با آخرین نسخه ای که گزارش داده شمرده می شود.
                                    </small>
                                </p>
                            @else
                                <div class="alert alert-info m-0">
                                    هنوز هیچ دستگاهی با نسخه جدید اپلیکیشن گزارش نصب ارسال نکرده است.
                                </div>
                            @endif
                        </div>

                        <div class="card-box">
                            <h4 class="text-dark header-title m-t-0">نحوه شمارش</h4>
                            <ul class="m-b-0" style="line-height: 2;">
                                <li>
                                    هر نصب با یک شناسه تصادفی که اپلیکیشن در اولین اجرا می سازد، فقط یک بار شمرده می شود؛
                                    فرقی نمی کند از کدام فروشگاه یا از فایل APK نصب شده باشد.
                                </li>
                                <li>
                                    نصب های قبل از
                                    @if($cutover)
                                        <b>{{ $cutover }}</b> (اولین گزارش نصب نسخه جدید)
                                    @else
                                        انتشار نسخه جدید
                                    @endif
                                    از روی ثبت توکن نوتیفیکیشن شمرده شده اند و منبع نصب آن ها مشخص نیست.
                                </li>
                                <li>
                                    دستگاه هایی که نسخه قدیمی را داشتند و به روز کردند
                                    (<b>{{ number_format($upgrades) }}</b> دستگاه تا کنون) دوباره شمرده نمی شوند.
                                </li>
                            </ul>
                        </div>
                    </div>

                    <div class="col-md-6">
                        <div class="card-box">
                            <h4 class="text-dark header-title m-t-0">نصب روزانه در 30 روز اخیر</h4>
                            <table class="table table-condensed m-0">
                                <tbody>
                                @foreach(array_reverse($daily) as $index => $day)
                                    <tr class="install-bar-row {{ $index === 0 ? 'today' : '' }}">
                                        <td style="width: 110px;">
                                            {{ $day['jalali'] }}
                                            @if($index === 0)
                                                <small class="text-danger">(امروز)</small>
                                            @endif
                                        </td>
                                        <td style="width: 50px;">{{ number_format($day['count']) }}</td>
                                        <td>
                                            <div class="install-bar">
                                                <span style="width: {{ round($day['count'] * 100 / $dailyMax) }}%"></span>
                                            </div>
                                        </td>
                                    </tr>
                                @endforeach
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
@endsection
