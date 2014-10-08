package com.versapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.versapp.confessions.ViewSingleConfessionActivity;
import com.versapp.connection.CredentialsManager;
import com.versapp.connection.LoginAT;


public class OpenActivityFromNotification extends Activity {

    private boolean close = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_activity_from_notification);

        final long confessionId = getIntent().getLongExtra(ViewSingleConfessionActivity.CONFESSION_ID_INTENT_EXTRA, -1);

        Log.d(Logger.CONNECTION_DEBUG, "Confession Id" + confessionId);

        String username = CredentialsManager.getInstance(getApplicationContext()).getValidUsername();
        String password = CredentialsManager.getInstance(getApplicationContext()).getValidPassword();

        Log.d(Logger.CONNECTION_DEBUG, "About to login");
        new LoginAT(this, new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(getApplicationContext(), ViewSingleConfessionActivity.class);
                intent.putExtra(ViewSingleConfessionActivity.CONFESSION_ID_INTENT_EXTRA, confessionId);
                startActivity(intent);

                close = true;
            }
        }, null).execute(username, password);
    }

    @Override
    protected void onResume() {

        if (close){
            finish();
        }

        super.onResume();
    }
}
