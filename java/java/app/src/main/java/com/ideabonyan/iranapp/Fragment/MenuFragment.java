package com.ideabonyan.iranapp.Fragment;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.ideabonyan.iranapp.Activity.AroundMeActivity;
import com.ideabonyan.iranapp.Activity.ConfirmationActivity;
import com.ideabonyan.iranapp.Activity.DashboardActivity;
import com.ideabonyan.iranapp.Activity.HelpActivity;
import com.ideabonyan.iranapp.Activity.LoginActivity;
import com.ideabonyan.iranapp.Activity.SignUpActivity;
import com.ideabonyan.iranapp.Fragment.Dialogs.CityPickerDialogFragment;
import com.ideabonyan.iranapp.Fragment.Dialogs.NotifKindChooserDialogFragment;
import com.ideabonyan.iranapp.Fragment.Dialogs.Select_Add_Type_Dialog;
import com.ideabonyan.iranapp.Fragment.Dialogs.Show_Link_To_Us;
import com.ideabonyan.iranapp.Fragment.Dialogs.Show_Rouls_Dialog;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.Utils.StaticData;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {


    View view;
    LinearLayout menuFragmentLoginBTN, menuFragmentSignupBTN, dashboardBTN, newAdBTN, aroundMeBTN, setLocationBTN, notifChooseBTN, tariffBTN, helpBTN,
            shareBTN, contactUsBTN, termsBTN, lin_byeCharge;
    User user;
    TextView txt_reg;
    LinearLayout lin_reg, lin_near_me, lin_link_to_us;
    ImageView img_focous;
    int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 1001;

    public MenuFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);

        initializer();
        hideUnhideMenuItems();
        onClicks();

        return view;
    }

    private void initializer() {
        lin_byeCharge = (LinearLayout) view.findViewById(R.id.lin_byeCharge);
        menuFragmentSignupBTN = (LinearLayout) view.findViewById(R.id.menuFragmentSignupBTN);
        menuFragmentLoginBTN = (LinearLayout) view.findViewById(R.id.menuFragmentLoginBTN);
        dashboardBTN = (LinearLayout) view.findViewById(R.id.menuFragmentDashboardBTN);
        newAdBTN = (LinearLayout) view.findViewById(R.id.menuFragmentNewAdBTN);
        aroundMeBTN = (LinearLayout) view.findViewById(R.id.menuFragmentAroundMEBTN);
        setLocationBTN = (LinearLayout) view.findViewById(R.id.menuFragmentSetLocationBTN);
        notifChooseBTN = (LinearLayout) view.findViewById(R.id.menuFragmentNotificationSelectionBTN);
        tariffBTN = (LinearLayout) view.findViewById(R.id.menuFragmentPlansBTN);
        helpBTN = (LinearLayout) view.findViewById(R.id.menuFragmentHelpBTN);
        shareBTN = (LinearLayout) view.findViewById(R.id.menuFragmentShareBTN);
        contactUsBTN = (LinearLayout) view.findViewById(R.id.menuFragmentContactUsBTN);
        termsBTN = (LinearLayout) view.findViewById(R.id.menuFragmentTermsBTN);
        lin_reg = (LinearLayout) view.findViewById(R.id.lin_reg);
        lin_link_to_us = (LinearLayout) view.findViewById(R.id.lin_link_to_us);
        user = UserHelper.LoadUserInfo(getActivity());
        txt_reg = view.findViewById(R.id.txt_reg);
        lin_near_me = view.findViewById(R.id.lin_near_me);
        img_focous = view.findViewById(R.id.img_focous);
        img_focous.setVisibility(View.GONE);


    }

    private void onClicks() {
        lin_byeCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (checkAndRequestPermissions(false)) {
                    String encodedHash = Uri.encode("#");
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel://*780" + encodedHash));
                    startActivity(intent);
//                }
            }
        });
        lin_link_to_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Show_Link_To_Us show_link_to_us = new Show_Link_To_Us("ارتباط با ما", getString(R.string.link_to_us));
                show_link_to_us.show(getChildFragmentManager(), "show_link_to_us");
            }
        });
        contactUsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Show_Rouls_Dialog show_rouls_dialog = new Show_Rouls_Dialog("درباره ما", getString(R.string.about_us));
                show_rouls_dialog.show(getChildFragmentManager(), "show_rouls_dialog");
            }
        });
        termsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Show_Rouls_Dialog show_rouls_dialog = new Show_Rouls_Dialog("قوانین", getString(R.string.rouls));
                show_rouls_dialog.show(getChildFragmentManager(), "show_rouls_dialog");
            }
        });
        menuFragmentSignupBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), SignUpActivity.class);
//                i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
            }
        });
        menuFragmentLoginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), LoginActivity.class);
//                i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
            }
        });

        dashboardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user.isLoggedIn() && !user.isVerrified()) {

                    Intent i = new Intent(getActivity(), ConfirmationActivity.class);
//                    i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(i);
                } else {

                    Intent i = new Intent(getActivity(), DashboardActivity.class);
                    startActivity(i);
                }
            }
        });

        newAdBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user.isLoggedIn() && !user.isVerrified()) {

                    Intent i = new Intent(getActivity(), ConfirmationActivity.class);
//                    i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(i);
                } else {

//                    Intent i = new Intent(getActivity(), NewAdActivity.class);
//                    startActivity(i);
                    Select_Add_Type_Dialog select_add_type_dialog = new Select_Add_Type_Dialog();
                    select_add_type_dialog.show(getChildFragmentManager(), "select_add_type_dialog");
                }
            }
        });

        aroundMeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                turnonlocation();
//
//                Intent intent = new Intent(getActivity(), AroundMeActivity.class);
//                startActivity(intent);
            }
        });

        setLocationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CityPickerDialogFragment cityPickerDialogFragment = new CityPickerDialogFragment();
                cityPickerDialogFragment.setContext(getActivity());
                cityPickerDialogFragment.show(getFragmentManager(), "ProvincePickerFragment");
            }
        });

        notifChooseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NotifKindChooserDialogFragment notifKindChooserDialogFragment = new NotifKindChooserDialogFragment();
                notifKindChooserDialogFragment.show(getActivity().getFragmentManager(), "NotificationKindChooser");
            }
        });

        tariffBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        helpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UserSessionManager userSessionManager=new UserSessionManager(getActivity());
//                userSessionManager.set_first_main("0");
//                userSessionManager.set_first_menu("0");
//                img_focous.setVisibility(View.VISIBLE);
//                show_helpe();
                startActivity(new Intent(getActivity(), HelpActivity.class));
            }
        });

        shareBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String shareText = "باایران آپ در تخفیف ها به روز باشید" + "\n\n" + StaticData.LINK_IN_BAZAR;

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
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


                        Intent intent = new Intent(getActivity(), AroundMeActivity.class);
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

    private void hideUnhideMenuItems() {
        if (user.isLoggedIn()) {
            dashboardBTN.setVisibility(View.VISIBLE);
            newAdBTN.setVisibility(View.VISIBLE);
            menuFragmentLoginBTN.setVisibility(View.GONE);
            menuFragmentSignupBTN.setVisibility(View.GONE);
        } else {

            dashboardBTN.setVisibility(View.GONE);
            newAdBTN.setVisibility(View.GONE);
            menuFragmentSignupBTN.setVisibility(View.VISIBLE);
            menuFragmentLoginBTN.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        hideUnhideMenuItems();
    }

    private boolean checkAndRequestPermissions(boolean showAlartDialog) {

        int CALL_PHONE = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (CALL_PHONE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            if (showAlartDialog) {
                ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new
                        String[listPermissionsNeeded.size()]), REQUEST_STORAGE_READ_ACCESS_PERMISSION);
            } else {
                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

                b.setMessage("برای خرید شارژ، لطفا اجازه دسترسی به تماس مستقیم با شماره ها را بدهید")
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
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new
                    String[listPermissionsNeeded.size()]), REQUEST_STORAGE_READ_ACCESS_PERMISSION);
            return false;
        }
        return true;
    }


}
