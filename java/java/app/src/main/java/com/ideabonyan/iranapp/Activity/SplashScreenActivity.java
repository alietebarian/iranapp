package com.ideabonyan.iranapp.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.farsitel.bazaar.IUpdateCheckService;
import com.ideabonyan.iranapp.Fragment.Dialogs.NotificationDialog;
import com.ideabonyan.iranapp.Fragment.Dialogs.Show_New_App_Version_Dialog;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data2;
import com.ideabonyan.iranapp.Interface.NewNotificationCame;
import com.ideabonyan.iranapp.Models.AdsToBeListed;
import com.ideabonyan.iranapp.Models.NewsData;
import com.ideabonyan.iranapp.Models.PhotosData;
import com.ideabonyan.iranapp.Models.VipAd;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back2;
import com.ideabonyan.iranapp.Utils.NotificationUtils;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ideabonyan.iranapp.Utils.StaticData.DOMAIN_WITH_API;

public class SplashScreenActivity extends AppCompatActivity implements Get_Insert_Edit_Data, Get_Insert_Edit_Data2 {
    TextView txt_appVersion;
    AdsToBeListed ad;
    NewsData newsData;
    UserSessionManager userSessionManager;
    String adStatusId = "0", newsStatusId = "1";
    ViewGroup rootView;
    ImageView i, r, a1, n, a2, p1, p2,img_asreEsfahanLogo;
    TextView txt_t4,txt_t3,txt_t2,txt_t1;
    ProgressBar progressBar;
    int REQUEST_ID_MULTIPLE_PERMISSIONS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        checkAndRequestPermissions();
        userSessionManager = new UserSessionManager(SplashScreenActivity.this);
        if (userSessionManager.getProvinceInfo().equals("0") || userSessionManager.getCityInfo().equals("0")) {
            userSessionManager.setProvinceInfo("4");
            userSessionManager.setCityInfo("57");
        }
        User user=UserHelper.LoadUserInfo(SplashScreenActivity.this);
        initializer();
//        initService();

        Log.v("show_intro",userSessionManager.getShowIntroPage()+"");
        if (userSessionManager.getDont_show_new_version().equals("0")) {
            getappversioncode();
        } else {
            if (userSessionManager.getShowIntroPage()==0){
                runTimer2();
                Log.v("show_intro",userSessionManager.getShowIntroPage()+"1");
                doAnimation();

            }else{
                Log.v("show_intro",userSessionManager.getShowIntroPage()+"2");

                if (user.isLoggedIn()&&user.isVerrified()){
                    getData();
                    Log.v("show_intro",userSessionManager.getShowIntroPage()+"3");
                    runTimer();
                    doAnimation();
                }else if (user.isLoggedIn()&!user.isVerrified()){
                    runTimer3();
                    Log.v("show_intro",userSessionManager.getShowIntroPage()+"4");
                    doAnimation();


                }else{
                    runTimer4();
                    Log.v("show_intro",userSessionManager.getShowIntroPage()+"5");
                    doAnimation();


                }

            }

        }
        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                img_asreEsfahanLogo.animate().alpha(1).translationY(-30).withLayer().setDuration(500);

            }
        }, 200);


    }

    private void getappversioncode() {
        String url = StaticData.getappverioncode;
        HashMap<String, String> params = new HashMap<>();
        Get_Volley_Call_Back.binddata(SplashScreenActivity.this);
        Get_Volley_Call_Back.Call_Volley(SplashScreenActivity.this, params, url, Request.Method.GET, 1500);
    }

    int ii;
    List<TextView> nameLetters;

    private void doAnimation() {
        nameLetters = new ArrayList<>();
        nameLetters.add(txt_t1);
        nameLetters.add(txt_t2);
        nameLetters.add(txt_t3);
        nameLetters.add(txt_t4);

        timerHandler.postDelayed(timerRunnable, 50);

    }

    int animatingNumber = 0;
    final Handler timerHandler = new Handler();

    Runnable timerRunnable = new Runnable() {

        @SuppressLint("NewApi")
        @Override
        public void run() {
            ///////////////////////////

            if (animatingNumber < nameLetters.size()) {
                nameLetters.get(animatingNumber).animate().alpha(1).translationY(-30).withLayer();
                animatingNumber++;
            } else {
                progressBar.animate().alpha(1).withLayer();
                timerHandler.removeCallbacks(timerRunnable);
            }
            timerHandler.postDelayed(this, 100);
        }
    };


    private void initializer() {
        txt_t4 =   findViewById(R.id.txt_t4);
        txt_t3 =   findViewById(R.id.txt_t3);
        txt_t2 =   findViewById(R.id.txt_t2);
        txt_t1 =   findViewById(R.id.txt_t1);
        img_asreEsfahanLogo =   findViewById(R.id.img_asreEsfahanLogo);
        txt_appVersion =   findViewById(R.id.txt_appVersion);
        rootView = (ViewGroup) findViewById(R.id.splashScreen);
        progressBar = (ProgressBar) findViewById(R.id.splashScreenProgressBar);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            txt_appVersion.setText("نسخه " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //// TODO: 2/5/2018  i change hear for remove city and province select
//{

        //}

    }

    boolean isDataCorrectlySavedNow = false;


    private void getData() {
        if (!(userSessionManager.getCityInfo().equals("0"))) {

            String url = DOMAIN_WITH_API + "/home-page/ads/" + userSessionManager.getCityInfo() + "/vip";

            Map<String, String> params = new HashMap<String, String>();
            Get_Volley_Call_Back.binddata(this);
            Get_Volley_Call_Back.Call_Volley(SplashScreenActivity.this, params, url, Request.Method.GET, 100);
        }

    }

    private void runTimer() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ////////////////////////////
                if (vipAd != null) {
                    MainActivity.vipAd = vipAd;
                }
                Bundle bundle = getIntent().getExtras();
                if (!(bundle != null && bundle.get("status") != null)) {
                    Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
//                    Toast.makeText(SplashScreenActivity.this, ""+bundle.get("status"), Toast.LENGTH_SHORT).show();
                    MainActivity.vipAd = null; //THIS MUST WORK -PLACE HOLDER-
                    if (bundle.get("status").toString().equals(adStatusId)) {
                        String url = StaticData.SINGE_AD + bundle.get("content_id").toString();
                        ge_notify_tData(url, 0);
                    } else if (bundle.get("status").toString().equals(newsStatusId)) {
                        String url = StaticData.SINGLE_NEWS + bundle.get("content_id").toString();
                        ge_notify_tData(url, 1);
                    } else if (bundle.get("status").toString().equals("10")) {
                        Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                        i.putExtra("show_notify", "1");
                        i.putExtra("status", bundle.get("status").toString());
                        i.putExtra("msg", bundle.get("msg").toString());

                        startActivity(i);

//                        newNotificationCame.onNewFirebaseNotification(bundle.get("msg").toString(),
//                                bundle.get("status").toString(), bundle.get("msg").toString());


//                        for (String key : bundle.keySet()) {
//                            Toast.makeText(SplashScreenActivity.this, ""+key, Toast.LENGTH_SHORT).show();
//                            Log.v("key",key+" = "+ bundle.get(key));
//                        }
//                        String string="";
//                        for (String key : bundle.keySet()) {
//
//                            string  = " " + key + " => " + bundle.get(key) + ";";
//                        }
//                        Toast.makeText(SplashScreenActivity.this, ""+string, Toast.LENGTH_SHORT).show();
//                        Log.v("string",string);
//                        newNotificationCame.onNewFirebaseNotification(bundle.get("body").toString(),
//                                bundle.get("status").toString(), bundle.get("content_id").toString());
                    }


                }
            }
        }, 4000);
    }

    private void runTimer2() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashScreenActivity.this, IntroActivity.class));
                SplashScreenActivity.this.finish();
            }
        }, 4000);
    }

    private void runTimer3() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashScreenActivity.this, ConfirmationActivity.class));
                SplashScreenActivity.this.finish();

            }
        }, 4000);
    }

    private void runTimer4() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashScreenActivity.this, SelectLoginOrRegiser.class));
                SplashScreenActivity.this.finish();

            }
        }, 4000);
    }


    VipAd vipAd = null;

    @Override
    public void on_volley_response(String response, int id) {
        if (id == 100) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                vipAd = VipAd.getvipAdObject(jsonObject.getJSONObject("ad"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (id == 55) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("status").equals("204") && isDataCorrectlySavedNow) {
                    userSessionManager.setNotificationInfo("2");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (id == 0) {
            try {
                JSONObject jsonObjectO = new JSONObject(response);
                JSONObject jsonObject = jsonObjectO.getJSONObject("ad");
                ad = new AdsToBeListed();
                ad.setId(jsonObject.getString("id"));
                ad.setTitle(jsonObject.getString("title"));
                ad.setLatitude(jsonObject.getString("latitude"));
                ad.setLongitude(jsonObject.getString("longitude"));
                ad.setAddress(jsonObject.getString("address"));
                ad.setType(jsonObject.getString("type"));
                ad.setUpdates_count(jsonObject.getString("updates_count"));
                ad.setMobile(jsonObject.getString("mobile"));
                ad.setTel1(jsonObject.getString("tel1"));
                ad.setTel2(jsonObject.getString("tel2"));
                ad.setLink(jsonObject.getString("link"));
                ad.setDiscount(jsonObject.getString("discount"));
                ad.setWorking_time(jsonObject.getString("working_time"));
                ad.setTelegram(jsonObject.getString("telegram"));
                ad.setInstagram(jsonObject.getString("instagram"));
                ad.setNotes(jsonObject.getString("notes"));
                ad.setAds_plan_id(jsonObject.getString("ads_plan_id"));
                ad.setStatus(jsonObject.getString("status"));
                ad.setUser_id(jsonObject.getString("user_id"));
                ad.setMax_number_of_update(jsonObject.getString("max_number_of_update"));
                ad.setCategory_id(jsonObject.getString("category_id"));
                ad.setProvince_id(jsonObject.getString("province_id"));
                ad.setCity_id(jsonObject.getString("city_id"));
                ad.setSub_category_id(jsonObject.getString("sub_category_id"));
                ad.setProvince_name(jsonObject.getString("province_name"));
                ad.setCity_name(jsonObject.getString("city_name"));
                ad.setAds_owner_name(jsonObject.getString("ads_owner_name"));
                List<PhotosData> photosData = PhotosData.Import(jsonObject.getJSONArray("photos"));
                ad.setPhotos(photosData);
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                Intent intent = new Intent(SplashScreenActivity.this, ShowAdActivity.class);
                intent.putExtra("ad", ad);
                startActivity(intent);


//                TransitionManager.beginDelayedTransition(rootView);


//                progressBar.setVisibility(View.GONE);
//                show.setVisibility(View.VISIBLE);
//                show.setClickable(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (id == 1) {
            try {
                JSONObject jsonObjectO = new JSONObject(response);
                JSONObject jsonObject = jsonObjectO.getJSONObject("news");
                newsData = new NewsData();
                newsData.setId(jsonObject.getString("id"));
                newsData.setTitle(jsonObject.getString("title"));
                newsData.setPassage(jsonObject.getString("passage"));
                newsData.setCreated_at(jsonObject.getString("created_at_fa"));
                newsData.setUpdated_at(jsonObject.getString("updated_at"));
                List<PhotosData> photosDatas = PhotosData.Import(jsonObject.getJSONArray("photos"));
                newsData.setPhotos(photosDatas);
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));

                Intent intent = new Intent(SplashScreenActivity.this, ShowNews.class);
                ShowNews.news = newsData;
                startActivity(intent);
//                TransitionManager.beginDelayedTransition(rootView);
//                progressBar.setVisibility(View.GONE);
//                show.setVisibility(View.VISIBLE);
//                show.setClickable(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (id == 1500) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if (status.equals("200")) {
                    String app_version = jsonObject.getString("app_version");
                    PackageInfo pInfo = null;
                    try {
                        pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
                        String version = pInfo.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }


                    if (pInfo == null) {
                        getData();
                        runTimer();
                        doAnimation();
                    } else {
                        Log.v("versioncode=", pInfo.versionCode + " version online=" + app_version);
                        if (Integer.parseInt(app_version) <= pInfo.versionCode) {
                            User user = UserHelper.LoadUserInfo(SplashScreenActivity.this);

                            if (userSessionManager.getShowIntroPage() == 0) {
                                runTimer2();
                                Log.v("show_intro", userSessionManager.getShowIntroPage() + "1");
                                doAnimation();

                            } else {
                                Log.v("show_intro", userSessionManager.getShowIntroPage() + "2");

                                if (user.isLoggedIn() && user.isVerrified()) {
                                    getData();
                                    Log.v("show_intro", userSessionManager.getShowIntroPage() + "3");
                                    runTimer();
                                    doAnimation();
                                } else if (user.isLoggedIn() & !user.isVerrified()) {
                                    runTimer3();
                                    Log.v("show_intro", userSessionManager.getShowIntroPage() + "4");
                                    doAnimation();


                                } else {
                                    runTimer4();
                                    Log.v("show_intro", userSessionManager.getShowIntroPage() + "5");
                                    doAnimation();


                                }

                            }
                        } else {
                            new Show_New_App_Version_Dialog(SplashScreenActivity.this)
                                    .show(getSupportFragmentManager(), "show_new_version");
                        }
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

        runTimer();
        doAnimation();
    }

    static NewNotificationCame newNotificationCame;

    public static void bindData(NewNotificationCame nnewNotificationCame) {
        newNotificationCame = nnewNotificationCame;
    }

    private void ge_notify_tData(String url, int id) {
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(SplashScreenActivity.this, params, url, Request.Method.GET, id);
    }


    private boolean checkAndRequestPermissions() {

        int sms = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int locSvc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int task = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_TASKS);
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int read_external_storeg = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int RECEIVE_SMS = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (sms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (locSvc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (task != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.GET_TASKS);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (read_external_storeg != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (RECEIVE_SMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new
                    String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

}
