package com.versapp.connection;

import android.content.Context;
import android.util.Log;

import com.versapp.Logger;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by william on 20/09/14.
 */
public class ConnectionListener implements org.jivesoftware.smack.ConnectionListener {

    private Context context;

    public ConnectionListener(Context context) {
        this.context = context;
    }

    @Override
    public void connected(XMPPConnection xmppConnection) {
        Log.d(Logger.CONNECTION_DEBUG, "Connected.");
    }

    @Override
    public void authenticated(XMPPConnection xmppConnection) {
        Log.d(Logger.CONNECTION_DEBUG, "Authenticated.");
    }

    @Override
    public void connectionClosed() {
        Log.d(Logger.CONNECTION_DEBUG, "connectionClosed()");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.d(Logger.CONNECTION_DEBUG, "connectionClosedOnError(). " + e.getStackTrace());
    }

    @Override
    public void reconnectingIn(int seconds) {
    Log.d(Logger.CONNECTION_DEBUG, "reconnectingIn(). Seconds: " + seconds);

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
        Log.d(Logger.CONNECTION_DEBUG, "reconnectionSuccessfull()");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.d(Logger.CONNECTION_DEBUG, "reconnectionFailed(). " + e.getStackTrace());
    }
}
