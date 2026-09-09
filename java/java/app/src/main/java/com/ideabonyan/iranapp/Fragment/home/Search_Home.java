package com.ideabonyan.iranapp.Fragment.home;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ideabonyan.iranapp.Activity.Car_Search;
import com.ideabonyan.iranapp.Activity.Home_Search;
import com.ideabonyan.iranapp.Activity.Job_Search;
import com.ideabonyan.iranapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Search_Home extends Fragment {

    LinearLayout lic_search_job,lin_search_car,lin__search_home;
    View view;
    public Search_Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_search__home, container, false);
        holder();
        onclick();
        return view;
    }
    private void holder(){
        lic_search_job= (LinearLayout) view.findViewById(R.id.lic_search_job);
        lin_search_car= (LinearLayout) view.findViewById(R.id.lin_search_car);
        lin__search_home= (LinearLayout) view.findViewById(R.id.lin__search_home);
    }

    private void onclick(){
        lin__search_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Home_Search.class));

            }
        });lin_search_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Car_Search.class));

            }
        });lic_search_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Job_Search.class));
            }
        });
    }

}
