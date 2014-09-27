package com.versapp;

import com.versapp.connection.ConnectionService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jivesoftware.smack.util.Base64Encoder;

import java.io.IOException;
import java.io.InputStream;

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

    public InputStream sendSimpleHttpRequest(String url) throws IOException {

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

    public InputStream simpleHTTPPost(String url, StringEntity stringEntity) throws IOException {

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
}
