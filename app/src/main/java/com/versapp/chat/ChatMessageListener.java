package com.versapp.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.versapp.NotificationManager;
import com.versapp.chat.conversation.Message;
import com.versapp.database.ChatsDAO;
import com.versapp.database.MessagesDAO;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;

/**
 * Created by william on 29/09/14.
 */
public class ChatMessageListener implements PacketListener {

    public static final String NEW_MESSAGE_ACTION = "NEW_MESSAGE_ACTION";
    public static final String CHAT_ID_ON_MESSAGE_INTENT_EXTRA = "CHAT_ID_ON_MESSAGE";
    public static final String MESSAGE_ID_INTENT_EXTRA = "MESSAGE_ID";

    private Context context;
    private MessagesDAO messagesDAO;
    private ChatsDAO chatsDAO;

    public ChatMessageListener(Context context) {
        this.context = context;
        this.messagesDAO = new MessagesDAO(context);
        this.chatsDAO = new ChatsDAO(context);
    }

    @Override
    public void processPacket(Packet packet) {

        org.jivesoftware.smack.packet.Message smackMessage = (org.jivesoftware.smack.packet.Message) packet;

        String thread = smackMessage.getFrom().split("@")[0];

        String body = smackMessage.getBody();

        String imageUrl = null;
        if (JivePropertiesManager.getProperty(smackMessage, Message.IMAGE_URL_PROPERTY) != null){
            imageUrl = JivePropertiesManager.getProperty(smackMessage, Message.IMAGE_URL_PROPERTY).toString();
        } else {
            imageUrl = "";
        }

        long timestamp = Message.getCurrentEpochTime();
        DelayInformation delayInfo = packet.getExtension("x", "jabber:x:delay");
        if (delayInfo != null) {
            timestamp = delayInfo.getStamp().getTime();
        }

        Message message = new Message(thread, body, imageUrl, timestamp, false);

        // If message is from new chat (not on local db), SYNC local db.
        if (ChatManager.getInstance().getChat(message.getThread()) == null){

            ChatManager.getInstance().clearChats();

            // Check if chat is on local db. If not, reload chat from server.
            if (chatsDAO.get(message.getThread()) == null) {
                ChatManager.getInstance().addAll(ChatManager.getInstance().syncLocalChatDB(context));
            } else {
                ChatManager.getInstance().addAll(chatsDAO.getAll());
            }


        }

        /*
        Chat chat = ChatManager.getInstance().getChat(message.getThread());
        // If user only has 1 friend, drop message.
        if (FriendsManager.getInstance().getFriends().size() < 2 && chat.getType().equals(OneToOneChat.TYPE)){
            return;
        }
        */


        // Add to database.
        long messageId = messagesDAO.insert(message);

        // Send broadcast.
        Intent intent = new Intent(NEW_MESSAGE_ACTION);
        intent.putExtra(CHAT_ID_ON_MESSAGE_INTENT_EXTRA, message.getThread());
        intent.putExtra(MESSAGE_ID_INTENT_EXTRA, messageId);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        // Show notification if user is not currently looking at the chat.
        if (ChatManager.getInstance().getCurrentOpenChat() == null || !ChatManager.getInstance().getCurrentOpenChat().getUuid().equals(message.getThread())){
            NotificationManager.getInstance(context).displayNewMessageNotification(message.getThread(), message.getBody());
        }
    }
}
