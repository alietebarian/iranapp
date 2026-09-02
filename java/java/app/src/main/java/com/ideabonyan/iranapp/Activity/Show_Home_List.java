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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Adapter.Cat_Home_Addapter;
import com.ideabonyan.iranapp.Adapter.Home_List_Adapter;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Models.Estates;
import com.ideabonyan.iranapp.Models.Home_Category;
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

public class Show_Home_List extends AppCompatActivity implements Get_Insert_Edit_Data {

    RecyclerView rv_cat_Feed, rv_add_Feed;
    ProgressBar adListProgressBar, adListLoadMoreProgressBar;
    public  String cat_id;
    String sub_cat_id="0";
    List<Home_Category> sub_cat;
    LinearLayoutManager layoutManager;
    private   boolean loadingMore = true;
    public  int first = 0;
    NestedScrollView nested;
    public boolean is_last_page = false;
    List<Estates> estatesList;
    TextView txt_no_val;
    Home_List_Adapter feedAdapter;
    ImageButton backInToolbar;
    ImageButton btn_search;
int code=1000545;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__home__list);
        holder();
        onclick();
        binddata();


    }

    private void holder() {
        cat_id=getIntent().getStringExtra("cat_id");
        txt_no_val = (TextView) findViewById(R.id.txt_no_val);
        estatesList = new ArrayList<>();
        backInToolbar = (ImageButton) findViewById(R.id.newAdBackButton);
        btn_search=findViewById(R.id.btn_search);

        rv_cat_Feed = (RecyclerView) findViewById(R.id.rv_cat_Feed);
        rv_add_Feed = (RecyclerView) findViewById(R.id.rv_add_Feed);
        rv_cat_Feed.setNestedScrollingEnabled(false);
        rv_add_Feed.setNestedScrollingEnabled(false);

        adListProgressBar = (ProgressBar) findViewById(R.id.adListProgressBar);
        adListLoadMoreProgressBar = (ProgressBar) findViewById(R.id.adListLoadMoreProgressBar);

        first = 0;
        nested = (NestedScrollView) findViewById(R.id.nested);
        nested.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    if (!loadingMore) {
                        loadingMore = true;
                        first += 10;
                        get_all_data();
//                        Log.v("is hear", "nested");
                    }
                }
            }
        });

        estatesList = new ArrayList<>();

    }

    private void onclick() {
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Show_Home_List.this,Home_Search.class);
//                Log.v("catid",cat_id);
                intent.putExtra("cat_id",cat_id);
               if (rv_cat_Feed.getVisibility()==View.VISIBLE){
                   intent.putExtra("sub_cat_id","0");

               }else{
                   intent.putExtra("sub_cat_id",sub_cat_id);



               }
                startActivity(intent);
            }
        });
        backInToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rv_cat_Feed.addOnItemTouchListener(new RecyclerItemClickListener(Show_Home_List.this, rv_cat_Feed, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                is_last_page = true;
                rv_cat_Feed.setVisibility(View.GONE);
                rv_add_Feed.setVisibility(View.GONE);
                first = 0;
                sub_cat_id = sub_cat.get(position).getId();
                estatesList = new ArrayList<Estates>();
                code++;

                get_all_data();
//                Toast.makeText(Show_Home_List.this, "aa"+sub_cat.get(position).getId(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));
        rv_add_Feed.addOnItemTouchListener(new RecyclerItemClickListener(Show_Home_List.this, rv_add_Feed, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(Show_Home_List.this,Show_Home_Ad_Details.class);
                intent.putExtra("estates",estatesList.get(position));
//                Show_Home_Ad_Details.estates=estatesList.get(position);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

    }

    private void binddata() {
        try {
            sub_cat = new ArrayList<>();


        if (cat_id.equals("1") ) {//home sell.
            sub_cat.add(new Home_Category("6", "آپارتمان", "1"));
            sub_cat.add(new Home_Category("7", "خانه و ویلا", "1"));
            sub_cat.add(new Home_Category("8", "زمین و کلنگی", "1"));
        } else if (cat_id.equals("2")) {// ejare home
            sub_cat.add(new Home_Category("9", "آپارتمان", "2"));
            sub_cat.add(new Home_Category("10", "خانه و ویلا", "2"));
        } else if (cat_id.equals("3")) {//seall edari
            sub_cat.add(new Home_Category("11", "دفترکار،اتاق اداری و مطب", "3"));
            sub_cat.add(new Home_Category("12", "مغازه و غرفه", "3"));
            sub_cat.add(new Home_Category("13", "صنعتی،کشاورزی وتجاری", "3"));
        } else if (cat_id.equals("4")) {//ejare edari
            sub_cat.add(new Home_Category("14", "دفترکار،اتاق اداری و مطب", "4"));
            sub_cat.add(new Home_Category("15", "مغازه و غرفه", "4"));
            sub_cat.add(new Home_Category("16", "صنعتی،کشاورزی وتجاری", "4"));
        } else if (cat_id.equals("5")) {//ejare edari
            sub_cat.add(new Home_Category("17", "آژانس املاک", "5"));
            sub_cat.add(new Home_Category("18", "مشارکت در ساخت", "5"));
            sub_cat.add(new Home_Category("19", "امور مالی و حقوقی", "5"));
            sub_cat.add(new Home_Category("20", "پیش فروش", "5"));
        }

        setup_sub_cat_feed();

        get_all_data();

        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (is_last_page) {
            rv_cat_Feed.setVisibility(View.VISIBLE);
            first = 0;
            is_last_page = false;
            rv_add_Feed.setVisibility(View.GONE);
            estatesList=new ArrayList<Estates>();
            get_all_data();
        }else  {

            Show_Home_List.this.finish();
        }
    }

    private void get_all_data() {
        String city = new UserSessionManager(Show_Home_List.this).getCityInfo();
        String url = StaticData.estates + "/city/" + city + "/ads" + "?offset=" + first + "&limit=10";
        if (is_last_page) {
            url += "&category_id=" + sub_cat_id;
        } else {
            url += "&category_id=" + cat_id;

        }

        if (first == 0) {
            adListProgressBar.setVisibility(View.VISIBLE);
        } else {
            adListLoadMoreProgressBar.setVisibility(View.VISIBLE);
        }

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Show_Home_List.this);

        Get_Volley_Call_Back.Call_Volley(Show_Home_List.this, params, url, Request.Method.GET, code);

    }

    private void setup_sub_cat_feed() {
        LinearLayoutManager layoutManager = new GridLayoutManager(Show_Home_List.this, 1, GridLayoutManager.VERTICAL, false);
        Cat_Home_Addapter feedAdapter = new Cat_Home_Addapter(sub_cat, Show_Home_List.this);
        rv_cat_Feed.setLayoutManager(layoutManager);
        rv_cat_Feed.setAdapter(feedAdapter);
        feedAdapter.notifyDataSetChanged();
    }

    @Override
    public void on_volley_response(String response, int id) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (id == code) {
                adListProgressBar.setVisibility(View.GONE);
                adListLoadMoreProgressBar.setVisibility(View.GONE);
                if (jsonObject.getString("status").equals("200")) {
                    List<Estates> estatesList1 = Estates.Import_list(jsonObject.getJSONArray("list"));
//                    Log.v("size", estatesList1.size() + "");
                    if (estatesList1.size() == 10) {
                        loadingMore = false;
                    }
                    for (int i = 0; i < estatesList1.size(); i++) {
                        estatesList.add(estatesList1.get(i));
                    }
                    if (first == 0) {
                        setupfeed();
                    } else {
                        feedAdapter.notifyDataSetChanged();
                    }

                    if (estatesList1.size() == 0 && estatesList.size() == 0) {
                        txt_no_val.setVisibility(View.VISIBLE);
                        rv_add_Feed.setVisibility(View.GONE);

                    } else {
                        rv_add_Feed.setVisibility(View.VISIBLE);
                        txt_no_val.setVisibility(View.GONE);

                    }
//                    Log.v("size", estatesList.size() + "");


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }


    private void setupfeed() {
        layoutManager = new GridLayoutManager(Show_Home_List.this, 1, GridLayoutManager.VERTICAL, false);
        feedAdapter = new Home_List_Adapter(estatesList, Show_Home_List.this);
        rv_add_Feed.setLayoutManager(layoutManager);
        rv_add_Feed.setAdapter(feedAdapter);
        feedAdapter.notifyDataSetChanged();
    }

}
