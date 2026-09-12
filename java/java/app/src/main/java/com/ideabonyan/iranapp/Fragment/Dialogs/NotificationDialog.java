package com.ideabonyan.iranapp.Fragment.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Activity.ShowAdActivity;
import com.ideabonyan.iranapp.Activity.ShowNews;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Models.AdsToBeListed;
import com.ideabonyan.iranapp.Models.NewsData;
import com.ideabonyan.iranapp.Models.PhotosData;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationDialog extends DialogFragment implements Get_Insert_Edit_Data {

    View view;
    Button show, dismiss;
    TextView title, header;
    ProgressBar progressBar;
    ViewGroup rootView;
    String adStatusId = "0", newsStatusId = "1", userinfo = "10";

    AdsToBeListed ad;
    NewsData newsData;

    String body, status, contentId;
    public void setData(String body, String status, String contentId){
        this.body = body;
        this.status = status;
        this.contentId = contentId;
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
        view = inflater.inflate(R.layout.dialog_notification_interceptor, null);

        show = (Button) view.findViewById(R.id.notificationDialogShowBTN);
        dismiss = (Button) view.findViewById(R.id.notificationDialogDismissBTN);
        title = (TextView) view.findViewById(R.id.notificationDialogText);
        header = (TextView) view.findViewById(R.id.notificationDialogTitleText);
        progressBar = (ProgressBar) view.findViewById(R.id.notificationDialogProgressBar);
        rootView = (ViewGroup) view.findViewById(R.id.notificationDialogRoot);


        onClicks();
        fillData();

        return view;
    }

    private void fillData() {
        title.setText(body);
        if (status.equals(adStatusId)) {
            header.setText("یک آگهی جدید");
            String url = StaticData.SINGE_AD + contentId;
            getData(url, 0);
        }
        else if (status.equals(newsStatusId)) {
            header.setText("یک خبر جدید");
            String url = StaticData.SINGLE_NEWS + contentId;
            getData(url, 1);
        }else if (status.equals(userinfo)){
            header.setText("معرفی کاربر جدید");
            show.setText("تایید");
            progressBar.setVisibility(View.GONE);
            show.setVisibility(View.VISIBLE);
            show.setClickable(true);
        }
    }

    private void onClicks() {
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status.equals(adStatusId)){
                    Intent intent = new Intent(getActivity(), ShowAdActivity.class);
                    intent.putExtra("ad",ad);
                    startActivity(intent);
                }
                else if (status.equals(newsStatusId)){
                    Intent intent = new Intent(getActivity(), ShowNews.class);
                    ShowNews.news = newsData;
                    startActivity(intent);
                }else if (status.equals(userinfo)){
                    dismiss();

                }

                dismiss();
            }
        });

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void getData(String url, int id) {
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, id);
    }

    @Override
    public void on_volley_response(String response, int id) {

        if (id == 0){
            try {
                JSONObject jsonObjectO = new JSONObject(response);
                JSONObject jsonObject = jsonObjectO.getJSONObject("ad");
                ad = new AdsToBeListed();
                ad.setId(jsonObject.getString("id"));
                ad.setTitle(jsonObject.getString("title"));
                ad.setLatitude(jsonObject.getString("latitude"));
                ad.setLongitude(jsonObject.getString("longitude"));
                ad.setAddress(jsonObject.getString("address"));
                ad.setType(jsonObject.getString("type"));
                ad.setUpdates_count(jsonObject.getString("updates_count"));
                ad.setMobile(jsonObject.getString("mobile"));
                ad.setTel1(jsonObject.getString("tel1"));
                ad.setTel2(jsonObject.getString("tel2"));
                ad.setLink(jsonObject.getString("link"));
                ad.setDiscount(jsonObject.getString("discount"));
                ad.setWorking_time(jsonObject.getString("working_time"));
                ad.setTelegram(jsonObject.getString("telegram"));
                ad.setInstagram(jsonObject.getString("instagram"));
                ad.setNotes(jsonObject.getString("notes"));
                ad.setAds_plan_id(jsonObject.getString("ads_plan_id"));
                ad.setStatus(jsonObject.getString("status"));
                ad.setUser_id(jsonObject.getString("user_id"));
                ad.setMax_number_of_update(jsonObject.getString("max_number_of_update"));
                ad.setCategory_id(jsonObject.getString("category_id"));
                ad.setProvince_id(jsonObject.getString("province_id"));
                ad.setCity_id(jsonObject.getString("city_id"));
                ad.setSub_category_id(jsonObject.getString("sub_category_id"));
                ad.setProvince_name(jsonObject.getString("province_name"));
                ad.setCity_name(jsonObject.getString("city_name"));
                ad.setAds_owner_name(jsonObject.getString("ads_owner_name"));
                ad.setEmail(jsonObject.getString("email"));
                List<PhotosData> photosData = PhotosData.Import(jsonObject.getJSONArray("photos"));
                ad.setPhotos(photosData);
                ad.setVideo_url(AdsToBeListed.parseVideoUrl(jsonObject));

                TransitionManager.beginDelayedTransition(rootView);
                progressBar.setVisibility(View.GONE);
                show.setVisibility(View.VISIBLE);
                show.setClickable(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (id == 1){
            try {
                JSONObject jsonObjectO = new JSONObject(response);
                JSONObject jsonObject = jsonObjectO.getJSONObject("news");
                newsData = new NewsData();
                newsData.setId(jsonObject.getString("id"));
                newsData.setTitle(jsonObject.getString("title"));
                newsData.setPassage(jsonObject.getString("passage"));
                newsData.setCreated_at(jsonObject.getString("created_at_fa"));
                newsData.setUpdated_at(jsonObject.getString("updated_at"));
                List<PhotosData> photosDatas = PhotosData.Import(jsonObject.getJSONArray("photos"));
                newsData.setPhotos(photosDatas);

                TransitionManager.beginDelayedTransition(rootView);
                progressBar.setVisibility(View.GONE);
                show.setVisibility(View.VISIBLE);
                show.setClickable(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }

}
