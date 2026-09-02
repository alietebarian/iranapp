<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">

    <!-- viewport meta -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>صفحه اصلی طرح 1</title>

    <!-- owl carousel css -->
    <link rel="stylesheet" href="{{ url()->to('/css/owl.carousel.css') }}"/>

    <!-- slick slider css -->
    <link rel="stylesheet" href="{{ url()->to('/css/slick.css') }}">


    <!-- font awesome -->
    <link rel="stylesheet" href="{{ url()->to('/css/font-awesome.min.css') }}"/>

    <!-- bootstrap -->
    <link rel="stylesheet" href="{{ url()->to('/css/bootstrap.min.css') }}"/>

    <!-- animte css -->
    <link rel="stylesheet" href="{{ url()->to('/css/animate.css') }}"/>

    <!-- style css -->
    <link rel="stylesheet" href="{{ url()->to('/style.css') }}"/>

    <!-- responsive css -->
    <link rel="stylesheet" href="{{ url()->to('/css/responsive.css') }}">

    <!-- Favicon -->
    <link rel="shortcut icon" type="image/png" href="{{ url()->to('/images/favicon.png') }}"/>
</head>
<body class="host_home1">
<!-- preloader -->
<div class="preloader-bg">
    <div class='preloader'>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
    </div>
</div>

<!--================================
    1.START HERO-SECTION
=================================-->
<section class="hk_hero_section">
    <!-- tiny header starts -->
    <div class="hk_tiny_header">
        <div class="container">
            <div class="row">
                <div class="col-md-12">
                    <div class="contact_email">
                        <span><i class="fa fa-clock-o"></i>24/7 پشتیبانی</span><a href="tel:1234567890"><p>(123)
                                4567890</p></a>
                    </div>
                    <div class="hk_contact_lang">
                        <ul>
                            <li>
                                <a href="cart.html">
                                    <span class="fa fa-shopping-cart"></span>
                                    <p>سبد</p>
                                    <span class="hk_cart_item">(2)</span>
                                </a>
                            </li>
                            <li class="hk_login_modal">
                                @if(auth()->check())
                                    <a href="{{ route('user.logout') }}"><span class="fa fa-lock"></span>
                                        <p>خروج</p></a>
                                @else
                                    <a href="{{ route('register.index') }}"><span class="fa fa-lock"></span>
                                        <p>ورود</p></a>

                                @endif

                                <div class="login_modal_wrapper">
                                    <form action="#">
                                        <div class="hk_input_wrapper">
                                            <input type="text" placeholder="نام کاربری">
                                            <span class="fa fa-user"></span>
                                        </div>
                                        <div class="hk_input_wrapper">
                                            <input type="password" placeholder="رمز عبور">
                                            <span class="fa fa-lock"></span>
                                        </div>
                                        <p>رمز عبور خود را فراموش کرده اید؟ <a href="#">کلیک کنید</a></p>
                                        <input class="hk_btn hk_login_btn" type="submit" value="ورود">
                                    </form>
                                </div>
                            </li>
                            <li class="lang">
                                <a href="#"><span class="lng">انگلیسی</span> <span class="caret"></span></a>
                                <ul class="hk_lang_dropdown">
                                    <li><a href="#">انگلیسی</a></li>
                                    <li><a href="#">پارسی</a></li>
                                    <li><a href="#">آلمانی</a></li>
                                    <li><a href="#">اسپانیایی</a></li>
                                </ul>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- tiny header ends -->

    <!-- main menu starts -->
    <div class="hk_mainmenu">
        <nav class="navbar navbar-default hk_nav_shadow">
            <div class="container">
                <!-- Brand and toggle get grouped for better mobile display -->
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                            data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                        <span class="sr-only">تعویض ناوبری</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="{{ url()->to('/') }}"><img class="hk_svg" src="images/logo.svg"
                                                                             alt=""></a>
                </div>

                <!-- Collect the nav links, forms, and other content for toggling -->
                <div class="collapse navbar-left navbar-collapse" id="bs-example-navbar-collapse-1">
                    <ul class="nav navbar-nav">
                        <li class="hk_has_dropdown">
                            <a href="#">خانه <span class="fa fa-angle-down"></span></a>
                            <div class="hk_dropdwon">
                                <ul>
                                    <li>
                                        <a href="index.html">خانه طرح 1 <span class="fa fa-caret-left"></span></a>
                                    </li>
                                    <li>
                                        <a href="index2.html">خانه طرح 2 <span class="fa fa-caret-left"></span></a>
                                    </li>
                                </ul>
                            </div>
                        </li>
                        <li><a href="domain-search.html">دامین ها</a></li>
                        <li class="hk_has_dropdown">
                            <a href="web-hosting.html">هاستینگ<span class="fa fa-angle-down"></span></a>
                            <div class="hk_dropdwon">
                                <ul>
                                    <li><a href="web-hosting.html">میزبانی وب <span class="fa fa-caret-left"></span></a>
                                    </li>
                                    <li><a href="vps-hosting.html">میزبانی وب سرور مجازی <span
                                                    class="fa fa-caret-left"></span></a></li>
                                    <li><a href="cloud-hosting.html">میزبانی ابری <span class="fa fa-caret-left"></span></a>
                                    </li>
                                    <li><a href="dedicated-hosting.html">سرور اختصاصی <span
                                                    class="fa fa-caret-left"></span></a></li>
                                </ul>
                            </div>
                        </li>
                        <li class="hk_has_mehgamenu">
                            <a href="#">صفحات<span class="fa fa-angle-down"></span></a>
                            <div class="hk_megamenu">
                                <ul>
                                    <li><a href="index.html">خانه طرح 1 <span class="fa fa-caret-left"></span></a></li>
                                    <li><a href="index2.html">خانه طرح 2 <span class="fa fa-caret-left"></span></a></li>
                                    <li><a href="web-hosting.html">میزبانی وب <span class="fa fa-caret-left"></span></a>
                                    </li>
                                    <li><a href="vps-hosting.html">میزبانی سرور مجازی <span
                                                    class="fa fa-caret-left"></span></a></li>
                                    <li><a href="cloud-hosting.html">میزبانی ابری<span class="fa fa-caret-left"></span></a>
                                    </li>
                                    <li><a href="dedicated-hosting.html">سرور اختصاصی <span
                                                    class="fa fa-caret-left"></span></a></li>
                                </ul>
                                <ul>
                                    <li><a href="domain-search.html">دامین ها <span class="fa fa-caret-left"></span></a>
                                    </li>
                                    <li><a href="faq.html">پرسش و پاسخ<span class="fa fa-caret-left"></span></a></li>
                                    <li><a href="cart.html">سبد<span class="fa fa-caret-left"></span></a></li>
                                    <li><a href="checkout.html">وارسی <span class="fa fa-caret-left"></span></a></li>
                                    <li><a href="support.html">پشتیبانی <span class="fa fa-caret-left"></span></a></li>
                                    <li><a href="clinet-area.html">ناحیه کاربری<span
                                                    class="fa fa-caret-left"></span></a></li>
                                </ul>
                                <ul>
                                    <li><a href="about_us.html">درباره ما<span class="fa fa-caret-left"></span></a></li>
                                    <li><a href="blog.html">وبلاگ<span class="fa fa-caret-left"></span></a></li>
                                    <li><a href="single_blog.html">نوشته وبلاگ<span class="fa fa-caret-left"></span></a>
                                    </li>
                                    <li><a href="testimonial.html">گواهی نامه <span class="fa fa-caret-left"></span></a>
                                    </li>
                                    <li><a href="contact.html">تماس با ما <span class="fa fa-caret-left"></span></a>
                                    </li>
                                    <li><a href="error.html">خطا 404<span class="fa fa-caret-left"></span></a></li>
                                </ul>
                            </div>
                        </li>
                        <li class="hk_has_dropdown">
                            <a href="blog.html">وبلاگ<span class="fa fa-angle-down"></span></a>
                            <div class="hk_dropdwon">
                                <ul>
                                    <li><a href="blog.html">وبلاگ <span class="fa fa-caret-left"></span></a></li>
                                    <li><a href="single_blog.html">نوشته وبلاگ<span class="fa fa-caret-left"></span></a>
                                    </li>
                                </ul>
                            </div>
                        </li>
                        <li><a href="contact.html">تماس با ما</a></li>
                        <li><a href="support.html">پشتیبانی</a></li>
                    </ul>
                    <div class="hk_menu_btn_wrapper">
                        <a href="{{ route('register.index') }}" class="btn hk_btn hk_login">حساب</a>
                    </div>
                </div><!-- /.navbar-collapse -->

            </div><!-- /.container -->
        </nav>
    </div>
    <!-- main menu ends -->

</section>
<!--================================
    1.END HERO-SECTION
=================================-->
@yield('content')
<!--================================
    6.START PARTNER-TESTIMONOAL
=================================-->

<!--================================
    6.START CLIENTS AREA
=================================-->
<section class="hk_clients_area">
    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <div class="hk_clients_area_wrapper">
                    <div class="hk_clients_slider">
                        <div class="single_client">
                            <img src="images/client_org.png" alt="">
                        </div>
                        <div class="single_client">
                            <img src="images/client_org2.png" alt="">
                        </div>
                        <div class="single_client">
                            <img src="images/client_org3.png" alt="">
                        </div>
                        <div class="single_client">
                            <img src="images/client_org4.png" alt="">
                        </div>
                        <div class="single_client">
                            <img src="images/client_org5.png" alt="">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
<!--================================
    6.END CLIENTS AREA
=================================-->

<!--================================
    7.START PARTNER-TESTIMONOAL
=================================-->
<section class="hk_promot_section">
    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <div class="hk_promot_text">
                    <h2>یک سوال پیش فروش برای بسته های ما؟</h2>
                </div>
                <div class="hk_promot_btn">
                    <a href="#" class="hk_btn">الان شروع کن</a>
                </div>
            </div>
        </div>
    </div>
</section>
<!--================================
    8.START PARTNER-TESTIMONOAL
=================================-->

<!--================================
    9.START PARTNER-TESTIMONOAL
=================================-->
<footer>
    <div class="hk_footer_wrapper hk_section_padding">
        <div class="container">
            <div class="row">
                <div class="col-md-3 col-sm-4">
                    <div class="hk_about_us_wrapper">
                        <div class="hk_footer_logo">
                            <img class="hk_svg" src="images/logo.svg" alt="">
                        </div>
                        <div class="hk_about_us">
                            <p>لورم ایپسوم متنی است که ساختگی برای طراحی و چاپ آن مورد است. صنعت چاپ زمانی لازم بود
                                شرایطی شما باید فکر ثبت نام و طراحی، لازمه خروج می باشد</p>
                        </div>
                        <div class="hk_mail_subscription">
                            <p>ثبت نام برای پیشنهاد ویژه ما</p>
                            <form action="#" class="hk_subs_form">
                                <input class="email" type="email" placeholder="ایمیل خود را وارد کنید">
                                <input class="hk_btn  hk_subscribe" type="submit" value="عضویت">
                            </form>
                        </div>
                    </div>
                </div>
                <div class="col-md-4 col-sm-4 col-md-offset-2">
                    <div class="hk_footer_widgets">
                        <div class="hk_widget_title">
                            <h4>کشف صفحات ما</h4>
                        </div>
                        <div class="hk_footer_links">
                            <ul>
                                <li><a href="#">خانه</a></li>
                                <li><a href="#">درباره ما</a></li>
                                <li><a href="#">خدمات</a></li>
                                <li><a href="#">وبلاگ</a></li>
                                <li><a href="#">هاستینگ</a></li>
                                <li><a href="#">بزودی</a></li>
                            </ul>
                        </div>
                        <div class="hk_footer_links">
                            <ul>
                                <li><a href="#">خانه</a></li>
                                <li><a href="#">درباره ما</a></li>
                                <li><a href="#">خدمات</a></li>
                                <li><a href="#">وبلاگ</a></li>
                                <li><a href="#">هاستینگ</a></li>
                                <li><a href="#">بزودی</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 col-md-offset-0 col-lg-2 col-sm-4 col-lg-offset-1">
                    <div class="hk_footer_widgets hk_footer_social">
                        <div class="hk_widget_title">
                            <h4>ارتباط با ما</h4>
                        </div>
                        <div class="hk_footer_links hk_social">
                            <ul>
                                <li><a href="tel:4567890"><span class="fa fa-phone"></span>
                                        <p>(123) 4567890</p></a></li>
                                <li><a href="maitlto:yourmail@gmail.com"><span class="fa fa-envelope-o"></span>
                                        <p>yourmail@gmail.com</p></a></li>
                                <li><a href="https://www.facebook.com/" target="_blank"><span
                                                class="fa fa-facebook"></span>
                                        <p>فیس بوک</p></a></li>
                                <li><a href="#" target="_blank"><span class="fa fa-twitter"></span>
                                        <p>توییتر</p></a></li>
                                <li><a href="#" target="_blank"><span class="fa  fa-google-plus"></span>
                                        <p>گوگل+</p></a></li>
                                <li><a href="#" target="_blank"><span class="fa fa-linkedin"></span>
                                        <p>یوتیوب</p></a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <div class="hk_tiny_footer">
                    <p>کپی رایت © 2016 هاست گینگ. <br>کلیه حقوق محفوظ است. راست چین شده توسط <a href="">مهدی رضوان
                            پور</a></p>
                </div>
            </div>
        </div>
    </div>
</footer>
<!--================================
    9.START PARTNER-TESTIMONOAL
=================================-->


<!--//////////////////// JS GOES HERE ////////////////-->

<!-- jquery latest version -->
<script src="{{ url()->to('/js/jquery-1.12.3.js') }}"></script>

<!-- bootstrap js -->
<script src="{{ url()->to('/js/bootstrap.min.js') }}"></script>

<!-- jquery easing 1.3 -->
<script src="{{ url()->to('/js/jquery.easing1.3.js') }}"></script>

<!-- Owl carousel js-->
<script src="{{ url()->to('/js/owl.carousel.min.js') }}"></script>

<!-- slick slider js -->
<script src="{{ url()->to('/js/slick.min.js') }}"></script>

<!-- waypoint js -->
<script src="{{ url()->to('/js/waypoints.min.js') }}"></script>

<!-- google map js -->
<script src="http://maps.googleapis.com/maps/api/js"></script>

<!-- smoothscroll js -->
<script src="{{ url()->to('/js/SmoothScroll.chrome.js') }}"></script>

<script type="text/javascript" src="{{ url()->to('/js/jquery.validate.min.js') }}"></script>

<script type="text/javascript" src="{{ url()->to('/js/sweetalert.min.js') }}"></script>

@include('components.swal')
<!-- Main js -->
<script src="{{ url()->to('/js/main.js') }}"></script>
</body>
</html>