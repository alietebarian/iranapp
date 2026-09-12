package com.ideabonyan.iranapp.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Models.HomeCategories;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.CategoryIcons;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Categories grid of the landing page. See {@link CategoryIcons} for how a category name picks
 * its icon and accent colour.
 *
 * Each icon sits on a glossy plate: a diagonal gradient of its accent, a soft highlight over the
 * top half, and (Android 9+) a glow of the same colour, so the icons stand out on the dark page.
 *
 * Created by SIM on 7/24/2017.
 */
public class HomeCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /** How far the gradient runs toward white at its top-left and toward black at its bottom-right. */
    private static final float GRADIENT_LIGHTEN = 0.28f;
    private static final float GRADIENT_DARKEN = 0.12f;
    /** Highlight over the top half of the plate. */
    private static final int GLOSS_COLOR = 0x47FFFFFF;

    private final List<HomeCategories> datas;
    private final Context context;
    private final int plateSize;
    private final float plateRadius;

    public HomeCategoriesAdapter(List<HomeCategories> datas, Context context) {
        this.datas = datas;
        this.context = context;
        this.plateSize = context.getResources().getDimensionPixelSize(R.dimen._48sdp);
        this.plateRadius = plateSize * 0.3f;
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
        int accent = CategoryIcons.accent(context, style);

        if (style == null && hasRemoteIcon(datas.get(position).getImage())) {
            // Unknown category: show whatever thumbnail the admin uploaded, on a plain light plate
            // so an arbitrary picture stays readable.
            setPlate(holder.plate, lightPlate(), Color.BLACK);
            holder.pic.setImageTintList(null);
            Picasso.get()
                    .load(datas.get(position).getImage())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.ic_cat_default)
                    .into(holder.pic);
            return;
        }

        setPlate(holder.plate, glossyPlate(accent), accent);
        holder.pic.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        holder.pic.setImageResource(style != null ? style.icon : CategoryIcons.DEFAULT.icon);
    }

    private static void setPlate(View plate, Drawable background, int glow) {
        plate.setBackground(background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            plate.setOutlineSpotShadowColor(glow);
            plate.setOutlineAmbientShadowColor(glow);
        }
    }

    /** Diagonal gradient of the accent with a highlight over its top half. */
    private Drawable glossyPlate(int accent) {
        GradientDrawable fill = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{
                ColorUtils.blendARGB(accent, Color.WHITE, GRADIENT_LIGHTEN),
                ColorUtils.blendARGB(accent, Color.BLACK, GRADIENT_DARKEN)});
        fill.setCornerRadius(plateRadius);

        GradientDrawable gloss = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{GLOSS_COLOR, Color.TRANSPARENT});
        gloss.setCornerRadii(new float[]{plateRadius, plateRadius, plateRadius, plateRadius, 0, 0, 0, 0});

        LayerDrawable plate = new LayerDrawable(new Drawable[]{fill, gloss});
        plate.setLayerInsetBottom(1, plateSize / 2);
        return plate;
    }

    private Drawable lightPlate() {
        GradientDrawable plate = new GradientDrawable();
        plate.setColor(Color.WHITE);
        plate.setCornerRadius(plateRadius);
        return plate;
    }

    private static boolean hasRemoteIcon(String image) {
        return image != null && !image.equals("null") && !image.trim().isEmpty();
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    private static class CellFeedViewHolder extends RecyclerView.ViewHolder {

        final ImageView pic;
        final MyTextView text;
        final FrameLayout plate;

        CellFeedViewHolder(View view) {
            super(view);

            pic = (ImageView) view.findViewById(R.id.homeCategoriesImage);
            text = (MyTextView) view.findViewById(R.id.homeCategoriesName);
            plate = (FrameLayout) view.findViewById(R.id.homeCategoriesIconPlate);
        }
    }
}
