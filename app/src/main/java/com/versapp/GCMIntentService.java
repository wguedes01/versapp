package com.versapp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by william on 25/09/14.
 */
public class GCMIntentService extends IntentService {

    public GCMIntentService() {
        super("GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(Logger.GCM_DEBUG, "Received GCM....");
    }
}
