package com.ideabonyan.iranapp.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import androidx.transition.TransitionManager;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data2;
import com.ideabonyan.iranapp.Models.AdsToBeListed;
import com.ideabonyan.iranapp.Models.HomeSubCategories;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.GPSTracker;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back2;
import com.ideabonyan.iranapp.Utils.StaticData;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AroundMeActivity extends AppCompatActivity implements OnMapReadyCallback, Get_Insert_Edit_Data, Get_Insert_Edit_Data2 {


    SupportMapFragment supportMapFragment;
    View mapFragment;
    View mapView;
    GoogleMap gmap;
    ViewGroup rootView;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    CardView noAdCard;
    TextView noAdAroundMe, noAdWhereILook;
    ProgressBar progressBar, spinnerProgressBar;
    Spinner catSpnr, subCatSpnr;
//    ImageView mapOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_around_me);

        initializer();
        smallStuff();
        spinnerStuff();
        getSpinnerData(catID, StaticData.All_CATEGORIES);
    }


    private void initializer() {
        if (gmap == null) {
            supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.aroundMeMap);
            mapView = supportMapFragment.getView();
            supportMapFragment.getMapAsync(this);
        } else {
            GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        }

        noAdCard = (CardView) findViewById(R.id.aroundMeNoAdCard);
        noAdAroundMe = (TextView) findViewById(R.id.aroundMeNoAdAroundMeText);
        noAdWhereILook = (TextView) findViewById(R.id.aroundMeNoAdWhereILookText);
        progressBar = (ProgressBar) findViewById(R.id.aroundMeProgressBar);
        spinnerProgressBar = (ProgressBar) findViewById(R.id.aroundMeSpinnerLoadProgressBar);
        catSpnr = (Spinner) findViewById(R.id.aroundMeCatSpinner);
        subCatSpnr = (Spinner) findViewById(R.id.aroundMeSubCatSpinner);
        rootView = (ViewGroup) findViewById(R.id.aroundMeRootView);
//        mapOverlay = (ImageView) findViewById(R.id.aroundMeMapOverlay);
        mapFragment = findViewById(R.id.aroundMeMap);
    }


    private void smallStuff() {
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

        turnonlocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gmap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));



        GPSTracker gpsTracker = new GPSTracker(AroundMeActivity.this);
        Location location2=gpsTracker.getLocation();
//        Show_Map.lng=String.valueOf(location.getLongitude());
//        Show_Map.lat=String.valueOf(location.getLatitude());


        if (location2 != null)
        {
//            Log.i("11111111111111", "locationFound");
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location2.getLatitude(), location2.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location2.getLatitude(), location2.getLongitude()))      // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
//                    .bearing(90)                // Sets the orientation of the camera to east
//                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            gmap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            Log.i("11111111111111", "locationFound");

            //TODO check if this happens, then give permission to show noAdAroundWarn
        }





        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);

            ////////////////////////

            changeInfoWindowLooks();

            mapListeners();

            try{
                lat = location2.getLatitude();
                lon = location2.getLongitude();
                getMapData();
            } catch (NullPointerException e){e.printStackTrace();}
        }
    }

    private void changeInfoWindowLooks() {
        gmap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

                View view = getLayoutInflater().inflate(R.layout.map_info_window, null);
                AdsToBeListed ad = null;// = adData.get(Integer.parseInt(marker.getTitle()));
                for (AdsToBeListed add : adData){
                    if (marker.getTitle().equals(add.getId())){
                        ad = add;
                    }
                }
                TextView title = (TextView) view.findViewById(R.id.mapInfoTitle);
                ImageView image = (ImageView) view.findViewById(R.id.mapInfoImage);

                title.setText(ad.getTitle());
                if (ad.getPhotos() != null && ad.getPhotos().size() > 0) {
                    if (ad.getPhotos().get(0) != null) {
                        Picasso.with(AroundMeActivity.this)
                                .load(ad.getPhotos().get(0).getName())
                                .resize(100, 100)
//                                .centerCrop()
                                .into(image);
                    }
                    else image.setVisibility(View.GONE);
                }else image.setVisibility(View.GONE);

                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    public void turnonlocation() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(AroundMeActivity.this).addApi(LocationServices.API).build();
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
//                        if (chbAddLoction.isChecked()) {
//                            mapLayout1.setVisibility(View.VISIBLE);
//                        } else {
//                            mapLayout1.setVisibility(View.GONE);
//                        }

                        if (ActivityCompat.checkSelfPermission(AroundMeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AroundMeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        } else
                            gmap.setMyLocationEnabled(true);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        chbAddLoction.setChecked(false);
//                        Log.i("abcd", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            status.startResolutionForResult(AroundMeActivity.this, REQUEST_CHECK_SETTINGS);
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


    ////////////////////////////////////////////////////////////////////
    double lat, lon;
    String catText="", subCatText="";
    List<String> catList, subCatList;
    List<HomeSubCategories> homeCategories;
    List<HomeSubCategories> subCategoriesObjectList;
    List<AdsToBeListed> adData;
    String catBeforeDeletion;
    int mapID = 1, catID = 2, subCatID = 3;
    boolean isCameraMoved = false;

    private void spinnerStuff() {

        catList = new ArrayList<>();
        subCatList = new ArrayList<>();
        catList.add("انتخاب کنید");
        subCatList.add("انتخاب کنید");
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(AroundMeActivity.this, R.layout.item_spinner_layout, catList);
        ArrayAdapter<String> subCatAdapter = new ArrayAdapter<>(AroundMeActivity.this, R.layout.item_spinner_layout, subCatList);
        catSpnr.setAdapter(catAdapter);
        subCatSpnr.setAdapter(subCatAdapter);



        catSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                subCatList.clear();
                subCatList.add("انتخاب کنید");
                subCatSpnr.setSelection(0);
                subCatText = "";

                catText = "";
                catBeforeDeletion = catText;

                if (position != 0) {
                    spinnerProgressBar.setVisibility(View.VISIBLE);
                    catText = "&category_id=" + homeCategories.get(position - 1).getId();
                    catBeforeDeletion = catText;

                    String spinnerDataUrl = StaticData.DOMAIN_WITH_API + "/categories/" + homeCategories.get(position - 1).getId() + "/subcategories/all";
                    getSpinnerData(subCatID, spinnerDataUrl);
                }

                gmap.clear();
                getMapData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subCatSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0){
                    subCatText = "&sub_category_id=" + subCategoriesObjectList.get(position - 1).getId();
                    catText = "";
                } else {
                    catText = catBeforeDeletion;
                    subCatText = "";
                }

                gmap.clear();
                getMapData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getSpinnerData(int volleyID, String spinnerDataUrl) {

        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back2.binddata(this);
        Get_Volley_Call_Back2.Call_Volley(AroundMeActivity.this, params, spinnerDataUrl, Request.Method.GET, volleyID);
    }


    LatLng cameraCenter;
    Marker lastOpenned = null;
    private void mapListeners() {

        gmap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {

                cameraCenter = gmap.getCameraPosition().target;

                if (isDistanceMoreThan500m()) {
                    isCameraMoved = true;
                    lat = cameraCenter.latitude;
                    lon = cameraCenter.longitude;
                    getMapData();
                }
            }
        });


        ////////////  THIS IS TO PREVENT CAMERA TO JUMP ON MARKER
        // Since we are consuming the event this is necessary to
        // manage closing opened markers before opening new ones
        gmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                // Check if there is an open info window
                if (lastOpenned != null) {
                    // Close the info window
                    lastOpenned.hideInfoWindow();

                    // Is the marker the same marker that was already open
                    if (lastOpenned.equals(marker)) {
                        // Nullify the lastOpened object
                        lastOpenned = null;
                        // Return so that the info window isn't opened again
                        return true;
                    }
                }

                // Open the info window for the marker
                marker.showInfoWindow();
                // Re-assign the last opened such that we can close it later
                lastOpenned = marker;

                // Event was handled by our code do not launch default behaviour.
                return true;
            }
        });

        gmap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                for (AdsToBeListed ad : adData){
                    if (marker.getPosition().latitude == Double.parseDouble(ad.getLatitude())){
                        if (marker.getPosition().longitude == Double.parseDouble(ad.getLongitude())){

                            Intent intent = new Intent(AroundMeActivity.this, ShowAdActivity.class);
                            intent.putExtra("ad",ad);
                            startActivity(intent);
                        }
                    }
                }

            }
        });
//        mapOverlay.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getAction();
//                switch (action) {
////                    case MotionEvent.ACTION_DOWN:
//
////                        return false;
//
////                    case MotionEvent.ACTION_UP:
////                        return true;
//
//                    case MotionEvent.ACTION_MOVE:
//
//                        Log.i("1111111111", "sdf");
//
//                        cameraCenter = gmap.getCameraPosition().target;
//
//                        if (isDistanceMoreThan500m()){
//                            isCameraMoved = true;
//                            lat = cameraCenter.latitude;
//                            lon = cameraCenter.longitude;
//                            getMapData();
//                        }
//                        return false;
//
//                    default:
//                        return true;
//                }
//            }
//        });
    }

    private boolean isDistanceMoreThan500m() {
//        double p = 0.017453292519943295;    // Math.PI / 180
//        double a = 0.5 - Math.cos((cameraCenter.latitude - lat) * p) / 2 +
//                Math.cos(lat * p) * Math.cos(cameraCenter.latitude * p) *
//                        (1 - Math.cos((cameraCenter.longitude - lon) * p)) / 2;
//
//        Log.i("1111111111", 12742 * Math.asin(Math.sqrt(a))+"");
////        return 12742 * Math.asin(Math.sqrt(a)) > 100 ? true : false; // 2 * R; R = 6371 km
//        return 12742 * Math.asin(Math.sqrt(a)) > 100; // 2 * R; R = 6371 km


        double lat1 = lat, lat2 = cameraCenter.latitude, lon1 = lon, lon2 = cameraCenter.longitude;

        double earthRadiusKm = 6371;

        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lon2 - lon1) * Math.PI / 180;

        lat1 = (lat1) * Math.PI / 180;
        lat2 = (lat2) * Math.PI / 180;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c * 1000 > 500;
    }

    private void getMapData(){
        progressBar.setVisibility(View.VISIBLE);

        String url = StaticData.AROUND_ME + "?latitude=" + lat + "&longitude=" + lon + catText + subCatText;
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(AroundMeActivity.this, params, url, Request.Method.GET, mapID);
    }

    @Override
    public void on_volley_response(String response, int id) {
        if (id == catID){
            spinnerProgressBar.setVisibility(View.INVISIBLE);

//            try {
//                JSONObject jsonObject = new JSONObject(response);
//                JSONArray jsonArray = jsonObject.getJSONArray("list");
//                for (int i = 0; i < jsonArray.length(); i++){
//                    JSONObject json = jsonArray.getJSONObject(i);
//                    catList.add(json.getString("name"));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            try {
                JSONObject jsonObject = new JSONObject(response);
//                JSONArray jsonArray = jsonObject.getJSONArray("list");
//                for (int i = 0; i < jsonArray.length(); i++){
//                    JSONObject json = jsonArray.getJSONObject(i);
//                    catList.add(json.getString("name"));
//                }
                homeCategories = HomeSubCategories.Categories(jsonObject);
                for (HomeSubCategories cat : homeCategories){
                    catList.add(cat.getName());
                }
                catSpnr.setEnabled(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        else if (id == subCatID){
            spinnerProgressBar.setVisibility(View.INVISIBLE);

            try {
                JSONObject jsonObject = new JSONObject(response);
                subCategoriesObjectList = HomeSubCategories.Categories(jsonObject);
                for (HomeSubCategories sub : subCategoriesObjectList){
                    subCatList.add(sub.getName());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        else if (id == mapID){
            try {
                JSONObject jsonObject = new JSONObject(response);
                adData = AdsToBeListed.Import(jsonObject);
                showMarkers();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override public void on_volley_error(VolleyError error, int id) {

    }

    private void showMarkers() {
        TransitionManager.beginDelayedTransition(rootView);

        progressBar.setVisibility(View.GONE);

        gmap.clear();

        if (adData.size() > 0) {

            noAdCard.setVisibility(View.GONE);
            noAdAroundMe.setVisibility(View.GONE);
            noAdWhereILook.setVisibility(View.GONE);


            for (AdsToBeListed ad : adData) {
                MarkerOptions markerOptions = new MarkerOptions();

                LatLng ll = new LatLng(Double.parseDouble(ad.getLatitude()), Double.parseDouble(ad.getLongitude()));

                markerOptions.position(ll);

                markerOptions.title(ad.getId());
//                        .snippet(ad.getNotes());

                gmap.addMarker(markerOptions);
            }
        }
        else {
            noAdCard.setVisibility(View.VISIBLE);
            if (!isCameraMoved) noAdAroundMe.setVisibility(View.VISIBLE);
            else {
                noAdWhereILook.setVisibility(View.VISIBLE);
                noAdAroundMe.setVisibility(View.GONE);
            }
        }

        //////////////////
        ///     CACHING IMAGES

        for (AdsToBeListed ad : adData) {
            if (ad.getPhotos() != null && ad.getPhotos().size() > 0) {
                if (ad.getPhotos().get(0) != null) {
                    Picasso.with(AroundMeActivity.this)
                            .load(ad.getPhotos().get(0).getName())
                            .resize(100, 100)
                            .fetch();
                }
            }
        }
    }
}
