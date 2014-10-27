package com.versapp.connection;

import android.content.Context;
import android.content.Intent;

import com.versapp.Environments;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;

/**
 * Created by william on 20/09/14.
 */
public class ConnectionManager {

    public static String MODE = Environments.MODE;
    public static String HTTP_PROTOCOL = Environments.HTTP_PROTOCOL;
    public static String SERVER_IP_ADDRESS = Environments.SERVER_IP_ADDRESS;
    public static final int PORT = Environments.PORT;
    public static final int NODE_PORT = Environments.NODE_PORT;
    public static final String VERIFY_PHONE_URL = Environments.VERIFY_PHONE_URL;
    public static final String NUMBER_AVAILABILITY_URL = Environments.NUMBER_AVAILABILITY_URL;
    public static final String FORGOT_PASSWORD_URL = Environments.FORGOT_PASSWORD_URL;

    // Thoughts
    public static final String CONFESSIONS_URL =  Environments.CONFESSIONS_URL;
    public static final String GET_SINGLE_CONFESSION_URL =  Environments.GET_SINGLE_CONFESSION_URL;

    // Friends
    public static final String FRIEND_URL = Environments.FRIEND_URL;
    public static final String PENDING_FRIEND_URL = Environments.PENDING_FRIEND_URL;

    // Chats
    public static final String CREATE_CHAT_URL = Environments.CREATE_CHAT_URL;
    public static final String JOINED_CHATS_URL = Environments.JOINED_CHATS_URL;
    public static final String PENDING_CHATS_URL = Environments.PENDING_CHATS_URL;
    public static final String JOIN_CHAT_URL = Environments.JOIN_CHAT_URL;
    public static final String LEAVE_CHAT_URL = Environments.LEAVE_CHAT_URL;

    private Context context;
    private static ConnectionManager instance;

    private ConnectionManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static ConnectionManager getInstance(Context context) {

        if (instance == null){
            instance = new ConnectionManager(context);
        }

        return instance;
    }

    public XMPPTCPConnection anonymousLogin() throws XMPPException, IOException, SmackException {

        ConnectionConfiguration config = new ConnectionConfiguration(SERVER_IP_ADDRESS, PORT);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

        XMPPTCPConnection conn = new XMPPTCPConnection(config);

        conn.connect();

        return conn;

    }

    public void logout() throws SmackException.NotConnectedException {
        CredentialsManager.getInstance(context).unsetValidCredentials();
        ConnectionService.getConnection().disconnect();
        context.stopService(new Intent(context, ConnectionService.class));
    }

}
