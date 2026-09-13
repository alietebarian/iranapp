<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="author" content="ّFarhad Ashtari">

    <link rel="shortcut icon" href="{{ URL::to('/admin') }}/assets/images/favicon_1.ico">

    <title>ایران اپ | پنل مدیریت</title>
    <script src="{{ URL::to('admin') }}/assets/js/jquery.min.js"></script>
    <!--Morris Chart CSS -->
    <link rel="stylesheet" href="{{ URL::to('/admin') }}/assets/plugins/morris/morris.css">
    <link href="{{ URL::to('/admin') }}/assets/plugins/bootstrap-sweetalert/sweet-alert.css" rel="stylesheet"
          type="text/css">
    <link href="{{ URL::to('/admin') }}/assets/css/bootstrap-rtl.min.css" rel="stylesheet" type="text/css"/>
    <link href="{{ URL::to('/admin') }}/assets/css/core.css" rel="stylesheet" type="text/css"/>
    <link href="{{ URL::to('/admin') }}/assets/css/components.css" rel="stylesheet" type="text/css"/>
    <link href="{{ URL::to('/admin') }}/assets/css/icons.css" rel="stylesheet" type="text/css"/>
    <link href="{{ URL::to('/admin') }}/assets/css/pages.css" rel="stylesheet" type="text/css"/>
    <link href="{{ URL::to('/admin') }}/assets/css/responsive.css" rel="stylesheet" type="text/css"/>
    <script src="{{ URL::to('admin') }}/assets/js/Sortable.min.js"></script>


    <!-- HTML5 Shiv and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->

    <script src="{{ URL::to('/admin') }}/assets/js/modernizr.min.js"></script>

    <script src="{{ URL::to('admin') }}/assets/plugins/morris/morris.min.js"></script>
    <script src="{{ URL::to('admin') }}/assets/plugins/raphael/raphael-min.js"></script>

    <script type="text/javascript" src="{{ URL::to('/admin/assets/js') }}/jquery.validate.min.js"></script>

    <link href="{{ URL::to('/admin') }}/assets/plugins/bootstrap-tagsinput/css/bootstrap-tagsinput.css"
          rel="stylesheet"/>
    <link href="{{ URL::to('/admin') }}/assets/plugins/switchery/css/switchery.min.css" rel="stylesheet"/>
    <link href="{{ URL::to('/admin') }}/assets/plugins/multiselect/css/multi-select.css" rel="stylesheet"
          type="text/css"/>
    <link href="{{ URL::to('/admin') }}/assets/plugins/select2/css/select2.min.css" rel="stylesheet" type="text/css"/>
    <link href="{{ URL::to('/admin') }}/assets/plugins/bootstrap-select/css/bootstrap-select.min.css" rel="stylesheet"/>

    <script src="{{ URL::to('/admin') }}/assets/plugins/bootstrap-tagsinput/js/bootstrap-tagsinput.min.js"></script>
    <script type="text/javascript"
            src="{{ URL::to('/admin') }}/assets/plugins/multiselect/js/jquery.multi-select.js"></script>
    <script src="{{ URL::to('/admin') }}/assets/plugins/bootstrap-select/js/bootstrap-select.min.js"
            type="text/javascript"></script>
    @yield('style')
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyB0ZLVnugpbpDVHV2sF_kZpoKwnUvml-Yw&language=fa&region=IR&libraries=places"></script>
    <style>
        .input-field-errors {
            color: #f44336;
        }

        #description {
            font-family: Roboto;
            font-size: 15px;
            font-weight: 300;
        }

        #infowindow-content .title {
            font-weight: bold;
        }

        #infowindow-content {
            display: none;
        }

        #map #infowindow-content {
            display: inline;
        }

        .pac-card {
            margin: 10px 10px 0 0;
            border-radius: 2px 0 0 2px;
            box-sizing: border-box;
            -moz-box-sizing: border-box;
            outline: none;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
            background-color: #fff;
            font-family: Roboto;
        }

        #pac-container {
            padding-bottom: 12px;
            margin-right: 12px;
        }

        .pac-controls {
            display: inline-block;
            padding: 5px 11px;
        }

        .pac-controls label {
            font-family: Roboto;
            font-size: 13px;
            font-weight: 300;
        }

        #pac-input {
            background-color: #fff;
            font-family: Roboto;
            font-size: 15px;
            font-weight: 300;
            margin-left: 12px;
            padding: 0 11px 0 13px;
            text-overflow: ellipsis;
            width: 400px;
        }

        #pac-input:focus {
            border-color: #4d90fe;
        }

        #title {
            color: #fff;
            background-color: #4d90fe;
            font-size: 25px;
            font-weight: 500;
            padding: 6px 12px;
        }

        #target {
            width: 345px;
        }

    </style>


</head>


<body class="fixed-left">

<!-- Begin page -->
<div id="wrapper">

    <!-- Top Bar Start -->
    <div class="topbar">

        <!-- LOGO -->
        <div class="topbar-left">
            <div class="text-center">
                <a href="" class="logo"><i style="font-size:12px;" class="icon-magnet icon-c-logo">ایران اپ</i></a>
                <!-- Image Logo here -->
                <!--<a href="index.html" class="logo">-->
                <!--<i class="icon-c-logo"> <img src="assets/images/logo_sm.png" height="42"/> </i>-->
                <!--<span><img src="assets/images/logo_light.png" height="20"/></span>-->
                <!--</a>-->
            </div>
        </div>

        <!-- Button mobile view to collapse sidebar menu -->
        <div class="navbar navbar-default" role="navigation">
            <div class="container">
                <div class="">
                    <div class="pull-left">
                        <button class="button-menu-mobile open-left waves-effect waves-light">
                            <i class="md md-menu"></i>
                        </button>
                        <span class="clearfix"></span>
                    </div>

                    <ul class="nav navbar-nav hidden-xs">
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle waves-effect waves-light" data-toggle="dropdown"
                               role="button" aria-haspopup="true" aria-expanded="false">حساب کاربری<span
                                        class="caret"></span></a>
                            <ul class="dropdown-menu">
                                <li><a href="{{ route('showMyAccountInAdminPanel') }}">تغییر مشخصات کاربری</a></li>
                                <li><a href="{{ route('showChangePasswordForm') }}">تغییر رمز عبور</a></li>
                            </ul>
                        </li>
                    </ul>
                    <ul class="nav navbar-nav navbar-right pull-right">
                        @php($adminNotifications = $adminNotifications ?? collect())
                        <li class="dropdown top-menu-item-xs">
                            <a href="#" class="dropdown-toggle waves-effect waves-light" data-toggle="dropdown"
                               aria-expanded="true"><i class="icon-bell"></i>
                                @if($adminNotifications->count() > 0)
                                    <span class="badge badge-xs badge-danger">{{ $adminNotifications->count() }}</span>
                                @endif
                            </a>
                            <ul class="dropdown-menu dropdown-menu-lg">
                                <li class="notifi-title">آگهی های در حال انقضاء</li>
                                <li class="list-group slimscroll-noti notification-list">
                                    @forelse($adminNotifications as $notification)
                                        @php($daysLeft = $notification->daysLeft())
                                        <a href="{{ route('openAdminNotification' , $notification->id) }}" class="list-group-item">
                                            <h5 class="media-heading">{{ $notification->title }}</h5>
                                            <p class="m-0">
                                                <small class="{{ $daysLeft <= 3 ? 'text-danger' : 'text-warning' }}">
                                                    @if($daysLeft < 0)
                                                        منقضی شده
                                                    @elseif($daysLeft == 0)
                                                        امروز منقضی می شود
                                                    @else
                                                        {{ $daysLeft }} روز تا انقضاء
                                                    @endif
                                                </small>
                                                <small class="text-muted">({{ \App\Libraries\jdf::jdate('j F Y' , \Carbon\Carbon::createFromFormat('Y-m-d' , $notification->valid_until)->getTimestamp()) }})</small>
                                            </p>
                                        </a>
                                    @empty
                                        <span class="list-group-item text-muted">اعلان جدیدی وجود ندارد</span>
                                    @endforelse
                                </li>
                                @if($adminNotifications->count() > 0)
                                    <li>
                                        <form action="{{ route('readAllAdminNotifications') }}" method="post" class="m-0">
                                            {{ csrf_field() }}
                                            <button type="submit" class="list-group-item text-center"
                                                    style="width: 100%; background: none;">
                                                <small class="font-600">علامت گذاری همه به عنوان خوانده شده</small>
                                            </button>
                                        </form>
                                    </li>
                                @endif
                            </ul>
                        </li>
                        <li class="hidden-xs">
                            <a href="#" id="btn-fullscreen" class="waves-effect waves-light"><i
                                        class="icon-size-fullscreen"></i></a>
                        </li>
                        <li class="dropdown top-menu-item-xs">
                            <a href="" class="dropdown-toggle profile waves-effect waves-light" data-toggle="dropdown"
                               aria-expanded="true"><img src="{{ URL::to('/admin/assets/images') }}/doc_placeholder.png"
                                                         alt="user-img" class="img-circle"> </a>
                            <ul class="dropdown-menu">
                                <li><a href="{{ route('showMyAccountInAdminPanel') }}"><i
                                                class="ti-user m-r-10 text-custom"></i> تغییر مشخصات کاربری</a></li>
                                <li><a href="{{ route('showChangePasswordForm') }}"><i
                                                class="ti-settings m-r-10 text-custom"></i> تغییر رمز عبور</a></li>
                                <li><a href="{{ route('smsPanelInformationShow') }}"><i
                                                class="ti-settings m-r-10 text-custom"></i> تغییر مشخصات پنل پیامک</a>
                                </li>
                                <li class="divider"></li>
                                <li><a href="{{ route('logoutAdmin') }}"><i class="ti-power-off m-r-10 text-danger"></i>
                                        خروج</a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
                <!--/.nav-collapse -->
            </div>
        </div>
    </div>
    <!-- Top Bar End -->


    <!-- ========== Left Sidebar Start ========== -->

    <div class="left side-menu">
        <div class="sidebar-inner slimscrollleft">
            <!--- Divider -->
            <div id="sidebar-menu">
                <ul>

                    <li class="text-muted menu-title">منوی اصلی</li>

                    <li>
                        <a href="{{ route('showAdminDashboard') }}" class="waves-effect"><i class="ti-home"></i> <span> داشبورد </span></a>
                    </li>

                    <li class="has_sub">
                        <a href="javascript:void(0);" class="waves-effect"><i class="ti-paint-bucket"></i> <span>تعاریف پایه</span>
                            <span class="menu-arrow"></span> </a>
                        <ul class="list-unstyled">
                            <li><a href="{{ route('showAllCategoriesInAdminPanel') }}">دسته ها و زیر دسته ها</a></li>
                            <li><a href="{{ route('ShowAllCylinderVolumesInAdminPanel') }}">حجم موتورها (ویژه آگهی های
                                    خودرو)</a></li>
                            <li><a href="{{ route('showAllProvincesInAdminPanel') }}">شهر ها و استان ها</a></li>
                            <li><a href="{{ route('showAllPlansListInAdminPanel') }}">پلن های آگهی ها</a></li>
                            <li><a href="{{ route('showAllBrandsInAdmin') }}">برند وسایل نقلیه</a></li>
                            <li><a href="{{ route('showAllSpecialitiesInAdminPanel') }}">تخصص ها</a></li>
                        </ul>
                    </li>

                    <li class="has_sub">
                        <a href="javascript:void(0);" class="waves-effect"><i class="ti-light-bulb"></i>
                            <span>اخبار</span> <span class="menu-arrow"></span> </a>
                        <ul class="list-unstyled">
                            <li><a href="{{ route('showNewsListInAdminPanel') }}">لیست اخبار</a></li>
                            <li><a href="{{ route('showNewsInsertForm') }}">ثبت خبر جدید</a></li>
                        </ul>
                    </li>
                    <li class="has_sub">
                        <a href="javascript:void(0);" class="waves-effect"><i class="ti-spray"></i>
                            <span> آگهی ها </span> <span class="menu-arrow"></span> </a>
                        <ul class="list-unstyled">
                            <li><a href="{{ route('showDiscountAdsListInAdminPanel') }}">لیست تخفیف ها</a></li>
                            <li><a href="{{ route('showAdsListInAdminPanel') }}">لیست آگهی ها</a></li>
                            <li><a href="{{ route('showAdsCreatePage') }}">ثبت آگهی جدید</a></li>
                            <li><a href="{{ route('showAllVipAdsInAdminPanel') }}">لیست آگهی های ویژه</a></li>
                            <li><a href="{{ route('showExpiringAds') }}">آگهی های در حال انقضاء</a></li>
                        </ul>
                    </li>
                    <li class="has_sub">
                        <a href="javascript:void(0);" class="waves-effect"><i class="ti-user"></i>
                            <span> کاربران </span> <span class="menu-arrow"></span> </a>
                        <ul class="list-unstyled">
                            <li><a href="{{ route('showUsersListInAdminPanel') }}">لیست کاربران</a></li>
                            <li><a href="{{ route('showUserCreatePage') }}">ثبت کاربر جدید</a></li>
                            <li><a href="{{ route('showUserMobileNumbersBank') }}">بانک تلفن همراه کاربران</a></li>
                        </ul>
                    </li>
                    <li class="has_sub">
                        <a href="javascript:void(0);" class="waves-effect"><i class="ti-home"></i> <span> آگهی های ویژه املاک </span>
                            <span class="menu-arrow"></span> </a>
                        <ul class="list-unstyled">
                            <li><a href="{{ route('estate.ads.createPage.show') }}">ثبت آگهی جدید</a></li>
                            <li><a href="{{ route('showAllEstateAdsInAdminPanel') }}">لیست آگهی ها</a></li>
                        </ul>
                    </li>
                    <li class="has_sub">
                        <a href="javascript:void(0);" class="waves-effect"><i class="ti-car"></i> <span
                                    style="font-size: 13px;"> آگهی های ویژه وسایل نقلیه </span> <span
                                    class="menu-arrow"></span> </a>
                        <ul class="list-unstyled">
                            <li><a href="{{ route('vehicle.ads.createPage.show') }}">ثبت آگهی جدید</a></li>
                            <li><a href="{{ route('showAllVehicleAdsInAdminPanel') }}">لیست آگهی ها</a></li>
                        </ul>
                    </li>
                    <li class="has_sub">
                        <a href="javascript:void(0);" class="waves-effect"><i class="ti-face-smile"></i> <span
                                    style="font-size: 13px;"> آگهی های ویژه استخدامی </span> <span
                                    class="menu-arrow"></span> </a>
                        <ul class="list-unstyled">
                            <li><a href="{{ route('employs.ads.createPage.show') }}">ثبت آگهی جدید</a></li>
                            <li><a href="{{ route('showAllEmploysAdsInAdminPanel') }}">لیست آگهی ها</a></li>
                        </ul>
                    </li>
                    <li class="has_sub">
                        <a href="javascript:void(0);" class="waves-effect"><i class="ti-car"></i> <span
                                    style="font-size: 13px;"> ویزیتور </span> <span class="menu-arrow"></span> </a>
                        <ul class="list-unstyled">
                            <li><a href="{{ route('showVisitorCreatePage') }}">ثبت ویزیتور جدید</a></li>
                            <li><a href="{{ route('showListOfVisitorPage') }}">لیست ویزیتور ها</a></li>
                        </ul>
                    </li>
                    <li class="has_sub">
                        <a href="javascript:void(0);" class="waves-effect"><i class="ti-car"></i> <span
                                    style="font-size: 13px;"> جایزه ها </span> <span class="menu-arrow"></span> </a>
                        <ul class="list-unstyled">
                            <li><a href="{{ route('admin.award.create') }}">ثبت جایزه جدید</a></li>
                            <li><a href="{{ route('admin.award.index') }}">لیست جایزه ها</a></li>
                        </ul>
                    </li>
                    <li>
                        <a href="{{ route('business.refers.index') }}" class="waves-effect"><i class="fa fa-users"></i>
                            <span> مراجعات به کسب و کارها </span></a>
                    </li>
                </ul>
                <div class="clearfix"></div>
            </div>
            <div class="clearfix"></div>
        </div>
    </div>
    <!-- Left Sidebar End -->


    <!-- ============================================================== -->
    <!-- Start right Content here -->
    <!-- ============================================================== -->
@yield('content')
<!-- ============================================================== -->
    <!-- End Right content here -->
    <!-- ============================================================== -->


    <!-- Right Sidebar -->

    <!-- /Right-bar -->

</div>
<!-- END wrapper -->


<script>
    var resizefunc = [];
</script>

<script src="{{ URL::to('admin') }}/assets/js/bootstrap-rtl.min.js"></script>
<script src="{{ URL::to('admin') }}/assets/js/detect.js"></script>
<script src="{{ URL::to('admin') }}/assets/js/fastclick.js"></script>
<script src="{{ URL::to('admin') }}/assets/js/jquery.slimscroll.js"></script>
<script src="{{ URL::to('admin') }}/assets/js/jquery.blockUI.js"></script>
<script src="{{ URL::to('admin') }}/assets/js/waves.js"></script>
<script src="{{ URL::to('admin') }}/assets/js/wow.min.js"></script>
<script src="{{ URL::to('admin') }}/assets/js/jquery.nicescroll.js"></script>
<script src="{{ URL::to('admin') }}/assets/js/jquery.scrollTo.min.js"></script>
<!-- jQuery  -->
<script src="{{ URL::to('admin') }}/assets/plugins/moment/moment.js"></script>


<script src="{{ URL::to('admin') }}/assets/plugins/bootstrap-sweetalert/sweet-alert.min.js"></script>

<!-- Todojs  -->
<script src="{{ URL::to('admin') }}/assets/pages/jquery.todo.js"></script>

<!-- chatjs  -->
<script src="{{ URL::to('admin') }}/assets/pages/jquery.chat.js"></script>

<script src="{{ URL::to('admin') }}/assets/plugins/peity/jquery.peity.min.js"></script>
<script src="{{ URL::to('admin') }}/assets/plugins/bootstrap-sweetalert/sweet-alert.min.js"></script>
@include('admin.sweet_alert')
<script src="{{ URL::to('admin') }}/assets/js/jquery.core.js"></script>
<script src="{{ URL::to('admin') }}/assets/js/jquery.app.js"></script>
<script src="{{ URL::to('admin') }}/assets/js/main.js"></script>

@yield('js')


</body>
</html>
