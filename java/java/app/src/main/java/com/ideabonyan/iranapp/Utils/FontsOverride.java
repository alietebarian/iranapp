package com.ideabonyan.iranapp.Utils;

import java.lang.reflect.Field;
import android.content.Context;
import android.graphics.Typeface;

public final class FontsOverride {

//    Context context;
//    String staticTypefaceFieldName;
//    String fontAssetName;
//
//    public FontsOverride(Context context, String staticTypefaceFieldName, String fontAssetName){
//        this.context = context;
//        this.staticTypefaceFieldName = staticTypefaceFieldName;
//        this.fontAssetName = fontAssetName;
//
//
//    }


    public static void setDefaultFont(Context context,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
