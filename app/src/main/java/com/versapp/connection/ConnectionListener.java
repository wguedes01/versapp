package com.versapp.connection;

import android.util.Log;

import com.versapp.Logger;

/**
 * Created by william on 20/09/14.
 */
public class ConnectionListener implements org.jivesoftware.smack.ConnectionListener {

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
