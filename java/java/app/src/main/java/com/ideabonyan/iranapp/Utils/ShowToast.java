package com.ideabonyan.iranapp.Utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.R;

/**
 * Created by SIM on 8/6/2017.
 */

public class ShowToast {

    public static void success(String msg, Activity context){

        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_success, (ViewGroup)context.findViewById(R.id.toastSuccess));

        MyTextView textView = (MyTextView) layout.findViewById(R.id.toastSuccessText);
        textView.setText(msg);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void failure(String msg, Activity context){

        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_failure, (ViewGroup)context.findViewById(R.id.toastFailure));

        MyTextView textView = (MyTextView) layout.findViewById(R.id.toastFailureText);
        textView.setText(msg);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
