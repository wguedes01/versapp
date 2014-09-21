package com.versapp.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.versapp.R;

public class VerificationCodeInputActivity extends Activity {

    EditText codeEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code_input);

        codeEdit = (EditText) findViewById(R.id.sign_up_verification_code_edit);
    }

    public void verify(View view) {

        if (codeEdit.getText().toString().equals(RegistrationManager.getInstance(this).getVerificationCode())){

            startActivity(new Intent(this, SignUpUsernamePassInputActivity.class));

        } else {
            Toast.makeText(this, "Invalid Code", Toast.LENGTH_SHORT).show();
        }

    }

}
