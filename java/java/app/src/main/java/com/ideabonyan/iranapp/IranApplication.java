package com.ideabonyan.iranapp;

import android.app.Application;

import com.ideabonyan.iranapp.Utils.AppUserAgent;

/** Runs before any screen or service of the app, whichever one Android starts first. */
public class IranApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppUserAgent.install();
    }
}
