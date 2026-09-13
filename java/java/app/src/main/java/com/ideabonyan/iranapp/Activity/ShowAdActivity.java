package com.ideabonyan.iranapp.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager2.widget.ViewPager2;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import com.ideabonyan.iranapp.Utils.ImageSliderAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ideabonyan.iranapp.Fragment.Dialogs.ContactFormDialog;
import com.ideabonyan.iranapp.Fragment.Dialogs.NotificationDialog;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data2;
import com.ideabonyan.iranapp.Interface.NewNotificationCame;
import com.ideabonyan.iranapp.Interface.RemoveAd;
import com.ideabonyan.iranapp.Interface.UpdateAd;
import com.ideabonyan.iranapp.Models.AdsToBeListed;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.CustomTypefaceSpan;
import com.ideabonyan.iranapp.Utils.GPSTracker;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back2;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;
import com.ideabonyan.iranapp.Utils.VolleySingleton;
import com.ideabonyan.iranapp.service.MyFirebaseMessagingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.delight.android.location.SimpleLocation;

public class ShowAdActivity extends AppCompatActivity implements UpdateAd, OnMapReadyCallback,
        Get_Insert_Edit_Data, Get_Insert_Edit_Data2, NewNotificationCame {

    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    final static int REQUEST_LOCATION = 199;
    static RemoveAd removeAd;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout, appBarLayout2;
    AdsToBeListed ad;
    View headerDevider;
    ViewPager2 sliderLayout;
    TabLayout pagerIndicator;
    RelativeLayout imageArea;
    NestedScrollView nestedScrollView;
    TextView discountAmountTXT, titleTXT, descriptionTXT, headerTXT;
    LinearLayout shareBTN, favoriteBTN, downVoteBTN, upVoteBTN;
    RelativeLayout discountBTN;
    LinearLayout contactInfoBTN;
    ImageButton backBTN, menuBTN;
    ImageButton backBTN2, menuBTN2;
    Context context;
    SupportMapFragment supportMapFragment;
    ImageView mapOverlay;
    ProgressBar progressBar;
    TextView retryBTN;
    LinearLayout userPrefDatalayout;
    ImageView favImage, downVoteIMG, upVoteIMG;
    TextView downVoteTXT, upVoteTXT;
    CardView mapCard;
    TextView pendingIndicator;
    RatingBar ratingBar;
    LinearLayout ratingBarArea;
    TextView ownerNameTXT;
    CardView ownerArea, card_discount;
    LinearLayout lin_use_discount;
    ProgressBar progressbar_use_discount;
    TextView txt_use_discount;
    boolean hasImage;
    User user;
    String likeStatus, favStatus, upVoteCount, downVoteCount;
    GPSTracker gpsTracker;
    int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    GoogleMap gmap;
    private SimpleLocation location;
    private Toolbar toolbar;
    static final int REQUEST_FULLSCREEN_VIDEO = 301;
    CardView videoCard;
    PlayerView videoPlayerView;
    ExoPlayer videoPlayer;
    long videoPosition = 0;
    boolean videoPlayWhenReady = false;

    static public void bindData(RemoveAd rremoveAd) {
        removeAd = rremoveAd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ad);

        initializer();
        onClicks();
        smallStuff();
        fillInfo();
        doAppBarLayoutStuff();
        if (savedInstanceState == null) recordView();

    }

    /**
     * Counts this opening of the ad for its performance page. Fire-and-forget: it goes
     * around Get_Volley_Call_Back so a failed count never shows the user an error. The
     * server ignores the owner's own views, which is why the token is sent when there is one.
     */
    private void recordView() {
        String token = StaticData.optionalTokenQuery(this);
        String url = StaticData.DOMAIN_WITH_API + "/ads/" + ad.getId() + "/views"
                + (token.isEmpty() ? "" : "?" + token.substring(1));
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
        }, error -> {
        });
        VolleySingleton.GetInstance(this).AddToRequestQueue(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkAndRequestPermissions(false)) {
            if (location.hasLocationEnabled()) {
                // ask the user to enable location access
//            SimpleLocation.openSettings(this);
                location.beginUpdates();

            }
        }
        progressBar.setVisibility(View.VISIBLE);
        userPrefDatalayout.setVisibility(View.INVISIBLE);
        getPrefData();
    }

    private boolean checkAndRequestPermissions(boolean showAlartDialog) {

        int ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (ACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            if (showAlartDialog) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new
                        String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
            return false;

        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                turnonlocation();
            } else {
                ShowToast.failure("دسترسی داده نشد", ShowAdActivity.this);
            }
        }

    }

    private void doAppBarLayoutStuff() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("صفحه اول");
                    toolbar.setBackgroundColor((Color.parseColor("#ffffff")));
                    backBTN.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    menuBTN.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    headerTXT.setTextColor(getResources().getColor(R.color.colorPrimary));
                    headerTXT.setText(ad.getTitle());
                    headerDevider.setVisibility(View.VISIBLE);

                    isShow = true;
                } else if (isShow) {
                    toolbar.setBackgroundColor(Color.parseColor("#00000000"));
                    backBTN.setColorFilter(Color.parseColor("#ffffff"));
                    menuBTN.setColorFilter(Color.parseColor("#ffffff"));
                    headerTXT.setTextColor(Color.parseColor("#ffffff"));
                    headerTXT.setText("");
                    headerDevider.setVisibility(View.GONE);


                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        location.endUpdates();
    }

    // The player only exists while the screen is visible, so a hidden ad never holds a decoder.
    @Override
    protected void onStart() {
        super.onStart();
        initializeVideoPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseVideoPlayer();
    }

    private void initializer() {
        location = new SimpleLocation(ShowAdActivity.this);

        MyFirebaseMessagingService.bindData(ShowAdActivity.this);
        card_discount = findViewById(R.id.card_discount);
        txt_use_discount = findViewById(R.id.txt_use_discount);
        progressbar_use_discount = findViewById(R.id.progressbar_use_discount);
        lin_use_discount = findViewById(R.id.lin_use_discount);
        ad = (AdsToBeListed) getIntent().getSerializableExtra("ad");
        ratingBarArea = (LinearLayout) findViewById(R.id.showAdRatingBarArea);
        ratingBar = (RatingBar) findViewById(R.id.showAdRatingBar);
        ownerNameTXT = (TextView) findViewById(R.id.showAdOwnerNameTXT);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.showAdCollapsingToolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.showAdAppBar);
//        appBarLayout2 = (AppBarLayout) findViewById(R.id.showAdAppBar2);
        headerDevider = findViewById(R.id.showAdHeaderDevider);

        sliderLayout = (ViewPager2) findViewById(R.id.showAdSliderLayout);
        pagerIndicator = (TabLayout) findViewById(R.id.showAdCustomIndicator);
        nestedScrollView = (NestedScrollView) findViewById(R.id.showAdNestedScroll);
        discountAmountTXT = (TextView) findViewById(R.id.showAdDiscountText);
        titleTXT = (TextView) findViewById(R.id.showAdTitle);
        descriptionTXT = (TextView) findViewById(R.id.showAdDescription);
        headerTXT = (TextView) findViewById(R.id.headerTXT);
        shareBTN = (LinearLayout) findViewById(R.id.showAdShareBTN);
        favoriteBTN = (LinearLayout) findViewById(R.id.showAdFavoriteBTN);
        downVoteBTN = (LinearLayout) findViewById(R.id.showAdDownVoteBTN);
        upVoteBTN = (LinearLayout) findViewById(R.id.showAdUpVoteBTN);
        discountBTN = (RelativeLayout) findViewById(R.id.showAdDiscountLayout);
        contactInfoBTN = (LinearLayout) findViewById(R.id.showAdContactUsBTN);
        backBTN = (ImageButton) findViewById(R.id.newAdBackButton);
//        backBTN2 = (ImageButton) findViewById(R.id.newAdBackButton2);
        menuBTN = (ImageButton) findViewById(R.id.showAdMenuBTN);
//        menuBTN2 = (ImageButton) findViewById(R.id.showAdMenuBTN2);
        context = ShowAdActivity.this;
        this.setTitle("");
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.showAdMap);
        mapOverlay = (ImageView) findViewById(R.id.showAdMapOverlay);
        progressBar = (ProgressBar) findViewById(R.id.showAdProgressBar);
        userPrefDatalayout = (LinearLayout) findViewById(R.id.showAdUserPrefLayout);
        retryBTN = (TextView) findViewById(R.id.showAdUserPrefRetryBTN);
        favImage = (ImageView) findViewById(R.id.showAdFavoriteIMG);
        downVoteIMG = (ImageView) findViewById(R.id.showAdDownVoteIMG);
        upVoteIMG = (ImageView) findViewById(R.id.showAdUpVoteIMG);
        downVoteTXT = (TextView) findViewById(R.id.showAdDownVoteTXT);
        upVoteTXT = (TextView) findViewById(R.id.showAdUpVoteTXT);
        mapCard = (CardView) findViewById(R.id.showAdMapCard);
        pendingIndicator = (TextView) findViewById(R.id.showAdPendingMessage);
        ownerArea = (CardView) findViewById(R.id.showAdAdOwnerArea);
        imageArea = (RelativeLayout) findViewById(R.id.showAdImageArea);
        videoCard = findViewById(R.id.showAdVideoCard);
        videoPlayerView = findViewById(R.id.showAdVideoPlayer);
    }

    private void smallStuff() {
        supportMapFragment.getMapAsync(this);
        NewAdActivity.bindData(this);
        user = UserHelper.LoadUserInfo(ShowAdActivity.this);
    }

    private void onClicks() {
        lin_use_discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                User user = UserHelper.LoadUserInfo(ShowAdActivity.this);
//                if (user.isLoggedIn() && user.isVerrified()) {
//                    if (txt_use_discount.getText().toString().equals("برای استفاده از تخفیف این مرکز کلیک نمایید")) {
//                        Animation logoMoveAnimation = AnimationUtils.loadAnimation(ShowAdActivity.this, R.anim.zoom);
//                        final Animation fadin = AnimationUtils.loadAnimation(ShowAdActivity.this, R.anim.fadein);
//                        txt_use_discount.startAnimation(logoMoveAnimation);
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                txt_use_discount.setText("اگر در این مکان حضور دارید کلیک کنید");
//                                txt_use_discount.startAnimation(fadin);
//                            }
//                        }, 300);
//
//
//                    } else {
//                        if (checkAndRequestPermissions(false)) {
//                            turnonlocation();
//
//                        } else {
//                            AlertDialog.Builder b = new AlertDialog.Builder(context);
//
//                            b.setMessage("برای استفاده از تخفیف ، باید به مکان یاب اجازه دسترسی بدهید .")
//                                    .setPositiveButton("باشه", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            checkAndRequestPermissions(true);
//                                        }
//                                    });
//                            AlertDialog a = b.create();
//
//                            a.show();
//
//                            Button bq = a.getButton(DialogInterface.BUTTON_NEGATIVE);
//                            Button bq1 = a.getButton(DialogInterface.BUTTON_POSITIVE);
//                            bq1.setTextColor(context.getResources().getColor(R.color.colorPrimary));
//                            bq1.setTextSize(16);
//                        }
//                    }
//                } else {
//                    ShowToast.failure("برای استفاده از تخفیف این مرکز ، ابتدا در برنامه ثبت نام کنید یا وارد حساب کاربری خود شوید", ShowAdActivity.this);
//                }



                AlertDialog.Builder builder;
//
                builder = new AlertDialog.Builder(ShowAdActivity.this);

                builder.setTitle("")
                        .setMessage("برای دریافت تخفیف از این مرکز با ما تماس بگیرید.")
                        .setPositiveButton("تماس", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + "۰۹۱۳۱۰۹۴۴۰۶"));
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("رد کردن", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //////////////////////////////////////////

                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        retryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPrefData();
                retryBTN.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        backBTN2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        menuBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ShowAdActivity.this, menuBTN);
                popupMenu.getMenuInflater().inflate(R.menu.showadoptions, popupMenu.getMenu());


                //removing unwanted menu entries
                Menu menu = popupMenu.getMenu();

                if (ad.getMax_number_of_update().equals(ad.getUpdates_count()))
                    menu.findItem(R.id.showAdUpdateAd).setVisible(false);
                else menu.findItem(R.id.showAdDENIED).setVisible(false);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.showAdStats:
                                Intent statsIntent = new Intent(ShowAdActivity.this, AdStatsActivity.class);
                                statsIntent.putExtra(AdStatsActivity.EXTRA_AD_ID, ad.getId());
                                statsIntent.putExtra(AdStatsActivity.EXTRA_AD_TITLE, ad.getTitle());
                                startActivity(statsIntent);
                                break;
                            case R.id.showAdUpdateAd:
                                ////////////////////////////////
                                Intent intent = new Intent(ShowAdActivity.this, NewAdActivity.class);
                                NewAdActivity.isItNewAd = false;
                                NewAdActivity.ad = ad;
                                finish();
                                startActivity(intent);
                                ////////////////////////////////
                                break;
                            case R.id.showAdRemoveAd:

                                AlertDialog.Builder builder;

                                builder = new AlertDialog.Builder(ShowAdActivity.this);

                                builder.setTitle("")
                                        .setMessage("آیا از حذف آگهی اطمینان دارید؟")
                                        .setPositiveButton("آری", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {


                                                removeAd();
                                                finish();
                                                removeAd.onAdRemoved();

                                            }
                                        })
                                        .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                break;
                        }

                        return true;
                    }
                });

//                Menu menu = popupMenu.getMenu();

                int menuCount = menu.size();
                for (int i = 0; i < menuCount; i++) {

                    MenuItem item = menu.getItem(i);
                    String settingsItemTitle = menu.getItem(i).getTitle().toString();
                    SpannableString s = new SpannableString(settingsItemTitle);

                    s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);

                    item.setTitle(s);
                }

                changeDrawerMenuFont(menu);

                popupMenu.show();
            }
        });
//        menuBTN2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu popupMenu = new PopupMenu(ShowAdActivity.this, menuBTN2);
//                popupMenu.getMenuInflater().inflate(R.menu.showadoptions, popupMenu.getMenu());
//
//
//                //removing unwanted menu entries
//                Menu menu = popupMenu.getMenu();
//                if (ad.getMax_number_of_update().equals("0")) menu.findItem(R.id.showAdUpdateAd).setVisible(false);
//                else menu.findItem(R.id.showAdDENIED).setVisible(false);
//
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//
//                        switch (item.getItemId()) {
//                            case R.id.showAdUpdateAd:
//                                ////////////////////////////////
//                                Intent intent = new Intent(ShowAdActivity.this, NewAdActivity.class);
//                                NewAdActivity.isItNewAd = false;
//                                NewAdActivity.ad = ad;
//                                startActivity(intent);
//                                ////////////////////////////////
//                                break;
//                            case R.id.showAdRemoveAd:
//
//                                AlertDialog.Builder builder;
//
//                                builder = new AlertDialog.Builder(ShowAdActivity.this);
//
//                                builder.setTitle("")
//                                        .setMessage("آیا از حذف آگهی اطمینان دارید؟")
//                                        .setPositiveButton("آری", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//
//
//                                                removeAd();
//                                                finish();
//                                                removeAd.onAdRemoved();
//
//                                            }
//                                        })
//                                        .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                            }
//                                        });
//                                AlertDialog alertDialog = builder.create();
//                                alertDialog.show();
//                                break;
//                        }
//
//                        return true;
//                    }
//                });
//
////                Menu menu = popupMenu.getMenu();
//
//                int menuCount = menu.size();
//                for (int i = 0; i < menuCount; i++){
//
//                    MenuItem item = menu.getItem(i);
//                    String settingsItemTitle = menu.getItem(i).getTitle().toString();
//                    SpannableString s = new SpannableString(settingsItemTitle);
//
//                    s.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, s.length(), 0);
//
//                    item.setTitle(s);
//                }
//
//                changeDrawerMenuFont(menu);
//
//                popupMenu.show();
//            }
//        });

        mapOverlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        nestedScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        nestedScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        nestedScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });

        contactInfoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContactFormDialog contactFormDialog = new ContactFormDialog();
                contactFormDialog.setContext(ShowAdActivity.this);
                contactFormDialog.setData(ad);
                contactFormDialog.show(getFragmentManager(), "ContactInfoFragment");
            }
        });


        /////////////////////////////
        //      FAVORITE BTN
        /////////////////////////////
        favoriteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = UserHelper.LoadUserInfo(ShowAdActivity.this);

                if (user.isLoggedIn() && user.isVerrified()) {

                    String url = StaticData.DOMAIN_WITH_API + "/ads/" + ad.getId() + "/fav" + "?token=" + new UserSessionManager(ShowAdActivity.this).getLoginToken();

                    Map<String, String> params = new HashMap<String, String>();
                    Get_Volley_Call_Back.binddata(ShowAdActivity.this);
                    Get_Volley_Call_Back.Call_Volley(context, params, url, Request.Method.POST, 2);

                    if (favStatus.equals("0")) {
                        favStatus = "1";

                        favImage.setImageResource(R.drawable.ic_favorite_black_24dp);
                        favImage.setColorFilter(Color.parseColor("#E91E63"));
                    } else {
                        favStatus = "0";

                        favImage.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        favImage.setColorFilter(Color.parseColor("#E91E63"));
                    }
                } else if (user.isLoggedIn() && !user.isVerrified()) {

                    Intent i = new Intent(ShowAdActivity.this, ConfirmationActivity.class);
//                    i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(i);
                } else {

                    Intent i = new Intent(ShowAdActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
        });

        //////////////////////////
        ///     LIKE AND DISLIKE
        //////////////////////////
        upVoteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = UserHelper.LoadUserInfo(ShowAdActivity.this);

                if (user.isLoggedIn() && user.isVerrified()) {

                    if (!likeStatus.equals("like")) {
                        String url = StaticData.DOMAIN_WITH_API + "/ads/" + ad.getId() + "/likes" + "?like_type=like" + "&token=" + new UserSessionManager(ShowAdActivity.this).getLoginToken();

                        Map<String, String> params = new HashMap<String, String>();
                        Get_Volley_Call_Back2.binddata(ShowAdActivity.this);

                        Get_Volley_Call_Back2.Call_Volley(context, params, url, Request.Method.POST, 99);

                        upVoteIMG.setColorFilter(Color.parseColor("#4CAF50"));
                        upVoteTXT.setTextColor(Color.parseColor("#4CAF50"));
                        upVoteCount = String.valueOf(Integer.parseInt(upVoteCount) + 1);
                        upVoteTXT.setText(upVoteCount);

                        if (likeStatus.equals("dislike")) {
                            downVoteIMG.setColorFilter(Color.parseColor("#999999"));
                            downVoteTXT.setTextColor(Color.parseColor("#999999"));
                            downVoteCount = (Integer.parseInt(downVoteTXT.getText().toString()) - 1) + "";
                            downVoteTXT.setText(downVoteCount);
                        }
                        likeStatus = "like";
                    } else {
                        String url = StaticData.DOMAIN_WITH_API + "/ads/" + ad.getId() + "/like/off?like_type=" + "like" + "&token=" + new UserSessionManager(ShowAdActivity.this).getLoginToken();

                        Map<String, String> params = new HashMap<String, String>();
                        Get_Volley_Call_Back2.Call_Volley(context, params, url, Request.Method.GET, 99);

                        upVoteIMG.setColorFilter(Color.parseColor("#999999"));
                        upVoteTXT.setTextColor(Color.parseColor("#999999"));
                        upVoteCount = (Integer.parseInt(upVoteTXT.getText().toString()) - 1) + "";
                        upVoteTXT.setText(upVoteCount);

                        likeStatus = "0";
                    }
                } else if (user.isLoggedIn() && !user.isVerrified()) {

                    Intent i = new Intent(ShowAdActivity.this, ConfirmationActivity.class);
//                    i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(i);
                } else {

                    Intent i = new Intent(ShowAdActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
        });
        downVoteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = UserHelper.LoadUserInfo(ShowAdActivity.this);

                if (user.isLoggedIn() && user.isVerrified()) {
                    if (!likeStatus.equals("dislike")) {
                        String url = StaticData.DOMAIN_WITH_API + "/ads/" + ad.getId() + "/likes" + "?like_type=dislike" + "&token=" + new UserSessionManager(ShowAdActivity.this).getLoginToken();

                        Map<String, String> params = new HashMap<String, String>();
                        Get_Volley_Call_Back2.Call_Volley(context, params, url, Request.Method.POST, 99);

                        downVoteIMG.setColorFilter(Color.parseColor("#f44336"));
                        downVoteTXT.setTextColor(Color.parseColor("#f44336"));
                        downVoteCount = (Integer.parseInt(downVoteTXT.getText().toString()) + 1) + "";
                        downVoteTXT.setText(downVoteCount);

                        if (likeStatus.equals("like")) {
                            upVoteIMG.setColorFilter(Color.parseColor("#999999"));
                            upVoteTXT.setTextColor(Color.parseColor("#999999"));
                            upVoteCount = (Integer.parseInt(upVoteTXT.getText().toString()) - 1) + "";
                            upVoteTXT.setText(upVoteCount);
                        }
                        likeStatus = "dislike";
                    } else {
                        String url = StaticData.DOMAIN_WITH_API + "/ads/" + ad.getId() + "/like/off?like_type=" + "dislike" + "&token=" + new UserSessionManager(ShowAdActivity.this).getLoginToken();

                        Map<String, String> params = new HashMap<String, String>();
                        Get_Volley_Call_Back2.Call_Volley(context, params, url, Request.Method.GET, 99);

                        downVoteIMG.setColorFilter(Color.parseColor("#999999"));
                        downVoteTXT.setTextColor(Color.parseColor("#999999"));
                        downVoteCount = (Integer.parseInt(downVoteTXT.getText().toString()) - 1) + "";
                        downVoteTXT.setText(downVoteCount);

                        likeStatus = "0";
                    }
                } else if (user.isLoggedIn() && !user.isVerrified()) {

                    Intent i = new Intent(ShowAdActivity.this, ConfirmationActivity.class);
//                    i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(i);
                } else {

                    Intent i = new Intent(ShowAdActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }
        });

        /////////////////////////////
        //      SHARE BUTTON
        shareBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareText = "ایران آپ : " + "\n"
                        + ad.getTitle() + "\n";
                if (!(ad.getTel1() == null || ad.getTel1().equals(null) || ad.getTel1().equals("null"))) {
                    shareText += ad.getTel1() + "\n";

                }
                if (!(ad.getTel2() == null || ad.getTel2().equals(null) || ad.getTel2().equals("null"))) {
                    shareText += ad.getTel2() + "\n";

                }
                shareText += ad.getAddress() + "\n"
                        + "با ایران آپ در تخفیف ها به روز باشید" + "\n\n"
                        + "لینک دانلود : " + StaticData.download_link;
//                        StaticData.LINK_IN_BAZAR;

//                String shareText = ad.getTitle() +  "\n\n"
//                        + ad.getNotes() +  "\n\n\n"
//                        + "با ایران آپ در تخفیف ها به روز باشید" + "\n\n" + StaticData.LINK_IN_BAZAR;

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            }
        });

        //////////////////////////////////
        //      DISCOUNT LAYOUT BUTTON
//        discountBTN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder;
//
//                builder = new AlertDialog.Builder(ShowAdActivity.this);
//
//                builder.setTitle("")
//                        .setMessage("برای دریافت تخفیف از این مرکز با ما تماس بگیرید.")
//                        .setPositiveButton("تماس", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent intent = new Intent(Intent.ACTION_DIAL);
//                                intent.setData(Uri.parse("tel:" + "۰۹۱۳۱۰۹۴۴۰۶"));
//                                startActivity(intent);
//
//                            }
//                        })
//                        .setNegativeButton("رد کردن", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                //////////////////////////////////////////
//
//                            }
//                        });
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//            }
//        });
    }

    private void fillInfo() {

        try {

            User user = UserHelper.LoadUserInfo(ShowAdActivity.this);
            if (user.isLoggedIn() && user.isVerrified()) {
                if (user.getuser_id().equals(ad.getUser_id())) {
                    menuBTN.setVisibility(View.VISIBLE);
//            menuBTN2.setVisibility(View.VISIBLE);
                }
            }


            titleTXT.setText(ad.getTitle());

            if (ad.getNotes() != null && !ad.getNotes().equals("null") && !ad.getNotes().equals("") && !ad.getNotes().equals(null)) {
                descriptionTXT.setText(ad.getNotes());
            } else descriptionTXT.setVisibility(View.GONE);

            if (!ad.getDiscount().equals(null) && !ad.getDiscount().equals("0") && !ad.getDiscount().equals("null")) {
//                discountAmountTXT.setText("تا " + ad.getDiscount() + "%");
                discountAmountTXT.setText(ad.getDiscount() + "%");
//                card_discount.setVisibility(View.VISIBLE);
            } else {
                discountBTN.setVisibility(View.GONE);
                card_discount.setVisibility(View.GONE);
            }

            if (ad.getStatus().equals("pending")) pendingIndicator.setVisibility(View.VISIBLE);

            try {
                if (Integer.parseInt(ad.getNum_of_stars()) == 0)
                    ratingBarArea.setVisibility(View.GONE);
                else ratingBar.setRating(Integer.parseInt(ad.getNum_of_stars()));
            } catch (Exception e) {
                ratingBarArea.setVisibility(View.GONE);
            }

            if (ad.getAds_owner_name() != null && !ad.getAds_owner_name().equals("null") && !ad.getAds_owner_name().equals("") && !ad.getAds_owner_name().equals(null))
                ownerNameTXT.setText(ad.getAds_owner_name());
            else ownerArea.setVisibility(View.GONE);

            runImageSlider();
            setupVideo();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private void runImageSlider() {

        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter();

        if (ad.getPhotos().size() == 0) {
            hasImage = false;
            sliderAdapter.showPlaceholderOnly();
        } else {
            hasImage = true;

            java.util.List<String> urls = new java.util.ArrayList<>();
            for (int i = 0; i < ad.getPhotos().size(); i++) {
                urls.add(ad.getPhotos().get(i).getName());
            }
            sliderAdapter.setImageUrls(urls);
        }

        ImageSliderAdapter.attach(sliderLayout, pagerIndicator, sliderAdapter);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void setupVideo() {
        if (!ad.hasVideo()) {
            videoCard.setVisibility(View.GONE);
            return;
        }
        videoCard.setVisibility(View.VISIBLE);
        videoPlayerView.setFullscreenButtonClickListener(isFullScreen -> openFullscreenVideo());
    }

    private void initializeVideoPlayer() {
        if (ad == null || !ad.hasVideo() || videoPlayer != null) return;

        videoPlayer = new ExoPlayer.Builder(this).build();
        videoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                ShowToast.failure("پخش ویدیو امکان پذیر نیست", ShowAdActivity.this);
            }
        });
        videoPlayerView.setPlayer(videoPlayer);
        videoPlayer.setMediaItem(MediaItem.fromUri(ad.getVideo_url()), videoPosition);
        videoPlayer.setPlayWhenReady(videoPlayWhenReady);
        // Left idle until the viewer presses play (the controller prepares an idle player
        // itself), so just opening an ad downloads none of the video.
        if (videoPlayWhenReady || videoPosition > 0) videoPlayer.prepare();
    }

    private void releaseVideoPlayer() {
        if (videoPlayer == null) return;

        videoPosition = videoPlayer.getCurrentPosition();
        videoPlayWhenReady = videoPlayer.getPlayWhenReady();
        videoPlayerView.setPlayer(null);
        videoPlayer.release();
        videoPlayer = null;
    }

    private void openFullscreenVideo() {
        long position = videoPosition;
        if (videoPlayer != null) {
            position = videoPlayer.getCurrentPosition();
            videoPlayer.pause();
        }
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(VideoPlayerActivity.EXTRA_URL, ad.getVideo_url());
        intent.putExtra(VideoPlayerActivity.EXTRA_POSITION, position);
        startActivityForResult(intent, REQUEST_FULLSCREEN_VIDEO);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gmap = googleMap;
        if (ad.getLatitude().equals("null") || ad.getLatitude().equals("") || ad.getLatitude() == null) {
            mapCard.setVisibility(View.GONE);
        } else {

            LatLng ll = new LatLng(Double.parseDouble(ad.getLatitude()), Double.parseDouble(ad.getLongitude()));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            googleMap.moveCamera(update);


            // Creating a Marker
            MarkerOptions markerOptions = new MarkerOptions();

            //setting the position or the marker
            markerOptions.position(ll);

            //setting the title for the marker
            markerOptions.title(ad.getAddress());

            //clearing the previous marker
            googleMap.clear();

            //animating to the touched position
//        googleMap.animateCamera(CameraUpdateFactory.newLatLng(ll));

            //adding new marker
            googleMap.addMarker(markerOptions);
        }
    }

    private void getPrefData() {
//        Log.v("getPrefData","getPrefData");
        User user = UserHelper.LoadUserInfo(ShowAdActivity.this);

        if (user.isLoggedIn() && user.isVerrified()) {
            String url = StaticData.DOMAIN_WITH_API + "/users/ads/" + ad.getId() + "?token=" + new UserSessionManager(ShowAdActivity.this).getLoginToken();
            Log.v("url", url);
            Map<String, String> params = new HashMap<String, String>();
            Get_Volley_Call_Back.binddata(this);
            Get_Volley_Call_Back.Call_Volley(context, params, url, Request.Method.GET, 1);
        } else {

            String url = StaticData.DOMAIN_WITH_API + "/ads/" + ad.getId() + "/likes-and-dislikes";
//            Log.v("url",url);
            Map<String, String> params = new HashMap<String, String>();
            Get_Volley_Call_Back.binddata(this);
            Get_Volley_Call_Back.Call_Volley(context, params, url, Request.Method.GET, 2);

        }
    }

    @Override
    public void on_volley_response(String response, int id) {
//        Log.v("response",id+" "+response);

        if (id == 1) {
            try {

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("status")) {
                    if (jsonObject.getString("status").equals("200")) {
                        likeStatus = jsonObject.getString("like");
                        favStatus = jsonObject.getString("fav");
                        upVoteCount = jsonObject.getString("likes_count");
                        downVoteCount = jsonObject.getString("dislikes_count");
                        fillUserPrefsData();
                    } else if (jsonObject.getString("status").equals("401")) {
                        if (jsonObject.getString("error").equals("token_expired")) {
                            UserHelper.RemoveUserInfo(ShowAdActivity.this);
                            Intent intent = new Intent(ShowAdActivity.this, LoginActivity.class);
                            ShowToast.failure("لطفا دوباره وارد حساب خود شوید", ShowAdActivity.this);
                            startActivity(intent);
                        }
                    }
                } else {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (id == 2) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                upVoteTXT.setText(jsonObject.getString("likes"));
                downVoteTXT.setText(jsonObject.getString("dislike"));

                progressBar.setVisibility(View.GONE);
                userPrefDatalayout.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (id == 100022) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if (status.equals("200")) {
                    ShowToast.success("درخواست شما با موفقیت به مسئول مرکز ارسال شد", ShowAdActivity.this);
                } else if (status.equals("400")) {
                    ShowToast.failure("فاصله شما تا مرکز زیاد می باشد لطفا در آنجا حضور یافته و دوباره درخواست نمایید.", ShowAdActivity.this);

                } else if (status.equals("405")) {
                    ShowToast.failure("آگهی فاقد نقاط جغرافیایی می باشد.", ShowAdActivity.this);
                } else if (status.equals("406")) {
                    ShowToast.failure("آگهی مورد نظر یافت نشد.", ShowAdActivity.this);
                }
                progressbar_use_discount.setVisibility(View.GONE);
                txt_use_discount.setVisibility(View.VISIBLE);
                txt_use_discount.setText("برای استفاده از تخفیف این مرکز کلیک نمایید");
                final Animation fadin = AnimationUtils.loadAnimation(ShowAdActivity.this, R.anim.fadein);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txt_use_discount.startAnimation(fadin);
                        progressbar_use_discount.setVisibility(View.GONE);

                    }
                }, 300);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void fillUserPrefsData() {
        progressBar.setVisibility(View.GONE);
        userPrefDatalayout.setVisibility(View.VISIBLE);

        upVoteTXT.setText(upVoteCount);
        downVoteTXT.setText(downVoteCount);

        if (likeStatus.equals("like")) {
            upVoteIMG.setColorFilter(Color.parseColor("#4CAF50"));
            upVoteTXT.setTextColor(Color.parseColor("#4CAF50"));
        } else if (likeStatus.equals("dislike")) {
            downVoteIMG.setColorFilter(Color.parseColor("#f44336"));
            downVoteTXT.setTextColor(Color.parseColor("#f44336"));
        }

        if (favStatus.equals("1")) {
            favImage.setImageResource(R.drawable.ic_favorite_black_24dp);
            favImage.setColorFilter(Color.parseColor("#E91E63"));
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

        if (id == 1 || id == 2) {
            progressBar.setVisibility(View.GONE);
            retryBTN.setVisibility(View.VISIBLE);
        } else if (id == 100022) {
            progressbar_use_discount.setVisibility(View.GONE);
            txt_use_discount.setVisibility(View.VISIBLE);
            txt_use_discount.setText("برای استفاده از تخفیف این مرکز کلیک نمایید");
            ShowToast.failure("آگهی مورد نظر یافت نشد.", ShowAdActivity.this);
            final Animation fadin = AnimationUtils.loadAnimation(ShowAdActivity.this, R.anim.fadein);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    txt_use_discount.startAnimation(fadin);
                }
            }, 300);


        }
    }

    private void changeDrawerMenuFont(Menu mm) {
        Menu m = mm;
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
        ////////////////////////////////////////
        ////        CHANGING TEXT ALIGNMENT
        ////////////////////////////////////////
//        mNewTitle.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, mNewTitle.length(), 0);
        mi.setTitle(mNewTitle);
    }

    private void removeAd() {
        String url = StaticData.DOMAIN_WITH_API + "/ads/" + ad.getId() + "/delete" + "?token=" + new UserSessionManager(ShowAdActivity.this).getLoginToken();

        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(context, params, url, Request.Method.GET, 2);
    }

    @Override
    public void onAdUpdated() {
        finish();
        removeAd.onAdRemoved();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case RESULT_OK: {

                        ShowAdActivity.this.finish();
                        startActivity(getIntent());
                        break;
                    }
                    case RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(ShowAdActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
            case REQUEST_FULLSCREEN_VIDEO:
                // onStart has already rebuilt the player at the old position; continue from
                // wherever fullscreen playback stopped.
                if (data != null) {
                    videoPosition = data.getLongExtra(VideoPlayerActivity.EXTRA_POSITION, videoPosition);
                    if (videoPlayer != null) videoPlayer.seekTo(videoPosition);
                }
                break;
        }
    }

    public void turnonlocation() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(ShowAdActivity.this).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        location.beginUpdates();

                        gmap.setMyLocationEnabled(true);

                        Log.i("abcd", "All location settings are satisfied.");
//                        gpsTracker = new GPSTracker(ShowAdActivity.this);

//                        Location locationnew = gpsTracker.getLocation();

                        location.beginUpdates();

                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                        Log.v("location", location.getLatitude() + "  " + location.getLongitude());
                        if (latLng != null && latLng.longitude != 0.0) {

                            Animation logoMoveAnimation = AnimationUtils.loadAnimation(ShowAdActivity.this, R.anim.zoom);
                            final Animation fadin = AnimationUtils.loadAnimation(ShowAdActivity.this, R.anim.fadein);

                            txt_use_discount.startAnimation(logoMoveAnimation);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressbar_use_discount.setVisibility(View.VISIBLE);
                                    progressbar_use_discount.startAnimation(fadin);
                                }
                            }, 300);

                            String url = StaticData.distance + "?token=" + new UserSessionManager(ShowAdActivity.this).getLoginToken() +
                                    "&adsId=" + ad.getId() + "&latitude=" + latLng.latitude + "&longitude=" + latLng.longitude;

                            Log.v("url", url);
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("token", new UserSessionManager(ShowAdActivity.this).getLoginToken());
                            Log.v("token", new UserSessionManager(ShowAdActivity.this).getLoginToken());
                            params.put("adsId", ad.getId());
                            Log.v("adsId", ad.getId());
                            params.put("latitude", latLng.latitude + "");
                            Log.v("latitude", latLng.latitude + "");
                            params.put("longitude", latLng.longitude + "");
                            Log.v("longitude", latLng.longitude + "");

                            Get_Volley_Call_Back.binddata(ShowAdActivity.this);
                            Get_Volley_Call_Back.Call_Volley(context, params, url, Request.Method.POST, 100022);

                        } else if (latLng.longitude == 0) {
                            ShowToast.failure("عدم دسترسی به موقعیت لطفا مجددا تلاش فرمایید.", ShowAdActivity.this);

                        } else {
                            ShowToast.failure("عدم دسترسی به موقعیت شما", ShowAdActivity.this);
                        }


//                        if (chbAddLoction.isChecked()) {
//                            mapLayout1.setVisibility(View.VISIBLE);
//                        } else {
//                            mapLayout1.setVisibility(View.GONE);
//                        }

                        if (ActivityCompat.checkSelfPermission(ShowAdActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ShowAdActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        } else
//                            gmap.setMyLocationEnabled(true);
                            break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        chbAddLoction.setChecked(false);
                        Log.i("abcd", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
//                        try {
//                            status.startResolutionForResult(
//                                    ShowAdActivity.this,
//                                    REQUEST_LOCATION);
//                        } catch (IntentSender.SendIntentException e) {
//                            e.printStackTrace();
//                        }
                        try {
                            status.startResolutionForResult(ShowAdActivity.this, 0x1);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("abcd", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("abcd", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
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
}
