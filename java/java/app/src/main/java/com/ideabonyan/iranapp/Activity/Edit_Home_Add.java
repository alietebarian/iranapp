package com.ideabonyan.iranapp.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Models.Estates;
import com.ideabonyan.iranapp.Models.Home_Category;
import com.ideabonyan.iranapp.Models.ProvicesAndCities;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Compress_image;
import com.ideabonyan.iranapp.Utils.GPSTracker;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;

import static com.ideabonyan.iranapp.Activity.NewAdActivity.scaleDown;

public class Edit_Home_Add extends AppCompatActivity implements OnMapReadyCallback, Get_Insert_Edit_Data {
    int delete_thumbnail_photo = 0;
    public Estates estates;
    public boolean isItNewAd = true;
    boolean isMapReady = false;
    ViewGroup rootView;
    GoogleMap googleMap;
    LatLng ltLg;
    RelativeLayout mapLayout;
    int selection_pic = 0;
    ImageView img_pic1, img_pic2, img_pic3, img_pic4, img_pic5;
    ImageView img_add_pic1, img_add_pic2, img_add_pic3, img_add_pic4, img_add_pic5;
    EditText edt_dec, edt_title;
    EditText edt_address;
    EditText edt_phone2, edt_phone1, edt_name;
    Spinner spin_region, spin_city, spin_province;
    Spinner spin_cat, spin_sub_cat;
    Spinner spin_room_number;
    LinearLayout lin_room_number;
    List<String> province, city, region;
    List<String> room_num;
    ProgressBar newAdSpinnerLoadProgressBar;
    List<ProvicesAndCities> provinces, citys, regions;
    CheckBox newAdShowMap;
    SupportMapFragment supportMapFragment;
    private final int REQUEST_IMAGE = 2;
    protected final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private ArrayList<String> mSelectPath;
    Button newAdSubmitBTN;
    List<String> cat, sub_cat;
    List<Home_Category> subcategory;
    TextInputLayout til_cost, til_vadeae, til_ejare;
    EditText edt_ejare, edt_vadeae, edt_cost;
    CardView card_type_person, card_type, card_area, card_sanad;
    RadioButton radio_person, radio_moshaver, radio_sell, radio_bye, radio_nist, radio_hast;
    RadioButton radio_darad, radio_nadarad;
    LinearLayout lin_cost, lin_vadeae, lin_ejare;
    RadioButton radio_tavafoghi, radio_maghto, radio_tavafoghi_vadeae, radio_maghto_vadeae, radio_maghto_ejare,
            radio_tavafoghi_ejare, radio_moaveze, radio_majani_ejare, radio_majani_vadeae;
    TextInputLayout til_meter;
    EditText edt_meter;
    List<String> photo_to_delete;
    CardView card_pic5, card_pic4, card_pic3, card_pic2, card_pic1;
    ScrollView scrollView;
    ImageView newAdMapOverlay;

    LinearLayout lin_remove_map_loc;

    boolean cat_change = false, brand_change = false, modell_change = false, sub_cat_change = false,
            province_change = false, city_change = false, region_change = false, cyland_change = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__new__home__add);
        holder();
        onclick();
        set_spinner_data();
        on_spinner_change_item();
        get_all_province();
        radio_listenr();
        radio_cost_change();
        radio_ejare_change();
        radio_vadeae_change();
        binddata();
        on_radio_change();

    }

    private void on_radio_change() {

        radio_tavafoghi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_cost_change();
                radio_tavafoghi.setChecked(true);
                til_cost.setHint("توافقی");
                edt_cost.setText("");
                edt_cost.setEnabled(false);

            }
        });
        radio_maghto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_cost_change();
                radio_maghto.setChecked(true);
                til_cost.setHint("قیمت کل ( تومان )");
                edt_cost.setText("");
                edt_cost.setEnabled(true);

            }
        });
        radio_moaveze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_cost_change();
                radio_moaveze.setChecked(true);
                til_cost.setHint("جهت معاوضه");
                edt_cost.setText("");
                edt_cost.setEnabled(false);
            }
        });
        radio_tavafoghi_ejare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_ejare_change();
                radio_tavafoghi_ejare.setChecked(true);
                til_ejare.setHint("توافقی");
                edt_ejare.setText("");
                edt_ejare.setEnabled(false);

            }
        });
        radio_maghto_ejare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_ejare_change();
                radio_maghto_ejare.setChecked(true);
                til_ejare.setHint("اجاره ( تومان )");
                edt_ejare.setText("");
                edt_ejare.setEnabled(true);

            }
        });
        radio_majani_ejare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_ejare_change();
                radio_majani_ejare.setChecked(true);
                til_ejare.setHint("مجانی");
                edt_ejare.setText("");
                edt_ejare.setEnabled(false);
            }
        });
        radio_tavafoghi_vadeae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_vadeae_change();
                radio_tavafoghi_vadeae.setChecked(true);
                til_vadeae.setHint("توافقی");
                edt_vadeae.setText("");
                edt_vadeae.setEnabled(false);

            }
        });
        radio_maghto_vadeae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_vadeae_change();
                radio_maghto_vadeae.setChecked(true);
                til_vadeae.setHint("اجاره ( تومان )");
                edt_vadeae.setText("");
                edt_vadeae.setEnabled(true);

            }
        });
        radio_majani_vadeae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_vadeae_change();
                radio_majani_vadeae.setChecked(true);
                til_vadeae.setHint("مجانی");
                edt_vadeae.setText("");
                edt_vadeae.setEnabled(false);
            }
        });


    }

    private void holder() {
        estates = (Estates) getIntent().getSerializableExtra("estates");

        scrollView = findViewById(R.id.scrollView);

        newAdMapOverlay = findViewById(R.id.newAdMapOverlay);
        card_pic1 = findViewById(R.id.card_pic1);
        card_pic2 = findViewById(R.id.card_pic2);
        card_pic3 = findViewById(R.id.card_pic3);
        card_pic4 = findViewById(R.id.card_pic4);
        card_pic5 = findViewById(R.id.card_pic5);

        radio_person = (RadioButton) findViewById(R.id.radio_person);
        radio_moshaver = (RadioButton) findViewById(R.id.radio_moshaver);
        radio_sell = (RadioButton) findViewById(R.id.radio_sell);
        radio_bye = (RadioButton) findViewById(R.id.radio_bye);
        radio_nist = (RadioButton) findViewById(R.id.radio_nist);
        radio_hast = (RadioButton) findViewById(R.id.radio_hast);
        radio_nadarad = (RadioButton) findViewById(R.id.radio_nadarad);
        radio_darad = (RadioButton) findViewById(R.id.radio_darad);
        radio_maghto = (RadioButton) findViewById(R.id.radio_maghto);
        radio_tavafoghi = (RadioButton) findViewById(R.id.radio_tavafoghi);
        radio_maghto_vadeae = (RadioButton) findViewById(R.id.radio_maghto_vadeae);
        radio_tavafoghi_vadeae = (RadioButton) findViewById(R.id.radio_tavafoghi_vadeae);
        radio_tavafoghi_ejare = (RadioButton) findViewById(R.id.radio_tavafoghi_ejare);
        radio_maghto_ejare = (RadioButton) findViewById(R.id.radio_maghto_ejare);
        radio_moaveze = (RadioButton) findViewById(R.id.radio_moaveze);
        radio_majani_ejare = (RadioButton) findViewById(R.id.radio_majani_ejare);
        radio_majani_vadeae = (RadioButton) findViewById(R.id.radio_majani_vadeae);

        radio_person.setChecked(true);
        radio_sell.setChecked(true);
        radio_nist.setChecked(true);
        radio_darad.setChecked(true);
        radio_tavafoghi.setChecked(true);
        radio_tavafoghi_vadeae.setChecked(true);
        radio_tavafoghi_ejare.setChecked(true);


        lin_room_number = (LinearLayout) findViewById(R.id.lin_room_number);
        lin_cost = (LinearLayout) findViewById(R.id.lin_cost);
        lin_vadeae = (LinearLayout) findViewById(R.id.lin_vadeae);
        lin_ejare = (LinearLayout) findViewById(R.id.lin_ejare);
        lin_remove_map_loc=findViewById(R.id.lin_remove_map_loc);
        lin_remove_map_loc.setVisibility(View.VISIBLE);

        card_area = (CardView) findViewById(R.id.card_area);
        card_type = (CardView) findViewById(R.id.card_type);
        card_type_person = (CardView) findViewById(R.id.card_type_person);
        card_sanad = (CardView) findViewById(R.id.card_sanad);

        til_ejare = (TextInputLayout) findViewById(R.id.til_ejare);
        til_vadeae = (TextInputLayout) findViewById(R.id.til_vadeae);
        til_cost = (TextInputLayout) findViewById(R.id.til_cost);
        til_meter = (TextInputLayout) findViewById(R.id.til_meter);

        edt_cost = (EditText) findViewById(R.id.edt_cost);
        edt_vadeae = (EditText) findViewById(R.id.edt_vadeae);
        edt_ejare = (EditText) findViewById(R.id.edt_ejare);
        edt_meter = (EditText) findViewById(R.id.edt_meter);
        edt_cost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edt_cost.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    edt_cost.setText(formattedString);
                    edt_cost.setSelection(edt_cost.getText().length());


                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                edt_cost.addTextChangedListener(this);
            }
        });
        edt_vadeae.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edt_vadeae.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    edt_vadeae.setText(formattedString);
                    edt_vadeae.setSelection(edt_vadeae.getText().length());


                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                edt_vadeae.addTextChangedListener(this);
            }
        });
        edt_ejare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edt_ejare.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    edt_ejare.setText(formattedString);
                    edt_ejare.setSelection(edt_ejare.getText().length());


                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                edt_ejare.addTextChangedListener(this);
            }
        });
        mapLayout = (RelativeLayout) findViewById(R.id.newAdMapLayout);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.newAdMap);
        supportMapFragment.getMapAsync(this);

        newAdSubmitBTN = (Button) findViewById(R.id.newAdSubmitBTN);

        newAdSpinnerLoadProgressBar = (ProgressBar) findViewById(R.id.newAdSpinnerLoadProgressBar);
        rootView = (ViewGroup) findViewById(R.id.newAd);

        provinces = new ArrayList<>();
        citys = new ArrayList<>();
        regions = new ArrayList<>();

        img_pic1 = (ImageView) findViewById(R.id.img_pic1);
        img_pic2 = (ImageView) findViewById(R.id.img_pic2);
        img_pic3 = (ImageView) findViewById(R.id.img_pic3);
        img_pic4 = (ImageView) findViewById(R.id.img_pic4);
        img_pic5 = (ImageView) findViewById(R.id.img_pic5);

        img_add_pic1 = (ImageView) findViewById(R.id.img_add_pic1);
        img_add_pic2 = (ImageView) findViewById(R.id.img_add_pic2);
        img_add_pic3 = (ImageView) findViewById(R.id.img_add_pic3);
        img_add_pic4 = (ImageView) findViewById(R.id.img_add_pic4);
        img_add_pic5 = (ImageView) findViewById(R.id.img_add_pic5);

        edt_title = (EditText) findViewById(R.id.edt_title);
        edt_dec = (EditText) findViewById(R.id.edt_dec);

        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_phone1 = (EditText) findViewById(R.id.edt_phone1);
        edt_phone2 = (EditText) findViewById(R.id.edt_phone2);

        edt_address = (EditText) findViewById(R.id.edt_address);

        spin_province = (Spinner) findViewById(R.id.spin_province);
        spin_city = (Spinner) findViewById(R.id.spin_city);
        spin_region = (Spinner) findViewById(R.id.spin_region);

        spin_sub_cat = (Spinner) findViewById(R.id.spin_sub_cat);
        spin_cat = (Spinner) findViewById(R.id.spin_cat);

        spin_room_number = (Spinner) findViewById(R.id.spin_room_number);

        newAdShowMap = (CheckBox) findViewById(R.id.newAdShowMap);
        newAdShowMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    TransitionManager.beginDelayedTransition(rootView);


                    if (isItNewAd) {
                        if (isMapReady) {
                            turnonlocation();
                        }
//
                    } else mapLayout.setVisibility(View.VISIBLE);

                } else {
                    TransitionManager.beginDelayedTransition(rootView);

                    mapLayout.setVisibility(View.GONE);
                }
            }
        });

        photo_to_delete = new ArrayList<>();

        newAdMapOverlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });

    }

    private void binddata() {
        if (!(estates.getLatitude() == null || estates.getLatitude().equals("null") || estates.getLatitude().equals(null) ||
                estates.getLatitude().equals(""))) {
            ltLg = new LatLng(Float.parseFloat(estates.getLatitude()), Float.parseFloat(estates.getLongitude()));

        }
        edt_title.setText(estates.getAds_title());
        edt_dec.setText(estates.getDescription());
        if (estates.getUser_type().equals("moshaver_amlak")) {
            radio_moshaver.setChecked(true);
        } else {
            radio_person.setChecked(true);
        }
        edt_name.setText(estates.getAds_owner_name());
        edt_address.setText(estates.getAddress());
        edt_phone1.setText(estates.getTelephone1());
        if (!(estates.getTelephone2() == null || estates.getTelephone2().equals("null") || estates.getTelephone2().equals(null) ||
                estates.getTelephone2().equals(""))) {
            edt_phone2.setText(estates.getTelephone2());
        }
        if (estates.getCategory_parent_id().equals("1")) {
            spin_cat.setSelection(1);
            if (estates.getPrice_kharid().equals("0")) {
                radio_tavafoghi.setChecked(true);
                edt_cost.setEnabled(false);
            } else if (estates.getPrice_kharid().equals("-1")) {
                radio_moaveze.setChecked(true);
                til_cost.setHint("جهت معاوضه");
                edt_cost.setEnabled(false);

            } else {
                radio_maghto.setChecked(true);
                edt_cost.setText(estates.getPrice_kharid());
                edt_cost.setEnabled(true);
            }
            edt_meter.setText(estates.getMeters());
            Log.v("room_num", estates.getRooms_count());
            if (estates.getRooms_count().equals("1")) {
                spin_room_number.setSelection(2);
            } else if (estates.getRooms_count().equals("0")) {
                spin_room_number.setSelection(1);
            }else if (estates.getRooms_count().equals("2")) {
                spin_room_number.setSelection(3);
            } else if (estates.getRooms_count().equals("3")) {
                spin_room_number.setSelection(4);
            } else if (estates.getRooms_count().equals("4")) {
                spin_room_number.setSelection(5);
            } else if (estates.getRooms_count().equals("5")) {
                spin_room_number.setSelection(6);
            }

            if (estates.getSell_or_buy().equals("buy")) {
                radio_bye.setChecked(true);
            } else {
                radio_sell.setChecked(true);
            }

            if (estates.getIs_in_hoome().equals("0")) {
                radio_nist.setChecked(true);
            } else {
                radio_hast.setChecked(true);
            }
        } else if (estates.getCategory_parent_id().equals("2")) {
            spin_cat.setSelection(2);
            edt_meter.setText(estates.getMeters());

            if (estates.getPre_pay_ejare().equals("0")) {
                radio_tavafoghi_vadeae.setChecked(true);
            } else if (estates.getPre_pay_ejare().equals("-1")) {
                til_vadeae.setHint("مجانی");
                radio_majani_vadeae.setChecked(true);
            } else {
                radio_maghto_vadeae.setChecked(true);
                edt_vadeae.setText(estates.getPre_pay_ejare());
                edt_vadeae.setEnabled(true);
            }
            if (estates.getMonthly_price_ejare().equals("0")) {
                radio_tavafoghi_ejare.setChecked(true);
            } else if (estates.getMonthly_price_ejare().equals("-1")) {
                til_ejare.setHint("مجانی");
                radio_majani_ejare.setChecked(true);
            } else {
                radio_maghto_ejare.setChecked(true);
                edt_ejare.setText(estates.getMonthly_price_ejare());
                edt_ejare.setEnabled(true);
            }

            Log.v("room_num", estates.getRooms_count());
            if (estates.getRooms_count().equals("1")) {
                spin_room_number.setSelection(2);
            } else if (estates.getRooms_count().equals("0")) {
                spin_room_number.setSelection(1);
            }else if (estates.getRooms_count().equals("2")) {
                spin_room_number.setSelection(3);
            } else if (estates.getRooms_count().equals("3")) {
                spin_room_number.setSelection(4);
            } else if (estates.getRooms_count().equals("4")) {
                spin_room_number.setSelection(5);
            } else if (estates.getRooms_count().equals("5")) {
                spin_room_number.setSelection(6);
            }

            if (estates.getSell_or_buy().equals("buy")) {
                radio_bye.setChecked(true);
            } else {
                radio_sell.setChecked(true);
            }

            if (estates.getIs_in_hoome().equals("0")) {
                radio_nist.setChecked(true);
            } else {
                radio_hast.setChecked(true);
            }

        } else if (estates.getCategory_parent_id().equals("3")) {
            spin_cat.setSelection(3);
            edt_meter.setText(estates.getMeters());

            if (estates.getPrice_kharid().equals("0")) {
                radio_tavafoghi.setChecked(true);
                edt_cost.setEnabled(false);
            } else if (estates.getPrice_kharid().equals("-1")) {
                radio_moaveze.setChecked(true);
                edt_cost.setText(estates.getPrice_kharid());
                edt_cost.setEnabled(false);

            } else {
                radio_maghto.setChecked(true);
                edt_cost.setText(estates.getPrice_kharid());
                edt_cost.setEnabled(true);
            }

            Log.v("room_num", estates.getRooms_count());
            if (estates.getRooms_count().equals("1")) {
                spin_room_number.setSelection(2);
            } else if (estates.getRooms_count().equals("0")) {
                spin_room_number.setSelection(1);
            }else if (estates.getRooms_count().equals("2")) {
                spin_room_number.setSelection(3);
            } else if (estates.getRooms_count().equals("3")) {
                spin_room_number.setSelection(4);
            } else if (estates.getRooms_count().equals("4")) {
                spin_room_number.setSelection(5);
            } else if (estates.getRooms_count().equals("5")) {
                spin_room_number.setSelection(6);
            }

            if (estates.getSell_or_buy().equals("buy")) {
                radio_bye.setChecked(true);
            } else {
                radio_sell.setChecked(true);
            }

            if (estates.getIs_in_hoome().equals("0")) {
                radio_nist.setChecked(true);
            } else {
                radio_hast.setChecked(true);
            }
            if (estates.getSanad_edari().equals("1")) {
                radio_darad.setChecked(true);

            } else {
                radio_nadarad.setChecked(true);

            }
        } else if (estates.getCategory_parent_id().equals("4")) {
            spin_cat.setSelection(4);
            edt_meter.setText(estates.getMeters());

            if (estates.getPre_pay_ejare().equals("0")) {
                radio_tavafoghi_vadeae.setChecked(true);
            } else if (estates.getPre_pay_ejare().equals("-1")) {
                radio_majani_vadeae.setChecked(true);
            } else {
                radio_maghto_vadeae.setChecked(true);
                edt_vadeae.setText(estates.getPre_pay_ejare());
                edt_vadeae.setEnabled(true);
            }
            if (estates.getMonthly_price_ejare().equals("0")) {
                radio_tavafoghi_ejare.setChecked(true);
            } else if (estates.getMonthly_price_ejare().equals("-1")) {
                radio_majani_ejare.setChecked(true);
            } else {
                radio_maghto_ejare.setChecked(true);
                edt_ejare.setText(estates.getMonthly_price_ejare());
                edt_ejare.setEnabled(true);
            }
            Log.v("room_num", estates.getRooms_count());
            if (estates.getRooms_count().equals("1")) {
                spin_room_number.setSelection(2);
            } else if (estates.getRooms_count().equals("0")) {
                spin_room_number.setSelection(1);
            }else if (estates.getRooms_count().equals("2")) {
                spin_room_number.setSelection(3);
            } else if (estates.getRooms_count().equals("3")) {
                spin_room_number.setSelection(4);
            } else if (estates.getRooms_count().equals("4")) {
                spin_room_number.setSelection(5);
            } else if (estates.getRooms_count().equals("5")) {
                spin_room_number.setSelection(6);
            }

//            if (Integer.parseInt(estates.getRooms_count())>=0 &&Integer.parseInt(estates.getRooms_count())<6){
//                spin_room_number.setSelection(Integer.parseInt(estates.getRooms_count()+1));
//            }

            if (estates.getSell_or_buy().equals("buy")) {
                radio_bye.setChecked(true);
            } else {
                radio_sell.setChecked(true);
            }

            if (estates.getIs_in_hoome().equals("0")) {
                radio_nist.setChecked(true);
            } else {
                radio_hast.setChecked(true);
            }

        } else if (estates.getCategory_parent_id().equals("5")) {
            spin_cat.setSelection(5);
        }
        if (!(estates.getThumbnail_photo() == null || estates.getThumbnail_photo().equals(null) ||
                estates.getThumbnail_photo().equals("") || estates.getThumbnail_photo().equals("null"))) {
            Picasso.with(Edit_Home_Add.this)
                    .load(estates.getThumbnail_photo())
                    .fit()
//                    .resizeDimen(16, 9)
//                    .centerCrop()
                    .placeholder(R.drawable.place_holder)
                    .into(img_pic1);

        }
        if (estates.getPhotosDatas().size() > 0) {
            List<ImageView> imageViews = new ArrayList<>();
            imageViews.add(img_pic2);
            imageViews.add(img_pic3);
            imageViews.add(img_pic4);
            imageViews.add(img_pic5);
            if (estates.getPhotosDatas().size() > 4) {
                for (int i = 0; i < 4; i++) {
                    Picasso.with(Edit_Home_Add.this)
                            .load(estates.getPhotosDatas().get(i).getName())
                            .fit()
                            .placeholder(R.drawable.place_holder)
                            .into(imageViews.get(i));
                }
            } else {
                for (int i = 0; i < estates.getPhotosDatas().size(); i++) {
                    Picasso.with(Edit_Home_Add.this)
                            .load(estates.getPhotosDatas().get(i).getName())
                            .fit()
                            .placeholder(R.drawable.place_holder)
                            .into(imageViews.get(i));
                }
            }
        }

    }

    private void radio_listenr() {
        radio_maghto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til_cost.setHint("قیمت کل ( تومان )");
                edt_cost.setText("");
                edt_cost.setEnabled(true);

            }
        });
        radio_tavafoghi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til_cost.setHint("توافقی");
                edt_cost.setText("");
                edt_cost.setEnabled(false);
            }
        });

        radio_maghto_vadeae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til_vadeae.setHint("قیمت کل ( تومان )");
                edt_vadeae.setText("");
                edt_vadeae.setEnabled(true);

            }
        });
        radio_tavafoghi_vadeae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til_vadeae.setHint("ودیعه ( توافقی )");
                edt_vadeae.setText("");
                edt_vadeae.setEnabled(false);
            }
        });
        radio_maghto_ejare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til_vadeae.setHint("قیمت کل ( تومان )");
                edt_vadeae.setText("");
                edt_vadeae.setEnabled(true);

            }
        });
        radio_tavafoghi_ejare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til_vadeae.setHint("اجاره ( توافقی )");
                edt_vadeae.setText("");
                edt_vadeae.setEnabled(false);
            }
        });
    }

    private void onclick() {
        lin_remove_map_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMap.clear();
                estates.setLatitude(null);
                estates.setLongitude(null);
                TransitionManager.beginDelayedTransition(rootView);
                ltLg=null;
//                mapLayout.setVisibility(View.GONE);
                newAdShowMap.setChecked(false);

                ShowToast.success("نشانه با موفقیت حذف گردید.", Edit_Home_Add.this);

            }
        });
        img_pic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection_pic = 1;
                pickImage();
            }
        });
        img_pic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmaps[0] != null || (!(estates.getThumbnail_photo() == null || estates.getThumbnail_photo().equals("null") ||
                        estates.getThumbnail_photo().equals("") || estates.getThumbnail_photo().equals(null)))) {
                    selection_pic = 2;
                    pickImage();
                } else {
                    ShowToast.failure("لطفا ابتدا تصویر پیش فرض را انتخاب نمایید.", Edit_Home_Add.this);


                }
            }
        });
        img_pic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmaps[1] != null || estates.getPhotosDatas().size() >= 1) {
                    selection_pic = 3;
                    pickImage();
                } else {
                    ShowToast.failure("لطفا ابتدا تصویر شماره 1 را انتخاب نمایید.", Edit_Home_Add.this);


                }
            }
        });
        img_pic4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmaps[2] != null || estates.getPhotosDatas().size() >= 2) {
                    selection_pic = 4;
                    pickImage();
                } else {
                    ShowToast.failure("لطفا ابتدا تصویر تصویر شماره 2 را انتخاب نمایید.", Edit_Home_Add.this);


                }
            }
        });
        img_pic5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmaps[3] != null || estates.getPhotosDatas().size() >= 3) {
                    selection_pic = 5;
                    pickImage();
                } else {


                }
            }
        });
        newAdSubmitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (valid()) {
                    doUpload();
                }
            }
        });
    }

    private void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
            requestPermission(Manifest.permission.CAMERA,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            boolean showCamera = true;
            MultiImageSelector selector = MultiImageSelector.create(Edit_Home_Add.this);
            selector.showCamera(showCamera);
            selector.single();
            selector.origin(mSelectPath);
            selector.start(Edit_Home_Add.this, REQUEST_IMAGE);
        }
    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Edit_Home_Add.this, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    String picpath;
    Bitmap bitmap1;
    Bitmap[] bitmaps = new Bitmap[5];

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                String picturePath;
                File imageFile;
                picturePath = mSelectPath.get(0).toString();
                imageFile = new File(picturePath);
                CropImage.activity(Uri.fromFile(imageFile))
                        .setAllowRotation(false)
                        .setFixAspectRatio(true)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                File myimageFile = new File(resultUri.toString());

                try {
                    Bitmap bitmapO = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);//
                    bitmap1 = bitmapO;
                    Bitmap bitmap = scaleDown(bitmapO, 500, true);

                    if (selection_pic == 1) {
                        img_pic1.setImageBitmap(bitmap);
                        bitmaps[0] = bitmap;
                        img_add_pic1.setVisibility(View.GONE);
                        if (!(estates.getThumbnail_photo() == null || estates.getThumbnail_photo().equals("null") ||
                                estates.getThumbnail_photo().equals(null) || estates.getThumbnail_photo().equals(""))) {
                            delete_thumbnail_photo = 1;
                        }
                        card_pic2.setCardBackgroundColor(Color.parseColor("#ffffff"));
                    } else if (selection_pic == 2) {
                        img_pic2.setImageBitmap(bitmap);
                        bitmaps[1] = bitmap;
                        img_add_pic2.setVisibility(View.GONE);
                        if (estates.getPhotosDatas().size() > 0) {
                            photo_to_delete.add(estates.getPhotosDatas().get(0).getId());
                        }
                        card_pic3.setCardBackgroundColor(Color.parseColor("#ffffff"));


                    } else if (selection_pic == 3) {
                        img_pic3.setImageBitmap(bitmap);
                        bitmaps[2] = bitmap;
                        img_add_pic3.setVisibility(View.GONE);
                        if (estates.getPhotosDatas().size() > 1) {
                            photo_to_delete.add(estates.getPhotosDatas().get(1).getId());
                        }
                        card_pic4.setCardBackgroundColor(Color.parseColor("#ffffff"));

                    } else if (selection_pic == 4) {
                        img_pic4.setImageBitmap(bitmap);
                        bitmaps[3] = bitmap;
                        img_add_pic4.setVisibility(View.GONE);
                        if (estates.getPhotosDatas().size() > 2) {
                            photo_to_delete.add(estates.getPhotosDatas().get(2).getId());
                        }
                        card_pic5.setCardBackgroundColor(Color.parseColor("#ffffff"));

                    } else if (selection_pic == 5) {
                        img_pic5.setImageBitmap(bitmap);
                        bitmaps[4] = bitmap;
                        img_add_pic5.setVisibility(View.GONE);
                        if (estates.getPhotosDatas().size() > 3) {
                            photo_to_delete.add(estates.getPhotosDatas().get(3).getId());
                        }
                        card_pic5.setCardBackgroundColor(Color.parseColor("#ffffff"));

                    }
                    picpath = myimageFile.toString();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void turnonlocation() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(Edit_Home_Add.this).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
//                        Log.i("abcd", "All location settings are satisfied.");


//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    if (provinceSpnr.getSelectedItemPosition() != 0) {
//                                        String location = provinceList.get(provinceSpnr.getSelectedItemPosition());
//                                        geoLocate(location);
//                                    }
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }, 500);
                        TransitionManager.beginDelayedTransition(rootView);

                        mapLayout.setVisibility(View.VISIBLE);

                        GPSTracker gpsTracker = new GPSTracker(Edit_Home_Add.this);
                        Location location2 = gpsTracker.getLocation();


                        if (location2 != null) {
//                            Log.i("11111111111111", "locationFound");
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location2.getLatitude(), location2.getLongitude()), 13));

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(location2.getLatitude(), location2.getLongitude()))      // Sets the center of the map to location user
                                    .zoom(15)                   // Sets the zoom
                                    .build();                   // Creates a CameraPosition from the builder
                            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                            Log.i("11111111111111", "locationFound");

                            //TODO check if this happens, then give permission to show noAdAroundWarn
                        }


//                        if (chbAddLoction.isChecked()) {
//                            mapLayout1.setVisibility(View.VISIBLE);
//                        } else {
//                            mapLayout1.setVisibility(View.GONE);
//                        }

                        if (ActivityCompat.checkSelfPermission(Edit_Home_Add.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Edit_Home_Add.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        } else
//                            gmap.setMyLocationEnabled(true);
                            break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        chbAddLoction.setChecked(false);
//                        Log.i("abcd", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            newAdShowMap.setChecked(false);
                            status.startResolutionForResult(Edit_Home_Add.this, 0x1);
                        } catch (IntentSender.SendIntentException e) {
//                            Log.i("abcd", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        Log.i("abcd", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    private void set_spinner_data() {
        cat = new ArrayList<>();
        sub_cat = new ArrayList<>();

        province = new ArrayList<>();
        city = new ArrayList<>();
        region = new ArrayList<>();

        room_num = new ArrayList<>();

        cat.add("لطفا انتخاب کنید.");
        cat.add("فروشی مسکونی (آپارتمان،خانه،زمین)");
        cat.add("اجاره مسکونی (آپارتمان،خانه،زمین)");
        cat.add("فروش اداری و تجاری (مغازه،دفتر،صنعتی)");
        cat.add("اجاره اداری و تجاری (مغازه،دفتر،صنعتی)");
        cat.add("خدمات املاک");

        sub_cat.add("لطفا انتخاب کنید.");

        province.add("لطفا انتخاب کنید.");
        city.add("لطفا انتخاب کنید.");
        region.add("لطفا انتخاب کنید.");

        room_num.add("لطفا انتخاب کنید.");
        room_num.add("بدون اتاق");
        room_num.add("یک");
        room_num.add("دو");
        room_num.add("سه");
        room_num.add("چهار");
        room_num.add("پنج یا بیشتر");


        ArrayAdapter<String> cat_Adapter = new ArrayAdapter<>(Edit_Home_Add.this, R.layout.item_spinner_layout, cat);
        ArrayAdapter<String> sub_cat_addapter = new ArrayAdapter<>(Edit_Home_Add.this, R.layout.item_spinner_layout, sub_cat);

        ArrayAdapter<String> room_num_addapter = new ArrayAdapter<>(Edit_Home_Add.this, R.layout.item_spinner_layout, room_num);

        ArrayAdapter<String> province_addapter = new ArrayAdapter<>(Edit_Home_Add.this, R.layout.item_spinner_layout, province);
        ArrayAdapter<String> city_addapter = new ArrayAdapter<>(Edit_Home_Add.this, R.layout.item_spinner_layout, city);
        ArrayAdapter<String> region_adapter = new ArrayAdapter<>(Edit_Home_Add.this, R.layout.item_spinner_layout, region);

        spin_cat.setAdapter(cat_Adapter);
        spin_sub_cat.setAdapter(sub_cat_addapter);

        spin_province.setAdapter(province_addapter);
        spin_city.setAdapter(city_addapter);
        spin_region.setAdapter(region_adapter);

        spin_room_number.setAdapter(room_num_addapter);

    }

    private void on_spinner_change_item() {
        spin_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position != 0) {
                    set_spin_sub_cat(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        spin_sub_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                set_cat_spin_change(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        spin_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position != 0) {
                    get_all_city(provinces.get(position - 1).getId());
                } else {
                    city = new ArrayList<>();
                    city.add("لطفا انتخاب کنید.");
                    ArrayAdapter<String> spin_city1 = new ArrayAdapter<>(Edit_Home_Add.this, R.layout.item_spinner_layout, city);
                    spin_city.setAdapter(spin_city1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        spin_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position != 0) {
                    get_all_region(citys.get(position - 1).getId());
                } else {
                    region = new ArrayList<>();
                    region.add("لطفا انتخاب کنید.");
                    ArrayAdapter<String> spin_region1 = new ArrayAdapter<>(Edit_Home_Add.this, R.layout.item_spinner_layout, region);
                    spin_region.setAdapter(spin_region1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void get_all_province() {
        newAdSpinnerLoadProgressBar.setVisibility(View.VISIBLE);

        String provinceUrl = StaticData.PROVINCE;
        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Edit_Home_Add.this);
        Get_Volley_Call_Back.Call_Volley(Edit_Home_Add.this, params, provinceUrl, Request.Method.GET, 128);
    }

    private void get_all_city(String id) {
        newAdSpinnerLoadProgressBar.setVisibility(View.VISIBLE);

        String url = StaticData.DOMAIN_WITH_API + "/provinces/" + id + "/cities";

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Edit_Home_Add.this);
        Get_Volley_Call_Back.Call_Volley(Edit_Home_Add.this, params, url, Request.Method.GET, 129);
    }

    private void get_all_region(String id) {
        newAdSpinnerLoadProgressBar.setVisibility(View.VISIBLE);

        String url = StaticData.get_all_region + id + "/regions";

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Edit_Home_Add.this);
        Get_Volley_Call_Back.Call_Volley(Edit_Home_Add.this, params, url, Request.Method.GET, 130);
    }

    private void set_spin_sub_cat(int position) {
        subcategory = new ArrayList<>();
        if (position == 1) {//home sell
            subcategory.add(new Home_Category("6", "آپارتمان", "1"));
            subcategory.add(new Home_Category("7", "خانه و ویلا", "1"));
            subcategory.add(new Home_Category("8", "زمین و کلنگی", "1"));

            lin_cost.setVisibility(View.VISIBLE);
            lin_ejare.setVisibility(View.GONE);
            lin_vadeae.setVisibility(View.GONE);
            card_area.setVisibility(View.VISIBLE);
            card_type.setVisibility(View.VISIBLE);
            lin_room_number.setVisibility(View.VISIBLE);
            til_meter.setVisibility(View.VISIBLE);
            card_sanad.setVisibility(View.GONE);
            radio_sell.setText("فروشی");

        } else if (position == 2) {// ejare home
            subcategory.add(new Home_Category("9", "آپارتمان", "2"));
            subcategory.add(new Home_Category("10", "خانه و ویلا", "2"));
            lin_cost.setVisibility(View.GONE);
            lin_ejare.setVisibility(View.VISIBLE);
            lin_vadeae.setVisibility(View.VISIBLE);
            card_area.setVisibility(View.VISIBLE);
            card_type.setVisibility(View.VISIBLE);
            lin_room_number.setVisibility(View.VISIBLE);
            til_meter.setVisibility(View.VISIBLE);
            card_sanad.setVisibility(View.GONE);
            radio_sell.setText("ارائه");


        } else if (position == 3) {//seall edari
            subcategory.add(new Home_Category("11", "دفترکار،اتاق اداری و مطب", "3"));
            subcategory.add(new Home_Category("12", "مغازه و غرفه", "3"));
            subcategory.add(new Home_Category("13", "صنعتی،کشاورزی وتجاری", "3"));
            lin_cost.setVisibility(View.VISIBLE);
            lin_ejare.setVisibility(View.GONE);
            lin_vadeae.setVisibility(View.GONE);
            card_area.setVisibility(View.VISIBLE);
            card_type.setVisibility(View.VISIBLE);
            lin_room_number.setVisibility(View.VISIBLE);
            til_meter.setVisibility(View.VISIBLE);
            card_sanad.setVisibility(View.VISIBLE);
            radio_sell.setText("فروشی");
        } else if (position == 4) {//ejare edari
            subcategory.add(new Home_Category("14", "دفترکار،اتاق اداری و مطب", "4"));
            subcategory.add(new Home_Category("15", "مغازه و غرفه", "4"));
            subcategory.add(new Home_Category("16", "صنعتی،کشاورزی وتجاری", "4"));
            lin_cost.setVisibility(View.GONE);
            lin_ejare.setVisibility(View.VISIBLE);
            lin_vadeae.setVisibility(View.VISIBLE);
            card_area.setVisibility(View.VISIBLE);
            card_type.setVisibility(View.VISIBLE);
            lin_room_number.setVisibility(View.VISIBLE);
            til_meter.setVisibility(View.VISIBLE);
            card_sanad.setVisibility(View.GONE);
            radio_sell.setText("ارائه");

        } else if (position == 5) {//ejare edari
            subcategory.add(new Home_Category("17", "آژانس املاک", "5"));
            subcategory.add(new Home_Category("18", "مشارکت در ساخت", "5"));
            subcategory.add(new Home_Category("19", "امور مالی و حقوقی", "5"));
            subcategory.add(new Home_Category("20", "پیش فروش", "5"));
            lin_cost.setVisibility(View.GONE);
            lin_ejare.setVisibility(View.GONE);
            lin_vadeae.setVisibility(View.GONE);
            card_area.setVisibility(View.GONE);
            card_type.setVisibility(View.GONE);
            lin_room_number.setVisibility(View.GONE);
            til_meter.setVisibility(View.GONE);
            card_sanad.setVisibility(View.GONE);
            card_sanad.setVisibility(View.GONE);
        }
        sub_cat = new ArrayList<>();
        sub_cat.add("لطفا انتخاب نمایید.");
        for (int i = 0; i < subcategory.size(); i++) {
            sub_cat.add(subcategory.get(i).getName());
        }
        ArrayAdapter<String> sub_cat_addapter = new ArrayAdapter<>(Edit_Home_Add.this, R.layout.item_spinner_layout, sub_cat);
        spin_sub_cat.setAdapter(sub_cat_addapter);
        int position11 = 0;
        if (!sub_cat_change) {
            for (int i = 0; i < subcategory.size(); i++) {
                if (subcategory.get(i).getId().equals(estates.getCategory_id())) {
                    Log.v("subcat", subcategory.get(i).getId() + " ==" + estates.getCategory_id() + i);
                    position11 = i + 1;
                    i = subcategory.size();
                }
            }
        }
        spin_sub_cat.setSelection(position11);
        sub_cat_change = true;
    }

    private void set_cat_spin_change(int posion) {
        if (spin_cat.getSelectedItemPosition() == 1 && spin_sub_cat.getSelectedItemPosition() == 3) {
            lin_room_number.setVisibility(View.GONE);
        } else if (spin_cat.getSelectedItemPosition() != 5) {
            lin_room_number.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        isMapReady = true;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        googleMap.setMyLocationEnabled(true);

        this.googleMap = googleMap;
//        if (isItNewAd) {
//            LatLng ll = new LatLng(32.539245, 53.816336);
//            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 4.5f);
//            googleMap.moveCamera(update);
//        }

        if (estates.getLatitude().equals("null") || estates.getLatitude().equals("") || estates.getLatitude() == null) {
        } else {
//            mapLayout.setVisibility(View.VISIBLE);
            newAdShowMap.setChecked(true);
            LatLng ll = new LatLng(Double.parseDouble(estates.getLatitude()), Double.parseDouble(estates.getLongitude()));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            googleMap.moveCamera(update);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(ll);
            markerOptions.title(estates.getAddress());
            googleMap.clear();
            googleMap.addMarker(markerOptions);
        }

        addMarkerWithClick();
    }

    private void addMarkerWithClick() {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                ltLg = latLng;

                // Creating a Marker
                MarkerOptions markerOptions = new MarkerOptions();

                //setting the position or the marker
                markerOptions.position(latLng);

                //setting the title for the marker
                markerOptions.title("موقعیت شما");

                //clearing the previous marker
                googleMap.clear();

                //animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                //adding new marker
                googleMap.addMarker(markerOptions);
            }
        });
    }

    @Override
    public void on_volley_response(String response, int id) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");
            if (id == 128) {
                if (status.equals("200")) {
                    provinces = ProvicesAndCities.cities(jsonObject);
                    set_spin_province();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Edit_Home_Add.this);
                    Edit_Home_Add.this.finish();

                }
                newAdSpinnerLoadProgressBar.setVisibility(View.GONE);

            }
            if (id == 129) {
                if (status.equals("200")) {
                    citys = ProvicesAndCities.cities(jsonObject);
                    set_spin_citys();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Edit_Home_Add.this);
                    Edit_Home_Add.this.finish();

                }
                newAdSpinnerLoadProgressBar.setVisibility(View.GONE);

            }
            if (id == 130) {
                if (status.equals("200")) {
                    regions = ProvicesAndCities.cities(jsonObject);
                    set_spin_rejon();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Edit_Home_Add.this);
                    Edit_Home_Add.this.finish();

                }
                newAdSpinnerLoadProgressBar.setVisibility(View.GONE);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void set_spin_province() {
        int position = 0;
        province = new ArrayList<>();
        province.add("لطفا انتخاب کنید.");
        for (int i = 0; i < provinces.size(); i++) {
            province.add(provinces.get(i).getName());
            if (!province_change && provinces.get(i).getId().equals(estates.getProvince_id())) {
                position = i + 1;
            }
        }
        ArrayAdapter<String> spin_provinces = new ArrayAdapter<>(Edit_Home_Add.this, R.layout.item_spinner_layout, province);
        spin_province.setAdapter(spin_provinces);

        spin_province.setSelection(position);


    }

    private void set_spin_citys() {
        city = new ArrayList<>();
        int posion = 0;
        city.add("لطفا انتخاب کنید.");
        for (int i = 0; i < citys.size(); i++) {
            city.add(citys.get(i).getName());
            if (!city_change && citys.get(i).getId().equals(estates.getCity_id())) {
                posion = i + 1;
            }
        }
        ArrayAdapter<String> spin_city1 = new ArrayAdapter<>(Edit_Home_Add.this, R.layout.item_spinner_layout, city);
        spin_city.setAdapter(spin_city1);
        city_change = true;
        spin_city.setSelection(posion);

    }

    private void set_spin_rejon() {
        int posion = 0;

        region = new ArrayList<>();
        region.add("لطفا انتخاب کنید.");
        for (int i = 0; i < regions.size(); i++) {
            region.add(regions.get(i).getName());
            if (!region_change && regions.get(i).getId().equals(estates.getRegion_id())) {
                posion = i + 1;
            }
        }
        ArrayAdapter<String> spin_rejon1 = new ArrayAdapter<>(Edit_Home_Add.this, R.layout.item_spinner_layout, region);
        spin_region.setAdapter(spin_rejon1);
        spin_region.setSelection(posion);
//        Log.v("position",posion+"");
        region_change = true;

    }


    private boolean valid() {
        int i = 0;
        String message = "";
        if (i == 0 && edt_title.getText().length() == 0) {
            message = "لطفا در وارد کردن عنوان دقت فرمایید.";
            i++;
        } else if (i == 0 && edt_dec.getText().length() == 0) {
            message = "لطفا در وارد کردن توضیحات دفت فرمایید.";
            i++;
        } else if (i == 0 && spin_cat.getSelectedItemPosition() == 0) {
            message = "لطفا در انتخاب دسته بندی دقت فرمایید";
            i++;
        } else if (i == 0 && spin_sub_cat.getSelectedItemPosition() == 0) {
            message = "لطفا در انتخاب زیر دسته دقت فرمایید";
            i++;
        }
        if (i == 0 && (spin_cat.getSelectedItemPosition() == 1 || spin_cat.getSelectedItemPosition() == 3)) {

            if (radio_maghto.isChecked() && edt_cost.getText().length() == 0) {
                i++;
                message = "لطفا در وارد نمودن قیمت کل دقت فرمایید.";
            } else if (i == 0 && edt_meter.getText().length() == 0) {
                i++;
                message = "لطفا در وارد نمودن متراژ دقت نمایید.";
            }
            if (i == 0 && spin_cat.getSelectedItemPosition() == 1 && spin_sub_cat.getSelectedItemPosition() == 3) {

            } else if (i == 0 && spin_room_number.getSelectedItemPosition() == 0) {
                i++;
                message = "لطفا در انتخاب تعداد اتاق دقت نمایید.";

            }
        } else if (i == 0 && (spin_cat.getSelectedItemPosition() == 2 || spin_cat.getSelectedItemPosition() == 4)) {
            if (i == 0 && radio_maghto_vadeae.isChecked() && edt_vadeae.getText().length() == 0) {
                i++;
                message = "لطفا در وارد نمودن مبلغ ودیعه دقت فرمایید.";
            } else if (i == 0 && radio_maghto_ejare.isChecked() && edt_ejare.getText().length() == 0) {
                i++;
                message = "لطفا در وارد نمودن مبلغ اجاره دقت فرمایید.";
            } else if (i == 0 && spin_room_number.getSelectedItemPosition() == 0) {
                i++;
                message = "لطفا در انتخاب تعداد اتاق دقت نمایید.";
            } else if (i == 0 && edt_meter.getText().length() == 0) {
                i++;
                message = "لطفا در وارد نمودن متراژ دقت نمایید.";
            }
        }
        if (i == 0 && edt_name.getText().length() == 0) {
            message = "لطفا در وارد نمودن نام و نام خانوادگی دقت فرمایید.";
            i++;
        } else if (i == 0 && edt_phone1.getText().length() != 11) {
            message = "لطفا در وارد نمودن شماره تماس 1 دقت فرمایید دقت فرمایید.";
            i++;
        } else if (i == 0 && (edt_phone2.getText().length() != 0 && edt_phone2.getText().length() != 11)) {
            message = "لطفا در وارد نمودن شماره تماس 2  دقت فرمایید دقت فرمایید.";
            i++;
        } else if (i == 0 && spin_province.getSelectedItemPosition() == 0) {
            message = "لطفا در انتخاب نام استان دقت فرمایید";
            i++;
        } else if (i == 0 && spin_city.getSelectedItemPosition() == 0) {
            message = "لطفا در انتخاب نام شهر دقت فرمایید";
            i++;
        } else if (i == 0 && spin_region.getSelectedItemPosition() == 0) {
            message = "لطفا در انتخاب نام منطقه دقت فرمایید";
            i++;
        } else if (i == 0 && edt_address.getText().length() == 0) {
            message = "لطفا در وارد نمودن آدرس دقت فرمایید";
            i++;
        }


        if (i > 0) {
            ShowToast.failure(message, Edit_Home_Add.this);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }


    private void doUpload() {
        final ProgressDialog prgDialog = new ProgressDialog(Edit_Home_Add.this);
        prgDialog.setMessage("درحال بارگذاری اطلاعات...");
        prgDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {

                String handleInserUrl;

                handleInserUrl = StaticData.estates + "/ads/" + estates.getId() + "/update";
                Log.v("handleInserUrl", handleInserUrl);

                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(handleInserUrl);
                    MultipartEntity reqEntity = new MultipartEntity();

                    String token = new UserSessionManager(Edit_Home_Add.this).getLoginToken();
                    Log.v("token", token);
//                    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjI3LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvZ2hvbGxhYy9wdWJsaWNfaHRtbC9hcGkvbG9naW4iLCJpYXQiOjE1MDgyMjI4MjQsImV4cCI6MTUwODgyNzYyNCwibmJmIjoxNTA4MjIyODI0LCJqdGkiOiJZcDhZWDhxZ3BhY1ZLYmJrIn0.2QojAogqfRL_TK8BYzh9mtL45UOwoGLJyUd--9aMClQ";
                    post.addHeader("Authorization", "Bearer " + token);

                    reqEntity.addPart("ads_title", new StringBody(edt_title.getText().toString(), "text/plain", Charset.forName("UTF-8")));
                    Log.v("params", "ads_title:" + edt_title.getText().toString());
                    reqEntity.addPart("description", new StringBody(edt_dec.getText().toString(), "text/plain", Charset.forName("UTF-8")));
                    Log.v("params", "description:" + edt_dec.getText().toString());

                    reqEntity.addPart("category_id", new StringBody(subcategory.get(spin_sub_cat.getSelectedItemPosition() - 1).getId(), "text/plain", Charset.forName("UTF-8")));
                    Log.v("params", "category_id:" + subcategory.get(spin_sub_cat.getSelectedItemPosition() - 1).getId());
//alaki fild
                    reqEntity.addPart("ejare_or_kharid", new StringBody("kharid", "text/plain", Charset.forName("UTF-8")));
                    Log.v("params", "ejare_or_kharid:" + "kharid");
                    reqEntity.addPart("type_karbari", new StringBody("maskooni", "text/plain", Charset.forName("UTF-8")));
                    Log.v("params", "type_karbari:" + "maskooni");
                    /// etmam

                    if (radio_person.isChecked()) {
                        reqEntity.addPart("user_type", new StringBody("person", "text/plain", Charset.forName("UTF-8")));
                        Log.v("params", "user_type:" + "person");

                    } else {
                        reqEntity.addPart("user_type", new StringBody("moshaver_amlak", "text/plain", Charset.forName("UTF-8")));
                        Log.v("params", "user_type:" + "moshaver_amlak");

                    }
                    reqEntity.addPart("ads_owner_name", new StringBody(edt_name.getText().toString(), "text/plain", Charset.forName("UTF-8")));
                    Log.v("params", "ads_owner_name:" + edt_name.getText().toString());

                    reqEntity.addPart("telephone1", new StringBody(edt_phone1.getText().toString(), "text/plain", Charset.forName("UTF-8")));
                    Log.v("params", "telephone1:" + edt_phone1.getText().toString());

                    if (edt_phone2.getText().length() > 0) {
                        reqEntity.addPart("telephone2", new StringBody(edt_phone2.getText().toString(), "text/plain", Charset.forName("UTF-8")));
                        Log.v("params", "telephone2:" + edt_phone2.getText().toString());

                    }

                    reqEntity.addPart("region_id", new StringBody(regions.get(spin_region.getSelectedItemPosition() - 1).getId(), "text/plain", Charset.forName("UTF-8")));
                    Log.v("params", "region_id:" + regions.get(spin_region.getSelectedItemPosition() - 1).getId());

                    reqEntity.addPart("address", new StringBody(edt_address.getText().toString(), "text/plain", Charset.forName("UTF-8")));
                    Log.v("params", "address:" + edt_address.getText().toString());

                    if (ltLg != null) {
                        reqEntity.addPart("latitude", new StringBody(ltLg.latitude + "", "text/plain", Charset.forName("UTF-8")));
                        Log.v("params", "latitude:" + ltLg.latitude + "");
                        reqEntity.addPart("longitude", new StringBody(ltLg.longitude + "", "text/plain", Charset.forName("UTF-8")));
                        Log.v("params", "longitude:" + ltLg.longitude + "");
                    }
                    if (radio_sell.isChecked()) {
                        reqEntity.addPart("sell_or_buy", new StringBody("sell", "text/plain", Charset.forName("UTF-8")));
                        Log.v("params", "sell_or_buy:" + "sell");
                    } else {
                        reqEntity.addPart("sell_or_buy", new StringBody("buy", "text/plain", Charset.forName("UTF-8")));
                        Log.v("params", "sell_or_buy:" + "buy");
                    }
                    if (spin_cat.getSelectedItemPosition() == 1) {
                        if (radio_tavafoghi.isChecked()) {
                            reqEntity.addPart("price_kharid", new StringBody("0", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "price_kharid:" + "0");
                        } else if (radio_maghto.isChecked()) {
                            reqEntity.addPart("price_kharid", new StringBody(edt_cost.getText().toString().replaceAll(",", ""), "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "price_kharid:" + edt_cost.getText().toString().replaceAll(",", ""));
                        } else if (radio_moaveze.isChecked()) {
                            reqEntity.addPart("price_kharid", new StringBody("-1", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "price_kharid:" + "-1");
                        }
                        if (spin_sub_cat.getSelectedItemPosition()==3){
                            reqEntity.addPart("rooms_count", new StringBody("0", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "rooms_count:" + "0");

                        }else{
                            reqEntity.addPart("rooms_count", new StringBody(String.valueOf(spin_room_number.getSelectedItemPosition() - 1), "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "rooms_count:" + edt_cost.getText().toString());

                        }

                        reqEntity.addPart("meters", new StringBody(edt_meter.getText().toString(), "text/plain", Charset.forName("UTF-8")));
                        Log.v("params", "meters:" + edt_meter.getText().toString());

                        if (radio_hast.isChecked()) {
                            reqEntity.addPart("is_in_hoome", new StringBody("1", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "is_in_hoome:" + "1");
                        } else {
                            reqEntity.addPart("is_in_hoome", new StringBody("0", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "is_in_hoome:" + "0");
                        }


                    } else if (spin_cat.getSelectedItemPosition() == 2 || spin_cat.getSelectedItemPosition() == 4) {
                        reqEntity.addPart("rooms_count", new StringBody(String.valueOf(spin_room_number.getSelectedItemPosition() - 1), "text/plain", Charset.forName("UTF-8")));
                        Log.v("params", "rooms_count:" + edt_cost.getText().toString());

                        reqEntity.addPart("meters", new StringBody(edt_meter.getText().toString(), "text/plain", Charset.forName("UTF-8")));
                        Log.v("params", "meters:" + edt_meter.getText().toString());

                        if (radio_hast.isChecked()) {
                            reqEntity.addPart("is_in_hoome", new StringBody("1", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "is_in_hoome:" + "1");
                        } else {
                            reqEntity.addPart("is_in_hoome", new StringBody("0", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "is_in_hoome:" + "0");
                        }
                        if (radio_tavafoghi_vadeae.isChecked()) {
                            reqEntity.addPart("pre_pay_ejare", new StringBody("0", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "pre_pay_ejare:" + "0");
                        } else if (radio_maghto_vadeae.isChecked()) {
                            reqEntity.addPart("pre_pay_ejare", new StringBody(edt_vadeae.getText().toString().replaceAll(",", ""), "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "pre_pay_ejare:" + edt_vadeae.getText().toString());
                        } else if (radio_majani_vadeae.isChecked()) {
                            reqEntity.addPart("pre_pay_ejare", new StringBody("-1", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "pre_pay_ejare:" + "-1");
                        }
                        if (radio_tavafoghi_ejare.isChecked()) {
                            reqEntity.addPart("monthly_price_ejare", new StringBody("0", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "monthly_price_ejare:" + "0");
                        } else if (radio_maghto_ejare.isChecked()) {
                            reqEntity.addPart("monthly_price_ejare", new StringBody(edt_ejare.getText().toString().replaceAll(",", ""), "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "monthly_price_ejare:" + edt_ejare.getText().toString());
                        } else if (radio_majani_ejare.isChecked()) {
                            reqEntity.addPart("monthly_price_ejare", new StringBody("-1", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "monthly_price_ejare:" + "-1");
                        }

                    } else if (spin_cat.getSelectedItemPosition() == 3) {
                        if (radio_tavafoghi.isChecked()) {
                            reqEntity.addPart("price_kharid", new StringBody("0", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "price_kharid:" + "0");
                        } else if (radio_maghto.isChecked()) {
                            reqEntity.addPart("price_kharid", new StringBody(edt_cost.getText().toString().replaceAll(",", ""), "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "price_kharid:" + edt_cost.getText().toString().replaceAll(",", ""));
                        } else if (radio_moaveze.isChecked()) {
                            reqEntity.addPart("price_kharid", new StringBody("-1", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "price_kharid:" + "-1");
                        }
                        reqEntity.addPart("rooms_count", new StringBody(String.valueOf(spin_room_number.getSelectedItemPosition() - 1), "text/plain", Charset.forName("UTF-8")));
                        Log.v("params", "rooms_count:" + String.valueOf(spin_room_number.getSelectedItemPosition() - 1));

                        reqEntity.addPart("meters", new StringBody(edt_meter.getText().toString(), "text/plain", Charset.forName("UTF-8")));
                        Log.v("params", "meters:" + edt_meter.getText().toString());

                        if (radio_darad.isChecked()) {
                            reqEntity.addPart("sanad_edari", new StringBody("1", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "sanad_edari:" + "1");
                        } else {
                            reqEntity.addPart("sanad_edari", new StringBody("0", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "sanad_edari:" + "0");
                        }

                        if (radio_hast.isChecked()) {
                            reqEntity.addPart("is_in_hoome", new StringBody("1", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "is_in_hoome:" + "1");
                        } else {
                            reqEntity.addPart("is_in_hoome", new StringBody("0", "text/plain", Charset.forName("UTF-8")));
                            Log.v("params", "is_in_hoome:" + "0");
                        }


                    }else{
                        reqEntity.addPart("meters", new StringBody("0", "text/plain", Charset.forName("UTF-8")));
                        Log.v("params", "meters:" + "0");

                    }


                    for (int i = 1; i < bitmaps.length; i++) {
                        long time = System.currentTimeMillis();
                        if (bitmaps[i] != null) {
                            String pathTemp = Compress_image.reductImageSize(time + ".jpg", bitmaps[i]);
                            reqEntity.addPart("photos[]", new FileBody(new File(pathTemp)));
                            Log.v("params", "photos[]:" + time + ".jpg");

                        }
                    }
                    if (bitmaps[0] != null) {
                        long time = System.currentTimeMillis();

                        String pathTemp = Compress_image.reductImageSize(time + ".jpg", bitmaps[0]);
                        reqEntity.addPart("thumbnail_photo", new FileBody(new File(pathTemp)));
                        Log.v("params", "thumbnail_photo:" + time + ".jpg");

                    }
                    for (int i = 0; i < photo_to_delete.size(); i++) {
                        reqEntity.addPart("delete_photo[]", new StringBody(photo_to_delete.get(i), "text/plain", Charset.forName("UTF-8")));

                    }
                    if (delete_thumbnail_photo == 1) {
                        reqEntity.addPart("delete_thumbnail_photo ", new StringBody("1", "text/plain", Charset.forName("UTF-8")));

                    }
//


                    post.setEntity(reqEntity);
                    HttpResponse response = client.execute(post);
                    HttpEntity resEntity = response.getEntity();
                    final String response_str = EntityUtils.toString(resEntity);
                    if (resEntity != null) {
//                        Log.i("RESPONSE", "-> " + response_str);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    prgDialog.dismiss();
//                                    descriptionEdt.setText(response_str);
                                    JSONObject Jobj = new JSONObject(response_str);
                                    if (Jobj.has("status")) {
                                        if (Jobj.getString("status").equalsIgnoreCase("200")) {

//                                            Toast.makeText(Add_New_Home_Add.this, "ثبت داده ها با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
                                            ShowToast.success("آگهی شما با موفقیت ثبت گردید و پس از تایید مدیر بنمایش گذاشته می شود", Edit_Home_Add.this);
//                                            startActivity(new Intent(Add_New_Home_Add.this,DashboardActivity.class));
                                            Edit_Home_Add.this.finish();

                                        } else if (Jobj.getString("status").equals("401")) {
                                            if (Jobj.getString("error").equals("token_expired")) {
                                                UserHelper.RemoveUserInfo(Edit_Home_Add.this);
                                                Intent intent = new Intent(Edit_Home_Add.this, LoginActivity.class);
                                                ShowToast.failure("لطفا دوباره وارد حساب خود شوید", Edit_Home_Add.this);
                                                startActivity(intent);
                                            }
                                        } else {
                                            ShowToast.failure("در ثبت آگهی به مشکل برخوردیم لطفا مجددا تلاش فرمایید.", Edit_Home_Add.this);
//                                            Add_New_Home_Add.this.finish();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception ex) {
                    Log.e("Debug", "error: " + ex.getMessage(), ex);
                }
            }
        }).start();
    }

    private void radio_cost_change() {
        radio_moaveze.setChecked(false);
        radio_maghto.setChecked(false);
        radio_tavafoghi.setChecked(false);
    }

    private void radio_ejare_change() {
        radio_majani_ejare.setChecked(false);
        radio_maghto_ejare.setChecked(false);
        radio_tavafoghi_ejare.setChecked(false);
    }

    private void radio_vadeae_change() {
        radio_majani_vadeae.setChecked(false);
        radio_maghto_vadeae.setChecked(false);
        radio_tavafoghi_vadeae.setChecked(false);
    }
}
