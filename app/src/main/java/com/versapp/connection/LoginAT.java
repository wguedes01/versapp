package com.versapp.connection;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.versapp.DashboardActivity;

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
    private Runnable postExecute;

    public LoginAT(Context context, Runnable run) {
        this.context = context;
        this.postExecute = run;
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


                connection.login(username, password);

                if (connection.isAuthenticated()){
                    CredentialsManager.getInstance(context).setValidCredentials(username, password);
                }

                return connection;

        } catch (XMPPException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Connection conn) {

        if (conn != null){

            if (conn.isAuthenticated()){

                context.startService(new Intent(context, ConnectionService.class));
                ConnectionService.setConnection(conn);


                if (postExecute != null) {

                    postExecute.run();

                } else {
                    context.startActivity(new Intent(context, DashboardActivity.class));
                }

            } else {

                Toast.makeText(context, "Invalid username or password.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(context, "Sorry, we are having some technical issues. Try again soon!", Toast.LENGTH_LONG).show();
        }

        super.onPostExecute(conn);
    }

}
