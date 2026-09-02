<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="author" content="Farhad Ashtari">

    <link rel="shortcut icon" href="{{ URL::to('/admin') }}/assets/images/favicon_1.ico">

    <title>ایران اپ - صفحه محاسبه درصد</title>

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
                    <span class="badge badge-danger">محاسبه درصد</span>
                </h2>
            </div>
            <div class="card-body">
                <p class="text-danger">
                    تست محاسبه درصد پرداخت
                </p>
                @if(isset($price))
                    <table class="table table-bordered">
                        <tr>
                            <td>مبلغ پرداختی مشتری:</td>
                            <td>{{number_format($shabaPrice)}}</td>
                        </tr>
                        <tr>
                            <td>مبلغ دریافتی شما:</td>
                            <td>{{number_format($shabaPrice - $price)}}</td>
                        </tr>
                        <tr>
                            <td>کارمزد ایران اپ:</td>
                            <td>{{number_format($price)}}</td>
                        </tr>
                    </table>
                @endif
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

            <form id="form" action="{{route('webazin.discount.check')}}"
                  method="get"
                  class="border">
                {{csrf_field()}}
                <div class="form-group form-inline">
                    <label for="price" class="">مبلغ مورد نظر</label>
                    <input type="number" min="1" step="any" class="form-control" name="toman" required value="1000">
                </div>
                <div class="form-group form-inline">
                    <label for="discount" class="">درصد مورد نظر</label>
                    <input id="discount" type="number" min="1" step="any" class="form-control" name="discount" required
                           value="10">
                </div>

                <hr>
                <div class="form-group">
                    <input id="btn" type="submit" value="محاسبه" class="btn btn-lg btn-success btn-block"
                           style="position: fixed;bottom: 0%;left: 0%;">
                </div>
            </form>
        </div>
    </div>


    <script>
        var resizefunc = [];
        $('#form').on('submit', function () {
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
