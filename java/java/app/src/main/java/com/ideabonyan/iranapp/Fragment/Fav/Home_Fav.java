package com.ideabonyan.iranapp.Fragment.Fav;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Activity.Show_Home_Ad_Details;
import com.ideabonyan.iranapp.Adapter.Home_List_Adapter;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data2;
import com.ideabonyan.iranapp.Models.Estates;
import com.ideabonyan.iranapp.Models.Home_Category;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back2;
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
public class Home_Fav extends Fragment implements Get_Insert_Edit_Data2 {

    RecyclerView  rv_add_Feed;
    ProgressBar adListProgressBar, adListLoadMoreProgressBar;
    public static int cat_id;
    String sub_cat_id;
    List<Home_Category> sub_cat;
    LinearLayoutManager layoutManager;
    private static boolean loadingMore = true;
    public static int first = 0;
    public boolean is_last_page = false;
    List<Estates> estatesList;
    Home_List_Adapter feedAdapter;
    View view;
    LinearLayout lin_no_row;    int pastVisiblesItems, visibleItemCount, totalItemCount;


    public Home_Fav() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_home__fav, container, false);
        holder();
        onclick();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        estatesList=new ArrayList<>();
        rv_add_Feed.setVisibility(View.GONE);
        get_all_data();

    }

    private void holder() {
        estatesList = new ArrayList<>();
        lin_no_row=view.findViewById(R.id.lin_no_row);
 
        rv_add_Feed = (RecyclerView) view.findViewById(R.id.rv_add_Feed);
        rv_add_Feed.setNestedScrollingEnabled(false);

        adListProgressBar = (ProgressBar) view.findViewById(R.id.adListProgressBar);
        adListLoadMoreProgressBar = (ProgressBar) view.findViewById(R.id.adListLoadMoreProgressBar);

        first = 0;
        rv_add_Feed.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            get_all_data();


                        }

                    }
                }
            }
        });

        estatesList = new ArrayList<>();

    }

    private void onclick() {
       
      
         
        rv_add_Feed.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rv_add_Feed, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=(new Intent(getActivity(), Show_Home_Ad_Details.class));
                intent.putExtra("estates",estatesList.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

    }


    private void get_all_data() {
        String url = StaticData.get_fav_home + "?token="+new UserSessionManager(getActivity()).getLoginToken() + "&offset=" + first + "&limit=10";


        if (first == 0) {
            adListProgressBar.setVisibility(View.VISIBLE);
        } else {
            adListLoadMoreProgressBar.setVisibility(View.VISIBLE);
        }

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back2.binddata(Home_Fav.this);

        Get_Volley_Call_Back2.Call_Volley(getActivity(), params, url, Request.Method.GET, 1004);

    }



    @Override
    public void on_volley_response(String response, int id) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (id == 1004) {
                adListProgressBar.setVisibility(View.GONE);
                adListLoadMoreProgressBar.setVisibility(View.GONE);
                if (jsonObject.getString("status").equals("200")) {
                    List<Estates> estatesList1 = Estates.Import_list(jsonObject.getJSONArray("list"));
                    Log.v("size", estatesList1.size() + "");
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
                        lin_no_row.setVisibility(View.VISIBLE);
                        rv_add_Feed.setVisibility(View.GONE);

                    } else {
                        rv_add_Feed.setVisibility(View.VISIBLE);
                        lin_no_row.setVisibility(View.GONE);

                    }
                    Log.v("size", estatesList.size() + "");


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
        layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        feedAdapter = new Home_List_Adapter(estatesList, getActivity());
        rv_add_Feed.setLayoutManager(layoutManager);
        rv_add_Feed.setAdapter(feedAdapter);
        feedAdapter.notifyDataSetChanged();
    }

}
