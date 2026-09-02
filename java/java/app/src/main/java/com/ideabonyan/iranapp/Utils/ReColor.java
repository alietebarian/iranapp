package com.ideabonyan.iranapp.Utils;

import android.graphics.Color;
import android.os.Handler;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ReColor {


    View viewBackground;
    String startingColor, endingColor;
    int duration, stepCount;
    List<String> colorArray;

    public void viewBackground(View view, String startingColor, String endingColor, int duration){

        viewBackground = view;
        this.startingColor = startingColor;
        this.endingColor = endingColor;
        this.duration = duration;
        stepCount = duration / 10;
        colorArray = getColorArray(startingColor, endingColor, stepCount);
        viewBackgroundTimerHandler.postDelayed(viewBackgroundTimerRunnable, 0);
    }



    private final Handler viewBackgroundTimerHandler = new Handler();
    int stepsPassed = 0;
    private Runnable viewBackgroundTimerRunnable = new Runnable() {

        @Override
        public void run() {
            ///////////////////////////

            viewBackground.setBackgroundColor(Color.parseColor(colorArray.get(stepsPassed)));
            stepsPassed++;

            /////////////////////////
            viewBackgroundTimerHandler.postDelayed(this, 10);
            if (stepsPassed == colorArray.size() - 1) viewBackgroundTimerHandler.removeCallbacksAndMessages(null);

//            Log.i("1111111111", stepsPassed + "   " + colorArray.get(stepsPassed));
        }
    };



    ////////////////////////////////////////////////////////////////////////////////////////////////
    private List<String> getColorArray(String startingColor, String endingColor, int stepCount) {
        List<String> colorArray = new ArrayList<>();
//        String[] startColorArray = startingColor.split("(?<=\\G.{3})"); // EXPLANATION BELOW // doesn't work correctly
//        String[] endColorArray = endingColor.split("(?<=\\G.{3})");
        List<String> startColorArray = getParts(startingColor, 2);
        List<String> endColorArray = getParts(endingColor, 2);


        int[] startColorInIntArray = new int[3];
        int[] endColorInIntArray = new int[3];

        startColorInIntArray[0] = Integer.parseInt(startColorArray.get(0), 16);
        startColorInIntArray[1] = Integer.parseInt(startColorArray.get(1), 16);
        startColorInIntArray[2] = Integer.parseInt(startColorArray.get(2), 16);
        endColorInIntArray[0] = Integer.parseInt(endColorArray.get(0), 16);
        endColorInIntArray[1] = Integer.parseInt(endColorArray.get(1), 16);
        endColorInIntArray[2] = Integer.parseInt(endColorArray.get(2), 16);

        int[] stepSizeForEachColor = new int[3];
        stepSizeForEachColor[0] = (endColorInIntArray[0] - startColorInIntArray[0]) / stepCount;
        stepSizeForEachColor[1] = (endColorInIntArray[1] - startColorInIntArray[1]) / stepCount;
        stepSizeForEachColor[2] = (endColorInIntArray[2] - startColorInIntArray[2]) / stepCount;

        for (int i = 1; i < stepCount; i++){
            String color1 = Integer.toHexString(startColorInIntArray[0] + (stepSizeForEachColor[0] * i));
            String color2 = Integer.toHexString(startColorInIntArray[1] + (stepSizeForEachColor[1] * i));
            String color3 = Integer.toHexString(startColorInIntArray[2] + (stepSizeForEachColor[2] * i));

            colorArray.add("#" + color1 + color2 + color3);
        }

        colorArray.add("#" + endingColor);
//        Log.i("1111111111", endingColor);


        return colorArray;
    }


    private static List<String> getParts(String string, int partitionSize) {
        List<String> parts = new ArrayList<String>();
        int len = string.length();
        for (int i=0; i<len; i+=partitionSize)
        {
            parts.add(string.substring(i, Math.min(len, i + partitionSize)));
        }
        return parts;
    }

// string.split("(?<=\\G...)")   ==> REGEX mojo for getting an [] array of the the string splitted on every 3 (number of dots after G) characters
// FROM WRITER => The regex (?<=\G...) matches an empty string that has the last match (\G) followed by three characters (...) before it ((?<= ))
}
