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
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Adapter.HomeCategoriesAdapter;
import com.ideabonyan.iranapp.Fragment.Dialogs.CityPickerDialogFragment;
import com.ideabonyan.iranapp.Fragment.Dialogs.SubcategoriesDialogFragment;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data3;
import com.ideabonyan.iranapp.Interface.LocationChange;
import com.ideabonyan.iranapp.Models.HomeCategories;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back3;
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
public class WithDiscounts extends Fragment implements Get_Insert_Edit_Data3, LocationChange {


    public WithDiscounts() {
    }


    RecyclerView categoriesRV;
    ProgressBar progressBar;
    ViewGroup rootView;
    RelativeLayout listEmptyText, rvArea;

    UserSessionManager userSessionManager;
    int time=0;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_kasbokar_discounts, container, false);
        userSessionManager = new UserSessionManager(getActivity());

        CityPickerDialogFragment.binddata(this);


        initializer();

        if (!userSessionManager.getCityInfo().equals("0")) {
            getData();
        }

        return view;
    }


    private void initializer() {
        categoriesRV = (RecyclerView) view.findViewById(R.id.kasbokarDiscountsRV);
        progressBar = (ProgressBar) view.findViewById(R.id.kasbokarDiscountsProgressBar);
        rootView = (ViewGroup) view.findViewById(R.id.kasbokarDiscounts);
        listEmptyText = (RelativeLayout) view.findViewById(R.id.kasbokarDiscountsListEmptyDialog);
        rvArea = (RelativeLayout) view.findViewById(R.id.kasbokarDiscountsRvArea);
    }


    public void getData() {
        String url = StaticData.DOMAIN_WITH_API +
                "/city/" + new UserSessionManager(getActivity()).getCityInfo() + "/ads/category";
        Map<String, String> params = new HashMap<String, String>();
//        params.put("ads_type", " discount");
        Get_Volley_Call_Back3.binddata(this);
        Get_Volley_Call_Back3.Call_Volley(getActivity(), params, url, Request.Method.GET, 1);
    }

    private void runRecyclerView() {
        TransitionManager.beginDelayedTransition(rootView);

        if (datas.size() > 0) {

            progressBar.setVisibility(View.GONE);
            rvArea.setVisibility(View.VISIBLE);
//            categoriesRV.setVisibility(View.VISIBLE);

            categoriesRV.setNestedScrollingEnabled(false);
            categoriesRV.setFocusable(false);

            LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
            HomeCategoriesAdapter adapter = new HomeCategoriesAdapter(datas, getActivity());
            categoriesRV.setLayoutManager(layoutManager);
            categoriesRV.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            recyclerViewOnClickListener();
        }
        else {
            progressBar.setVisibility(View.GONE);
            listEmptyText.setVisibility(View.VISIBLE);
        }
    }


    private void recyclerViewOnClickListener() {

        categoriesRV.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), categoriesRV, new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                SubcategoriesDialogFragment subcategoriesDialogFragment = new SubcategoriesDialogFragment();
                subcategoriesDialogFragment.setContext(getActivity());
                subcategoriesDialogFragment.setCategoryId(datas.get(position).getId());
//                subcategoriesDialogFragment.setAdType("discount");
                subcategoriesDialogFragment.show(getActivity().getSupportFragmentManager(), "ProvincePickerFragment");
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

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

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }

    @Override
    public void onLocationChanged() {
//        getData();
        getActivity().recreate();
    }
}
