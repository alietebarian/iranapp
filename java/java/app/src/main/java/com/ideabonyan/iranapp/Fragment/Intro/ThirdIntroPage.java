package com.ideabonyan.iranapp.Fragment.Intro;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ideabonyan.iranapp.R;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdIntroPage extends Fragment {

    View view;
    ImageView img_main;
    public ThirdIntroPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_first_intro_page, container, false);
        holder(view);

        return view;
    }

    private void holder(View view){
        img_main=view.findViewById(R.id.img_main);
        Picasso.with(getActivity())
                .load(R.drawable.intro3)
                .fit()
                .centerCrop()
                .into(img_main);


    }

}
