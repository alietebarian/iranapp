package com.ideabonyan.iranapp.Fragment.Dialogs;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Activity.MainActivity;
import com.ideabonyan.iranapp.Activity.SplashScreenActivity;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Models.VipAd;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.ideabonyan.iranapp.Utils.StaticData.DOMAIN_WITH_API;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class Show_New_App_Version_Dialog extends DialogFragment implements Get_Insert_Edit_Data{

    View view;
    LinearLayout lexit, lupdate;
    Context context;
    CheckBox check_dontshow;
    VipAd vipAd = null;


    public Show_New_App_Version_Dialog(Context context) {
        // Required empty public constructor
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_show__new__app__version__dialog, container, false);
        holder();
        onclick();
        getData();
        return view;
    }

    private void getData() {
        UserSessionManager userSessionManager=new UserSessionManager(getActivity());
        if (!(userSessionManager.getCityInfo().equals("0"))) {

            String url = DOMAIN_WITH_API + "/home-page/ads/" + userSessionManager.getCityInfo() + "/vip";

            Map<String, String> params = new HashMap<String, String>();
            Get_Volley_Call_Back.binddata(this);
            Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 100);
        }
    }


    public void holder() {

        lupdate = (LinearLayout) view.findViewById(R.id.lupdate);
        lexit = (LinearLayout) view.findViewById(R.id.lexit);
        check_dontshow  = view.findViewById(R.id.check_dontshow);

    }


    public void onclick() {

        lexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Show_New_App_Version_Dialog.this.dismiss();
//                Activity activity = (Activity) context;
//                activity.finish();

                if (check_dontshow.isChecked()){
                    new UserSessionManager(getActivity()).setDont_show_new_version("1");
                }
                MainActivity.vipAd=vipAd;
                startActivity(new Intent(getActivity(), MainActivity.class));
                Activity activity =(Activity) context;
                activity.finish();

            }
        });
        lupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("bazaar://details?id=" + "com.ideabonyan.iranapp"));
                intent.setPackage("com.farsitel.bazaar");
                startActivity(intent);
            }
        });

    }

    @Override
    public void on_volley_response(String response, int id) {
        if (id == 100) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                vipAd = VipAd.getvipAdObject(jsonObject.getJSONObject("ad"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }
}
