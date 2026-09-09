package com.ideabonyan.iranapp.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Models.Estates;
import com.ideabonyan.iranapp.Models.PhotosData;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Show_Home_Ad_Details extends AppCompatActivity implements OnMapReadyCallback, Get_Insert_Edit_Data {
    public Estates estates;
    TextView txt_type, txt_ejare, txt_vadeae, txt_cost, txt_meter, txt_region, txt_area, txt_room_num, txt_type_person, txt_sanad;
    TextView txt_time, showAdTitle;
    TextView txt_tell1, txt_tell2;
    TextView showAdDescription;
    LinearLayout lin_type, lin_sanad, lin_ejare, lin_vadeae, lin_cost, lin_erea, lin_type_person, lin_room_num, lin_meter;
    LinearLayout lin_tell2, lin_tell1;
    SliderLayout showAdSliderLayout;
    PagerIndicator showAdCustomIndicator;
    boolean hasImage;
    CardView showAdMapCard;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout, appBarLayout2;
    private Toolbar toolbar;
    ImageButton backBTN, menuBTN;
    TextView titleTXT, descriptionTXT, headerTXT;
    View headerDevider;
    SupportMapFragment supportMapFragment;
    LinearLayout lin_fav;
    ProgressBar progressbar;
    ImageView img_fac_icn;
    LinearLayout lin_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__home__ad__details);
        holder();
        binddata();
        onclick();
        doAppBarLayoutStuff();

    }

    private void holder() {

        estates = (Estates) getIntent().getSerializableExtra("estates");
        headerDevider = findViewById(R.id.showAdHeaderDevider);

        lin_share = findViewById(R.id.lin_share);

        titleTXT = (TextView) findViewById(R.id.showAdTitle);
        descriptionTXT = (TextView) findViewById(R.id.showAdDescription);
        headerTXT = (TextView) findViewById(R.id.headerTXT);
        backBTN = (ImageButton) findViewById(R.id.newAdBackButton);
        menuBTN = (ImageButton) findViewById(R.id.showAdMenuBTN);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.showAdCollapsingToolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.showAdAppBar);

        txt_type = (TextView) findViewById(R.id.txt_type);
        txt_ejare = (TextView) findViewById(R.id.txt_ejare);
        txt_vadeae = (TextView) findViewById(R.id.txt_vadeae);
        txt_cost = (TextView) findViewById(R.id.txt_cost);
        txt_meter = (TextView) findViewById(R.id.txt_meter);
        txt_region = (TextView) findViewById(R.id.txt_region);
        txt_area = (TextView) findViewById(R.id.txt_area);
        txt_room_num = (TextView) findViewById(R.id.txt_room_num);
        txt_type_person = (TextView) findViewById(R.id.txt_type_person);
        txt_sanad = (TextView) findViewById(R.id.txt_sanad);

        showAdTitle = (TextView) findViewById(R.id.showAdTitle);
        txt_time = (TextView) findViewById(R.id.txt_time);

        txt_tell1 = (TextView) findViewById(R.id.txt_tell1);
        txt_tell1.setTextColor(Color.parseColor("#2196F3"));

        txt_tell2 = (TextView) findViewById(R.id.txt_tell2);
        txt_tell2.setTextColor(Color.parseColor("#2196F3"));


        showAdDescription = (TextView) findViewById(R.id.showAdDescription);

        lin_type = (LinearLayout) findViewById(R.id.lin_type);
        lin_sanad = (LinearLayout) findViewById(R.id.lin_sanad);
        lin_ejare = (LinearLayout) findViewById(R.id.lin_ejare);
        lin_vadeae = (LinearLayout) findViewById(R.id.lin_vadeae);
        lin_cost = (LinearLayout) findViewById(R.id.lin_cost);
        lin_erea = (LinearLayout) findViewById(R.id.lin_erea);
        lin_type_person = (LinearLayout) findViewById(R.id.lin_type_person);
        lin_room_num = (LinearLayout) findViewById(R.id.lin_room_num);
        lin_meter = (LinearLayout) findViewById(R.id.lin_meter);

        lin_tell1 = (LinearLayout) findViewById(R.id.lin_tell1);
        lin_tell2 = (LinearLayout) findViewById(R.id.lin_tell2);

        showAdSliderLayout = (SliderLayout) findViewById(R.id.showAdSliderLayout);

        showAdCustomIndicator = (PagerIndicator) findViewById(R.id.showAdCustomIndicator);

        showAdMapCard = (CardView) findViewById(R.id.showAdMapCard);
        img_fac_icn = findViewById(R.id.img_fac_icn);
        progressbar = findViewById(R.id.progressbar);
        lin_fav = (LinearLayout) findViewById(R.id.lin_fav);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.showAdMap);
        supportMapFragment.getMapAsync(this);

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
                    headerTXT.setText(estates.getAds_title());
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

    private void find_fav() {
        User user = UserHelper.LoadUserInfo(Show_Home_Ad_Details.this);

        if (user.isLoggedIn() && user.isVerrified()) {
            progressbar.setVisibility(View.VISIBLE);
            String url = StaticData.setfav + "/check?token=" + new UserSessionManager(Show_Home_Ad_Details.this).getLoginToken() +
                    "&adsId=" + estates.getId() + "&adType=estates";
            Map<String, String> params = new HashMap<String, String>();
//            params.put("adsId",estates.getId());
//            params.put("adType","estates");
            Get_Volley_Call_Back.binddata(Show_Home_Ad_Details.this);
            Get_Volley_Call_Back.Call_Volley(Show_Home_Ad_Details.this, params, url, Request.Method.GET, 1002);
        }


    }

    private void binddata() {

        find_fav();
        showAdTitle.setText(estates.getAds_title());

        txt_time.setText(estates.getElapsed_time());
        if (estates.getUser_type().equals("moshaver_amlak")) {
            txt_type_person.setText("مشاور املاک");
        } else if (estates.getUser_type().equals("person")) {
            txt_type_person.setText("شخصی");
        }
        txt_region.setText(estates.getCity_name() + " ," + estates.getRegion_name());
        showAdDescription.setText(estates.getDescription());
//        txt_tell1.setText(estates.getTelephone1());
        if (!(estates.getTelephone2() == null || estates.getTelephone2().equals("null") || estates.getTelephone2().equals("")
                || estates.getTelephone2().equals(null))) {
            txt_tell2.setText(estates.getTelephone2());
            lin_tell2.setVisibility(View.VISIBLE);

        } else {
            lin_tell2.setVisibility(View.GONE);
        }

        if (!(estates.getTelephone1() == null || estates.getTelephone1().equals("null") || estates.getTelephone1().equals("")
                || estates.getTelephone1().equals(null))) {
            txt_tell1.setText(estates.getTelephone1());
            lin_tell1.setVisibility(View.VISIBLE);

        } else {
            lin_tell1.setVisibility(View.GONE);
        }
        if (estates.getCategory_id().equals("1") || estates.getCategory_parent_id().equals("1") ||
                estates.getCategory_id().equals("3") || estates.getCategory_parent_id().equals("3")) {
            lin_meter.setVisibility(View.VISIBLE);
            lin_cost.setVisibility(View.VISIBLE);
            lin_type.setVisibility(View.VISIBLE);
            lin_type_person.setVisibility(View.VISIBLE);
            lin_room_num.setVisibility(View.VISIBLE);
            lin_erea.setVisibility(View.VISIBLE);
            if (estates.getCategory_id().equals("8")) {
                lin_room_num.setVisibility(View.GONE);
            }

            txt_meter.setText(estates.getMeters() + "متر");
            if (!estates.getPrice_kharid().equals("0") && !estates.getPrice_kharid().equals("-1")) {
                try {
                    Long num = Long.parseLong(estates.getPrice_kharid());
                    DecimalFormat numFormat;
                    String number;
                    numFormat = new DecimalFormat("#,###,###");
                    number = numFormat.format(num);
                    txt_cost.setText(String.valueOf(number) + " تومان");

                } catch (NumberFormatException e) {
                    txt_cost.setText(estates.getPrice_kharid() + " تومان");
                }
            } else if (estates.getPrice_kharid().equals("0")) {
                txt_cost.setText(" توافقی");
            } else {
                txt_cost.setText("جهت معاوضه");
            }

//            if (estates.getSell_or_buy().equals("sell")) {
//                txt_type.setText("ارائه");
//            } else {
//                txt_type.setText("درخواست");
//
//            }

            if (estates.getSell_or_buy().equals("buy")) {
                txt_type.setText("درخواستی");
            } else {
                txt_type.setText("فروشی");

            }
            Log.v("getRooms_count", estates.getRooms_count());
            if (estates.getRooms_count() == null || estates.getRooms_count().equals(null)) {
                txt_room_num.setText("بدون اتاق");

            } else if (estates.getRooms_count().equals("0")) {
                txt_room_num.setText("بدون اتاق");

            } else if (estates.getRooms_count().equals("1")) {
                txt_room_num.setText("یک");

            } else if (estates.getRooms_count().equals("2")) {
                txt_room_num.setText("دو");

            } else if (estates.getRooms_count().equals("3")) {
                txt_room_num.setText("سه");

            } else if (estates.getRooms_count().equals("4")) {
                txt_room_num.setText("چهار");

            } else if (estates.getRooms_count().equals("5")) {
                txt_room_num.setText("پنج یا بیشتر");

            } else {
                txt_room_num.setText("بدون اتاق");

            }


            if (estates.getIs_in_hoome().equals("0")) {
                txt_area.setText("نیست");
            } else {
                txt_area.setText("هست");

            }

            if (estates.getCategory_id().equals("3") || estates.getCategory_parent_id().equals("3")) {
                lin_sanad.setVisibility(View.VISIBLE);
                if (estates.getSanad_edari().equals("1")) {
                    txt_sanad.setText("دارد");
                }
            }
        } else if (estates.getCategory_id().equals("2") || estates.getCategory_parent_id().equals("2") ||
                estates.getCategory_id().equals("4") || estates.getCategory_parent_id().equals("4")) {
            lin_meter.setVisibility(View.VISIBLE);
            lin_vadeae.setVisibility(View.VISIBLE);
            lin_ejare.setVisibility(View.VISIBLE);
            lin_type.setVisibility(View.VISIBLE);
            lin_type_person.setVisibility(View.VISIBLE);
            lin_room_num.setVisibility(View.VISIBLE);
            lin_erea.setVisibility(View.VISIBLE);

            txt_meter.setText(estates.getMeters() + "متر");
            if (!estates.getPre_pay_ejare().equals("0") && !estates.getPre_pay_ejare().equals("-1")) {
                try {
                    Long num = Long.parseLong(estates.getPre_pay_ejare());
                    DecimalFormat numFormat;
                    String number;
                    numFormat = new DecimalFormat("#,###,###");
                    number = numFormat.format(num);
                    txt_vadeae.setText(String.valueOf(number) + " تومان");

                } catch (NumberFormatException e) {
                    txt_vadeae.setText(estates.getPre_pay_ejare() + " تومان");
                }
            } else if (estates.getPre_pay_ejare().equals("0")) {
                txt_vadeae.setText(" توافقی");
            } else {
                txt_vadeae.setText("مجانی");

            }
            if (!estates.getMonthly_price_ejare().equals("0") && !estates.getMonthly_price_ejare().equals("-1")) {
                try {
                    Long num = Long.parseLong(estates.getMonthly_price_ejare());
                    DecimalFormat numFormat;
                    String number;
                    numFormat = new DecimalFormat("#,###,###");
                    number = numFormat.format(num);
                    txt_ejare.setText(String.valueOf(number) + " تومان");

                } catch (NumberFormatException e) {
                    txt_ejare.setText(estates.getMonthly_price_ejare() + " تومان");
                }
            } else if (estates.getMonthly_price_ejare().equals("0")) {
                txt_ejare.setText("توافقی");
            } else {
                txt_ejare.setText("مجانی");
            }
            if (estates.getSell_or_buy().equals("buy")) {
                txt_type.setText("درخواستی");
            } else {
                txt_type.setText("ارائه");

            }

//            txt_room_num.setText(estates.getRooms_count() + " عدد");
            if (estates.getRooms_count() == null || estates.getRooms_count().equals(null)) {
                txt_room_num.setText("بدون اتاق");

            } else if (estates.getRooms_count().equals("0")) {
                txt_room_num.setText("بدون اتاق");

            } else if (estates.getRooms_count().equals("1")) {
                txt_room_num.setText("یک");

            } else if (estates.getRooms_count().equals("2")) {
                txt_room_num.setText("دو");

            } else if (estates.getRooms_count().equals("3")) {
                txt_room_num.setText("سه");

            } else if (estates.getRooms_count().equals("4")) {
                txt_room_num.setText("چهار");

            } else if (estates.getRooms_count().equals("5")) {
                txt_room_num.setText("پنج یا بیشتر");

            } else {
                txt_room_num.setText("بدون اتاق");

            }
            if (estates.getIs_in_hoome().equals("0")) {

                txt_area.setText("نیست");
            } else {
                txt_area.setText("هست");

            }
        }
        runImageSlider();

    }

    private void runImageSlider() {

        if (!(estates.getThumbnail_photo() == null || estates.getThumbnail_photo().equals("null") ||
                estates.getThumbnail_photo().equals(null) || estates.getThumbnail_photo().equals(""))) {
            PhotosData photosData = new PhotosData();
            photosData.setId("0");
            photosData.setName(estates.getThumbnail_photo());
            if (estates.getPhotosDatas().size() == 0) {
                estates.getPhotosDatas().add(0, photosData);

            } else {
                if (!estates.getPhotosDatas().get(0).getName().equals(estates.getThumbnail_photo())) {
                    estates.getPhotosDatas().add(0, photosData);

                }
            }

        }
        if (estates.getPhotosDatas().size() == 0) {

            hasImage = false;

            DefaultSliderView textSliderView = new DefaultSliderView(Show_Home_Ad_Details.this);
            textSliderView
                    .image(R.drawable.place_holder_home);

            showAdSliderLayout.addSlider(textSliderView);
            showAdSliderLayout.stopAutoCycle();

        } else {

            hasImage = true;

            for (int i = 0; i < estates.getPhotosDatas().size(); i++) {

                DefaultSliderView textSliderView = new DefaultSliderView(Show_Home_Ad_Details.this);
                textSliderView
                        .image(estates.getPhotosDatas().get(i).getName());

                showAdSliderLayout.addSlider(textSliderView);
            }

            showAdSliderLayout.setCustomIndicator(showAdCustomIndicator);

            if (estates.getPhotosDatas().size() == 1) showAdSliderLayout.stopAutoCycle();
//        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Tablet);
//        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
        }
    }

    private void fav_or_disfav() {
        User user = UserHelper.LoadUserInfo(Show_Home_Ad_Details.this);
        progressbar.setVisibility(View.VISIBLE);
        if (user.isLoggedIn() && user.isVerrified()) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("type", "estates");
            Log.v("type", "estates");
            params.put("ads_id", estates.getId());
            Log.v("ads_id", estates.getId());

//            params.put("token",new UserSessionManager(Show_Home_Ad_Details.this).getLoginToken());
//            Log.v("token",new UserSessionManager(Show_Home_Ad_Details.this).getLoginToken());

            String url = StaticData.setfav + "?token=" + new UserSessionManager(Show_Home_Ad_Details.this).getLoginToken();
            Get_Volley_Call_Back.binddata(Show_Home_Ad_Details.this);
            Get_Volley_Call_Back.Call_Volley(Show_Home_Ad_Details.this, params, url, Request.Method.POST, 1001);
        } else {
            ShowToast.failure("لطفا ابتدا ثبت نام نمایید.", Show_Home_Ad_Details.this);
        }
    }

    private void onclick() {
        lin_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareText = "ایران آپ : " + "\n"
                        + estates.getAds_title() + "\n";
                if (!(estates.getTelephone1() == null || estates.getTelephone1().equals(null) || estates.getTelephone1().equals("null"))) {
                    shareText += estates.getTelephone1() + "\n";

                }
                if (!(estates.getTelephone2() == null || estates.getTelephone2().equals(null) || estates.getTelephone2().equals("null"))) {
                    shareText += estates.getTelephone2() + "\n";

                }
                shareText += estates.getAddress() + "\n"
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
        lin_tell1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + estates.getTelephone1()));
                startActivity(intent);
            }
        });
        lin_tell2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + estates.getTelephone2()));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (estates.getLatitude().equals("null") || estates.getLatitude().equals("") || estates.getLatitude() == null) {
            showAdMapCard.setVisibility(View.GONE);
        } else {

            LatLng ll = new LatLng(Double.parseDouble(estates.getLatitude()), Double.parseDouble(estates.getLongitude()));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            googleMap.moveCamera(update);


            // Creating a Marker
            MarkerOptions markerOptions = new MarkerOptions();

            //setting the position or the marker
            markerOptions.position(ll);

            //setting the title for the marker
            markerOptions.title(estates.getAddress());

            //clearing the previous marker
            googleMap.clear();

            //animating to the touched position
//        googleMap.animateCamera(CameraUpdateFactory.newLatLng(ll));

            //adding new marker
            googleMap.addMarker(markerOptions);
        }
    }

    @Override
    public void on_volley_response(String response, int id) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (id == 1001) {
                progressbar.setVisibility(View.GONE);

                if (jsonObject.getString("status").equals("200")) {

                    if (jsonObject.getString("is_fav").equals("true")) {
                        Picasso.with(Show_Home_Ad_Details.this)
                                .load(R.drawable.heart)
                                .fit()
                                .into(img_fac_icn);
                        ShowToast.success("آگهی مورد نظر با موفقیت به لیست علاقه مندی ها افزوده شد", Show_Home_Ad_Details.this);

                    } else {
                        Picasso.with(Show_Home_Ad_Details.this)
                                .load(R.drawable.ic_favorite_border_black_24dp)
                                .fit()
                                .into(img_fac_icn);
                        ShowToast.success("آگهی مورد نظر با موفقیت از لیست علاقه مندی ها حذف گردید", Show_Home_Ad_Details.this);

                    }
                } else {
                    ShowToast.failure("لطفا نحوه ی اتصال به اینترنت دستگاه خود را بررسی نمایید", Show_Home_Ad_Details.this);
                }
            } else if (id == 1002) {
                progressbar.setVisibility(View.GONE);
                if (jsonObject.getString("status").equals("200")) {
                    if (jsonObject.getString("is_favorite").equals("true")) {
                        Picasso.with(Show_Home_Ad_Details.this)
                                .load(R.drawable.heart)
                                .fit()
                                .into(img_fac_icn);

                    } else {
                        Picasso.with(Show_Home_Ad_Details.this)
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
