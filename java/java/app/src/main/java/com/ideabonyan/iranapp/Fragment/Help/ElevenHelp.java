package com.ideabonyan.iranapp.Fragment.Help;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ideabonyan.iranapp.R;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ElevenHelp extends Fragment {


    public ElevenHelp() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_blank, container, false);
        holder();
        return view;

    }
    ImageView img;
    View view;
    private void holder() {
        img=view.findViewById(R.id.img);
        Picasso.with(getActivity())
                .load(R.drawable.h11)
                .fit()
                .into(img);

    }

}
