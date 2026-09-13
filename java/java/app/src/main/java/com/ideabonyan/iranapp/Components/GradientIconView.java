package com.ideabonyan.iranapp.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.ideabonyan.iranapp.R;

/**
 * Icon of a category tile on the landing page. Uses its drawable only as a mask and fills it with
 * a diagonal gradient, white at the top left fading into the accent red at the bottom right
 * (home_icon_start / home_icon_end), so the whole grid shares one look.
 *
 * Only the alpha of the drawable counts, so the colour an icon was drawn in does not matter. Turn
 * the gradient off for images that carry their own colours, like a thumbnail uploaded by an admin.
 */
public class GradientIconView extends androidx.appcompat.widget.AppCompatImageView {

    private final Paint gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean gradientEnabled = true;

    public GradientIconView(Context context) {
        super(context);
        init();
    }

    public GradientIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GradientIconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        gradientPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    public void setGradientEnabled(boolean enabled) {
        if (gradientEnabled == enabled) return;
        gradientEnabled = enabled;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        gradientPaint.setShader(new LinearGradient(0, 0, w, h,
                ContextCompat.getColor(getContext(), R.color.home_icon_start),
                ContextCompat.getColor(getContext(), R.color.home_icon_end),
                Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!gradientEnabled || getDrawable() == null) {
            super.onDraw(canvas);
            return;
        }

        // Draw the icon into its own layer, then keep the gradient only where the icon is opaque.
        int layer = canvas.saveLayer(0, 0, getWidth(), getHeight(), null);
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), gradientPaint);
        canvas.restoreToCount(layer);
    }
}
