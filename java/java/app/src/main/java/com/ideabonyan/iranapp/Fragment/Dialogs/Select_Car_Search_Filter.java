package com.ideabonyan.iranapp.Fragment.Dialogs;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.Select_Car_Filter;
import com.ideabonyan.iranapp.Models.Brand;
import com.ideabonyan.iranapp.Models.Modell;
import com.ideabonyan.iranapp.Models.ProvicesAndCities;
import com.ideabonyan.iranapp.Models.cylinder_volumes;
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
public class Select_Car_Search_Filter extends DialogFragment implements Get_Insert_Edit_Data {
    public static Select_Car_Filter mySelect_car_filter;
    View view;
    CardView card_brand, card_weghit, card_chasi, card_year, card_cost, card_kilometer;
    LinearLayout lin_show_cat, lin_show_chasi, lin_show_year, lin_show_cost, lin_show_kilometer, lin_show_location,
            lin_show_weight, lin_show_brand, lin_delete_filter;
    LinearLayout lin_cat_visibility, lin_chasi_visibiliy, lin_year_visibility, lin_cost_visibility, lin_location_visibility,
            lin_kilometer_visibility, lin_weight_visibiliy, lin_bran_visibiliy;
    LinearLayout lin_modell, lin_brand;
    Spinner spin_modell, spin_brand, spin_cat;
    Spinner spin_chasi;
    Spinner spin_motor_weghit;
    EditText edt_year_to, edt_year_from;
    EditText edt_cost_to, edt_cost_from;
    EditText edt_kilometr_to, edt_kilomter_from;
    Spinner spin_city, spin_province;
    List<String> cat, moddel, brand, shasi, motore_weghit;
    List<String> province, city, region_list;
    ProgressBar ProgressBar;
    List<Brand> brands;
    List<Modell> modells;
    List<cylinder_volumes> cylinder_volumes;
    List<ProvicesAndCities> provinces, citys, region;
    LinearLayout lin_submit;
    String cat_name;
    int brand_id, model_id;
    String chasi_type, tolid_from, tolid_to, cost_from, cost_to, kilometer_from, kilometer_to, region_id, order_bye;
    int motor_weghit;
    int province_id;
    int city_id;
    Spinner spin_region;
    CardView card_newst, card_lowest, card_highest;
    CardView card_newst1, card_lowest1, card_highest1;
    LinearLayout lin_highest, lin_lowest, lin_news;
    TextView txt_newst, txt_lowest, txt_highest;
    String type;
    int statous = 0;

    public Select_Car_Search_Filter(String type) {
        // Required empty public constructor
        this.type = type;
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
        view = inflater.inflate(R.layout.fragment_select__car__search__filter, container, false);
        holder();
        onclick();
        set_spinner_data();
        on_spiner_change_item();
        get_all_brand();
        get_all_province();
        get_all_cilander_volum();
        bindspinner();
        return view;
    }

    private void holder() {
        txt_highest = view.findViewById(R.id.txt_highest);
        txt_lowest = view.findViewById(R.id.txt_lowest);
        txt_newst = view.findViewById(R.id.txt_newst);

        lin_show_kilometer = (LinearLayout) view.findViewById(R.id.lin_show_kilometer);
        lin_show_cost = (LinearLayout) view.findViewById(R.id.lin_show_cost);
        lin_show_year = (LinearLayout) view.findViewById(R.id.lin_show_year);
        lin_show_chasi = (LinearLayout) view.findViewById(R.id.lin_show_chasi);
        lin_show_cat = (LinearLayout) view.findViewById(R.id.lin_show_cat);
        lin_show_location = (LinearLayout) view.findViewById(R.id.lin_show_location);
        lin_show_weight = (LinearLayout) view.findViewById(R.id.lin_show_weight);
        lin_show_brand = (LinearLayout) view.findViewById(R.id.lin_show_brand);

        lin_cost_visibility = (LinearLayout) view.findViewById(R.id.lin_cost_visibility);
        lin_year_visibility = (LinearLayout) view.findViewById(R.id.lin_year_visibility);
        lin_chasi_visibiliy = (LinearLayout) view.findViewById(R.id.lin_chasi_visibiliy);
        lin_cat_visibility = (LinearLayout) view.findViewById(R.id.lin_cat_visibility);
        lin_location_visibility = (LinearLayout) view.findViewById(R.id.lin_location_visibility);
        lin_kilometer_visibility = (LinearLayout) view.findViewById(R.id.lin_kilometer_visibility);
        lin_weight_visibiliy = (LinearLayout) view.findViewById(R.id.lin_weight_visibiliy);
        lin_bran_visibiliy = (LinearLayout) view.findViewById(R.id.lin_bran_visibiliy);

        lin_news = (LinearLayout) view.findViewById(R.id.lin_news);
        lin_lowest = (LinearLayout) view.findViewById(R.id.lin_lowest);
        lin_highest = (LinearLayout) view.findViewById(R.id.lin_highest);

        lin_modell = (LinearLayout) view.findViewById(R.id.lin_modell);
        lin_brand = (LinearLayout) view.findViewById(R.id.lin_brand);

        lin_delete_filter = (LinearLayout) view.findViewById(R.id.lin_delete_filter);

        spin_cat = (Spinner) view.findViewById(R.id.spin_cat);
        spin_brand = (Spinner) view.findViewById(R.id.spin_brand);
        spin_modell = (Spinner) view.findViewById(R.id.spin_modell);
        spin_region = view.findViewById(R.id.spin_region);

        spin_chasi = (Spinner) view.findViewById(R.id.spin_chasi);

        spin_motor_weghit = (Spinner) view.findViewById(R.id.spin_motor_weghit);

        spin_province = (Spinner) view.findViewById(R.id.spin_province);
        spin_city = (Spinner) view.findViewById(R.id.spin_city);

        edt_kilometr_to = (EditText) view.findViewById(R.id.edt_kilometr_to);
        edt_cost_from = (EditText) view.findViewById(R.id.edt_cost_from);
        edt_cost_to = (EditText) view.findViewById(R.id.edt_cost_to);
        edt_year_from = (EditText) view.findViewById(R.id.edt_year_from);
        edt_year_to = (EditText) view.findViewById(R.id.edt_year_to);
        edt_kilomter_from = (EditText) view.findViewById(R.id.edt_kilomter_from);

        card_brand = (CardView) view.findViewById(R.id.card_brand);
        card_weghit = (CardView) view.findViewById(R.id.card_weghit);
        card_chasi = (CardView) view.findViewById(R.id.card_chasi);
        card_year = (CardView) view.findViewById(R.id.card_year);
        card_cost = (CardView) view.findViewById(R.id.card_cost);
        card_kilometer = (CardView) view.findViewById(R.id.card_kilometer);

        card_highest = (CardView) view.findViewById(R.id.card_highest);
        card_lowest = (CardView) view.findViewById(R.id.card_lowest);
        card_newst = (CardView) view.findViewById(R.id.card_newst);

        card_highest1 = (CardView) view.findViewById(R.id.card_highest1);
        card_lowest1 = (CardView) view.findViewById(R.id.card_lowest1);
        card_newst1 = (CardView) view.findViewById(R.id.card_newst1);

        ProgressBar = (android.widget.ProgressBar) view.findViewById(R.id.ProgressBar);

        lin_submit = (LinearLayout) view.findViewById(R.id.lin_submit);
        lin_delete_filter.setEnabled(true);

        statous = 0;
//        card_newst1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//        txt_newst.setTextColor(Color.parseColor("#ffffff"));

    }

    private void onclick() {
        lin_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_scale(3);

            }
        });
        lin_lowest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_scale(2);

            }
        });
        lin_highest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear_scale(1);

            }
        });


        lin_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valid()) {
                    setdata();
                }
//                mySelect_car_filter.on_filter_set();
            }
        });
        lin_show_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lin_weight_visibiliy.getVisibility() == View.GONE) {
                    lin_weight_visibiliy.setVisibility(View.VISIBLE);
                } else {
                    lin_weight_visibiliy.setVisibility(View.GONE);

                }
            }
        });


        lin_show_brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lin_bran_visibiliy.getVisibility() == View.GONE) {
                    lin_bran_visibiliy.setVisibility(View.VISIBLE);
                } else {
                    lin_bran_visibiliy.setVisibility(View.GONE);

                }
            }
        });

        lin_show_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lin_cat_visibility.getVisibility() == View.GONE) {
                    lin_cat_visibility.setVisibility(View.VISIBLE);
                } else {
                    lin_cat_visibility.setVisibility(View.GONE);
                }
            }
        });


        lin_show_chasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lin_chasi_visibiliy.getVisibility() == View.GONE) {
                    lin_chasi_visibiliy.setVisibility(View.VISIBLE);
                } else {
                    lin_chasi_visibiliy.setVisibility(View.GONE);

                }
            }
        });
        lin_show_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lin_year_visibility.getVisibility() == View.GONE) {
                    lin_year_visibility.setVisibility(View.VISIBLE);
                } else {
                    lin_year_visibility.setVisibility(View.GONE);

                }
            }
        });
        lin_show_cost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lin_cost_visibility.getVisibility() == View.GONE) {
                    lin_cost_visibility.setVisibility(View.VISIBLE);
                } else {
                    lin_cost_visibility.setVisibility(View.GONE);

                }
            }
        });
        lin_show_kilometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lin_kilometer_visibility.getVisibility() == View.GONE) {
                    lin_kilometer_visibility.setVisibility(View.VISIBLE);
                } else {
                    lin_kilometer_visibility.setVisibility(View.GONE);

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

        lin_delete_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cat_name = "";
                brand_id = 0;
                model_id = 0;
                chasi_type = "";
                tolid_from = "";
                tolid_to = "";
                cost_from = "";
                cost_to = "";
                kilometer_from = "";
                kilometer_to = "";
                motor_weghit = 0;
                province_id = 0;
                city_id = 0;
                order_bye = "";
                region_id = "";
                mySelect_car_filter.on_filter_set(cat_name, brand_id, model_id, chasi_type, tolid_from
                        , tolid_to, cost_from, cost_to, kilometer_from, kilometer_to, motor_weghit
                        , province_id, city_id, order_bye, region_id);

                Select_Car_Search_Filter.this.dismiss();
            }
        });
    }

    private void set_spinner_data() {
        cat = new ArrayList<>();
        moddel = new ArrayList<>();
        brand = new ArrayList<>();
        shasi = new ArrayList<>();
        motore_weghit = new ArrayList<>();
        region_list = new ArrayList<>();

        province = new ArrayList<>();
        city = new ArrayList<>();
        region = new ArrayList<>();

        cat.add("همه");
        cat.add("خودرو");
        cat.add("موتور سیکلت");
        cat.add("خودرو کلاسیک");
        cat.add("خودرو سنگین و نیمه سنگین");
        cat.add("لوازم و وسایل نقلیه");
        cat.add("سایر وسایل نقلیه");

        shasi.add("همه");
        shasi.add("سواری");
        shasi.add("هاچ بک");
        shasi.add("شاسی بلند");
        shasi.add("وانت");
        shasi.add("کروک");
        shasi.add("ون");
        shasi.add("کوپه");
        shasi.add("استیشن");
        shasi.add("دیگر");


        moddel.add("همه");
        brand.add("همه");
        motore_weghit.add("همه");

        province.add("همه");
        city.add("همه");
        region_list.add("همه");


        ArrayAdapter<String> cat_Adapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, cat);
        ArrayAdapter<String> moddel_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, moddel);
        ArrayAdapter<String> brand_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, brand);
        ArrayAdapter<String> shasi_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, shasi);
        ArrayAdapter<String> motoer_weghit = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, motore_weghit);

        ArrayAdapter<String> province_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, province);
        ArrayAdapter<String> city_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, city);
        ArrayAdapter<String> region_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, region_list);

        spin_cat.setAdapter(cat_Adapter);
        spin_modell.setAdapter(moddel_addapter);
        spin_chasi.setAdapter(shasi_addapter);
        spin_brand.setAdapter(brand_addapter);
        spin_motor_weghit.setAdapter(motoer_weghit);

        spin_province.setAdapter(province_addapter);
        spin_city.setAdapter(city_addapter);
        spin_region.setAdapter(region_addapter);

    }

    private void bindspinner() {
        if (type.equals("other")) {
            spin_cat.setSelection(6);
        } else if (type.equals("khodro")) {
            spin_cat.setSelection(1);

        } else if (type.equals("motorcycle")) {
            spin_cat.setSelection(2);

        } else if (type.equals("khodroclasic")) {
            spin_cat.setSelection(3);

        } else if (type.equals("khordrosorn")) {
            spin_cat.setSelection(4);

        } else if (type.equals("lavazem")) {
            spin_cat.setSelection(5);

        } else {
            lin_cat_visibility.setVisibility(View.GONE);
        }
    }

    private void on_spiner_change_item() {
        spin_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position > 0) {
                    get_all_modell(brands.get(position - 1).getId());
                } else {
                    lin_modell.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
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
                    if (region_list.size() > 1) {
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


        spin_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    card_brand.setVisibility(View.GONE);
                    card_weghit.setVisibility(View.GONE);
                    card_chasi.setVisibility(View.GONE);
                    card_year.setVisibility(View.GONE);
                    card_cost.setVisibility(View.VISIBLE);
                    card_kilometer.setVisibility(View.GONE);

                } else if (position == 1) {
                    card_brand.setVisibility(View.VISIBLE);
                    card_weghit.setVisibility(View.GONE);
                    card_chasi.setVisibility(View.VISIBLE);
                    card_year.setVisibility(View.VISIBLE);
                    card_cost.setVisibility(View.VISIBLE);
                    card_kilometer.setVisibility(View.VISIBLE);
                } else if (position == 2) {
                    card_brand.setVisibility(View.GONE);
                    card_weghit.setVisibility(View.VISIBLE);
                    card_chasi.setVisibility(View.GONE);
                    card_year.setVisibility(View.VISIBLE);
                    card_cost.setVisibility(View.VISIBLE);
                    card_kilometer.setVisibility(View.GONE);
                } else if (position == 3) {
                    card_brand.setVisibility(View.GONE);
                    card_weghit.setVisibility(View.GONE);
                    card_chasi.setVisibility(View.GONE);
                    card_year.setVisibility(View.GONE);
                    card_cost.setVisibility(View.VISIBLE);
                    card_kilometer.setVisibility(View.GONE);

                } else {
                    card_brand.setVisibility(View.GONE);
                    card_weghit.setVisibility(View.GONE);
                    card_chasi.setVisibility(View.GONE);
                    card_year.setVisibility(View.GONE);
                    card_cost.setVisibility(View.VISIBLE);
                    card_kilometer.setVisibility(View.GONE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    private void get_all_province() {
        ProgressBar.setVisibility(View.VISIBLE);

        String provinceUrl = StaticData.PROVINCE;
        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Select_Car_Search_Filter.this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, provinceUrl, Request.Method.GET, 128);
    }

    private void get_all_city(String id) {
        ProgressBar.setVisibility(View.VISIBLE);

        String url = StaticData.DOMAIN_WITH_API + "/provinces/" + id + "/cities";

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Select_Car_Search_Filter.this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 129);
    }

    private void get_all_region(String id) {
        ProgressBar.setVisibility(View.VISIBLE);

        String url = StaticData.get_all_region + id + "/regions";

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Select_Car_Search_Filter.this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 130);
    }

    private void get_all_modell(String id) {
        ProgressBar.setVisibility(View.VISIBLE);
        String url = StaticData.get_all_modell + id + "/models";
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(Select_Car_Search_Filter.this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 127);
    }

    private void get_all_brand() {
        ProgressBar.setVisibility(View.VISIBLE);
        String url = StaticData.get_all_brand;
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(Select_Car_Search_Filter.this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 126);
    }

    private void get_all_cilander_volum() {
        ProgressBar.setVisibility(View.VISIBLE);
        String url = StaticData.getall_culander_volun;
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(Select_Car_Search_Filter.this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 131);
    }

    @Override
    public void on_volley_response(String response, int id) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");
            if (id == 126) {
                if (status.equals("200")) {
                    brands = Brand.Import_list(jsonObject, "list");
                    set_spin_brand();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", getActivity());
                    Select_Car_Search_Filter.this.dismiss();

                }
                ProgressBar.setVisibility(View.GONE);

            }
            if (id == 127) {
                if (status.equals("200")) {
                    modells = Modell.Import_list(jsonObject, "list");
                    set_spin_modell();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", getActivity());
                    Select_Car_Search_Filter.this.dismiss();

                }
                ProgressBar.setVisibility(View.GONE);

            }
            if (id == 128) {
                if (status.equals("200")) {
                    provinces = ProvicesAndCities.cities(jsonObject);
                    set_spin_province();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", getActivity());
                    Select_Car_Search_Filter.this.dismiss();

                }
                ProgressBar.setVisibility(View.GONE);

            }
            if (id == 129) {
                if (status.equals("200")) {
                    citys = ProvicesAndCities.cities(jsonObject);
                    set_spin_citys();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", getActivity());
                    Select_Car_Search_Filter.this.dismiss();

                }
                ProgressBar.setVisibility(View.GONE);

            }
            if (id == 130) {
                if (status.equals("200")) {
                    region = ProvicesAndCities.cities(jsonObject);
                    set_spin_region();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", getActivity());
                    Select_Car_Search_Filter.this.dismiss();

                }
                ProgressBar.setVisibility(View.GONE);

            }
            if (id == 131) {
                if (status.equals("200")) {
                    cylinder_volumes = com.ideabonyan.iranapp.Models.cylinder_volumes.Import_list(jsonObject, "list");
                    set_spin_cylander();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", getActivity());
                    Select_Car_Search_Filter.this.dismiss();

                }
                ProgressBar.setVisibility(View.GONE);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private void set_spin_brand() {
        brand = new ArrayList<>();
        brand.add("همه");
        for (int i = 0; i < brands.size(); i++) {
            brand.add(brands.get(i).getName());
        }
        ArrayAdapter<String> brand_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, brand);
        spin_brand.setAdapter(brand_addapter);

    }

    private void set_spin_cylander() {
        motore_weghit = new ArrayList<>();
        motore_weghit.add("همه");
        for (int i = 0; i < cylinder_volumes.size(); i++) {
            motore_weghit.add(cylinder_volumes.get(i).getValue());
        }

        ArrayAdapter<String> motore_power_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, motore_weghit);
        spin_motor_weghit.setAdapter(motore_power_addapter);

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

    private void set_spin_citys() {
        city = new ArrayList<>();
        city.add("همه");
        for (int i = 0; i < citys.size(); i++) {
            city.add(citys.get(i).getName());
        }
        ArrayAdapter<String> spin_city1 = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, city);
        spin_city.setAdapter(spin_city1);

    }


    private void set_spin_modell() {
        if (modells.size() != 0) {
            moddel = new ArrayList<>();
            moddel.add("همه");
            for (int i = 0; i < modells.size(); i++) {
                moddel.add(modells.get(i).getName());
            }
            ArrayAdapter<String> model_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, moddel);
            lin_modell.setVisibility(View.VISIBLE);
            spin_modell.setAdapter(model_addapter);
        } else {
            lin_modell.setVisibility(View.GONE);
        }
    }

    public static void binddata(Select_Car_Filter select_car_filter) {
        mySelect_car_filter = select_car_filter;
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }

    private boolean valid() {

        if (spin_cat.getSelectedItemPosition() == 1) {
            if ((edt_year_from.getText().length() != 0 && (edt_year_from.getText().length() != 4)) ||
                    edt_year_to.getText().length() != 0 && edt_year_to.getText().length() != 4) {
                ShowToast.failure("لطفا در وارد نمودن سال تولید دقت فرمایید", getActivity());
                return false;

            } else if (edt_year_from.getText().length() == 4 && edt_year_to.getText().length() != 4) {
                ShowToast.failure("لطفا در وارد نمودن سال تولید دقت فرمایید", getActivity());
                return false;

            } else if ((edt_cost_from.getText().length() == 0 && (edt_cost_to.getText().length() != 0)) ||
                    edt_cost_from.getText().length() != 0 && edt_cost_to.getText().length() == 0) {
                ShowToast.failure("لطفا در وارد نمودن قیمت ها دقت فرمایید", getActivity());
                return false;

            } else if ((edt_kilomter_from.getText().length() != 0 && (edt_kilometr_to.getText().length() == 0)) ||
                    edt_kilomter_from.getText().length() == 0 && edt_kilometr_to.getText().length() != 0) {
                ShowToast.failure("لطفا در وارد نمودن مقادیر کیلومتر ها ها دقت فرمایید", getActivity());
                return false;

            } else {
                return true;
            }
        } else if (spin_cat.getSelectedItemPosition() == 2) {
            if (edt_year_from.getText().length() == 4 && edt_year_to.getText().length() != 4 ||
                    edt_year_from.getText().length() != 4 && edt_year_to.getText().length() == 4) {
                ShowToast.failure("لطفا در وارد نمودن سال تولید دقت فرمایید", getActivity());
                return false;

            } else if ((edt_cost_from.getText().length() == 0 && (edt_cost_to.getText().length() != 0)) ||
                    edt_cost_from.getText().length() != 0 && edt_cost_to.getText().length() == 0) {
                ShowToast.failure("لطفا در وارد نمودن قیمت ها دقت فرمایید", getActivity());
                return false;

            } else {
                return true;
            }
        } else {
            if ((edt_cost_from.getText().length() == 0 && (edt_cost_to.getText().length() != 0)) ||
                    edt_cost_from.getText().length() != 0 && edt_cost_to.getText().length() == 0) {
                ShowToast.failure("لطفا در وارد نمودن قیمت ها دقت فرمایید", getActivity());
                return false;

            } else {
                return true;
            }

        }
    }

    private void setdata() {
        cat_name = "";
        brand_id = 0;
        model_id = 0;
        chasi_type = "";
        tolid_from = "";
        tolid_to = "";
        cost_from = "";
        cost_to = "";
        kilometer_from = "";
        kilometer_to = "";
        motor_weghit = 0;
        province_id = 0;
        city_id = 0;

        if (spin_cat.getSelectedItemPosition() == 1) {
            cat_name = "khodro";
            if (spin_brand.getSelectedItemPosition() != 0) {
                brand_id = Integer.parseInt(brands.get(spin_brand.getSelectedItemPosition() - 1).getId());
                if (spin_modell.getSelectedItemPosition() != 0) {
                    model_id = Integer.parseInt(modells.get(spin_modell.getSelectedItemPosition() - 1).getId());
                }
            } else {
                brand_id = 0;
                model_id = 0;

            }
            if (edt_year_to.getText().length() != 0) {
                tolid_to = edt_year_to.getText().toString();
                tolid_from = edt_year_from.getText().toString();
            } else {
                tolid_to = "";
                tolid_from = "";
            }

            if (edt_cost_from.getText().length() != 0) {
                cost_from = edt_cost_from.getText().toString();
                cost_to = edt_cost_to.getText().toString();
            } else {
                cost_from = "";
                cost_to = "";
            }
            if (edt_kilomter_from.getText().length() != 0) {
                kilometer_from = edt_kilomter_from.getText().toString();
                kilometer_to = edt_kilometr_to.getText().toString();
            } else {
                kilometer_from = "";
                kilometer_to = "";
            }
            if (spin_chasi.getSelectedItemPosition() != 0) {
                if (spin_chasi.getSelectedItemPosition() == 1) {
                    chasi_type = "savari";

                } else if (spin_chasi.getSelectedItemPosition() == 2) {
                    chasi_type = "hachback";

                } else if (spin_chasi.getSelectedItemPosition() == 3) {
                    chasi_type = "shasiboland";

                } else if (spin_chasi.getSelectedItemPosition() == 4) {
                    chasi_type = "vanet";

                } else if (spin_chasi.getSelectedItemPosition() == 5) {
                    chasi_type = "krook";

                } else if (spin_chasi.getSelectedItemPosition() == 6) {
                    chasi_type = "van";

                } else if (spin_chasi.getSelectedItemPosition() == 7) {
                    chasi_type = "cupe";

                } else if (spin_chasi.getSelectedItemPosition() == 8) {
                    chasi_type = "station";

                } else if (spin_chasi.getSelectedItemPosition() == 9) {
                    chasi_type = "other";

                }
            } else {
                chasi_type = "";
            }


        } else if (spin_cat.getSelectedItemPosition() == 2) {
            if (edt_year_to.getText().length() != 0) {
                tolid_to = edt_year_to.getText().toString();
                tolid_from = edt_year_from.getText().toString();
            } else {
                tolid_to = "";
                tolid_from = "";
            }
            cat_name = "motorcycle";
//            Log.v("spin_motor_weghit", spin_motor_weghit.getSelectedItemPosition() + "");
            if (spin_motor_weghit.getSelectedItemPosition() != 0) {
//                Log.v("spin_motor_weghit1", cylinder_volumes.get(spin_motor_weghit.getSelectedItemPosition() - 1).getId() + "");

                motor_weghit = Integer.parseInt(cylinder_volumes.get(spin_motor_weghit.getSelectedItemPosition() - 1).getId());
            } else {
                motor_weghit = 0;
            }
            if (edt_cost_from.getText().length() != 0) {
                cost_from = edt_cost_from.getText().toString();
                cost_to = edt_cost_to.getText().toString();
            } else {
                cost_from = "";
                cost_to = "";
            }
        } else if (spin_cat.getSelectedItemPosition() == 3) {
            cat_name = "khodroclasic";
            if (edt_cost_from.getText().length() != 0) {
                cost_from = edt_cost_from.getText().toString();
                cost_to = edt_cost_to.getText().toString();
            } else {
                cost_from = "";
                cost_to = "";
            }

        } else if (spin_cat.getSelectedItemPosition() == 4) {
            cat_name = "khordrosorn";
            if (edt_cost_from.getText().length() != 0) {
                cost_from = edt_cost_from.getText().toString();
                cost_to = edt_cost_to.getText().toString();
            } else {
                cost_from = "";
                cost_to = "";
            }
        } else if (spin_cat.getSelectedItemPosition() == 5) {
            cat_name = "lavazem";
            if (edt_cost_from.getText().length() != 0) {
                cost_from = edt_cost_from.getText().toString();
                cost_to = edt_cost_to.getText().toString();
            } else {
                cost_from = "";
                cost_to = "";
            }

        } else if (spin_cat.getSelectedItemPosition() == 6) {
            cat_name = "other";
            if (edt_cost_from.getText().length() != 0) {
                cost_from = edt_cost_from.getText().toString();
                cost_to = edt_cost_to.getText().toString();
            } else {
                cost_from = "";
                cost_to = "";
            }

        }

        if (spin_province.getSelectedItemPosition() != 0) {
            province_id = Integer.parseInt(provinces.get(spin_province.getSelectedItemPosition() - 1).getId());
        } else {
            province_id = 0;
        }
        if (spin_city.getSelectedItemPosition() != 0) {
            city_id = Integer.parseInt(citys.get(spin_city.getSelectedItemPosition() - 1).getId());
        } else {
            city_id = 0;
        }
        if (spin_region.getSelectedItemPosition() != 0) {
            region_id = region.get(spin_region.getSelectedItemPosition() - 1).getId();
        } else {
            region_id = "0";
        }


        mySelect_car_filter.on_filter_set(cat_name, brand_id, model_id, chasi_type, tolid_from
                , tolid_to, cost_from, cost_to, kilometer_from, kilometer_to, motor_weghit
                , province_id, city_id, statous + "", region_id);

        Select_Car_Search_Filter.this.dismiss();

    }

    public void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                startScale, endScale, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(100);
        v.startAnimation(anim);
    }

    private void clear_scale(int i) {
        if (statous == 3) {
            if (i == 3) {

            } else if (i == 2) {
                scaleView(card_newst, 1.1f, 1f);
                scaleView(card_lowest, 1f, 1.1f);
                clear_color();
                card_lowest1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                txt_lowest.setTextColor(Color.parseColor("#ffffff"));

            } else if (i == 1) {
                scaleView(card_newst, 1.1f, 1f);
                scaleView(card_highest, 1f, 1.1f);
                clear_color();
                card_highest1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                txt_highest.setTextColor(Color.parseColor("#ffffff"));


            }

        } else if (statous == 2) {
            if (i == 3) {
                scaleView(card_lowest, 1.1f, 1f);
                scaleView(card_newst, 1f, 1.1f);
                clear_color();

                card_newst1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                txt_newst.setTextColor(Color.parseColor("#ffffff"));

            } else if (i == 2) {

            } else if (i == 1) {

                scaleView(card_lowest, 1.1f, 1f);
                scaleView(card_highest, 1f, 1.1f);
                clear_color();

                card_highest1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                txt_highest.setTextColor(Color.parseColor("#ffffff"));

            }
        } else if (statous == 1) {
            if (i == 3) {
                scaleView(card_highest, 1.1f, 1f);
                scaleView(card_newst, 1f, 1.1f);
                clear_color();

                card_newst1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                txt_newst.setTextColor(Color.parseColor("#ffffff"));

            } else if (i == 2) {
                scaleView(card_highest, 1.1f, 1f);
                scaleView(card_lowest, 1f, 1.1f);
                clear_color();

                card_lowest1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                txt_lowest.setTextColor(Color.parseColor("#ffffff"));

            } else if (i == 1) {
            }
        } else if (statous == 0) {
            if (i == 3) {
//                scaleView(card_highest, 1.1f, 1f);
                scaleView(card_newst, 1f, 1.1f);
                clear_color();

                card_newst1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                txt_newst.setTextColor(Color.parseColor("#ffffff"));

            } else if (i == 2) {
//                scaleView(card_highest, 1.1f, 1f);
                scaleView(card_lowest, 1f, 1.1f);
                clear_color();

                card_lowest1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                txt_lowest.setTextColor(Color.parseColor("#ffffff"));

            } else if (i == 1) {
//                scaleView(card_lowest, 1.1f, 1f);
                scaleView(card_highest, 1f, 1.1f);
                clear_color();

                card_highest1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                txt_highest.setTextColor(Color.parseColor("#ffffff"));
            }
        }
        statous = i;
    }

    private void clear_color() {
        txt_highest.setTextColor(getResources().getColor(R.color.colorPrimary));
        txt_lowest.setTextColor(getResources().getColor(R.color.colorPrimary));
        txt_newst.setTextColor(getResources().getColor(R.color.colorPrimary));
        card_lowest1.setBackgroundColor(Color.parseColor("#ffffff"));
        card_highest1.setBackgroundColor(Color.parseColor("#ffffff"));
        card_newst1.setBackgroundColor(Color.parseColor("#ffffff"));
    }

}
