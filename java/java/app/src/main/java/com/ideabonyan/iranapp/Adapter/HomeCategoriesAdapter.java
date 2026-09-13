package com.ideabonyan.iranapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ideabonyan.iranapp.Components.GradientIconView;
import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Models.HomeCategories;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.CategoryIcons;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Categories grid of the landing page. See {@link CategoryIcons} for how a category name picks
 * its icon; every icon is drawn in the same white-to-red gradient on the same plate
 * (rv_home_categories2, GradientIconView).
 *
 * Created by SIM on 7/24/2017.
 */
public class HomeCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<HomeCategories> datas;
    private final Context context;

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

        holder.text.setText(datas.get(position).getName());

        Picasso.get().cancelRequest(holder.pic);

        CategoryIcons.Style style = CategoryIcons.of(datas.get(position).getName());

        if (style == null && hasRemoteIcon(datas.get(position).getImage())) {
            // Unknown category: show whatever thumbnail the admin uploaded, in its own colours.
            holder.pic.setGradientEnabled(false);
            Picasso.get()
                    .load(datas.get(position).getImage())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.ic_cat_default)
                    .into(holder.pic);
            return;
        }

        holder.pic.setGradientEnabled(true);
        holder.pic.setImageResource(style != null ? style.icon : CategoryIcons.DEFAULT.icon);
    }

    static boolean hasRemoteIcon(String image) {
        return image != null && !image.equals("null") && !image.trim().isEmpty();
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private static class CellFeedViewHolder extends RecyclerView.ViewHolder {

        final GradientIconView pic;
        final MyTextView text;

        CellFeedViewHolder(View view) {
            super(view);

            pic = (GradientIconView) view.findViewById(R.id.homeCategoriesImage);
            text = (MyTextView) view.findViewById(R.id.homeCategoriesName);
        }
    }
}
