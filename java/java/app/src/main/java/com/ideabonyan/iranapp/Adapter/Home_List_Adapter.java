package com.ideabonyan.iranapp.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Models.Estates;
import com.ideabonyan.iranapp.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;


public class Home_List_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Estates> estatesList;
    Context context;

    public Home_List_Adapter(List<Estates> estatesList, Context context) {
        this.estatesList = estatesList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.rv_home_in_list, parent, false);

        return new Home_List_Adapter.CellFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final Home_List_Adapter.CellFeedViewHolder holder = (Home_List_Adapter.CellFeedViewHolder) viewHolder;

        holder.rvAdListTitle.setText(estatesList.get(position).getAds_title());

        holder.rvAdListAddress.setText(estatesList.get(position).getCity_name() + "،" + estatesList.get(position).getRegion_name()
                + " / " + estatesList.get(position).getElapsed_time());

        if (estatesList.get(position).getStatus().equals("pending"))
            holder.pendingIdentifier.setVisibility(View.VISIBLE);
        else holder.pendingIdentifier.setVisibility(View.GONE);


        if (estatesList.get(position).getThumbnail_photo() != null && !estatesList.get(position).getThumbnail_photo().equals(null)
                && !estatesList.get(position).getThumbnail_photo().equals("null") && !estatesList.get(position).getThumbnail_photo().equals("")) {
            Picasso.get()
                    .load(estatesList.get(position).getThumbnail_photo())
//                    .resize(200, 200)
                    .fit()
//                    .resizeDimen(16, 9)
//                    .centerCrop()
                    .placeholder(R.drawable.place_holder_home)
                    .into(holder.rvAdListImage);
        } else {
            Picasso.get()
                    .load(R.drawable.place_holder_home)
//                    .resize(200, 200)
                    .fit()
//                    .resizeDimen(16, 9)
//                    .centerCrop()
                    //  .placeholder(R.drawable.placeholder)
                    .into(holder.rvAdListImage);

        }


        if (estatesList.get(position).getCategory_id().equals("1") || estatesList.get(position).getCategory_id().equals("3")
                ||estatesList.get(position).getCategory_parent_id().equals("1")|| estatesList.get(position).getCategory_parent_id().equals("3")) {
            holder.txt_ejare.setVisibility(View.INVISIBLE);
            holder.txt_vadeae.setVisibility(View.INVISIBLE);
            String cost = "";

            if (!estatesList.get(position).getPrice_kharid().equals("0")&&!estatesList.get(position).getPrice_kharid().equals("-1")) {
                try {
                    Long num = Long.parseLong(estatesList.get(position).getPrice_kharid());
                    DecimalFormat numFormat;
                    String number;
                    numFormat = new DecimalFormat("#,###,###");
                    number = numFormat.format(num);
                    cost = (String.valueOf(number) + " تومان");

                } catch (NumberFormatException e) {
                    cost = (estatesList.get(position).getPrice_kharid() + " تومان");
                }
                holder.txt_cost.setText("قیمت : " + cost);

            } else if (estatesList.get(position).getPrice_kharid().equals("0")){
                cost = ("توافقی");
                holder.txt_cost.setText( cost);

            } else {
                cost = ("جهت معاوضه");
                holder.txt_cost.setText( cost);

            }

            holder.txt_cost.setVisibility(View.VISIBLE);
        } else if (estatesList.get(position).getCategory_id().equals("2") || estatesList.get(position).getCategory_id().equals("4")
                ||estatesList.get(position).getCategory_parent_id().equals("2")||estatesList.get(position).getCategory_parent_id().equals("4")) {
            holder.txt_cost.setVisibility(View.INVISIBLE);
            holder.txt_ejare.setVisibility(View.VISIBLE);
            holder.txt_vadeae.setVisibility(View.VISIBLE);
            String cost="";
            String cost1="";
            if (!estatesList.get(position).getPre_pay_ejare().equals("0")&&!estatesList.get(position).getPre_pay_ejare().equals("-1")) {
                try {
                    Long num = Long.parseLong(estatesList.get(position).getPre_pay_ejare());
                    DecimalFormat numFormat;
                    String number;
                    numFormat = new DecimalFormat("#,###,###");
                    number = numFormat.format(num);
                    cost = (String.valueOf(number) + " تومان");

                } catch (NumberFormatException e) {
                    cost = (estatesList.get(position).getPre_pay_ejare() + " تومان");
                }
            } else if (estatesList.get(position).getPre_pay_ejare().equals("0")){
                cost = ("توافقی");
            }else {
                cost = ("مجانی");

            }
            holder.txt_vadeae.setText("ودیعه : " + cost);
            if (!estatesList.get(position).getMonthly_price_ejare().equals("0")&&!estatesList.get(position).getMonthly_price_ejare().equals("-1")) {
                try {
                    Long num = Long.parseLong(estatesList.get(position).getMonthly_price_ejare());
                    DecimalFormat numFormat;
                    String number;
                    numFormat = new DecimalFormat("#,###,###");
                    number = numFormat.format(num);
                    cost1 = (String.valueOf(number) + " تومان");

                } catch (NumberFormatException e) {
                    cost1 = (estatesList.get(position).getMonthly_price_ejare() + " تومان");
                }
            } else if (estatesList.get(position).getMonthly_price_ejare().equals("0")){
                cost1 = ("توافقی");
            }else{
                cost1 = ("مجانی");

            }
            holder.txt_ejare.setText("اجاره ماهیانه : " + cost1);

        } else {
            holder.txt_cost.setVisibility(View.INVISIBLE);
            holder.txt_ejare.setVisibility(View.INVISIBLE);
            holder.txt_vadeae.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return estatesList.size();
    }

    private class CellFeedViewHolder extends RecyclerView.ViewHolder {

        ImageView rvAdListImage, pendingIdentifier;
        MyTextView rvAdListTitle, rvAdListAddress;
        TextView txt_cost, txt_vadeae, txt_ejare;

        public CellFeedViewHolder(View view) {
            super(view);

            rvAdListImage = (ImageView) view.findViewById(R.id.rvAdListImage);
            rvAdListTitle = (MyTextView) view.findViewById(R.id.rvAdListTitle);
            rvAdListAddress = (MyTextView) view.findViewById(R.id.rvAdListAddress);
            txt_ejare = (MyTextView) view.findViewById(R.id.txt_ejare);
            txt_vadeae = (MyTextView) view.findViewById(R.id.txt_vadeae);
            pendingIdentifier = (ImageView) view.findViewById(R.id.rvAdListPendingIdentifier);
            txt_cost = (TextView) view.findViewById(R.id.txt_cost);
        }
    }
}
