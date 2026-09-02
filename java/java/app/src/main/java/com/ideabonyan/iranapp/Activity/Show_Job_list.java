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
import com.ideabonyan.iranapp.Adapter.Job_List_Adapter;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
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

public class Show_Job_list extends AppCompatActivity implements Get_Insert_Edit_Data {
    List<Job> jobs;
    public static String type;
    ProgressBar progressbar, progressBar1;
    LinearLayout lin_no_row;
    RecyclerView rvFeed;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager layoutManager;
    private static boolean loadingMore = true;
    public static int first = 0;
    public Job_List_Adapter feedAdapter;
    ImageButton backInToolbar;
    ImageButton btn_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__job_list);
        hoder();
        getdata();
        onclick();
    }

    private void hoder() {
        backInToolbar = (ImageButton) findViewById(R.id.newAdBackButton);
        btn_search = (ImageButton) findViewById(R.id.btn_search);

        jobs = new ArrayList<>();
        first = 0;
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);

        lin_no_row = (LinearLayout) findViewById(R.id.lin_no_row);

        rvFeed = (RecyclerView) findViewById(R.id.rvFeed);

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

    private void onclick() {
        backInToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Show_Job_list.this,Job_Search.class);
                if (type == null) {
                    intent.putExtra("cat","");
                } else {
                    intent.putExtra("cat",type);
                }
                startActivity(intent);
            }
        });

        rvFeed.addOnItemTouchListener(new RecyclerItemClickListener(Show_Job_list.this, rvFeed, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                ShowCarActivity.type=type;
//                Show_Job_Ad_Details.job=jobs.get(position);

                Intent intent = new Intent(Show_Job_list.this, Show_Job_Ad_Details.class);
                intent.putExtra("job",jobs.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));


    }

    private void getdata() {
        String city = new UserSessionManager(Show_Job_list.this).getCityInfo();
        String url = "";
        if (type == null) {
            url = StaticData.employs + "/cities/" + city + "/ads"+ "?offset=" + first + "&limit=10";

        } else {
            url = StaticData.employs + "/cities/" + city + "/ads" + "?type=" + type+ "&offset=" + first + "&limit=10";
        }
        if (first == 0) {
            progressbar.setVisibility(View.VISIBLE);
        } else {
            progressBar1.setVisibility(View.VISIBLE);
        }

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Show_Job_list.this);

        Get_Volley_Call_Back.Call_Volley(Show_Job_list.this, params, url, Request.Method.GET, 151);


    }

    @Override
    public void on_volley_response(String response, int id) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (id == 151) {
                progressbar.setVisibility(View.GONE);
                progressBar1.setVisibility(View.GONE);
                if (jsonObject.getString("status").equals("200")) {
                    List<Job> jobs1 = Job.Import(jsonObject.getJSONArray("list"));
                    if (jobs1.size() == 10) {
                        loadingMore = false;
                    }
                    for (int i = 0; i < jobs1.size(); i++) {
                        jobs.add(jobs1.get(i));
                    }
                    if (first == 0) {
                        setupfeed();
                    } else {
                        feedAdapter.notifyDataSetChanged();
                    }

                    if (jobs1.size() == 0 && jobs.size() == 0) {
                        lin_no_row.setVisibility(View.VISIBLE);
                        rvFeed.setVisibility(View.GONE);

                    } else {
                        rvFeed.setVisibility(View.VISIBLE);
                        lin_no_row.setVisibility(View.GONE);

                    }


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupfeed() {
        layoutManager = new GridLayoutManager(Show_Job_list.this, 1, GridLayoutManager.VERTICAL, false);
        feedAdapter = new Job_List_Adapter(jobs, Show_Job_list.this);
        rvFeed.setLayoutManager(layoutManager);
        rvFeed.setAdapter(feedAdapter);
        feedAdapter.notifyDataSetChanged();
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }


}
