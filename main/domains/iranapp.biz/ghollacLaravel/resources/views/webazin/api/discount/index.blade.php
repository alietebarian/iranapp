<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="author" content="Farhad Ashtari">

    <link rel="shortcut icon" href="{{ URL::to('/admin') }}/assets/images/favicon_1.ico">

    <title>ایران اپ - صفحه پرداخت</title>

    <link href="{{ URL::to('/admin') }}/assets/css/bootstrap-rtl.min.css" rel="stylesheet" type="text/css"/>
    <link href="{{ URL::to('/admin') }}/assets/css/core.css" rel="stylesheet" type="text/css"/>
    <link href="{{ URL::to('/admin') }}/assets/css/components.css" rel="stylesheet" type="text/css"/>
    <link href="{{ URL::to('/admin') }}/assets/css/icons.css" rel="stylesheet" type="text/css"/>
    <link href="{{ URL::to('/admin') }}/assets/css/pages.css" rel="stylesheet" type="text/css"/>
    <link href="{{ URL::to('/admin') }}/assets/css/responsive.css" rel="stylesheet" type="text/css"/>

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
    <div class="ex-page-content text-center">
        <div class="card">
            <div class="card-title">
                <h2>
                    {{$adsItem->title}}
                    <span class="badge badge-danger">{{$adsItem->discount}}%</span>
                </h2>
            </div>
            <div class="card-body">
                <p class="text-danger">
                    پرداخت خود را نهایی کنید
                </p>
                <p class="text-danger">
                    پس از پرداخت برای پذیرنده پیام پرداخت شما ارسال میگردد
                </p>
            </div>
            <hr>
        </div>
        @if ($errors->any())
            <div class="alert alert-danger">
                <ul>
                    @foreach ($errors->all() as $error)
                        <li>{{ $error }}</li>
                    @endforeach
                </ul>
            </div>
        @endif

        <form id="form" action="{{route('webazin.discount.pay',['ads_id'=>$adsItem->id]).'?token='.$token}}"
              method="post"
              class="border">
            {{csrf_field()}}
            <div class="form-group form-inline">
                <label for="price" class="">مبلغ فاکتور</label>
                <input type="number" min="1" step="any" class="form-control" name="price"
                       placeholder="مبلغ فاکتور خود را پس از کسر {{$adsItem->discount}}% به ریال وارد کنید" required>
            </div>
            <hr>
            <div class="form-group">
                <input id="btn" type="submit" value="پرداخت" class="btn btn-lg btn-success btn-block"
                       style="position: fixed;bottom: 0%;left: 0%;">
            </div>
        </form>
    </div>
</div>


<script>
    var resizefunc = [];
    $('#form').on('submit', function(){
        alert('1111')
        $('#btn').attr('type', 'hidden')
    })
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


<script src="{{ URL::to('/admin') }}/assets/js/jquery.core.js"></script>
<script src="{{ URL::to('/admin') }}/assets/js/jquery.app.js"></script>

</body>
</html>
