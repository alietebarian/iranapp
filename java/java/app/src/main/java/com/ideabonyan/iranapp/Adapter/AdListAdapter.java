package com.ideabonyan.iranapp.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Models.AdsToBeListed;
import com.ideabonyan.iranapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class AdListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<AdsToBeListed> datas;
    Context context;

    public AdListAdapter(List<AdsToBeListed> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.rv_ads_in_list, parent, false);
        return new AdListAdapter.CellFeedViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final AdListAdapter.CellFeedViewHolder holder = (AdListAdapter.CellFeedViewHolder) viewHolder;

        holder.rvAdListTitle.setText(datas.get(position).getTitle());

        if (datas.get(position).getAddress() != null && !datas.get(position).getAddress().equals("null") && !datas.get(position).getAddress().equals("") && !datas.get(position).getAddress().equals(null)) {
            holder.rvAdListAddress.setText(datas.get(position).getAddress());
        } else holder.rvAdListAddress.setText("بدون آدرس");

        if (datas.get(position).getStatus().equals("pending")) {
            holder.pendingIdentifier.setVisibility(View.VISIBLE);
        } else {
            holder.pendingIdentifier.setVisibility(View.GONE);
        }

        /// check mobile numbers
//        if (!datas.get(position).getTel1().equals(null) && !datas.get(position).getTel1().equals("")) {
//            if (!datas.get(position).getTel2().equals(null) && !datas.get(position).getTel2().equals("")) {
//                holder.rvAdListNumber.setText(datas.get(position).getMobile() +
//                        " - " + datas.get(position).getTel1() +
//                        " - " + datas.get(position).getTel2());
//            } else {
//                holder.rvAdListNumber.setText(datas.get(position).getMobile() +
//                        " - " + datas.get(position).getTel1());
//            }
//        } else{

        if (!(datas.get(position).getMobile() == null || datas.get(position).getMobile().equals("null") ||
                datas.get(position).getMobile().equals("") || datas.get(position).getMobile().equals(null) ||
                datas.get(position).getMobile().equals("0"))) {
            holder.rvAdListNumber.setText(datas.get(position).getMobile());
            holder.lin_phone_number.setVisibility(View.VISIBLE);


        } else if (!(datas.get(position).getTel1() == null || datas.get(position).getTel1().equals("null") ||
                datas.get(position).getTel1().equals("") || datas.get(position).getTel1().equals(null) ||
                datas.get(position).getTel1().equals("0"))) {
            holder.rvAdListNumber.setText(datas.get(position).getTel1());
            holder.lin_phone_number.setVisibility(View.VISIBLE);


        } else if (!(datas.get(position).getTel2() == null || datas.get(position).getTel2().equals("null") ||
                datas.get(position).getTel2().equals("") || datas.get(position).getTel2().equals(null) ||
                datas.get(position).getTel2().equals("0"))) {
            holder.rvAdListNumber.setText(datas.get(position).getTel2());
            holder.lin_phone_number.setVisibility(View.VISIBLE);


        }else{
            holder.lin_phone_number.setVisibility(View.INVISIBLE);
        }
//        }

        ///show or hide discount
//        Log.v("descont", datas.get(position).getDiscount() + " ali");
        if (datas.get(position).getDiscount() != null && !datas.get(position).getDiscount().equals(null)
                && !datas.get(position).getDiscount().equals("") && !datas.get(position).getDiscount().equals("null")
                && !datas.get(position).getDiscount().equals(null)) {
            holder.rvAdListDiscountText.setText("%" + datas.get(position).getDiscount());
            holder.rvAdListDiscountLayout.setVisibility(View.VISIBLE);
        } else {
            holder.rvAdListDiscountLayout.setVisibility(View.GONE);
        }

        holder.rvAdListVideoBadge.setVisibility(datas.get(position).hasVideo() ? View.VISIBLE : View.GONE);

        ///put image
        if (datas.get(position).getPhotos() != null && datas.get(position).getPhotos().size() > 0) {
            if (datas.get(position).getPhotos().get(0) != null) {
                Picasso.get()
                        .load(datas.get(position).getPhotos().get(0).getName())
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.place_holder)
                        .into(holder.rvAdListImage);
            } else {
                Picasso.get()
                        .load(R.drawable.place_holder)
                        .fit()
                        //  .placeholder(R.drawable.placeholder)
                        .into(holder.rvAdListImage);

            }
        } else {
            Picasso.get()
                    .load(R.drawable.place_holder)
                    .fit()
                    //  .placeholder(R.drawable.placeholder)
                    .into(holder.rvAdListImage);

        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private class CellFeedViewHolder extends RecyclerView.ViewHolder {
        LinearLayout lin_phone_number;
        ImageView rvAdListImage, pendingIdentifier, rvAdListVideoBadge;
        RelativeLayout rvAdListDiscountLayout;
        MyTextView rvAdListDiscountText, rvAdListTitle, rvAdListAddress, rvAdListNumber;

        public CellFeedViewHolder(View view) {
            super(view);

            lin_phone_number =  view.findViewById(R.id.lin_phone_number);
            rvAdListImage = (ImageView) view.findViewById(R.id.rvAdListImage);
            rvAdListDiscountLayout = (RelativeLayout) view.findViewById(R.id.rvAdListDiscountLayout);
            rvAdListDiscountText = (MyTextView) view.findViewById(R.id.rvAdListDiscountText);
            rvAdListTitle = (MyTextView) view.findViewById(R.id.rvAdListTitle);
            rvAdListAddress = (MyTextView) view.findViewById(R.id.rvAdListAddress);
            rvAdListNumber = (MyTextView) view.findViewById(R.id.rvAdListNumber);
            pendingIdentifier = (ImageView) view.findViewById(R.id.rvAdListPendingIdentifier);
            rvAdListVideoBadge = view.findViewById(R.id.rvAdListVideoBadge);
        }
    }
}
