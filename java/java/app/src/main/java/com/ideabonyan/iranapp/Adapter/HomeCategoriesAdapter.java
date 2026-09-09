package com.ideabonyan.iranapp.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Models.HomeCategories;
import com.ideabonyan.iranapp.R;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by SIM on 7/24/2017.
 */

public class HomeCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<HomeCategories> datas;
    Context context;

    public HomeCategoriesAdapter(List<HomeCategories> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.rv_home_categories2, parent, false);

        return new CellFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;

        String color = "#00ffffff";
//        if (position == 1) color = "#c62828";
//        if (position == 2) color = "#AD1457";
//        if (position == 3) color = "#6A1B9A";
//        if (position == 4) color = "#00838F";
//        if (position == 5) color = "#2E7D32";
//        if (position == 6) color = "#9E9D24";
//        if (position == 7) color = "#4E342E";
//        if (position == 8) color = "#6A1B9A";
//        if (position == 9) color = "#AD1457";
//        if (position == 10) color = "#006064";
//        if (position == 11) color = "#827717";
//        if (position == 12) color = "#D84315";
//        if (position == 13) color = "#37474F";
//        if (position == 14) color = "#2E7D32";
//        if (position == 15) color = "#1565C0";

//        holder.pic.setBackgroundColor(Color.parseColor(datas.get(position).getImage()));
//        holder.cv.setCardBackgroundColor(Color.parseColor(color));
        holder.text.setText(datas.get(position).getName());

//        Log.i("1111111", datas.get(position).getImage());
//        Log.i("1111111", "1111111");

        if (!datas.get(position).getImage().equals("null") && !datas.get(position).getImage().equals("") && !datas.get(position).getImage().equals(null) && datas.get(position).getImage() != null) {
            Picasso.with(context)
                    .load(datas.get(position).getImage())
//                    .resize(100,100)
                    .fit()
//                    .resizeDimen(16, 9)
//                    .centerCrop()
                    .placeholder(R.drawable.place_holder)
                    .into(holder.pic);
        } else {
            Picasso.with(context)
                    .load(R.drawable.place_holder)
                    .resize(100,100)
                    //.fit()
//                    .resizeDimen(16, 9)
                    .centerCrop()
                    //  .placeholder(R.drawable.placeholder)
                    .into(holder.pic);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private class CellFeedViewHolder extends RecyclerView.ViewHolder {

        SelectableRoundedImageView pic;
        MyTextView text;
//        CardView cv;

        public CellFeedViewHolder(View view) {
            super(view);

//            cv = (CardView) view.findViewById(R.id.rv_home_card_img);
            pic = (SelectableRoundedImageView) view.findViewById(R.id.homeCategoriesImage);
            text = (MyTextView) view.findViewById(R.id.homeCategoriesName);
        }
    }
}
