package com.versapp.connection;

import android.content.Context;

import com.versapp.Logger;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by william on 20/09/14.
 */
public class ConnectionListener implements org.jivesoftware.smack.ConnectionListener {

    private Context context;
    public static boolean reconnecting = false;

    public ConnectionListener(Context context) {
        this.context = context;
    }

    @Override
    public void connected(XMPPConnection xmppConnection) {
        Logger.log(Logger.CONNECTION_DEBUG, "Connected.");
    }

    @Override
    public void authenticated(XMPPConnection xmppConnection) {
        Logger.log(Logger.CONNECTION_DEBUG, "Authenticated.");
    }

    @Override
    public void connectionClosed() {
        Logger.log(Logger.CONNECTION_DEBUG, "connectionClosed()");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Logger.log(Logger.CONNECTION_DEBUG, "connectionClosedOnError(). " + e.getStackTrace());
    }

    @Override
    public void reconnectingIn(int seconds) {
        Logger.log(Logger.CONNECTION_DEBUG, "reconnectingIn(). Seconds: " + seconds);
    reconnecting = true;
        /*
        new LoginAT(context, new Runnable() {
            @Override
            public void run() {

            }
        }, null, null).execute(CredentialsManager.getInstance(context).getValidUsername(), CredentialsManager.getInstance(context).getValidPassword());
        */

    }

    @Override
    public void reconnectionSuccessful() {
        reconnecting = false;
        Logger.log(Logger.CONNECTION_DEBUG, "reconnectionSuccessfull()");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        reconnecting = false;
        Logger.log(Logger.CONNECTION_DEBUG, "reconnectionFailed(). " + e.getStackTrace());
    }
}
