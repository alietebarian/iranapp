@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">

                <!-- Page-Title -->
                <div class="row">
                    <div class="col-sm-12">
                        <h4 class="page-title">داشبورد</h4>
                        <p class="text-muted page-title-alt">ایران اپ</p>
                    </div>
                </div>

                <div class="row">
                    <div class="col-lg-3 col-sm-6">
                        <div class="widget-panel widget-style-2 bg-white">
                            <i class="md md-store-mall-directory text-info"></i>
                            <h2 class="m-0 text-dark counter font-600">{{ $needsAdsCount + $discountAdsCount + $ResumeAdsCount + $OppurtunitiesAdsCount + $estateAdsCount + $vehicleAdsCount }}</h2>
                            <div class="text-muted m-t-5">کل آگهی ها</div>
                        </div>
                    </div>
                    <div class="col-lg-3 col-sm-6">
                        <div class="widget-panel widget-style-2 bg-white">
                            <i class="md md-store-mall-directory text-info"></i>
                            <h2 class="m-0 text-dark counter font-600">{{ $discountAdsCount }}</h2>
                            <div class="text-muted m-t-5">تخفیفات</div>
                        </div>
                    </div>
                    <div class="col-lg-3 col-sm-6">
                        <div class="widget-panel widget-style-2 bg-white">
                            <i class="md md-store-mall-directory text-info"></i>
                            <h2 class="m-0 text-dark counter font-600">{{ $needsAdsCount }}</h2>
                            <div class="text-muted m-t-5">نیازمندی ها</div>
                        </div>
                    </div>
                    <div class="col-lg-3 col-sm-6">
                        <div class="widget-panel widget-style-2 bg-white">
                            <i class="md md-store-mall-directory text-info"></i>
                            <h2 class="m-0 text-dark counter font-600">{{ $vipAdsCount }}</h2>
                            <div class="text-muted m-t-5">آگهی های ویژه</div>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-lg-12">
                        <div class="card-box">
                            <h4 class="text-dark header-title m-t-0">آمار آگهی های ثبت شده</h4>

                            <div class="row">
                                <div class="col-md-8">
                                    <div class="text-center">
                                    </div>

                                    <div id="morris-area-with-dotted" style="height: 300px;"></div>

                                </div>
                                <script>
                                    Morris.Bar({
                                        element: 'morris-area-with-dotted',
                                        data: [
                                                @foreach($adsChart as $index => $row)
                                            {
                                                y: '{{ $adsChart[$index]['monthName'] }}',
                                                a: {{ $adsChart[$index]['count'] }}
                                            }
                                            @if($index != 12)
                                            ,
                                            @endif
                                            @endforeach
                                        ],
                                        xkey: 'y',
                                        ykeys: ['a'],
                                        labels: ['تعداد آگهی های ثبت شده']
                                    });
                                </script>
                                <div class="col-md-4">

                                    <p class="font-600">تخفیفات <span class="text-primary pull-right">{{ $discountPercentage }}
                                            %</span></p>
                                    <div class="progress m-b-30">
                                        <div class="progress-bar progress-bar-primary progress-animated wow animated"
                                             role="progressbar" aria-valuenow="80" aria-valuemin="0" aria-valuemax="100"
                                             style="width: {{ $discountPercentage }}%">
                                        </div><!-- /.progress-bar .progress-bar-danger -->
                                    </div><!-- /.progress .no-rounded -->

                                    <p class="font-600">نیازمندی ها<span class="text-pink pull-right">{{ $needsPercentage }}
                                            %</span></p>
                                    <div class="progress m-b-30">
                                        <div class="progress-bar progress-bar-pink progress-animated wow animated"
                                             role="progressbar" aria-valuenow="50" aria-valuemin="0" aria-valuemax="100"
                                             style="width: {{ $needsPercentage }}%">
                                        </div><!-- /.progress-bar .progress-bar-pink -->
                                    </div><!-- /.progress .no-rounded -->

                                    <p class="font-600">آگهی های ویژه<span class="text-danger pull-right">{{ $vipAdsPercentage }}
                                            %</span></p>
                                    <div class="progress m-b-30">
                                        <div class="progress-bar progress-bar-danger progress-animated wow animated"
                                             role="progressbar" aria-valuenow="50" aria-valuemin="0" aria-valuemax="100"
                                             style="width: {{ $vipAdsPercentage }}%">
                                        </div><!-- /.progress-bar .progress-bar-pink -->
                                    </div><!-- /.progress .no-rounded -->


                                </div>


                            </div>

                            <!-- end row -->

                        </div>

                    </div>


                </div>
                <!-- end row -->
                <div class="row">
                    <!-- Transactions -->
                    <div class="col-lg-6">
                        <div class="card-box">
                            <h4 class="m-t-0 m-b-20 header-title"><b>کاربران ثبت نام شده یک هفته گذشته</b></h4>

                            <div class="nicescroll mx-box">
                                <ul class="list-unstyled transaction-list m-r-5">
                                    @foreach($lastWeekRegisteredUsers as $user)
                                        <li>
                                            <i class="ti-user text-success"></i>
                                            <span class="tran-text">{{ $user->first_name }} {{ $user->last_name }}</span>
                                            <span class="pull-right text-muted">{{ \App\Libraries\jdf::jdate('j/n/y' , \Carbon\Carbon::createFromFormat('Y-m-d H:i:s' , $user->created_at)->getTimestamp()) }}</span>
                                            <span class="clearfix"></span>
                                        </li>
                                    @endforeach
                                </ul>
                            </div>
                        </div>

                    </div> <!-- end col -->

                    <div class="col-lg-6">
                        <div class="card-box">
                            <h4 class="m-t-0 m-b-20 header-title"><b>کاربران ثبت نام شده امروز</b></h4>

                            <div class="widget-panel widget-style-2 bg-white m-b-0">
                                <i class="ti-user text-success"></i>
                                <h2 class="m-0 text-dark counter font-600">{{ $todayRegisteredUsersCount }}</h2>
                                <div class="text-muted m-t-5">{{ $todayJalaliDate }}</div>
                            </div>
                        </div>
                    </div> <!-- end col -->

                    <!-- CHAT -->
                </div> <!-- end row -->


            </div> <!-- container -->

        </div> <!-- content -->
        @include('admin.footer')
    </div>
@endsection
