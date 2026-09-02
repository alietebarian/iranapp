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
import com.ideabonyan.iranapp.Activity.Show_Job_Ad_Details;
import com.ideabonyan.iranapp.Adapter.Job_List_Adapter;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data3;
import com.ideabonyan.iranapp.Models.Job;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back3;
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
public class Job_Fav extends Fragment implements Get_Insert_Edit_Data3 {
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
    View view;

    public Job_Fav() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_job_fav, container, false);
        hoder();
        onclick();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        jobs=new ArrayList<>();
        rvFeed.setVisibility(View.GONE);
        getdata();

    }

    private void hoder() {

        jobs = new ArrayList<>();
        first = 0;
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        progressBar1 = (ProgressBar) view.findViewById(R.id.progressBar1);

        lin_no_row = (LinearLayout) view.findViewById(R.id.lin_no_row);

        rvFeed = (RecyclerView) view.findViewById(R.id.rvFeed);

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

        rvFeed.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvFeed, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                ShowCarActivity.type=type;
                Intent intent = new Intent(getActivity(), Show_Job_Ad_Details.class);
                intent.putExtra("job",jobs.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));


    }

    private void getdata() {
        String url  = StaticData.get_fav_job + "?token=" + new UserSessionManager(getActivity()).getLoginToken()
                + "&offset=" + first + "&limit=10";


        if (first == 0) {
            progressbar.setVisibility(View.VISIBLE);
        } else {
            progressBar1.setVisibility(View.VISIBLE);
        }

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back3.binddata(Job_Fav.this);

        Get_Volley_Call_Back3.Call_Volley(getActivity(), params, url, Request.Method.GET, 151);


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
        layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        feedAdapter = new Job_List_Adapter(jobs, getActivity());
        rvFeed.setLayoutManager(layoutManager);
        rvFeed.setAdapter(feedAdapter);
        feedAdapter.notifyDataSetChanged();
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }


}
