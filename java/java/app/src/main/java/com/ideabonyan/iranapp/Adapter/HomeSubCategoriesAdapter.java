package com.ideabonyan.iranapp.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Models.HomeSubCategories;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.CategoryIcons;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Sub categories grid, shown after tapping a category on the landing page. Each sub category
 * gets the icon its name calls for, drawn like the landing page icons (one colour, one plate);
 * names that match nothing borrow the icon of the category they were opened from. See
 * {@link CategoryIcons}.
 *
 * Created by SIM on 7/24/2017.
 */
public class HomeSubCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<HomeSubCategories> datas;
    private final Context context;
    private final CategoryIcons.Style parentStyle;
    private final ColorStateList iconTint;

    public HomeSubCategoriesAdapter(List<HomeSubCategories> datas, Context context) {
        this(datas, context, null);
    }

    public HomeSubCategoriesAdapter(List<HomeSubCategories> datas, Context context,
                                    CategoryIcons.Style parentStyle) {
        this.datas = datas;
        this.context = context;
        this.parentStyle = parentStyle;
        this.iconTint = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.sub_icon));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.rv_sub_categories, parent, false);

        return new CellFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;

        holder.text.setText(datas.get(position).getName());

        Picasso.get().cancelRequest(holder.pic);

        CategoryIcons.Style style = CategoryIcons.ofSub(datas.get(position).getName(), parentStyle);

        if (style == CategoryIcons.DEFAULT && HomeCategoriesAdapter.hasRemoteIcon(datas.get(position).getImage())) {
            // Nothing matched and there is no parent to borrow from: use the uploaded thumbnail.
            holder.pic.setImageTintList(null);
            Picasso.get()
                    .load(datas.get(position).getImage())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.ic_cat_default)
                    .into(holder.pic);
            return;
        }

        holder.pic.setImageTintList(iconTint);
        holder.pic.setImageResource(style.icon);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private static class CellFeedViewHolder extends RecyclerView.ViewHolder {

        final ImageView pic;
        final MyTextView text;

        CellFeedViewHolder(View view) {
            super(view);

            pic = (ImageView) view.findViewById(R.id.homeCategoriesImage);
            text = (MyTextView) view.findViewById(R.id.homeCategoriesName);
        }
    }
}
