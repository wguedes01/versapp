package com.versapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.versapp.connection.CredentialsManager;
import com.versapp.connection.LoginAT;

/**
 * Created by william on 25/09/14.
 */
public class GCMIntentService extends IntentService {

    private static final String PUSH_TYPE_CONFESSION = "confession";
    private static final String PUSH_TYPE_MESSAGE = "message";

    public GCMIntentService() {
        super("GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String pushType = intent.getStringExtra("push_type");

        System.out.print("Extras: ");
        Bundle b = intent.getExtras();

        for (String key : b.keySet()) {
            Object value = b.get(key);
            Log.d(Logger.GCM_DEBUG, String.format("%s %s (%s)", key,
                    value.toString(), value.getClass().getName()));
        }

        if (pushType == null) {

        } else if (pushType.equals(PUSH_TYPE_CONFESSION)) {
            Log.d(Logger.GCM_DEBUG, "Received Confession Push. Type:" + pushType);
        } else if(pushType.equals(PUSH_TYPE_MESSAGE)) {


            if (CredentialsManager.getInstance(getApplicationContext()).getValidUsername() != null){

                String username = CredentialsManager.getInstance(getApplicationContext()).getValidUsername();
                String password = CredentialsManager.getInstance(getApplicationContext()).getValidPassword();

                new LoginAT(getApplicationContext(), new Runnable() {
                    @Override
                    public void run() {

                    }
                }).execute(username, password);

            }



            String body = intent.getStringExtra("body");

            Log.d(Logger.GCM_DEBUG, "Received Message Push. Type:" + pushType + ". Body: " + body);
        } else {
            Log.d(Logger.GCM_DEBUG, "ERROR. INVALID PUSH TYPE. Type: " + pushType);
        }


    }
}
