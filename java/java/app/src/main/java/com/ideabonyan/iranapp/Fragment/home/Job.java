package com.ideabonyan.iranapp.Fragment.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ideabonyan.iranapp.Activity.Show_Job_list;
import com.ideabonyan.iranapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Job extends Fragment {

    LinearLayout lin_both, lin_employe, lin_worker;
    View view;

    public Job() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_job, container, false);
        holder();
        onclick();
        return view;
    }

    private void holder() {
        lin_both = (LinearLayout) view.findViewById(R.id.lin_both);
        lin_employe = (LinearLayout) view.findViewById(R.id.lin_employe);
        lin_worker = (LinearLayout) view.findViewById(R.id.lin_worker);


    }

    private void onclick() {

        lin_both.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show_Job_list.type=null;
                startActivity(new Intent(getActivity(),Show_Job_list.class));

            }
        });
        lin_employe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show_Job_list.type="forsatshoghli";
                startActivity(new Intent(getActivity(),Show_Job_list.class));


            }
        });
        lin_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show_Job_list.type="karjoo";
                startActivity(new Intent(getActivity(),Show_Job_list.class));


            }
        });

    }
}
