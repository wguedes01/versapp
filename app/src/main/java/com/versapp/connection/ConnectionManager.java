package com.versapp.connection;

import android.content.Context;
import android.content.Intent;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by william on 20/09/14.
 */
public class ConnectionManager {

    public static final String MODE = "dev";
    public static String HTTP_PROTOCOL = "http";
    public static String SERVER_IP_ADDRESS = "harmon.dev.versapp.co";
    public static final String CONFERENCE_IP_ADDRESS = "conference." + SERVER_IP_ADDRESS;
    public static final int PORT = 5222;
    public static final int NODE_PORT = 8052;
    public static final String VERIFY_PHONE_URL = "http://harmon.dev.versapp.co/verify/";
    public static final String NUMBER_AVAILABILITY_URL = "http://" + SERVER_IP_ADDRESS + "/validate.php?";

    private Context context;
    private static ConnectionManager instance;

    private ConnectionManager(Context context) {
        this.context = context;
    }

    public static ConnectionManager getInstance(Context context) {

        if (instance == null){
            instance = new ConnectionManager(context);
        }

        return instance;
    }

    public Connection anonymousLogin() throws XMPPException {

        ConnectionConfiguration config = new ConnectionConfiguration(SERVER_IP_ADDRESS, PORT);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

        Connection conn = new XMPPConnection(config);

        conn.connect();

        return conn;

    }

    public void logout(){
        CredentialsManager.getInstance(context).unsetValidCredentials();
        ConnectionService.getConnection().disconnect();
        context.stopService(new Intent(context, ConnectionService.class));
    }

}
