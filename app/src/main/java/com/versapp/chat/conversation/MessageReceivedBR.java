package com.versapp.chat.conversation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;

import com.versapp.Logger;
import com.versapp.chat.ChatMessageListener;
import com.versapp.database.MessagesDAO;

/**
 * Created by william on 29/09/14.
 */
public class MessageReceivedBR extends BroadcastReceiver {

    private String chatUUID;
    private ArrayAdapter<ChatMessage> adapter;
    private MessagesDAO messagesDAO;

    public MessageReceivedBR(String chatUUID, ArrayAdapter<ChatMessage> adapter) {
        this.chatUUID = chatUUID;
        this.adapter = adapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Logger.log(Logger.CHAT_DEBUG, "Inside New Message BR.");

        if (messagesDAO == null) {
            messagesDAO = new MessagesDAO(context);
        }

        String chatIdOnMessage = intent.getStringExtra(ChatMessageListener.CHAT_ID_ON_MESSAGE_INTENT_EXTRA);
        long messageId = intent.getLongExtra(ChatMessageListener.MESSAGE_ID_INTENT_EXTRA, -1);

        if (chatIdOnMessage == null) {

        } else if (chatUUID.equals(chatIdOnMessage)){

            if (messageId >= 0){
                // get message from database.
                Message message =messagesDAO.get(messageId);

                adapter.add(new ChatMessage(message, true));
                adapter.notifyDataSetChanged();
            }

        } else {
            // does nothing right now.
        }

    }
}
