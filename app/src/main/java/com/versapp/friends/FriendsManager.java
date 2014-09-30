package com.versapp.friends;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.versapp.HTTPRequestManager;
import com.versapp.confessions.Confession;
import com.versapp.confessions.ConfessionDeserializer;
import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by william on 23/09/14.
 */
public class FriendsManager {

    private static final String FRIEND_URL = ConnectionManager.HTTP_PROTOCOL+"://"+ConnectionManager.SERVER_IP_ADDRESS+":"+ConnectionManager.NODE_PORT+"/friends/active";
    private static final String PENDING_FRIEND_URL =  ConnectionManager.HTTP_PROTOCOL+"://"+ConnectionManager.SERVER_IP_ADDRESS+":"+ConnectionManager.NODE_PORT+"/friends/pending";

    private static final String SUBSCRIPTION_NONE = "none";

    private static FriendsManager instance;

    private FriendsManager() {}

    public static FriendsManager getInstance() {

        if (instance == null){
            instance = new FriendsManager();
        }

        return instance;
    }

    public ArrayList<Friend> getFriends(){

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Confession.class, new ConfessionDeserializer());
        Gson gson = gsonBuilder.create();

        Friend[] friends = null;
        InputStream in = null;
        try {

            in = HTTPRequestManager.getInstance().sendSimpleHttpRequest(FRIEND_URL);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (in != null){
            Reader reader = new InputStreamReader(in);
            friends = gson.fromJson(reader, Friend[].class);
        }

        ArrayList<Friend> friendList = new ArrayList<Friend>();

        if (friends != null) {
            Arrays.sort(friends);
            friendList.addAll(Arrays.asList(friends));
        }

        return friendList;
    }

    public ArrayList<Friend> getPendingFriends(){

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Confession.class, new ConfessionDeserializer());
        Gson gson = gsonBuilder.create();

        Friend[] friends = null;
        InputStream in = null;
        try {

            in = HTTPRequestManager.getInstance().sendSimpleHttpRequest(PENDING_FRIEND_URL);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (in != null){
            Reader reader = new InputStreamReader(in);
            friends = gson.fromJson(reader, Friend[].class);
        }

        ArrayList<Friend> friendList = new ArrayList<Friend>();

        if (friends != null) {
            Arrays.sort(friends);
            friendList.addAll(Arrays.asList(friends));
        }

        return friendList;
    }



    public void removeFriend(Context context, String username) {

        // remove from roster.
        String packetId = "get_last";
        String xml = "<iq type='set' id='" + packetId + "'> <query xmlns='jabber:iq:roster'><item jid='" + username + "@"
                + ConnectionManager.SERVER_IP_ADDRESS + "' subscription='remove'/></query></iq>";

        String response = ConnectionService.sendCustomXMLPacket(xml, packetId);
    }

    public boolean sendRequest(Context context, String toJID) {

        // FriendsDAO friendsDb = new FriendsDAO(context);
        // friendsDb.setFriendRequestSent(toJID.split("@")[0]);

        if (!toJID.contains("@")) {
            toJID = toJID + "@" + ConnectionManager.SERVER_IP_ADDRESS;
        }

        XMPPTCPConnection connection = ConnectionService.getConnection();

        Presence presenceResponse = new Presence(Presence.Type.subscribe);
        presenceResponse.setTo(toJID);
        try {
            connection.sendPacket(presenceResponse);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        return true;
    }


}
