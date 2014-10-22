package com.versapp;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by william on 20/10/14.
 */
public class MainApplication extends Application {

    private static final String APP_ID = "UA-55948602-1";

    private Tracker tracker;

    public synchronized Tracker getTracker(){

        if (tracker == null){
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker(APP_ID);
        }

        return tracker;
    }


}
