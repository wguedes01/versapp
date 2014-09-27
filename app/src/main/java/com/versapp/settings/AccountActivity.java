package com.versapp.settings;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.versapp.R;
import com.versapp.UserInfoManager;
import com.versapp.connection.ConnectionService;

public class AccountActivity extends Activity {

    TextView usernameTextView;
    TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        usernameTextView = (TextView) findViewById(R.id.activity_account_username);
        emailTextView = (TextView) findViewById(R.id.activity_account_email);

        usernameTextView.setText(ConnectionService.getUser());

        new AsyncTask<Void, Void, String>(){

            @Override
            protected String doInBackground(Void... params) {
                return UserInfoManager.getInstance().getEmail();
            }

            @Override
            protected void onPostExecute(String s) {
                emailTextView.setText(s);
                super.onPostExecute(s);
            }
        }.execute();
    }
}
