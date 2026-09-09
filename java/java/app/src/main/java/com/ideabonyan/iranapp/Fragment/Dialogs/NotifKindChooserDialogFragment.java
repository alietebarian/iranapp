package com.ideabonyan.iranapp.Fragment.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NotifKindChooserDialogFragment extends DialogFragment implements Get_Insert_Edit_Data {


    View view;
    CheckBox ads, news;
    Button save, cancel;
    UserSessionManager userSessionManager;
    List<String> notifData;
    ProgressBar progressBar;




//    public void setContext(Context context) {
//        this.context = context;
//    }
//
//    public void setData(VipAd data) {
//        this.vipAd = data;
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        mycommunicator = (cityPickerCommunicator) context;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_notification_kind_chooser, null);

        ads = (CheckBox) view.findViewById(R.id.dialogNotifAds);
        news = (CheckBox) view.findViewById(R.id.dialogNotifNews);
        save = (Button) view.findViewById(R.id.dialogNotifSaveBTN);
        cancel = (Button) view.findViewById(R.id.dialogNotifCancelBTN);
        progressBar = (ProgressBar) view.findViewById(R.id.dialogNotifProgressBar);
        userSessionManager = new UserSessionManager(getActivity());
        notifData = userSessionManager.getNotificationStatus();

        getData();
        onClicks();

        return view;
    }

    String url;
    String newsInfo, adsInfo;
    private void onClicks() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setCancelable(false);

                if (news.isChecked()) newsInfo = "1";
                else newsInfo = "0";
                if (ads.isChecked()) adsInfo = "1";
                else adsInfo = "0";

                url = StaticData.UPDATE_NOTIFICATION_STATUS +
                        "?send_news_notifications=" + newsInfo + "&send_ads_notifications=" + adsInfo +
                        "&token=" + userSessionManager.getNotigy_code()+"&city_id="+userSessionManager.getCityInfo();

                setData();

//                userSessionManager.setNotificationStatus(newsInfo, adsInfo);
//                dismiss();



                save.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.GONE);
                save.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setData() {
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.PUT, 1);
    }

    private void getData() {
        Log.i("11111111111", notifData.get(0) + "  " + notifData.get(1));

        if (notifData.get(0).equals("0")) news.setChecked(false);
        else news.setChecked(true);

        if (notifData.get(1).equals("0")) ads.setChecked(false);
        else ads.setChecked(true);
    }

    @Override
    public void on_volley_response(String response, int id) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("204")){
                ShowToast.success("انتخاب شما ذخیره شد", getActivity());
                userSessionManager.setNotificationStatus(newsInfo, adsInfo);
                dismiss();
            }
            else {
                ShowToast.failure("لطفا دوباره تلاش کنید", getActivity());
                progressBar.setVisibility(View.GONE);
                save.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                save.setEnabled(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }
}
