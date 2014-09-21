package com.versapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.versapp.connection.LoginAT;
import com.versapp.signup.SignUpNameEmailInputActivity;


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

    public void signUp(View view) {

       startActivity(new Intent(this, SignUpNameEmailInputActivity.class));

    }

}
