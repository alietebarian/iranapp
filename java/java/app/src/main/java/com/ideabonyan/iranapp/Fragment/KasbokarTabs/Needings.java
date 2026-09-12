package com.ideabonyan.iranapp.Fragment.KasbokarTabs;


import android.os.Bundle;
import androidx.transition.TransitionManager;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Adapter.HomeCategoriesAdapter;
import com.ideabonyan.iranapp.Fragment.Dialogs.CityPickerDialogFragment;
import com.ideabonyan.iranapp.Fragment.Dialogs.SubcategoriesDialogFragment;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.LocationChange;
import com.ideabonyan.iranapp.Models.HomeCategories;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
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
public class Needings extends Fragment implements Get_Insert_Edit_Data, LocationChange {


    public Needings() {
    }

    RecyclerView categoriesRV;
    ProgressBar progressBar;
    ViewGroup rootView;

    UserSessionManager userSessionManager;


    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_kasbokar_needings, container, false);
        userSessionManager = new UserSessionManager(getActivity());

        CityPickerDialogFragment.binddata(this);


        initializer();

        if (!userSessionManager.getCityInfo().equals("0")) {
            getData();
        }

        return view;
    }

    private void initializer() {
        categoriesRV = (RecyclerView) view.findViewById(R.id.homeCategoriesRV);
        progressBar = (ProgressBar) view.findViewById(R.id.homeProgressBar);
        rootView = (ViewGroup) view.findViewById(R.id.homeRoot);
    }


    public void getData() {
        String url = StaticData.All_CATEGORIES;
        Map<String, String> params = new HashMap<String, String>();
//        params.put("ads_type", "need");
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 1);
    }

    private void runRecyclerView() {
        TransitionManager.beginDelayedTransition(rootView);

        progressBar.setVisibility(View.GONE);
        categoriesRV.setVisibility(View.VISIBLE);

        categoriesRV.setNestedScrollingEnabled(false);
        categoriesRV.setFocusable(false);

        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        HomeCategoriesAdapter adapter = new HomeCategoriesAdapter(datas, getActivity());
        categoriesRV.setLayoutManager(layoutManager);
        categoriesRV.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        recyclerViewOnClickListener();
    }


    List<HomeCategories> datas;
    @Override
    public void on_volley_response(String response, int id) {


        if (id == 1) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                datas = HomeCategories.Categories(jsonObject);
                runRecyclerView();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void recyclerViewOnClickListener() {

        categoriesRV.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), categoriesRV, new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                SubcategoriesDialogFragment subcategoriesDialogFragment = new SubcategoriesDialogFragment();
                subcategoriesDialogFragment.setContext(getActivity());
                subcategoriesDialogFragment.setCategoryId(datas.get(position).getId());
                subcategoriesDialogFragment.setCategoryName(datas.get(position).getName());
//                subcategoriesDialogFragment.setAdType("need");
                subcategoriesDialogFragment.show(getActivity().getSupportFragmentManager(), "ProvincePickerFragment");
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
    public void onLocationChanged() {
//        getData();

        getActivity().recreate();
    }
}
