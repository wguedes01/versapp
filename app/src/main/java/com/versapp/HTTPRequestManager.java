package com.versapp;

import android.util.Base64;

import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.jivesoftware.smack.util.Base64Encoder;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by william on 24/09/14.
 */
public class HTTPRequestManager {

    private static HTTPRequestManager instance;

    private HTTPRequestManager() {
    }

    public static HTTPRequestManager getInstance() {

        if (instance == null) {
            instance = new HTTPRequestManager();
        }

        return instance;
    }

    public InputStream sendSimpleHttpRequestDEPRICATED(String url) throws IOException {

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        String sessionId = ConnectionService.getSessionId();

        System.out.println("Session Id: " + sessionId);

        String encoding = Base64Encoder.getInstance().encode(String.format("%s:%s", ConnectionService.getUser(), sessionId));

        httpGet.setHeader("Authorization", "Basic " + encoding);



        HttpResponse res = httpClient.execute(httpGet);
        HttpEntity entity = res.getEntity();

        if (res.getStatusLine().getStatusCode() == 200){
            return entity.getContent();
        }

        return null;
    }

    public InputStream simpleHTTPPostDepricated(String url, StringEntity stringEntity) throws IOException {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        stringEntity.setContentType("application/json");
        httpPost.setEntity(stringEntity);

        String encoding = Base64Encoder.getInstance().encode(String.format("%s:%s", ConnectionService.getUser(), ConnectionService.getSessionId()));
        httpPost.setHeader("Authorization", "Basic " + encoding);

        HttpResponse res = httpClient.execute(httpPost);
        HttpEntity entity = res.getEntity();

        int code = res.getStatusLine().getStatusCode();
        if (code == 200) {
            InputStream in = entity.getContent();
            return in;
        } else {
            return null;
        }

    }

    public InputStream simpleHTTPSPost(String urlString, JSONObject jsonObject) throws IOException {

        if (ConnectionManager.MODE.equals("dev")){
            StringEntity e = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
            return simpleHTTPPostDepricated(urlString, e);
        }

        URL url = new URL(urlString);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json");

        con.setRequestMethod("POST");

        String encoding = Base64Encoder.getInstance().encode(String.format("%s:%s", ConnectionService.getUser(), ConnectionService.getSessionId()));
        con.setRequestProperty("Authorization", "Basic " + encoding);

        OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());

        wr.write(jsonObject.toString());

        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        if (responseCode    == 200){
            return con.getInputStream();
        } else {
            return null;
        }

    }

    public InputStream sendSimpleHttpsRequest(String urlString) {

        if (ConnectionManager.MODE.equals("dev")){
            try {
                return sendSimpleHttpRequestDEPRICATED(urlString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String https_url = urlString;
        URL url;
        try {

            url = new URL(https_url);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

            String userpass = ConnectionService.getUser() + ":" + ConnectionService.getSessionId();
            String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
            con.setRequestProperty("Authorization", basicAuth);

            return con.getInputStream();

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
