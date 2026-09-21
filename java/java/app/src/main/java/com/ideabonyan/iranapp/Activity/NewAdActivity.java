package com.ideabonyan.iranapp.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.ideabonyan.iranapp.Components.MyCheckbox;
import com.ideabonyan.iranapp.Fragment.Dialogs.Show_Rouls_Dialog;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data2;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data3;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data4;
import com.ideabonyan.iranapp.Interface.UpdateAd;
import com.ideabonyan.iranapp.Models.AdPlans;
import com.ideabonyan.iranapp.Models.AdsToBeListed;
import com.ideabonyan.iranapp.Models.HomeSubCategories;
import com.ideabonyan.iranapp.Models.ProvicesAndCities;
import com.ideabonyan.iranapp.BuildConfig;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Compress_image;
import com.ideabonyan.iranapp.Utils.GPSTracker;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back2;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back3;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back4;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;
import com.ideabonyan.iranapp.Utils.Utilis;
import com.squareup.picasso.Picasso;
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

public class NewAdActivity extends AppCompatActivity implements OnMapReadyCallback
        , Get_Insert_Edit_Data, Get_Insert_Edit_Data2, Get_Insert_Edit_Data3, Get_Insert_Edit_Data4 {

    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private static final int REQUEST_IMAGE = 2;
    /////////
    // NEW = true , UPDATE = false
    public static boolean isItNewAd = true;
    public static AdsToBeListed ad = null;
    public static boolean isCitySetOnce = false;
    public static boolean isSubcatSetOnce = false;
    static UpdateAd updateAd;
    AdPlans adPlans;
    LinearLayout lin_remove_map_loc;
    LinearLayout lin_accept_roul;
    MyCheckbox chk_accept_roul;
    View txt_show_rouls;
    ImageButton backInToolbar;
    ViewGroup rootView;
    MyCheckbox showMap;
    RelativeLayout mapLayout;
    ScrollView scrollView;
    ImageView mapOverlay;
    SupportMapFragment supportMapFragment;
    Context context;
    Button submitBtn;
    EditText titleEdt, descriptionEdt, workingTimeEdt, ownerNameEdt, discountEdt, phoneEdt, tel1Edt, tel2Edt, linkEdt, telegramEdt, instagramEdt, addressEdt, edt_email;
    TextView titleWarn, descriptionWarn, catWarn, subCatWarn, ownerNameWarn, adTypeWarn, phoneWarn, provinceWarn, cityWarn, addressWarn, majorWarnAtBottom, txt_Dis_Danger, txt_emailWarn, txt_mapError;
    TextView plansPriceTv, plansPicCountTv, plansUpdateCountTv, plansIntervalTv;
    TextView updatesLeftCountTXT;
    ImageButton contactUsBTN;
    Spinner catSpnr, subCatSpnr, adTypeSpnr, provinceSpnr, citySpnr;
    CardView planDescCrd, cantChangeTypeCrd;
    ProgressBar spinnerLoaderProgressBar;
    boolean isMapReady = false;
    ///////////////////////
    //      IMAGES ITEMS
    ///////////////////////
    TextView imagesWarn;
    ImageView imgLock1, imgLock2, imgLock3, imgLock4, imgLock5, imgLock6, imgLock7, imgLock8, imgLock9, imgLock10;
    ImageView img1, img2, img3, img4, img5, img6, img7, img8, img9, img10;
    List<String> catList, subCatList, provinceList, cityList, adTypeList;
    int catID = 1, subCatID = 2, provinceID = 3, cityID = 4, adTypeID = 5;
    String catUrl, provinceUrl, adTypeUrl, restUrl;
    List<AdPlans> adPlanses;
    List<HomeSubCategories> subCategoriesObjectList;
    List<HomeSubCategories> homeCategoriesObjectList;
    List<ProvicesAndCities> citiesObjectList;
    List<ProvicesAndCities> provinceObjevtList;
    GoogleMap googleMap;
    LatLng ltLg;
    /////////////////////////////////////////////
    ////////                             ////////
    ////////     MULTI IMAGE SELECTOR    ////////
    ////////                             ////////
    /////////////////////////////////////////////
    int numberOfTheImageView;
    String picpath;
    Bitmap bitmap1;
    //this is to know if a imageView has an image inside, so it would offer to delete it when filled and touched
    boolean[] isBitmapsFilled = new boolean[10];
    Bitmap[] bitmaps = new Bitmap[10];
    //this is the new images
    Bitmap[] newBitmaps = new Bitmap[10];
    //these are to get changed images => 1 for imageChange, 2 for image deletes, 3 for new image
    int[] isImageChanged = new int[10];
    // this is to know if a imageView was empty from the beginning
    boolean[] didThisHaveImage = new boolean[10];
    String title, latitude = "", longitude = "", city_id, address = "", type = "", sub_category_id, mobile, tel1 = "", tel2 = "", link = "", discount = "", working_time = "", telegram = "", instagram = "", notes, ads_plan_id;
    int REQUEST_ID_MULTIPLE_PERMISSIONS1 = 1001;
    private ArrayList<String> mSelectPath;

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public static void bindData(UpdateAd updateAd1) {
        updateAd = updateAd1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ad);

        isMapReady = false;

        for (int i = 0; i < isBitmapsFilled.length; i++) {
            isBitmapsFilled[i] = false;
            isImageChanged[i] = 0;
            didThisHaveImage[i] = false;
        }

        initializer();
        onClicks();
        imagesOnclicks();
        smallStuff();
        spinnerStuff();
        submitOnClick();

        if (!isItNewAd) fillEmptyPlacesFromAdObject();
    }

    private void fillEmptyPlacesFromAdObject() {
        titleEdt.setText(ad.getTitle());
        descriptionEdt.setText(ad.getNotes());
        if (!ad.getWorking_time().equals("null")) workingTimeEdt.setText(ad.getWorking_time());
        if (!ad.getDiscount().equals("null")) discountEdt.setText(ad.getDiscount());
        phoneEdt.setText(ad.getMobile());
        if (!ad.getTel1().equals("null")) tel1Edt.setText(ad.getTel1());
        if (!ad.getTel2().equals("null")) tel2Edt.setText(ad.getTel2());
        if (!ad.getLink().equals("null")) linkEdt.setText(ad.getLink());
        if (!ad.getTelegram().equals("null")) telegramEdt.setText(ad.getTelegram());
        if (!ad.getInstagram().equals("null")) instagramEdt.setText(ad.getInstagram());
        addressEdt.setText(ad.getAddress());
        ownerNameEdt.setText(ad.getAds_owner_name());

        if (ad.getLatitude() != null && !ad.getLatitude().equals("null")) {
            this.ltLg = new LatLng(Double.parseDouble(ad.getLatitude()), Double.parseDouble(ad.getLongitude()));
        }
        if (ad.getEmail() != null && !ad.getEmail().equalsIgnoreCase("null") && !ad.getEmail().equalsIgnoreCase(null)) {
            edt_email.setText(ad.getEmail());
        }

        submitBtn.setText("ثبت تغییرات");

        if (ad.getPhotos().size() > 0) {

            List<ImageView> imgLocks = new ArrayList<ImageView>();
            imgLocks.add(imgLock1);
            imgLocks.add(imgLock2);
            imgLocks.add(imgLock3);
            imgLocks.add(imgLock4);
            imgLocks.add(imgLock5);
            imgLocks.add(imgLock6);
            imgLocks.add(imgLock7);
            imgLocks.add(imgLock8);
            imgLocks.add(imgLock9);
            imgLocks.add(imgLock10);
            List<ImageView> imgs = new ArrayList<ImageView>();
            imgs.add(img1);
            imgs.add(img2);
            imgs.add(img3);
            imgs.add(img4);
            imgs.add(img5);
            imgs.add(img6);
            imgs.add(img7);
            imgs.add(img8);
            imgs.add(img9);
            imgs.add(img10);

            for (int i = 0; i < ad.getPhotos().size(); i++) {
                imgs.get(i).setImageResource(R.drawable.image_in_newad);
                Picasso.get()
                        .load(ad.getPhotos().get(i).getName())
                        .resize(500, 500)
                        .into(imgs.get(i));
                imgLocks.get(i).setVisibility(View.GONE);
                isBitmapsFilled[i] = true;
                didThisHaveImage[i] = true;
            }
        }
    }

    private void initializer() {
        adPlans = new AdPlans();
        txt_mapError = findViewById(R.id.txt_mapError);
        txt_emailWarn = findViewById(R.id.txt_emailWarn);
        edt_email = findViewById(R.id.edt_email);
        txt_Dis_Danger = findViewById(R.id.txt_Dis_Danger);
        lin_remove_map_loc = findViewById(R.id.lin_remove_map_loc);
        lin_remove_map_loc.setVisibility(View.VISIBLE);

        lin_accept_roul = findViewById(R.id.lin_accept_roul);
        lin_accept_roul.setVisibility(isItNewAd ? View.VISIBLE : View.GONE);
        chk_accept_roul = findViewById(R.id.chk_accept_roul);
        txt_show_rouls = findViewById(R.id.txt_show_rouls);

        rootView = (ViewGroup) findViewById(R.id.newAd);
        backInToolbar = (ImageButton) findViewById(R.id.newAdBackButton);

        showMap = (MyCheckbox) findViewById(R.id.newAdShowMap);
        mapLayout = (RelativeLayout) findViewById(R.id.newAdMapLayout);
        scrollView = (ScrollView) findViewById(R.id.newAdScrollView);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.newAdMap);
        supportMapFragment.getMapAsync(this);
        mapOverlay = (ImageView) findViewById(R.id.newAdMapOverlay);

        context = NewAdActivity.this;

        submitBtn = (Button) findViewById(R.id.newAdSubmitBTN);

        titleEdt = (EditText) findViewById(R.id.newAdTitle);
        descriptionEdt = (EditText) findViewById(R.id.newAdDescription);
        workingTimeEdt = (EditText) findViewById(R.id.newAdWorkTime);
        ownerNameEdt = (EditText) findViewById(R.id.newAdOwnerName);
        discountEdt = (EditText) findViewById(R.id.newAdDiscount);
        phoneEdt = (EditText) findViewById(R.id.newAdPhone);
        tel1Edt = (EditText) findViewById(R.id.newAdTel1);
        tel2Edt = (EditText) findViewById(R.id.newAdTel2);
        linkEdt = (EditText) findViewById(R.id.newAdLink);
        telegramEdt = (EditText) findViewById(R.id.newAdTelegram);
        instagramEdt = (EditText) findViewById(R.id.newAdInstagram);
        addressEdt = (EditText) findViewById(R.id.newAdAddress);

        titleWarn = (TextView) findViewById(R.id.newAdTitleWarn);
        descriptionWarn = (TextView) findViewById(R.id.newAdDescriptionWarn);
        catWarn = (TextView) findViewById(R.id.newAdCatWarn);
        subCatWarn = (TextView) findViewById(R.id.newAdSubCatWarn);
        ownerNameWarn = (TextView) findViewById(R.id.newAdOwnerNameWarn);
        adTypeWarn = (TextView) findViewById(R.id.newAdTypeWarn);
        phoneWarn = (TextView) findViewById(R.id.newAdPhoneWarn);
        provinceWarn = (TextView) findViewById(R.id.newAdProvinceWarn);
        cityWarn = (TextView) findViewById(R.id.newAdCityWarn);
        addressWarn = (TextView) findViewById(R.id.newAdAddressWarn);
        majorWarnAtBottom = (TextView) findViewById(R.id.newAdMajorWarnAtBottom);

        catSpnr = (Spinner) findViewById(R.id.newAdCategories);
        subCatSpnr = (Spinner) findViewById(R.id.newAdSubCategories);
        adTypeSpnr = (Spinner) findViewById(R.id.newAdAdKind);
        provinceSpnr = (Spinner) findViewById(R.id.newAdProvince);
        citySpnr = (Spinner) findViewById(R.id.newAdCity);

        spinnerLoaderProgressBar = (ProgressBar) findViewById(R.id.newAdSpinnerLoadProgressBar);

        planDescCrd = (CardView) findViewById(R.id.newAdPlanDescription);
        plansPriceTv = (TextView) findViewById(R.id.newAdPlanPrice);
        plansPicCountTv = (TextView) findViewById(R.id.newAdPlanImageCount);
        plansUpdateCountTv = (TextView) findViewById(R.id.newAdPlanUpdateCount);
        plansIntervalTv = (TextView) findViewById(R.id.newAdPlanInterval);

        cantChangeTypeCrd = (CardView) findViewById(R.id.newAdCantUpdateTypeWarn);
        updatesLeftCountTXT = (TextView) findViewById(R.id.newAdUpdatesLeftCountTXT);
        contactUsBTN = (ImageButton) findViewById(R.id.newAdContactUsBTN);


        ///////////////////////
        //      IMAGES ITEMS
        ///////////////////////
        imagesWarn = (TextView) findViewById(R.id.newAdImagesWarn);
        imgLock1 = (ImageView) findViewById(R.id.newAdImage1lock);
        imgLock2 = (ImageView) findViewById(R.id.newAdImage2lock);
        imgLock3 = (ImageView) findViewById(R.id.newAdImage3lock);
        imgLock4 = (ImageView) findViewById(R.id.newAdImage4lock);
        imgLock5 = (ImageView) findViewById(R.id.newAdImage5lock);
        imgLock6 = (ImageView) findViewById(R.id.newAdImage6lock);
        imgLock7 = (ImageView) findViewById(R.id.newAdImage7lock);
        imgLock8 = (ImageView) findViewById(R.id.newAdImage8lock);
        imgLock9 = (ImageView) findViewById(R.id.newAdImage9lock);
        imgLock10 = (ImageView) findViewById(R.id.newAdImage10lock);
        img1 = (ImageView) findViewById(R.id.newAdImage1);
        img2 = (ImageView) findViewById(R.id.newAdImage2);
        img3 = (ImageView) findViewById(R.id.newAdImage3);
        img4 = (ImageView) findViewById(R.id.newAdImage4);
        img5 = (ImageView) findViewById(R.id.newAdImage5);
        img6 = (ImageView) findViewById(R.id.newAdImage6);
        img7 = (ImageView) findViewById(R.id.newAdImage7);
        img8 = (ImageView) findViewById(R.id.newAdImage8);
        img9 = (ImageView) findViewById(R.id.newAdImage9);
        img10 = (ImageView) findViewById(R.id.newAdImage10);

        imagesWarn.setVisibility(View.GONE);
    }

    private void smallStuff() {
        /////////////////////////////
        //  DISABLING SPINNERS
        catSpnr.setEnabled(false);
        subCatSpnr.setEnabled(false);
        provinceSpnr.setEnabled(false);
        citySpnr.setEnabled(false);
        adTypeSpnr.setEnabled(false);
    }

    private void onClicks() {
        lin_remove_map_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleMap.clear();
                ad.setLatitude(null);
                ad.setLongitude(null);
                TransitionManager.beginDelayedTransition(rootView);
                ltLg = null;
                showMap.setChecked(false);

                ShowToast.success("نشانه با موفقیت حذف گردید.", NewAdActivity.this);

            }
        });

        txt_show_rouls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Show_Rouls_Dialog show_rouls_dialog = new Show_Rouls_Dialog("قوانین", getString(R.string.rouls));
                show_rouls_dialog.show(getSupportFragmentManager(), "show_rouls_dialog");
            }
        });

        contactUsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + "03132730481"));
                startActivity(intent);
            }
        });

        backInToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad = null;
                isItNewAd = true;
                isCitySetOnce = false;
                isSubcatSetOnce = false;
                finish();
            }
        });

        showMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    TransitionManager.beginDelayedTransition(rootView);


                    if (isItNewAd) {

//                        mapLayout.setVisibility(View.VISIBLE);
                        if (isMapReady) {
                            if (checkAndRequestPermissions1(false)){
                                turnonlocation();

                            }else{
//                                AlertDialog.Builder b = new AlertDialog.Builder(context);
//
//                                b.setMessage("برای مکان یابی خودکار باید دسترسی به مکان یاب را بدهید .")
//                                        .setPositiveButton("باشه", new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                checkAndRequestPermissions1(true);
//                                            }
//                                        }) ;
//                                AlertDialog a = b.create();
//
//                                a.show();
//
//                                Button bq = a.getButton(DialogInterface.BUTTON_NEGATIVE);
//                                Button bq1 = a.getButton(DialogInterface.BUTTON_POSITIVE);
//                                bq1.setTextColor(context.getResources().getColor(R.color.colorPrimary));
//                                bq1.setTextSize(16);
                            }
                        }
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

//                        mapLayout.setVisibility(View.VISIBLE);
//
//                        GPSTracker gpsTracker = new GPSTracker(NewAdActivity.this);
//                        Location location2=gpsTracker.getLocation();
//
//
//                        if (location2 != null)
//                        {
//                            Log.i("11111111111111", "locationFound");
//                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location2.getLatitude(), location2.getLongitude()), 13));
//
//                            CameraPosition cameraPosition = new CameraPosition.Builder()
//                                    .target(new LatLng(location2.getLatitude(), location2.getLongitude()))      // Sets the center of the map to location user
//                                    .zoom(15)                   // Sets the zoom
//                                    .build();                   // Creates a CameraPosition from the builder
//                            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                            Log.i("11111111111111", "locationFound");
//
//                            //TODO check if this happens, then give permission to show noAdAroundWarn
//                        }
                    } else mapLayout.setVisibility(View.VISIBLE);

                } else {
                    TransitionManager.beginDelayedTransition(rootView);

                    mapLayout.setVisibility(View.GONE);
                }
            }
        });


        ///////////////////////////////////
        /////       THIS IS TO MAKE MAP USABLE
        /////            IN SCROLLVIEW
        ///////////////////////////////////
        mapOverlay.setOnTouchListener(new View.OnTouchListener() {
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

    public void turnonlocation() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(NewAdActivity.this).addApi(LocationServices.API).build();
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

                        GPSTracker gpsTracker = new GPSTracker(NewAdActivity.this);
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

                        if (ActivityCompat.checkSelfPermission(NewAdActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NewAdActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        } else
//                            gmap.setMyLocationEnabled(true);
                            break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        chbAddLoction.setChecked(false);
//                        Log.i("abcd", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            showMap.setChecked(false);
                            status.startResolutionForResult(NewAdActivity.this, 0x1);
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

    private void spinnerStuff() {
        catList = new ArrayList<>();
        subCatList = new ArrayList<>();
        provinceList = new ArrayList<>();
        cityList = new ArrayList<>();
        adTypeList = new ArrayList<>();

        catList.add("انتخاب کنید");
        subCatList.add("انتخاب کنید");
        provinceList.add("انتخاب کنید");
        cityList.add("انتخاب کنید");
        adTypeList.add("انتخاب کنید");

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(context, R.layout.item_spinner_layout, catList);
        ArrayAdapter<String> subCatAdapter = new ArrayAdapter<>(context, R.layout.item_spinner_layout, subCatList);
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(context, R.layout.item_spinner_layout, provinceList);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(context, R.layout.item_spinner_layout, cityList);
        ArrayAdapter<String> adTypeAdapter = new ArrayAdapter<>(context, R.layout.item_spinner_layout, adTypeList);

        catSpnr.setAdapter(catAdapter);
        subCatSpnr.setAdapter(subCatAdapter);
        provinceSpnr.setAdapter(provinceAdapter);
        citySpnr.setAdapter(cityAdapter);
        adTypeSpnr.setAdapter(adTypeAdapter);


        loadSpinnerData();
        spinnerItemSelects();
    }

    private void spinnerItemSelects() {
        adTypeSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {

                    TransitionManager.beginDelayedTransition(rootView);

                    List<ImageView> imgLocks = new ArrayList<ImageView>();
                    imgLocks.add(imgLock1);
                    imgLocks.add(imgLock2);
                    imgLocks.add(imgLock3);
                    imgLocks.add(imgLock4);
                    imgLocks.add(imgLock5);
                    imgLocks.add(imgLock6);
                    imgLocks.add(imgLock7);
                    imgLocks.add(imgLock8);
                    imgLocks.add(imgLock9);
                    imgLocks.add(imgLock10);
                    List<ImageView> imgs = new ArrayList<ImageView>();
                    imgs.add(img1);
                    imgs.add(img2);
                    imgs.add(img3);
                    imgs.add(img4);
                    imgs.add(img5);
                    imgs.add(img6);
                    imgs.add(img7);
                    imgs.add(img8);
                    imgs.add(img9);
                    imgs.add(img10);

                    if (position == 0 && isItNewAd) {
                        planDescCrd.setVisibility(View.GONE);
                        plansPriceTv.setText("- -");

                        imagesWarn.setVisibility(View.VISIBLE);
                        for (ImageView iv : imgLocks) {
                            iv.setVisibility(View.VISIBLE);
                            iv.setImageResource(R.drawable.ic_lock_black_24dp);
                            iv.setColorFilter(Color.parseColor("#F4511E"));
                        }
                        for (ImageView iv : imgs) {
                            iv.setImageResource(R.drawable.image_in_newad);
                        }
                        for (int i = 0; i < bitmaps.length; i++) {
                            bitmaps[i] = null;
                        }

                    } else {

                        if (position == 1) plansPriceTv.setText("- -");
                        else
                            plansPriceTv.setText(adPlanses.get(position - 1).getPrice() + "   تومان");

                        /////// CHECKING FOR ACTIVITY INTRO TYPE
                        if (isItNewAd) {
                            plansUpdateCountTv.setText(adPlanses.get(position - 1).getNum_of_updates());
                            plansPicCountTv.setText(adPlanses.get(position - 1).getMax_number_of_photos());
                            plansIntervalTv.setText(adPlanses.get(position - 1).getInterval_days());

                            planDescCrd.setVisibility(View.VISIBLE);
                        } else {
//                            updatesLeftCountTXT.setText(Integer.parseInt(ad.getMax_number_of_update()) - Integer.parseInt(ad.getUpdates_count()));

                            cantChangeTypeCrd.setVisibility(View.VISIBLE);
                        }

                        imagesWarn.setVisibility(View.GONE);
                        for (ImageView iv : imgLocks) {
                            iv.setImageResource(R.drawable.ic_lock_black_24dp);
                            iv.setColorFilter(Color.parseColor("#F4511E"));
                        }
                        for (int i = 0; i < Integer.parseInt(adPlanses.get(position - 1).getMax_number_of_photos()); i++) {
//                        imgLocks.get(i).setVisibility(View.GONE);
                            if (bitmaps[i] == null) {
                                imgLocks.get(i).setImageResource(R.drawable.ic_add_black_24dp);
                                imgLocks.get(i).setColorFilter(Color.parseColor("#757575"));
                            } else imgLocks.get(i).setVisibility(View.GONE);
                        }
                        for (int i = 9; i >= Integer.parseInt(adPlanses.get(position - 1).getMax_number_of_photos()); i--) {
                            imgLocks.get(i).setVisibility(View.VISIBLE);
                            imgs.get(i).setImageResource(R.drawable.image_in_newad);
                            bitmaps[i] = null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        catSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    TransitionManager.beginDelayedTransition(rootView);

                    subCatSpnr.setEnabled(false);

                    spinnerLoaderProgressBar.setVisibility(View.VISIBLE);

                    subCatList.clear();
                    subCatList.add("انتخاب کنید");
                    subCatSpnr.setSelection(0);

                    if (position != 0) {

                        String url = StaticData.DOMAIN_WITH_API + "/categories/" + homeCategoriesObjectList.get(position - 1).getId() + "/subcategories/all";
                        loadRestOfSpinnersData(subCatID, url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        provinceSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    TransitionManager.beginDelayedTransition(rootView);

                    citySpnr.setEnabled(false);

                    spinnerLoaderProgressBar.setVisibility(View.VISIBLE);

                    cityList.clear();
                    cityList.add("انتخاب کنید");
                    citySpnr.setSelection(0);

                    if (position != 0) {

                        String url = StaticData.DOMAIN_WITH_API + "/provinces/" + position + "/cities";
                        loadRestOfSpinnersData(cityID, url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSpinnerData() {
        catUrl = StaticData.All_CATEGORIES;
        provinceUrl = StaticData.PROVINCE;
        adTypeUrl = StaticData.PLANS;
        Log.v("url", adTypeUrl);

        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(context, params, catUrl, Request.Method.GET, catID);

        Get_Volley_Call_Back2.binddata(this);
        Get_Volley_Call_Back2.Call_Volley(context, params, provinceUrl, Request.Method.GET, provinceID);

        Get_Volley_Call_Back3.binddata(this);
        Get_Volley_Call_Back3.Call_Volley(context, params, adTypeUrl, Request.Method.GET, adTypeID);
    }

    private void loadRestOfSpinnersData(int id, String url) {

        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back4.binddata(this);
        Get_Volley_Call_Back4.Call_Volley(context, params, url, Request.Method.GET, id);
    }

    @Override
    public void on_volley_response(String response, int id) {

        if (id == catID) {

            try {
                JSONObject jsonObject = new JSONObject(response);

                homeCategoriesObjectList = HomeSubCategories.Categories(jsonObject);
                for (HomeSubCategories cat : homeCategoriesObjectList) {
                    catList.add(cat.getName());
                }
                catSpnr.setEnabled(true);

                if (!isItNewAd) {
                    for (int i = 0; i < homeCategoriesObjectList.size(); i++) {
                        if (homeCategoriesObjectList.get(i).getId().equals(ad.getCategory_id())) {
                            catSpnr.setSelection(i + 1);
                            i = homeCategoriesObjectList.size();

                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (id == provinceID) {

            try {
                JSONObject jsonObject = new JSONObject(response);
                provinceObjevtList = ProvicesAndCities.cities(jsonObject);
                for (ProvicesAndCities city : provinceObjevtList) {
                    provinceList.add(city.getName());
                }
                provinceSpnr.setEnabled(true);

                if (!isItNewAd) {
                    for (int i = 0; i < provinceObjevtList.size(); i++) {
                        if (provinceObjevtList.get(i).getId().equals(ad.getProvince_id())) {
                            provinceSpnr.setSelection(i + 1);
                            i = provinceObjevtList.size();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (id == adTypeID) {
            TransitionManager.beginDelayedTransition(rootView);
            spinnerLoaderProgressBar.setVisibility(View.INVISIBLE);

            try {
                JSONObject jsonObject = new JSONObject(response);
                adPlanses = AdPlans.Import(jsonObject);

                for (AdPlans ad : adPlanses) {
                    adTypeList.add(ad.getPlan_title());
                }

                if (isItNewAd) {
                    setdefaltPlane();
                }
//                adTypeSpnr.setEnabled(true);

                if (!isItNewAd) {
                    for (int i = 0; i < adPlanses.size(); i++) {
                        if (ad.getAds_plan_id().equals(adPlanses.get(i).getId()))
                            adTypeSpnr.setSelection(i + 1);
                    }

//                    adTypeSpnr.setSelection(Integer.parseInt(ad.getAds_plan_id()) + 1);
                } else adTypeSpnr.setEnabled(true);

                if (!isItNewAd) {
                    setadspanel();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (id == subCatID) {
            TransitionManager.beginDelayedTransition(rootView);
            spinnerLoaderProgressBar.setVisibility(View.INVISIBLE);

            try {
                JSONObject jsonObject = new JSONObject(response);
                subCategoriesObjectList = HomeSubCategories.Categories(jsonObject);
                for (HomeSubCategories sub : subCategoriesObjectList) {
                    subCatList.add(sub.getName());
                }
                subCatSpnr.setEnabled(true);

                if (!isItNewAd && !isSubcatSetOnce) {
                    isSubcatSetOnce = true;
                    for (int i = 0; i < subCategoriesObjectList.size(); i++) {
                        if (subCategoriesObjectList.get(i).getId().equals(ad.getSub_category_id())) {
                            subCatSpnr.setSelection(i + 1);
                            i = subCategoriesObjectList.size();

                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (id == cityID) {
            TransitionManager.beginDelayedTransition(rootView);
            spinnerLoaderProgressBar.setVisibility(View.INVISIBLE);

            try {
                JSONObject jsonObject = new JSONObject(response);
                citiesObjectList = ProvicesAndCities.cities(jsonObject);
                for (ProvicesAndCities city : citiesObjectList) {
                    cityList.add(city.getName());
                }
                citySpnr.setEnabled(true);

                if (!isItNewAd && !isCitySetOnce) {
                    isCitySetOnce = true;
                    for (int i = 0; i < citiesObjectList.size(); i++) {
                        if (ad.getCity_id().equals(citiesObjectList.get(i).getId())) {
                            citySpnr.setSelection(i + 1);
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

    }

    private void setdefaltPlane() {
        List<ImageView> imgLocks = new ArrayList<ImageView>();
        imgLocks.add(imgLock1);
        imgLocks.add(imgLock2);
        imgLocks.add(imgLock3);
        imgLocks.add(imgLock4);
        imgLocks.add(imgLock5);
        imgLocks.add(imgLock6);
        imgLocks.add(imgLock7);
        imgLocks.add(imgLock8);
        imgLocks.add(imgLock9);
        imgLocks.add(imgLock10);
        List<ImageView> imgs = new ArrayList<ImageView>();
        imgs.add(img1);
        imgs.add(img2);
        imgs.add(img3);
        imgs.add(img4);
        imgs.add(img5);
        imgs.add(img6);
        imgs.add(img7);
        imgs.add(img8);
        imgs.add(img9);
        imgs.add(img10);

        for (ImageView iv : imgLocks) {
            iv.setImageResource(R.drawable.ic_lock_black_24dp);
            iv.setColorFilter(Color.parseColor("#F4511E"));
        }
        int maxphoto = 0;
        for (int i = 0; i < adPlanses.size(); i++) {
            Log.v("plan_id", adPlanses.get(i).getId());
            if (adPlanses.get(i).getId().equals("17")) {
                maxphoto = Integer.parseInt(adPlanses.get(i).getMax_number_of_photos());
                adPlans = adPlanses.get(i);
            }
        }


        for (int i = 0; i < maxphoto; i++) {
//                        imgLocks.get(i).setVisibility(View.GONE);
            if (bitmaps[i] == null) {
                imgLocks.get(i).setImageResource(R.drawable.ic_add_black_24dp);
                imgLocks.get(i).setColorFilter(Color.parseColor("#757575"));
            } else imgLocks.get(i).setVisibility(View.GONE);
        }
        for (int i = 9; i >= maxphoto; i--) {
            imgLocks.get(i).setVisibility(View.VISIBLE);
            imgs.get(i).setImageResource(R.drawable.image_in_newad);
            bitmaps[i] = null;
        }
    }

    private void setadspanel() {
        List<ImageView> imgLocks = new ArrayList<ImageView>();
        imgLocks.add(imgLock1);
        imgLocks.add(imgLock2);
        imgLocks.add(imgLock3);
        imgLocks.add(imgLock4);
        imgLocks.add(imgLock5);
        imgLocks.add(imgLock6);
        imgLocks.add(imgLock7);
        imgLocks.add(imgLock8);
        imgLocks.add(imgLock9);
        imgLocks.add(imgLock10);
        List<ImageView> imgs = new ArrayList<ImageView>();
        imgs.add(img1);
        imgs.add(img2);
        imgs.add(img3);
        imgs.add(img4);
        imgs.add(img5);
        imgs.add(img6);
        imgs.add(img7);
        imgs.add(img8);
        imgs.add(img9);
        imgs.add(img10);

        for (ImageView iv : imgLocks) {
            iv.setImageResource(R.drawable.ic_lock_black_24dp);
            iv.setColorFilter(Color.parseColor("#F4511E"));
        }
        int maxphoto = 0;
        for (int i = 0; i < adPlanses.size(); i++) {
            Log.v("plan_id", adPlanses.get(i).getId());
            if (adPlanses.get(i).getId().equals(ad.getAds_plan_id())) {
                maxphoto = Integer.parseInt(adPlanses.get(i).getMax_number_of_photos());
                adPlans = adPlanses.get(i);
            }
        }


        for (int i = 0; i < maxphoto; i++) {
//                        imgLocks.get(i).setVisibility(View.GONE);
            if (bitmaps[i] == null) {
                imgLocks.get(i).setImageResource(R.drawable.ic_add_black_24dp);
                imgLocks.get(i).setColorFilter(Color.parseColor("#757575"));
            } else imgLocks.get(i).setVisibility(View.GONE);
        }
        for (int i = 9; i >= maxphoto; i--) {
            imgLocks.get(i).setVisibility(View.VISIBLE);
            imgs.get(i).setImageResource(R.drawable.image_in_newad);
            bitmaps[i] = null;
        }
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
        } else {
            if (!ad.getLatitude().equals("null")) {
                showMap.setChecked(true);

                LatLng ll = new LatLng(Double.parseDouble(ad.getLatitude()), Double.parseDouble(ad.getLongitude()));
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
                googleMap.moveCamera(update);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(ll);
                markerOptions.title(ad.getAddress());
                googleMap.clear();
                googleMap.addMarker(markerOptions);

            } else {
                LatLng ll = new LatLng(32.539245, 53.816336);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 4.5f);
                googleMap.moveCamera(update);
            }
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

    private void imagesOnclicks() {
        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (adTypeSpnr.getSelectedItemPosition() != 0) {
                if (!isBitmapsFilled[0]) {
                    if (Integer.parseInt(adPlans.getMax_number_of_photos()) >= 1) {

                        pickImage();
                        numberOfTheImageView = 1;
                    }
                } else {
                    AlertDialog.Builder builder;

                    builder = new AlertDialog.Builder(NewAdActivity.this);

                    builder.setTitle("")
                            .setMessage("آیا از حذف عکس اطمینان دارید؟")
                            .setPositiveButton("آری", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    bitmaps[0] = null;
                                    isBitmapsFilled[0] = false;
                                    imgLock1.setVisibility(View.VISIBLE);
                                    img1.setImageResource(R.drawable.image_in_newad);
                                    if (didThisHaveImage[0]) isImageChanged[0] = 2;
                                    else isImageChanged[0] = 3;
                                }
                            })
                            .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
//                }
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (adTypeSpnr.getSelectedItemPosition() != 0) {
                if (!isBitmapsFilled[1]) {
                    if (Integer.parseInt(adPlans.getMax_number_of_photos()) >= 2) {

                        pickImage();
                        numberOfTheImageView = 2;
                    }
                } else {
                    AlertDialog.Builder builder;

                    builder = new AlertDialog.Builder(NewAdActivity.this);

                    builder.setTitle("")
                            .setMessage("آیا از حذف عکس اطمینان دارید؟")
                            .setPositiveButton("آری", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    bitmaps[1] = null;
                                    isBitmapsFilled[1] = false;
                                    imgLock2.setVisibility(View.VISIBLE);
                                    img2.setImageResource(R.drawable.image_in_newad);
                                    if (didThisHaveImage[1]) isImageChanged[1] = 2;
                                    else isImageChanged[1] = 3;
                                }
                            })
                            .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
//                }
            }
        });
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (adTypeSpnr.getSelectedItemPosition() != 0) {
                if (!isBitmapsFilled[2]) {
                    if (Integer.parseInt(adPlans.getMax_number_of_photos()) >= 3) {

                        pickImage();
                        numberOfTheImageView = 3;
                    }
                } else {
                    AlertDialog.Builder builder;

                    builder = new AlertDialog.Builder(NewAdActivity.this);

                    builder.setTitle("")
                            .setMessage("آیا از حذف عکس اطمینان دارید؟")
                            .setPositiveButton("آری", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    bitmaps[2] = null;
                                    isBitmapsFilled[2] = false;
                                    imgLock3.setVisibility(View.VISIBLE);
                                    img3.setImageResource(R.drawable.image_in_newad);
                                    if (didThisHaveImage[2]) isImageChanged[2] = 2;
                                    else isImageChanged[2] = 3;
                                }
                            })
                            .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
//                }
            }
        });
        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (adTypeSpnr.getSelectedItemPosition() != 0) {
                if (!isBitmapsFilled[3]) {
                    if (Integer.parseInt(adPlans.getMax_number_of_photos()) >= 4) {

                        pickImage();
                        numberOfTheImageView = 4;
                    }
                } else {
                    AlertDialog.Builder builder;

                    builder = new AlertDialog.Builder(NewAdActivity.this);

                    builder.setTitle("")
                            .setMessage("آیا از حذف عکس اطمینان دارید؟")
                            .setPositiveButton("آری", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    bitmaps[3] = null;
                                    isBitmapsFilled[3] = false;
                                    imgLock4.setVisibility(View.VISIBLE);
                                    img4.setImageResource(R.drawable.image_in_newad);
                                    if (didThisHaveImage[3]) isImageChanged[3] = 2;
                                    else isImageChanged[3] = 3;
                                }
                            })
                            .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
//                }
            }
        });
        img5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (adTypeSpnr.getSelectedItemPosition() != 0) {
                if (!isBitmapsFilled[4]) {
                    if (Integer.parseInt(adPlans.getMax_number_of_photos()) >= 5) {

                        pickImage();
                        numberOfTheImageView = 5;
                    }
                } else {
                    AlertDialog.Builder builder;

                    builder = new AlertDialog.Builder(NewAdActivity.this);

                    builder.setTitle("")
                            .setMessage("آیا از حذف عکس اطمینان دارید؟")
                            .setPositiveButton("آری", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    bitmaps[4] = null;
                                    isBitmapsFilled[4] = false;
                                    imgLock5.setVisibility(View.VISIBLE);
                                    img5.setImageResource(R.drawable.image_in_newad);
                                    if (didThisHaveImage[4]) isImageChanged[4] = 2;
                                    else isImageChanged[4] = 3;
                                }
                            })
                            .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
//                }
            }
        });
        img6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (adTypeSpnr.getSelectedItemPosition() != 0) {
                if (!isBitmapsFilled[5]) {
                    if (Integer.parseInt(adPlans.getMax_number_of_photos()) >= 6) {

                        pickImage();
                        numberOfTheImageView = 6;
                    }
                } else {
                    AlertDialog.Builder builder;

                    builder = new AlertDialog.Builder(NewAdActivity.this);

                    builder.setTitle("")
                            .setMessage("آیا از حذف عکس اطمینان دارید؟")
                            .setPositiveButton("آری", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    bitmaps[5] = null;
                                    isBitmapsFilled[5] = false;
                                    imgLock6.setVisibility(View.VISIBLE);
                                    img6.setImageResource(R.drawable.image_in_newad);
                                    if (didThisHaveImage[5]) isImageChanged[5] = 2;
                                    else isImageChanged[5] = 3;
                                }
                            })
                            .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
//                }
            }
        });
        img7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (adTypeSpnr.getSelectedItemPosition() != 0) {
                if (!isBitmapsFilled[6]) {
                    if (Integer.parseInt(adPlans.getMax_number_of_photos()) >= 7) {

                        pickImage();
                        numberOfTheImageView = 7;
                    }
                } else {
                    AlertDialog.Builder builder;

                    builder = new AlertDialog.Builder(NewAdActivity.this);

                    builder.setTitle("")
                            .setMessage("آیا از حذف عکس اطمینان دارید؟")
                            .setPositiveButton("آری", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    bitmaps[6] = null;
                                    isBitmapsFilled[6] = false;
                                    imgLock7.setVisibility(View.VISIBLE);
                                    img7.setImageResource(R.drawable.image_in_newad);
                                    if (didThisHaveImage[6]) isImageChanged[6] = 2;
                                    else isImageChanged[6] = 3;
                                }
                            })
                            .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
//                }
            }
        });
        img8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (adTypeSpnr.getSelectedItemPosition() != 0) {
                if (!isBitmapsFilled[7]) {
                    if (Integer.parseInt(adPlans.getMax_number_of_photos()) >= 8) {

                        pickImage();
                        numberOfTheImageView = 8;
                    }
                } else {
                    AlertDialog.Builder builder;

                    builder = new AlertDialog.Builder(NewAdActivity.this);

                    builder.setTitle("")
                            .setMessage("آیا از حذف عکس اطمینان دارید؟")
                            .setPositiveButton("آری", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    bitmaps[7] = null;
                                    isBitmapsFilled[7] = false;
                                    imgLock8.setVisibility(View.VISIBLE);
                                    img8.setImageResource(R.drawable.image_in_newad);
                                    if (didThisHaveImage[7]) isImageChanged[7] = 2;
                                    else isImageChanged[7] = 3;
                                }
                            })
                            .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
//                }
            }
        });
        img9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (adTypeSpnr.getSelectedItemPosition() != 0) {
                if (!isBitmapsFilled[8]) {
                    if (Integer.parseInt(adPlanses.get(adTypeSpnr.getSelectedItemPosition() - 1).getMax_number_of_photos()) >= 9) {

                        pickImage();
                        numberOfTheImageView = 9;
                    }
                } else {
                    AlertDialog.Builder builder;

                    builder = new AlertDialog.Builder(NewAdActivity.this);

                    builder.setTitle("")
                            .setMessage("آیا از حذف عکس اطمینان دارید؟")
                            .setPositiveButton("آری", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    bitmaps[8] = null;
                                    isBitmapsFilled[8] = false;
                                    imgLock9.setVisibility(View.VISIBLE);
                                    img9.setImageResource(R.drawable.image_in_newad);
                                    if (didThisHaveImage[8]) isImageChanged[8] = 2;
                                    else isImageChanged[8] = 3;
                                }
                            })
                            .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
//                }
            }
        });
        img10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (adTypeSpnr.getSelectedItemPosition() != 0) {
                if (!isBitmapsFilled[9]) {
                    if (Integer.parseInt(adPlanses.get(adTypeSpnr.getSelectedItemPosition() - 1).getMax_number_of_photos()) >= 10) {

                        pickImage();
                        numberOfTheImageView = 10;
                    }
                } else {
                    AlertDialog.Builder builder;

                    builder = new AlertDialog.Builder(NewAdActivity.this);

                    builder.setTitle("")
                            .setMessage("آیا از حذف عکس اطمینان دارید؟")
                            .setPositiveButton("آری", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    bitmaps[9] = null;
                                    isBitmapsFilled[9] = false;
                                    imgLock10.setVisibility(View.VISIBLE);
                                    img10.setImageResource(R.drawable.image_in_newad);
                                    if (didThisHaveImage[9]) isImageChanged[9] = 2;
                                    else isImageChanged[9] = 3;
                                }
                            })
                            .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
//                }
            }
        });
    }

    private void pickImage() {
        if (!checkAndRequestPermissions(false)) {
        } else {
//            Log.v("permission","no");
            boolean showCamera = true;
            MultiImageSelector selector = MultiImageSelector.create(NewAdActivity.this);
            selector.showCamera(showCamera);
            selector.single();
            selector.origin(mSelectPath);
            selector.start(NewAdActivity.this, REQUEST_IMAGE);
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
                AlertDialog.Builder b = new AlertDialog.Builder(context);

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
                bq1.setTextColor(context.getResources().getColor(R.color.colorPrimary));
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

                    if (numberOfTheImageView == 1) {
                        img1.setImageBitmap(bitmap);
                        bitmaps[0] = bitmap;
                        imgLock1.setVisibility(View.GONE);
                        isBitmapsFilled[0] = true;
                        if (didThisHaveImage[0]) isImageChanged[0] = 1;
                        else isImageChanged[0] = 3;
                    } else if (numberOfTheImageView == 2) {
                        img2.setImageBitmap(bitmap);
                        bitmaps[1] = bitmap;
                        imgLock2.setVisibility(View.GONE);
                        isBitmapsFilled[1] = true;
                        if (didThisHaveImage[1]) isImageChanged[1] = 1;
                        else isImageChanged[1] = 3;
                    } else if (numberOfTheImageView == 3) {
                        img3.setImageBitmap(bitmap);
                        bitmaps[2] = bitmap;
                        imgLock3.setVisibility(View.GONE);
                        isBitmapsFilled[2] = true;
                        if (didThisHaveImage[2]) isImageChanged[2] = 1;
                        else isImageChanged[2] = 3;
                    } else if (numberOfTheImageView == 4) {
                        img4.setImageBitmap(bitmap);
                        bitmaps[3] = bitmap;
                        imgLock4.setVisibility(View.GONE);
                        isBitmapsFilled[3] = true;
                        if (didThisHaveImage[3]) isImageChanged[3] = 1;
                        else isImageChanged[3] = 3;
                    } else if (numberOfTheImageView == 5) {
                        img5.setImageBitmap(bitmap);
                        bitmaps[4] = bitmap;
                        imgLock5.setVisibility(View.GONE);
                        isBitmapsFilled[4] = true;
                        if (didThisHaveImage[4]) isImageChanged[4] = 1;
                        else isImageChanged[4] = 3;
                    } else if (numberOfTheImageView == 6) {
                        img6.setImageBitmap(bitmap);
                        bitmaps[5] = bitmap;
                        imgLock6.setVisibility(View.GONE);
                        isBitmapsFilled[5] = true;
                        if (didThisHaveImage[5]) isImageChanged[5] = 1;
                        else isImageChanged[5] = 3;
                    } else if (numberOfTheImageView == 7) {
                        img7.setImageBitmap(bitmap);
                        bitmaps[6] = bitmap;
                        imgLock7.setVisibility(View.GONE);
                        isBitmapsFilled[6] = true;
                        if (didThisHaveImage[6]) isImageChanged[6] = 1;
                        else isImageChanged[6] = 3;
                    } else if (numberOfTheImageView == 8) {
                        img8.setImageBitmap(bitmap);
                        bitmaps[7] = bitmap;
                        imgLock8.setVisibility(View.GONE);
                        isBitmapsFilled[7] = true;
                        if (didThisHaveImage[7]) isImageChanged[7] = 1;
                        else isImageChanged[7] = 3;
                    } else if (numberOfTheImageView == 9) {
                        img9.setImageBitmap(bitmap);
                        bitmaps[8] = bitmap;
                        imgLock9.setVisibility(View.GONE);
                        isBitmapsFilled[8] = true;
                        if (didThisHaveImage[8]) isImageChanged[8] = 1;
                        else isImageChanged[8] = 3;
                    } else if (numberOfTheImageView == 10) {
                        img10.setImageBitmap(bitmap);
                        bitmaps[9] = bitmap;
                        imgLock10.setVisibility(View.GONE);
                        isBitmapsFilled[9] = true;
                        if (didThisHaveImage[9]) isImageChanged[9] = 1;
                        else isImageChanged[9] = 3;
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

    private void submitOnClick() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isDataValid()) {
//                    fillInfo();
                    doUpload();
                }
            }
        });
    }

    private boolean isDataValid() {
        TransitionManager.beginDelayedTransition(rootView);
        int errorCount = 0;

        if (titleEdt.getText().toString().trim().length() == 0) {
            titleWarn.setVisibility(View.VISIBLE);
            errorCount++;
        } else titleWarn.setVisibility(View.INVISIBLE);
        if (catSpnr.getSelectedItemPosition() == 0) {
            catWarn.setVisibility(View.VISIBLE);
            errorCount++;
        }
        if (subCatSpnr.getSelectedItemPosition() == 0) {
            subCatWarn.setVisibility(View.VISIBLE);
            errorCount++;
        } else subCatWarn.setVisibility(View.INVISIBLE);
//        if (adTypeSpnr.getSelectedItemPosition() == 0) {
//            adTypeWarn.setVisibility(View.VISIBLE);
//            errorCount++;
//        } else adTypeWarn.setVisibility(View.INVISIBLE);
        if (ownerNameEdt.getText().toString().trim().length() == 0) {
            ownerNameWarn.setVisibility(View.VISIBLE);
            errorCount++;
        } else ownerNameWarn.setVisibility(View.INVISIBLE);
//        if (phoneEdt.getText().toString().trim().length() == 0) {
//            phoneWarn.setVisibility(View.VISIBLE);
//            errorCount++;
//        } else phoneWarn.setVisibility(View.INVISIBLE);
        if (provinceSpnr.getSelectedItemPosition() == 0) {
            provinceWarn.setVisibility(View.VISIBLE);
            errorCount++;
        } else provinceWarn.setVisibility(View.INVISIBLE);
        if (citySpnr.getSelectedItemPosition() == 0) {
            cityWarn.setVisibility(View.VISIBLE);
            errorCount++;
        } else cityWarn.setVisibility(View.INVISIBLE);
        if (descriptionEdt.getText().toString().trim().length() == 0) {
            descriptionWarn.setVisibility(View.VISIBLE);
            errorCount++;
        } else descriptionWarn.setVisibility(View.INVISIBLE);
        if (addressEdt.getText().toString().trim().length() == 0) {
            addressWarn.setVisibility(View.VISIBLE);
            errorCount++;
        } else addressWarn.setVisibility(View.INVISIBLE);

        if (discountEdt.getText().toString().trim().length() == 0) {
            txt_Dis_Danger.setVisibility(View.VISIBLE);
            errorCount++;
        } else discountEdt.setVisibility(View.INVISIBLE);

        if (edt_email.getText().length() != 0 & !Utilis.isValidEmail(edt_email.getText().toString())) {
            txt_emailWarn.setVisibility(View.VISIBLE);
            errorCount++;

        } else txt_emailWarn.setVisibility(View.GONE);

        if (ltLg == null) {
            txt_mapError.setVisibility(View.VISIBLE);
            errorCount++;

        } else {
            txt_mapError.setVisibility(View.INVISIBLE);

        }


        if (isItNewAd && !chk_accept_roul.isChecked()) {
            ShowToast.failure("برای ثبت آگهی باید با قوانین و مقررات موافقت کنید", NewAdActivity.this);
            errorCount++;
        }

        if (errorCount != 0) majorWarnAtBottom.setVisibility(View.VISIBLE);
        else majorWarnAtBottom.setVisibility(View.GONE);

        return errorCount == 0;
    }

    private void doUpload() {
        final ProgressDialog prgDialog = new ProgressDialog(NewAdActivity.this);
        prgDialog.setMessage("درحال بارگذاری اطلاعات...");
        prgDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {

                String handleInserUrl;

                if (isItNewAd) handleInserUrl = StaticData.NEW_AD;
                else handleInserUrl = StaticData.UPDATE_AD + ad.getId() + "/update";


                try {
                    okhttp3.MultipartBody.Builder reqEntity = new okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM);

                    String token = new UserSessionManager(NewAdActivity.this).getLoginToken();

                    // Legal record of the consent the user gave before submitting.
                    reqEntity.addFormDataPart("terms_accepted", chk_accept_roul.isChecked() ? "1" : "0");
                    reqEntity.addFormDataPart("terms_version", StaticData.TERMS_VERSION);
                    reqEntity.addFormDataPart("app_version", BuildConfig.VERSION_NAME);

                    reqEntity.addFormDataPart("title", titleEdt.getText().toString().trim());
                    reqEntity.addFormDataPart("city_id", citiesObjectList.get(citySpnr.getSelectedItemPosition() - 1).getId());
                    reqEntity.addFormDataPart("sub_category_id", subCategoriesObjectList.get(subCatSpnr.getSelectedItemPosition() - 1).getId());
                    reqEntity.addFormDataPart("ads_plan_id", "17");
                    reqEntity.addFormDataPart("mobile", phoneEdt.getText().toString().trim());
                    reqEntity.addFormDataPart("notes", descriptionEdt.getText().toString().trim());
                    reqEntity.addFormDataPart("ads_owner_name", ownerNameEdt.getText().toString().trim());
                    if (discountEdt.getText().toString().trim().length() != 0 && Integer.parseInt(discountEdt.getText().toString().trim()) > 0) {
                        reqEntity.addFormDataPart("discount", discountEdt.getText().toString().trim());
                        reqEntity.addFormDataPart("type", "discount");
                    } else {
                        reqEntity.addFormDataPart("type", "need");
                    }
                    if (ltLg != null) {
                        reqEntity.addFormDataPart("latitude", ltLg.latitude + "");
                        reqEntity.addFormDataPart("longitude", ltLg.longitude + "");
                    }
                    if (addressEdt.getText().toString().trim().length() != 0) {
                        reqEntity.addFormDataPart("address", addressEdt.getText().toString().trim());
                    }
                    if (tel1Edt.getText().toString().trim().length() != 0) {
                        reqEntity.addFormDataPart("tel1", tel1Edt.getText().toString().trim());
                    }
                    if (tel2Edt.getText().toString().trim().length() != 0) {
                        reqEntity.addFormDataPart("tel2", tel2Edt.getText().toString().trim());
                    }
                    if (workingTimeEdt.getText().toString().trim().length() != 0) {
                        reqEntity.addFormDataPart("working_time", workingTimeEdt.getText().toString().trim());
                    }
                    if (linkEdt.getText().toString().trim().length() != 0) {
                        reqEntity.addFormDataPart("link", linkEdt.getText().toString().trim());
                    }
                    if (telegramEdt.getText().toString().trim().length() != 0) {
                        reqEntity.addFormDataPart("telegram", telegramEdt.getText().toString().trim());
                    }
                    if (instagramEdt.getText().toString().trim().length() != 0) {
                        reqEntity.addFormDataPart("instagram", instagramEdt.getText().toString().trim());
                    }
                    if (edt_email.getText().toString().trim().length() != 0) {
                        reqEntity.addFormDataPart("email", edt_email.getText().toString().trim());
                    }


                    if (isItNewAd) {

                        for (Bitmap bitmap : bitmaps) {
                            long time = System.currentTimeMillis();
                            if (bitmap != null) {
                                String pathTemp = Compress_image.reductImageSize(time + ".jpg", bitmap);
                                File photoFile = new File(pathTemp);
                                reqEntity.addFormDataPart("photos[]", photoFile.getName(), okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), photoFile));
                            }
                        }
                    } else {

                        for (int i = 0; i < isImageChanged.length; i++) {
//                            Log.i("00000000000000", isImageChanged[i]+"");
                            if (isImageChanged[i] != 0 && isImageChanged[i] != 3) {
                                if (isImageChanged[i] == 2) { //Images have been removed
//                                    Log.i("00000000000000", ad.getPhotos().get(i).getId());
                                    reqEntity.addFormDataPart("ids_to_delete[]", ad.getPhotos().get(i).getId());
                                } else if (isImageChanged[i] == 1) { //Images have been replaced
//                                    Log.i("00000000000000", i+" it happened");
//                                    Log.i("00000000000000", ad.getPhotos().get(i).getId());
                                    reqEntity.addFormDataPart("photos_to_edit_id[]", ad.getPhotos().get(i).getId());

//                                    for (int j = 0; j > bitmaps.length; j++) {
                                    long time = System.currentTimeMillis();
                                    if (bitmaps[i] != null) {
                                        String pathTemp = Compress_image.reductImageSize(time + ".jpg", bitmaps[i]);
                                        File photoFile = new File(pathTemp);
                                        reqEntity.addFormDataPart("photo_to_edit[]", photoFile.getName(), okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), photoFile));
                                        // i nullify it here so it wouldn't be in the list for the totally new images
                                        bitmaps[i] = null;
                                    }
//                                    }
                                }
                            }
                        }
                        for (Bitmap bitmap : bitmaps) {
                            long time = System.currentTimeMillis();
                            if (bitmap != null) {
                                String pathTemp = Compress_image.reductImageSize(time + ".jpg", bitmap);
                                File photoFile = new File(pathTemp);
                                reqEntity.addFormDataPart("new_photos[]", photoFile.getName(), okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), photoFile));
                            }
                        }
                    }


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
                                            if (isItNewAd)
                                                ShowToast.success("آگهی شما بدرستی ذخیره گردید و پس از تایید مدیر به نمایش در می آید.", NewAdActivity.this);
                                            else
                                                ShowToast.success("تغییرات در آگهی بدرستی ذخیره گردید و پس از تایید مدیر به نمایش در می آید.", NewAdActivity.this);
                                            NewAdActivity.this.finish();
                                            if (!isItNewAd) updateAd.onAdUpdated();
                                        } else if (Jobj.getString("status").equals("401")) {
                                            if (Jobj.getString("error").equals("token_expired")) {
                                                UserHelper.RemoveUserInfo(NewAdActivity.this);
                                                Intent intent = new Intent(NewAdActivity.this, LoginActivity.class);
                                                ShowToast.failure("لطفا دوباره وارد حساب خود شوید", NewAdActivity.this);
                                                startActivity(intent);
                                            }
                                        } else {
                                            if (isItNewAd)
                                                ShowToast.failure("در ثبت آگهی به مشکل برخوردیم لطفا مجددا تلاش فرمایید.", NewAdActivity.this);
                                            else
                                                ShowToast.failure("در ثبت تغییرات به مشکل برخوردیم لطفا مجددا تلاش فرمایید.", NewAdActivity.this);
//                                            NewAdActivity.this.finish();
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

    @Override
    public void onBackPressed() {
        ad = null;
        isItNewAd = true;
        isCitySetOnce = false;
        isSubcatSetOnce = false;
        super.onBackPressed();
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
                AlertDialog.Builder b = new AlertDialog.Builder(context);

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
                bq1.setTextColor(context.getResources().getColor(R.color.colorPrimary));
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
                turnonlocation();
                showMap.setChecked(true);

            } else {
                ShowToast.failure("شما اجازه دسترسی به موقعیت خود را به این برنامه نداده اید", NewAdActivity.this);
                showMap.setChecked(false);

            }
        } else if (requestCode == REQUEST_STORAGE_READ_ACCESS_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    ((grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) || grantResults.length == 1)) {
                boolean showCamera = true;
                MultiImageSelector selector = MultiImageSelector.create(NewAdActivity.this);
                selector.showCamera(showCamera);
                selector.single();
                selector.origin(mSelectPath);
                selector.start(NewAdActivity.this, REQUEST_IMAGE);
            } else {
                ShowToast.failure("شما اجازه دسترسی به حافظه و دوربین را به این برنامه نداده اید", NewAdActivity.this);
            }
        }

    }
}

