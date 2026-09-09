package com.ideabonyan.iranapp.Fragment;


import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.ideabonyan.iranapp.Activity.PishkhanBookSearchActivity;
import com.ideabonyan.iranapp.Activity.PishkhanWeatherActivity;
import com.ideabonyan.iranapp.Activity.PishkhanWebviewActivity;
import com.ideabonyan.iranapp.Activity.PishkhanHafezFalActivity;
import com.ideabonyan.iranapp.Activity.PishkhanTime;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.StaticData;


/**
 * A simple {@link Fragment} subclass.
 */
public class PishkhanFragment extends Fragment {


    public PishkhanFragment() {
    }


    View view;
    LinearLayout falHafez, ageCalculator, time, bookSrarch, liveScores, PersianGulfLeague;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pishkhan, container, false);

        initializer();
        onClicks();


        return view;
    }

    private void initializer() {
        falHafez = (LinearLayout) view.findViewById(R.id.pishkhanFalHafezBTN);
        ageCalculator = (LinearLayout) view.findViewById(R.id.pishkhanAgeCalculatorBTN);
        time = (LinearLayout) view.findViewById(R.id.pishkhanTime);
        bookSrarch = (LinearLayout) view.findViewById(R.id.pishkhanCurrency);
        liveScores = (LinearLayout) view.findViewById(R.id.pishkhanLiveScores);
        PersianGulfLeague = (LinearLayout) view.findViewById(R.id.pishkhanPersianGulfLeague);
    }


    private void onClicks() {
        falHafez.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PishkhanHafezFalActivity.class);
                startActivity(intent);
            }
        });

        ageCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), PishkhanWebviewActivity.class);
//                PishkhanWebviewActivity.url = "http://iran-dev.ir/age/";
//                startActivity(intent);
                turnonlocation();

            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PishkhanTime.class);
                startActivity(intent);
            }
        });

        bookSrarch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), PishkhanBookSearchActivity.class);
                startActivity(intent);
            }
        });

        liveScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), PishkhanWebviewActivity.class);
                PishkhanWebviewActivity.url = StaticData.PERSIAN_GULF_LEAGUE;
                startActivity(intent);
            }
        });

        PersianGulfLeague.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), PishkhanWebviewActivity.class);
                PishkhanWebviewActivity.url = StaticData.LIVE_SCORES;
                startActivity(intent);
            }
        });
    }

    public void turnonlocation() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).build();
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



                        Intent intent = new Intent(getActivity(), PishkhanWeatherActivity.class);
                        startActivity(intent);


//                        if (chbAddLoction.isChecked()) {
//                            mapLayout1.setVisibility(View.VISIBLE);
//                        } else {
//                            mapLayout1.setVisibility(View.GONE);
//                        }

                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        } else
//                            gmap.setMyLocationEnabled(true);
                            break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        chbAddLoction.setChecked(false);
//                        Log.i("abcd", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            status.startResolutionForResult(getActivity(), 0x1);
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

}
