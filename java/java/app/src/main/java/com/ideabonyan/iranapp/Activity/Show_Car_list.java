package com.ideabonyan.iranapp.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Adapter.Car_List_Adapter;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Models.Vehicles;
import com.ideabonyan.iranapp.R;
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

public class Show_Car_list extends AppCompatActivity implements Get_Insert_Edit_Data {
    List<Vehicles> vehiclesList;
    public String type;
    ProgressBar progressbar,progressBar1;
    LinearLayout lin_no_row;
    RecyclerView rvFeed;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager layoutManager;
    private static boolean loadingMore = true;
    public static int first = 0;
    public Car_List_Adapter feedAdapter;
    ImageButton backInToolbar;
    ImageButton btn_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__car_list);
        hoder();
        getdata();
        onclick();
    }
    private void hoder() {
        type=getIntent().getStringExtra("type");
        backInToolbar = (ImageButton) findViewById(R.id.newAdBackButton);
        btn_search=findViewById(R.id.btn_search);
        vehiclesList=new ArrayList<>();
        first=0;
        progressbar = (ProgressBar)  findViewById(R.id.progressbar);
        progressBar1 = (ProgressBar)  findViewById(R.id.progressBar1);

        lin_no_row = (LinearLayout)  findViewById(R.id.lin_no_row);

        rvFeed = (RecyclerView)  findViewById(R.id.rvFeed);

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
        backInToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Show_Car_list.this,Car_Search.class);
                intent.putExtra("type",type);
                startActivity(intent);
            }
        });

        rvFeed.addOnItemTouchListener(new RecyclerItemClickListener(Show_Car_list.this, rvFeed, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Toast.makeText(Show_Car_list.this, "gkjfgjdfg", Toast.LENGTH_SHORT).show();
//                ShowCarActivity.type=type;

//                ShowCarActivity.vehicles=vehiclesList.get(position);
                Intent intent = new Intent(Show_Car_list.this, ShowCarActivity.class);
                intent.putExtra("vehicles",vehiclesList.get(position));
                intent.putExtra("type",type);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));


    }

    private void getdata() {
        String city = new UserSessionManager(Show_Car_list.this).getCityInfo();

        String url = null;
        try {
            url = StaticData.get_car + "?type=" + type + "&city_id=" + city+ "&offset=" + first + "&limit=10";
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        if (first==0){
            progressbar.setVisibility(View.VISIBLE);
        }else{
            progressBar1.setVisibility(View.VISIBLE);
        }

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Show_Car_list.this);

        Get_Volley_Call_Back.Call_Volley(Show_Car_list.this,params,url, Request.Method.GET,125);


    }

    @Override
    public void on_volley_response(String response, int id) {

        try {
            JSONObject jsonObject=new JSONObject(response);
            if (id==125){
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
        layoutManager = new GridLayoutManager(Show_Car_list.this, 1, GridLayoutManager.VERTICAL, false);
        feedAdapter = new Car_List_Adapter( vehiclesList,Show_Car_list.this);
        rvFeed.setLayoutManager(layoutManager);
        rvFeed.setAdapter(feedAdapter);
        feedAdapter.notifyDataSetChanged();
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }

}

