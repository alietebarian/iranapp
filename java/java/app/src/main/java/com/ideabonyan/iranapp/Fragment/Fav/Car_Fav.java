package com.ideabonyan.iranapp.Fragment.Fav;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Activity.ShowCarActivity;
import com.ideabonyan.iranapp.Adapter.Car_List_Adapter;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Models.Vehicles;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.RecyclerItemClickListener;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Car_Fav extends Fragment implements Get_Insert_Edit_Data {

    List<Vehicles> vehiclesList;
    public static String type;
    ProgressBar progressbar,progressBar1;
    LinearLayout lin_no_row;
    RecyclerView rvFeed;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager layoutManager;
    private static boolean loadingMore = true;
    public static int first = 0;
    public Car_List_Adapter feedAdapter;
    View view;
    public Car_Fav() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_car__fav, container, false);
        hoder();
        onclick();
        return view;
    }
    private void hoder() { 
        vehiclesList=new ArrayList<>();
        first=0;
        progressbar = (ProgressBar)  view.findViewById(R.id.progressbar);
        progressBar1 = (ProgressBar)  view.findViewById(R.id.progressBar1);

        lin_no_row = (LinearLayout)  view.findViewById(R.id.lin_no_row);

        rvFeed = (RecyclerView)  view.findViewById(R.id.rvFeed);

        rvFeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (!loadingMore) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
//                            Log.v("shod1", "sholde");

                            loadingMore = true;
                            first += 10;
                            getdata();


                        }

                    }
                }
            }
        });

    }

    private void onclick(){  

        rvFeed.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvFeed, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(getActivity(), "gkjfgjdfg", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ShowCarActivity.class);
                intent.putExtra("vehicles",vehiclesList.get(position));
                intent.putExtra("type",type);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));


    }

    @Override
    public void onResume() {
        super.onResume();
        rvFeed.setVisibility(View.GONE);
        vehiclesList=new ArrayList<>();
        getdata();

    }

    private void getdata() {
        User user= UserHelper.LoadUserInfo(getActivity());
        if (user.isVerrified()&&user.isLoggedIn()) {

            String url = null;
            url = StaticData.get_fav_car +"?token="+new UserSessionManager(getActivity()).getLoginToken()+ "&offset=" + first + "&limit=10";


            if (first == 0) {
                progressbar.setVisibility(View.VISIBLE);
            } else {
                progressBar1.setVisibility(View.VISIBLE);
            }

            Map<String, String> params = new HashMap<String, String>();

            Get_Volley_Call_Back.binddata(Car_Fav.this);

            Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 1003);
        }else{
            lin_no_row.setVisibility(View.VISIBLE);
            rvFeed.setVisibility(View.GONE);
        }

    }
    @Override
    public void on_volley_response(String response, int id) {

        try {
            JSONObject jsonObject=new JSONObject(response);
            if (id==1003){
                progressbar.setVisibility(View.GONE);
                progressBar1.setVisibility(View.GONE);
                if (jsonObject.getString("status").equals("200")){
                    List<Vehicles>vehiclesList1=Vehicles.Import_List(jsonObject,"list");
                    if (vehiclesList1.size() == 10) {
                        loadingMore = false;
                    }
                    for (int i=0;i<vehiclesList1.size();i++){
                        vehiclesList.add(vehiclesList1.get(i));
                    }
                    if (first == 0) {
                        setupfeed();
                    } else {
                        feedAdapter.notifyDataSetChanged();
                    }

                    if (vehiclesList1.size()==0&&vehiclesList.size()==0){
                        lin_no_row.setVisibility(View.VISIBLE);
                        rvFeed.setVisibility(View.GONE);

                    }else {
                        rvFeed.setVisibility(View.VISIBLE);
                        lin_no_row.setVisibility(View.GONE);

                    }


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupfeed(){
        layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        feedAdapter = new Car_List_Adapter( vehiclesList,getActivity());
        rvFeed.setLayoutManager(layoutManager);
        rvFeed.setAdapter(feedAdapter);
        feedAdapter.notifyDataSetChanged();
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }

}

