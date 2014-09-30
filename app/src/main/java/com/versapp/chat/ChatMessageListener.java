package com.versapp.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.versapp.Logger;
import com.versapp.chat.conversation.Message;
import com.versapp.database.MessagesDAO;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

/**
 * Created by william on 29/09/14.
 */
public class ChatMessageListener implements PacketListener {

    public static final String NEW_MESSAGE_ACTION = "NEW_MESSAGE_ACTION";
    public static final String CHAT_ID_ON_MESSAGE_INTENT_EXTRA = "CHAT_ID_ON_MESSAGE";
    public static final String MESSAGE_ID_INTENT_EXTRA = "MESSAGE_ID";

    private Context context;
    private MessagesDAO messagesDAO;

    public ChatMessageListener(Context context) {
        this.context = context;
        this.messagesDAO = new MessagesDAO(context);
    }

    @Override
    public void processPacket(Packet packet) {

        org.jivesoftware.smack.packet.Message smackMessage = (org.jivesoftware.smack.packet.Message) packet;

        Message message = new Message(smackMessage.getFrom().split("@")[0], smackMessage.getBody(), smackMessage.getProperty(Message.IMAGE_URL_PROPERTY).toString(), Message.getCurrentEpochTime(), false);

        // Add to database.
        long messageId = messagesDAO.insert(message);

        // Send broadcast.
        Intent intent = new Intent(NEW_MESSAGE_ACTION);
        intent.putExtra(CHAT_ID_ON_MESSAGE_INTENT_EXTRA, message.getThread());
        intent.putExtra(MESSAGE_ID_INTENT_EXTRA, messageId);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        Log.d(Logger.CHAT_DEBUG, "Received chat message: " + smackMessage.toXML());
    }
}
