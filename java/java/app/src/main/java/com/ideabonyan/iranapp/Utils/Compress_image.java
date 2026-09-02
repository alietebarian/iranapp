package com.ideabonyan.iranapp.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by SIM on 8/24/2017.
 */

public class Compress_image {
    public static String reductImageSize(String imgName, Bitmap bi) {
        FileOutputStream fo = null;
        String newImagePath = null;
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            // options.inSampleSize = 16;//smart change image dimensions (with ratio)
            Bitmap bitmap = bi;

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) 440, (int) 440, true);//change image dimensions

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);//change resolution keep image dimensions

            // you can create a new file name "test.jpg" in sdcard folder.
            newImagePath = Environment.getExternalStorageDirectory() + File.separator + imgName;
            File f = new File(newImagePath);
            f.createNewFile();
            fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fo != null)
                    fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newImagePath;
    }
}
