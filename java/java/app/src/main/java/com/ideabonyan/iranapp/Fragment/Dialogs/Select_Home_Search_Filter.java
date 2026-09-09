package com.ideabonyan.iranapp.Fragment.Dialogs;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.Select_Home_Filter;
import com.ideabonyan.iranapp.Models.Home_Category;
import com.ideabonyan.iranapp.Models.ProvicesAndCities;
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
public class Select_Home_Search_Filter extends DialogFragment implements Get_Insert_Edit_Data {
    View view;
    LinearLayout lin_show_cost, lin_show_type, lin_show_type_person, lin_show_homeh, lin_show_cat,
            lin_show_location, lin_show_vadeae, lin_show_ejare, lin_show_meter, lin_show_sanad_edari, lin_show_room_num;
    LinearLayout lin_cost_visibility, lin_type_visibiliy, lin_type_person_visibiliy, lin_homeeh_visibiliy, lin_cat_visibility,
            lin_vadeae_visibility, lin_ejare_visibility, lin_meter_visibility, lin_location_visibility, lin_sanad_edari_visibiliy,
            lin_room_num_visibiliy;
    Spinner spin_type, spin_type_person, spin_home, spin_sub_cat, spin_cat, spin_sanad_edari;
    EditText edt_ejare_from, edt_meter_to, edt_meter_from, edt_cost_to, edt_cost_from,
            edt_vadeae_to, edt_vadeae_from, edt_ejare_to;
    CardView card_meter, card_cost, card_type, card_type_person, card_hommeh, card_vadee, card_ejare, card_sanad_edari, card_room_num;
    LinearLayout lin_delete_filter;
    Spinner spin_city, spin_province, spin_room_num;
    List<String> cat, sub_cat, homeh, type, person_type, sanad_edari2;
    List<String> province, city, region_list, room_num_list;
    ProgressBar ProgressBar;
    List<ProvicesAndCities> provinces, citys, region;
    LinearLayout lin_submit;
    String cat_id = "", home = "", person_type2 = "", type2 = "", meter_from = "", meter_to = "", cost_from = "", cost_to = "",
            ejare_from = "", ejare_to = "", vadaea_from = "", Vadea_to = "", sanad_edari = "", province_id = "", city_id = "", parent_id = "";
    String region_id = "", room_num = "";
    List<Home_Category> subcategory;
    public static Select_Home_Filter mySelect_home_filter;
    Spinner spin_region;
    RadioButton radio_moaveze, radio_tavafoghi, radio_maghto, radio_all;
    RadioButton radio_maghto_ejare, radio_all_ejare, radio_majani_ejare, radio_tavafoghi_ejare;
    RadioButton radio_maghto_vadeae, radio_all_vadeae, radio_majani_vadeae, radio_tavafoghi_vadeae;
    String cat_id1,sub_cat_id1;
    boolean sub_cat_change=false;

    public Select_Home_Search_Filter(String cat_id1,String sub_cat_id1) {
        // Required empty public constructor
        this.cat_id1=cat_id1;
        this.sub_cat_id1=sub_cat_id1;
        Log.v("cat_id1",cat_id1+" "+sub_cat_id1);
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
        view = inflater.inflate(R.layout.fragment_select__home__search__filter, container, false);
        holder();
        onclick();
        set_spinner_data();
        on_spiner_change_item();
        get_all_province();
        on_radio_change();
        bind_spinner();
        return view;
    }



    private void on_radio_change() {

        radio_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_cost_change();
                radio_all.setChecked(true);
//                til_cost.setHint("توافقی");
                edt_cost_from.setText("");

                edt_cost_from.setEnabled(false);
                edt_cost_to.setText("");
                edt_cost_to.setEnabled(false);

            }
        });
        radio_tavafoghi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_cost_change();
                radio_tavafoghi.setChecked(true);
//                til_cost.setHint("توافقی");
                edt_cost_from.setText("");
                edt_cost_from.setEnabled(false);
                edt_cost_to.setText("");
                edt_cost_to.setEnabled(false);

            }
        });
        radio_maghto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_cost_change();
                radio_maghto.setChecked(true);
                edt_cost_from.setText("");
                edt_cost_from.setEnabled(true);
                edt_cost_to.setText("");
                edt_cost_to.setEnabled(true);
            }
        });
        radio_moaveze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_cost_change();
                radio_moaveze.setChecked(true);
                edt_cost_from.setText("");
                edt_cost_from.setEnabled(false);
                edt_cost_to.setText("");
                edt_cost_to.setEnabled(false);
            }
        });
        radio_all_ejare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_ejare_change();
                radio_all_ejare.setChecked(true);
                edt_ejare_from.setText("");
                edt_ejare_from.setEnabled(false);
                edt_ejare_to.setText("");
                edt_ejare_to.setEnabled(false);

            }
        });
        radio_tavafoghi_ejare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_ejare_change();
                radio_tavafoghi_ejare.setChecked(true);
                edt_ejare_from.setText("");
                edt_ejare_from.setEnabled(false);
                edt_ejare_to.setText("");
                edt_ejare_to.setEnabled(false);

            }
        });
        radio_maghto_ejare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_ejare_change();
                radio_maghto_ejare.setChecked(true);
                edt_ejare_from.setText("");
                edt_ejare_from.setEnabled(true);
                edt_ejare_to.setText("");
                edt_ejare_to.setEnabled(true);

            }
        });
        radio_majani_ejare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_ejare_change();
                radio_majani_ejare.setChecked(true);
                edt_ejare_from.setText("");
                edt_ejare_from.setEnabled(false);
                edt_ejare_to.setText("");
                edt_ejare_to.setEnabled(false);
            }
        });
        radio_all_vadeae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_vadeae_change();
                radio_all_vadeae.setChecked(true);
                edt_vadeae_from.setText("");
                edt_vadeae_from.setEnabled(false);
                edt_vadeae_to.setText("");
                edt_vadeae_to.setEnabled(false);

            }
        });
        radio_tavafoghi_vadeae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_vadeae_change();
                radio_tavafoghi_vadeae.setChecked(true);
                edt_vadeae_from.setText("");
                edt_vadeae_from.setEnabled(false);
                edt_vadeae_to.setText("");
                edt_vadeae_to.setEnabled(false);

            }
        });
        radio_maghto_vadeae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_vadeae_change();
                radio_maghto_vadeae.setChecked(true);
                edt_vadeae_from.setText("");
                edt_vadeae_from.setEnabled(true);
                edt_vadeae_to.setText("");
                edt_vadeae_to.setEnabled(true);

            }
        });
        radio_majani_vadeae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radio_vadeae_change();
                radio_majani_vadeae.setChecked(true);
                edt_vadeae_from.setText("");
                edt_vadeae_from.setEnabled(false);
                edt_vadeae_to.setText("");
                edt_vadeae_to.setEnabled(false);
            }
        });


    }

    private void holder() {
        lin_show_cat = (LinearLayout) view.findViewById(R.id.lin_show_cat);
        lin_show_homeh = (LinearLayout) view.findViewById(R.id.lin_show_homeh);
        lin_show_type_person = (LinearLayout) view.findViewById(R.id.lin_show_type_person);
        lin_show_type = (LinearLayout) view.findViewById(R.id.lin_show_type);
        lin_show_cost = (LinearLayout) view.findViewById(R.id.lin_show_cost);
        lin_show_meter = (LinearLayout) view.findViewById(R.id.lin_show_meter);
        lin_show_ejare = (LinearLayout) view.findViewById(R.id.lin_show_ejare);
        lin_show_vadeae = (LinearLayout) view.findViewById(R.id.lin_show_vadeae);
        lin_show_location = (LinearLayout) view.findViewById(R.id.lin_show_location);
        lin_show_sanad_edari = (LinearLayout) view.findViewById(R.id.lin_show_sanad_edari);
        lin_show_room_num = (LinearLayout) view.findViewById(R.id.lin_show_room_num);

        lin_cat_visibility = (LinearLayout) view.findViewById(R.id.lin_cat_visibility);
        lin_homeeh_visibiliy = (LinearLayout) view.findViewById(R.id.lin_homeeh_visibiliy);
        lin_type_person_visibiliy = (LinearLayout) view.findViewById(R.id.lin_type_person_visibiliy);
        lin_type_visibiliy = (LinearLayout) view.findViewById(R.id.lin_type_visibiliy);
        lin_cost_visibility = (LinearLayout) view.findViewById(R.id.lin_cost_visibility);
        lin_location_visibility = (LinearLayout) view.findViewById(R.id.lin_location_visibility);
        lin_meter_visibility = (LinearLayout) view.findViewById(R.id.lin_meter_visibility);
        lin_ejare_visibility = (LinearLayout) view.findViewById(R.id.lin_ejare_visibility);
        lin_vadeae_visibility = (LinearLayout) view.findViewById(R.id.lin_vadeae_visibility);
        lin_sanad_edari_visibiliy = (LinearLayout) view.findViewById(R.id.lin_sanad_edari_visibiliy);
        lin_room_num_visibiliy = (LinearLayout) view.findViewById(R.id.lin_room_num_visibiliy);

        lin_delete_filter = (LinearLayout) view.findViewById(R.id.lin_delete_filter);

        spin_cat = (Spinner) view.findViewById(R.id.spin_cat);
        spin_sub_cat = (Spinner) view.findViewById(R.id.spin_sub_cat);
        spin_home = (Spinner) view.findViewById(R.id.spin_home);
        spin_type_person = (Spinner) view.findViewById(R.id.spin_type_person);
        spin_type = (Spinner) view.findViewById(R.id.spin_type);
        spin_sanad_edari = (Spinner) view.findViewById(R.id.spin_sanad_edari);
        spin_room_num = (Spinner) view.findViewById(R.id.spin_room_num);


        spin_province = (Spinner) view.findViewById(R.id.spin_province);
        spin_city = (Spinner) view.findViewById(R.id.spin_city);
        spin_region = view.findViewById(R.id.spin_region);

        edt_cost_from = (EditText) view.findViewById(R.id.edt_cost_from);
        edt_cost_to = (EditText) view.findViewById(R.id.edt_cost_to);
        edt_meter_from = (EditText) view.findViewById(R.id.edt_meter_from);
        edt_meter_to = (EditText) view.findViewById(R.id.edt_meter_to);
        edt_ejare_from = (EditText) view.findViewById(R.id.edt_ejare_from);
        edt_ejare_to = (EditText) view.findViewById(R.id.edt_ejare_to);
        edt_vadeae_from = (EditText) view.findViewById(R.id.edt_vadeae_from);
        edt_vadeae_to = (EditText) view.findViewById(R.id.edt_vadeae_to);

        card_hommeh = (CardView) view.findViewById(R.id.card_hommeh);
        card_type_person = (CardView) view.findViewById(R.id.card_type_person);
        card_type = (CardView) view.findViewById(R.id.card_type);
        card_cost = (CardView) view.findViewById(R.id.card_cost);
        card_meter = (CardView) view.findViewById(R.id.card_meter);
        card_ejare = (CardView) view.findViewById(R.id.card_ejare);
        card_vadee = (CardView) view.findViewById(R.id.card_vadee);
        card_sanad_edari = (CardView) view.findViewById(R.id.card_sanad_edari);
        card_room_num = (CardView) view.findViewById(R.id.card_room_num);

        ProgressBar = (android.widget.ProgressBar) view.findViewById(R.id.ProgressBar);

        lin_submit = (LinearLayout) view.findViewById(R.id.lin_submit);
        lin_delete_filter = (LinearLayout) view.findViewById(R.id.lin_delete_filter);
        lin_delete_filter.setEnabled(true);


        radio_all = view.findViewById(R.id.radio_all);
        radio_maghto = view.findViewById(R.id.radio_maghto);
        radio_tavafoghi = view.findViewById(R.id.radio_tavafoghi);
        radio_moaveze = view.findViewById(R.id.radio_moaveze);

        radio_tavafoghi_ejare = view.findViewById(R.id.radio_tavafoghi_ejare);
        radio_maghto_ejare = view.findViewById(R.id.radio_maghto_ejare);
        radio_majani_ejare = view.findViewById(R.id.radio_majani_ejare);
        radio_all_ejare = view.findViewById(R.id.radio_all_ejare);

        radio_tavafoghi_vadeae = view.findViewById(R.id.radio_tavafoghi_vadeae);
        radio_majani_vadeae = view.findViewById(R.id.radio_majani_vadeae);
        radio_all_vadeae = view.findViewById(R.id.radio_all_vadeae);
        radio_maghto_vadeae = view.findViewById(R.id.radio_maghto_vadeae);

        radio_maghto_vadeae.setChecked(true);
        radio_maghto_ejare.setChecked(true);
        radio_maghto.setChecked(true);

    }

    private void onclick() {
        lin_show_sanad_edari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                if (lin_sanad_edari_visibiliy.getVisibility() == View.GONE) {
                    lin_sanad_edari_visibiliy.setVisibility(View.VISIBLE);
                } else {
                    lin_sanad_edari_visibiliy.setVisibility(View.GONE);

                }

            }
        });
        lin_show_room_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                if (lin_room_num_visibiliy.getVisibility() == View.GONE) {
                    lin_room_num_visibiliy.setVisibility(View.VISIBLE);
                } else {
                    lin_room_num_visibiliy.setVisibility(View.GONE);

                }

            }
        });
        lin_show_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                if (lin_cat_visibility.getVisibility() == View.GONE) {
                    lin_cat_visibility.setVisibility(View.VISIBLE);
                } else {
                    lin_cat_visibility.setVisibility(View.GONE);

                }

            }
        });
        lin_show_homeh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lin_homeeh_visibiliy.getVisibility() == View.GONE) {
                    lin_homeeh_visibiliy.setVisibility(View.VISIBLE);
                } else {
                    lin_homeeh_visibiliy.setVisibility(View.GONE);

                }
            }
        });


        lin_show_type_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lin_type_person_visibiliy.getVisibility() == View.GONE) {
                    lin_type_person_visibiliy.setVisibility(View.VISIBLE);
                } else {
                    lin_type_person_visibiliy.setVisibility(View.GONE);

                }
            }
        });

        lin_show_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lin_type_visibiliy.getVisibility() == View.GONE) {
                    lin_type_visibiliy.setVisibility(View.VISIBLE);
                } else {
                    lin_type_visibiliy.setVisibility(View.GONE);
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
        lin_show_meter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lin_meter_visibility.getVisibility() == View.GONE) {
                    lin_meter_visibility.setVisibility(View.VISIBLE);
                } else {
                    lin_meter_visibility.setVisibility(View.GONE);

                }
            }
        });
        lin_show_ejare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lin_ejare_visibility.getVisibility() == View.GONE) {
                    lin_ejare_visibility.setVisibility(View.VISIBLE);
                } else {
                    lin_ejare_visibility.setVisibility(View.GONE);

                }
            }
        });
        lin_show_vadeae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lin_vadeae_visibility.getVisibility() == View.GONE) {
                    lin_vadeae_visibility.setVisibility(View.VISIBLE);
                } else {
                    lin_vadeae_visibility.setVisibility(View.GONE);

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
                if (valid()) {
                    setdata();
                }
////                mySelect_car_filter.on_filter_set();
            }
        });

        lin_delete_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cat_id = "";
                home = "";
                person_type2 = "";
                type2 = "";
                meter_from = "";
                meter_to = "";
                cost_from = "";
                cost_to = "";
                ejare_from = "";
                ejare_to = "";
                vadaea_from = "";
                Vadea_to = "";
                sanad_edari = "";
                province_id = "";
                city_id = "";
                parent_id = "";
                region_id = "";
                room_num = "";
                mySelect_home_filter.on_filter_set(cat_id, home, person_type2, type2, meter_from, meter_to,
                        cost_from, cost_to, ejare_from, ejare_to, vadaea_from, Vadea_to
                        , sanad_edari, province_id, city_id, parent_id, region_id, room_num);
                Select_Home_Search_Filter.this.dismiss();
            }
        });
    }


    private void bind_spinner(){

        spin_cat.setSelection(Integer.parseInt(cat_id1));


    }

    private void set_spinner_data() {
        cat = new ArrayList<>();
        sub_cat = new ArrayList<>();

        province = new ArrayList<>();
        city = new ArrayList<>();
        region_list = new ArrayList<>();

        homeh = new ArrayList<>();

        sanad_edari2 = new ArrayList<>();

        type = new ArrayList<>();

        person_type = new ArrayList<>();
        room_num_list = new ArrayList<>();
        room_num_list.add("لطفا انتخاب کنید.");
        room_num_list.add("بدون اتاق");
        room_num_list.add("یک");
        room_num_list.add("دو");
        room_num_list.add("سه");
        room_num_list.add("چهار");
        room_num_list.add("پنج یا بیشتر");

        sanad_edari2.add("همه");
        sanad_edari2.add("دارد");
        sanad_edari2.add("ندارد");

        type.add("همه");
        type.add("ارائه یا فروشی");
        type.add("درخواستی");

        person_type.add("همه");
        person_type.add("شخصی");
        person_type.add("مشاور املاک");

        homeh.add("همه");
        homeh.add("نیست");
        homeh.add("هست");


        cat.add("همه");
        cat.add("فروشی مسکونی (آپارتمان،خانه،زمین)");
        cat.add("اجاره مسکونی (آپارتمان،خانه،زمین)");
        cat.add("فروش اداری و تجاری (مغازه،دفتر،صنعتی)");
        cat.add("اجاره اداری و تجاری (مغازه،دفتر،صنعتی)");
        cat.add("خدمات املاک");

        sub_cat.add("همه");

        province.add("همه");
        city.add("همه");
        region_list.add("همه");


        ArrayAdapter<String> cat_Adapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, cat);
        ArrayAdapter<String> sub_cat_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, sub_cat);

        ArrayAdapter<String> home_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, homeh);
        ArrayAdapter<String> peron_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, person_type);
        ArrayAdapter<String> type_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, type);
        ArrayAdapter<String> sanad_edari_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, sanad_edari2);
        ArrayAdapter<String> room_num_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, room_num_list);


        ArrayAdapter<String> province_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, province);
        ArrayAdapter<String> city_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, city);
        ArrayAdapter<String> region_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, region_list);

        spin_cat.setAdapter(cat_Adapter);
        spin_sub_cat.setAdapter(sub_cat_addapter);
        spin_room_num.setAdapter(room_num_addapter);

        spin_province.setAdapter(province_addapter);
        spin_city.setAdapter(city_addapter);

        spin_home.setAdapter(home_addapter);
        spin_type_person.setAdapter(peron_addapter);
        spin_type.setAdapter(type_addapter);
        spin_sanad_edari.setAdapter(sanad_edari_addapter);
        spin_region.setAdapter(region_addapter);


    }

    private void on_spiner_change_item() {
        spin_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position != 0) {
                    get_all_city(provinces.get(position - 1).getId());
                } else {
                    if (city.size() > 1) {
                        city = new ArrayList<>();
                        city.add("همه");
                        ArrayAdapter<String> spin_city1 = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, city);
                        spin_city.setAdapter(spin_city1);
                    }

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

                set_spin_cat(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        spin_sub_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                set_spin_sub_cat();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void set_spin_sub_cat() {
        if (spin_cat.getSelectedItemPosition() == 1 && spin_sub_cat.getSelectedItemPosition() == 3) {
            card_room_num.setVisibility(View.GONE);
            spin_room_num.setSelection(0);
        } else if (spin_cat.getSelectedItemPosition() != 5) {
            card_room_num.setVisibility(View.VISIBLE);
//
        }
    }

    private void set_spin_cat(int position) {
        subcategory = new ArrayList<>();
        if (position == 1) {//home sell
            subcategory.add(new Home_Category("6", "آپارتمان", "1"));
            subcategory.add(new Home_Category("7", "خانه و ویلا", "1"));
            subcategory.add(new Home_Category("8", "زمین کلنگی", "1"));

            type = new ArrayList<>();
            type.add("همه");
            type.add("فروشی");
            type.add("درخواستی");
            ArrayAdapter<String> type_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, type);
            spin_type.setAdapter(type_addapter);


            card_cost.setVisibility(View.VISIBLE);
            card_ejare.setVisibility(View.GONE);
            card_vadee.setVisibility(View.GONE);
            card_hommeh.setVisibility(View.VISIBLE);
            card_type.setVisibility(View.VISIBLE);
            card_meter.setVisibility(View.VISIBLE);
            card_sanad_edari.setVisibility(View.GONE);
            card_room_num.setVisibility(View.VISIBLE);


        } else if (position == 2) {// ejare home
            subcategory.add(new Home_Category("9", "آپارتمان", "2"));
            subcategory.add(new Home_Category("10", "خانه و ویلا", "2"));
            card_cost.setVisibility(View.GONE);
            card_ejare.setVisibility(View.VISIBLE);
            card_vadee.setVisibility(View.VISIBLE);
            card_hommeh.setVisibility(View.VISIBLE);
            card_type.setVisibility(View.VISIBLE);
            card_meter.setVisibility(View.VISIBLE);
            card_sanad_edari.setVisibility(View.GONE);
            card_room_num.setVisibility(View.VISIBLE);
            type = new ArrayList<>();
            type.add("همه");
            type.add("ارائه");
            type.add("درخواستی");
            ArrayAdapter<String> type_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, type);
            spin_type.setAdapter(type_addapter);


        } else if (position == 3) {//seall edari
            subcategory.add(new Home_Category("11", "دفترکار،اتاق اداری و مطب", "3"));
            subcategory.add(new Home_Category("12", "مغازه و غرفه", "3"));
            subcategory.add(new Home_Category("13", "صنعتی،کشاورزی وتجاری", "3"));
            card_cost.setVisibility(View.VISIBLE);
            card_ejare.setVisibility(View.GONE);
            card_vadee.setVisibility(View.GONE);
            card_hommeh.setVisibility(View.VISIBLE);
            card_type.setVisibility(View.VISIBLE);
            card_meter.setVisibility(View.VISIBLE);
            card_sanad_edari.setVisibility(View.VISIBLE);
            card_room_num.setVisibility(View.VISIBLE);
            type = new ArrayList<>();
            type.add("همه");
            type.add("فروشی");
            type.add("درخواستی");
            ArrayAdapter<String> type_addapter = new ArrayAdapter<>(getActivity(),R.layout.item_spinner_layout, type);
            spin_type.setAdapter(type_addapter);


        } else if (position == 4) {//ejare edari
            subcategory.add(new Home_Category("14", "دفترکار،اتاق اداری و مطب", "4"));
            subcategory.add(new Home_Category("15", "مغازه و غرفه", "4"));
            subcategory.add(new Home_Category("16", "صنعتی،کشاورزی وتجاری", "4"));
            card_cost.setVisibility(View.GONE);
            card_ejare.setVisibility(View.VISIBLE);
            card_vadee.setVisibility(View.VISIBLE);
            card_hommeh.setVisibility(View.VISIBLE);
            card_type.setVisibility(View.VISIBLE);
            card_meter.setVisibility(View.VISIBLE);
            card_sanad_edari.setVisibility(View.GONE);
            card_room_num.setVisibility(View.VISIBLE);
            type = new ArrayList<>();
            type.add("همه");
            type.add("ارائه");
            type.add("درخواستی");
            ArrayAdapter<String> type_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, type);
            spin_type.setAdapter(type_addapter);


        } else if (position == 5) {
            subcategory.add(new Home_Category("17", "آژانس املاک", "5"));
            subcategory.add(new Home_Category("18", "مشارکت در ساخت", "5"));
            subcategory.add(new Home_Category("19", "امور مالی و حقوقی", "5"));
            subcategory.add(new Home_Category("20", "پیش فروش", "5"));
            card_cost.setVisibility(View.GONE);
            card_ejare.setVisibility(View.GONE);
            card_vadee.setVisibility(View.GONE);
            card_hommeh.setVisibility(View.GONE);
            card_type.setVisibility(View.GONE);
            card_meter.setVisibility(View.GONE);
            card_sanad_edari.setVisibility(View.GONE);
            card_room_num.setVisibility(View.GONE);

        } else if (position == 0) {//non of them
            subcategory = new ArrayList<>();

            card_cost.setVisibility(View.GONE);
            card_ejare.setVisibility(View.GONE);
            card_vadee.setVisibility(View.GONE);
            card_hommeh.setVisibility(View.GONE);
            card_type.setVisibility(View.GONE);
            card_meter.setVisibility(View.GONE);
            card_sanad_edari.setVisibility(View.GONE);
            card_room_num.setVisibility(View.GONE);

        }
        sub_cat = new ArrayList<>();
        sub_cat.add("همه");
        for (int i = 0; i < subcategory.size(); i++) {
            sub_cat.add(subcategory.get(i).getName());
        }
        ArrayAdapter<String> sub_cat_addapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, sub_cat);
        spin_sub_cat.setAdapter(sub_cat_addapter);

        if (position!=0&&!sub_cat_change&&!sub_cat_id1.equals("0")){
            for (int i=0;i<subcategory.size();i++){
                if (subcategory.get(i).getId().equals(sub_cat_id1)){
                    spin_sub_cat.setSelection(i+1);
                    i=subcategory.size();
                }
            }

        }

    }

    private void get_all_province() {
        ProgressBar.setVisibility(View.VISIBLE);

        String provinceUrl = StaticData.PROVINCE;
        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Select_Home_Search_Filter.this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, provinceUrl, Request.Method.GET, 128);
    }

    private void get_all_city(String id) {
        ProgressBar.setVisibility(View.VISIBLE);

        String url = StaticData.DOMAIN_WITH_API + "/provinces/" + id + "/cities";

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Select_Home_Search_Filter.this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 129);
    }

    private void get_all_region(String id) {
        ProgressBar.setVisibility(View.VISIBLE);

        String url = StaticData.get_all_region + id + "/regions";

        Map<String, String> params = new HashMap<String, String>();

        Get_Volley_Call_Back.binddata(Select_Home_Search_Filter.this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 130);
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
                    Select_Home_Search_Filter.this.dismiss();

                }
                ProgressBar.setVisibility(View.GONE);

            }
            if (id == 129) {
                if (status.equals("200")) {
                    citys = ProvicesAndCities.cities(jsonObject);
                    set_spin_citys();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", getActivity());
                    Select_Home_Search_Filter.this.dismiss();

                }
                ProgressBar.setVisibility(View.GONE);

            }
            if (id == 130) {
                if (status.equals("200")) {
                    region = ProvicesAndCities.cities(jsonObject);
                    set_spin_region();

                } else {
                    ShowToast.failure("در ارتباط با سرور به مشکل برخورد کرده ایم لطفا مجددا تلاش نفرمایید.", getActivity());
                    Select_Home_Search_Filter.this.dismiss();

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

    private void set_spin_citys() {
        city = new ArrayList<>();
        city.add("همه");
        for (int i = 0; i < citys.size(); i++) {
            city.add(citys.get(i).getName());
        }
        ArrayAdapter<String> spin_city1 = new ArrayAdapter<>(getActivity(), R.layout.item_spinner_layout, city);
        spin_city.setAdapter(spin_city1);

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


    public static void binddata(Select_Home_Filter select_home_filter) {
        mySelect_home_filter = select_home_filter;
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }

    private boolean valid() {


        if (edt_cost_from.getText().length() != 0 && edt_cost_to.getText().length() == 0) {
            ShowToast.failure("لطفا در وارد نمودن قیمت ها دقت فرمایید", getActivity());

            return false;

        } else if (edt_cost_from.getText().length() == 0 && edt_cost_to.getText().length() != 0) {
            ShowToast.failure("لطفا در وارد نمودن قیمت ها دقت فرمایید", getActivity());

            return false;

        } else if (edt_meter_from.getText().length() != 0 && edt_meter_to.getText().length() == 0) {
            ShowToast.failure("لطفا در وارد نمودن متراژ دقت فرمایید", getActivity());
            return false;

        } else if (edt_meter_from.getText().length() == 0 && edt_meter_to.getText().length() != 0) {
            ShowToast.failure("لطفا در وارد نمودن متراژ دقت فرمایید", getActivity());

            return false;

        } else if (edt_ejare_from.getText().length() != 0 && edt_ejare_to.getText().length() == 0) {
            ShowToast.failure("لطفا در وارد نمودن مبلغ اجاره دقت فرمایید", getActivity());
            return false;

        } else if (edt_ejare_from.getText().length() == 0 && edt_ejare_to.getText().length() != 0) {
            ShowToast.failure("لطفا در وارد نمودن مبلغ اجاره دقت فرمایید", getActivity());
            return false;
        } else if (edt_vadeae_from.getText().length() != 0 && edt_vadeae_to.getText().length() == 0) {
            ShowToast.failure("لطفا در وارد نمودن مبلغ ودیعه دقت فرمایید", getActivity());
            return false;

        } else if (edt_vadeae_from.getText().length() == 0 && edt_vadeae_to.getText().length() != 0) {
            ShowToast.failure("لطفا در وارد نمودن مبلغ ودیعه دقت فرمایید", getActivity());
            return false;
        } else {
            return true;
        }


    }

    private void setdata() {
        cat_id = "";
        home = "";
        person_type2 = "";
        type2 = "";
        meter_from = "";
        meter_to = "";
        cost_from = "";
        cost_to = "";
        ejare_from = "";
        ejare_to = "";
        vadaea_from = "";
        Vadea_to = "";
        sanad_edari = "";
        province_id = "";
        city_id = "";
        parent_id = "";
        region_id = "";
        room_num = "";

        if (spin_cat.getSelectedItemPosition() == 1 || spin_cat.getSelectedItemPosition() == 2 ||
                spin_cat.getSelectedItemPosition() == 3 || spin_cat.getSelectedItemPosition() == 4) {
            if (spin_home.getSelectedItemPosition() != 0) {
                if (spin_home.getSelectedItemPosition() == 1) {
                    home = "0";
                } else {
                    home = "1";
                }
            } else {
                home = "-1";
            }

            if (spin_type.getSelectedItemPosition() != 0) {
                if (spin_type.getSelectedItemPosition() == 1) {
                    type2 = "sell";
                } else {
                    type2 = "buy";
                }
            } else {
                type2 = "";
            }

            if (edt_meter_to.getText().length() != 0) {
                meter_from = edt_meter_from.getText().toString();
                meter_to = edt_meter_to.getText().toString();
            } else {
                meter_from = "";
                meter_to = "";
            }

            if (spin_cat.getSelectedItemPosition() == 1 || spin_cat.getSelectedItemPosition() == 3) {
                if (radio_maghto.isChecked()) {
                    if (edt_cost_to.getText().length() != 0) {
                        cost_from = edt_cost_from.getText().toString();
                        cost_to = edt_cost_to.getText().toString();
                    } else {
                        cost_from = "";
                        cost_to = "";
                    }
                }else if (radio_moaveze.isChecked()){
                    cost_from = "-1";
                    cost_to = "-1";
                }else if (radio_tavafoghi.isChecked()){
                    cost_from = "0";
                    cost_to = "0";
                }

                if (spin_cat.getSelectedItemPosition() == 3) {
                    if (spin_sanad_edari.getSelectedItemPosition() != 0) {
                        if (spin_sanad_edari.getSelectedItemPosition() == 1) {
                            sanad_edari = "1";
                        } else {
                            sanad_edari = "0";
                        }
                    } else {
                        sanad_edari = "-1";
                    }
                }
            } else if (spin_cat.getSelectedItemPosition() == 2 || spin_cat.getSelectedItemPosition() == 4) {

                if (radio_maghto_ejare.isChecked()) {
                    if (edt_ejare_from.getText().length() != 0) {
                        ejare_from = edt_ejare_from.getText().toString();
                        ejare_to = edt_ejare_to.getText().toString();
                    } else {
                        ejare_from = "";
                        ejare_to = "";
                    }
                    if (edt_vadeae_from.getText().length() != 0) {
                        vadaea_from = edt_vadeae_from.getText().toString();
                        Vadea_to = edt_vadeae_to.getText().toString();
                    } else {
                        vadaea_from = "";
                        Vadea_to = "";
                    }
                } else if (radio_tavafoghi_ejare.isChecked()) {
                    ejare_from = "0";
                    ejare_to = "0";

                } else if (radio_majani_ejare.isChecked()) {
                    ejare_from = "-1";
                    ejare_to = "-1";

                }
                if (radio_tavafoghi_vadeae.isChecked()){
                    vadaea_from = "0";
                    Vadea_to = "0";
                }else if (radio_majani_vadeae.isChecked()){
                    vadaea_from = "-1";
                    Vadea_to = "-1";
                }
            }
        }
        if (spin_cat.getSelectedItemPosition() != 0) {
            if (spin_sub_cat.getSelectedItemPosition() != 0) {
                cat_id = subcategory.get(spin_sub_cat.getSelectedItemPosition() - 1).getId();
                parent_id = subcategory.get(spin_sub_cat.getSelectedItemPosition() - 1).getParent_id();

            } else {
                cat_id = String.valueOf(spin_cat.getSelectedItemPosition());
                parent_id = "0";
            }
        } else {
            cat_id = "0";
            parent_id = "0";
        }
        if (spin_province.getSelectedItemPosition() != 0) {
            if (spin_city.getSelectedItemPosition() != 0) {
                if (spin_region.getSelectedItemPosition() != 0) {
                    region_id = region.get(spin_region.getSelectedItemPosition() - 1).getId();
                } else {
                    city_id = citys.get(spin_city.getSelectedItemPosition() - 1).getId();
                }
            } else {
                province_id = provinces.get(spin_province.getSelectedItemPosition() - 1).getId();
            }
        } else {
            province_id = "0";
            city_id = "0";
            region_id = "0";
        }

        if (spin_type_person.getSelectedItemPosition() != 0) {
            if (spin_type_person.getSelectedItemPosition() == 1) {
                person_type2 = "person";

            } else {
                person_type2 = "moshaver_amlak";

            }
        } else {
            person_type2 = "";

        }
        if (spin_room_num.getSelectedItemPosition() != 0) {
            room_num = String.valueOf(spin_room_num.getSelectedItemPosition() - 1);
        }


        mySelect_home_filter.on_filter_set(cat_id, home, person_type2, type2, meter_from, meter_to,
                cost_from, cost_to, ejare_from, ejare_to, vadaea_from, Vadea_to
                , sanad_edari, province_id, city_id, parent_id, region_id, room_num);

        Select_Home_Search_Filter.this.dismiss();

    }

    private void radio_cost_change() {
        radio_moaveze.setChecked(false);
        radio_maghto.setChecked(false);
        radio_tavafoghi.setChecked(false);
        radio_all.setChecked(false);
    }

    private void radio_ejare_change() {
        radio_majani_ejare.setChecked(false);
        radio_maghto_ejare.setChecked(false);
        radio_tavafoghi_ejare.setChecked(false);
        radio_all_ejare.setChecked(false);
    }

    private void radio_vadeae_change() {
        radio_majani_vadeae.setChecked(false);
        radio_maghto_vadeae.setChecked(false);
        radio_tavafoghi_vadeae.setChecked(false);
        radio_all_vadeae.setChecked(false);
    }

}
