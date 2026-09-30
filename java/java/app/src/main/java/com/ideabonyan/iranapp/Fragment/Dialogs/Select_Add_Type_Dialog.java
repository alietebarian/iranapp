package com.ideabonyan.iranapp.Fragment.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.ideabonyan.iranapp.Activity.Add_New_Car_Add;
import com.ideabonyan.iranapp.Activity.Add_New_Home_Add;
import com.ideabonyan.iranapp.Activity.Add_New_Job;
import com.ideabonyan.iranapp.Activity.NewAdActivity;
import com.ideabonyan.iranapp.R;


public class Select_Add_Type_Dialog extends DialogFragment {

    /**
     * TEMPORARY: new «آگهی کسب و کار» ads are switched off; the button stays visible but dimmed
     * and only explains that. Set back to true to allow them again. Editing an existing business
     * ad (ShowAdActivity) is not affected.
     */
    private static final boolean BUSINESS_ADS_ENABLED = false;

    View view;
    LinearLayout lin_home, lin_job, lin_car, lin_descount;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onStart() {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_select_add_type, null);
        holder();

        onClicks();

        return view;
    }

    private void holder() {
        lin_descount = (LinearLayout) view.findViewById(R.id.lin_descount);
        lin_car = (LinearLayout) view.findViewById(R.id.lin_car);
        lin_job = (LinearLayout) view.findViewById(R.id.lin_job);
        lin_home = (LinearLayout) view.findViewById(R.id.lin_home);
    }

    private void onClicks() {
        if (!BUSINESS_ADS_ENABLED) lin_descount.setAlpha(0.4f);
        lin_descount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BUSINESS_ADS_ENABLED) {
                    Toast.makeText(getActivity(), "ثبت آگهی کسب و کار موقتاً غیرفعال است", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i = new Intent(getActivity(), NewAdActivity.class);
                startActivity(i);
                Select_Add_Type_Dialog.this.dismiss();
            }
        });
        lin_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Add_New_Car_Add.class);
                startActivity(i);
                Select_Add_Type_Dialog.this.dismiss();

            }
        });
        lin_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Add_New_Job.class);
                startActivity(i);
                Select_Add_Type_Dialog.this.dismiss();
            }
        });
        lin_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Add_New_Home_Add.class);
                startActivity(i);
                Select_Add_Type_Dialog.this.dismiss();

            }
        });
    }


}
