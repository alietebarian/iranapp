package com.ideabonyan.iranapp.Activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Fragment.Dialogs.CityPickerDialogFragment;
import com.ideabonyan.iranapp.Fragment.Dialogs.NotificationDialog;
import com.ideabonyan.iranapp.Fragment.Dialogs.VipAdDialog;
import com.ideabonyan.iranapp.Fragment.Fav.Fav_Host;
import com.ideabonyan.iranapp.Fragment.KasbokarFragment;
import com.ideabonyan.iranapp.Fragment.MenuFragment;
import com.ideabonyan.iranapp.Fragment.NewsFragment;
import com.ideabonyan.iranapp.Fragment.home.Home_Host;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.LoginLogoutChangeListener;
import com.ideabonyan.iranapp.Interface.NewNotificationCame;
import com.ideabonyan.iranapp.Interface.ShowVipAdInterface;
import com.ideabonyan.iranapp.Interface.Show_Helpe;
import com.ideabonyan.iranapp.Models.ProvicesAndCities;
import com.ideabonyan.iranapp.Models.VipAd;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Config;
import com.ideabonyan.iranapp.Utils.CustomTypefaceSpan;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.NotificationUtils;
import com.ideabonyan.iranapp.service.MyFirebaseMessagingService;

import java.util.ArrayList;
import java.util.List;

//import com.ideabonyan.iranapp.Firebase.MyFirebaseMessagingService;
//import com.ideabonyan.iranapp.Firebase.NotificationUtils;

public class MainActivity extends AppCompatActivity implements Get_Insert_Edit_Data, ShowVipAdInterface
        , NewNotificationCame, LoginLogoutChangeListener {
    public static FragmentManager fragmentManager;
    public static Show_Helpe myShow_helpe;
    static public boolean isSplashAdShownYet = true;
    ////////////////////////////////
    // LOOKING FOR AD OR IF CITY IS PICKED
    ////////////////////////////////////
    static public VipAd vipAd = null;
    static androidx.fragment.app.FragmentTransaction fragmentTransaction;
    int REQUEST_ID_MULTIPLE_PERMISSIONS = 0;
    LinearLayout menuBTN, favoritesBTN, pishkhanBTN, newsBTN, kasbokarBTN, lin_big;
    ImageView menuIMG, favoritesIMG, pishkhanIMG, newsIMG, kasbokarIMG;
    TextView menuTXT, favoritesTXT, pishkhanTXT, pishkhanTXT2, pishkhanTXT1, newsTXT, kasbokarTXT;
    Context context;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Menu navMenu;
    MyTextView headerWelcomeTXT, headerAccountTXT;
    UserSessionManager userSessionManager;
    boolean isUserLoggedIn;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    boolean canExitNow = false;

    public static void setFragment(Context context, androidx.fragment.app.Fragment fragment, String tag, String s) {
        fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        //This is to get the current displaying fragment (IN THIS CASE, BEFORE IT CHANGES)
        Fragment currentDisplayingFragment = new Fragment();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment1 : fragments) {
                if (fragment1 != null && fragment1.isVisible())
                    currentDisplayingFragment = fragment1;
            }
        }

        if (currentDisplayingFragment.getTag() == tag) return;

//        getTransactionAnimation(currentDisplayingFragment, tag);
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);


        fragmentTransaction.replace(R.id.viewpager, fragment, tag).addToBackStack(s);
        fragmentTransaction.commit();

        //fragmentManager.popBackStack(); ====> this results to bad behaviour. i can manually override Back button.
    }

    public static void binshow_helpe(Show_Helpe show_helpe) {
        myShow_helpe = show_helpe;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Log.v("tokrnfcm", new UserSessionManager(MainActivity.this).getNotigy_code());
        initializer();
        fragmentChanger();
//        navigationViewItems();
        buttonClicks();
        smallStuff();

//        hideUnhideMenuItems();

        fireBaseStuff();

//        changeDrawerMenuFont();

        Get_Volley_Call_Back.binddata(this);
        VipAd.binddata(this);
        MyFirebaseMessagingService.bindData(this);
//        SplashScreenActivity.bindData(this);
        LoginActivity.bindData(this);
        ConfirmationActivity.bindData(this);
        DashboardActivity.bindData(this);

        showCityPickerOrLoadAd();
        checkAndRequestPermissions();

        if (getIntent().getStringExtra("status") != null) {
            NotificationDialog notificationDialog = new NotificationDialog();
            notificationDialog.setData(getIntent().getStringExtra("msg"), getIntent().getStringExtra("status")
                    , getIntent().getStringExtra("msg"));
            notificationDialog.show(getFragmentManager(), "NotificationDialog");
        }

        if (userSessionManager.getFistSee().equals("0")) {
            userSessionManager.setFistSee("1");

            AlertDialog.Builder b = new AlertDialog.Builder(context);

            b.setMessage("برای استفاده بهتر از تمامی خدمات این برنامه حتماً ابتدا از قسمت منو به راهنمای برنامه مراجعه نمایید")
                    .setPositiveButton("متوجه شدم", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    }) ;
            AlertDialog a = b.create();

            a.show();

            Button bq = a.getButton(DialogInterface.BUTTON_NEGATIVE);
            Button bq1 = a.getButton(DialogInterface.BUTTON_POSITIVE);
            bq1.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            bq1.setTextSize(16);
        }



    }

    private void initializer() {
        userSessionManager = new UserSessionManager(MainActivity.this);
        menuBTN = (LinearLayout) findViewById(R.id.menuBTN);
        favoritesBTN = (LinearLayout) findViewById(R.id.favoritesBTN);
        pishkhanBTN = (LinearLayout) findViewById(R.id.pishkhanBTN);
        newsBTN = (LinearLayout) findViewById(R.id.newsBTN);
        kasbokarBTN = (LinearLayout) findViewById(R.id.kasbokarBTN);

        menuIMG = (ImageView) findViewById(R.id.menuIMG);
        favoritesIMG = (ImageView) findViewById(R.id.favoritesIMG);
        pishkhanIMG = (ImageView) findViewById(R.id.pishkhanIMG);
        newsIMG = (ImageView) findViewById(R.id.newsIMG);
        kasbokarIMG = (ImageView) findViewById(R.id.kasbokarIMG);

        menuTXT = (TextView) findViewById(R.id.menuTXT);
        favoritesTXT = (TextView) findViewById(R.id.favoritesTXT);
        pishkhanTXT = (TextView) findViewById(R.id.pishkhanTXT);
        pishkhanTXT1 = (TextView) findViewById(R.id.pishkhanTXT1);
        pishkhanTXT2 = (TextView) findViewById(R.id.pishkhanTXT2);
        newsTXT = (TextView) findViewById(R.id.newsTXT);
        kasbokarTXT = (TextView) findViewById(R.id.kasbokarTXT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        drawerLayout = (DrawerLayout) findViewById(R.id.dl);
//        navigationView = (NavigationView) findViewById(R.id.nv);
//        navMenu = navigationView.getMenu();


//        View headerLayout = navigationView.getHeaderView(0);
//        headerWelcomeTXT = (MyTextView) headerLayout.findViewById(R.id.headerWelcomeTXT);
//        headerAccountTXT = (MyTextView) headerLayout.findViewById(R.id.headerAccountTXT);

    }

    private void smallStuff() {

        context = MainActivity.this;
        setFragment(MainActivity.this, new KasbokarFragment(), "Kasbokar", "Kasbokar");
        kasbokarIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
        kasbokarTXT.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

//        setSupportActionBar(toolbar);
    }

    private void buttonClicks() {
//        headerAccountTXT.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                User user = UserHelper.LoadUserInfo(MainActivity.this);
//                if (isUserLoggedIn) {
//
//                    if (user.isLoggedIn() && !user.isVerrified()) {
//
//                        Intent i = new Intent(MainActivity.this, ConfirmationActivity.class);
//                        i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
//                        startActivity(i);
//                    } else {
//
//                        Intent i = new Intent(MainActivity.this, DashboardActivity.class);
//                        startActivity(i);
//
//                    }
//                } else {
//
//                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
//                    i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    startActivity(i);
//                }
//
//                drawerLayout.closeDrawer(Gravity.RIGHT);
//            }
//        });
    }

    private void showCityPickerOrLoadAd() {

//        if (userSessionManager.getProvinceInfo().equals("0") || userSessionManager.getCityInfo().equals("0")) {
//// TODO: 2/5/2018  i change hear for remove city and province select
////            CityPickerDialogFragment cityPickerDialogFragment = new CityPickerDialogFragment();
////            cityPickerDialogFragment.setContext(MainActivity.this);
////            cityPickerDialogFragment.show(getSupportFragmentManager(), "ProvincePickerFragment");
//            loadVipAd(vipAd);
//
//        } else
        if (vipAd != null) {
            loadVipAd(vipAd);
        }
    }

    private void navigationViewItems() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                User user = UserHelper.LoadUserInfo(MainActivity.this);

                switch (item.getItemId()) {
                    case R.id.login:

                        if (user.isLoggedIn() && !user.isVerrified()) {

                            Intent i = new Intent(MainActivity.this, ConfirmationActivity.class);
//                            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(i);

                        } else {

                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
//                            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(i);
                        }
                        break;

                    case R.id.dashboard:

                        if (user.isLoggedIn() && !user.isVerrified()) {

                            Intent i = new Intent(MainActivity.this, ConfirmationActivity.class);
//                            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(i);
                        } else {

                            Intent i = new Intent(MainActivity.this, DashboardActivity.class);
                            startActivity(i);
                        }
                        break;

                    case R.id.navigationBarNewAd:

                        if (user.isLoggedIn() && !user.isVerrified()) {

                            Intent i = new Intent(MainActivity.this, ConfirmationActivity.class);
//                            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(i);


                        } else {
//
                            Intent i = new Intent(MainActivity.this, NewAdActivity.class);
                            startActivity(i);
//                            Select_Add_Type_Dialog select_add_type_dialog=new Select_Add_Type_Dialog();
//                            select_add_type_dialog.show(getFragmentManager(),"select_add_type_dialog");
                        }
                        break;

                    case R.id.defineLocation:


                        CityPickerDialogFragment cityPickerDialogFragment = new CityPickerDialogFragment();
                        cityPickerDialogFragment.setContext(MainActivity.this);
                        cityPickerDialogFragment.show(getSupportFragmentManager(), "ProvincePickerFragment");

                        break;
                    case R.id.aroundMe:

                        Intent intent = new Intent(context, AroundMeActivity.class);
                        startActivity(intent);
                }

                drawerLayout.closeDrawer(Gravity.RIGHT);
                return true;
            }
        });
    }

    private void changeDrawerMenuFont() {
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "IRANYekanRegularMobile(FaNum).ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void hideUnhideMenuItems() {
        User user = UserHelper.LoadUserInfo(MainActivity.this);
        if (user.isLoggedIn()) {
            headerWelcomeTXT.setText(user.getFirstname() + " " + user.getLastname() + " به قلک خوش آمدید");
            headerAccountTXT.setText("برای ورود به محیط کاربری کلیک کنید");
            isUserLoggedIn = true;

            navMenu.findItem(R.id.dashboard).setVisible(true);
            navMenu.findItem(R.id.navigationBarNewAd).setVisible(true);
            navMenu.findItem(R.id.login).setVisible(false);
        } else {
            headerWelcomeTXT.setText("کاربر مهمان به قلک خوش آمدید");
            headerAccountTXT.setText("برای ورود یا ثبت نام کلیک کنید");
            isUserLoggedIn = false;

            navMenu.findItem(R.id.dashboard).setVisible(false);
            navMenu.findItem(R.id.navigationBarNewAd).setVisible(false);
            navMenu.findItem(R.id.login).setVisible(true);
        }
    }

//    private static void getTransactionAnimation(Fragment currentDisplayingFragment, String tag) {
//
//
//        if (currentDisplayingFragment.getTag() == "Kasbokar")
//            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
//
//        else if (currentDisplayingFragment.getTag() == "News") {
//            if (tag == "Kasbokar") {
//                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
//            } else {
//                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
//            }
//        } else if (currentDisplayingFragment.getTag() == "Pishkhan") {
//            if (tag == "Kasbokar" || tag == "News") {
//                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
//            } else {
//                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
//            }
//        } else if (currentDisplayingFragment.getTag() == "Favorites") {
//            if (tag == "Pishkhan" || tag == "News" || tag == "Kasbokar") {
//                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
//            } else {
//                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
//            }
//        } else if (currentDisplayingFragment.getTag() == "Menu") {
//            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int in = item.getItemId();

        switch (in) {
            case R.id.action_search:
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {

                    drawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void fragmentChanger() {
        menuBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(MainActivity.this, new MenuFragment(), "Menu", "Menu");

                menuIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                favoritesIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                pishkhanIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                newsIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                kasbokarIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));

                menuTXT.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                favoritesTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                pishkhanTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                pishkhanTXT2.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                pishkhanTXT1.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                newsTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                kasbokarTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
            }
        });

        favoritesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(MainActivity.this, new Fav_Host(), "Favorites", "Favorites");
//                setFragment(MainActivity.this, new Car(), "job", "Favorites");
//
                menuIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                favoritesIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                pishkhanIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                newsIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                kasbokarIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));

                menuTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                favoritesTXT.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                pishkhanTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                pishkhanTXT2.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                pishkhanTXT1.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                newsTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                kasbokarTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
            }
        });

        pishkhanBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(MainActivity.this, new Home_Host(), "Pishkhan", "Pishkhan");

                menuIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                favoritesIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                pishkhanIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                newsIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                kasbokarIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));

                menuTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                favoritesTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                pishkhanTXT.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                pishkhanTXT2.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                pishkhanTXT1.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                newsTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                kasbokarTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
            }
        });

        newsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(MainActivity.this, new NewsFragment(), "News", "News");

                menuIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                favoritesIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                pishkhanIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                newsIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                kasbokarIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));

                menuTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                favoritesTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                pishkhanTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                pishkhanTXT2.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                pishkhanTXT1.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                newsTXT.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                kasbokarTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
            }
        });

        kasbokarBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(MainActivity.this, new KasbokarFragment(), "Kasbokar", "Kasbokar");

                menuIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                favoritesIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                pishkhanIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                newsIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                kasbokarIMG.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));

                menuTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                favoritesTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                pishkhanTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                pishkhanTXT2.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                pishkhanTXT1.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                newsTXT.setTextColor(ContextCompat.getColor(context, R.color.colorToolbarDefault));
                kasbokarTXT.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        });
    }

    private void fireBaseStuff() {

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to global topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

//                    Toast.makeText(MainActivity.this, "Push notification: " + message, Toast.LENGTH_LONG).show();

                    Log.v("firebase", message);
//                    txtMessage.setText(message);
                }
            }
        };
    }


    ///////////////////////////////////////
    ////         VOLLEY STUFF          ////
    ///////////////////////////////////////

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        try {
            Log.v("token", FirebaseInstanceId.getInstance().getToken());

        } catch (Exception e) {
        }

        Log.e("firebase", "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            Log.v("firebase", "Firebase Reg Id: " + regId);
        else {
//            Log.v("Firebase Reg Id: ", regId);
            Log.v("firebase", "Firebase Reg Id is not received yet!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        /// This is important for Interfaces.
//        Get_Volley_Call_Back.binddata(this);


//        checkAndRequestPermissions();

//        hideUnhideMenuItems();

        /////////////////////////////////
        ///     firebase stuff
        /////////////////////////////////
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(Config.REGISTRATION_COMPLETE));
//
//        // register new push message receiver
//        // by doing this, the activity will be notified each time a new message arrives
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(Config.PUSH_NOTIFICATION));
//
//        // clear the notification area when the app is opened
//        NotificationUtils.clearNotifications(getApplicationContext());
//        showcart();

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    public void on_volley_response(String response, int id) {
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {
    }

    private void showProvinceOrCityPicker(List<ProvicesAndCities> provicesAndCities) {
//        Dialog provinceDialog = new Dialog(MainActivity.this);
//        provinceDialog.setContentView(R.layout.dialog_province);
//        provinceDialog.setCancelable(false);
//        provinceDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

//        RecyclerView rv = (RecyclerView) provinceDialog.findViewById(R.id.provinceDialogRvProvince);
//        ProvinceSelectionAdapter adapter = new ProvinceSelectionAdapter(provicesAndCities, MainActivity.this);
//        LinearLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 1, GridLayoutManager.VERTICAL, false);
//        rv.setLayoutManager(layoutManager);
//        rv.setAdapter(adapter);
//        adapter.notifyDataSetChanged();

//        provinceDialog.show();
    }

    @Override
    public void onAdFound(VipAd vipAd, String callerActivity) {

        if (callerActivity.equals("mainActivity")) {
            loadVipAd(vipAd);
        }
    }

    private void loadVipAd(VipAd vipAd) {

        VipAdDialog vipAdDialog = new VipAdDialog();
        vipAdDialog.setContext(context);
        vipAdDialog.setData(vipAd);
        vipAdDialog.show(getSupportFragmentManager(), "VipAd");
        this.vipAd = null;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        ///////////////////

        if (canExitNow) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            System.exit(1);
        } else {
            canExitNow = true;

            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.dl),
                    "آیا مایل به خروج از برنامه هستید؟", Snackbar.LENGTH_SHORT);
            mySnackbar.setAction("خروج", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    System.exit(1);
                }
            });
            mySnackbar.setActionTextColor(Color.YELLOW);
            mySnackbar.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    canExitNow = false;
                }
            }, 1200);
        }
    }

    private boolean checkAndRequestPermissions() {

        int sms = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int locSvc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int task = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_TASKS);
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int read_external_storeg = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

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

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new
                    String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onNewFirebaseNotification(String body, String status, String adId) {

        try {
            NotificationDialog notificationDialog = new NotificationDialog();
            notificationDialog.setData(body, status, adId);
            notificationDialog.show(getFragmentManager(), "NotificationDialog");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLoginLogoutChangeListener() {
        recreate();
    }


}
