package com.versapp.connection;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.jivesoftware.smack.Connection;

/**
 * Created by william on 20/09/14.
 */
public class ConnectionService extends Service {

    private static Connection connection;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Connection getConnection(){
        return connection;
    }

    public static void setConnection(Connection conn) {
        connection = conn;
    }
}
