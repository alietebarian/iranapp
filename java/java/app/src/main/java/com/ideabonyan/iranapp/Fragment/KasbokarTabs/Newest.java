package com.ideabonyan.iranapp.Fragment.KasbokarTabs;


import android.content.Intent;
import android.os.Bundle;
import androidx.transition.TransitionManager;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Activity.ShowAdActivity;
import com.ideabonyan.iranapp.Adapter.AdListAdapter;
import com.ideabonyan.iranapp.Fragment.Dialogs.CityPickerDialogFragment;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data2;
import com.ideabonyan.iranapp.Interface.LocationChange;
import com.ideabonyan.iranapp.Interface.RemoveAd;
import com.ideabonyan.iranapp.Models.AdsToBeListed;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back2;
import com.ideabonyan.iranapp.Utils.RecyclerItemClickListener;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Newest extends Fragment implements RemoveAd,  LocationChange {


    public Newest() {
        // Required empty public constructor
    }


    View view;
    RecyclerView rv;
    ProgressBar progressBar, loadMoreProgressbar;
    RelativeLayout listEmptyText;
    ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_kasbokar_newest, container, false);

        offset = 0;
        isThereData = false;
        adData = null;

        rv = (RecyclerView) view.findViewById(R.id.kasbokarNewestRecyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.kasbokarNewestProgressBar);
        listEmptyText = (RelativeLayout) view.findViewById(R.id.kasbokarNewestListEmptyDialog);
        rootView = (ViewGroup) view.findViewById(R.id.kasbokarNewest);
        loadMoreProgressbar = (ProgressBar) view.findViewById(R.id.kasbokarNewestLoadMoreProgressBar);

        CityPickerDialogFragment.binddata(this);
        ShowAdActivity.bindData(this);

        getData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

//        offset = 0;
//        isThereData = false;
//        adData = null;
//
//        rv = (RecyclerView) view.findViewById(R.id.kasbokarNewestRecyclerView);
//        progressBar = (ProgressBar) view.findViewById(R.id.kasbokarNewestProgressBar);
//        listEmptyText = (RelativeLayout) view.findViewById(R.id.kasbokarNewestListEmptyDialog);
//        rootView = (ViewGroup) view.findViewById(R.id.kasbokarNewest);
//        loadMoreProgressbar = (ProgressBar) view.findViewById(R.id.kasbokarNewestLoadMoreProgressBar);
//
//        CityPickerDialogFragment.binddata(this);
//        ShowAdActivity.bindData(this);
//
//        getData();
    }


    int offset = 0;
    List<AdsToBeListed> adData;
    boolean loadMore = true;
    boolean isLoadingNow = false;
    boolean isThereData;

    private void getData() {

        String city = new UserSessionManager(getActivity()).getCityInfo();
        String url = StaticData.NEWEST_ADS + "?city_id=" + city + "&offset=" + offset + "&limit=10";
        Log.v("url",url);
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(new Get_Insert_Edit_Data() {
            @Override
            public void on_volley_response(String response, int id) {
                if (id == 10012){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void on_volley_error(VolleyError error, int id) {

            }
        });
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 10012);
    }




    AdListAdapter adapter;
    LinearLayoutManager layoutManager;
    private void runRv() {

        if (!isThereData) TransitionManager.beginDelayedTransition(rootView);

        progressBar.setVisibility(View.GONE);

        if (adData.size() > 0) {

            offset += 10;
//            Log.i("11111111", offset+"");


            if (!isThereData) {
                adapter = new AdListAdapter(adData, getActivity());
                layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                rv.setLayoutManager(layoutManager);
                rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                rv.setVisibility(View.VISIBLE);
            } else adapter.notifyDataSetChanged();

            isThereData = true;

            recyclerViewOnClickListener();

            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) //check for scroll down
                    {
//                        Toast.makeText(AdListActivity.this, "1", Toast.LENGTH_SHORT).show();
                        if (!isLoadingNow) {

//                            Toast.makeText(AdListActivity.this, "2", Toast.LENGTH_SHORT).show();

                            int visibleItemCount = layoutManager.getChildCount();
                            int totalItemCount = layoutManager.getItemCount();
                            int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                            if (pastVisibleItems + visibleItemCount >= totalItemCount) {

                                if (loadMore) {
                                    loadMoreProgressbar.setVisibility(View.VISIBLE);
                                    isLoadingNow = true;
                                    getData();
                                }
                            }
                        }
                    }
                }
            });
        }
        else {
            listEmptyText.setVisibility(View.VISIBLE);
        }
    }


    private void recyclerViewOnClickListener() {

        rv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rv, new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {

                AdsToBeListed ad;
                ad = adData.get(position);
                Intent intent = new Intent(getActivity(), ShowAdActivity.class);
                intent.putExtra("ad",ad);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

    }

    @Override
    public void onLocationChanged() {
//        ShowToast.success("a;sjdklf", getActivity());
//        getData();

        getActivity().recreate();
    }

    @Override
    public void onAdRemoved() {
        rv.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        offset = 0;
        adData = null;
        getData();
    }
}
