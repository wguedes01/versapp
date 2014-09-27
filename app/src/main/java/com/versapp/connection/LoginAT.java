package com.versapp.connection;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.versapp.DashboardActivity;
import com.versapp.gcm.GCMDeviceRegistration;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;

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


                connection.login(username, password, "who");

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

                setAllPacketsListener(conn);

                ConnectionService.setConnection(conn);

                if (!GCMDeviceRegistration.isGCMDeviceIdRegistered(context)) {
                    GCMDeviceRegistration.registerDeviceOnBackground(context);
                }


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

    private void setAllPacketsListener(Connection conn) {
        conn.addPacketListener((PacketListener) new PacketListener() {

            @Override
            public void processPacket(Packet packet) {
                Log.d("ALL_PACKETS", packet.toXML());
            }
        }, new PacketFilter() {

            @Override
            public boolean accept(Packet packet) {
                return true;
            }
        });

    }

}
