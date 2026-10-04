package com.ideabonyan.iranapp.Fragment.home;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ideabonyan.iranapp.Activity.Show_Job_list;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.SectionTiles;
import com.ideabonyan.iranapp.Utils.StaticData;

/**
 * A simple {@link Fragment} subclass.
 */
public class Job extends Fragment {

    LinearLayout lin_both, lin_employe, lin_worker, lin_pro;
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
        SectionTiles.bindPro(view, "آگهی های استخدام کاربران پرو");
        SectionTiles.bind(view, R.id.lin_worker, R.drawable.ic_job_seeker, "آماده به کار", null);
        SectionTiles.bind(view, R.id.lin_employe, R.drawable.ic_cat_job, "استخدام", null);
        SectionTiles.bind(view, R.id.lin_both, R.drawable.ic_job_both, "هر دو", null);

        lin_both = (LinearLayout) view.findViewById(R.id.lin_both);
        lin_employe = (LinearLayout) view.findViewById(R.id.lin_employe);
        lin_worker = (LinearLayout) view.findViewById(R.id.lin_worker);
        lin_pro = (LinearLayout) view.findViewById(R.id.lin_pro);


    }

    private void onclick() {
        lin_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show_Job_list.type=null;
                Intent intent=new Intent(getActivity(),Show_Job_list.class);
                intent.putExtra(StaticData.EXTRA_PRO_ONLY, true);
                startActivity(intent);
            }
        });

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
