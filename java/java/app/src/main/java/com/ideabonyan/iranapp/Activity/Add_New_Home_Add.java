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
import com.ideabonyan.iranapp.Fragment.Dialogs.Show_Rouls_Dialog;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
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

public class Add_New_Home_Add extends AppCompatActivity implements OnMapReadyCallback, Get_Insert_Edit_Data {
    public static boolean isItNewAd = true;
    protected final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private final int REQUEST_IMAGE = 2;
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
    CardView card_pic5, card_pic4, card_pic3, card_pic2, card_pic1;
    ScrollView scrollView;
    ImageView newAdMapOverlay;
    LinearLayout lin_accept_roul;
    String picpath;
    Bitmap bitmap1;
    Bitmap[] bitmaps = new Bitmap[5];
    int REQUEST_ID_MULTIPLE_PERMISSIONS1 = 1001;
    private ArrayList<String> mSelectPath;

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
                til_vadeae.setHint("ودیعه ( تومان )");
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
        lin_accept_roul = findViewById(R.id.lin_accept_roul);
        lin_accept_roul.setVisibility(View.VISIBLE);

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

        edt_meter = (EditText) findViewById(R.id.edt_meter);

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
                            if (checkAndRequestPermissions1(false)){
                                turnonlocation();

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
                til_vadeae.setHint("ودیعه ( تومان )");
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
                til_ejare.setHint("اجاره ( تومان )");
                edt_ejare.setText("");
                edt_ejare.setEnabled(true);

            }
        });
        radio_tavafoghi_ejare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til_ejare.setHint("اجاره ( توافقی )");
                edt_ejare.setText("");
                edt_ejare.setEnabled(false);
            }
        });
    }

    private void onclick() {
        lin_accept_roul.setOnClickListener(new View.OnClickListener() {
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

    private void pickImage() {
        if (!checkAndRequestPermissions(false)) {


        } else {
            Log.v("permission", "no");
            boolean showCamera = true;
            MultiImageSelector selector = MultiImageSelector.create(Add_New_Home_Add.this);
            selector.showCamera(showCamera);
            selector.single();
            selector.origin(mSelectPath);
            selector.start(Add_New_Home_Add.this, REQUEST_IMAGE);
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
                AlertDialog.Builder b = new AlertDialog.Builder(Add_New_Home_Add.this);

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

    public void turnonlocation() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(Add_New_Home_Add.this).addApi(LocationServices.API).build();
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

                        GPSTracker gpsTracker = new GPSTracker(Add_New_Home_Add.this);
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

                        if (ActivityCompat.checkSelfPermission(Add_New_Home_Add.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Add_New_Home_Add.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        } else
//                            gmap.setMyLocationEnabled(true);
                            break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        chbAddLoction.setChecked(false);
//                        Log.i("abcd", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            newAdShowMap.setChecked(false);
                            status.startResolutionForResult(Add_New_Home_Add.this, 0x1);
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


        ArrayAdapter<String> cat_Adapter = new ArrayAdapter<>(Add_New_Home_Add.this, R.layout.item_spinner_layout, cat);
        ArrayAdapter<String> sub_cat_addapter = new ArrayAdapter<>(Add_New_Home_Add.this, R.layout.item_spinner_layout, sub_cat);

        ArrayAdapter<String> room_num_addapter = new ArrayAdapter<>(Add_New_Home_Add.this, R.layout.item_spinner_layout, room_num);

        ArrayAdapter<String> province_addapter = new ArrayAdapter<>(Add_New_Home_Add.this, R.layout.item_spinner_layout, province);
        ArrayAdapter<String> city_addapter = new ArrayAdapter<>(Add_New_Home_Add.this, R.layout.item_spinner_layout, city);
        ArrayAdapter<String> region_adapter = new ArrayAdapter<>(Add_New_Home_Add.this, R.layout.item_spinner_layout, region);

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
                    ArrayAdapter<String> spin_city1 = new ArrayAdapter<>(Add_New_Home_Add.this, R.layout.item_spinner_layout, city);
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
                    ArrayAdapter<String> spin_region1 = new ArrayAdapter<>(Add_New_Home_Add.this, R.layout.item_spinner_layout, region);
                    spin_region.setAdapter(spin_region1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void set_cat_spin_change(int posion) {
        if (spin_cat.getSelectedItemPosition() == 1 && spin_sub_cat.getSelectedItemPosition() == 3) {
            lin_room_number.setVisibility(View.GONE);
        } else if (spin_cat.getSelectedItemPosition() != 5) {
            lin_room_number.setVisibility(View.VISIBLE);

        }
    }

    private void get_all_province() {
        newAdSpinnerLoadProgressBar.setVisibility(View.VISIBLE);

        String provinceUrl = StaticData.PROVINCE;
        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Add_New_Home_Add.this);
        Get_Volley_Call_Back.Call_Volley(Add_New_Home_Add.this, params, provinceUrl, Request.Method.GET, 128);
    }

    private void get_all_city(String id) {
        newAdSpinnerLoadProgressBar.setVisibility(View.VISIBLE);

        String url = StaticData.DOMAIN_WITH_API + "/provinces/" + id + "/cities";

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Add_New_Home_Add.this);
        Get_Volley_Call_Back.Call_Volley(Add_New_Home_Add.this, params, url, Request.Method.GET, 129);
    }

    private void get_all_region(String id) {
        newAdSpinnerLoadProgressBar.setVisibility(View.VISIBLE);

        String url = StaticData.get_all_region + id + "/regions";

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Add_New_Home_Add.this);
        Get_Volley_Call_Back.Call_Volley(Add_New_Home_Add.this, params, url, Request.Method.GET, 130);
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
        ArrayAdapter<String> sub_cat_addapter = new ArrayAdapter<>(Add_New_Home_Add.this, R.layout.item_spinner_layout, sub_cat);
        spin_sub_cat.setAdapter(sub_cat_addapter);

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
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Add_New_Home_Add.this);
                    Add_New_Home_Add.this.finish();

                }
                newAdSpinnerLoadProgressBar.setVisibility(View.GONE);

            }
            if (id == 129) {
                if (status.equals("200")) {
                    citys = ProvicesAndCities.cities(jsonObject);
                    set_spin_citys();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Add_New_Home_Add.this);
                    Add_New_Home_Add.this.finish();

                }
                newAdSpinnerLoadProgressBar.setVisibility(View.GONE);

            }
            if (id == 130) {
                if (status.equals("200")) {
                    regions = ProvicesAndCities.cities(jsonObject);
                    set_spin_rejon();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", Add_New_Home_Add.this);
                    Add_New_Home_Add.this.finish();

                }
                newAdSpinnerLoadProgressBar.setVisibility(View.GONE);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void set_spin_rejon() {
        region = new ArrayList<>();
        region.add("لطفا انتخاب کنید.");
        for (int i = 0; i < regions.size(); i++) {
            region.add(regions.get(i).getName());
        }
        ArrayAdapter<String> spin_rejon1 = new ArrayAdapter<>(Add_New_Home_Add.this, R.layout.item_spinner_layout, region);
        spin_region.setAdapter(spin_rejon1);

    }

    private void set_spin_citys() {
        city = new ArrayList<>();
        city.add("لطفا انتخاب کنید.");
        for (int i = 0; i < citys.size(); i++) {
            city.add(citys.get(i).getName());
        }
        ArrayAdapter<String> spin_city1 = new ArrayAdapter<>(Add_New_Home_Add.this, R.layout.item_spinner_layout, city);
        spin_city.setAdapter(spin_city1);

    }

    private void set_spin_province() {
        province = new ArrayList<>();
        province.add("لطفا انتخاب کنید.");
        for (int i = 0; i < provinces.size(); i++) {
            province.add(provinces.get(i).getName());
        }
        ArrayAdapter<String> spin_provinces = new ArrayAdapter<>(Add_New_Home_Add.this, R.layout.item_spinner_layout, province);
        spin_province.setAdapter(spin_provinces);

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
            ShowToast.failure(message, Add_New_Home_Add.this);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }

    private void doUpload() {
        final ProgressDialog prgDialog = new ProgressDialog(Add_New_Home_Add.this);
        prgDialog.setMessage("درحال بارگذاری اطلاعات...");
        prgDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {

                String handleInserUrl;

                handleInserUrl = StaticData.estates + "/save";


                try {
                    okhttp3.MultipartBody.Builder reqEntity = new okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM);

                    String token = new UserSessionManager(Add_New_Home_Add.this).getLoginToken();
//                    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjI3LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvZ2hvbGxhYy9wdWJsaWNfaHRtbC9hcGkvbG9naW4iLCJpYXQiOjE1MDgyMjI4MjQsImV4cCI6MTUwODgyNzYyNCwibmJmIjoxNTA4MjIyODI0LCJqdGkiOiJZcDhZWDhxZ3BhY1ZLYmJrIn0.2QojAogqfRL_TK8BYzh9mtL45UOwoGLJyUd--9aMClQ";


                    reqEntity.addFormDataPart("ads_title", edt_title.getText().toString());
                    Log.v("params", "ads_title:" + edt_title.getText().toString());
                    reqEntity.addFormDataPart("description", edt_dec.getText().toString());
                    Log.v("params", "description:" + edt_dec.getText().toString());

                    reqEntity.addFormDataPart("category_id", subcategory.get(spin_sub_cat.getSelectedItemPosition() - 1).getId());
                    Log.v("params", "category_id:" + subcategory.get(spin_sub_cat.getSelectedItemPosition() - 1).getId());
//alaki fild
                    reqEntity.addFormDataPart("ejare_or_kharid", "kharid");
                    Log.v("params", "ejare_or_kharid:" + "kharid");
                    reqEntity.addFormDataPart("type_karbari", "maskooni");
                    Log.v("params", "type_karbari:" + "maskooni");
                    /// etmam

                    if (radio_person.isChecked()) {
                        reqEntity.addFormDataPart("user_type", "person");
                        Log.v("params", "user_type:" + "person");

                    } else {
                        reqEntity.addFormDataPart("user_type", "moshaver_amlak");
                        Log.v("params", "user_type:" + "moshaver_amlak");

                    }
                    reqEntity.addFormDataPart("ads_owner_name", edt_name.getText().toString());
                    Log.v("params", "ads_owner_name:" + edt_name.getText().toString());

                    reqEntity.addFormDataPart("telephone1", edt_phone1.getText().toString());
                    Log.v("params", "telephone1:" + edt_phone1.getText().toString());

                    if (edt_phone2.getText().length() > 0) {
                        reqEntity.addFormDataPart("telephone2", edt_phone2.getText().toString());
                        Log.v("params", "telephone2:" + edt_phone2.getText().toString());

                    }

                    reqEntity.addFormDataPart("region_id", regions.get(spin_region.getSelectedItemPosition() - 1).getId());
                    Log.v("params", "region_id:" + regions.get(spin_region.getSelectedItemPosition() - 1).getId());

                    reqEntity.addFormDataPart("address", edt_address.getText().toString());
                    Log.v("params", "address:" + edt_address.getText().toString());

                    if (ltLg != null) {
                        reqEntity.addFormDataPart("latitude", ltLg.latitude + "");
                        Log.v("params", "latitude:" + ltLg.latitude + "");
                        reqEntity.addFormDataPart("longitude", ltLg.longitude + "");
                        Log.v("params", "longitude:" + ltLg.longitude + "");
                    }
                    if (radio_sell.isChecked()) {
                        reqEntity.addFormDataPart("sell_or_buy", "sell");
                        Log.v("params", "sell_or_buy:" + "sell");
                    } else {
                        reqEntity.addFormDataPart("sell_or_buy", "buy");
                        Log.v("params", "sell_or_buy:" + "buy");
                    }
                    if (spin_cat.getSelectedItemPosition() == 1) {
                        if (radio_tavafoghi.isChecked()) {
                            reqEntity.addFormDataPart("price_kharid", "0");
                            Log.v("params", "price_kharid:" + "0");
                        } else if (radio_maghto.isChecked()) {
                            reqEntity.addFormDataPart("price_kharid", edt_cost.getText().toString().replaceAll(",", ""));
                            Log.v("params", "price_kharid:" + edt_cost.getText().toString().replaceAll(",", ""));
                        } else if (radio_moaveze.isChecked()) {
                            reqEntity.addFormDataPart("price_kharid", "-1");
                            Log.v("params", "price_kharid:" + "-1");
                        }

                        if (spin_cat.getSelectedItemPosition() == 1 && spin_sub_cat.getSelectedItemPosition() == 3) {
                            reqEntity.addFormDataPart("rooms_count", "0");
                            Log.v("params", "rooms_count:" + String.valueOf(spin_room_number.getSelectedItemPosition() - 1));
                        } else {
                            reqEntity.addFormDataPart("rooms_count", String.valueOf(spin_room_number.getSelectedItemPosition() - 1));
                            Log.v("params", "rooms_count:" + String.valueOf(spin_room_number.getSelectedItemPosition() - 1));
                        }


                        reqEntity.addFormDataPart("meters", edt_meter.getText().toString());
                        Log.v("params", "meters:" + edt_meter.getText().toString());

                        if (radio_hast.isChecked()) {
                            reqEntity.addFormDataPart("is_in_hoome", "1");
                            Log.v("params", "is_in_hoome:" + "1");
                        } else {
                            reqEntity.addFormDataPart("is_in_hoome", "0");
                            Log.v("params", "is_in_hoome:" + "0");
                        }


                    } else if (spin_cat.getSelectedItemPosition() == 2 || spin_cat.getSelectedItemPosition() == 4) {
                        reqEntity.addFormDataPart("rooms_count", String.valueOf(spin_room_number.getSelectedItemPosition() - 1));
                        Log.v("params", "rooms_count:" + String.valueOf(spin_room_number.getSelectedItemPosition() - 1));

                        reqEntity.addFormDataPart("meters", edt_meter.getText().toString());
                        Log.v("params", "meters:" + edt_meter.getText().toString());

                        if (radio_hast.isChecked()) {
                            reqEntity.addFormDataPart("is_in_hoome", "1");
                            Log.v("params", "is_in_hoome:" + "1");
                        } else {
                            reqEntity.addFormDataPart("is_in_hoome", "0");
                            Log.v("params", "is_in_hoome:" + "0");
                        }
                        if (radio_tavafoghi_vadeae.isChecked()) {
                            reqEntity.addFormDataPart("pre_pay_ejare", "0");
                            Log.v("params", "pre_pay_ejare:" + "0");
                        } else if (radio_maghto_vadeae.isChecked()) {
                            reqEntity.addFormDataPart("pre_pay_ejare", edt_vadeae.getText().toString().replaceAll(",", ""));
                            Log.v("params", "pre_pay_ejare:" + edt_vadeae.getText().toString());
                        } else if (radio_majani_vadeae.isChecked()) {
                            reqEntity.addFormDataPart("pre_pay_ejare", "-1");
                            Log.v("params", "pre_pay_ejare:" + "-1");
                        }
                        if (radio_tavafoghi_ejare.isChecked()) {
                            reqEntity.addFormDataPart("monthly_price_ejare", "0");
                            Log.v("params", "monthly_price_ejare:" + "0");
                        } else if (radio_maghto_ejare.isChecked()) {
                            reqEntity.addFormDataPart("monthly_price_ejare", edt_ejare.getText().toString().replaceAll(",", ""));
                            Log.v("params", "monthly_price_ejare:" + edt_ejare.getText().toString());
                        } else if (radio_majani_ejare.isChecked()) {
                            reqEntity.addFormDataPart("monthly_price_ejare", "-1");
                            Log.v("params", "monthly_price_ejare:" + "-1");
                        }

                    } else if (spin_cat.getSelectedItemPosition() == 3) {
                        if (radio_tavafoghi.isChecked()) {
                            reqEntity.addFormDataPart("price_kharid", "0");
                            Log.v("params", "price_kharid:" + "0");
                        } else if (radio_maghto.isChecked()) {
                            reqEntity.addFormDataPart("price_kharid", edt_cost.getText().toString().replaceAll(",", ""));
                            Log.v("params", "price_kharid:" + edt_cost.getText().toString().replaceAll(",", ""));
                        } else if (radio_moaveze.isChecked()) {
                            reqEntity.addFormDataPart("price_kharid", "-1");
                            Log.v("params", "price_kharid:" + "-1");
                        }
                        reqEntity.addFormDataPart("rooms_count", String.valueOf(spin_room_number.getSelectedItemPosition() - 1));
                        Log.v("params", "rooms_count:" + String.valueOf(spin_room_number.getSelectedItemPosition() - 1));

                        reqEntity.addFormDataPart("meters", edt_meter.getText().toString());
                        Log.v("params", "meters:" + edt_meter.getText().toString());

                        if (radio_darad.isChecked()) {
                            reqEntity.addFormDataPart("sanad_edari", "1");
                            Log.v("params", "sanad_edari:" + "1");
                        } else {
                            reqEntity.addFormDataPart("sanad_edari", "0");
                            Log.v("params", "sanad_edari:" + "0");
                        }

                        if (radio_hast.isChecked()) {
                            reqEntity.addFormDataPart("is_in_hoome", "1");
                            Log.v("params", "is_in_hoome:" + "1");
                        } else {
                            reqEntity.addFormDataPart("is_in_hoome", "0");
                            Log.v("params", "is_in_hoome:" + "0");
                        }


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

//                                            Toast.makeText(Add_New_Home_Add.this, "ثبت داده ها با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
                                            ShowToast.success("آگهی شما با موفقیت ثبت گردید و پس از تایید مدیر بنمایش گذاشته می شود", Add_New_Home_Add.this);
//                                            startActivity(new Intent(Add_New_Home_Add.this,DashboardActivity.class));
                                            Add_New_Home_Add.this.finish();

                                        } else if (Jobj.getString("status").equals("401")) {
                                            if (Jobj.getString("error").equals("token_expired")) {
                                                UserHelper.RemoveUserInfo(Add_New_Home_Add.this);
                                                Intent intent = new Intent(Add_New_Home_Add.this, LoginActivity.class);
                                                ShowToast.failure("لطفا دوباره وارد حساب خود شوید", Add_New_Home_Add.this);
                                                startActivity(intent);
                                            }
                                        } else {
                                            ShowToast.failure("در ثبت آگهی به مشکل برخوردیم لطفا مجددا تلاش فرمایید.", Add_New_Home_Add.this);
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
                AlertDialog.Builder b = new AlertDialog.Builder(Add_New_Home_Add.this);

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

    private void radio_vadeae_change() {
        radio_majani_vadeae.setChecked(false);
        radio_maghto_vadeae.setChecked(false);
        radio_tavafoghi_vadeae.setChecked(false);
    }

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
                ShowToast.failure("شما اجازه دسترسی به موقعیت خود را به این برنامه نداده اید", Add_New_Home_Add.this);
                newAdShowMap.setChecked(false);

            }
        } else if (requestCode == REQUEST_STORAGE_READ_ACCESS_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    ((grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) || grantResults.length == 1)) {
                boolean showCamera = true;
                MultiImageSelector selector = MultiImageSelector.create(Add_New_Home_Add.this);
                selector.showCamera(showCamera);
                selector.single();
                selector.origin(mSelectPath);
                selector.start(Add_New_Home_Add.this, REQUEST_IMAGE);
            } else {
                ShowToast.failure("شما اجازه دسترسی به حافظه و دوربین را به این برنامه نداده اید", Add_New_Home_Add.this);
            }
        }

    }

}
