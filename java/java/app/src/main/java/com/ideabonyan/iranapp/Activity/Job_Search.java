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
import com.ideabonyan.iranapp.Adapter.Job_List_Adapter;
import com.ideabonyan.iranapp.Fragment.Dialogs.Select_Job_Search_Filter;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.Select_Job_Filter;
import com.ideabonyan.iranapp.Models.Job;
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

public class Job_Search extends AppCompatActivity implements Get_Insert_Edit_Data,Select_Job_Filter {
    TextView no_value;
    LinearLayout kasbokarSearchBTN, lin_select_filter;
    NestedScrollView kasbokarSearchNestedScroll;
    RecyclerView kasbokarSearchRV;
    EditText kasbokarSearchEditText;
    ProgressBar progressBar1, progressbar;
    List<Job> jobs;
    LinearLayoutManager layoutManager;
    private static boolean loadingMore = true;
    public static int first = 0;
    Job_List_Adapter feedAdapter;
    String cat;
    String type = "", education_level = "", speciality_id = "", agreement_type = "", city_id = "", parent_id = "", region_id = "", order_bye = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job__search);
        holder();
        onclick();
    }

    private void holder() {
        cat=getIntent().getStringExtra("cat");
        type=cat;
        jobs = new ArrayList<>();
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
        kasbokarSearchRV.addOnItemTouchListener(new RecyclerItemClickListener(Job_Search.this, kasbokarSearchRV, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(Job_Search.this, Show_Job_Ad_Details.class);
                intent.putExtra("job",jobs.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

        lin_select_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Select_Job_Search_Filter.binddata(Job_Search.this);
                Select_Job_Search_Filter select_job_search_filter=new Select_Job_Search_Filter(cat);
                select_job_search_filter.show(getFragmentManager(),"select_job_search_filter");
            }
        });

        kasbokarSearchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobs = new ArrayList<>();
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
        String city = new UserSessionManager(Job_Search.this).getCityInfo();

        String Url = StaticData.employs + "/search";
        Map<String, String> params = new HashMap<String, String>();
        if (kasbokarSearchEditText.getText().length() != 0) {
            params.put("keyword", kasbokarSearchEditText.getText().toString());

        }
        params.put("offset",first+"");
        params.put("limit","10");
//        Log.v("region_id",region_id+"23");
        if (!(region_id.equals("")||region_id.equals("0"))){
            params.put("region_id", region_id + "");

        }else if (!city_id.equals("0") && !city_id.equals("")) {
            params.put("city_id", city_id + "");

        }else  if (!parent_id.equals("0") && !parent_id.equals("")) {
            params.put("province_id", parent_id + "");

        }else {
            params.put("city_id", city + "");

        }
       if (!type.equals("")){
           params.put("type", type);

       }
//       Log.v("type",type+"11");
       if (!education_level.equals("")){
           params.put("education_level", education_level);

       }if (!speciality_id.equals("")){
            params.put("speciality_id", speciality_id);

        }
        if (!agreement_type.equals("")){
            params.put("agreement_type", agreement_type);

        }
        if (first == 0) {
            progressbar.setVisibility(View.VISIBLE);
            kasbokarSearchRV.setVisibility(View.GONE);
        } else {
            progressBar1.setVisibility(View.VISIBLE);
        }
        if (kasbokarSearchEditText.getText().length()!=0){
            params.put("keyword", kasbokarSearchEditText.getText().toString());

        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
//            Log.v("param->", entry.getKey() + "=" + entry.getValue());
//            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
        Get_Volley_Call_Back.binddata(Job_Search.this);
        Get_Volley_Call_Back.Call_Volley(Job_Search.this, params, Url, Request.Method.POST, 1002);

    }


    @Override
    public void on_volley_response(String response, int id) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (id == 1002) {
                progressBar1.setVisibility(View.GONE);
                progressbar.setVisibility(View.GONE);
                if (jsonObject.getString("status").equals("200")) {
                    List<Job> jobs1 = Job.Import(jsonObject.getJSONArray("list"));
                    for (int i = 0; i < jobs1.size(); i++) {
                        jobs.add(jobs1.get(i));
                    }
                    if (first == 0) {
                        setupfeed();
                    } else {
                        feedAdapter.notifyDataSetChanged();
                    }
                    if (jobs.size() > 0) {
                        kasbokarSearchRV.setVisibility(View.VISIBLE);
                        no_value.setVisibility(View.GONE);
                    } else {
                        kasbokarSearchRV.setVisibility(View.GONE);
                        no_value.setVisibility(View.VISIBLE);
                    }

                    if (jobs1.size() == 10) {
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
        layoutManager = new GridLayoutManager(Job_Search.this, 1, GridLayoutManager.VERTICAL, false);
        feedAdapter = new Job_List_Adapter(jobs, Job_Search.this);
        kasbokarSearchRV.setLayoutManager(layoutManager);
        kasbokarSearchRV.setAdapter(feedAdapter);
        feedAdapter.notifyDataSetChanged();
    }

    @Override
    public void on_filter_set(String type, String education_level, String speciality_id, String agreement_type, String city_id
            , String parent_id, String order_bye, String region_id) {
       this.type=type;
       cat=type;
       this.education_level=education_level;
       this.speciality_id=speciality_id;
       this.agreement_type=agreement_type;
       this.city_id=city_id;
       this.parent_id=parent_id;
       this.region_id=region_id;
       this.order_bye=order_bye;



        jobs = new ArrayList<>();
        first = 0;
        get_all_car();
    }

//    @Override
//    public void on_filter_set(String cat_id, String home, String person_type, String type, String meter_from, String meter_to,
//                              String cost_from, String cost_to, String ejare_from, String ejare_to, String vadaea_from,
//                              String Vadea_to, String sanad_edari, String province_id, String city_id, String parent_id) {
//        this.cat_id = cat_id;
//        this.home = home;
//        this.person_type2 = person_type;
//        this.type2 = type;
//        this.meter_from = meter_from;
//        this.meter_to = meter_to;
//        this.cost_from = cost_from;
//        this.cost_to = cost_to;
//        this.ejare_from = ejare_from;
//        this.ejare_to = ejare_to;
//        this.vadaea_from = vadaea_from;
//        this.Vadea_to = Vadea_to;
//        this.sanad_edari = sanad_edari;
//        this.province_id = province_id;
//        this.city_id = city_id;
//        this.parent_id = parent_id;
//
//
//
//    }
}
