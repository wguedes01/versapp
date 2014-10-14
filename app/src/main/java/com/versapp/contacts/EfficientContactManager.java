package com.versapp.contacts;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.versapp.Environments;
import com.versapp.HTTPRequestManager;
import com.versapp.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by william on 25/09/14.
 */
public class EfficientContactManager {

    private static final String BLACK_LIST_MODEL_FILE = "black_list_model";
    private static final String BLACK_LIST_MODEL_IS_PUBLISHED_KEY = "is_published_key";

    private Context context;
    private SharedPreferences preferences;
    private static EfficientContactManager instance;


    private EfficientContactManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(BLACK_LIST_MODEL_FILE, Context.MODE_PRIVATE);
    }

    public static EfficientContactManager getInstance(Context context) {

        if (instance == null) {
            instance = new EfficientContactManager(context);
        }

        return instance;
    }

    public String[] getPhones() {

        Cursor phonesCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        String[] phones = new String[phonesCursor.getCount()*2]; // multiply by two because we might add a copy with country code.
        int phoneIndex = 0;

        while (phonesCursor.moveToNext()) {
            String phone = phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phone = phone.replaceAll("\\D+", "");
            phones[phoneIndex] = phone;
            phoneIndex++;
            phones[phoneIndex] = "1"+phone;
            phoneIndex++;
        }

        return phones;
    }

    public String[] getEmails(){

        Cursor emailsCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null);

        String[] emails = new String[emailsCursor.getCount()];
        int emailIndex = 0;

        while (emailsCursor.moveToNext()) {
            String email = emailsCursor.getString(emailsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            emails[emailIndex] = email;
            emailIndex++;
        }

        return emails;
    }

    public void publishContacts() throws JSONException, IOException {

        String[] phones = getPhones();
        String[] emails = getEmails();

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("phones", new JSONArray(Arrays.asList(phones)));
        jsonObj.put("emails", new JSONArray(Arrays.asList(emails)));

        //e.setContentType("application/json");

        InputStream in = HTTPRequestManager.getInstance().simpleHTTPSPost(Environments.BLACK_LIST_URL, jsonObj);

        if (in != null){
            setContactsPushedToServer();
        }



    }

    private void setContactsPushedToServer(){
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(BLACK_LIST_MODEL_IS_PUBLISHED_KEY, true);
        edit.commit();
        Logger.log(Logger.BLM_DEBUG, "setContactsPushedToServer()");
    }

    public boolean areContactsPublished(){
        return preferences.getBoolean(BLACK_LIST_MODEL_IS_PUBLISHED_KEY, false);
    }
}
