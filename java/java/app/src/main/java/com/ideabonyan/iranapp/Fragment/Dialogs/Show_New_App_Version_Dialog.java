package com.ideabonyan.iranapp.Fragment.Dialogs;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.StaticData;


/**
 * Shown on the splash screen when the server's app_version is above this build's versionCode.
 * The update is mandatory: the dialog can't be dismissed and its only choices are updating or
 * leaving the app.
 */
@SuppressLint("ValidFragment")
public class Show_New_App_Version_Dialog extends DialogFragment {

    View view;
    LinearLayout lexit, lupdate;
    Context context;

    /** Used by Android when it re-creates the dialog; see onCreateView. */
    public Show_New_App_Version_Dialog() {
    }

    public Show_New_App_Version_Dialog(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No tap-outside or back-button way around the update.
        setCancelable(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        // setCancelable(false) swallows the back button; let it close the app as «خروج» does.
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    exitApp();
                    return true;
                }
                return false;
            }
        });
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Android re-created this dialog after killing the app in the background. The splash
            // screen is re-created too and checks the version again, showing a fresh dialog.
            dismissAllowingStateLoss();
            return null;
        }

        view = inflater.inflate(R.layout.fragment_show__new__app__version__dialog, container, false);
        lupdate = view.findViewById(R.id.lupdate);
        lexit = view.findViewById(R.id.lexit);

        lexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitApp();
            }
        });
        lupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdate();
            }
        });
        return view;
    }

    /** Opens the app's Cafe Bazaar page, or the download page when Bazaar isn't installed. */
    private void openUpdate() {
        Intent bazaar = new Intent(Intent.ACTION_VIEW,
                Uri.parse("bazaar://details?id=com.ideabonyan.iranapp"));
        bazaar.setPackage("com.farsitel.bazaar");
        try {
            startActivity(bazaar);
        } catch (ActivityNotFoundException noBazaar) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(StaticData.DOWNLOAD_PAGE)));
            } catch (ActivityNotFoundException noBrowser) {
                // Nothing can open the link; the dialog stays up.
            }
        }
    }

    private void exitApp() {
        Activity activity = getActivity();
        if (activity != null) activity.finishAffinity();
    }
}
