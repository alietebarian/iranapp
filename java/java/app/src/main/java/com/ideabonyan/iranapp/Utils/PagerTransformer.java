/*
 * Copyright (C) 2016 .all right reserved for Esfandune.ir & Mohammad Rahmani
 * salam
 * in source ba zahmate faravani tahiye va montasher shode
 * lotfan az enteshare source khoddari farmaeid.
 * in kar be lahaze ghanuni va shar'i gheire ghanuni mibashad
 * lotfan baraye hemayat az ma dustane barnamenevise khod ra be Esfandune.ir hedayat konid.
 * ba tashakor az shoma
 * MOhammad Rahmani
 */

package com.ideabonyan.iranapp.Utils;

import android.support.v4.view.ViewPager;
import android.view.View;

/*
 * by Esfandune.ir(Mohammad Rahmani)
 */


//class nahve tavize page ha dar viewpager
public class PagerTransformer implements ViewPager.PageTransformer {
   public static enum TransformType {
        FLOW,
        DEPTH,
        ZOOM,
        SLIDE_OVER
    }
    private final TransformType mTransformType;

    public PagerTransformer(TransformType transformType) {
        mTransformType = transformType;
    }
    //maghadire pishfarz:ba taghire in ha nahve animation taghire peida mikonad
    private static final float MIN_SCALE_DEPTH = 0.75f;
    private static final float MIN_SCALE_ZOOM = 0.85f;
    private static final float MIN_ALPHA_ZOOM = 0.5f;
    private static final float SCALE_FACTOR_SLIDE = 0.85f;
    private static final float MIN_ALPHA_SLIDE = 0.35f;

    public void transformPage(View page, float position) {
        final float alpha;
        final float scale;
        final float translationX;

        switch (mTransformType) {
            //halate labela
            case FLOW:
                page.setRotationY(position * -30f);
                return;
            //halate slide
            case SLIDE_OVER:
                if (position < 0 && position > -1) {
                    // safe samte chapi
                    scale = Math.abs(Math.abs(position) - 1) * (1.0f - SCALE_FACTOR_SLIDE) + SCALE_FACTOR_SLIDE;
                    alpha = Math.max(MIN_ALPHA_SLIDE, 1 - Math.abs(position));
                    int pageWidth = page.getWidth();
                    float translateValue = position * -pageWidth;
                    if (translateValue > -pageWidth) {
                        translationX = translateValue;
                    } else {
                        translationX = 0;
                    }
                } else {
                    alpha = 1;
                    scale = 1;
                    translationX = 0;
                }
                break;
            //halate omghi
            case DEPTH:
                if (position > 0 && position < 1) {
                    // harekat be samte rast
                    alpha = (1 - position);
                    scale = MIN_SCALE_DEPTH + (1 - MIN_SCALE_DEPTH) * (1 - Math.abs(position));
                    translationX = (page.getWidth() * -position);
                } else {
                    alpha = 1;
                    scale = 1;
                    translationX = 0;
                }
                break;
            //halate zoom
            case ZOOM:
                if (position >= -1 && position <= 1) {
                    scale = Math.max(MIN_SCALE_ZOOM, 1 - Math.abs(position));
                    alpha = MIN_ALPHA_ZOOM +
                            (scale - MIN_SCALE_ZOOM) / (1 - MIN_SCALE_ZOOM) * (1 - MIN_ALPHA_ZOOM);
                    float vMargin = page.getHeight() * (1 - scale) / 2;
                    float hMargin = page.getWidth() * (1 - scale) / 2;
                    if (position < 0) {
                        translationX = (hMargin - vMargin / 2);
                    } else {
                        translationX = (-hMargin + vMargin / 2);
                    }
                } else {
                    alpha = 1;
                    scale = 1;
                    translationX = 0;
                }
                break;

            default:
                return;
        }

        page.setAlpha(alpha);
        page.setTranslationX(translationX);
        page.setScaleX(scale);
        page.setScaleY(scale);
    }
}
