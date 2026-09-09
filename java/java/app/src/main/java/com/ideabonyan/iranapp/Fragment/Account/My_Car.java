package com.ideabonyan.iranapp.Fragment.Account;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Activity.LoginActivity;
import com.ideabonyan.iranapp.Adapter.My_Car_List_Adapter;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data2;
import com.ideabonyan.iranapp.Models.Vehicles;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back2;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class My_Car extends Fragment implements Get_Insert_Edit_Data2 {
    LinearLayoutManager layoutManager;
    My_Car_List_Adapter feedAdapter;
    public static RecyclerView rv;
    public static ProgressBar progressBar, loadMoreProgressbar;
    View view;
    TextView emptyListText;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    NestedScrollView nested;
    public static List<Vehicles> vehiclesList;
    public static int offset = 0;
//    boolean loadMore = true;
    boolean isLoadingNow = true;
    boolean isThereData;

    public My_Car() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my__car, container, false);
        holder();
        onclick();
        return view;
    }

    private void holder() {
        rv = (RecyclerView) view.findViewById(R.id.dashboardRv);
        rv.setNestedScrollingEnabled(false);
        progressBar = (ProgressBar) view.findViewById(R.id.dashboardProgressBar);
        loadMoreProgressbar = (ProgressBar) view.findViewById(R.id.dashboardLoadMoreProgressBar);
        emptyListText = (TextView) view.findViewById(R.id.dashboardEmptyListText);
        nested= (NestedScrollView) view.findViewById(R.id.nested);
        nested.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
//                    if (!loadingMore) {
//                        loadingMore = true;
//                        first += 10;
//                        get_all_data();
//                        Log.v("is hear", "nested");
//                    }

                    if (!isLoadingNow) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
//                            Log.v("shod1", "sholde");

                            isLoadingNow = true;
                            offset += 10;
                            getData(getActivity(), My_Car.this);


                        }
                    }
                }
            }
        });

//        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0) //check for scroll down
//                {
//                    visibleItemCount = layoutManager.getChildCount();
//                    totalItemCount = layoutManager.getItemCount();
//                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
//
//
//
//                    }
//                }
//            }
//        });

    }

    public static void getData(Context context, Get_Insert_Edit_Data2 get_insert_edit_data2) {
        String url = StaticData.get_all_my_car_ads + "?token=" + new UserSessionManager(context).getLoginToken()
                + "&offset=" + offset + "&limit=10";
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back2.binddata(get_insert_edit_data2);
        Get_Volley_Call_Back2.Call_Volley(context, params, url, Request.Method.GET, 133);
    }

    @Override
    public void onResume() {
        super.onResume();
        rv.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        offset = 0;
        vehiclesList = null;
        getData(getActivity(), My_Car.this);
    }

    private void onclick() {

    }

    @Override
    public void on_volley_response(String response, int id) {
        if (id == 133) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("status").equals("200")) {
                    if (vehiclesList == null) {
                        vehiclesList = Vehicles.Import_List(jsonObject, "list");
                        if (vehiclesList.size() ==10){ isLoadingNow = false;}
                    } else {
                        List<Vehicles> vehicles = Vehicles.Import_List(jsonObject, "list");
                        for (Vehicles vehicles1 : vehicles) {
                            vehiclesList.add(vehicles1);
                        }
                        loadMoreProgressbar.setVisibility(View.GONE);
                        if (vehicles.size()== 10)  {
                        isLoadingNow = false;}
                        feedAdapter.notifyDataSetChanged();
                    }

                    progressBar.setVisibility(View.GONE);
                    setupfeed();
                    if (vehiclesList.size() == 0) {
                        emptyListText.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);

                    } else {
                        rv.setVisibility(View.VISIBLE);
                        emptyListText.setVisibility(View.GONE);

                    }
                } else if (jsonObject.getString("status").equals("401")) {
                    if (jsonObject.getString("error").equals("token_invalid")) {
                        UserHelper.RemoveUserInfo(getActivity());
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        ShowToast.failure("لطفا دوباره وارد حساب خود شوید", getActivity());
                        startActivity(intent);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.v("exit","exit");
            }
        }

    }

    public void setupfeed() {
        layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        feedAdapter = new My_Car_List_Adapter(vehiclesList, getActivity(), My_Car.this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(feedAdapter);
        feedAdapter.notifyDataSetChanged();
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {
        if (error.toString().equals("com.android.volley.AuthFailureError")){
        UserHelper.RemoveUserInfo(getActivity());
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        ShowToast.failure("لطفا دوباره وارد حساب خود شوید", getActivity());
        startActivity(intent);}
    }
}
