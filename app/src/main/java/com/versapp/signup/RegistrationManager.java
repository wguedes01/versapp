package com.versapp.signup;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by william on 21/09/14.
 */
public class RegistrationManager {

    private static final String REGISTRATION_INFO = "REGISTRATION_INFO";
    private static final String REGISTRATION_INFO_NAME_KEY = "REGISTRATION_INFO_NAME_KEY";
    private static final String REGISTRATION_INFO_USERNAME_KEY = "REGISTRATION_INFO_USERNAME_KEY";
    private static final String REGISTRATION_INFO_PASSWORD_KEY = "REGISTRATION_INFO_PASSWORD_KEY";
    private static final String REGISTRATION_INFO_PHONE_KEY = "REGISTRATION_INFO_PHONE_KEY";
    private static final String REGISTRATION_INFO_EMAIL_KEY = "REGISTRATION_INFO_EMAIL_KEY";
    private static final String REGISTRATION_INFO_RANDOM_CODE_KEY = "REGISTRATION_INFO_RANDOM_CODE_KEY";

    private Context context;
    private static RegistrationManager instance;
    private SharedPreferences preferences;


    private RegistrationManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(REGISTRATION_INFO, Context.MODE_PRIVATE);
    }

    public static RegistrationManager getInstance(Context context) {

        if (instance == null){
            instance = new RegistrationManager(context);
        }

        return instance;
    }

    public void storeName(String name) {storeStringData(REGISTRATION_INFO_NAME_KEY, name);}
    public void storePhone(String phone) {storeStringData(REGISTRATION_INFO_PHONE_KEY, phone);}
    public void storeUsername(String username) {storeStringData(REGISTRATION_INFO_USERNAME_KEY, username);}
    public void storePassword(String password) {storeStringData(REGISTRATION_INFO_PASSWORD_KEY, password);}
    public void storeEmail(String email) {storeStringData(REGISTRATION_INFO_EMAIL_KEY, email);}
    public void storeVerificationCode(String code) {storeStringData(REGISTRATION_INFO_RANDOM_CODE_KEY, code);}

    public String getPhone(){return preferences.getString(REGISTRATION_INFO_PHONE_KEY, null);}
    public String getName(){return preferences.getString(REGISTRATION_INFO_NAME_KEY, null);}
    public String getEmail(){return preferences.getString(REGISTRATION_INFO_EMAIL_KEY, null);}
    public String getUsername(){return preferences.getString(REGISTRATION_INFO_USERNAME_KEY, null);}
    public String getPassword(){return preferences.getString(REGISTRATION_INFO_PASSWORD_KEY, null);}
    public String getVerificationCode(){return preferences.getString(REGISTRATION_INFO_RANDOM_CODE_KEY, null);}

    private void storeStringData(String key, String value){
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public void sendConfirmationText(){

        String randomCode = generateRandomCode();
        storeVerificationCode(randomCode);

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(ConnectionManager.VERIFY_PHONE_URL);

            List<NameValuePair> postParameters = new ArrayList<NameValuePair>(2);
            postParameters.add(new BasicNameValuePair("country", "1"));
            postParameters.add(new BasicNameValuePair("phone", getPhone()));
            postParameters.add(new BasicNameValuePair("code", randomCode));

            httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));


            HttpResponse res = httpClient.execute(httpPost);
            HttpEntity entity = res.getEntity();

            int responseCode = res.getStatusLine().getStatusCode();

            if (responseCode == 200){

            } else {
                // bad, make this more specific.
                throw new IOException();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private String generateRandomCode(){
        return String.valueOf((int) (Math.random() * 1000));
    }

    /**
     * Once user successfully registers, we need to remove the verification code.
     */
    private void expireCode(){
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove(REGISTRATION_INFO_RANDOM_CODE_KEY);
    }

    public boolean register() throws XMPPException {

        String packetID = "register";

        StringBuilder packet = new StringBuilder();

        packet.append(String.format("<iq id='%s' type='set' to='%s'>", packetID, ConnectionManager.SERVER_IP_ADDRESS));
        packet.append("<query xmlns='jabber:iq:register'>");
        packet.append(String.format("<username>%s</username>", getUsername()));
        packet.append(String.format("<password>%s</password>", getPassword()));
        packet.append("</query>");
        packet.append("</iq>");

        String response = null;
        try {
            response = ConnectionService.sendUnauthenticatedCustomXMLPacket(packet.toString(), packetID, ConnectionManager.getInstance(context).anonymousLogin());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }
        Log.d("Registration", response);

        // THIS IS A HACK.
        if (response.contains("type='result'") || response.contains("type=\"result\"")) {
            return true;
        }
        return false;
    }



    public boolean isPhoneAvailable(String phone) throws IOException {

        String url = ConnectionManager.NUMBER_AVAILABILITY_URL + "ccode=" + "1" + "&phone=" + phone;

        URL obj = null;

        try {
            obj = new URL(url);

            HttpURLConnection con;
            con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            // add request header
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = con.getResponseCode();

            return (responseCode == 200 ? true : false);


        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }

    }

}
