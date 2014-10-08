package com.versapp.friends;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import com.versapp.connection.ConnectionService;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

/**
 * Created by william on 07/10/14.
 */
public class FriendRequestListener implements PacketListener {

    public static final String FRIEND_REQUEST_RECEIVED_INTENT_ACTION = "FRIEND_REQUEST_RECEIVED_INTENT_ACTION";
    public static final String NEW_FRIEND_INTENT_ACTION = "NEW_FRIEND_INTENT_ACTION";

    private Context context;

    public FriendRequestListener(Context context) {
        this.context = context;
    }

    @Override
    public void processPacket(Packet packet) throws SmackException.NotConnectedException {

        Presence presence = (Presence) packet;

        if (presence.getType() == Presence.Type.subscribe) {

            System.out.println("RECEIVED SUBSCRIBE");

            new AsyncTask<Void, Void, Void>(){

                @Override
                protected Void doInBackground(Void... params) {
                    FriendsManager.getInstance().getPendingFriends().clear();
                    FriendsManager.getInstance().getPendingFriends().addAll(FriendsManager.getInstance().getPendingFriendsFromServer());
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(FRIEND_REQUEST_RECEIVED_INTENT_ACTION));
                    super.onPostExecute(aVoid);
                }

            }.execute();

        } else if (presence.getType() == Presence.Type.subscribed) {

            System.out.println("RECEIVED SUBSCRIBED");

            Presence sub = new Presence(Presence.Type.subscribed);
            sub.setTo(presence.getFrom());
            ConnectionService.getConnection().sendPacket(sub);

            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(NEW_FRIEND_INTENT_ACTION));
        }

    }
}
