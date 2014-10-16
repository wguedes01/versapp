package com.versapp.connection;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * Created by william on 21/09/14.
 */
public class CredentialsManager {

    private static final String CREDENTIALS = "CREDENTIALS";
    private static final String USERNAME_KEY = "USERNAME_KEY";
    private static final String PASSWORD_KEY = "PASSWORD_KEY";

    private static CredentialsManager instance;
    private Context context;
    private SharedPreferences preferences;

    private CredentialsManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(CREDENTIALS, Context.MODE_PRIVATE);
    }


    public static CredentialsManager getInstance(Context context) {

        if (instance == null) {
            instance = new CredentialsManager(context);
        }

        return instance;
    }

    public void setValidCredentials(String username, String password){
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(USERNAME_KEY, username);
        edit.putString(PASSWORD_KEY, password);
        edit.commit();
    }

    public void unsetValidCredentials(){
        SharedPreferences.Editor edit = preferences.edit();
        edit.clear();
        edit.commit();
    }

    public String getValidUsername(){
        return preferences.getString(USERNAME_KEY, null);
    }

    public String getValidPassword(){
        return preferences.getString(PASSWORD_KEY, null);
    }

    public void forgotPassword(String email){

        Ion.with(context).load(ConnectionManager.FORGOT_PASSWORD_URL + email) // url/email
        .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {

                    if (e == null){
                        Toast.makeText(context, "Reset password email sent.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Oops.. something went wrong. Try again.", Toast.LENGTH_SHORT).show();
                    }

                }
            });

    }

}
