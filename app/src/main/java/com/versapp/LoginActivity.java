package com.versapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.versapp.connection.LoginAT;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {

        LoginAT loginAt = new LoginAT(this);
        loginAt.execute("will", "111111");

    }
}
