package com.versapp.confessions;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.versapp.HTTPRequestManager;
import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;
import com.versapp.database.ReportedConfessionsDAO;

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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by william on 20/09/14.
 */
public class ConfessionManager {

    private static ConfessionManager  instance;

    private ConfessionManager() {
    }

    public static ConfessionManager getInstance() {

        if (instance == null){
            instance = new ConfessionManager();
        }

        return instance;
    }

    protected Confession[] getConfessions(Context context){

        Confession[] confessions = downloadConfessions(context);

        ArrayList<Long> blockedIds = new ReportedConfessionsDAO(context).getAll();

        if (blockedIds.size() <= 0){
            return confessions;
        } else {

            Confession[] filteredConfessions = new Confession[confessions.length];

            int i = 0;
            while( i < confessions.length){

                if (!blockedIds.contains(confessions[i].getId())){
                    filteredConfessions[filteredConfessions.length] = confessions[i];
                }

                i++;
            }


            return filteredConfessions;
        }
    }

    private Confession[] downloadConfessions(Context context){

        Confession[] confessions = null;

        InputStream in = HTTPRequestManager.getInstance().sendSimpleHttpsRequest(ConnectionManager.CONFESSIONS_URL);
        confessions = deserializeConfessionsStream(in);

        return confessions;
    }

    private InputStream makeHttpRequest(Context context) throws IOException {

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(ConnectionManager.CONFESSIONS_URL);

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

    public Confession createConfession(String body, String imageUrl){


        String packetId = "create_confession";

        String xml = String.format("<iq type='set' to='%s' id='%s' from='%s'><confession xmlns='who:iq:confession'><create><body>%s</body><image_url>%s</image_url></create></confession></iq>",
                ConnectionManager.SERVER_IP_ADDRESS, packetId, ConnectionService.getJid(), URLEncoder.encode(body), imageUrl);

        String response = ConnectionService.sendCustomXMLPacket(xml, packetId);

        Pattern p = Pattern.compile(">(\\d+),(\\d+)<");
        Matcher m = p.matcher(response);

        long id = 0;
        long createdTimestamp = 0;
        if (m.find()) {
            id = Long.valueOf(m.group(1));
            createdTimestamp = Long.valueOf(m.group(2));
            return  new Confession(id, body, createdTimestamp, imageUrl, 0, false, 0);
        }

        return null;
    }

    public Confession getConfessionFromServer(Context context, long id) {

        Confession confession = null;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Confession.class, new ConfessionDeserializer());
        Gson gson = gsonBuilder.create();

        InputStream in = HTTPRequestManager.getInstance().sendSimpleHttpsRequest(ConnectionManager.GET_SINGLE_CONFESSION_URL+"/"+id);

        if (in != null){
            Reader reader = new InputStreamReader(in);

            confession = gson.fromJson(reader, Confession.class);

        } else {
            return null;
        }

        return confession;
    }

    private Confession[] deserializeConfessionsStream(InputStream in){

        if (in != null){
            Reader reader = new InputStreamReader(in);

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Confession.class, new ConfessionDeserializer());
            Gson gson = gsonBuilder.create();

            return gson.fromJson(reader, Confession[].class);
        } else {
            return new Confession[0];
        }

    }

    public Confession[] getDeserializeConfessionsStreamMethod(InputStream in){
        return deserializeConfessionsStream(in);
    }

}
