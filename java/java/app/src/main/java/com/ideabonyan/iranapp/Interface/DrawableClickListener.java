package com.ideabonyan.iranapp.Interface;

/**
 * Created by Novin Pendar on 03/23/2017.
 */

public interface DrawableClickListener {

    public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT }
    public void onClick(DrawablePosition target);
}