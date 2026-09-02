package com.ideabonyan.iranapp.Fragment.Dialogs;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ideabonyan.iranapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class Show_Link_To_Us extends DialogFragment {


    TextView txt_rouls,txt_title;
    LinearLayout lin_back,lin_send_sms;
    View view;
    String title,text;
    public Show_Link_To_Us(String title, String text) {
        // Required empty public constructor
        this.title=title;
        this.text=text;
    }

    public void onStart()
    {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_show__link_to_us__dialog, container, false);
        holer();
        onclick();
        binddata();
        return view;
    }

    private void binddata() {
        txt_rouls.setText((Html.fromHtml(text)));
        txt_title.setText(title);
    }

    private void onclick() {
        lin_send_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = "985000538027";  // The number on which you want to send SMS
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
            }
        });
        lin_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void holer() {
        txt_rouls=view.findViewById(R.id.txt_rouls);
        lin_back=view.findViewById(R.id.lin_back);
        txt_title=view.findViewById(R.id.txt_title);
        lin_send_sms=view.findViewById(R.id.lin_send_sms);
    }

}
