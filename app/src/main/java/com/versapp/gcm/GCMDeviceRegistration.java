package com.versapp.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.versapp.Logger;
import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;

import java.io.IOException;

/**
 * Created by william on 25/09/14.
 */
public class GCMDeviceRegistration {

    public static final String SENDER_ID = "630369039619"; // Google ID
    private static final String GCM_ID_PREF_KEY = "deviceId";

    public static void registerDeviceOnBackground(final Context context) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    String deviceId = registerOnGCMServer(context);

                    registerOnRemoteServer(context, deviceId);

                    SharedPreferences prefs = getGCMSharedPreferences(context);
                    prefs.edit().putString(GCM_ID_PREF_KEY, deviceId);
                    prefs.edit().commit();


                    Logger.log(Logger.GCM_DEBUG, "Registered device on GCM and Versapp Server: " + deviceId);
                } catch (IOException e) {
                    Logger.log(Logger.GCM_DEBUG, "UNABLE TO REGISTER DEVICE ON GCM SERVER");
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();

    }

    /**
     * Registers on Google service server.
     *
     * @param context
     * @return device id
     * @throws IOException
     */
    private static String registerOnGCMServer(Context context) throws IOException {
        return GoogleCloudMessaging.getInstance(context).register(SENDER_ID);
    }

    /**
     * Registers on Versapp's server.
     *
     * @param context
     */
    private static void registerOnRemoteServer(Context context, String deviceId) {

        String packetId = "registerId";

        String xml = "<iq id='" + packetId + "' from='" + ConnectionService.getConnection().getUser() + "' to='"
                + ConnectionManager.SERVER_IP_ADDRESS + "' type='set' ><query xmlns='who:iq:token'><token>" + deviceId
                + "</token><type>android</type></query></iq>";

        String response = ConnectionService.sendCustomXMLPacket(xml, packetId);

    }

    public static boolean isGCMDeviceIdRegistered(Context context) {
        return getGCMSharedPreferences(context).contains(GCM_ID_PREF_KEY);
    }

    private static SharedPreferences getGCMSharedPreferences(Context context) {

        return context.getSharedPreferences("gandwill.who.gcm_preferences", Context.MODE_PRIVATE);
    }

}
