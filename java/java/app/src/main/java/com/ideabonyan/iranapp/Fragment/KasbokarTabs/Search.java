package com.ideabonyan.iranapp.Fragment.KasbokarTabs;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Activity.ShowAdActivity;
import com.ideabonyan.iranapp.Adapter.AdListAdapter;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data2;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data3;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data4;
import com.ideabonyan.iranapp.Interface.RemoveAd;
import com.ideabonyan.iranapp.Models.AdsToBeListed;
import com.ideabonyan.iranapp.Models.HomeSubCategories;
import com.ideabonyan.iranapp.Models.ProvicesAndCities;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back2;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back3;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back4;
import com.ideabonyan.iranapp.Utils.RecyclerItemClickListener;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Search extends Fragment implements RemoveAd, Get_Insert_Edit_Data, Get_Insert_Edit_Data4, Get_Insert_Edit_Data2 {


    public Search() {
    }

    View view;
    RelativeLayout searchBtn;
    EditText inputField;
    Spinner categorySPNR, subCategorySPNR, provinceSPNR, citySPNR, adtypeSPNR;
    RecyclerView rv;
    ViewGroup rootView;
    ProgressBar progressBar, spinnerLoaderProgressBar, loadMoreProgressbar;
    TextView warn, listEmptyText;
    NestedScrollView nestedScrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_kasbokar_search, container, false);

        offset = 0;
        isThereData = false;
        adData = null;

        initializer();
        spinnerStuff();

        smallStuff();
        spinnerItemListeners();
        onClicks();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

//        offset = 0;
//        isThereData = false;
//        adData = null;
//
//        initializer();
//        spinnerStuff();
//
//        smallStuff();
//        spinnerItemListeners();
//        onClicks();

    }

    private void smallStuff() {
        offset = 0;

        inputField.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        ShowAdActivity.bindData(this);

        volleyID3 = catID;
        spinnerDataUrl3 = StaticData.All_CATEGORIES;
        getSpinnerData3();

        volleyID4 = provinceID;
        spinnerDataUrl4 = StaticData.PROVINCE;
        getSpinnerData4();

        ////////////////////////
        //  DEACTIVATING SPINNERS

        categorySPNR.setEnabled(false);
        subCategorySPNR.setEnabled(false);
        provinceSPNR.setEnabled(false);
        citySPNR.setEnabled(false);
    }

    private void initializer() {
        searchBtn = (RelativeLayout) view.findViewById(R.id.kasbokarSearchBTN);
        inputField = (EditText) view.findViewById(R.id.kasbokarSearchEditText);
        categorySPNR = (Spinner) view.findViewById(R.id.kasbokarSearchCatSpinner);
        subCategorySPNR = (Spinner) view.findViewById(R.id.kasbokarSearchSubCatSpinner);
        provinceSPNR = (Spinner) view.findViewById(R.id.kasbokarSearchProvinceSpinner);
        citySPNR = (Spinner) view.findViewById(R.id.kasbokarSearchCitySpinner);
        adtypeSPNR = (Spinner) view.findViewById(R.id.kasbokarSearchAdTypeSpinner);
        rv = (RecyclerView) view.findViewById(R.id.kasbokarSearchRV);
        rootView = (ViewGroup) view.findViewById(R.id.kasbokarSearch);
        progressBar = (ProgressBar) view.findViewById(R.id.kasbokarSearchProgressBar);
        spinnerLoaderProgressBar = (ProgressBar) view.findViewById(R.id.kasbokarSearchSpinnerLoadProgressBar);
        loadMoreProgressbar = (ProgressBar) view.findViewById(R.id.kasbokarSearchLoadMoreProgressBar);
        warn = (TextView) view.findViewById(R.id.kasbokarSearchWarn);
        listEmptyText = (TextView) view.findViewById(R.id.kasbokarSearchEmptyListText);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.kasbokarSearchNestedScroll);
    }



    private void onClicks() {
        inputField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warn.setVisibility(View.INVISIBLE);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String term = null;
                try {
                    term = URLDecoder.decode(inputField.getText().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

//                if (term.equals("")){
//                    warn.setVisibility(View.VISIBLE);
//                }else {
                    offset = 0;
                    loadMore = true;
                    isThereData = false;
                    adData = null;

                    warn.setVisibility(View.INVISIBLE);

                    provinceText = "";
                    cityText = "&city_id=" + new UserSessionManager(getActivity()).getCityInfo();

                    spinnerDataUrl = StaticData.SEARCH + "?title=" + term + catText + subCatText + provinceText + cityText + adTypeText;// + "&offset=" + offset + "&limit=10";
                    rv.setVisibility(View.GONE);
                    listEmptyText.setVisibility(View.GONE);
                    getData();

//                    TransitionManager.beginDelayedTransition(rootView);
                    progressBar.setVisibility(View.VISIBLE);

                closeKeyboard();
//                }
            }
        });

        inputField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String term = null;
                    try {
                        term = URLDecoder.decode(inputField.getText().toString(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

//                if (term.equals("")){
//                    warn.setVisibility(View.VISIBLE);
//                }else {
                    offset = 0;
                    loadMore = true;
                    isThereData = false;
                    adData = null;

                    warn.setVisibility(View.INVISIBLE);

                    provinceText = "";
                    cityText = "&city_id=" + new UserSessionManager(getActivity()).getCityInfo();

                    spinnerDataUrl = StaticData.SEARCH + "?title=" + term + catText + subCatText + provinceText + cityText + adTypeText;// + "&offset=" + offset + "&limit=10";
                    rv.setVisibility(View.GONE);
                    listEmptyText.setVisibility(View.GONE);
                    getData();

//                    TransitionManager.beginDelayedTransition(rootView);
                    progressBar.setVisibility(View.VISIBLE);

                    closeKeyboard();
                    return true;
                }
                return false;
            }
        });
    }

    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    List<String> catList, subCatList, provinceList, cityList, adTypeList;
    String catText = "", subCatText = "", provinceText = "", cityText = "", adTypeText = "";
    List<HomeSubCategories> subCategoriesObjectList;
    List<HomeSubCategories> homeCategoriesObjectList;
    List<ProvicesAndCities> citiesObjectList;
    List<ProvicesAndCities> provinceObjectList;
    private void spinnerStuff() {
        catList = new ArrayList<>();
        subCatList = new ArrayList<>();
        provinceList = new ArrayList<>();
        cityList = new ArrayList<>();
        adTypeList = new ArrayList<>();

        catList.add("انتخاب کنید");
        subCatList.add("انتخاب کنید");
        provinceList.add("انتخاب کنید");
        cityList.add("انتخاب کنید");
        adTypeList.add("همه موارد");
        adTypeList.add("تخفیف دار ها");
//        adTypeList.add("بدون تخفیف ها");

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, catList);
        ArrayAdapter<String> subCatAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, subCatList);
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, provinceList);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, cityList);
        ArrayAdapter<String> adTypeAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, adTypeList);

        categorySPNR.setAdapter(catAdapter);
        subCategorySPNR.setAdapter(subCatAdapter);
        provinceSPNR.setAdapter(provinceAdapter);
        citySPNR.setAdapter(cityAdapter);
        adtypeSPNR.setAdapter(adTypeAdapter);
    }


    String provinceBeforeDeletion, catBeforeDeletion;
    private void spinnerItemListeners() {
        categorySPNR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {

//                    TransitionManager.beginDelayedTransition(rootView);

                    subCategorySPNR.setEnabled(false);

                    subCatList.clear();
                    subCatList.add("انتخاب کنید");
                    subCategorySPNR.setSelection(0);

                    catText = "";
                    catBeforeDeletion = catText;

                    if (position != 0) {
                        spinnerLoaderProgressBar.setVisibility(View.VISIBLE);
                        catText = "&category_id=" + homeCategoriesObjectList.get(position - 1).getId();
                        catBeforeDeletion = catText;

                        volleyID3 = subCatID;
                        spinnerDataUrl3 = StaticData.DOMAIN_WITH_API + "/categories/" + homeCategoriesObjectList.get(position - 1).getId() + "/subcategories/all";
                        getSpinnerData3();

                    }
                } catch (Exception e){e.printStackTrace();}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        provinceSPNR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {

//                    TransitionManager.beginDelayedTransition(rootView);

                    citySPNR.setEnabled(false);

                    cityList.clear();
                    cityList.add("انتخاب کنید");
                    citySPNR.setSelection(0);

                    provinceText = "";
                    provinceBeforeDeletion = provinceText;

                    if (position != 0) {
                        spinnerLoaderProgressBar.setVisibility(View.VISIBLE);
                        provinceText = "&province_id=" + provinceObjectList.get(position - 1).getId();
                        provinceBeforeDeletion = provinceText;

                        volleyID4 = cityID;
                        spinnerDataUrl4 = StaticData.DOMAIN_WITH_API + "/provinces/" + provinceObjectList.get(position - 1).getId() + "/cities";
                        getSpinnerData4();
                    }
                } catch (Exception e){e.printStackTrace();}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subCategorySPNR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    if (position != 0) {
//                        subCatText = "&subcategory_id=" + subCategoriesObjectList.get(position - 1).getId();
                        subCatText =""+position;
                        catText = "";
                    } else {
                        catText = catBeforeDeletion;
                        subCatText = "";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        citySPNR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position != 0) {
                        cityText = "&city_id=" + citiesObjectList.get(position - 1).getId();
                        provinceText = "";
                    } else {
                        provinceText = provinceBeforeDeletion;
                    }
                } catch (Exception e){e.printStackTrace();}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adtypeSPNR.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position == 0) {
                        adTypeText = "";
                    } else if (position == 1) {
                        adTypeText = "&type=discount";
                    } else {
                        adTypeText = "&type=need";
                    }
                } catch (Exception e){e.printStackTrace();}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    int catID = 1, subCatID = 2, provinceID = 3, cityID = 4, searchResultID = 5;

    String spinnerDataUrl3;
    int volleyID3;
    private void getSpinnerData3() {
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back4.binddata(this);
        Get_Volley_Call_Back4.Call_Volley(getActivity(), params, spinnerDataUrl3, Request.Method.GET, volleyID3);
    }
    String spinnerDataUrl4;
    int volleyID4;
    private void getSpinnerData4() {
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back4.binddata(this);
        Get_Volley_Call_Back4.Call_Volley(getActivity(), params, spinnerDataUrl4, Request.Method.GET, volleyID4);
    }


    String spinnerDataUrl;
    int volleyID;
    int offset = 0;
    boolean loadMore = true;
    boolean isLoadingNow = false;
    boolean isThereData;


    private void getData() {
        String url = StaticData.SEARCH ;
        Map<String, String> params = new HashMap<String, String>();
        params.put("offset",offset+"");
        params.put("limit","10");
        if (!inputField.getText().toString().equals("")) {
            params.put("title", inputField.getText().toString());
        }
        if (subCategorySPNR.getSelectedItemPosition()!=0){

            params.put("subcategory_id",subCategoriesObjectList.get(subCategorySPNR.getSelectedItemPosition()- 1).getId());

        }else if (categorySPNR.getSelectedItemPosition()!=0){
            params.put("category_id",homeCategoriesObjectList.get(categorySPNR.getSelectedItemPosition()- 1).getId());

        }
        params.put("city_id",new UserSessionManager(getActivity()).getCityInfo());

        if (adtypeSPNR.getSelectedItemPosition() == 1) {
            params.put("type","discount");
        }

        Get_Volley_Call_Back4.binddata(this);
        Get_Volley_Call_Back4.Call_Volley(getActivity(), params, url, Request.Method.POST, searchResultID);
    }

    @Override
    public void on_volley_response(String response, int id) {

        if (id == catID){
//            TransitionManager.beginDelayedTransition(rootView);
            spinnerLoaderProgressBar.setVisibility(View.INVISIBLE);

            try {
                JSONObject jsonObject = new JSONObject(response);
//                JSONArray jsonArray = jsonObject.getJSONArray("list");
//                for (int i = 0; i < jsonArray.length(); i++){
//                    JSONObject json = jsonArray.getJSONObject(i);
//                    catList.add(json.getString("name"));
//                }
                homeCategoriesObjectList = HomeSubCategories.Categories(jsonObject);
                for (HomeSubCategories cat : homeCategoriesObjectList){
                    catList.add(cat.getName());
                }
                categorySPNR.setEnabled(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        else if (id == provinceID){

            try {
                JSONObject jsonObject = new JSONObject(response);
//                JSONArray jsonArray = jsonObject.getJSONArray("list");
//                for (int i = 0; i < jsonArray.length(); i++){
//                    JSONObject json = jsonArray.getJSONObject(i);
//                    provinceList.add(json.getString("name"));
//                }

                provinceObjectList = ProvicesAndCities.cities(jsonObject);
                for (ProvicesAndCities city : provinceObjectList) {
                    provinceList.add(city.getName());
                }
                provinceSPNR.setEnabled(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        else if (id == subCatID){
//            TransitionManager.beginDelayedTransition(rootView);
            spinnerLoaderProgressBar.setVisibility(View.INVISIBLE);

            try {
                JSONObject jsonObject = new JSONObject(response);
                subCategoriesObjectList = HomeSubCategories.Categories(jsonObject);
                for (HomeSubCategories sub : subCategoriesObjectList){
                    subCatList.add(sub.getName());
                }
                subCategorySPNR.setEnabled(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        else if (id == cityID){
//            TransitionManager.beginDelayedTransition(rootView);
            spinnerLoaderProgressBar.setVisibility(View.INVISIBLE);

            try {
                JSONObject jsonObject = new JSONObject(response);
                citiesObjectList = ProvicesAndCities.cities(jsonObject);
                for (ProvicesAndCities city : citiesObjectList){
                    cityList.add(city.getName());
                }
                citySPNR.setEnabled(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        else if (id == searchResultID){
            try {
                JSONObject jsonObject = new JSONObject(response);
//                Log.i("111111111", response);
                if (adData == null) {
                    adData = AdsToBeListed.Import(jsonObject);
                    progressBar.setVisibility(View.GONE);
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

    List<AdsToBeListed> adData;
    AdListAdapter adapter;
    LinearLayoutManager layoutManager;
    private void runRv() {

        if (adData.size()<=10){
            adapter = new AdListAdapter(adData, getActivity());
             layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);

        }

//        if (!isThereData) TransitionManager.beginDelayedTransition(rootView);


        //TODO enable these when searching
//        rv.setVisibility(View.VISIBLE);
//        rv.setVisibility(View.GONE);



        if (adData.size() > 0) {


            offset += 10;


            if (!isThereData) {
                isThereData = true;
                rv.setLayoutManager(layoutManager);
                rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                rv.setVisibility(View.VISIBLE);
            } else {
                adapter.notifyDataSetChanged();
            }


            rv.setNestedScrollingEnabled(false);


            recyclerViewOnClickListener();

            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {

                        if (!isLoadingNow) {


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

        } else {
//            TransitionManager.beginDelayedTransition(rootView);
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
    public void on_volley_error(VolleyError error, int id) {

//        Log.i("111111", error+"");
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
