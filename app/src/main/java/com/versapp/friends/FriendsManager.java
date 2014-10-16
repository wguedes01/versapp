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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by william on 23/09/14.
 */
public class FriendsManager {

    private static final String SUBSCRIPTION_NONE = "none";

    private static FriendsManager instance;
    private ArrayList<Friend> friends;
    private ArrayList<Friend> pendingFriends;

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

        in = HTTPRequestManager.getInstance().sendSimpleHttpsRequest(ConnectionManager.FRIEND_URL);


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
        if (pendingFriends == null) {
            pendingFriends = getPendingFriendsFromServer();
        }

        return pendingFriends;
    }

    public ArrayList<Friend> getPendingFriendsFromServer(){

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Confession.class, new ConfessionDeserializer());
        Gson gson = gsonBuilder.create();

        Friend[] friends = null;
        InputStream in = null;

        in = HTTPRequestManager.getInstance().sendSimpleHttpsRequest(ConnectionManager.PENDING_FRIEND_URL);

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

    public void acceptFriend(String username){

        try {

            Presence presenceResponse = new Presence(Presence.Type.subscribed);
            presenceResponse.setTo(username+"@"+ConnectionManager.SERVER_IP_ADDRESS);
            ConnectionService.getConnection().sendPacket(presenceResponse);

            // remove from roster.
            String packetId = "get_last";
            String xml = "<iq type='set' id='" + packetId + "' from='"+ConnectionService.getConnection().getUser()+"'> <query xmlns='jabber:iq:roster'><item jid='" + username + "@"
                    + ConnectionManager.SERVER_IP_ADDRESS + "' subscription='both'><group>Contacts</group></item></query></iq>";

            ConnectionService.sendCustomXMLPacket(xml, packetId);

            int i = 0;
            while(i < getPendingFriends().size()){

                if (getPendingFriends().get(i).getUsername().equals(username)){
                    Friend friend = getPendingFriends().remove(i);
                    getFriends().add(friend);
                    break;
                }

                i++;
            }

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

    }


    public void removeFriend(String username) {

        // remove from roster.
        String packetId = "get_last";
        String xml = "<iq type='set' id='" + packetId + "' > <query xmlns='jabber:iq:roster'><item jid='" + username + "@"
                + ConnectionManager.SERVER_IP_ADDRESS + "' subscription='remove'/></query></iq>";

        boolean removed = false;


        int a = 0;
        while(a < getPendingFriends().size()){

            if (getPendingFriends().get(a).getUsername().equals(username)){
                getPendingFriends().remove(a);
                removed = true;
                break;
            }

            a++;
        }

        if (!removed){

            int i = 0;
            while(i < getFriends().size()){

                if (getFriends().get(i).getUsername().equals(username)){
                    getFriends().remove(i);
                    break;
                }

                i++;
            }

        }

        ConnectionService.sendCustomXMLPacket(xml, packetId);
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

    public ArrayList<Friend> getCachedFriends(){
        if (friends == null) {
            friends = new ArrayList<Friend>();
            friends = getFriends();
        }

        return friends;
    }






}
