package com.ideabonyan.iranapp.Activity;

import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Adapter.Home_List_Adapter;
import com.ideabonyan.iranapp.Fragment.Dialogs.Select_Home_Search_Filter;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.Select_Home_Filter;
import com.ideabonyan.iranapp.Models.Estates;
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

public class Home_Search extends AppCompatActivity implements Get_Insert_Edit_Data, Select_Home_Filter {
    TextView no_value;
    LinearLayout kasbokarSearchBTN, lin_select_filter;
    NestedScrollView kasbokarSearchNestedScroll;
    RecyclerView kasbokarSearchRV;
    EditText kasbokarSearchEditText;
    ProgressBar progressBar1, progressbar;
    List<Estates> estatesList;
    LinearLayoutManager layoutManager;
    private boolean loadingMore = true;
    public int first = 0;
    Home_List_Adapter feedAdapter;
    String cat_id1,sub_cat_id1;

    String cat_id = "", home = "", person_type2 = "", type2 = "", meter_from = "", meter_to = "", cost_from = "", cost_to = "",
            ejare_from = "", ejare_to = "", vadaea_from = "", Vadea_to = "", sanad_edari = "", province_id = "", city_id = "", parent_id = "";
    String region_id = "", room_num = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home__search);
        holder();
        onclick();
    }

    private void holder() {
        cat_id1=getIntent().getStringExtra("cat_id");
        cat_id=cat_id1;
        sub_cat_id1=getIntent().getStringExtra("sub_cat_id");

        Log.v("subid",sub_cat_id1);
        if (!sub_cat_id1.equals("0")){
            cat_id=sub_cat_id1;
            parent_id=cat_id1;

        }
        Log.v("cat_id",cat_id1+"aaa");
        Log.v("cat_id",cat_id+"aaa");
        estatesList = new ArrayList<>();
        kasbokarSearchBTN = (LinearLayout) findViewById(R.id.kasbokarSearchBTN);
        lin_select_filter = (LinearLayout) findViewById(R.id.lin_select_filter);
        kasbokarSearchNestedScroll = (NestedScrollView) findViewById(R.id.kasbokarSearchNestedScroll);
        kasbokarSearchRV = (RecyclerView) findViewById(R.id.kasbokarSearchRV);
        kasbokarSearchRV.setNestedScrollingEnabled(false);
        kasbokarSearchEditText = (EditText) findViewById(R.id.kasbokarSearchEditText);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        no_value = (TextView) findViewById(R.id.kasbokarSearchEmptyListText);
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

    }

    private void onclick() {
        kasbokarSearchRV.addOnItemTouchListener(new RecyclerItemClickListener(Home_Search.this, kasbokarSearchRV, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=(new Intent(Home_Search.this, Show_Home_Ad_Details.class));
//                Show_Home_Ad_Details.estates = estatesList.get(position);
                intent.putExtra("estates",estatesList.get(position));
                startActivity(intent);
            }


            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

        lin_select_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Select_Home_Search_Filter.binddata(Home_Search.this);
                Select_Home_Search_Filter select_home_search_filter = new Select_Home_Search_Filter(cat_id1,sub_cat_id1);
                select_home_search_filter.show(getFragmentManager(), "select_home_search_filter");
            }
        });

        int a=120;

        kasbokarSearchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estatesList = new ArrayList<Estates>();
                first = 0;
                get_all_car();
            }
        });
    }

    private void get_all_car() {

        if (first == 0) {
            progressbar.setVisibility(View.VISIBLE);
            kasbokarSearchRV.setVisibility(View.GONE);
        } else {
            progressBar1.setVisibility(View.VISIBLE);
        }
        String city = new UserSessionManager(Home_Search.this).getCityInfo();

        String Url = StaticData.estates + "/search";
        Map<String, String> params = new HashMap<String, String>();
        if (kasbokarSearchEditText.getText().length() != 0) {
            params.put("keyword", kasbokarSearchEditText.getText().toString());

        }
        if (!(room_num.equals(""))) {
            params.put("rooms_count", room_num);

        }
        Log.v("region_id", region_id);
        if (!region_id.equals("0") && !region_id.equals("")) {
            params.put("region_id", region_id + "");

        } else if (!city_id.equals("0") && !city_id.equals("")) {
            params.put("city_id", city_id + "");

        }else  if (!province_id.equals("0") && !province_id.equals("")) {
            params.put("province_id", province_id + "");

        }else {
            params.put("city_id", city + "");

        }
//
//        if (!province_id.equals("0") && !province_id.equals("")) {
//            if (!city_id.equals("0") && !city_id.equals("")) {
//                if (!region_id.equals("0") && !region_id.equals("")) {
//                    params.put("region_id", region_id + "");
//
//                } else {
//                    params.put("city_id", city_id + "");
//                }
//
//            } else {
//                params.put("province_id", province_id + "");
//
//            }
//        } else {
//            params.put("city_id", city + "");
//
//        }
        if (!person_type2.equals("")) {
            params.put("user_type", person_type2);

        }
        params.put("offset", first + "");
        params.put("limit", "10");
//        Log.v("cat_id", cat_id + "ad");
        if (!cat_id.equals("0")) {
            if (cat_id.equals("1") || cat_id.equals("2") || cat_id.equals("3") || cat_id.equals("4") ||
                    parent_id.equals("1") || parent_id.equals("2") || parent_id.equals("3") || parent_id.equals("4")) {
                if (home.equals("0")) {
                    params.put("is_in_hoome",  "0");

                } else if (home.equals("1")) {
                    params.put("is_in_hoome",  "1");

                }
                if (!parent_id.equals("0") && !parent_id.equals("")) {

                    params.put("category_id", cat_id);

                } else {
                    params.put("category_id", cat_id);

                }

                if (!type2.equals("")) {
                    params.put("sell_or_buy", type2);
                }

                if (!meter_from.equals("")) {
                    params.put("meters_from", meter_from);
                    params.put("meters_to", meter_to);

                }
                if (cat_id.equals("1") || cat_id.equals("3") || parent_id.equals("1") || parent_id.equals("3")) {
                    if (!cost_from.equals("")) {
                        params.put("price_kharid_from", cost_from);
                        params.put("price_kharid_to", cost_to);

                    }
                    if (cat_id.equals("3") || parent_id.equals("3")) {
                        if (!sanad_edari.equals("-1")) {
                            params.put("sanad_edari", sanad_edari);

                        }
                    }
                }

                if (cat_id.equals("2") || cat_id.equals("4") || parent_id.equals("2") || parent_id.equals("4")) {
                    if (!ejare_from.equals("")) {
                        params.put("monthly_price_from", ejare_from);
                        params.put("monthly_price_to", ejare_to);

                    }
                    if (!vadaea_from.equals("")) {
                        params.put("vadiee_from", vadaea_from);
                        params.put("vadiee_to", Vadea_to);
                    }
                }


            }else if (cat_id.equals("5")||parent_id.equals("5")){
                if (!parent_id.equals("0") && !parent_id.equals("")) {
                    params.put("category_id", cat_id);

                } else {
                    params.put("category_id", cat_id);

                }
            }

        }


        for (Map.Entry<String, String> entry : params.entrySet()) {
            Log.v("param->", entry.getKey() + "=" + entry.getValue());
//            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
        Get_Volley_Call_Back.binddata(Home_Search.this);
        Get_Volley_Call_Back.Call_Volley(Home_Search.this, params, Url, Request.Method.POST, 1002);

    }


    @Override
    public void on_volley_response(String response, int id) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (id == 1002) {
                progressBar1.setVisibility(View.GONE);
                progressbar.setVisibility(View.GONE);
                if (jsonObject.getString("status").equals("200")) {
                    List<Estates> estatesList1 = Estates.Import_list(jsonObject.getJSONArray("list"));
                    for (int i = 0; i < estatesList1.size(); i++) {
                        estatesList.add(estatesList1.get(i));
                    }
                    if (first == 0) {
                        setupfeed();
                    } else {
                        feedAdapter.notifyDataSetChanged();
                    }
                    if (estatesList.size() > 0) {
                        kasbokarSearchRV.setVisibility(View.VISIBLE);
                        no_value.setVisibility(View.GONE);
                    } else {
                        kasbokarSearchRV.setVisibility(View.GONE);
                        no_value.setVisibility(View.VISIBLE);
                    }

                    if (estatesList1.size() == 10) {
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
        progressBar1.setVisibility(View.GONE);
        progressbar.setVisibility(View.GONE);
        kasbokarSearchRV.setVisibility(View.GONE);
        no_value.setVisibility(View.VISIBLE);
    }


    private void setupfeed() {
        layoutManager = new GridLayoutManager(Home_Search.this, 1, GridLayoutManager.VERTICAL, false);
        feedAdapter = new Home_List_Adapter(estatesList, Home_Search.this);
        kasbokarSearchRV.setLayoutManager(layoutManager);
        kasbokarSearchRV.setAdapter(feedAdapter);
        feedAdapter.notifyDataSetChanged();
    }

    @Override
    public void on_filter_set(String cat_id, String home, String person_type, String type, String meter_from, String meter_to,
                              String cost_from, String cost_to, String ejare_from, String ejare_to, String vadaea_from,
                              String Vadea_to, String sanad_edari, String province_id, String city_id, String parent_id, String region_id, String room_num) {
        this.cat_id = cat_id;
        this.home = home;
        this.person_type2 = person_type;
        this.type2 = type;
        this.meter_from = meter_from;
        this.meter_to = meter_to;
        this.cost_from = cost_from;
        this.cost_to = cost_to;
        this.ejare_from = ejare_from;
        this.ejare_to = ejare_to;
        this.vadaea_from = vadaea_from;
        this.Vadea_to = Vadea_to;
        this.sanad_edari = sanad_edari;
        this.province_id = province_id;
        this.city_id = city_id;
        this.parent_id = parent_id;
        this.room_num = room_num;
        this.region_id = region_id;
//        if (parent_id.equals("")){
//            cat_id1="0";
//        }else{
//            cat_id1=parent_id;
//
//        }
//        if (cat_id.equals("")){
//            sub_cat_id1="0";
//        }else{
//            sub_cat_id1=cat_id;
//
//        }
Log.v("ejare_from",ejare_from);
        estatesList = new ArrayList<>();
        first = 0;
        get_all_car();

    }
}
