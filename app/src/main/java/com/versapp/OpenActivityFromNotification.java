package com.versapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.versapp.confessions.ViewSingleConfessionActivity;
import com.versapp.connection.ConnectionService;
import com.versapp.connection.CredentialsManager;
import com.versapp.connection.LoginAT;
import com.versapp.requests.RequestsActivity;


public class OpenActivityFromNotification extends Activity {

    public static final String OPEN_ACTIVITY_INTENT_EXTRA = "OPEN_ACTIVITY_INTENT_EXTRA";
    private boolean close = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_activity_from_notification);

        if (getIntent().getStringExtra(OPEN_ACTIVITY_INTENT_EXTRA).equals(ViewSingleConfessionActivity.class.getName())){

            final long confessionId = getIntent().getLongExtra(ViewSingleConfessionActivity.CONFESSION_ID_INTENT_EXTRA, -1);

            Log.d(Logger.CONNECTION_DEBUG, "Confession Id" + confessionId);

            String username = CredentialsManager.getInstance(getApplicationContext()).getValidUsername();
            String password = CredentialsManager.getInstance(getApplicationContext()).getValidPassword();

            Log.d(Logger.CONNECTION_DEBUG, "About to login");

            if (ConnectionService.getConnection() != null && ConnectionService.getConnection().isAuthenticated()){

                Intent intent = new Intent(getApplicationContext(), ViewSingleConfessionActivity.class);
                intent.putExtra(ViewSingleConfessionActivity.CONFESSION_ID_INTENT_EXTRA, confessionId);
                startActivity(intent);

                close = true;

            } else {

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


        } else if(getIntent().getStringExtra(OPEN_ACTIVITY_INTENT_EXTRA).equals(RequestsActivity.class.getName())){

            String username = CredentialsManager.getInstance(getApplicationContext()).getValidUsername();
            String password = CredentialsManager.getInstance(getApplicationContext()).getValidPassword();

            if (ConnectionService.getConnection() != null && ConnectionService.getConnection().isAuthenticated()){

                Intent intent = new Intent(getApplicationContext(), RequestsActivity.class);
                startActivity(intent);

                close = true;

            } else {

                new LoginAT(this, new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(getApplicationContext(), RequestsActivity.class);
                        startActivity(intent);

                        close = true;
                    }
                }, null).execute(username, password);

            }



        }

    }

    @Override
    protected void onResume() {

        if (close){
            finish();
        }

        super.onResume();
    }
}
