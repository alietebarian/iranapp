package com.ideabonyan.iranapp.Utils;


public class StaticData {

    /**
     * Public ad listings take a token optionally: with one the server also reports the
     * viewer's own like on each ad, without one it simply returns them unvoted.
     */
    public static String optionalTokenQuery(android.content.Context context) {
        com.ideabonyan.iranapp.UserData.User user =
                com.ideabonyan.iranapp.UserData.UserHelper.LoadUserInfo(context);
        if (!user.isLoggedIn()) return "";
        String token = new com.ideabonyan.iranapp.UserData.UserSessionManager(context).getLoginToken();
        if (token == null || token.isEmpty()) return "";
        return "&token=" + token;
    }


//    public static String DOMAIN = "http://192.168.1.4/gollac/public_html";http://
    public static String DOMAIN = "http://iranapp.biz";
//    public static String DOMAIN = "http://asreesfahanapp.com";
    public static String DOMAIN_WITH_API = DOMAIN + "/api";

    static public String REGISTER = DOMAIN_WITH_API + "/register" +                   "?first_name=فرهاد&last_name=اشتری&password=123456&fcm_token=asdfasdf&mobile=09139665377";
    static public String LOGIN = DOMAIN_WITH_API + "/login" +                         "?mobile=09139665376&password=123456";
    static public String distance = DOMAIN_WITH_API + "/distance";
    static public String CONFIRMATION = DOMAIN_WITH_API + "/verify-and-login" +       "?mobile=09139665376&password=123456&token=1234";
    static public String UPDATE_PASS = DOMAIN_WITH_API + "/password/change" ;//+         "?password=123456789&token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjEsImlzcyI6Imh0dHA6Ly93d3cuZ2hvbGxhYy5mYXJoYWQtYXNodGFyaS5jb20vYXBpL2xvZ2luIiwiaWF0IjoxNTAxNzQzMDMzLCJleHAiOjE1MDE3NDY2MzMsIm5iZiI6MTUwMTc0MzAzMywianRpIjoiQUdXZHNGenVlc0M5QVRJYSJ9.HKayrv8fsMxkm3fqdFTb8df2B-uY2DaO7C2XEXe5ixM";
    static public String UPDATE_NAME = DOMAIN_WITH_API + "/account-info/change" ;// + "?first_name=farhad&last_name=ashtari&token=eyJ0eXAiOiJ" ;

    static public String PROVINCE = DOMAIN_WITH_API + "/provinces";
    static public String CITY = DOMAIN_WITH_API + "/provinces/1/cities";

    static public String CATEGORIES = DOMAIN_WITH_API + "/city/57/ads/category";
    static public String All_CATEGORIES = DOMAIN_WITH_API + "/categories";// +          "?ads_type=need";
    static public String HOME_SUB_CATEGORIES = DOMAIN_WITH_API + "/categories/2/subcategories";
    static public String ALL_SUB_CATEGORIES = DOMAIN_WITH_API + "/categories/2/subcategories/all";

    static public String ADS = DOMAIN_WITH_API + "/cities/57/subCategories/1/ads";
    static public String SINGE_AD = DOMAIN_WITH_API + "/ads/id/"; //+ "2";

    static public String NEWS = DOMAIN_WITH_API + "/news";
    static public String SINGLE_NEWS = DOMAIN_WITH_API + "/news/";//+ "1";

    static public String SPLASH_AD = DOMAIN_WITH_API + "/home-page/ads/57/vip";
    static public String NEWEST_ADS = DOMAIN_WITH_API + "/ads/latest";// +               "?city_id=57";

    static public String USER_PREF_DATA_IN_SHOW_AD = DOMAIN_WITH_API + "/users/ads/2" +  "?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjIyLCJpc3MiOiJodHRwOi8vMTkyLjE2OC4xLjQ6ODA4MC9naG9sbGFjL2dob2xsYWMvYXBpL2xvZ2luIiwiaWF0IjoxNTAyOTUwMzE3LCJleHAiOjE1MDM1NTUxMTcsIm5iZiI6MTUwMjk1MDMxNywianRpIjoiN2RPdGRQMDFLZkJOM3hrdSJ9.qnUN1WLPHisatp9FRPnD66u-HgxQXl1pmufyVeZjG_0";
    static public String LIKE_DISLIKE_DATA_IN_SHOW_AD = DOMAIN_WITH_API + "/ads/58/likes-and-dislikes";
    static public String MARKING_FAVORITE = DOMAIN_WITH_API + "/ads/2/fav" +             "?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjIyLCJpc3MiOiJodHRwOi8vMTkyLjE2OC4xLjQ6ODA4MC9naG9sbGFjL2dob2xsYWMvYXBpL2xvZ2luIiwiaWF0IjoxNTAyOTUwMzE3LCJleHAiOjE1MDM1NTUxMTcsIm5iZiI6MTUwMjk1MDMxNywianRpIjoiN2RPdGRQMDFLZkJOM3hrdSJ9.qnUN1WLPHisatp9FRPnD66u-HgxQXl1pmufyVeZjG_0";
    static public String UPVOTING_DOWNVOTING = DOMAIN_WITH_API + "/ads/10/likes" +       "?like_type=dislike&token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjIwLCJpc3MiOiJodHRwOi8vMTkyLjE2OC4xLjQ6ODA4MC9naG9sbGFjL2dob2xsYWMvYXBpL2xvZ2luIiwiaWF0IjoxNTAzNTYyNjg3LCJleHAiOjE1MDQxNjc0ODcsIm5iZiI6MTUwMzU2MjY4NywianRpIjoiYUhTaGxSeU9pb3lPcWxZaSJ9.d1Upnu8gBuH1L4KIyO_ioenPVRSGmHMziUw6f79q0xk";
    static public String DEVOTING = DOMAIN_WITH_API + "/ads/130/like/off" +              "?like_type=like&token=sdfsdf";

    static public String ALL_FAVORITES = DOMAIN_WITH_API + "/users/ads/favorites";// +   "?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjIyLCJpc3MiOiJodHRwOi8vMTkyLjE2OC4xLjQ6ODA4MC9naG9sbGFjL2dob2xsYWMvYXBpL2xvZ2luIiwiaWF0IjoxNTAyOTUwMzE3LCJleHAiOjE1MDM1NTUxMTcsIm5iZiI6MTUwMjk1MDMxNywianRpIjoiN2RPdGRQMDFLZkJOM3hrdSJ9.qnUN1WLPHisatp9FRPnD66u-HgxQXl1pmufyVeZjG_0";

    static public String SEARCH = DOMAIN_WITH_API + "/ads/search";// +                    "?title=یخ&subcategory_id=1&address=توحید&type=discount&category_id=1&city_id=1&province_id=1";

    static public String PLANS = DOMAIN_WITH_API + "/ads/plans";

    static public String NEW_AD =DOMAIN_WITH_API + "/ads";// + "?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjIwLCJpc3MiOiJodHRwOi8vMTkyLjE2OC4xLjQ6ODA4MC9naG9sbGFjL2dob2xsYWMvYXBpL2xvZ2luIiwiaWF0IjoxNTAzNTYyNjg3LCJleHAiOjE1MDQxNjc0ODcsIm5iZiI6MTUwMzU2MjY4NywianRpIjoiYUhTaGxSeU9pb3lPcWxZaSJ9.d1Upnu8gBuH1L4KIyO_ioenPVRSGmHMziUw6f79q0xk&title=کلینیکپارسیان&latitude=32.524102&longitude=51.201145&city_id=1&address=asdfasdfsadf&type=discount&sub_category_id=1&email=farhadkb6868@gmail.com&mobile=09139665376&tel1=03132504052&tel2=03132504052&link=http://www.idea-bonyan.com&discount=50&working_time=9 صبح الی 12ظهر&telegram=t.me/xx.xx&instagram=instagram.me/xxx.xxx&notes=asdfasdfasdfasdfasdfasf&ads_plan_id=2";
    static public String UPDATE_AD =DOMAIN_WITH_API + "/ads/";// + "23 + "/update" + "?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjIwLCJpc3MiOiJodHRwOi8vMTkyLjE2OC4xLjQ6ODA4MC9naG9sbGFjL2dob2xsYWMvYXBpL2xvZ2luIiwiaWF0IjoxNTAzNTYyNjg3LCJleHAiOjE1MDQxNjc0ODcsIm5iZiI6MTUwMzU2MjY4NywianRpIjoiYUhTaGxSeU9pb3lPcWxZaSJ9.d1Upnu8gBuH1L4KIyO_ioenPVRSGmHMziUw6f79q0xk&title=کلینیکپارسیان&latitude=32.524102&longitude=51.201145&city_id=1&address=asdfasdfsadf&type=discount&sub_category_id=1&email=farhadkb6868@gmail.com&mobile=09139665376&tel1=03132504052&tel2=03132504052&link=http://www.idea-bonyan.com&discount=50&working_time=9 صبح الی 12ظهر&telegram=t.me/xx.xx&instagram=instagram.me/xxx.xxx&notes=asdfasdfasdfasdfasdfasf&ads_plan_id=2";

    static public String USER_ADS =DOMAIN_WITH_API + "/current-user/ads";//             + "?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjIwLCJpc3MiOiJodHRwOi8vMTkyLjE2OC4xLjQ6ODA4MC9naG9sbGFjL2dob2xsYWMvYXBpL2xvZ2luIiwiaWF0IjoxNTAzNTYyNjg3LCJleHAiOjE1MDQxNjc0ODcsIm5iZiI6MTUwMzU2MjY4NywianRpIjoiYUhTaGxSeU9pb3lPcWxZaSJ9.d1Upnu8gBuH1L4KIyO_ioenPVRSGmHMziUw6f79q0xk";

    static public String AROUND_ME =DOMAIN_WITH_API + "/nearest-ads";// +        "?latitude=31.526321&longitude=51.321020&sub_category_id=1&category_id=1";

    static public String REMOVE_AD =DOMAIN_WITH_API + "/ads/12/delete" +                  "?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjIwLCJpc3MiOiJodHRwOi8vMTkyLjE2OC4xLjQ6ODA4MC9naG9sbGFjL2dob2xsYWMvYXBpL2xvZ2luIiwiaWF0IjoxNTAzNTYyNjg3LCJleHAiOjE1MDQxNjc0ODcsIm5iZiI6MTUwMzU2MjY4NywianRpIjoiYUhTaGxSeU9pb3lPcWxZaSJ9.d1Upnu8gBuH1L4KIyO_ioenPVRSGmHMziUw6f79q0xk";

//    static public String UPDATE_NOTIFICATION_STATUS =DOMAIN_WITH_API + "/users/notifications/setting";// + "?send_news_notifications=1&send_ads_notifications=1&token=sdfasdfsdf";
    static public String UPDATE_NOTIFICATION_STATUS =DOMAIN_WITH_API + "/notification-settings/update";// + "?send_news_notifications=1&send_ads_notifications=1&token=sdfasdfsdf"; => this is Firebase token

    static public String RESEND_ACTIVATION_CODE =DOMAIN_WITH_API + "/users/verify-token/regenerate";// + "?mobile=11111111111";

    static public String CHANGE_NUMBER =DOMAIN_WITH_API + "/users/mobile/change";// +?old_mobile=22222222222&new_mobile=122222222225";

    static public String NOTIFICATIONS_IF_NOT_LOGGED_IN =DOMAIN_WITH_API + "/notification-settings/create";// +"?token=asdfasdf"; => this is Firebase token

    //FORGETTING PASSWORD
    static public String FORGOT_PASS_NUM =DOMAIN_WITH_API + "/users/password/reset-password/token/send";//?mobile=121212";
    static public String FORGOT_PASS_CONFIRM_CODE =DOMAIN_WITH_API + "/reset-password/token/verify";//?mobile=09139665376&verifyToken=24938";
    static public String FORGOT_PASS_FINAL_HIT =DOMAIN_WITH_API + "/reset-password";//?mobile=09139665376&verifyToken=24938";



//    http://iranapp.biz/currencies



    static public String setfav =DOMAIN_WITH_API + "/vip-ads/favorite";

    ////////// just for car
    static public String get_car =DOMAIN_WITH_API + "/vehicles/ads";
    static public String get_all_brand =DOMAIN_WITH_API + "/vehicles/brands";
    static public String get_all_modell =DOMAIN_WITH_API + "/vehicles/brands/";
    static public String get_all_region =DOMAIN_WITH_API + "/provinces/cities/";
    static public String getall_culander_volun =DOMAIN_WITH_API + "/cylinder-volumes";
    static public String add_cae =DOMAIN_WITH_API + "/vehicles";
    static public String get_all_my_car_ads =DOMAIN_WITH_API + "/vehicles/users/ads";
    static public String search_car =DOMAIN_WITH_API + "/vehicles/search";
    static public String get_fav_car =DOMAIN_WITH_API + "/users/vehicle-ads/favorites";

///////////////////////////just for get home

    static public String estates =DOMAIN_WITH_API + "/estates";
    static public String get_fav_home =DOMAIN_WITH_API + "/users/estate-ads/favorites";

    ///////just for job

    static public String employs =DOMAIN_WITH_API + "/employs";
    static public String get_fav_job =DOMAIN_WITH_API + "/users/employs-ads/favorites";


    /////////////


    static public String LINK_IN_BAZAR = "https://cafebazaar.ir/app/com.ideabonyan.iranapp/?l=fa";

    static public String CURRENCY_LINK = DOMAIN + "/currencies";
    static public String LIVE_SCORES = DOMAIN + "/football";
    static public String PERSIAN_GULF_LEAGUE = DOMAIN + "/gulf";
    static public String BOOK_SEARCH = "http://api.wikiapi.ir/book/api/v1/search.php";

    public static String getappverioncode=DOMAIN_WITH_API+"/app-version";
    static public String INSTALL_STATS = DOMAIN_WITH_API + "/app-stats/installs";


    public static String download_link="https://cafebazaar.ir/app/com.ideabonyan.iranapp/";
}
