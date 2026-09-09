package com.ideabonyan.iranapp.Fragment.Dialogs;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ideabonyan.iranapp.R;

import java.lang.reflect.Array;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class Show_Rouls_Dialog extends DialogFragment {


    TextView txt_rouls,txt_title;
    LinearLayout lin_back;
    View view;
    String title,text;
    public Show_Rouls_Dialog(String title,String text) {
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

        view= inflater.inflate(R.layout.fragment_show__rouls__dialog, container, false);
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
    }

}
