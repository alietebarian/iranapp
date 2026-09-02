package com.ideabonyan.iranapp.Fragment.Account;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Activity.LoginActivity;
import com.ideabonyan.iranapp.Activity.ShowAdActivity;
import com.ideabonyan.iranapp.Adapter.AdListAdapter;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.RemoveAd;
import com.ideabonyan.iranapp.Models.AdsToBeListed;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.RecyclerItemClickListener;
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
public class My_Descount_adds extends Fragment implements Get_Insert_Edit_Data, RemoveAd {
    NestedScrollView nested;

    RecyclerView rv;
    ProgressBar progressBar, loadMoreProgressbar;
    View view;
    TextView emptyListText;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    public My_Descount_adds() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my__descount_adds, container, false);
        holder();
//        getData();
        smallStuff();

        return view;
    }

    private void holder() {

        rv = (RecyclerView) view.findViewById(R.id.dashboardRv);
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
                            isLoadingNow = true;
                            getData();


                        }
                    }
                }
            }
        });

    }
    private void smallStuff() {

//        setSupportActionBar(toolbar);
        ShowAdActivity.bindData(this);
    }

    List<AdsToBeListed> adData;
    int offset = 0;
    boolean loadMore = true;
    boolean isLoadingNow = false;
    boolean isThereData;


    private void getData() {
        String url = StaticData.USER_ADS + "?token=" + new UserSessionManager(getActivity()).getLoginToken()
                + "&offset=" + offset + "&limit=10";
        Log.v("url",url);
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 132);
    }

    @Override
    public void on_volley_response(String response, int id) {

        if (id == 132) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("status").equals("200")) {
                    if (adData == null) {
                        adData = AdsToBeListed.Import(jsonObject);
                        if (adData.size() < 10) loadMore = false;
                    } else {
                        List<AdsToBeListed> newSet = AdsToBeListed.Import(jsonObject);
                        for (AdsToBeListed ad : newSet) {
                            adData.add(ad);
                        }
                        loadMoreProgressbar.setVisibility(View.GONE);
                        if (newSet.size() < 10) loadMore = false;
                        isLoadingNow = false;
                    }
                    runRv();
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
            }
        }
    }

    private void runRv() {

//        TransitionManager.beginDelayedTransition(rootView);

        progressBar.setVisibility(View.GONE);

        if (adData.size() > 0 || isThereData) {

            isThereData = true;

            offset += 10;

            AdListAdapter adapter = new AdListAdapter(adData, getActivity());
            final LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
            rv.setLayoutManager(layoutManager);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            rv.setVisibility(View.VISIBLE);

            rv.setNestedScrollingEnabled(false);

            recyclerViewOnClickListener();

        } else {
            emptyListText.setVisibility(View.VISIBLE);
        }
    }


    private void recyclerViewOnClickListener() {

        rv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                AdsToBeListed ad;
                ad = adData.get(position);
                Intent intent = new Intent(getActivity(), ShowAdActivity.class);
                intent.putExtra("ad",ad);
//                ShowAdActivity.ad = ad;
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

    }


    @Override
    public void on_volley_error(VolleyError error, int id) {

    }

    @Override
    public void onAdRemoved() {
//        rv.setVisibility(View.GONE);
//        progressBar.setVisibility(View.VISIBLE);
//        offset = 0;
//        adData = null;
//        getData();
    }


    @Override
    public void onResume() {
        super.onResume();
        rv.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        offset = 0;
        adData = null;
        getData();
    }
}
