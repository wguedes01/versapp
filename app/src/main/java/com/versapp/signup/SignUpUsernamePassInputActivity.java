package com.versapp.signup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.versapp.DashboardActivity;
import com.versapp.R;
import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;
import com.versapp.connection.LoginAT;
import com.versapp.vcard.VCard;
import com.versapp.vcard.VCardManager;

import org.jivesoftware.smack.XMPPException;


public class SignUpUsernamePassInputActivity extends Activity {

    EditText usernameEdit;
    EditText passwordEdit;
    EditText confirmpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_username_pass_input);

        usernameEdit = (EditText) findViewById(R.id.sign_up_username_edit);
        passwordEdit = (EditText) findViewById(R.id.sign_up_password_edit);
        confirmpassword  = (EditText) findViewById(R.id.sign_up_confirm_password_edit);

        usernameEdit.requestFocus();
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void register(View view) {



        if (!passwordEdit.getText().toString().equals(confirmpassword.getText().toString())){
            Toast.makeText(this, "Password and confirmation do not match", Toast.LENGTH_LONG).show();
        } else if (passwordEdit.getText().toString().length() < 4){
            Toast.makeText(this, "Password must be longer than 3 characters", Toast.LENGTH_LONG).show();
        } else if (usernameEdit.getText().toString().length() < 3) {
            Toast.makeText(this, "Username must be longer than 3 characters", Toast.LENGTH_LONG).show();
        } else {
            RegistrationManager.getInstance(this).storePassword(passwordEdit.getText().toString());
            RegistrationManager.getInstance(this).storeUsername(usernameEdit.getText().toString());

            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        return  RegistrationManager.getInstance(SignUpUsernamePassInputActivity.this).register();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Boolean registered) {

                    if (registered == null) {
                        Toast.makeText(SignUpUsernamePassInputActivity.this, "Internal Error. Failed to Register. Try again.", Toast.LENGTH_SHORT).show();
                    } else if (registered) {

                            new LoginAT(SignUpUsernamePassInputActivity.this, new Runnable() {
                                @Override
                                public void run() {

                                    new AsyncTask<Void, Void, Void>() {
                                        @Override
                                        protected Void doInBackground(Void... params) {


                                            String first = RegistrationManager.getInstance(SignUpUsernamePassInputActivity.this).getName().split(" ")[0];
                                            String last = RegistrationManager.getInstance(SignUpUsernamePassInputActivity.this).getName().split(" ")[1];
                                            String phone = RegistrationManager.getInstance(SignUpUsernamePassInputActivity.this).getPhone();
                                            String email = RegistrationManager.getInstance(SignUpUsernamePassInputActivity.this).getEmail();

                                            VCardManager.createVCard(new VCard(first, last));


                                            String packetId = "user_info";
                                            String xml = "<iq id='" + packetId + "' type='set' to='" + ConnectionManager.SERVER_IP_ADDRESS
                                                    + "'><query xmlns='who:iq:info'><ccode>" + "1" + "</ccode><phone>" + phone + "</phone><email>" + email
                                                    + "</email></query></iq>";
                                            System.out.println("INFO: " + ConnectionService.sendCustomXMLPacket(xml, packetId));


                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Void aVoid) {
                                            super.onPostExecute(aVoid);

                                            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));

                                        }
                                    }.execute();


                                }
                            }).execute(usernameEdit.getText().toString(), passwordEdit.getText().toString());


                        } else {
                        // failed to register.
                        Toast.makeText(SignUpUsernamePassInputActivity.this, "Username is taken.", Toast.LENGTH_SHORT).show();
                    }


                    super.onPostExecute(registered);
                }
            }.execute();

        }


    }

}
