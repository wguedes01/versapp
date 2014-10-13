package com.versapp.connection;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.versapp.DashboardActivity;
import com.versapp.chat.ChatMessageListener;
import com.versapp.chat.SynchronizeChatDB;
import com.versapp.contacts.EfficientContactManager;
import com.versapp.friends.FriendRequestListener;
import com.versapp.gcm.GCMDeviceRegistration;

import org.apache.harmony.javax.security.sasl.SaslException;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.json.JSONException;

import java.io.IOException;

/**
 * Created by william on 20/09/14.
 */
public class LoginAT extends AsyncTask<String, Void, XMPPTCPConnection>{


    private Context context;
    private Runnable postExecute;
    private ProgressBar progressBar;
    private View loginCover;

    private int errorCode = 0;


    public LoginAT(Context context, Runnable run, ProgressBar progressBar, View loginCover) {
        this.context = context;
        this.postExecute = run;
        this.progressBar = progressBar;
        this.loginCover = loginCover;
    }

    @Override
    protected void onPreExecute() {

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        super.onPreExecute();
    }

    @Override
    protected XMPPTCPConnection doInBackground(String... strings) {

        String username = strings[0];
        String password = strings[1];

        SASLAuthentication.supportSASLMechanism("PLAIN", 0);

        ConnectionConfiguration config = new ConnectionConfiguration(ConnectionManager.SERVER_IP_ADDRESS, ConnectionManager.PORT);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setCompressionEnabled(true);
        //config.setSASLAuthenticationEnabled(true);

        XMPPTCPConnection connection = new XMPPTCPConnection(config);
        connection.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual);

        try {
            connection.connect();
            connection.addConnectionListener(new ConnectionListener(context));

            // Listens to friend requests and respond to accepted friends.
            setFriendRequestListener(connection);

                connection.login(username, password, "who");

                if (connection.isAuthenticated()){
                    CredentialsManager.getInstance(context).setValidCredentials(username, password);

                    setAllPacketsListener(connection);
                    setMessageListener(connection);

                    ConnectionService.setConnection(connection);

                    new SynchronizeChatDB(context).execute();

                }

                return connection;

        } catch (SASLErrorException e){
            errorCode = -1;
            e.printStackTrace();
            return null;

        } catch (XMPPException e) {
            e.printStackTrace();
            return null;
        } catch (SaslException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(XMPPTCPConnection conn) {

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        if (conn != null){

            if (conn.isAuthenticated()){

                context.startService(new Intent(context, ConnectionService.class));

                if (!EfficientContactManager.getInstance(context).areContactsPublished()){

                    new AsyncTask<Void, Void, Void>(){
                        @Override
                        protected Void doInBackground(Void... params) {

                            try {
                                EfficientContactManager.getInstance(context).publishContacts();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                        }
                    }.execute();

                }

                if (!GCMDeviceRegistration.isGCMDeviceIdRegistered(context)) {
                    GCMDeviceRegistration.registerDeviceOnBackground(context);
                }

                if (postExecute != null) {

                    postExecute.run();

                } else {

                    Intent intent = new  Intent(context, DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }

            }

        } else {

            if (loginCover != null){
                loginCover.setVisibility(View.GONE);
            }


            if (errorCode == -1) {
                Toast.makeText(context, "Invalid username or password.", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(context, "Sorry, we are having some technical issues. Try again soon!", Toast.LENGTH_LONG).show();
            }


        }

        super.onPostExecute(conn);
    }

    private void setAllPacketsListener(XMPPTCPConnection conn) {
        conn.addPacketListener((PacketListener) new PacketListener() {

            @Override
            public void processPacket(Packet packet) {
                Log.d("ALL_PACKETS", packet.toXML().toString());
            }
        }, new PacketFilter() {

            @Override
            public boolean accept(Packet packet) {
                return true;
            }
        });

    }

    private void setMessageListener(XMPPTCPConnection conn){
        conn.addPacketListener(new ChatMessageListener(context), new PacketFilter() {

            @Override
            public boolean accept(Packet packet) {
                // return if it's not confession packet and IT is message
                // packet.
                return packet.getClass() == org.jivesoftware.smack.packet.Message.class;
            }
        });
    }

    private void setFriendRequestListener(XMPPTCPConnection conn){
        conn.addPacketListener(new FriendRequestListener(context), new PacketTypeFilter(Presence.class));
    }

}
