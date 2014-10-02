package com.versapp.chat;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.versapp.HTTPRequestManager;
import com.versapp.NotificationManager;
import com.versapp.connection.ConnectionManager;
import com.versapp.database.ChatsDAO;

import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by william on 25/09/14.
 */
public class ChatManager {

    private static final String CREATE_CHAT_URL = ConnectionManager.HTTP_PROTOCOL+"://"+ConnectionManager.SERVER_IP_ADDRESS+":"+ConnectionManager.NODE_PORT+"/chat/create";
    private static final String JOINED_CHATS_URL = ConnectionManager.HTTP_PROTOCOL+"://"+ConnectionManager.SERVER_IP_ADDRESS+":"+ConnectionManager.NODE_PORT+"/chat/joined";

    private static ChatManager instance;
    private static ArrayList<Chat> chats;

    private static Chat openChat;

    private ChatManager() {
    }

    public static ChatManager getInstance() {

        if (instance == null){
            instance = new ChatManager();
            chats = new ArrayList<Chat>();
        }


        return instance;
    }

    public ArrayList<Chat> getChatsFromServer(){

        ArrayList<Chat> chats = null;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Chat.class, new ChatDeserializer());
        Gson gson = gsonBuilder.create();

        Chat[] chatArray = null;

        try {

            InputStream in = HTTPRequestManager.getInstance().sendSimpleHttpRequest(JOINED_CHATS_URL);

            Reader reader = new InputStreamReader(in);

            chatArray = gson.fromJson(reader, Chat[].class);

        } catch (IOException e) {
            e.printStackTrace();
        }



        if (chatArray != null){
            chats = new ArrayList<Chat>(Arrays.asList(chatArray));
        } else {
            chats = new ArrayList<Chat>();
        }

        return chats;
    }

    public Chat createChat(ChatBuilder chatBuilder) throws IOException {

        Chat chat = null;

        try {
            InputStream in = HTTPRequestManager.getInstance().simpleHTTPPost(CREATE_CHAT_URL, new StringEntity(chatBuilder.toJson()));

            if (in != null) {

                chat = inputStreamToChat(in);

            } else {
                return null;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return chat;
    }

    private Chat inputStreamToChat(InputStream in){

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Chat.class, new ChatDeserializer());
        Gson gson = gsonBuilder.create();

        Reader reader = new InputStreamReader(in);

        Chat chat = gson.fromJson(reader, Chat.class);

        return chat;
    }

    public ArrayList<Chat> getChats() {

        if (chats == null){
            chats = new ArrayList<Chat>();
        }

        return chats;
    }

    public void moveToTop(String chatId){
        Chat chat =  remove(chatId);
        chats.add(0, chat);
    }

    private Chat remove(String chatId){

        if (chats == null || chats.size() <= 0){
            return null;
        }

        int i = 0;
        while(i < chats.size()){

            if (chats.get(i).getUuid().equals(chatId)){
                return chats.remove(i);
            }

            i++;
        }

        return null;
    }

    public Chat getChat(String chatUUID){
        if (chats.size() <= 0){
            return null;
        }

        int i = 0;
        while(i < chats.size()){

            if (chats.get(i).getUuid().equals(chatUUID)){
                return chats.get(i);
            }

            i++;
        }

        return null;
    }

    public ArrayList<Chat> syncLocalChatDB(Context context){

        ChatsDAO chatsDb = new ChatsDAO(context);

        ArrayList<Chat> chats = ChatManager.getInstance().getChatsFromServer();

        for (Chat c : chats){

            if (chatsDb.get(c.getUuid()) == null){
                chatsDb.insert(c);
            } else {
                chatsDb.update(c);
            }

        }

        return chats;
    }

    public void leaveChat(Context context, Chat chat){

        new ChatsDAO(context).delete(chat.getUuid());

        chat.leave();
    }

    public void setChatOpen(Context context, Chat chat){
        new ChatsDAO(context).updateLastOpenedTimestamp(chat.getUuid());

        if (NotificationManager.getInstance(context).hasNotification(chat.getUuid())){
            NotificationManager.getInstance(context).removeNotification(chat.getUuid());
        }

        openChat = chat;
    }

    public void setChatClosed(Context context, Chat chat){
        new ChatsDAO(context).updateLastOpenedTimestamp(chat.getUuid());
        openChat = null;
    }

    public Chat getCurrentOpenChat(){
        return openChat;
    }
}
