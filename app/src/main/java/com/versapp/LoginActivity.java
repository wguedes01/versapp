package com.versapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.versapp.connection.LoginAT;
import com.versapp.signup.SignUpNameEmailInputActivity;


public class LoginActivity extends Activity {

    View userPassContainer;
    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username_edit);
        password = (EditText) findViewById(R.id.password_edit);
        userPassContainer = findViewById(R.id.login_and_pass_edit_container);

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    login(password);
                    return true;
                }
                return false;
            }
        });


    }

    public void showLoginFields(View view) {
        userPassContainer.setVisibility(View.VISIBLE);
    }

    public void hideLoginFields(View view) {
        userPassContainer.setVisibility(View.GONE);
    }

    public void login(View view) {

        if (username.getText().toString().length() <= 0) {
            Toast.makeText(this, "Username is empty.", Toast.LENGTH_SHORT).show();
        } else if (password.getText().toString().length() <= 0){
            Toast.makeText(this, "Password is empty.", Toast.LENGTH_SHORT).show();
        } else {
            LoginAT loginAt = new LoginAT(this, null);
            loginAt.execute(username.getText().toString(), password.getText().toString());
        }

    }

    public void signUp(View view) {

        startActivity(new Intent(this, SignUpNameEmailInputActivity.class));

    }

}
