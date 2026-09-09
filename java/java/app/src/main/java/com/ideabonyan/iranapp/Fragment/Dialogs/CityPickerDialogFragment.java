package com.ideabonyan.iranapp.Fragment.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.TransitionManager;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Adapter.ProvinceSelectionAdapter;
import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.LocationChange;
import com.ideabonyan.iranapp.Models.ProvicesAndCities;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.RecyclerItemClickListener;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CityPickerDialogFragment extends DialogFragment implements Get_Insert_Edit_Data{

    View view;
    RecyclerView rv;
    cityPickerCommunicator mycommunicator;
    List<ProvicesAndCities> data;
    Context context;
    boolean isChoosingProvince = true;
    UserSessionManager userSessionManager;
    MyTextView title;
    ViewGroup rootLayout;
    ProgressBar progressBar;
    ImageButton backBTN;
    TextView retryBTN;
    boolean isDataShownYet = false;




    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<ProvicesAndCities> data) {
        this.data = data;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        mycommunicator = (cityPickerCommunicator) context;
    }

    public void onStart()
    {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
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
        view = inflater.inflate(R.layout.dialog_province, null);


        userSessionManager = new UserSessionManager(context);
        title = (MyTextView) view.findViewById(R.id.provinceDialogTitleText);
        rootLayout = (ViewGroup) view.findViewById(R.id.provinceDialogRoot);
        progressBar = (ProgressBar) view.findViewById(R.id.provinceDialogProgressBar);
        rv = (RecyclerView) view.findViewById(R.id.provinceDialogRvProvince);
        backBTN = (ImageButton) view.findViewById(R.id.provinceDialogBackBTN);
        retryBTN = (TextView) view.findViewById(R.id.provinceDialogRetryBtn);

        getData();

        if (userSessionManager.getCityInfo().equals("0")) backBTN.setVisibility(View.GONE);
        onClicks();

        setCancelable(false);
        return view;
    }

    private void onClicks() {

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChoosingProvince){
                    dismiss();
                }else{
                    backBTN.setVisibility(View.GONE);

                    progressBar.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(rootLayout);
                    rv.setVisibility(View.INVISIBLE);

                    title.setText("  لطفا استان خود را انتخاب کنید  ");

                    isChoosingProvince = true;

                    url = StaticData.PROVINCE;
                    getData();
                    isDataShownYet = false;
                }
            }
        });
        retryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                progressBar.setVisibility(View.VISIBLE);
                retryBTN.setVisibility(View.GONE);
            }
        });
    }


    String url = StaticData.PROVINCE;
    public void getData() {
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(context, params, url, Request.Method.GET, 5);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isDataShownYet){
                    progressBar.setVisibility(View.GONE);
                    retryBTN.setVisibility(View.VISIBLE);
                }
            }
        }, 3000);
    }

    @Override
    public void on_volley_response(String response, int id) {

        if (id == 5) {
            try {
                isDataShownYet = true;
                retryBTN.setVisibility(View.GONE);
                JSONObject jsonObject = new JSONObject(response);
                List<ProvicesAndCities> provinces = ProvicesAndCities.cities(jsonObject);
                setData(provinces);
                runRV();
                recyclerViewOnClickListener();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (id==12100){
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("status").equals("204")){
                    ShowToast.success("انتخاب شما ذخیره شد", getActivity());
//                    userSessionManager.setNotificationStatus(newsInfo, adsInfo);
//                    dismiss();
                }
                else {

                    ShowToast.failure("در ثبت اطلاعات به مشکل برخوردیم لطفا مجددا تلاش فرمایید",getActivity());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }



    private void runRV() {
        progressBar.setVisibility(View.GONE);



        ProvinceSelectionAdapter adapter = new ProvinceSelectionAdapter(data, context);
        LinearLayoutManager layoutManager = new GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        rv.setVisibility(View.VISIBLE);
        rv.setVisibility(View.INVISIBLE);

        TransitionManager.beginDelayedTransition(rootLayout);
        rv.setVisibility(View.VISIBLE);
    }



    private void recyclerViewOnClickListener() {

        rv.addOnItemTouchListener(new RecyclerItemClickListener(context, rv, new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {

                TransitionManager.beginDelayedTransition(rootLayout);


                String id = data.get(position).getId();
                String name = data.get(position).getName();

                if (isChoosingProvince){
                    backBTN.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(rootLayout);
                    rv.setVisibility(View.INVISIBLE);

                    userSessionManager.setProvinceInfo(id);
                    userSessionManager.setProvinceName(name);

                    title.setText("  لطفا شهر خود را انتخاب کنید  ");

                    isChoosingProvince = false;

                    url = StaticData.DOMAIN_WITH_API + "/provinces/" + id + "/cities";
                    getData();
                    isDataShownYet = false;
                }else{
                    userSessionManager.setCityInfo(id);
                    userSessionManager.setCityName(name);
                    mLocationChange.onLocationChanged();
                    edit_notify_city();
                    dismiss();
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

    }

    private void edit_notify_city() {
        url = StaticData.UPDATE_NOTIFICATION_STATUS +
                "?send_news_notifications=" + userSessionManager.getNotificationStatus().get(0) + "&send_ads_notifications=" +
                userSessionManager.getNotificationStatus().get(0)+"&token=" + userSessionManager.getNotigy_code()+"&city_id="+
                userSessionManager.getCityInfo();
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.PUT, 12100);
    }


    static LocationChange mLocationChange;
    public static void binddata(LocationChange locationChange) {
        mLocationChange = locationChange;
    }







    public interface cityPickerCommunicator{
        public void provinceIdReturner(String id);
    }
}
