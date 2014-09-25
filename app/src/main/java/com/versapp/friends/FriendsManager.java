package com.versapp.friends;

import android.content.Context;
import android.util.Log;

import com.versapp.Logger;
import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;
import com.versapp.database.FriendsDAO;
import com.versapp.vcard.VCard;
import com.versapp.vcard.VCardManager;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by william on 23/09/14.
 */
public class FriendsManager {

    private static final String SUBSCRIPTION_NONE = "none";

    private Context context;
    private static FriendsManager instance;

    private FriendsManager(Context context) {
        this.context = context;
    }

    public static FriendsManager getInstance(Context context) {

        if (instance == null){
            instance = new FriendsManager(context);
        }

        return instance;
    }

    public ArrayList<Friend> syncWithServer() {

        ArrayList<Friend> friends = new ArrayList<Friend>();

        String packetID = "get_roster";
        String query = "<iq id='" + packetID + "' type='get'> <query xmlns='jabber:iq:roster'/></iq>";
        String response = ConnectionService.sendCustomXMLPacket(query, packetID);

        // ATTNETION: the > in the end of the regexp says we should ignore any
        // item with the attribute ask="subscribe". If we want to know who we
        // have
        // subscrived to we need to consider that portion
        Pattern jidPattern = Pattern.compile("<item jid=\"([^@]+){1,}@.*?\"[^\\/]{1,}subscription=\"(.*?)\".*?(\\sask=\"(\\w+)\")?");// ask="subscribe"
        Matcher m = jidPattern.matcher(response);


        FriendsDAO db = new FriendsDAO(context);

        while (m.find()) {

            String username = m.group(1);
            String status = null;

            if (m.group(2).equals(SUBSCRIPTION_NONE)) {

                // if friend is on db. remove it and continue.
                removeFriend(context, username);
                continue;

            }

            if (m.group(4) != null) {
                if (m.group(4).equals("subscribe")) {
                    status = Friend.PENDING_SENT;
                }
            }



           Friend friend = db.get(username);

            // If contact does not exist in the local db, get VCard. Otherwise,
            // don't waste time sending request for vcard;
           if (friend == null) {
                // Can improve this by using another method to get friend from
                // db.
                // Method should not build friend object. just check if friend
                // exists.

               Log.d(Logger.FRIEND_DEBUG, "Getting VCard for: " + username);
               VCard vCard = VCardManager.getVCard(username);

                friend = new Friend(username, vCard.getFullName());

                db.insert(friend, Friend.ACCEPTED);
           } /*else {

               Log.d(Logger.FRIEND_DEBUG, "Updating " + username);

                db.update(friend, status);
             }*/

            friends.add(friend);

        }



/*
        // Updated blocked users.
        ArrayList<String> blockedUsernames = Blocker.getExplicitUsersBlocked();
        for (String blocked : blockedUsernames) {
            Friend friend = db.get(blocked);
            friend.setStatus(Friend.BLOCKED);
            db.insert(friend);
        }
        // Send broadcast that alerts friends have been synchronized.
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(FriendsManager.SYNC_COMPLETE_BR_ACTION));
*/

        return friends;
    }


    public void removeFriend(Context context, String username) {

        // remove from roster.
        String packetId = "get_last";
        String xml = "<iq type='set' id='" + packetId + "'> <query xmlns='jabber:iq:roster'><item jid='" + username + "@"
                + ConnectionManager.SERVER_IP_ADDRESS + "' subscription='remove'/></query></iq>";

        String response = ConnectionService.sendCustomXMLPacket(xml, packetId);

    }


}
