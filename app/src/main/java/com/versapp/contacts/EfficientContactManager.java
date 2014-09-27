package com.versapp.contacts;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.jivesoftware.smack.util.Base64Encoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by william on 25/09/14.
 */
public class EfficientContactManager {


    private static final String BLACK_LIST_URL =  ConnectionManager.HTTP_PROTOCOL+"://"+ConnectionManager.SERVER_IP_ADDRESS+":"+ConnectionManager.NODE_PORT+"/blacklist";
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

        String[] phones = new String[phonesCursor.getCount()];
        int phoneIndex = 0;

        while (phonesCursor.moveToNext()) {
            String phone = phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phone = phone.replaceAll("\\D+", "");
            phones[phoneIndex] = phone;
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

        //String[] phones = getPhones();
        //String[] emails = getEmails();

        String[] phones = {"5745142948", "1111111111", "15745142948", "11111111111"};
        String[] emails = {};

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://harmon.dev.versapp.co:8052/blacklist");

        String sessionId = ConnectionService.getSessionId();

        System.out.println("Session Id: " + sessionId);

        String encoding = Base64Encoder.getInstance().encode(String.format("%s:%s", ConnectionService.getUser(), sessionId));

        httpPost.setHeader("Authorization", "Basic " + encoding);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("phones", new JSONArray(Arrays.asList(phones)));
        jsonObj.put("emails", new JSONArray(Arrays.asList(emails)));

        StringEntity e = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
        e.setContentType("application/json");
        httpPost.setEntity(e);

        HttpResponse res = httpClient.execute(httpPost);
        HttpEntity entity = res.getEntity();

        if (res.getStatusLine().getStatusCode() == 200){
            setContactsPushedToServer();
        }

    }

    private void setContactsPushedToServer(){
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(BLACK_LIST_MODEL_IS_PUBLISHED_KEY, true);
        edit.commit();
    }

    public boolean areContactsPublished(){
        return preferences.getBoolean(BLACK_LIST_MODEL_IS_PUBLISHED_KEY, false);
    }
}
