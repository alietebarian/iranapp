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
import com.google.android.material.textfield.TextInputLayout;
import androidx.transition.TransitionManager;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import com.ideabonyan.iranapp.Models.Brand;
import com.ideabonyan.iranapp.Models.Modell;
import com.ideabonyan.iranapp.Models.ProvicesAndCities;
import com.ideabonyan.iranapp.Models.Vehicles;
import com.ideabonyan.iranapp.Models.cylinder_volumes;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Compress_image;
import com.ideabonyan.iranapp.Utils.GPSTracker;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;
import com.squareup.picasso.Picasso;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;

import static com.ideabonyan.iranapp.Activity.NewAdActivity.scaleDown;


public class Edite_Car_Add extends AppCompatActivity implements Get_Insert_Edit_Data, OnMapReadyCallback {
    public static boolean isItNewAd = true;
    boolean isMapReady = false;
    ViewGroup rootView;
    GoogleMap googleMap;
    LatLng ltLg;
    RelativeLayout mapLayout;
    int selection_pic = 0;
    ImageView img_pic1, img_pic2, img_pic3, img_pic4, img_pic5;
    ImageView img_add_pic1, img_add_pic2, img_add_pic3, img_add_pic4, img_add_pic5;
    EditText edt_dec, edt_title;
    EditText edt_kilometr, edt_year, edt_cost;
    EditText edt_address;
    EditText edt_phone2, edt_phone1, edt_name;
    Spinner spin_shasi, spin_modell, spin_cat, spin_motor_weghit, spin_brand;
    Spinner spin_region, spin_city, spin_province;
    List<String> cat, moddel, brand, shasi, motore_weghit;
    List<String> province, city, region;
    LinearLayout lin_brand, lin_shasi, lin_modell, lin_motore_weghit;
    TextInputLayout til_year, til_kilometer, til_cost;
    ProgressBar newAdSpinnerLoadProgressBar;
    List<Brand> brands;
    List<Modell> modells;
    List<cylinder_volumes> cylinder_volumes;
    List<ProvicesAndCities> provinces, citys, regions;
    CheckBox newAdShowMap;
    SupportMapFragment supportMapFragment;
    private final int REQUEST_IMAGE = 2;
    protected final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private ArrayList<String> mSelectPath;
    Button newAdSubmitBTN;
    RadioButton radio_old, radio_new;
    RadioButton radio_tavafoghi, radio_maghto;
    public Vehicles vehicles;
    boolean cat_change = false, brand_change = false, modell_change = false,
            province_change = false, city_change = false, region_change = false, cyland_change = false;
    int image_size = 0;
    int delete_thumbnail_photo = 0;
    List<String> photo_to_delete;
    CardView card_pic5, card_pic4, card_pic3, card_pic2, card_pic1;
    RadioButton radio_person, radio_company;
    ScrollView scrollView;
    ImageView newAdMapOverlay;
    LinearLayout lin_remove_map_loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__new__car__add);
        holder();
        set_spinner_data();
        on_spinner_change_item();
        get_all_brand();
        get_all_province();
        get_all_cilander_volum();
        onclick();
        binddata();
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
    }

    private void onclick() {
        lin_remove_map_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMap.clear();
                vehicles.setLatitude(null);
                vehicles.setLongitude(null);
                TransitionManager.beginDelayedTransition(rootView);
                ltLg=null;
                newAdShowMap.setChecked(false);

                ShowToast.success("نشانه با موفقیت حذف گردید.", Edite_Car_Add.this);

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
                if (bitmaps[0] != null || (!(vehicles.getThumbnail_photo() == null || vehicles.getThumbnail_photo().equals("null") ||
                        vehicles.getThumbnail_photo().equals("") || vehicles.getThumbnail_photo().equals(null)))) {
                    selection_pic = 2;
                    pickImage();
                } else {


                }
            }
        });
        img_pic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmaps[1] != null || vehicles.getPhotosDatas().size() >= 1) {
                    selection_pic = 3;
                    pickImage();
                } else {


                }
            }
        });
        img_pic4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmaps[2] != null || vehicles.getPhotosDatas().size() >= 2) {
                    selection_pic = 4;
                    pickImage();
                } else {


                }
            }
        });
        img_pic5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmaps[3] != null || vehicles.getPhotosDatas().size() >= 3) {
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

    private void binddata() {
        if (!(vehicles.getLatitude() == null || vehicles.getLatitude().equals("null") || vehicles.getLatitude().equals(null) ||
                vehicles.getLatitude().equals(""))) {
            ltLg = new LatLng(Float.parseFloat(vehicles.getLatitude()), Float.parseFloat(vehicles.getLongitude()));

        }
        if (vehicles.getPrice().equals("0")) {
            radio_tavafoghi.setChecked(true);
            edt_cost.setEnabled(false);


        } else {
            radio_maghto.setChecked(true);
            edt_cost.setText(vehicles.getPrice());
            edt_cost.setEnabled(true);

        }

        radio_listenr();

        photo_to_delete = new ArrayList<>();
        edt_title.setText(vehicles.getAds_title());
        edt_dec.setText(vehicles.getDescription());
        edt_name.setText(vehicles.getAds_owner_name());
        edt_phone1.setText(vehicles.getTelephone1());
        edt_address.setText(vehicles.getAddress());
        if (!(vehicles.getTelephone2() == null || vehicles.getTelephone2().equals("null") || vehicles.getTelephone2().equals(null)
                || vehicles.getTelephone2().equals(""))) {
            edt_phone2.setText(vehicles.getTelephone2());
        }
        if (vehicles.getNeworold().equals("new")) {
            radio_new.setChecked(true);
        } else {
            radio_old.setChecked(true);
        }
        if (vehicles.getType().equals("khodro")) {
            spin_cat.setSelection(1);
            if (vehicles.getChassis_type().equals("savari")) {
                spin_shasi.setSelection(1);
            } else if (vehicles.getChassis_type().equals("hachback")) {
                spin_shasi.setSelection(2);
            } else if (vehicles.getChassis_type().equals("shasiboland")) {
                spin_shasi.setSelection(3);
            } else if (vehicles.getChassis_type().equals("vanet")) {
                spin_shasi.setSelection(4);
            } else if (vehicles.getChassis_type().equals("krook")) {
                spin_shasi.setSelection(5);
            } else if (vehicles.getChassis_type().equals("van")) {
                spin_shasi.setSelection(6);
            } else if (vehicles.getChassis_type().equals("cupe")) {
                spin_shasi.setSelection(7);
            } else if (vehicles.getChassis_type().equals("station")) {
                spin_shasi.setSelection(8);
            } else if (vehicles.getChassis_type().equals("other")) {
                spin_shasi.setSelection(9);
            }
            edt_kilometr.setText(vehicles.getKilometre());
            edt_year.setText(vehicles.getProduction_year());
        } else if (vehicles.getType().equals("motorcycle")) {
            spin_cat.setSelection(2);
            edt_year.setText(vehicles.getProduction_year());

        } else if (vehicles.getType().equals("khodroclasic")) {
            spin_cat.setSelection(3);

        } else if (vehicles.getType().equals("khordrosorn")) {
            spin_cat.setSelection(4);


        } else if (vehicles.getType().equals("lavazem")) {
            spin_cat.setSelection(5);

        } else if (vehicles.getType().equals("other")) {
            spin_cat.setSelection(6);

        }

        if (!(vehicles.getThumbnail_photo() == null || vehicles.getThumbnail_photo().equals(null) ||
                vehicles.getThumbnail_photo().equals("") || vehicles.getThumbnail_photo().equals("null"))) {
            Picasso.get()
                    .load(vehicles.getThumbnail_photo())
                    .fit()
//                    .resizeDimen(16, 9)
//                    .centerCrop()
                    .placeholder(R.drawable.place_holder)
                    .into(img_pic1);

        }
        if (vehicles.getPhotosDatas().size() > 0) {
            List<ImageView> imageViews = new ArrayList<>();
            imageViews.add(img_pic2);
            imageViews.add(img_pic3);
            imageViews.add(img_pic4);
            imageViews.add(img_pic5);
            if (vehicles.getPhotosDatas().size() > 4) {
                for (int i = 0; i < 4; i++) {
                    Picasso.get()
                            .load(vehicles.getPhotosDatas().get(i).getName())
                            .fit()
                            .placeholder(R.drawable.place_holder)
                            .into(imageViews.get(i));
                }
            } else {
                for (int i = 0; i < vehicles.getPhotosDatas().size(); i++) {
                    Picasso.get()
                            .load(vehicles.getPhotosDatas().get(i).getName())
                            .fit()
                            .placeholder(R.drawable.place_holder)
                            .into(imageViews.get(i));
                }
            }
        }

        if (vehicles.getPerson_or_company().equals("company")) {
            radio_company.setChecked(true);
        } else {
            radio_person.setChecked(true);

        }


    }

    private void holder() {
        lin_remove_map_loc=findViewById(R.id.lin_remove_map_loc);
        lin_remove_map_loc.setVisibility(View.VISIBLE);

        vehicles = (Vehicles) getIntent().getSerializableExtra("vehicles");
        scrollView = findViewById(R.id.scrollView);

        newAdMapOverlay = findViewById(R.id.newAdMapOverlay);
        card_pic1 = findViewById(R.id.card_pic1);
        card_pic2 = findViewById(R.id.card_pic2);
        card_pic3 = findViewById(R.id.card_pic3);
        card_pic4 = findViewById(R.id.card_pic4);
        card_pic5 = findViewById(R.id.card_pic5);

        radio_new = (RadioButton) findViewById(R.id.radio_new);
        radio_old = (RadioButton) findViewById(R.id.radio_old);
        radio_maghto = (RadioButton) findViewById(R.id.radio_maghto);
        radio_tavafoghi = (RadioButton) findViewById(R.id.radio_tavafoghi);
        radio_company = (RadioButton) findViewById(R.id.radio_company);
        radio_person = (RadioButton) findViewById(R.id.radio_person);

        radio_new.setChecked(true);
        mapLayout = (RelativeLayout) findViewById(R.id.newAdMapLayout);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.newAdMap);
        supportMapFragment.getMapAsync(this);

        newAdSubmitBTN = (Button) findViewById(R.id.newAdSubmitBTN);
        newAdSubmitBTN.setText("ویرایش آگهی");
        newAdSpinnerLoadProgressBar = (ProgressBar) findViewById(R.id.newAdSpinnerLoadProgressBar);
        rootView = (ViewGroup) findViewById(R.id.newAd);

        brand = new ArrayList<>();
        moddel = new ArrayList<>();
        provinces = new ArrayList<>();
        citys = new ArrayList<>();
        regions = new ArrayList<>();


        til_kilometer = (TextInputLayout) findViewById(R.id.til_kilometer);
        til_year = (TextInputLayout) findViewById(R.id.til_year);
        til_cost = (TextInputLayout) findViewById(R.id.til_cost);

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

        edt_kilometr = (EditText) findViewById(R.id.edt_kilometr);
        edt_year = (EditText) findViewById(R.id.edt_year);
        edt_cost = (EditText) findViewById(R.id.edt_cost);
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

        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_phone1 = (EditText) findViewById(R.id.edt_phone1);
        edt_phone2 = (EditText) findViewById(R.id.edt_phone2);

        edt_address = (EditText) findViewById(R.id.edt_address);


        spin_cat = (Spinner) findViewById(R.id.spin_cat);
        spin_modell = (Spinner) findViewById(R.id.spin_modell);
        spin_shasi = (Spinner) findViewById(R.id.spin_shasi);
        spin_motor_weghit = (Spinner) findViewById(R.id.spin_motor_weghit);
        spin_brand = (Spinner) findViewById(R.id.spin_brand);

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
        cat = new ArrayList<>();
        moddel = new ArrayList<>();
        brand = new ArrayList<>();
        shasi = new ArrayList<>();
        motore_weghit = new ArrayList<>();

        province = new ArrayList<>();
        city = new ArrayList<>();
        region = new ArrayList<>();

        cat.add("لطفا انتخاب کنید.");
        cat.add("خودرو");
        cat.add("موتور سیکلت");
        cat.add("خودرو کلاسیک");
        cat.add("خودرو سنگین و نیمه سنگین");
        cat.add("لوازم و وسایل نقلیه");
        cat.add("سایر وسایل نقلیه");

        shasi.add("لطفا انتخاب کنید.");
        shasi.add("سواری");
        shasi.add("هاچ بک");
        shasi.add("شاسی بلند");
        shasi.add("وانت");
        shasi.add("کروک");
        shasi.add("ون");
        shasi.add("کوپه");
        shasi.add("استیشن");
        shasi.add("دیگر");


        moddel.add("لطفا انتخاب کنید.");
        brand.add("لطفا انتخاب کنید.");
        motore_weghit.add("لطفا انتخاب کنید.");

        province.add("لطفا انتخاب کنید.");
        city.add("لطفا انتخاب کنید.");
        region.add("لطفا انتخاب کنید.");


        ArrayAdapter<String> cat_Adapter = new ArrayAdapter<>(Edite_Car_Add.this, R.layout.item_spinner_layout, cat);
        ArrayAdapter<String> moddel_addapter = new ArrayAdapter<>(Edite_Car_Add.this, R.layout.item_spinner_layout, moddel);
        ArrayAdapter<String> brand_addapter = new ArrayAdapter<>(Edite_Car_Add.this, R.layout.item_spinner_layout, brand);
        ArrayAdapter<String> shasi_addapter = new ArrayAdapter<>(Edite_Car_Add.this, R.layout.item_spinner_layout, shasi);
        ArrayAdapter<String> motoer_weghit = new ArrayAdapter<>(Edite_Car_Add.this, R.layout.item_spinner_layout, motore_weghit);

        ArrayAdapter<String> province_addapter = new ArrayAdapter<>(Edite_Car_Add.this, R.layout.item_spinner_layout, province);
        ArrayAdapter<String> city_addapter = new ArrayAdapter<>(Edite_Car_Add.this, R.layout.item_spinner_layout, city);
        ArrayAdapter<String> region_adapter = new ArrayAdapter<>(Edite_Car_Add.this, R.layout.item_spinner_layout, region);

        spin_cat.setAdapter(cat_Adapter);
        spin_modell.setAdapter(moddel_addapter);
        spin_shasi.setAdapter(shasi_addapter);
        spin_brand.setAdapter(brand_addapter);
        spin_motor_weghit.setAdapter(motoer_weghit);

        spin_province.setAdapter(province_addapter);
        spin_city.setAdapter(city_addapter);
        spin_region.setAdapter(region_adapter);

    }

    private void on_spinner_change_item() {
        spin_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    lin_motore_weghit.setVisibility(View.GONE);
                    lin_brand.setVisibility(View.GONE);
                    lin_shasi.setVisibility(View.GONE);
                    lin_modell.setVisibility(View.GONE);
                    til_year.setVisibility(View.GONE);
                    til_kilometer.setVisibility(View.GONE);

                } else if (position == 1) {
                    lin_motore_weghit.setVisibility(View.GONE);
                    lin_brand.setVisibility(View.VISIBLE);
                    lin_shasi.setVisibility(View.VISIBLE);
                    lin_modell.setVisibility(View.GONE);
                    til_year.setVisibility(View.VISIBLE);
                    til_kilometer.setVisibility(View.VISIBLE);
                } else if (position == 2) {
                    lin_motore_weghit.setVisibility(View.VISIBLE);
                    lin_brand.setVisibility(View.GONE);
                    lin_shasi.setVisibility(View.GONE);
                    lin_modell.setVisibility(View.GONE);
                    til_year.setVisibility(View.VISIBLE);
                    til_kilometer.setVisibility(View.GONE);
                } else {
                    lin_motore_weghit.setVisibility(View.GONE);
                    lin_brand.setVisibility(View.GONE);
                    lin_shasi.setVisibility(View.GONE);
                    lin_modell.setVisibility(View.GONE);
                    til_year.setVisibility(View.GONE);
                    til_kilometer.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        spin_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position > 0) {
                    get_all_modell(brands.get(position - 1).getId());
                } else {
                    lin_modell.setVisibility(View.GONE);
                }
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
                    ArrayAdapter<String> spin_city1 = new ArrayAdapter<>(Edite_Car_Add.this, R.layout.item_spinner_layout, city);
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
                    ArrayAdapter<String> spin_region1 = new ArrayAdapter<>(Edite_Car_Add.this, R.layout.item_spinner_layout, region);
                    spin_region.setAdapter(spin_region1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void get_all_brand() {
        newAdSpinnerLoadProgressBar.setVisibility(View.VISIBLE);
        String url = StaticData.get_all_brand;
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(Edite_Car_Add.this);
        Get_Volley_Call_Back.Call_Volley(Edite_Car_Add.this, params, url, Request.Method.GET, 126);
    }

    private void get_all_cilander_volum() {
        newAdSpinnerLoadProgressBar.setVisibility(View.VISIBLE);
        String url = StaticData.getall_culander_volun;
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(Edite_Car_Add.this);
        Get_Volley_Call_Back.Call_Volley(Edite_Car_Add.this, params, url, Request.Method.GET, 131);
    }

    private void get_all_modell(String id) {
        newAdSpinnerLoadProgressBar.setVisibility(View.VISIBLE);
        String url = StaticData.get_all_modell + id + "/models";
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(Edite_Car_Add.this);
        Get_Volley_Call_Back.Call_Volley(Edite_Car_Add.this, params, url, Request.Method.GET, 127);
    }

    private void get_all_province() {
        newAdSpinnerLoadProgressBar.setVisibility(View.VISIBLE);

        String provinceUrl = StaticData.PROVINCE;
        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Edite_Car_Add.this);
        Get_Volley_Call_Back.Call_Volley(Edite_Car_Add.this, params, provinceUrl, Request.Method.GET, 128);
    }

    private void get_all_city(String id) {
        newAdSpinnerLoadProgressBar.setVisibility(View.VISIBLE);

        String url = StaticData.DOMAIN_WITH_API + "/provinces/" + id + "/cities";

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Edite_Car_Add.this);
        Get_Volley_Call_Back.Call_Volley(Edite_Car_Add.this, params, url, Request.Method.GET, 129);
    }

    private void get_all_region(String id) {
        newAdSpinnerLoadProgressBar.setVisibility(View.VISIBLE);

        String url = StaticData.get_all_region + id + "/regions";

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Edite_Car_Add.this);
        Get_Volley_Call_Back.Call_Volley(Edite_Car_Add.this, params, url, Request.Method.GET, 130);
    }

    @Override
    public void on_volley_response(String response, int id) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");
            if (id == 126) {
                if (status.equals("200")) {
                    brands = Brand.Import_list(jsonObject, "list");
                    set_spin_brand();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Edite_Car_Add.this);
                    Edite_Car_Add.this.finish();

                }
                newAdSpinnerLoadProgressBar.setVisibility(View.GONE);

            }
            if (id == 127) {
                if (status.equals("200")) {
                    modells = Modell.Import_list(jsonObject, "list");
                    set_spin_modell();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Edite_Car_Add.this);
                    Edite_Car_Add.this.finish();

                }
                newAdSpinnerLoadProgressBar.setVisibility(View.GONE);

            }
            if (id == 128) {
                if (status.equals("200")) {
                    provinces = ProvicesAndCities.cities(jsonObject);
                    set_spin_province();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Edite_Car_Add.this);
                    Edite_Car_Add.this.finish();

                }
                newAdSpinnerLoadProgressBar.setVisibility(View.GONE);

            }
            if (id == 129) {
                if (status.equals("200")) {
                    citys = ProvicesAndCities.cities(jsonObject);
                    set_spin_citys();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Edite_Car_Add.this);
                    Edite_Car_Add.this.finish();

                }
                newAdSpinnerLoadProgressBar.setVisibility(View.GONE);

            }
            if (id == 130) {
                if (status.equals("200")) {
                    regions = ProvicesAndCities.cities(jsonObject);
                    set_spin_rejon();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Edite_Car_Add.this);
                    Edite_Car_Add.this.finish();

                }
                newAdSpinnerLoadProgressBar.setVisibility(View.GONE);

            }
            if (id == 131) {
                if (status.equals("200")) {
                    cylinder_volumes = com.ideabonyan.iranapp.Models.cylinder_volumes.Import_list(jsonObject, "list");
                    set_spin_cylander();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Edite_Car_Add.this);
                    Edite_Car_Add.this.finish();

                }
                newAdSpinnerLoadProgressBar.setVisibility(View.GONE);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {
        ShowToast.failure("در ازتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Edite_Car_Add.this);
        Edite_Car_Add.this.finish();
    }

    private void set_spin_brand() {
        int postion = 0;
        brand = new ArrayList<>();
        brand.add("لطفا انتخاب کنید.");
        for (int i = 0; i < brands.size(); i++) {
            brand.add(brands.get(i).getName());
//            Log.v("brands",brands.get(i).getId()+"=="+vehicles.getBrand_id());
            if (!brand_change && brands.get(i).getId().equals(vehicles.getBrand_id())) {
                postion = i + 1;
            }
        }
        ArrayAdapter<String> brand_addapter = new ArrayAdapter<>(Edite_Car_Add.this, R.layout.item_spinner_layout, brand);
        spin_brand.setAdapter(brand_addapter);
        spin_brand.setSelection(postion);
//        Log.v("position", postion + "," + vehicles.getBrand_id());
        brand_change = true;

    }

    private void set_spin_cylander() {
        int posion = 0;

        motore_weghit = new ArrayList<>();
        motore_weghit.add("لطفا انتخاب کنید.");
        for (int i = 0; i < cylinder_volumes.size(); i++) {
            motore_weghit.add(cylinder_volumes.get(i).getValue());
            if (vehicles.getType().equals("motorcycle") &&
                    !cyland_change && cylinder_volumes.get(i).getId().equals(vehicles.getCylinder_volume())) {
                posion = i + 1;
            }
        }

        ArrayAdapter<String> motore_power_addapter = new ArrayAdapter<>(Edite_Car_Add.this, R.layout.item_spinner_layout, motore_weghit);
        spin_motor_weghit.setAdapter(motore_power_addapter);
        spin_motor_weghit.setSelection(posion);
        cyland_change = true;

    }

    private void set_spin_province() {
        int position = 0;
        province = new ArrayList<>();
        province.add("لطفا انتخاب کنید.");
        for (int i = 0; i < provinces.size(); i++) {
            province.add(provinces.get(i).getName());
            if (!province_change && provinces.get(i).getId().equals(vehicles.getProvince_id())) {
                position = i + 1;
            }
        }
        ArrayAdapter<String> spin_provinces = new ArrayAdapter<>(Edite_Car_Add.this, R.layout.item_spinner_layout, province);
        spin_province.setAdapter(spin_provinces);

        spin_province.setSelection(position);


    }

    private void set_spin_citys() {
        city = new ArrayList<>();
        int posion = 0;
        city.add("لطفا انتخاب کنید.");
        for (int i = 0; i < citys.size(); i++) {
            city.add(citys.get(i).getName());
            if (!city_change && citys.get(i).getId().equals(vehicles.getCity_id())) {
                posion = i + 1;
            }
        }
        ArrayAdapter<String> spin_city1 = new ArrayAdapter<>(Edite_Car_Add.this, R.layout.item_spinner_layout, city);
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
            if (!region_change && regions.get(i).getId().equals(vehicles.getRegion_id())) {
                posion = i + 1;
            }
        }
        ArrayAdapter<String> spin_rejon1 = new ArrayAdapter<>(Edite_Car_Add.this, R.layout.item_spinner_layout, region);
        spin_region.setAdapter(spin_rejon1);
        spin_region.setSelection(posion);
//        Log.v("position",posion+"");
        region_change = true;

    }

    private void set_spin_modell() {
        int posion = 0;
        if (modells.size() != 0) {
            moddel = new ArrayList<>();
            moddel.add("لطفا انتخاب کنید.");
            for (int i = 0; i < modells.size(); i++) {
//                Log.v("model", modells.get(i).getId() + "==" + vehicles.getModel_id());
                moddel.add(modells.get(i).getName());
                if (!modell_change && modells.get(i).getId().equals(vehicles.getModel_id())) {
                    posion = i + 1;
                }
            }
            ArrayAdapter<String> model_addapter = new ArrayAdapter<>(Edite_Car_Add.this, R.layout.item_spinner_layout, moddel);
            lin_modell.setVisibility(View.VISIBLE);
            spin_modell.setAdapter(model_addapter);
        } else {
            lin_modell.setVisibility(View.GONE);
        }
        spin_modell.setSelection(posion);
        modell_change = true;
    }

    public void turnonlocation() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(Edite_Car_Add.this).addApi(LocationServices.API).build();
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

                        GPSTracker gpsTracker = new GPSTracker(Edite_Car_Add.this);
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

                        if (ActivityCompat.checkSelfPermission(Edite_Car_Add.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Edite_Car_Add.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        } else
//                            gmap.setMyLocationEnabled(true);
                            break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        chbAddLoction.setChecked(false);
//                        Log.i("abcd", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            newAdShowMap.setChecked(false);
                            status.startResolutionForResult(Edite_Car_Add.this, 0x1);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        googleMap.setMyLocationEnabled(true);

        this.googleMap = googleMap;
        if (vehicles.getLatitude().equals("null") || vehicles.getLatitude().equals("") || vehicles.getLatitude() == null) {
//            showAdMapCard.setVisibility(View.GONE);
        } else {
            newAdShowMap.setChecked(true);

            LatLng ll = new LatLng(Double.parseDouble(vehicles.getLatitude()), Double.parseDouble(vehicles.getLongitude()));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            googleMap.moveCamera(update);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(ll);
            markerOptions.title(vehicles.getAddress());
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

    private boolean valid() {
        if (edt_title.getText().length() == 0) {
            ShowToast.failure("لطفا در وارد کردن عنوان آگهی دقت فرمایید", Edite_Car_Add.this);
            return false;
        } else if (edt_dec.getText().length() == 0) {
            ShowToast.failure("لطفا در وارد نمودن توضیحات آگهی دقت نمایید.", Edite_Car_Add.this);
            return false;
        } else if (spin_cat.getSelectedItemPosition() == 0) {
            ShowToast.failure("لطفا در انتخاب دسته بندی دقت فرمایید", Edite_Car_Add.this);
            return false;

        } else if (spin_cat.getSelectedItemPosition() == 1 && spin_brand.getSelectedItemPosition() == 0) {
            ShowToast.failure("لطفا در وارد کردن برند خودرو دقت فرمایید", Edite_Car_Add.this);
            return false;
        } else if (spin_cat.getSelectedItemPosition() == 1 && spin_brand.getSelectedItemPosition() != 0 &&
                (modells.size() > 0 && spin_modell.getSelectedItemPosition() == 0)) {
            ShowToast.failure("لطفا در انتخاب مدل خودرو دقت فرمایید", Edite_Car_Add.this);
            return false;
        } else if (spin_cat.getSelectedItemPosition() == 1 && spin_shasi.getSelectedItemPosition() == 0) {
            ShowToast.failure("لطفا در انتخاب نوع شاسی دقت فرمایید.", Edite_Car_Add.this);
            return false;
        } else if (spin_cat.getSelectedItemPosition() == 1 && edt_year.length() == 0) {
            ShowToast.failure("لطفا در وارد کردن سال تولید دقت فرمایید", Edite_Car_Add.this);
            return false;
        } else if (spin_cat.getSelectedItemPosition() == 1 && edt_kilometr.length() == 0) {
            ShowToast.failure("لطفا در وارد کردن کیلومتر دقت فرمایید", Edite_Car_Add.this);
            return false;
        } else if (spin_cat.getSelectedItemPosition() == 2 && edt_year.getText().length() != 4) {
            ShowToast.failure("لطفا در وارد نمودن سال تولید دقت فرمایید.", Edite_Car_Add.this);
            return false;
        } else if (spin_cat.getSelectedItemPosition() == 2 && spin_motor_weghit.getSelectedItemPosition() == 0) {
            ShowToast.failure("لطفا در انتخاب حجم موتور دقت فرمایید.", Edite_Car_Add.this);
            return false;
        } else if (radio_maghto.isChecked() && edt_cost.getText().length() == 0) {
            ShowToast.failure("لطفا در وارد کردن قیمت دقت فرمایید", Edite_Car_Add.this);
            return false;
        } else if (edt_name.getText().length() == 0) {
            ShowToast.failure("لطفا در وارد کردن نام و نام خوانوادگی دقت فرمایید دقت فرمایید", Edite_Car_Add.this);
            return false;
        } else if (edt_phone1.getText().length() != 11) {
            ShowToast.failure("لطفا در وارد کردن شماره تلفن 1 دقت فرمایید دقت فرمایید", Edite_Car_Add.this);
            return false;
        } else if (edt_phone2.getText().length() != 0 && edt_phone2.getText().length() != 11) {
            ShowToast.failure("لطفا در وارد کردن شماره تلفن 2 دقت فرمایید دقت فرمایید", Edite_Car_Add.this);
            return false;
        } else if (spin_province.getSelectedItemPosition() == 0) {
            ShowToast.failure("لطفا در انتخاب استان دقت فرمایید", Edite_Car_Add.this);
            return false;
        } else if (spin_city.getSelectedItemPosition() == 0) {
            ShowToast.failure("لطفا در انتخاب شهر دقت فرمایید", Edite_Car_Add.this);
            return false;
        } else if (spin_region.getSelectedItemPosition() == 0) {
            ShowToast.failure("لطفا در انتخاب منطقه دقت فرمایید", Edite_Car_Add.this);
            return false;
        } else if (edt_address.getText().length() == 0) {
            ShowToast.failure("لطفا در وارد نمودن آدرس خود دقت فرمایید", Edite_Car_Add.this);
            return false;
        } else {
            return true;
        }
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
            MultiImageSelector selector = MultiImageSelector.create(Edite_Car_Add.this);
            selector.showCamera(showCamera);
            selector.single();
            selector.origin(mSelectPath);
            selector.start(Edite_Car_Add.this, REQUEST_IMAGE);
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
                            ActivityCompat.requestPermissions(Edite_Car_Add.this, new String[]{permission}, requestCode);
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
                        if (!(vehicles.getThumbnail_photo() == null || vehicles.getThumbnail_photo().equals("null") ||
                                vehicles.getThumbnail_photo().equals(null) || vehicles.getThumbnail_photo().equals(""))) {
                            delete_thumbnail_photo = 1;
                        }
                        card_pic2.setCardBackgroundColor(Color.parseColor("#ffffff"));
                    } else if (selection_pic == 2) {
                        img_pic2.setImageBitmap(bitmap);
                        bitmaps[1] = bitmap;
                        img_add_pic2.setVisibility(View.GONE);
                        if (vehicles.getPhotosDatas().size() > 0) {
                            photo_to_delete.add(vehicles.getPhotosDatas().get(0).getId());
                        }
                        card_pic3.setCardBackgroundColor(Color.parseColor("#ffffff"));


                    } else if (selection_pic == 3) {
                        img_pic3.setImageBitmap(bitmap);
                        bitmaps[2] = bitmap;
                        img_add_pic3.setVisibility(View.GONE);
                        if (vehicles.getPhotosDatas().size() > 1) {
                            photo_to_delete.add(vehicles.getPhotosDatas().get(1).getId());
                        }
                        card_pic4.setCardBackgroundColor(Color.parseColor("#ffffff"));

                    } else if (selection_pic == 4) {
                        img_pic4.setImageBitmap(bitmap);
                        bitmaps[3] = bitmap;
                        img_add_pic4.setVisibility(View.GONE);
                        if (vehicles.getPhotosDatas().size() > 2) {
                            photo_to_delete.add(vehicles.getPhotosDatas().get(2).getId());
                        }
                        card_pic5.setCardBackgroundColor(Color.parseColor("#ffffff"));

                    } else if (selection_pic == 5) {
                        img_pic5.setImageBitmap(bitmap);
                        bitmaps[4] = bitmap;
                        img_add_pic5.setVisibility(View.GONE);
                        if (vehicles.getPhotosDatas().size() > 3) {
                            photo_to_delete.add(vehicles.getPhotosDatas().get(3).getId());
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

    private void doUpload() {
        final ProgressDialog prgDialog = new ProgressDialog(Edite_Car_Add.this);
        prgDialog.setMessage("درحال بارگذاری اطلاعات...");
        prgDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {

                String handleInserUrl;

                handleInserUrl = StaticData.add_cae + "/" + vehicles.getId() + "/update";
//                Log.v("url", handleInserUrl);

                try {
                    okhttp3.MultipartBody.Builder reqEntity = new okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM);

                    String token = new UserSessionManager(Edite_Car_Add.this).getLoginToken();
//                    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjI3LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvZ2hvbGxhYy9wdWJsaWNfaHRtbC9hcGkvbG9naW4iLCJpYXQiOjE1MDgyMjI4MjQsImV4cCI6MTUwODgyNzYyNCwibmJmIjoxNTA4MjIyODI0LCJqdGkiOiJZcDhZWDhxZ3BhY1ZLYmJrIn0.2QojAogqfRL_TK8BYzh9mtL45UOwoGLJyUd--9aMClQ";

                    reqEntity.addFormDataPart("ads_title", edt_title.getText().toString());
//                    Log.v("params", "ads_title:" + edt_title.getText().toString());
                    reqEntity.addFormDataPart("region_id", regions.get(spin_region.getSelectedItemPosition() - 1).getId());
//                    Log.v("params", "region_id:" + regions.get(spin_region.getSelectedItemPosition() - 1).getId());

                    reqEntity.addFormDataPart("address", edt_address.getText().toString());
//                    Log.v("params", "address:" + edt_address.getText().toString());
                    if (radio_person.isChecked()) {
                        reqEntity.addFormDataPart("person_or_company", "person");
//                        Log.v("person_or_company", "person");
                    } else {
                        reqEntity.addFormDataPart("person_or_company", "company");
//                        Log.v("person_or_company", "company");
                    }
//                    reqEntity.addFormDataPart("price", edt_cost.getText().toString().replaceAll(",", ""));
//                    Log.v("params", "price:" + edt_cost.getText().toString());
                    if (radio_tavafoghi.isChecked()) {
                        reqEntity.addFormDataPart("price", "0");
//                        Log.v("params", "price:" + "0");
                    } else {
                        reqEntity.addFormDataPart("price", edt_cost.getText().toString().replaceAll(",", ""));
//                        Log.v("params", "price:" + edt_cost.getText().toString().replaceAll(",", ""));
                    }
                    reqEntity.addFormDataPart("telephone1", edt_phone1.getText().toString());
//                    Log.v("params", "telephone1:" + edt_phone1.getText().toString());

                    if (edt_phone2.getText().length() > 0) {
                        reqEntity.addFormDataPart("telephone2", edt_phone2.getText().toString());
//                        Log.v("params", "telephone2:" + edt_phone2.getText().toString());

                    }
                    reqEntity.addFormDataPart("ads_owner_name", edt_name.getText().toString());
//                    Log.v("params", "ads_owner_name:" + edt_name.getText().toString());

                    reqEntity.addFormDataPart("description", edt_dec.getText().toString());
//                    Log.v("params", "description:" + edt_dec.getText().toString());

                    if (ltLg != null) {
                        reqEntity.addFormDataPart("latitude", ltLg.latitude + "");
//                        Log.v("params", "latitude:" + ltLg.latitude + "");
                        reqEntity.addFormDataPart("longitude", ltLg.longitude + "");
//                        Log.v("params", "longitude:" + ltLg.longitude + "");
                    }


                    if (radio_new.isChecked()) {
                        reqEntity.addFormDataPart("neworold", "new");
//                        Log.v("params", "neworold:" + "new");

                    } else {
                        reqEntity.addFormDataPart("neworold", "old");
//                        Log.v("params", "neworold:" + "old");

                    }
                    if (spin_cat.getSelectedItemPosition() == 1) {
                        reqEntity.addFormDataPart("type", "khodro");
//                        Log.v("params", "type:" + "khodro");

                        reqEntity.addFormDataPart("brand", brands.get(spin_brand.getSelectedItemPosition() - 1).getId());
//                        Log.v("params", "brand:" + brands.get(spin_brand.getSelectedItemPosition() - 1).getId());

                        if (spin_modell.getSelectedItemPosition() > 0) {
                            reqEntity.addFormDataPart("model", modells.get(spin_modell.getSelectedItemPosition() - 1).getId());
//                            Log.v("params", "model:" + modells.get(spin_modell.getSelectedItemPosition() - 1).getId());

                        }
                        reqEntity.addFormDataPart("production_year", edt_year.getText().toString());
//                        Log.v("params", "production_year:" + edt_year.getText().toString());

                        reqEntity.addFormDataPart("kilometre", edt_kilometr.getText().toString());
//                        Log.v("params", "kilometre:" + edt_kilometr.getText().toString());

                        if (spin_shasi.getSelectedItemPosition() == 1) {
                            reqEntity.addFormDataPart("chassis_type", "savari");
//                            Log.v("params", "chassis_type:" + "savari");

                        } else if (spin_shasi.getSelectedItemPosition() == 2) {
                            reqEntity.addFormDataPart("chassis_type", "hachback");
//                            Log.v("params", "chassis_type:" + "hachback");

                        } else if (spin_shasi.getSelectedItemPosition() == 3) {
                            reqEntity.addFormDataPart("chassis_type", "shasiboland");
//                            Log.v("params", "chassis_type:" + "shasiboland");

                        } else if (spin_shasi.getSelectedItemPosition() == 4) {
                            reqEntity.addFormDataPart("chassis_type", "vanet");
//                            Log.v("params", "chassis_type:" + "vanet");

                        } else if (spin_shasi.getSelectedItemPosition() == 5) {
                            reqEntity.addFormDataPart("chassis_type", "krook");
//                            Log.v("params", "chassis_type:" + "krook");

                        } else if (spin_shasi.getSelectedItemPosition() == 6) {
                            reqEntity.addFormDataPart("chassis_type", "van");
//                            Log.v("params", "chassis_type:" + "van");

                        } else if (spin_shasi.getSelectedItemPosition() == 7) {
                            reqEntity.addFormDataPart("chassis_type", "cupe");
//                            Log.v("params", "chassis_type:" + "cupe");

                        } else if (spin_shasi.getSelectedItemPosition() == 8) {
                            reqEntity.addFormDataPart("chassis_type", "station");
//                            Log.v("params", "chassis_type:" + "station");

                        } else if (spin_shasi.getSelectedItemPosition() == 9) {
                            reqEntity.addFormDataPart("chassis_type", "other");
//                            Log.v("params", "chassis_type:" + "other");

                        }

                    } else if (spin_cat.getSelectedItemPosition() == 2) {
                        reqEntity.addFormDataPart("cylinder_volume", cylinder_volumes.get(spin_motor_weghit.getSelectedItemPosition() - 1).getId());
//                        Log.v("params", "cylinder_volume:" + cylinder_volumes.get(spin_motor_weghit.getSelectedItemPosition() - 1).getId());

                        reqEntity.addFormDataPart("production_year", edt_year.getText().toString());
//                        Log.v("params", "production_year:" + edt_year.getText().toString());

                        reqEntity.addFormDataPart("type", "motorcycle");
//                        Log.v("params", "type:" + "motorcycle");

                    } else if (spin_cat.getSelectedItemPosition() == 3) {
                        reqEntity.addFormDataPart("type", "khodroclasic");
//                        Log.v("params", "type:" + "khodroclasic");

                    } else if (spin_cat.getSelectedItemPosition() == 4) {
                        reqEntity.addFormDataPart("type", "khordrosorn");
//                        Log.v("params", "type:" + "khordrosorn");

                    } else if (spin_cat.getSelectedItemPosition() == 5) {
                        reqEntity.addFormDataPart("type", "lavazem");
//                        Log.v("params", "type:" + "lavazem");

                    } else if (spin_cat.getSelectedItemPosition() == 6) {
                        reqEntity.addFormDataPart("type", "other");
//                        Log.v("params", "type:" + "other");

                    }


                    for (int i = 1; i < bitmaps.length; i++) {
                        long time = System.currentTimeMillis();
                        if (bitmaps[i] != null) {
                            String pathTemp = Compress_image.reductImageSize(time + ".jpg", bitmaps[i]);
                            reqEntity.addFormDataPart("photos[]", new File(pathTemp).getName(), okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), new File(pathTemp)));
//                            Log.v("params", "photos[]:" + time + ".jpg");

                        }
                    }
                    for (int i = 0; i < photo_to_delete.size(); i++) {
                        reqEntity.addFormDataPart("delete_photo[]", photo_to_delete.get(i));

                    }
                    if (delete_thumbnail_photo == 1) {
                        reqEntity.addFormDataPart("delete_thumbnail_photo ", "1");

                    }
                    if (bitmaps[0] != null) {
                        long time = System.currentTimeMillis();

                        String pathTemp = Compress_image.reductImageSize(time + ".jpg", bitmaps[0]);
                        reqEntity.addFormDataPart("thumbnail_photo", new File(pathTemp).getName(), okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), new File(pathTemp)));
//                        Log.v("params", "thumbnail_photo:" + time + ".jpg");

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

//                                            Toast.makeText(Add_New_Car_Add.this, "ثبت داده ها با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
                                            ShowToast.success("ویرایش آگهی شما با موفقیت ثبت گردید و پس از تایید مدیر بنمایش گذاشته می شود", Edite_Car_Add.this);
//                                            startActivity(new Intent(Add_New_Car_Add.this,DashboardActivity.class));
                                            Edite_Car_Add.this.finish();

                                        } else if (Jobj.getString("status").equals("401")) {
                                            if (Jobj.getString("error").equals("token_expired")) {
                                                UserHelper.RemoveUserInfo(Edite_Car_Add.this);
                                                Intent intent = new Intent(Edite_Car_Add.this, LoginActivity.class);
                                                ShowToast.failure("لطفا دوباره وارد حساب خود شوید", Edite_Car_Add.this);
                                                startActivity(intent);
                                            }
                                        } else {
                                            ShowToast.failure("در ثبت آگهی به مشکل برخوردیم لطفا مجددا تلاش فرمایید.", Edite_Car_Add.this);
//                                            Add_New_Car_Add.this.finish();
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


}
