package com.ideabonyan.iranapp.Fragment.Dialogs;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.Select_Job_Filter;
import com.ideabonyan.iranapp.Models.ProvicesAndCities;
import com.ideabonyan.iranapp.Models.Specialities;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.ShowToast;
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
@SuppressLint("ValidFragment")
public class Select_Job_Search_Filter extends DialogFragment implements Get_Insert_Edit_Data {
    View view;
    LinearLayout lin_show_location, lin_show_agreement_type, lin_show_speciality, lin_show_education_level, lin_show_rezome;
    LinearLayout lin_location_visibility, lin_agreement_type_visibiliy, lin_speciality_visibiliy, lin_education_level_visibiliy,
            lin_rezome_visibility;
    Spinner spin_rezome, spin_education_level, spin_speciality, spin_agreement_type;
    List<Specialities> specialitiesList;

    LinearLayout lin_delete_filter;
    Spinner spin_city, spin_province;
    List<String> list_agremment_type, list_specialty, list_ducation_level, list_type;
    List<String> province, city,region_list;
    ProgressBar ProgressBar;
    List<ProvicesAndCities> provinces, citys,region;
    LinearLayout lin_submit;
    String type = "", education_level = "", speciality_id = "", agreement_type = "", city_id = "", parent_id = "", region_id = "", order_bye = "";
    public static Select_Job_Filter mySelect_job_filter;
    Spinner spin_region;
    String type1;

    public Select_Job_Search_Filter(String type) {
        // Required empty public constructor
        this.type1=type;
    }

    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_select__job__search__filter, container, false);
        holder();
        onclick();
        set_spinner_data();
        on_spiner_change_item();
        get_all_specialty();
        get_all_province();
        setdatafirst();

        return view;
    }

    private void holder() {
        lin_show_rezome = (LinearLayout) view.findViewById(R.id.lin_show_rezome);
        lin_show_education_level = (LinearLayout) view.findViewById(R.id.lin_show_education_level);
        lin_show_speciality = (LinearLayout) view.findViewById(R.id.lin_show_speciality);
        lin_show_location = (LinearLayout) view.findViewById(R.id.lin_show_location);
        lin_show_agreement_type = (LinearLayout) view.findViewById(R.id.lin_show_agreement_type);

        lin_rezome_visibility = (LinearLayout) view.findViewById(R.id.lin_rezome_visibility);
        lin_education_level_visibiliy = (LinearLayout) view.findViewById(R.id.lin_education_level_visibiliy);
        lin_speciality_visibiliy = (LinearLayout) view.findViewById(R.id.lin_speciality_visibiliy);
        lin_agreement_type_visibiliy = (LinearLayout) view.findViewById(R.id.lin_agreement_type_visibiliy);
        lin_location_visibility = (LinearLayout) view.findViewById(R.id.lin_location_visibility);

        lin_delete_filter = (LinearLayout) view.findViewById(R.id.lin_delete_filter);

        spin_agreement_type = (Spinner) view.findViewById(R.id.spin_agreement_type);
        spin_speciality = (Spinner) view.findViewById(R.id.spin_speciality);
        spin_education_level = (Spinner) view.findViewById(R.id.spin_education_level);
        spin_rezome = (Spinner) view.findViewById(R.id.spin_rezome);

        spin_province = (Spinner) view.findViewById(R.id.spin_province);
        spin_city = (Spinner) view.findViewById(R.id.spin_city);
        spin_region= (Spinner) view.findViewById(R.id.spin_region);


        ProgressBar = (android.widget.ProgressBar) view.findViewById(R.id.ProgressBar);

        lin_submit = (LinearLayout) view.findViewById(R.id.lin_submit);
        lin_delete_filter = (LinearLayout) view.findViewById(R.id.lin_delete_filter);
        lin_delete_filter.setEnabled(true);
        specialitiesList = new ArrayList<>();
    }
    private void setdatafirst(){
//        Log.v("cat",type1);
        if (!type1.equals("")){
            lin_rezome_visibility.setVisibility(View.VISIBLE);
            if (type1.equals("forsatshoghli")){
                spin_rezome.setSelection(1);
            }else{
                spin_rezome.setSelection(2);
            }
        }

    }

    private void get_all_specialty() {
        ProgressBar.setVisibility(View.VISIBLE);
        String url = StaticData.employs + "/specialities";
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(Select_Job_Search_Filter.this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 136);
    }

    private void onclick() {
        lin_show_rezome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                if (lin_rezome_visibility.getVisibility() == View.GONE) {
                    lin_rezome_visibility.setVisibility(View.VISIBLE);
                } else {
                    lin_rezome_visibility.setVisibility(View.GONE);

                }

            }
        });
        lin_show_education_level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                if (lin_education_level_visibiliy.getVisibility() == View.GONE) {
                    lin_education_level_visibiliy.setVisibility(View.VISIBLE);
                } else {
                    lin_education_level_visibiliy.setVisibility(View.GONE);

                }

            }
        });
        lin_show_speciality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lin_speciality_visibiliy.getVisibility() == View.GONE) {
                    lin_speciality_visibiliy.setVisibility(View.VISIBLE);
                } else {
                    lin_speciality_visibiliy.setVisibility(View.GONE);

                }
            }
        });


        lin_show_agreement_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lin_agreement_type_visibiliy.getVisibility() == View.GONE) {
                    lin_agreement_type_visibiliy.setVisibility(View.VISIBLE);
                } else {
                    lin_agreement_type_visibiliy.setVisibility(View.GONE);

                }
            }
        });


        lin_show_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lin_location_visibility.getVisibility() == View.GONE) {
                    lin_location_visibility.setVisibility(View.VISIBLE);
                } else {
                    lin_location_visibility.setVisibility(View.GONE);

                }
            }
        });

        lin_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (valid()) {
                    setdata();
//                }
////                mySelect_car_filter.on_filter_set();
            }
        });

        lin_delete_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "";
                education_level = "";
                speciality_id = "";
                agreement_type = "";
                city_id = "";
                parent_id = "";
                region_id = "";
                order_bye = "";
                mySelect_job_filter.on_filter_set(type,education_level,speciality_id,agreement_type,city_id,parent_id,order_bye,region_id);

                Select_Job_Search_Filter.this.dismiss();

            }
        });
    }

    private void set_spinner_data() {
        list_type = new ArrayList<>();
        list_ducation_level = new ArrayList<>();
        list_specialty = new ArrayList<>();
        list_agremment_type = new ArrayList<>();
        region_list = new ArrayList<>();

        province = new ArrayList<>();
        city = new ArrayList<>();

        list_type.add("همه");
        list_type.add("استخدام");
        list_type.add("آماده به کار");

        list_ducation_level.add("همه");
        list_ducation_level.add("زیر دیپلم");
        list_ducation_level.add("دیپلم");
        list_ducation_level.add("کاردانی");
        list_ducation_level.add("کارشناسی");
        list_ducation_level.add("کارشناسی ارشد");
        list_ducation_level.add("دکترا");


        list_specialty.add("همه");
        list_agremment_type.add("همه");
        list_agremment_type.add("تمام وقت");
        list_agremment_type.add("پاره وقت");
        list_agremment_type.add("مشاوره ای");
        list_agremment_type.add("پروژه ای");

        province.add("همه");
        city.add("همه");
        region_list.add("همه");


        ArrayAdapter<String> type_Adapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, list_type);
        ArrayAdapter<String> ducation_level_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, list_ducation_level);
        ArrayAdapter<String> specialty_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, list_specialty);
        ArrayAdapter<String> agremment_type_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, list_agremment_type);

        ArrayAdapter<String> province_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, province);
        ArrayAdapter<String> city_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, city);
        ArrayAdapter<String> region_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, region_list);

        spin_rezome.setAdapter(type_Adapter);
        spin_education_level.setAdapter(ducation_level_addapter);
        spin_speciality.setAdapter(specialty_addapter);
        spin_agreement_type.setAdapter(agremment_type_addapter);

        spin_province.setAdapter(province_addapter);
        spin_city.setAdapter(city_addapter);
        spin_region.setAdapter(region_addapter);

    }

    private void on_spiner_change_item() {
        spin_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position != 0) {
                    get_all_city(provinces.get(position - 1).getId());
                } else {
                    city = new ArrayList<>();
                    city.add("همه");
                    ArrayAdapter<String> spin_city1 = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, city);
                    spin_city.setAdapter(spin_city1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        spin_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position != 0) {
                    get_all_region(citys.get(position - 1).getId());
                } else {
                    if (region_list.size()>1){
                        region_list = new ArrayList<>();
                        region_list.add("همه");
                        ArrayAdapter<String> region_adapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, region_list);
                        spin_region.setAdapter(region_adapter);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });



    }

    private void get_all_region(String id) {
        ProgressBar.setVisibility(View.VISIBLE);

        String url = StaticData.get_all_region + id + "/regions";

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Select_Job_Search_Filter.this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 130);
    }

    private void get_all_province() {
        ProgressBar.setVisibility(View.VISIBLE);

        String provinceUrl = StaticData.PROVINCE;
        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Select_Job_Search_Filter.this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, provinceUrl, Request.Method.GET, 128);
    }

    private void get_all_city(String id) {
        ProgressBar.setVisibility(View.VISIBLE);

        String url = StaticData.DOMAIN_WITH_API + "/provinces/" + id + "/cities";

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Select_Job_Search_Filter.this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 129);
    }


    @Override
    public void on_volley_response(String response, int id) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");
            if (id == 128) {
                if (status.equals("200")) {
                    provinces = ProvicesAndCities.cities(jsonObject);
                    set_spin_province();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", getActivity());
                    Select_Job_Search_Filter.this.dismiss();

                }
                ProgressBar.setVisibility(View.GONE);

            }
            if (id == 130) {
                if (status.equals("200")) {
                    region = ProvicesAndCities.cities(jsonObject);
                    set_spin_region();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", getActivity());
                    Select_Job_Search_Filter.this.dismiss();

                }
                ProgressBar.setVisibility(View.GONE);

            }

            if (id == 136) {
                if (status.equals("200")) {
                    specialitiesList = Specialities.Import(jsonObject.getJSONArray("list"));
                    set_spin_specialis();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", getActivity());
                    Select_Job_Search_Filter.this.dismiss();


                }
                ProgressBar.setVisibility(View.GONE);

            }
            if (id == 129) {
                if (status.equals("200")) {
                    citys = ProvicesAndCities.cities(jsonObject);
                    set_spin_citys();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", getActivity());
                    Select_Job_Search_Filter.this.dismiss();

                }
                ProgressBar.setVisibility(View.GONE);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void set_spin_province() {
        province = new ArrayList<>();
        province.add("همه");
        for (int i = 0; i < provinces.size(); i++) {
            province.add(provinces.get(i).getName());
        }
        ArrayAdapter<String> spin_provinces = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, province);
        spin_province.setAdapter(spin_provinces);

    }
    private void set_spin_region() {
        region_list = new ArrayList<>();
        region_list.add("همه");
        for (int i = 0; i < region.size(); i++) {
            region_list.add(region.get(i).getName());
        }
        ArrayAdapter<String> spin_region1 = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, region_list);
        spin_region.setAdapter(spin_region1);

    }


    private void set_spin_citys() {
        city = new ArrayList<>();
        city.add("همه");
        for (int i = 0; i < citys.size(); i++) {
            city.add(citys.get(i).getName());
        }
        ArrayAdapter<String> spin_city1 = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, city);
        spin_city.setAdapter(spin_city1);

    }


    public static void binddata(Select_Job_Filter select_job_filter) {
        mySelect_job_filter = select_job_filter;
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }


    private void set_spin_specialis() {
        list_specialty = new ArrayList<>();
        list_specialty.add("همه");
        for (int i = 0; i < specialitiesList.size(); i++) {
            list_specialty.add(specialitiesList.get(i).getName());
        }
        ArrayAdapter<String> specialty_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, list_specialty);
        spin_speciality.setAdapter(specialty_addapter);

    }


    private void setdata() {
        type = "";
        education_level = "";
        speciality_id = "";
        agreement_type = "";
        city_id = "";
        parent_id = "";
        if (spin_rezome.getSelectedItemPosition() != 0) {
            if (spin_rezome.getSelectedItemPosition() == 1) {
                type = "forsatshoghli";
            } else {
                type = "karjoo";

            }
        }

        if (spin_education_level.getSelectedItemPosition() != 0) {
            if (spin_education_level.getSelectedItemPosition() == 1) {
                education_level = "underdiploma";

            } else if (spin_education_level.getSelectedItemPosition() == 2) {
                education_level = "diploma";

            } else if (spin_education_level.getSelectedItemPosition() == 3) {
                education_level = "tact";

            } else if (spin_education_level.getSelectedItemPosition() == 4) {
                education_level = "expertise";

            } else if (spin_education_level.getSelectedItemPosition() == 5) {
                education_level = "masterdegree";

            } else if (spin_education_level.getSelectedItemPosition() == 6) {
                education_level = "doctoral";
            }
        }else{
            education_level = "";

        }
        if (spin_speciality.getSelectedItemPosition() != 0) {
            speciality_id = specialitiesList.get(spin_speciality.getSelectedItemPosition() - 1).getId();
        }else{
            speciality_id = "";

        }
        if (spin_agreement_type.getSelectedItemPosition() != 0) {
            if (spin_agreement_type.getSelectedItemPosition() == 1) {
                agreement_type = "tamamvaght";
            } else if (spin_agreement_type.getSelectedItemPosition() == 2) {
                agreement_type = "parevaght";

            } else if (spin_agreement_type.getSelectedItemPosition() == 3) {
                agreement_type = "moshaveri";

            } else if (spin_agreement_type.getSelectedItemPosition() == 4) {
                agreement_type = "projei";

            }
        }else{
            agreement_type = "";

        }
        if (spin_region.getSelectedItemPosition()!=0){
            region_id=region.get(spin_region.getSelectedItemPosition()-1).getId();
        }else if (spin_province.getSelectedItemPosition()!=0){
            if (spin_city.getSelectedItemPosition()!=0){
                city_id=citys.get(spin_city.getSelectedItemPosition()-1).getId();
            }else{
                parent_id=provinces.get(spin_province.getSelectedItemPosition()-1).getId();
            }
        }else{
            parent_id="0";
            city_id="0";
        }

        mySelect_job_filter.on_filter_set(type,education_level,speciality_id,agreement_type,city_id,parent_id,order_bye,region_id);
        Select_Job_Search_Filter.this.dismiss();
    }

}
