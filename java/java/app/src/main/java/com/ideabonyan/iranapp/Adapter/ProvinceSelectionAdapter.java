package com.ideabonyan.iranapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Models.ProvicesAndCities;
import com.ideabonyan.iranapp.R;

import java.util.List;


public class ProvinceSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {


    List<ProvicesAndCities> datas;
    Context context;

    public ProvinceSelectionAdapter(List<ProvicesAndCities> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.rv_province_and_city_selection, parent, false);

        return new ProvinceSelectionAdapter.CellFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ProvinceSelectionAdapter.CellFeedViewHolder holder = (ProvinceSelectionAdapter.CellFeedViewHolder) viewHolder;

        holder.txt_name.setText(datas.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private class CellFeedViewHolder extends RecyclerView.ViewHolder {

        MyTextView txt_name;

        public CellFeedViewHolder(View view) {
            super(view);

            txt_name = (MyTextView) view.findViewById(R.id.txt_name);
        }
    }
}
