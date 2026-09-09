package com.ideabonyan.iranapp.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ideabonyan.iranapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Photo carousel for the ad/news detail screens, backing a ViewPager2.
 * Replaces the abandoned com.daimajia.slider library, which called a Picasso API
 * that no longer exists.
 */
public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.SlideHolder> {

    private static final long AUTO_CYCLE_DELAY_MS = 4000;

    private final List<String> imageUrls = new ArrayList<>();
    private int placeholderRes = R.drawable.place_holder;

    public ImageSliderAdapter() {
    }

    public ImageSliderAdapter(int placeholderRes) {
        this.placeholderRes = placeholderRes;
    }

    /** Shows a single placeholder slide, for ads with no photos. */
    public void showPlaceholderOnly() {
        imageUrls.clear();
        notifyDataSetChanged();
    }

    public void setImageUrls(List<String> urls) {
        imageUrls.clear();
        if (urls != null) imageUrls.addAll(urls);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SlideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_slide, parent, false);
        return new SlideHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideHolder holder, int position) {
        if (imageUrls.isEmpty()) {
            holder.image.setImageResource(placeholderRes);
            return;
        }
        Picasso.get()
                .load(imageUrls.get(position))
                .placeholder(placeholderRes)
                .error(placeholderRes)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return Math.max(1, imageUrls.size());
    }

    static class SlideHolder extends RecyclerView.ViewHolder {
        final ImageView image;

        SlideHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.slideImage);
        }
    }

    /**
     * Wires a pager to its dot indicator and starts auto-cycling when there is more than
     * one photo. The cycle stops when the pager leaves the window, so no handler outlives
     * the Activity.
     */
    public static void attach(ViewPager2 pager, TabLayout indicator, ImageSliderAdapter adapter) {
        pager.setAdapter(adapter);

        if (indicator != null) {
            new TabLayoutMediator(indicator, pager, (tab, position) -> {
            }).attach();
            indicator.setVisibility(adapter.getItemCount() > 1 ? View.VISIBLE : View.GONE);
        }

        if (adapter.getItemCount() <= 1) return;

        final Runnable advance = new Runnable() {
            @Override
            public void run() {
                int count = pager.getAdapter() == null ? 0 : pager.getAdapter().getItemCount();
                if (count > 1) {
                    pager.setCurrentItem((pager.getCurrentItem() + 1) % count, true);
                }
                pager.postDelayed(this, AUTO_CYCLE_DELAY_MS);
            }
        };

        pager.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                pager.postDelayed(advance, AUTO_CYCLE_DELAY_MS);
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
                pager.removeCallbacks(advance);
            }
        });

        if (pager.isAttachedToWindow()) pager.postDelayed(advance, AUTO_CYCLE_DELAY_MS);
    }
}
