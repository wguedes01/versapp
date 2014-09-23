package com.versapp.confessions;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jivesoftware.smack.util.Base64Encoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by william on 20/09/14.
 */
public class ConfessionManager {

    public static final String CONFESSIONS_URL = "http://"+ConnectionManager.SERVER_IP_ADDRESS+":8052/thoughts";

    private static ConfessionManager  instance;

    private ConfessionManager() {
    }

    public static ConfessionManager getInstance() {

        if (instance == null){
            instance = new ConfessionManager();
        }

        return instance;
    }

    protected Confession[] downloadConfessions(Context context){

        Confession[] confessions = null;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Confession.class, new ConfessionDeserializer());
        Gson gson = gsonBuilder.create();

        try {

            InputStream in = makeHttpRequest(context);

            Reader reader = new InputStreamReader(in);

            confessions = gson.fromJson(reader, Confession[].class);

        } catch (IOException e) {
            e.printStackTrace();
        }


        for(Confession c : confessions){
            System.out.println(c.toString());
        }

        return confessions;
    }

    private InputStream makeHttpRequest(Context context) throws IOException {


            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(CONFESSIONS_URL);

            String sessionId = ConnectionService.getSessionId();

            System.out.println("Session Id: " + sessionId);

            String encoding = Base64Encoder.getInstance().encode(String.format("%s:%s", ConnectionService.getUser(), sessionId));


            httpGet.setHeader("Authorization", "Basic " + encoding);

            HttpResponse res = httpClient.execute(httpGet);
            HttpEntity entity = res.getEntity();


            InputStream in = entity.getContent();

            StringBuilder sb = new StringBuilder();
/*
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(in), 5);

            String line = null;

            while((line = reader.readLine()) != null){
                sb.append(line);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("CONTENT: " + sb.toString());
*/
           return in;


        //return context.getResources().openRawResource(R.raw.confessions_input);

    }

    private void makeHttpsRequest(){
/*
        URL url = new URL(THOUGHTS_URL_SECURE);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        // Create the SSL connection
        SSLContext sc;
        sc = SSLContext.getInstance("TLS");
        sc.init(null, null, new java.security.SecureRandom());
        conn.setSSLSocketFactory(sc.getSocketFactory());

        // Use this if you need SSL authentication
        String userpass = ConnectionService.user + ":" + ConnectionService.getSessionId();
        String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
        conn.setRequestProperty("Authorization", basicAuth);

        String postParameters = "method=" + degree + "&since=" + String.valueOf(since);

        // set Timeout and method
        conn.setReadTimeout(7000);
        conn.setConnectTimeout(7000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        conn.setFixedLengthStreamingMode(postParameters.length());
        conn.setDoOutput(true);
        conn.setDoInput(true);

        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print(postParameters);
        out.close();
        conn.connect();

        int responseCode = conn.getResponseCode();
        System.out.println(responseCode);

        is = conn.getInputStream();
        StringBuffer sb = new StringBuffer();
        int ch;
        while ((ch = is.read()) != -1) {
            sb.append((char) ch);
        }
        response = sb.toString();

        conn.disconnect();
*/
    }


}
