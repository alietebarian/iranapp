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
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.transition.TransitionManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import com.ideabonyan.iranapp.Fragment.Dialogs.Show_Rouls_Dialog;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Models.Brand;
import com.ideabonyan.iranapp.Models.Modell;
import com.ideabonyan.iranapp.Models.ProvicesAndCities;
import com.ideabonyan.iranapp.Models.Specialities;
import com.ideabonyan.iranapp.BuildConfig;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Compress_image;
import com.ideabonyan.iranapp.Utils.GPSTracker;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;

import static com.ideabonyan.iranapp.Activity.NewAdActivity.scaleDown;

public class Add_New_Job extends AppCompatActivity implements OnMapReadyCallback, Get_Insert_Edit_Data {
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private static final int REQUEST_IMAGE = 2;
    public static boolean isItNewAd = true;
    Spinner spin_agremment_type, spin_specialty, spin_education_level, spin_type;
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
    List<String> list_agremment_type, list_specialty, list_ducation_level, list_type;
    List<String> province, city, region;
    LinearLayout lin_brand, lin_shasi, lin_modell, lin_motore_weghit;
    TextInputLayout til_year, til_kilometer;
    ProgressBar newAdSpinnerLoadProgressBar;
    List<Brand> brands;
    List<Modell> modells;
    List<com.ideabonyan.iranapp.Models.cylinder_volumes> cylinder_volumes;
    List<ProvicesAndCities> provinces, citys, regions;
    CheckBox newAdShowMap;
    SupportMapFragment supportMapFragment;
    Button newAdSubmitBTN;
    List<Specialities> specialitiesList;
    CardView card_pic5, card_pic4, card_pic3, card_pic2, card_pic1;
    RadioButton radio_person, radio_company;
    ScrollView scrollView;
    ImageView newAdMapOverlay;
    LinearLayout lin_accept_roul;
    CheckBox chk_accept_roul;
    View txt_show_rouls;
    String picpath;
    Bitmap bitmap1;
    Bitmap[] bitmaps = new Bitmap[5];
    int REQUEST_ID_MULTIPLE_PERMISSIONS1 = 1001;
    private ArrayList<String> mSelectPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__new__job);
        holder();
        set_spinner_data();
        on_spinner_change_item();
        get_all_specialty();
        get_all_province();
        onclick();
    }

    private void onclick() {
        txt_show_rouls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Show_Rouls_Dialog show_rouls_dialog = new Show_Rouls_Dialog("قوانین", getString(R.string.rouls));
                show_rouls_dialog.show(getSupportFragmentManager(), "show_rouls_dialog");
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
                if (bitmaps[0] == null) {

                } else {
                    selection_pic = 2;
                    pickImage();
                }
            }
        });
        img_pic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmaps[1] == null) {

                } else {
                    selection_pic = 3;
                    pickImage();
                }
            }
        });
        img_pic4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmaps[2] == null) {

                } else {
                    selection_pic = 4;
                    pickImage();
                }
            }
        });
        img_pic5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmaps[3] == null) {

                } else {
                    selection_pic = 5;
                    pickImage();
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

    private void holder() {
        lin_accept_roul = findViewById(R.id.lin_accept_roul);
        lin_accept_roul.setVisibility(View.VISIBLE);
        chk_accept_roul = findViewById(R.id.chk_accept_roul);
        txt_show_rouls = findViewById(R.id.txt_show_rouls);

        scrollView = findViewById(R.id.scrollView);

        newAdMapOverlay = findViewById(R.id.newAdMapOverlay);
        card_pic1 = findViewById(R.id.card_pic1);
        card_pic2 = findViewById(R.id.card_pic2);
        card_pic3 = findViewById(R.id.card_pic3);
        card_pic4 = findViewById(R.id.card_pic4);
        card_pic5 = findViewById(R.id.card_pic5);

        radio_company = (RadioButton) findViewById(R.id.radio_company);
        radio_person = (RadioButton) findViewById(R.id.radio_person);

        radio_person.setChecked(true);

        mapLayout = (RelativeLayout) findViewById(R.id.newAdMapLayout);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.newAdMap);
        supportMapFragment.getMapAsync(this);

        newAdSubmitBTN = (Button) findViewById(R.id.newAdSubmitBTN);

        newAdSpinnerLoadProgressBar = (ProgressBar) findViewById(R.id.newAdSpinnerLoadProgressBar);
        rootView = (ViewGroup) findViewById(R.id.newAd);

        provinces = new ArrayList<>();
        citys = new ArrayList<>();
        regions = new ArrayList<>();


        til_kilometer = (TextInputLayout) findViewById(R.id.til_kilometer);
        til_year = (TextInputLayout) findViewById(R.id.til_year);

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


        spin_type = (Spinner) findViewById(R.id.spin_type);
        spin_education_level = (Spinner) findViewById(R.id.spin_education_level);
        spin_specialty = (Spinner) findViewById(R.id.spin_specialty);
        spin_agremment_type = (Spinner) findViewById(R.id.spin_agremment_type);

        spin_province = (Spinner) findViewById(R.id.spin_province);
        spin_city = (Spinner) findViewById(R.id.spin_city);
        spin_region = (Spinner) findViewById(R.id.spin_region);

        lin_motore_weghit = (LinearLayout) findViewById(R.id.lin_motore_weghit);
        lin_modell = (LinearLayout) findViewById(R.id.lin_modell);
        lin_shasi = (LinearLayout) findViewById(R.id.lin_shasi);
        lin_brand = (LinearLayout) findViewById(R.id.lin_brand);

        newAdShowMap = (CheckBox) findViewById(R.id.newAdShowMap);
        newAdShowMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    TransitionManager.beginDelayedTransition(rootView);


                    if (isItNewAd) {
                        if (isMapReady) {
                            if (checkAndRequestPermissions1(false)) {
                                turnonlocation();

                            } else {
//                                AlertDialog.Builder b = new AlertDialog.Builder(Add_New_Job.this);
//
//                                b.setMessage("برای مکان یابی خودکار باید دسترسی به مکان یاب را بدهید .")
//                                        .setPositiveButton("باشه", new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                checkAndRequestPermissions1(true);
//                                            }
//                                        });
//                                AlertDialog a = b.create();
//
//                                a.show();
//
//                                Button bq = a.getButton(DialogInterface.BUTTON_NEGATIVE);
//                                Button bq1 = a.getButton(DialogInterface.BUTTON_POSITIVE);
//                                bq1.setTextColor(getResources().getColor(R.color.colorPrimary));
//                                bq1.setTextSize(16);
                            }
                        }
//
                    } else mapLayout.setVisibility(View.VISIBLE);

                } else {
                    TransitionManager.beginDelayedTransition(rootView);

                    mapLayout.setVisibility(View.GONE);
                }
            }
        });
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

    private void set_spinner_data() {
        list_type = new ArrayList<>();
        list_ducation_level = new ArrayList<>();
        list_specialty = new ArrayList<>();
        list_agremment_type = new ArrayList<>();

        province = new ArrayList<>();
        city = new ArrayList<>();
        region = new ArrayList<>();

        list_type.add("لطفا انتخاب کنید.");
        list_type.add("استخدام");
        list_type.add("آماده به کار");

        list_ducation_level.add("لطفا انتخاب کنید.");
        list_ducation_level.add("زیر دیپلم");
        list_ducation_level.add("دیپلم");
        list_ducation_level.add("کاردانی");
        list_ducation_level.add("کارشناسی");
        list_ducation_level.add("کارشناسی ارشد");
        list_ducation_level.add("دکترا");


        list_specialty.add("لطفا انتخاب کنید.");
        list_agremment_type.add("لطفا انتخاب کنید.");
        list_agremment_type.add("تمام وقت");
        list_agremment_type.add("پاره وقت");
        list_agremment_type.add("مشاوره ای");
        list_agremment_type.add("پروژه ای");

        province.add("لطفا انتخاب کنید.");
        city.add("لطفا انتخاب کنید.");
        region.add("لطفا انتخاب کنید.");


        ArrayAdapter<String> type_Adapter = new ArrayAdapter<>(Add_New_Job.this, R.layout.item_spinner_layout, list_type);
        ArrayAdapter<String> ducation_level_addapter = new ArrayAdapter<>(Add_New_Job.this, R.layout.item_spinner_layout, list_ducation_level);
        ArrayAdapter<String> specialty_addapter = new ArrayAdapter<>(Add_New_Job.this, R.layout.item_spinner_layout, list_specialty);
        ArrayAdapter<String> agremment_type_addapter = new ArrayAdapter<>(Add_New_Job.this, R.layout.item_spinner_layout, list_agremment_type);

        ArrayAdapter<String> province_addapter = new ArrayAdapter<>(Add_New_Job.this, R.layout.item_spinner_layout, province);
        ArrayAdapter<String> city_addapter = new ArrayAdapter<>(Add_New_Job.this, R.layout.item_spinner_layout, city);
        ArrayAdapter<String> region_adapter = new ArrayAdapter<>(Add_New_Job.this, R.layout.item_spinner_layout, region);

        spin_type.setAdapter(type_Adapter);
        spin_education_level.setAdapter(ducation_level_addapter);
        spin_specialty.setAdapter(specialty_addapter);
        spin_agremment_type.setAdapter(agremment_type_addapter);

        spin_province.setAdapter(province_addapter);
        spin_city.setAdapter(city_addapter);
        spin_region.setAdapter(region_adapter);

    }

    private void on_spinner_change_item() {
        spin_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position != 0) {
                    get_all_city(provinces.get(position - 1).getId());
                } else {
                    city = new ArrayList<>();
                    city.add("لطفا انتخاب کنید.");
                    ArrayAdapter<String> spin_city1 = new ArrayAdapter<>(Add_New_Job.this, R.layout.item_spinner_layout, city);
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
                    ArrayAdapter<String> spin_region1 = new ArrayAdapter<>(Add_New_Job.this, R.layout.item_spinner_layout, region);
                    spin_region.setAdapter(spin_region1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void get_all_specialty() {
        newAdSpinnerLoadProgressBar.setVisibility(View.VISIBLE);
        String url = StaticData.employs + "/specialities";
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(Add_New_Job.this);
        Get_Volley_Call_Back.Call_Volley(Add_New_Job.this, params, url, Request.Method.GET, 136);
    }

    private void get_all_province() {
        newAdSpinnerLoadProgressBar.setVisibility(View.VISIBLE);

        String provinceUrl = StaticData.PROVINCE;
        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Add_New_Job.this);
        Get_Volley_Call_Back.Call_Volley(Add_New_Job.this, params, provinceUrl, Request.Method.GET, 128);
    }

    private void get_all_city(String id) {
        newAdSpinnerLoadProgressBar.setVisibility(View.VISIBLE);

        String url = StaticData.DOMAIN_WITH_API + "/provinces/" + id + "/cities";

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Add_New_Job.this);
        Get_Volley_Call_Back.Call_Volley(Add_New_Job.this, params, url, Request.Method.GET, 129);
    }

    private void get_all_region(String id) {
        newAdSpinnerLoadProgressBar.setVisibility(View.VISIBLE);

        String url = StaticData.get_all_region + id + "/regions";

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Add_New_Job.this);
        Get_Volley_Call_Back.Call_Volley(Add_New_Job.this, params, url, Request.Method.GET, 130);
    }

    @Override
    public void on_volley_response(String response, int id) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");
            if (id == 136) {
                if (status.equals("200")) {
                    specialitiesList = Specialities.Import(jsonObject.getJSONArray("list"));
                    set_spin_specialis();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Add_New_Job.this);
                    Add_New_Job.this.finish();

                }
                newAdSpinnerLoadProgressBar.setVisibility(View.GONE);

            }
            if (id == 128) {
                if (status.equals("200")) {
                    provinces = ProvicesAndCities.cities(jsonObject);
                    set_spin_province();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Add_New_Job.this);
                    Add_New_Job.this.finish();

                }
                newAdSpinnerLoadProgressBar.setVisibility(View.GONE);

            }
            if (id == 129) {
                if (status.equals("200")) {
                    citys = ProvicesAndCities.cities(jsonObject);
                    set_spin_citys();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Add_New_Job.this);
                    Add_New_Job.this.finish();

                }
                newAdSpinnerLoadProgressBar.setVisibility(View.GONE);

            }
            if (id == 130) {
                if (status.equals("200")) {
                    regions = ProvicesAndCities.cities(jsonObject);
                    set_spin_rejon();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Add_New_Job.this);
                    Add_New_Job.this.finish();

                }
                newAdSpinnerLoadProgressBar.setVisibility(View.GONE);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void set_spin_specialis() {
        list_specialty = new ArrayList<>();
        list_specialty.add("لطفا انتخاب کنید.");
        for (int i = 0; i < specialitiesList.size(); i++) {
            list_specialty.add(specialitiesList.get(i).getName());
        }
        ArrayAdapter<String> specialty_addapter = new ArrayAdapter<>(Add_New_Job.this, R.layout.item_spinner_layout, list_specialty);
        spin_specialty.setAdapter(specialty_addapter);

    }

    @Override
    public void on_volley_error(VolleyError error, int id) {
        ShowToast.failure("در ازتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Add_New_Job.this);
        Add_New_Job.this.finish();
    }

    private void set_spin_province() {
        province = new ArrayList<>();
        province.add("لطفا انتخاب کنید.");
        for (int i = 0; i < provinces.size(); i++) {
            province.add(provinces.get(i).getName());
        }
        ArrayAdapter<String> spin_provinces = new ArrayAdapter<>(Add_New_Job.this, R.layout.item_spinner_layout, province);
        spin_province.setAdapter(spin_provinces);

    }

    private void set_spin_citys() {
        city = new ArrayList<>();
        city.add("لطفا انتخاب کنید.");
        for (int i = 0; i < citys.size(); i++) {
            city.add(citys.get(i).getName());
        }
        ArrayAdapter<String> spin_city1 = new ArrayAdapter<>(Add_New_Job.this, R.layout.item_spinner_layout, city);
        spin_city.setAdapter(spin_city1);

    }

    private void set_spin_rejon() {
        region = new ArrayList<>();
        region.add("لطفا انتخاب کنید.");
        for (int i = 0; i < regions.size(); i++) {
            region.add(regions.get(i).getName());
        }
        ArrayAdapter<String> spin_rejon1 = new ArrayAdapter<>(Add_New_Job.this, R.layout.item_spinner_layout, region);
        spin_region.setAdapter(spin_rejon1);

    }

    public void turnonlocation() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(Add_New_Job.this).addApi(LocationServices.API).build();
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

                        GPSTracker gpsTracker = new GPSTracker(Add_New_Job.this);
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

                        if (ActivityCompat.checkSelfPermission(Add_New_Job.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Add_New_Job.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        } else
//                            gmap.setMyLocationEnabled(true);
                            break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        chbAddLoction.setChecked(false);
//                        Log.i("abcd", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            newAdShowMap.setChecked(false);
                            status.startResolutionForResult(Add_New_Job.this, 0x1);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        isMapReady = true;
        if (checkAndRequestPermissions1(false)) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
        }

        this.googleMap = googleMap;
        if (isItNewAd) {
            LatLng ll = new LatLng(32.539245, 53.816336);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 4.5f);
            googleMap.moveCamera(update);
        }
//        else {
//            if (!ad.getLatitude().equals("null")) {
//                showMap.setChecked(true);
//
//                LatLng ll = new LatLng(Double.parseDouble(ad.getLatitude()), Double.parseDouble(ad.getLongitude()));
//                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
//                googleMap.moveCamera(update);
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(ll);
//                markerOptions.title(ad.getAddress());
//                googleMap.clear();
//                googleMap.addMarker(markerOptions);
//            } else {
//                LatLng ll = new LatLng(32.539245, 53.816336);
//                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 4.5f);
//                googleMap.moveCamera(update);
//            }
//        }

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

    private boolean valid() {
        if (edt_title.getText().length() == 0) {
            ShowToast.failure("لطفا در وارد کردن عنوان آگهی دقت فرمایید", Add_New_Job.this);
            return false;
        } else if (edt_dec.getText().length() == 0) {
            ShowToast.failure("لطفا در وارد نمودن توضیحات آگهی دقت نمایید.", Add_New_Job.this);
            return false;
        } else if (spin_type.getSelectedItemPosition() == 0) {
            ShowToast.failure("لطفا در انتخاب نوع آگهی دقت فرمایید", Add_New_Job.this);
            return false;

        } else if (spin_education_level.getSelectedItemPosition() == 0) {
            ShowToast.failure("لطفا در انتخاب میزان تحصیلات دقت فرمایید", Add_New_Job.this);
            return false;
        } else if (spin_specialty.getSelectedItemPosition() == 0) {
            ShowToast.failure("لطفا در انتخاب تخصص دقت فرمایید", Add_New_Job.this);
            return false;
        } else if (spin_agremment_type.getSelectedItemPosition() == 0) {
            ShowToast.failure("لطفا در انتخاب نوع قرارداد دقت فرمایید.", Add_New_Job.this);
            return false;
        } else if (edt_name.getText().length() == 0) {
            ShowToast.failure("لطفا در وارد کردن نام و نام خوانوادگی دقت فرمایید دقت فرمایید", Add_New_Job.this);
            return false;
        } else if (edt_phone1.getText().length() != 11) {
            ShowToast.failure("لطفا در وارد کردن شماره تلفن 1 دقت فرمایید دقت فرمایید", Add_New_Job.this);
            return false;
        } else if (edt_phone2.getText().length() != 0 && edt_phone2.getText().length() != 11) {
            ShowToast.failure("لطفا در وارد کردن شماره تلفن 2 دقت فرمایید دقت فرمایید", Add_New_Job.this);
            return false;
        } else if (spin_province.getSelectedItemPosition() == 0) {
            ShowToast.failure("لطفا در انتخاب استان دقت فرمایید", Add_New_Job.this);
            return false;
        } else if (spin_city.getSelectedItemPosition() == 0) {
            ShowToast.failure("لطفا در انتخاب شهر دقت فرمایید", Add_New_Job.this);
            return false;
        } else if (spin_region.getSelectedItemPosition() == 0) {
            ShowToast.failure("لطفا در انتخاب منطقه دقت فرمایید", Add_New_Job.this);
            return false;
        } else if (edt_address.getText().length() == 0) {
            ShowToast.failure("لطفا در وارد نمودن آدرس خود دقت فرمایید", Add_New_Job.this);
            return false;
        } else if (!chk_accept_roul.isChecked()) {
            ShowToast.failure("برای ثبت آگهی باید با قوانین و مقررات موافقت کنید", Add_New_Job.this);
            return false;
        } else {
            return true;
        }
    }

    private void pickImage() {
        if (!checkAndRequestPermissions(false)) {
        } else {
            Log.v("permission", "no");
            boolean showCamera = true;
            MultiImageSelector selector = MultiImageSelector.create(Add_New_Job.this);
            selector.showCamera(showCamera);
            selector.single();
            selector.origin(mSelectPath);
            selector.start(Add_New_Job.this, REQUEST_IMAGE);
        }
    }

    private boolean checkAndRequestPermissions(boolean showAlartDialog) {

        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int read_external_storeg = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (read_external_storeg != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            if (showAlartDialog) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new
                        String[listPermissionsNeeded.size()]), REQUEST_STORAGE_READ_ACCESS_PERMISSION);
            } else {
                AlertDialog.Builder b = new AlertDialog.Builder(Add_New_Job.this);

                b.setMessage("برای اضافه کردن تصاویر باید اجازه دسترسی به فایل ها را بدهید")
                        .setPositiveButton("باشه", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                checkAndRequestPermissions(true);
                            }
                        });
                AlertDialog a = b.create();

                a.show();

                Button bq = a.getButton(DialogInterface.BUTTON_NEGATIVE);
                Button bq1 = a.getButton(DialogInterface.BUTTON_POSITIVE);
                bq1.setTextColor(getResources().getColor(R.color.colorPrimary));
                bq1.setTextSize(16);
            }
            return false;

        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new
                    String[listPermissionsNeeded.size()]), REQUEST_STORAGE_READ_ACCESS_PERMISSION);
            return false;
        }
        return true;
    }

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

                Uri resultUri = result.getUriContent();
                File myimageFile = new File(resultUri.toString());

                try {
                    Bitmap bitmapO = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);//
                    bitmap1 = bitmapO;
                    Bitmap bitmap = scaleDown(bitmapO, 500, true);

                    if (selection_pic == 1) {
                        img_pic1.setImageBitmap(bitmap);
                        bitmaps[0] = bitmap;
                        img_add_pic1.setVisibility(View.GONE);
                        card_pic2.setCardBackgroundColor(Color.parseColor("#ffffff"));
                    } else if (selection_pic == 2) {
                        img_pic2.setImageBitmap(bitmap);
                        bitmaps[1] = bitmap;
                        img_add_pic2.setVisibility(View.GONE);
                        card_pic3.setCardBackgroundColor(Color.parseColor("#ffffff"));

                    } else if (selection_pic == 3) {
                        img_pic3.setImageBitmap(bitmap);
                        bitmaps[2] = bitmap;
                        img_add_pic3.setVisibility(View.GONE);
                        card_pic4.setCardBackgroundColor(Color.parseColor("#ffffff"));

                    } else if (selection_pic == 4) {
                        img_pic4.setImageBitmap(bitmap);
                        bitmaps[3] = bitmap;
                        img_add_pic4.setVisibility(View.GONE);
                        card_pic5.setCardBackgroundColor(Color.parseColor("#ffffff"));

                    } else if (selection_pic == 5) {
                        img_pic5.setImageBitmap(bitmap);
                        bitmaps[4] = bitmap;
                        img_add_pic5.setVisibility(View.GONE);
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

    private void doUpload() {
        final ProgressDialog prgDialog = new ProgressDialog(Add_New_Job.this);
        prgDialog.setMessage("درحال بارگذاری اطلاعات...");
        prgDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {

                String handleInserUrl;

                handleInserUrl = StaticData.employs;


                try {
                    okhttp3.MultipartBody.Builder reqEntity = new okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM);

                    String token = new UserSessionManager(Add_New_Job.this).getLoginToken();

                    // Legal record of the consent the user gave before submitting.
                    reqEntity.addFormDataPart("terms_accepted", chk_accept_roul.isChecked() ? "1" : "0");
                    reqEntity.addFormDataPart("terms_version", StaticData.TERMS_VERSION);
                    reqEntity.addFormDataPart("app_version", BuildConfig.VERSION_NAME);
//                    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjI3LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvZ2hvbGxhYy9wdWJsaWNfaHRtbC9hcGkvbG9naW4iLCJpYXQiOjE1MDgyMjI4MjQsImV4cCI6MTUwODgyNzYyNCwibmJmIjoxNTA4MjIyODI0LCJqdGkiOiJZcDhZWDhxZ3BhY1ZLYmJrIn0.2QojAogqfRL_TK8BYzh9mtL45UOwoGLJyUd--9aMClQ";
                    if (radio_person.isChecked()) {
                        reqEntity.addFormDataPart("person_or_company", "person");
                        Log.v("person_or_company", "person");
                    } else {
                        reqEntity.addFormDataPart("person_or_company", "company");
                        Log.v("person_or_company", "company");
                    }
                    reqEntity.addFormDataPart("ads_title", edt_title.getText().toString());
                    Log.v("params", "ads_title:" + edt_title.getText().toString());
                    reqEntity.addFormDataPart("description", edt_dec.getText().toString());
                    Log.v("params", "description:" + edt_dec.getText().toString());


                    reqEntity.addFormDataPart("region_id", regions.get(spin_region.getSelectedItemPosition() - 1).getId());
                    Log.v("params", "region_id:" + regions.get(spin_region.getSelectedItemPosition() - 1).getId());

                    reqEntity.addFormDataPart("address", edt_address.getText().toString());
                    Log.v("params", "address:" + edt_address.getText().toString());


                    reqEntity.addFormDataPart("telephone1", edt_phone1.getText().toString());
                    Log.v("params", "telephone1:" + edt_phone1.getText().toString());

                    if (edt_phone2.getText().length() > 0) {
                        reqEntity.addFormDataPart("telephone2", edt_phone2.getText().toString());
                        Log.v("params", "telephone2:" + edt_phone2.getText().toString());

                    }
                    reqEntity.addFormDataPart("ads_owner_name", edt_name.getText().toString());
                    Log.v("params", "ads_owner_name:" + edt_name.getText().toString());

                    reqEntity.addFormDataPart("specialty", specialitiesList.get(spin_specialty.getSelectedItemPosition() - 1).getId());
                    Log.v("params", "specialty:" + specialitiesList.get(spin_specialty.getSelectedItemPosition() - 1).getId());


                    if (ltLg != null) {
                        reqEntity.addFormDataPart("latitude", ltLg.latitude + "");
                        Log.v("params", "latitude:" + ltLg.latitude + "");
                        reqEntity.addFormDataPart("longitude", ltLg.longitude + "");
                        Log.v("params", "longitude:" + ltLg.longitude + "");
                    }

//                    reqEntity.addFormDataPart("ads_owner_name", specialitiesList.get(spin_specialty.getSelectedItemPosition() - 1).getId());

                    if (spin_agremment_type.getSelectedItemPosition() == 1) {
                        reqEntity.addFormDataPart("agremment_type", "tamamvaght");
                    } else if (spin_agremment_type.getSelectedItemPosition() == 2) {
                        reqEntity.addFormDataPart("agremment_type", "parevaght");
                    } else if (spin_agremment_type.getSelectedItemPosition() == 3) {
                        reqEntity.addFormDataPart("agremment_type", "moshaveri");
                    } else if (spin_agremment_type.getSelectedItemPosition() == 4) {
                        reqEntity.addFormDataPart("agremment_type", "projei");
                    }

                    if (spin_education_level.getSelectedItemPosition() == 1) {
                        reqEntity.addFormDataPart("education_level", "underdiploma");
                    } else if (spin_education_level.getSelectedItemPosition() == 2) {
                        reqEntity.addFormDataPart("education_level", "diploma");
                    } else if (spin_education_level.getSelectedItemPosition() == 3) {
                        reqEntity.addFormDataPart("education_level", "tact");
                    } else if (spin_education_level.getSelectedItemPosition() == 4) {
                        reqEntity.addFormDataPart("education_level", "expertise");
                    } else if (spin_education_level.getSelectedItemPosition() == 5) {
                        reqEntity.addFormDataPart("education_level", "masterdegree");
                    } else if (spin_education_level.getSelectedItemPosition() == 6) {
                        reqEntity.addFormDataPart("education_level", "doctoral");
                    }

                    if (spin_type.getSelectedItemPosition() == 1) {
                        reqEntity.addFormDataPart("type", "forsatshoghli");
                    } else {
                        reqEntity.addFormDataPart("type", "karjoo");

                    }

                    for (int i = 1; i < bitmaps.length; i++) {
                        long time = System.currentTimeMillis();
                        if (bitmaps[i] != null) {
                            String pathTemp = Compress_image.reductImageSize(time + ".jpg", bitmaps[i]);
                            reqEntity.addFormDataPart("photos[]", new File(pathTemp).getName(), okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), new File(pathTemp)));
                            Log.v("params", "photos[]:" + time + ".jpg");

                        }
                    }
                    if (bitmaps[0] != null) {
                        long time = System.currentTimeMillis();

                        String pathTemp = Compress_image.reductImageSize(time + ".jpg", bitmaps[0]);
                        reqEntity.addFormDataPart("thumbnail_photo", new File(pathTemp).getName(), okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), new File(pathTemp)));
                        Log.v("params", "thumbnail_photo:" + time + ".jpg");

                    }
//


                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(handleInserUrl)
                            .header("Authorization", "Bearer " + token)
                            .post(reqEntity.build())
                            .build();
                    okhttp3.Response response = new okhttp3.OkHttpClient().newCall(request).execute();
                    okhttp3.ResponseBody resEntity = response.body();
                    final String response_str = resEntity != null ? resEntity.string() : "";
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

//                                            Toast.makeText(Add_New_Job.this, "ثبت داده ها با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
                                            ShowToast.success("آگهی شما با موفقیت ثبت گردید و پس از تایید مدیر بنمایش گذاشته می شود", Add_New_Job.this);
//                                            startActivity(new Intent(Add_New_Job.this,DashboardActivity.class));
                                            Add_New_Job.this.finish();

                                        } else if (Jobj.getString("status").equals("401")) {
                                            if (Jobj.getString("error").equals("token_expired")) {
                                                UserHelper.RemoveUserInfo(Add_New_Job.this);
                                                Intent intent = new Intent(Add_New_Job.this, LoginActivity.class);
                                                ShowToast.failure("لطفا دوباره وارد حساب خود شوید", Add_New_Job.this);
                                                startActivity(intent);
                                            }
                                        } else {
                                            ShowToast.failure("در ثبت آگهی به مشکل برخوردیم لطفا مجددا تلاش فرمایید.", Add_New_Job.this);
//                                            Add_New_Job.this.finish();
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

    private boolean checkAndRequestPermissions1(boolean showAlartDialog) {

        int ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            if (showAlartDialog) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new
                        String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS1);
            } else {
                AlertDialog.Builder b = new AlertDialog.Builder(Add_New_Job.this);

                b.setMessage("برای مکان یابی خودکار باید دسترسی به مکان یاب را بدهید .")
                        .setPositiveButton("باشه", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                checkAndRequestPermissions1(true);
                            }
                        });
                AlertDialog a = b.create();

                a.show();

                Button bq = a.getButton(DialogInterface.BUTTON_NEGATIVE);
                Button bq1 = a.getButton(DialogInterface.BUTTON_POSITIVE);
                bq1.setTextColor(getResources().getColor(R.color.colorPrimary));
                bq1.setTextSize(16);
            }
            return false;

        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    ((grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) || grantResults.length == 1)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                newAdShowMap.setChecked(true);
                turnonlocation();

            } else {
                ShowToast.failure("شما اجازه دسترسی به موقعیت خود را به این برنامه نداده اید", Add_New_Job.this);
                newAdShowMap.setChecked(false);

            }
        } else if (requestCode == REQUEST_STORAGE_READ_ACCESS_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    ((grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) || grantResults.length == 1)) {
                boolean showCamera = true;
                MultiImageSelector selector = MultiImageSelector.create(Add_New_Job.this);
                selector.showCamera(showCamera);
                selector.single();
                selector.origin(mSelectPath);
                selector.start(Add_New_Job.this, REQUEST_IMAGE);
            } else {
                ShowToast.failure("شما اجازه دسترسی به حافظه و دوربین را به این برنامه نداده اید", Add_New_Job.this);
            }
        }

    }
}
