package com.ideabonyan.iranapp.Activity;

import android.content.Intent;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Adapter.Car_List_Adapter;
import com.ideabonyan.iranapp.Fragment.Dialogs.Select_Car_Search_Filter;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.Select_Car_Filter;
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

public class Car_Search extends AppCompatActivity implements Get_Insert_Edit_Data, Select_Car_Filter {
    TextView no_value;
    LinearLayout kasbokarSearchBTN, lin_select_filter;
    String cat_name;
    int brand_id, model_id;
    String chasi_type, tolid_from, tolid_to, cost_from, cost_to, kilometer_from, kilometer_to,region_id,order_bye;
    int motor_weghit;
    int province_id;
    int city_id;
    String type;
    NestedScrollView kasbokarSearchNestedScroll;
    RecyclerView kasbokarSearchRV;
    EditText kasbokarSearchEditText;
    ProgressBar progressBar1,progressbar;
    List<Vehicles>vehiclesList;
    LinearLayoutManager layoutManager;
    private static boolean loadingMore = true;
    public static int first = 0;
    public Car_List_Adapter feedAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car__search);
        holder();
        onclick();
    }

    private void holder() {
        cat_name=type;
        vehiclesList=new ArrayList<>();
        cat_name = "";
        brand_id = 0;
        model_id = 0;
        chasi_type=""; tolid_from=""; tolid_to=""; cost_from=""; cost_to=""; kilometer_from=""; kilometer_to=""; order_bye=""; region_id="";
        motor_weghit=0;
        province_id=0;
        city_id=0;
        kasbokarSearchBTN = (LinearLayout) findViewById(R.id.kasbokarSearchBTN);
        lin_select_filter = (LinearLayout) findViewById(R.id.lin_select_filter);
        kasbokarSearchNestedScroll = (NestedScrollView) findViewById(R.id.kasbokarSearchNestedScroll);
        kasbokarSearchRV = (RecyclerView) findViewById(R.id.kasbokarSearchRV);
        kasbokarSearchRV.setNestedScrollingEnabled(false);
        kasbokarSearchEditText= (EditText) findViewById(R.id.kasbokarSearchEditText);
        progressbar= (ProgressBar) findViewById(R.id.progressbar);
        progressBar1= (ProgressBar) findViewById(R.id.progressBar1);
        no_value= (TextView) findViewById(R.id.kasbokarSearchEmptyListText);
        kasbokarSearchNestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    if (!loadingMore) {
                        loadingMore = true;
                        first += 10;
                            get_all_car();

                    }
                }
            }
        });
        type=getIntent().getStringExtra("type");
        cat_name=type;

    }

    private void onclick() {
        kasbokarSearchRV.addOnItemTouchListener(new RecyclerItemClickListener(Car_Search.this, kasbokarSearchRV, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(Car_Search.this, ShowCarActivity.class);
                intent.putExtra("vehicles",vehiclesList.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

        lin_select_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Select_Car_Search_Filter.binddata(Car_Search.this);
                Select_Car_Search_Filter select_car_search_filter = new Select_Car_Search_Filter(type);
                select_car_search_filter.show(getFragmentManager(), "select_car_search_filter");
            }
        });

        kasbokarSearchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                vehiclesList=new ArrayList<Vehicles>();
                first=0;
                get_all_car();
            }
        });
    }
    private void get_all_car() {

        if (first==0){
            progressbar.setVisibility(View.VISIBLE);
            kasbokarSearchRV.setVisibility(View.GONE);
        }else{
            progressBar1.setVisibility(View.VISIBLE);
        }
        String city = new UserSessionManager(Car_Search.this).getCityInfo();

        String Url = StaticData.search_car;
        Map<String, String> params = new HashMap<String, String>();

        if (!(region_id.equals("")||region_id.equals("0"))){
            params.put("region_id",region_id+"");

        }else if (city_id!=0){
            params.put("city_id",city_id+"");

        }else   if (province_id!=0){
            params.put("province_id",province_id+"");

        }else{
            params.put("city_id",city+"");

        }
        if (!(order_bye.equals("")||order_bye.equals("0"))){
            if (order_bye.equals("1")){
                params.put("ordering","highest_price");

            }else if (order_bye.equals("2")){
                params.put("ordering","lowest_price");

            }else if (order_bye.equals("3")){
                params.put("ordering","latest");

            }
        }


        if (motor_weghit!=0){
            params.put("cylinder_volume_id",motor_weghit+"");

        }


        params.put("offset",first+"");
        params.put("limit","10");
        if (brand_id!=0){
            if (model_id==0){
                params.put("brand_id",""+brand_id);

            }else{
                params.put("model_id",""+model_id);

            }
        }

        if (!cat_name.equals("")){
            params.put("type",""+cat_name);

        }
        if (kasbokarSearchEditText.getText().length()!=0){
            params.put("ads_title",kasbokarSearchEditText.getText().toString());

        }
        if (!chasi_type.equals("")){
            params.put("chassis_type",""+chasi_type);
        }
        if (!tolid_from.equals("")){
            params.put("production_year_from",""+tolid_from);
            params.put("production_year_to",""+tolid_to);
        }
        if (!cost_from.equals("")){
            params.put("price_from",""+cost_from);
            params.put("price_to",""+cost_to);

        }
        if (!kilometer_from.equals("")){
            params.put("kilometer_from",""+kilometer_from);
            params.put("kilometer_to",""+kilometer_to);

        }
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            Log.v("param->",entry.getKey() + "=" + entry.getValue());
//            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
        Get_Volley_Call_Back.binddata(Car_Search.this);
        Get_Volley_Call_Back.Call_Volley(Car_Search.this,params,Url,Request.Method.POST,1001);

    }


    @Override
    public void on_volley_response(String response, int id) {
        try {
            JSONObject jsonObject=new JSONObject(response);
            if (id==1001){
                progressBar1.setVisibility(View.GONE);
                progressbar.setVisibility(View.GONE);
                if (jsonObject.getString("status").equals("200")){
                    List<Vehicles>vehiclesList1=Vehicles.Import_List(jsonObject,"list");
                    for (int i=0;i<vehiclesList1.size();i++){
                        vehiclesList.add(vehiclesList1.get(i));
                    }
                    if (first == 0) {
                        setupfeed();
                    } else {
                        feedAdapter.notifyDataSetChanged();
                    }
                    if (vehiclesList.size()>0){
                        kasbokarSearchRV.setVisibility(View.VISIBLE);
                        no_value.setVisibility(View.GONE);
                    }else{
                        kasbokarSearchRV.setVisibility(View.GONE);
                        no_value.setVisibility(View.VISIBLE);
                    }

                    if (vehiclesList1.size()==10){
                        loadingMore = false;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }



    @Override
    public void on_filter_set(String cat, int brand_id, int model_id, String chasi_type, String tolid_from,
                              String tolid_to, String cost_from, String cost_to, String kilometer_from, String kilometer_to,
                              int motor_weghit, int province_id, int city_id,String order_bye,String region_id) {
        this.cat_name = cat;
        type=cat;
        this.brand_id = brand_id;
        this.model_id = model_id;
        this.chasi_type = chasi_type;
        this.tolid_from = tolid_from;
        this.tolid_to = tolid_to;
        this.cost_from = cost_from;
        this.cost_to = cost_to;
        this.kilometer_from = kilometer_from;
        this.kilometer_to = kilometer_to;
        this.motor_weghit = motor_weghit;
        this.province_id = province_id;
        this.city_id = city_id;
        this.region_id = region_id;
        this.order_bye = order_bye;

        vehiclesList=new ArrayList<Vehicles>();
        first=0;
        get_all_car();
    }
    private void setupfeed(){
        layoutManager = new GridLayoutManager(Car_Search.this, 1, GridLayoutManager.VERTICAL, false);
        feedAdapter = new Car_List_Adapter( vehiclesList,Car_Search.this);
        kasbokarSearchRV.setLayoutManager(layoutManager);
        kasbokarSearchRV.setAdapter(feedAdapter);
        feedAdapter.notifyDataSetChanged();
    }

}
