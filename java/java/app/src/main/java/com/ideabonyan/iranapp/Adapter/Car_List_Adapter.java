package com.ideabonyan.iranapp.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Models.Vehicles;
import com.ideabonyan.iranapp.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;


public class Car_List_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Vehicles> vehiclesList;
    Context context;

    public Car_List_Adapter(List<Vehicles> vehiclesList, Context context) {
        this.vehiclesList = vehiclesList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.rv_car_in_list, parent, false);

        return new Car_List_Adapter.CellFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final Car_List_Adapter.CellFeedViewHolder holder = (Car_List_Adapter.CellFeedViewHolder) viewHolder;

        holder.rvAdListTitle.setText(vehiclesList.get(position).getAds_title());

       holder.rvAdListAddress.setText(vehiclesList.get(position).getCity_name()+"،"+vehiclesList.get(position).getRegion_name()
       +" / "+vehiclesList.get(position).getPassed_time());

        if (vehiclesList.get(position).getStatus().equals("pending"))
            holder.pendingIdentifier.setVisibility(View.VISIBLE);
        else holder.pendingIdentifier.setVisibility(View.GONE);

        if (!vehiclesList.get(position).getPrice().equals("0")) {
            try {
                Long num = Long.parseLong(vehiclesList.get(position).getPrice());

                DecimalFormat numFormat;
                String number;
                numFormat = new DecimalFormat("#,###,###");
                number = numFormat.format(num);
                holder.txt_cost.setText(String.valueOf(number) + " تومان");

            } catch (NumberFormatException e) {
                holder.txt_cost.setText(vehiclesList.get(position).getPrice() + " تومان");
            }
        } else {
            holder.txt_cost.setText("قیمت توافقی");
        }
        if (vehiclesList.get(position).getThumbnail_photo() != null && !vehiclesList.get(position).getThumbnail_photo().equals(null)
                && !vehiclesList.get(position).getThumbnail_photo().equals("null") && !vehiclesList.get(position).getThumbnail_photo().equals("")) {
            Picasso.get()
                    .load(vehiclesList.get(position).getThumbnail_photo())
                    .resize(200, 200)
                    //.fit()
//                    .resizeDimen(16, 9)
                    .centerCrop()
                    .placeholder(R.drawable.place_holder_car)
                    .into(holder.rvAdListImage);
        }
        else {
            Picasso.get()
                    .load(R.drawable.place_holder_car)
                    .resize(200, 200)
                    //.fit()
//                    .resizeDimen(16, 9)
                    .centerCrop()
                    //  .placeholder(R.drawable.placeholder)
                    .into(holder.rvAdListImage);

        }





    }

    @Override
    public int getItemCount() {
        return vehiclesList.size();
    }

    private class CellFeedViewHolder extends RecyclerView.ViewHolder {

        ImageView rvAdListImage, pendingIdentifier;
        MyTextView  rvAdListTitle, rvAdListAddress;
        TextView txt_cost;

        public CellFeedViewHolder(View view) {
            super(view);

            rvAdListImage = (ImageView) view.findViewById(R.id.rvAdListImage);
            rvAdListTitle = (MyTextView) view.findViewById(R.id.rvAdListTitle);
            rvAdListAddress = (MyTextView) view.findViewById(R.id.rvAdListAddress);
            pendingIdentifier = (ImageView) view.findViewById(R.id.rvAdListPendingIdentifier);

            txt_cost= (TextView) view.findViewById(R.id.txt_cost);
        }
    }
}
