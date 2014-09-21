package com.versapp.connection;

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
    public static final String VERIFY_PHONE_URL = "http://harmon.dev.versapp.co/verify/";
    public static final String NUMBER_AVAILABILITY_URL = "http://" + SERVER_IP_ADDRESS + "/validate.php?";




    private static ConnectionManager instance;

    private ConnectionManager() {
    }

    public static ConnectionManager getInstance() {

        if (instance == null){
            instance = new ConnectionManager();
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


}
