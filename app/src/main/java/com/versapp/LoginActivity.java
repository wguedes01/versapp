package com.versapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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


        if (CredentialsManager.getInstance(getApplicationContext()).getValidUsername() != null) {

            login(CredentialsManager.getInstance(getApplicationContext()).getValidUsername(), CredentialsManager.getInstance(getApplicationContext()).getValidPassword());

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
        startActivity(new Intent(getApplicationContext(), SignUpNameEmailInputActivity.class));
    }

    private void login(String username, String password){
        LoginAT loginAt = new LoginAT(getApplicationContext(), null, progressBar, loginCover);
        loginAt.execute(username, password);
    }

    public void forgotPassword(View view){

        final EditText emailEdit = new EditText(LoginActivity.this);

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(emailEdit);
        emailEdit.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter email used for your account.");
        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CredentialsManager.getInstance(getApplicationContext()).forgotPassword(emailEdit.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
