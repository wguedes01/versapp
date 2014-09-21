package com.versapp.connection;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.versapp.DashboardActivity;
import com.versapp.Logger;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by william on 20/09/14.
 */
public class LoginAT extends AsyncTask<String, Void, Connection>{


    private Context context;

    public LoginAT(Context context) {
        this.context = context;
    }

    @Override
    protected Connection doInBackground(String... strings) {

        String username = strings[0];
        String password = strings[1];

        SASLAuthentication.supportSASLMechanism("PLAIN", 0);

        ConnectionConfiguration config = new ConnectionConfiguration(ConnectionManager.SERVER_IP_ADDRESS, ConnectionManager.PORT);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setCompressionEnabled(true);
        config.setSASLAuthenticationEnabled(true);

        Connection connection = new XMPPConnection(config);


        try {
            connection.connect();
            connection.addConnectionListener(new ConnectionListener());

            if (connection.isConnected()){
                connection.login(username, password);
                return connection;
            } else {
                // Failed to connect
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Connection conn) {

        if (conn != null){
            context.startService(new Intent(context, ConnectionService.class));
            ConnectionService.setConnection(conn);

            Log.d(Logger.CONNECTION_DEBUG, "Session Id: " + ConnectionService.getSessionId());
            context.startActivity(new Intent(context, DashboardActivity.class));
        }

        super.onPostExecute(conn);
    }
}
