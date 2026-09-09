package com.ideabonyan.iranapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.ideabonyan.iranapp.Utils.ImageSliderAdapter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.RemoveAd;
import com.ideabonyan.iranapp.Interface.UpdateAd;
import com.ideabonyan.iranapp.Models.PhotosData;
import com.ideabonyan.iranapp.Models.Vehicles;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.CustomTypefaceSpan;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ShowCarActivity extends AppCompatActivity implements UpdateAd, OnMapReadyCallback, Get_Insert_Edit_Data {

    public  String type;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout, appBarLayout2;
    public Vehicles vehicles;
    private Toolbar toolbar;
    View headerDevider;
    ViewPager2 sliderLayout;
    TabLayout pagerIndicator;
    RelativeLayout imageArea;
    NestedScrollView nestedScrollView;
    TextView titleTXT, descriptionTXT, headerTXT;
    LinearLayout favoriteBTN, downVoteBTN, upVoteBTN;
    //    LinearLayout shareBTN;
    LinearLayout contactInfoBTN;
    ImageButton backBTN, menuBTN;
    ImageButton backBTN2, menuBTN2;
    Context context;
    SupportMapFragment supportMapFragment;
    ImageView mapOverlay;
    LinearLayout lin_share;

    //    ProgressBar progressBar;
//    TextView retryBTN;
//    LinearLayout userPrefDatalayout;
    ImageView favImage, downVoteIMG, upVoteIMG;
    TextView downVoteTXT, upVoteTXT;

    CardView mapCard;

    TextView pendingIndicator;
    LinearLayout ratingBarArea, lin_tell1, lin_tell2;
    TextView txt_engin;
    CardView ownerArea;
    LinearLayout lin_model, lin_chassis_kind, lin_create_year, lin_kilometer, lin_engin;
    TextView txt_model, txt_chassis_kind, create_year, txt_kilometer, txt_brand, txt_region, txt_cost, txt_tell1, txt_tell2;
    boolean hasImage;
    User user;
    LinearLayout lin_fav;
    ProgressBar progressbar;
    ImageView img_fac_icn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_car);

        initializer();
        onClicks();
        smallStuff();
        fillInfo();
//        fillUserPrefsData();
        doAppBarLayoutStuff();
    }

    @Override
    protected void onResume() {
        super.onResume();


//        progressBar.setVisibility(View.VISIBLE);
//        userPrefDatalayout.setVisibility(View.INVISIBLE);
//        getPrefData();
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
                    headerTXT.setText(vehicles.getAds_title());
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

    private void fav_or_disfav() {
        User user = UserHelper.LoadUserInfo(ShowCarActivity.this);
        progressbar.setVisibility(View.VISIBLE);
        if (user.isLoggedIn() && user.isVerrified()) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("type", "vehicles");
            params.put("ads_id", vehicles.getId());
            Log.v("type", "vehicles");
            Log.v("ads_id", vehicles.getId());

            String url = StaticData.setfav + "?token=" + new UserSessionManager(ShowCarActivity.this).getLoginToken();
            Get_Volley_Call_Back.binddata(ShowCarActivity.this);
            Get_Volley_Call_Back.Call_Volley(ShowCarActivity.this, params, url, Request.Method.POST, 1001);
        } else {
            ShowToast.failure("لطفا ابتدا ثبت نام نمایید.", ShowCarActivity.this);
        }
    }

    private void initializer() {
        vehicles=(Vehicles) getIntent().getSerializableExtra("vehicles") ;
        type=getIntent().getStringExtra("type");
        lin_share = findViewById(R.id.lin_share);

        img_fac_icn = findViewById(R.id.img_fac_icn);
        progressbar = findViewById(R.id.progressbar);
        lin_fav = (LinearLayout) findViewById(R.id.lin_fav);
        txt_kilometer = (TextView) findViewById(R.id.txt_kilometer);
        create_year = (TextView) findViewById(R.id.create_year);
        txt_chassis_kind = (TextView) findViewById(R.id.txt_chassis_kind);
        txt_model = (TextView) findViewById(R.id.txt_model);
        txt_brand = (TextView) findViewById(R.id.txt_brand);
        txt_region = (TextView) findViewById(R.id.txt_region);
        txt_cost = (TextView) findViewById(R.id.txt_cost);
        txt_tell1 = (TextView) findViewById(R.id.txt_tell1);
        txt_tell1.setTextColor(Color.parseColor("#2196F3"));

        txt_tell2 = (TextView) findViewById(R.id.txt_tell2);
        txt_tell2.setTextColor(Color.parseColor("#2196F3"));

        txt_engin = (TextView) findViewById(R.id.txt_engin);

        lin_tell1 = (LinearLayout) findViewById(R.id.lin_tell1);
        lin_tell2 = (LinearLayout) findViewById(R.id.lin_tell2);
        lin_model = (LinearLayout) findViewById(R.id.lin_model);
        lin_chassis_kind = (LinearLayout) findViewById(R.id.lin_chassis_kind);
        lin_create_year = (LinearLayout) findViewById(R.id.lin_create_year);
        lin_kilometer = (LinearLayout) findViewById(R.id.lin_kilometer);
        lin_engin = (LinearLayout) findViewById(R.id.lin_engin);

        ratingBarArea = (LinearLayout) findViewById(R.id.showAdRatingBarArea);
//        ownerNameTXT = (TextView) findViewById(R.id.showAdOwnerNameTXT);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.showAdCollapsingToolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.showAdAppBar);
//        appBarLayout2 = (AppBarLayout) findViewById(R.id.showAdAppBar2);
        headerDevider = findViewById(R.id.showAdHeaderDevider);

        sliderLayout = (ViewPager2) findViewById(R.id.showAdSliderLayout);
        pagerIndicator = (TabLayout) findViewById(R.id.showAdCustomIndicator);
        nestedScrollView = (NestedScrollView) findViewById(R.id.showAdNestedScroll);
        titleTXT = (TextView) findViewById(R.id.showAdTitle);
        descriptionTXT = (TextView) findViewById(R.id.showAdDescription);
        headerTXT = (TextView) findViewById(R.id.headerTXT);
//        shareBTN = (LinearLayout) findViewById(R.id.showAdShareBTN);
        favoriteBTN = (LinearLayout) findViewById(R.id.showAdFavoriteBTN);
        downVoteBTN = (LinearLayout) findViewById(R.id.showAdDownVoteBTN);
        upVoteBTN = (LinearLayout) findViewById(R.id.showAdUpVoteBTN);
        contactInfoBTN = (LinearLayout) findViewById(R.id.showAdContactUsBTN);
        backBTN = (ImageButton) findViewById(R.id.newAdBackButton);
//        backBTN2 = (ImageButton) findViewById(R.id.newAdBackButton2);
        menuBTN = (ImageButton) findViewById(R.id.showAdMenuBTN);
//        menuBTN2 = (ImageButton) findViewById(R.id.showAdMenuBTN2);
        context = ShowCarActivity.this;
        this.setTitle("");
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.showAdMap);
        mapOverlay = (ImageView) findViewById(R.id.showAdMapOverlay);
//        progressBar = (ProgressBar) findViewById(R.id.showAdProgressBar);
//        userPrefDatalayout = (LinearLayout) findViewById(R.id.showAdUserPrefLayout);
//        retryBTN = (TextView) findViewById(R.id.showAdUserPrefRetryBTN);
        favImage = (ImageView) findViewById(R.id.showAdFavoriteIMG);
        downVoteIMG = (ImageView) findViewById(R.id.showAdDownVoteIMG);
        upVoteIMG = (ImageView) findViewById(R.id.showAdUpVoteIMG);
        downVoteTXT = (TextView) findViewById(R.id.showAdDownVoteTXT);
        upVoteTXT = (TextView) findViewById(R.id.showAdUpVoteTXT);
        mapCard = (CardView) findViewById(R.id.showAdMapCard);
        pendingIndicator = (TextView) findViewById(R.id.showAdPendingMessage);
//        ownerArea = (CardView) findViewById(R.id.showAdAdOwnerArea);
        imageArea = (RelativeLayout) findViewById(R.id.showAdImageArea);
    }

    private void smallStuff() {
        supportMapFragment.getMapAsync(this);
        NewAdActivity.bindData(this);
        user = UserHelper.LoadUserInfo(ShowCarActivity.this);
    }

    private void onClicks() {
        lin_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareText ="ایران آپ : "+"\n"
                        + vehicles.getAds_title() +  "\n";
                if (!(vehicles.getTelephone1()==null||vehicles.getTelephone1().equals(null)||vehicles.getTelephone1().equals("null"))){
                    shareText+= vehicles.getTelephone1() +  "\n";

                }if (!(vehicles.getTelephone2()==null||vehicles.getTelephone2().equals(null)||vehicles.getTelephone2().equals("null"))){
                    shareText+= vehicles.getTelephone2() +  "\n";

                }
                shareText += vehicles.getAddress() +  "\n"
                        + "با ایران آپ در تخفیف ها به روز باشید" + "\n\n"
                        +"لینک دانلود : "+StaticData.download_link;
//                        StaticData.LINK_IN_BAZAR;

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        lin_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fav_or_disfav();
            }
        });

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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


        lin_tell1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + vehicles.getTelephone1()));
                startActivity(intent);
            }
        });
        lin_tell2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + vehicles.getTelephone2()));
                startActivity(intent);
            }
        });


    }

    private void find_fav() {
        User user = UserHelper.LoadUserInfo(ShowCarActivity.this);

        if (user.isLoggedIn() && user.isVerrified()) {
            progressbar.setVisibility(View.VISIBLE);
            String url = StaticData.setfav + "/check?token=" + new UserSessionManager(ShowCarActivity.this).getLoginToken() +
                    "&adsId=" + vehicles.getId() + "&adType=vehicles";
            Map<String, String> params = new HashMap<String, String>();
//            params.put("adsId",estates.getId());
//            params.put("adType","estates");
            Get_Volley_Call_Back.binddata(ShowCarActivity.this);
            Get_Volley_Call_Back.Call_Volley(ShowCarActivity.this, params, url, Request.Method.GET, 1002);
        }


    }

    private void fillInfo() {

        try {
            find_fav();
            titleTXT.setText(vehicles.getAds_title());
            txt_region.setText(vehicles.getCity_name() + "،" + vehicles.getRegion_name());

            if (!vehicles.getPrice().equals("0")) {
                try {
                    BigInteger num = BigInteger.valueOf(Long.parseLong(vehicles.getPrice()));
                    DecimalFormat numFormat;
                    String number;
                    numFormat = new DecimalFormat("#,###,###");
                    number = numFormat.format(num);
                    txt_cost.setText(String.valueOf(number) + " تومان");

                } catch (NumberFormatException e) {
                    txt_cost.setText(vehicles.getPrice() + " تومان");
                }
            } else {
                txt_cost.setText("قیمت توافقی");
            }
            if (vehicles.getTelephone1() == null || vehicles.getTelephone1().equals(null) ||
                    vehicles.getTelephone1().equals("null") || vehicles.getTelephone1().equals("")) {
                lin_tell1.setVisibility(View.GONE);
            } else {
                lin_tell1.setVisibility(View.VISIBLE);
                txt_tell1.setText(vehicles.getTelephone1());


            }
            if (vehicles.getTelephone2() == null || vehicles.getTelephone2().equals(null) ||
                    vehicles.getTelephone2().equals("null") || vehicles.getTelephone2().equals("")) {
                lin_tell2.setVisibility(View.GONE);
            } else {
                lin_tell2.setVisibility(View.VISIBLE);
                txt_tell2.setText(vehicles.getTelephone2());


            }
            if (vehicles.getDescription() != null && !vehicles.getDescription().equals("null") &&
                    !vehicles.getDescription().equals("") && !vehicles.getDescription().equals(null)) {
                descriptionTXT.setText(vehicles.getDescription());
            } else descriptionTXT.setVisibility(View.GONE);
            if (vehicles.getStatus().equals("pending"))
                pendingIndicator.setVisibility(View.VISIBLE);

            runImageSlider();
            type = vehicles.getType();

            if (type.equals("khodro")) {

                txt_brand.setText(vehicles.getBrand());
                Log.v("brand", vehicles.getBrand_id());
                if (vehicles.getChassis_type().equals("savari")) {
                    txt_chassis_kind.setText("سواری");

                } else if (vehicles.getChassis_type().equals("hachback")) {
                    txt_chassis_kind.setText("هاچ بک");

                } else if (vehicles.getChassis_type().equals("shasiboland")) {
                    txt_chassis_kind.setText("شاسی بلند");

                } else if (vehicles.getChassis_type().equals("vanet")) {
                    txt_chassis_kind.setText("وانت");

                } else if (vehicles.getChassis_type().equals("krook")) {
                    txt_chassis_kind.setText("کروک");

                } else if (vehicles.getChassis_type().equals("van")) {
                    txt_chassis_kind.setText("ون");

                } else if (vehicles.getChassis_type().equals("cupe")) {
                    txt_chassis_kind.setText("کوپه");

                } else if (vehicles.getChassis_type().equals("station")) {
                    txt_chassis_kind.setText("استیشن");

                } else if (vehicles.getChassis_type().equals("other")) {
                    txt_chassis_kind.setText("دیگر");

                }
                create_year.setText(vehicles.getProduction_year());
                txt_kilometer.setText(vehicles.getKilometre());
                lin_chassis_kind.setVisibility(View.VISIBLE);
                lin_create_year.setVisibility(View.VISIBLE);
                lin_kilometer.setVisibility(View.VISIBLE);
                lin_engin.setVisibility(View.GONE);

                if (vehicles.getModel_id() == null || vehicles.getModel_id().equals("null") || vehicles.getModel_id().equals(null) ||
                        vehicles.getModel_id().equals("")) {
                    lin_model.setVisibility(View.GONE);
                } else {
                    lin_model.setVisibility(View.VISIBLE);
                    txt_model.setText(vehicles.getModel_name());
                }
            } else {
                lin_chassis_kind.setVisibility(View.GONE);
                lin_create_year.setVisibility(View.GONE);
                lin_kilometer.setVisibility(View.GONE);
                if (type.equals("motorcycle")) {
                    lin_engin.setVisibility(View.VISIBLE);
                    txt_engin.setText(vehicles.getCylinder_volume());
                    txt_brand.setText("موتور سیکلت");
                    lin_create_year.setVisibility(View.VISIBLE);
                    create_year.setText(vehicles.getProduction_year());


                } else if (type.equals("other")) {
                    lin_engin.setVisibility(View.GONE);
                    txt_brand.setText("سایر وسایل نقلیه");


                } else if (type.equals("khodroclasic")) {
                    lin_engin.setVisibility(View.GONE);
                    txt_brand.setText("خودرو کلاسیک");


                } else if (type.equals("khordrosorn")) {
                    lin_engin.setVisibility(View.GONE);
                    txt_brand.setText("خودرو سنگین");


                } else if (type.equals("lavazem")) {
                    lin_engin.setVisibility(View.GONE);
                    txt_brand.setText("لوازم وسایل نقلیه");


                }


            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private void runImageSlider() {

        if (!(vehicles.getThumbnail_photo() == null || vehicles.getThumbnail_photo().equals("null") ||
                vehicles.getThumbnail_photo().equals(null) || vehicles.getThumbnail_photo().equals(""))) {
            PhotosData photosData = new PhotosData();
            photosData.setId("0");
            photosData.setName(vehicles.getThumbnail_photo());
            if (vehicles.getPhotosDatas().size() == 0) {
                vehicles.getPhotosDatas().add(0, photosData);
                Log.v("imagurl0",vehicles.getPhotosDatas().get(0).getName()+"="+vehicles.getThumbnail_photo());

            } else {
                if (!vehicles.getPhotosDatas().get(0).getName().equals((vehicles.getThumbnail_photo()))) {
                    vehicles.getPhotosDatas().add(0, photosData);
                    Log.v("imagurl1",vehicles.getPhotosDatas().get(0).getName()+"="+vehicles.getThumbnail_photo());
                }
            }

        }
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(R.drawable.place_holder_car);

        if (vehicles.getPhotosDatas().size() == 0) {
            hasImage = false;
            sliderAdapter.showPlaceholderOnly();
        } else {
            hasImage = true;

            java.util.List<String> urls = new java.util.ArrayList<>();
            for (int i = 0; i < vehicles.getPhotosDatas().size(); i++) {
                urls.add(vehicles.getPhotosDatas().get(i).getName());
            }
            sliderAdapter.setImageUrls(urls);
        }

        ImageSliderAdapter.attach(sliderLayout, pagerIndicator, sliderAdapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (vehicles.getLatitude().equals("null") || vehicles.getLatitude().equals("") || vehicles.getLatitude() == null) {
            mapCard.setVisibility(View.GONE);
        } else {

            LatLng ll = new LatLng(Double.parseDouble(vehicles.getLatitude()), Double.parseDouble(vehicles.getLongitude()));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            googleMap.moveCamera(update);


            // Creating a Marker
            MarkerOptions markerOptions = new MarkerOptions();

            //setting the position or the marker
            markerOptions.position(ll);

            //setting the title for the marker
            markerOptions.title(vehicles.getAddress());

            //clearing the previous marker
            googleMap.clear();

            //animating to the touched position
//        googleMap.animateCamera(CameraUpdateFactory.newLatLng(ll));

            //adding new marker
            googleMap.addMarker(markerOptions);
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
        mi.setTitle(mNewTitle);
    }


    static RemoveAd removeAd;

    static public void bindData(RemoveAd rremoveAd) {
        removeAd = rremoveAd;
    }

    @Override
    public void onAdUpdated() {
        finish();
        removeAd.onAdRemoved();
    }

    @Override
    public void on_volley_response(String response, int id) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (id == 1001) {
                if (jsonObject.getString("status").equals("200")) {
                    progressbar.setVisibility(View.GONE);

                    if (jsonObject.getString("is_fav").equals("true")) {
                        Picasso.get()
                                .load(R.drawable.heart)
                                .fit()
                                .into(img_fac_icn);
                        ShowToast.success("آگهی مورد نظر با موفقیت به لیست علاقه مندی ها افزوده شد", ShowCarActivity.this);

                    } else {
                        Picasso.get()
                                .load(R.drawable.ic_favorite_border_black_24dp)
                                .fit()
                                .into(img_fac_icn);
                        ShowToast.success("آگهی مورد نظر با موفقیت از لیست علاقه مندی ها حذف گردید", ShowCarActivity.this);

                    }
                } else {
                    ShowToast.failure("لطفا نحوه ی اتصال به اینترنت دستگاه خود را بررسی نمایید", ShowCarActivity.this);
                }
            } else if (id == 1002) {
                progressbar.setVisibility(View.GONE);
                if (jsonObject.getString("status").equals("200")) {
                    if (jsonObject.getString("is_favorite").equals("true")) {
                        Picasso.get()
                                .load(R.drawable.heart)
                                .fit()
                                .into(img_fac_icn);

                    } else {
                        Picasso.get()
                                .load(R.drawable.ic_favorite_border_black_24dp)
                                .fit()
                                .into(img_fac_icn);

                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }
}

