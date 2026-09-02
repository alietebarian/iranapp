package com.ideabonyan.iranapp.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
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
import com.ideabonyan.iranapp.Models.Job;
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

import java.util.HashMap;
import java.util.Map;

public class Show_Job_Ad_Details extends AppCompatActivity implements OnMapReadyCallback,Get_Insert_Edit_Data {
    public Job job;
    TextView txt_type,txt_contract,txt_education,txt_cat,txt_region,txt_expertise,txt_cost;
    TextView txt_time, showAdTitle;
    TextView txt_tell1, txt_tell2;
    TextView showAdDescription;
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
        setContentView(R.layout.activity_show__job__ad__details);
        holder();
        binddata();
        onclick();
        doAppBarLayoutStuff();

    }
    private void fav_or_disfav() {
        User user = UserHelper.LoadUserInfo(Show_Job_Ad_Details.this);
        progressbar.setVisibility(View.VISIBLE);
        if (user.isLoggedIn() && user.isVerrified()) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("type", "employs");
            params.put("ads_id", job.getId()); 

            String url = StaticData.setfav + "?token=" + new UserSessionManager(Show_Job_Ad_Details.this).getLoginToken();
            Get_Volley_Call_Back.binddata(Show_Job_Ad_Details.this);
            Get_Volley_Call_Back.Call_Volley(Show_Job_Ad_Details.this, params, url, Request.Method.POST, 1001);
        } else {
            ShowToast.failure("لطفا ابتدا ثبت نام نمایید.", Show_Job_Ad_Details.this);
        }
    }
    private void holder() {
        job=(Job) getIntent().getSerializableExtra("job");

        lin_share = findViewById(R.id.lin_share);

        headerDevider = findViewById(R.id.showAdHeaderDevider);
        img_fac_icn = findViewById(R.id.img_fac_icn);
        progressbar = findViewById(R.id.progressbar);
        lin_fav = (LinearLayout) findViewById(R.id.lin_fav);
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
        txt_region = (TextView) findViewById(R.id.txt_region);
        txt_cat = (TextView) findViewById(R.id.txt_cat);
        txt_education = (TextView) findViewById(R.id.txt_education);
        txt_contract = (TextView) findViewById(R.id.txt_contract);
        txt_expertise = (TextView) findViewById(R.id.txt_expertise);
        txt_time = (TextView) findViewById(R.id.txt_time);

        showAdTitle = (TextView) findViewById(R.id.showAdTitle);
        txt_cost = (TextView) findViewById(R.id.txt_cost);

        txt_tell1 = (TextView) findViewById(R.id.txt_tell1);
        txt_tell1.setTextColor(Color.parseColor("#2196F3"));

        txt_tell2 = (TextView) findViewById(R.id.txt_tell2);
        txt_tell2.setTextColor(Color.parseColor("#2196F3"));


        showAdDescription = (TextView) findViewById(R.id.showAdDescription);

        lin_tell1 = (LinearLayout) findViewById(R.id.lin_tell1);
        lin_tell2 = (LinearLayout) findViewById(R.id.lin_tell2);

        showAdSliderLayout = (SliderLayout) findViewById(R.id.showAdSliderLayout);

        showAdCustomIndicator = (PagerIndicator) findViewById(R.id.showAdCustomIndicator);

        showAdMapCard= (CardView) findViewById(R.id.showAdMapCard);

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
                    headerTXT.setText(job.getAds_title());
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
    private void find_fav(){
        User user = UserHelper.LoadUserInfo(Show_Job_Ad_Details.this);

        if (user.isLoggedIn()&&user.isVerrified()){
            progressbar.setVisibility(View.VISIBLE);
            String url=StaticData.setfav+"/check?token="+new UserSessionManager(Show_Job_Ad_Details.this).getLoginToken()+
                    "&adsId="+job.getId()+"&adType=employs";
            Map<String, String> params = new HashMap<String, String>();
//            params.put("adsId",estates.getId());
//            params.put("adType","estates");
            Get_Volley_Call_Back.binddata(Show_Job_Ad_Details.this);
            Get_Volley_Call_Back.Call_Volley(Show_Job_Ad_Details.this,params,url, Request.Method.GET,1002);
        }


    }
    private void binddata() {

        try {
            find_fav();
            txt_region.setText(job.getCity_name()+" ،"+job.getRegion_name());

        showAdTitle.setText(job.getAds_title());
        txt_time.setText(job.getElapsed_time());
        txt_cat.setText("استخدام");
        if (job.getEducation_level().equals("underdiploma")){
            txt_education.setText("زیر دیپلم");
        }else if (job.getEducation_level().equals("diploma")){
            txt_education.setText(" دیپلم");
        }else if (job.getEducation_level().equals("tact")){
            txt_education.setText("کاردانی");
        }else if (job.getEducation_level().equals("expertise")){
            txt_education.setText("کارشناسی");
        }else if (job.getEducation_level().equals("masterdegree")){
            txt_education.setText("کارشناسی ارشد");
        }else if (job.getEducation_level().equals("doctoral")){
            txt_education.setText("دکترا");
        }

        showAdDescription.setText(job.getDescription());
//        txt_tell1.setText(job.getTelephone1());
        if (!(job.getTelephone2()==null||job.getTelephone2().equals(null)||job.getTelephone2().equals("null")
                ||job.getTelephone2().equals(""))){
            txt_tell2.setText(job.getTelephone2());
            lin_tell2.setVisibility(View.VISIBLE);

        }else{
            lin_tell2.setVisibility(View.GONE);
        }
        if (!(job.getTelephone1()==null||job.getTelephone1().equals(null)||job.getTelephone1().equals("null")
                ||job.getTelephone1().equals(""))){
            txt_tell1.setText(job.getTelephone1());
            lin_tell1.setVisibility(View.VISIBLE);

        }else{
            lin_tell1.setVisibility(View.GONE);
        }

            Log.v("arg_type",job.getAgremment_type());
        if (job.getAgremment_type().equals("tamamvaght")){
            txt_contract.setText("تمام وقت");

        }else if (job.getAgremment_type().equals("parevaght"))
        {  txt_contract.setText(" پاره وقت");

        }else if (job.getAgremment_type().equals("moshaveri"))
        {  txt_contract.setText("مشاوره ای");

        }else if (job.getAgremment_type().equals("projei"))
        {  txt_contract.setText("پروژه ای");

        }

        if (job.getType().equals("forsatshoghli")){
            txt_type.setText("استخدام");

        }else{
            txt_type.setText("آماده به کار");

        }
        txt_expertise.setText(job.getSpecialty());

        runImageSlider();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

    }
    private void runImageSlider() {

        if (!(job.getThumbnail_photo() == null || job.getThumbnail_photo().equals("null") ||
                job.getThumbnail_photo().equals(null) || job.getThumbnail_photo().equals(""))) {
            PhotosData photosData = new PhotosData();
            photosData.setId("0");
            photosData.setName(job.getThumbnail_photo());
            if (job.getPhotos().size() == 0) {
                job.getPhotos().add(0, photosData);

            }else{
                if (!job.getPhotos().get(0).getName().equals(job.getThumbnail_photo())){
                    job.getPhotos().add(0, photosData);
                }
            }

        }
        if (job.getPhotos().size() == 0) {

            hasImage = false;

            DefaultSliderView textSliderView = new DefaultSliderView(Show_Job_Ad_Details.this);
            textSliderView
                    .image(R.drawable.place_holder_job);

            showAdSliderLayout.addSlider(textSliderView);
            showAdSliderLayout.stopAutoCycle();

        } else {

            hasImage = true;

            for (int i = 0; i < job.getPhotos().size(); i++) {

                DefaultSliderView textSliderView = new DefaultSliderView(Show_Job_Ad_Details.this);
                textSliderView
                        .image(job.getPhotos().get(i).getName());

                showAdSliderLayout.addSlider(textSliderView);
            }

            showAdSliderLayout.setCustomIndicator(showAdCustomIndicator);

            if (job.getPhotos().size() == 1) showAdSliderLayout.stopAutoCycle();
//        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Tablet);
//        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
        }
    }

    private void onclick() {
        lin_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareText ="ایران آپ : "+"\n"
                        + job.getAds_title() +  "\n";
                if (!(job.getTelephone1()==null||job.getTelephone1().equals(null)||job.getTelephone1().equals("null"))){
                    shareText+= job.getTelephone1() +  "\n";

                }if (!(job.getTelephone2()==null||job.getTelephone2().equals(null)||job.getTelephone2().equals("null"))){
                    shareText+= job.getTelephone2() +  "\n";

                }
                shareText += job.getAddress() + "\n\n"
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
                intent.setData(Uri.parse("tel:" + job.getTelephone1()));
                startActivity(intent);
            }
        });
        lin_tell2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + job.getTelephone2()));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (job.getLatitude().equals("null") || job.getLatitude().equals("") || job.getLatitude() == null) {
            showAdMapCard.setVisibility(View.GONE);
        } else {

            LatLng ll = new LatLng(Double.parseDouble(job.getLatitude()), Double.parseDouble(job.getLongitude()));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            googleMap.moveCamera(update);


            // Creating a Marker
            MarkerOptions markerOptions = new MarkerOptions();

            //setting the position or the marker
            markerOptions.position(ll);

            //setting the title for the marker
            markerOptions.title(job.getAddress());

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
                if (jsonObject.getString("status").equals("200")) {
                    progressbar.setVisibility(View.GONE);

                    if (jsonObject.getString("is_fav").equals("true")) {
                        Picasso.with(Show_Job_Ad_Details.this)
                                .load(R.drawable.heart)
                                .fit()
                                .into(img_fac_icn);
                        ShowToast.success("آگهی مورد نظر با موفقیت به لیست علاقه مندی ها افزوده شد", Show_Job_Ad_Details.this);

                    } else {
                        Picasso.with(Show_Job_Ad_Details.this)
                                .load(R.drawable.ic_favorite_border_black_24dp)
                                .fit()
                                .into(img_fac_icn);
                        ShowToast.success("آگهی مورد نظر با موفقیت از لیست علاقه مندی ها حذف گردید", Show_Job_Ad_Details.this);

                    }
                } else {
                    ShowToast.failure("لطفا نحوه ی اتصال به اینترنت دستگاه خود را بررسی نمایید", Show_Job_Ad_Details.this);
                }
                
            }else if (id==1002){
                progressbar.setVisibility(View.GONE);
                if (jsonObject.getString("status").equals("200")) {
                    if (jsonObject.getString("is_favorite").equals("true")){
                        Picasso.with(Show_Job_Ad_Details.this)
                                .load(R.drawable.heart)
                                .fit()
                                .into(img_fac_icn);

                    }else{
                        Picasso.with(Show_Job_Ad_Details.this)
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


