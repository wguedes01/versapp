package com.versapp.chat.conversation;

import android.content.Context;

import com.versapp.connection.ConnectionService;
import com.versapp.database.MessagesDAO;

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

        // store on db.
        messagesDAO.insert(message);

        // send over network.
        org.jivesoftware.smack.packet.Message msg = message.getSmackMessage();
        msg.setFrom(ConnectionService.getJid());
        msg.setTo(to);
        ConnectionService.getConnection().sendPacket(msg);
    }
}
