package com.versapp.signup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.versapp.R;

public class SignUpNameEmailInputActivity extends Activity {

    EditText nameEdit;
    EditText emailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_name_email_input);

        nameEdit = (EditText) findViewById(R.id.sign_up_name_edit);
        emailEdit = (EditText) findViewById(R.id.sign_up_email_edit);

        nameEdit.requestFocus();
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    public void next(View view){

        String name = nameEdit.getText().toString();

        String email = emailEdit.getText().toString();

        if (name.length() <= 0){
            Toast.makeText(this, "Enter a valid name.", Toast.LENGTH_SHORT).show();
        } else if(!name.contains(" ")){
            Toast.makeText(this, "Enter first and last name", Toast.LENGTH_SHORT).show();
        } else if(email.length() <= 0 || !email.contains("@")){
            Toast.makeText(this, "Enter a valid email.", Toast.LENGTH_SHORT).show();
        } else {

            RegistrationManager.getInstance(this).storeName(name);
            RegistrationManager.getInstance(this).storeEmail(email);

            startActivity(new Intent(this, SignUpPhoneVerificationInputActivity.class));
        }

    }
}
