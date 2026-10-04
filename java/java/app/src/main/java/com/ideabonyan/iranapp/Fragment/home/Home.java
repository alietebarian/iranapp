package com.ideabonyan.iranapp.Fragment.home;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ideabonyan.iranapp.Activity.Show_Home_List;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.SectionTiles;
import com.ideabonyan.iranapp.Utils.StaticData;

public class Home extends Fragment {

    View view;
    LinearLayout lin_sell_home, ejare_home, forosh_edari, ejare_edari, khadamar_home, lin_pro;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        holder();
        onclick();
        return view;
    }

    private void holder() {
        SectionTiles.bindPro(view, "آگهی های املاک کاربران پرو");
        SectionTiles.bind(view, R.id.lin_sell_home, R.drawable.ic_estate_home_sale, "فروش مسکونی", "آپارتمان، خانه، زمین");
        SectionTiles.bind(view, R.id.ejare_home, R.drawable.ic_estate_home_rent, "اجاره مسکونی", "آپارتمان، خانه");
        SectionTiles.bind(view, R.id.khadamar_home, R.drawable.ic_estate_services, "خدمات املاک", null);
        SectionTiles.bind(view, R.id.forosh_edari, R.drawable.ic_estate_office_sale, "فروش اداری و تجاری", "مغازه، دفتر کار، صنعتی");
        SectionTiles.bind(view, R.id.ejare_edari, R.drawable.ic_estate_office_rent, "اجاره اداری و تجاری", "مغازه، دفتر کار، صنعتی");

        lin_sell_home = (LinearLayout) view.findViewById(R.id.lin_sell_home);
        ejare_home = (LinearLayout) view.findViewById(R.id.ejare_home);
        forosh_edari = (LinearLayout) view.findViewById(R.id.forosh_edari);
        ejare_edari = (LinearLayout) view.findViewById(R.id.ejare_edari);
        khadamar_home = (LinearLayout) view.findViewById(R.id.khadamar_home);
        lin_pro = (LinearLayout) view.findViewById(R.id.lin_pro);
    }

    private void onclick() {
        lin_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Show_Home_List.class);
                intent.putExtra(StaticData.EXTRA_PRO_ONLY, true);
                startActivity(intent);
            }
        });
        lin_sell_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Show_Home_List.class);
                intent.putExtra("cat_id","1");
                startActivity(intent);

            }
        });
        ejare_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Show_Home_List.class);
                intent.putExtra("cat_id","2");
                startActivity(intent);

            }
        });
        forosh_edari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Show_Home_List.class);
                intent.putExtra("cat_id","3");
                startActivity(intent);
            }
        });
        ejare_edari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Show_Home_List.class);
                intent.putExtra("cat_id","4");
                startActivity(intent);

            }
        });
        khadamar_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Show_Home_List.class);
                intent.putExtra("cat_id","5");
                startActivity(intent);

            }
        });
    }


}
