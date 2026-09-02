package com.ideabonyan.iranapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Models.Job;
import com.ideabonyan.iranapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class Job_List_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Job> jobs;
    Context context;

    public Job_List_Adapter(List<Job> jobs, Context context) {
        this.jobs = jobs;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.rv_job_in_list, parent, false);

        return new Job_List_Adapter.CellFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final Job_List_Adapter.CellFeedViewHolder holder = (Job_List_Adapter.CellFeedViewHolder) viewHolder;

        holder.rvAdListTitle.setText(jobs.get(position).getAds_title());

        holder.rvAdListAddress.setText(jobs.get(position).getCity_name() + "،" + jobs.get(position).getRegion_name()
                + " / " + jobs.get(position).getElapsed_time());


//        Log.v("size_photh", jobs.get(position).getPhotos().get(0).getName() + "");
        if (jobs.get(position).getThumbnail_photo() != null && !jobs.get(position).getThumbnail_photo().equals(null)
                && !jobs.get(position).getThumbnail_photo().equals("null") && !jobs.get(position).getThumbnail_photo().equals("")) {
            Picasso.with(context)
                    .load(jobs.get(position).getThumbnail_photo())
                    .fit()
                    .placeholder(R.drawable.place_holder_job)
                    .into(holder.rvAdListImage);
        } else {
            if (jobs.get(position).getPhotos().size() > 0) {
                Picasso.with(context)
                        .load(jobs.get(position).getPhotos().get(0).getName())
                        .fit()
                        .placeholder(R.drawable.place_holder_job)
                        .into(holder.rvAdListImage);
            } else {
                Picasso.with(context)
                        .load(R.drawable.place_holder_job)
                        .fit()
                        .centerCrop()
                        .into(holder.rvAdListImage);
            }
        }

        String type = "";
        if (jobs.get(position).getType().equals("karjoo")) {
            type = "آماده به کار";
        } else {
            type = "استخدام";
        }
        String agremment_type = "";
        if (jobs.get(position).getAgremment_type().equals("tamamvaght")) {
            agremment_type = ("تمام وقت");

        } else if (jobs.get(position).getAgremment_type().equals("parevaght")) {
            agremment_type = ("پروژه ای");

        } else if (jobs.get(position).getAgremment_type().equals("moshaveri")) {
            agremment_type = ("مشاوره ای");

        } else if (jobs.get(position).getAgremment_type().equals("parevaght")) {
            agremment_type = ("پاره وقت");

        }
        holder.txt_cost.setText(type + " / " + agremment_type);

//        holder.txt_cost.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    private class CellFeedViewHolder extends RecyclerView.ViewHolder {

        ImageView rvAdListImage, pendingIdentifier;
        MyTextView rvAdListTitle, rvAdListAddress;
        TextView txt_cost;

        public CellFeedViewHolder(View view) {
            super(view);

            rvAdListImage = (ImageView) view.findViewById(R.id.rvAdListImage);
            rvAdListTitle = (MyTextView) view.findViewById(R.id.rvAdListTitle);
            rvAdListAddress = (MyTextView) view.findViewById(R.id.rvAdListAddress);
            pendingIdentifier = (ImageView) view.findViewById(R.id.rvAdListPendingIdentifier);

            txt_cost = (TextView) view.findViewById(R.id.txt_cost);
        }
    }
}
