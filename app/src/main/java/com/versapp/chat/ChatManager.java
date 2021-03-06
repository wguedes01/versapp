package com.versapp.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.versapp.HTTPRequestManager;
import com.versapp.NotificationManager;
import com.versapp.connection.ConnectionManager;
import com.versapp.database.ChatsDAO;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static final String CHAT_SYNCED_ACTION = "CHAT_SYNCED_ACTION";

    private static ChatManager instance;
    //private static ArrayList<Chat> chats = new ArrayList<Chat>();
    private static ArrayList<Chat> pendingChats;

    private static Chat openChat;

    private ChatManager() {
    }

    public static ChatManager getInstance() {

        if (instance == null){
            instance = new ChatManager();
            //chats = new ArrayList<Chat>();
        }

        return instance;
    }

    private ArrayList<Chat> getChatsFromServer(String url){

        ArrayList<Chat> chats = null;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Chat.class, new ChatDeserializer());
        Gson gson = gsonBuilder.create();

        Chat[] chatArray = null;

        InputStream in = HTTPRequestManager.getInstance().sendSimpleHttpsRequest(url);

        if (in == null) {
            return new ArrayList<Chat>();
        }

        Reader reader = new InputStreamReader(in);

        chatArray = gson.fromJson(reader, Chat[].class);

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
            InputStream in = HTTPRequestManager.getInstance().simpleHTTPSPost(ConnectionManager.CREATE_CHAT_URL, chatBuilder.toJson());

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

    public  ArrayList<Chat> getPendingChats() {

        if (pendingChats == null){
            pendingChats = getPendingChatsFromServer();
        }

        return pendingChats;
    }


    public void syncLocalChatDB(Context context){

        ChatsDAO chatsDb = new ChatsDAO(context);

        ArrayList<Chat> chats = ChatManager.getInstance().getChatsFromServer(ConnectionManager.JOINED_CHATS_URL);

        /*
        if (FriendsManager.getInstance().getCachedFriends().size() < 2){

            int i = 0;
            while(i < chats.size()){
                if (chats.get(i).getType().equals(OneToOneChat.TYPE)){
                    chats.remove(i);
                } else {
                    i++;
                }
            }

        }
        */

        for (Chat c : chats){

            if (chatsDb.get(c.getUuid()) == null){
                chatsDb.insert(c);
            } else {
                chatsDb.update(c);
            }

        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(CHAT_SYNCED_ACTION));
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

    public ArrayList<Chat> getPendingChatsFromServer(){
        return getChatsFromServer(ConnectionManager.PENDING_CHATS_URL);
    };

    public ArrayList<Chat> getJoinedChatsFromServer(){
        return getChatsFromServer(ConnectionManager.JOINED_CHATS_URL);
    }

    public void joinGroup(Context context, GroupChat chat) {

        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("uuid", chat.getUuid());

            HTTPRequestManager.getInstance().simpleHTTPSPost(ConnectionManager.JOIN_CHAT_URL, jsonObj);

            new ChatsDAO(context).insert(chat);
            pendingChats.remove(chat);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void leaveChat(GroupChat chat) {

        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("uuid", chat.getUuid());

            HTTPRequestManager.getInstance().simpleHTTPSPost(ConnectionManager.LEAVE_CHAT_URL, jsonObj);

            pendingChats.remove(chat);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void invalidatePendingChatCache(){
        pendingChats = null;
    }



}
