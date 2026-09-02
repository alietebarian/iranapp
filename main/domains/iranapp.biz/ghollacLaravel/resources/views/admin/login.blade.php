<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="author" content="Farhad Ashtari">
    <meta name="robots" content="noindex">
    <meta name="googlebot" content="noindex">

    <link rel="shortcut icon" href="{{ URL::to('/admin') }}/assets/images/favicon_1.ico">

    <title>ایران اپ | ورود به حساب کاربری</title>
    <link href="{{ URL::to('/admin') }}/assets/plugins/bootstrap-sweetalert/sweet-alert.css" rel="stylesheet" type="text/css">
    <link href="{{ URL::to('/admin') }}/assets/css/bootstrap-rtl.min.css" rel="stylesheet" type="text/css" />
    <link href="{{ URL::to('/admin') }}/assets/css/core.css" rel="stylesheet" type="text/css" />
    <link href="{{ URL::to('/admin') }}/assets/css/components.css" rel="stylesheet" type="text/css" />
    <link href="{{ URL::to('/admin') }}/assets/css/icons.css" rel="stylesheet" type="text/css" />
    <link href="{{ URL::to('/admin') }}/assets/css/pages.css" rel="stylesheet" type="text/css" />
    <link href="{{ URL::to('/admin') }}/assets/css/responsive.css" rel="stylesheet" type="text/css" />

    <!-- HTML5 Shiv and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->

    <script src="{{ URL::to('/admin') }}/assets/js/modernizr.min.js"></script>

</head>
<body>

<div class="account-pages"></div>
<div class="clearfix"></div>
<div class="wrapper-page">
    <div class=" card-box">
        <div class="panel-heading">
            <h3 class="text-center"> ورود  به حساب <strong class="text-custom">ایران اپ</strong> </h3>
        </div>


        <div class="panel-body">
            <form class="form-horizontal m-t-20" action="{{ route('doLogin') }}" method="post">
                {{ csrf_field() }}
                <div class="form-group ">
                    <div class="col-xs-12">
                        <input class="form-control" name="login" type="text" required="" placeholder="ایمیل یا تلفن همراه">
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-xs-12">
                        <input class="form-control" name="password" type="password" required="" placeholder="پسورد">
                    </div>
                </div>

                <div class="form-group ">
                    <div class="col-xs-12">
                        <div class="checkbox checkbox-primary">
                            <input name="remember_me" id="checkbox-signup" type="checkbox">
                            <label for="checkbox-signup">
                                منو به خاطر بسپار
                            </label>
                        </div>

                    </div>
                </div>

                <div class="form-group text-center m-t-40">
                    <div class="col-xs-12">
                        <button class="btn btn-pink btn-block text-uppercase waves-effect waves-light" type="submit">ورود</button>
                    </div>
                </div>
            </form>

        </div>
    </div>
</div>




<script>
    var resizefunc = [];
</script>

<!-- jQuery  -->
<script src="{{ URL::to('/admin') }}/assets/js/jquery.min.js"></script>
<script src="{{ URL::to('/admin') }}/assets/js/bootstrap-rtl.min.js"></script>
<script src="{{ URL::to('/admin') }}/assets/js/detect.js"></script>
<script src="{{ URL::to('/admin') }}/assets/js/fastclick.js"></script>
<script src="{{ URL::to('/admin') }}/assets/js/jquery.slimscroll.js"></script>
<script src="{{ URL::to('/admin') }}/assets/js/jquery.blockUI.js"></script>
<script src="{{ URL::to('/admin') }}/assets/js/waves.js"></script>
<script src="{{ URL::to('/admin') }}/assets/js/wow.min.js"></script>
<script src="{{ URL::to('/admin') }}/assets/js/jquery.nicescroll.js"></script>
<script src="{{ URL::to('/admin') }}/assets/js/jquery.scrollTo.min.js"></script>
<script src="{{ URL::to('admin') }}/assets/plugins/bootstrap-sweetalert/sweet-alert.min.js"></script>
@include('admin.sweet_alert')
<script src="{{ URL::to('/admin') }}/assets/js/jquery.core.js"></script>
<script src="{{ URL::to('/admin') }}/assets/js/jquery.app.js"></script>

</body>
</html>