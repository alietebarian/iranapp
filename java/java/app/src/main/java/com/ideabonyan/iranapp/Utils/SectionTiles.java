package com.ideabonyan.iranapp.Utils;

import android.view.View;
import android.widget.TextView;

import com.ideabonyan.iranapp.Components.GradientIconView;
import com.ideabonyan.iranapp.R;

/**
 * Fills the tiles of the املاک / وسایل نقلیه / استخدام pages (layouts item_section_tile and
 * item_section_pro_tile, included under the id each fragment clicks on).
 */
public final class SectionTiles {

    private SectionTiles() {
    }

    /** @param subtitle a short second line under the title, or null for none */
    public static void bind(View root, int tileId, int icon, String title, String subtitle) {
        View tile = root.findViewById(tileId);
        ((GradientIconView) tile.findViewById(R.id.sectionTileIcon)).setImageResource(icon);
        ((TextView) tile.findViewById(R.id.sectionTileName)).setText(title);

        TextView subtitleView = (TextView) tile.findViewById(R.id.sectionTileSubtitle);
        subtitleView.setText(subtitle);
        subtitleView.setVisibility(subtitle != null ? View.VISIBLE : View.GONE);
    }

    /** The second line of the «کاربران پرو» row, e.g. "آگهی های املاک کاربران پرو". */
    public static void bindPro(View root, String subtitle) {
        ((TextView) root.findViewById(R.id.sectionProSubtitle)).setText(subtitle);
    }
}
