package com.versapp.chat.conversation;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.versapp.MainApplication;
import com.versapp.connection.ConnectionService;
import com.versapp.database.MessagesDAO;

import org.jivesoftware.smack.SmackException;

/**
 * Created by william on 29/09/14.
 */
public class ConversationManager {

    private static ConversationManager instance;

    private Context context;
    private MessagesDAO messagesDAO;

    private ConversationManager(Context context) {
        this.context = context;
        this.messagesDAO = new MessagesDAO(context);
    }

    public static ConversationManager getInstance(Context context) {

        if (instance == null){
            instance = new ConversationManager(context);
        }

        return instance;
    }

    public void sendMessage(Message message, String to){

        // send over network.
        org.jivesoftware.smack.packet.Message msg = message.getSmackMessage();
        msg.setFrom(ConnectionService.getJid());
        msg.setTo(to);
        try {
            ConnectionService.getConnection().sendPacket(msg);

            // store on db.
            messagesDAO.insert(message);

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to send message. Please check the device's network connection.", Toast.LENGTH_LONG).show();
        }

        // Get tracker.
        Tracker t = ((MainApplication) context).getTracker();
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder()
                .setCategory("Messages")
                .setAction("Send Message")
                .build());


    }
}
