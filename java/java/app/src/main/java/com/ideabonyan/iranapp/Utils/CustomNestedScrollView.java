package com.ideabonyan.iranapp.Utils;

import android.content.Context;
import androidx.core.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by AliReza on 2017/07/18.
 */
public class CustomNestedScrollView extends NestedScrollView {

    public CustomNestedScrollView(Context context) {
        super(context);
    }

    public CustomNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed){
        dispatchNestedPreScroll(dx,dy,consumed,null);
    }
}