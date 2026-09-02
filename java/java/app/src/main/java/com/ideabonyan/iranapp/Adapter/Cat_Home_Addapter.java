package com.ideabonyan.iranapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ideabonyan.iranapp.Models.Home_Category;
import com.ideabonyan.iranapp.R;

import java.util.List;


public class Cat_Home_Addapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Home_Category> home_categories;
    Context context;

    public Cat_Home_Addapter(List<Home_Category> home_categories, Context context) {
        this.home_categories = home_categories;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.rv_cat_home, parent, false);

        return new Cat_Home_Addapter.CellFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final Cat_Home_Addapter.CellFeedViewHolder holder = (Cat_Home_Addapter.CellFeedViewHolder) viewHolder;

        holder.title.setText(home_categories.get(position).getName());
//        holder.writer.setText(home_categories.get(position).getWriterName());
    }

    @Override
    public int getItemCount() {
        return home_categories.size();
    }

    private class CellFeedViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        CellFeedViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.pishkhanBookRvName);
//            writer = (TextView) view.findViewById(R.id.pishkhanBookRvWriter);
        }
    }
}
