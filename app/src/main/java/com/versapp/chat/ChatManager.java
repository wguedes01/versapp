package com.versapp.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.versapp.HTTPRequestManager;
import com.versapp.connection.ConnectionManager;

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

    private ChatManager() {
    }

    public static ChatManager getInstance() {

        if (instance == null)
            instance = new ChatManager();

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



}
