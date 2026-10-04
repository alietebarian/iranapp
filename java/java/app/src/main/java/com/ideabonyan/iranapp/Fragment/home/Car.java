package com.ideabonyan.iranapp.Fragment.home;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ideabonyan.iranapp.Activity.Show_Car_list;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.SectionTiles;
import com.ideabonyan.iranapp.Utils.StaticData;

/**
 * A simple {@link Fragment} subclass.
 */
public class Car extends Fragment {

    LinearLayout lin_car_part, lin_havey_car, lin_classic_car, lin_motor, lin_car, lin_other, lin_pro;
    View view;

    public Car() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_car, container, false);
        holder();
        onclick();
        return view;
    }

    private void holder() {
        SectionTiles.bindPro(view, "آگهی های وسایل نقلیه کاربران پرو");
        SectionTiles.bind(view, R.id.lin_car, R.drawable.ic_cat_car, "خودرو", null);
        SectionTiles.bind(view, R.id.lin_motor, R.drawable.ic_vehicle_motorcycle, "موتور سیکلت", null);
        SectionTiles.bind(view, R.id.lin_havey_car, R.drawable.ic_cat_transport, "خودرو سنگین و نیمه سنگین", null);
        SectionTiles.bind(view, R.id.lin_classic_car, R.drawable.ic_vehicle_classic, "خودرو کلاسیک", null);
        SectionTiles.bind(view, R.id.lin_car_part, R.drawable.ic_cat_service, "لوازم وسایل نقلیه", null);
        SectionTiles.bind(view, R.id.lin_other, R.drawable.ic_vehicle_bicycle, "سایر وسایل نقلیه", null);

        lin_car_part = (LinearLayout) view.findViewById(R.id.lin_car_part);
        lin_havey_car = (LinearLayout) view.findViewById(R.id.lin_havey_car);
        lin_classic_car = (LinearLayout) view.findViewById(R.id.lin_classic_car);
        lin_motor = (LinearLayout) view.findViewById(R.id.lin_motor);
        lin_car = (LinearLayout) view.findViewById(R.id.lin_car);
        lin_other = (LinearLayout) view.findViewById(R.id.lin_other);
        lin_pro = (LinearLayout) view.findViewById(R.id.lin_pro);

    }

    private void onclick() {
        lin_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Show_Car_list.class);
                intent.putExtra(StaticData.EXTRA_PRO_ONLY, true);
                startActivity(intent);
            }
        });

        lin_car_part.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Show_Car_list.class);
                intent.putExtra("type","lavazem");
                startActivity(intent);

            }
        });
        lin_havey_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Show_Car_list.class);
                intent.putExtra("type","khordrosorn");
                startActivity(intent);


            }
        });
        lin_classic_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Show_Car_list.class);
                intent.putExtra("type","khodroclasic");
                startActivity(intent);


            }
        });
        lin_motor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Show_Car_list.class);
                intent.putExtra("type","motorcycle");
                startActivity(intent);


            }
        });
        lin_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Show_Car_list.class);
                intent.putExtra("type","khodro");
                startActivity(intent);



            }
        });
        lin_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),Show_Car_list.class);
                intent.putExtra("type","other");
                startActivity(intent);


            }
        });

    }
}
