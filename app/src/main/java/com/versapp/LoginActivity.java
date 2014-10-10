package com.versapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.versapp.connection.CredentialsManager;
import com.versapp.connection.LoginAT;
import com.versapp.signup.SignUpNameEmailInputActivity;


public class LoginActivity extends Activity {

    View userPassContainer;
    EditText usernameEdit;
    EditText passwordEdit;

    ProgressBar progressBar;

    private View loginCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = (ProgressBar) findViewById(R.id.activity_login_progress_bar);

        loginCover = findViewById(R.id.login_cover);


        if (CredentialsManager.getInstance(this).getValidUsername() != null) {

            login(CredentialsManager.getInstance(this).getValidUsername(), CredentialsManager.getInstance(this).getValidPassword());

        } else {
            loginCover.setVisibility(View.GONE);

            usernameEdit = (EditText) findViewById(R.id.username_edit);
            passwordEdit = (EditText) findViewById(R.id.password_edit);
            userPassContainer = findViewById(R.id.login_and_pass_edit_container);

            passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_GO) {
                        login(passwordEdit);
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        return true;
                    }
                    return false;
                }
            });

        }


    }

    public void showLoginFields(View view) {
        userPassContainer.setVisibility(View.VISIBLE);

        usernameEdit.requestFocus();
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    public void hideLoginFields(View view) {
        userPassContainer.setVisibility(View.GONE);
    }

    public void login(View view) {

        if (usernameEdit.getText().toString().length() <= 0) {
            Toast.makeText(this, "Username is empty.", Toast.LENGTH_SHORT).show();
        } else if (passwordEdit.getText().toString().length() <= 0){
            Toast.makeText(this, "Password is empty.", Toast.LENGTH_SHORT).show();
        } else {
            login(usernameEdit.getText().toString(), passwordEdit.getText().toString());
        }

    }

    public void signUp(View view) {

        startActivity(new Intent(this, SignUpNameEmailInputActivity.class));

    }

    private void login(String username, String password){

        LoginAT loginAt = new LoginAT(this, null, progressBar, loginCover);
        loginAt.execute(username, password);
    }

}
