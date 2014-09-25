package com.versapp.friends;

import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by william on 24/09/14.
 */
public class Blocker {

    public static final String ACTION_BLOCK = "block";
    public static final String ACTION_UNBLOCK = "unblock";

    public static final String EXPLICIT_USER_BLOCK = "explicit_user"; // used to
    // block a
    // user
    public static final String EXPLICIT_GROUP_BLOCK = "explicit_group"; // used
    // to
    // block
    // an
    // entire
    // group
    public static final String USER_IN_GROUP_BLOCK = "user_in_group"; // used to
    // block
    // an
    // annonymous
    // sender
    // of a
    // message
    // WITHIN
    // a
    // group.
    public static final String IMPLICIT_USER_BLOCK = "implicit_user"; // used to
    // block
    // an
    // annonymous
    // chat.

    // Used to block users when you know who you are blocking
    public void blockExplicitUser(String username) {
        block(username, null, EXPLICIT_USER_BLOCK);
    }

    // Used to block one to one chats
    public void blockImplicitUser(String username) {
        block(username, null, IMPLICIT_USER_BLOCK);
    }

    public void blockUserWithinChat(String username, String chatId) {
        block(username, chatId, USER_IN_GROUP_BLOCK);
    }

    private void block(String username, String chatId, String type) {

        String packetId = "block";
        String xml;
        if (chatId == null) {
            xml = "<iq type='set' to='" + ConnectionManager.SERVER_IP_ADDRESS + "' id='" + packetId + "'><query xmlns='who:iq:block'><block><username>" + username + "</username><type>" + type + "</type></block></query></iq>";
        } else {
            xml = "<iq type='set' to='" + ConnectionManager.SERVER_IP_ADDRESS + "' id='" + packetId + "'><query xmlns='who:iq:block'><block><username>" + username + "</username><chat_id>" + chatId + "</chat_id><type>" + type
                    + "</type></block></query></iq>";
        }

        ConnectionService.sendCustomXMLPacket(xml, packetId);

    }

    /**
     *
     * @return arraylist with usernames of blocked friends.
     */
    public static ArrayList<String> getExplicitUsersBlocked() {

        ArrayList<String> blocked = new ArrayList<String>();

        String packetId = "get_blocked";
        String xml = "<iq type='get' to='" + ConnectionManager.SERVER_IP_ADDRESS + "' id='" + packetId + "'>" + "<query xmlns='who:iq:block'>" + "<blocker type='" + EXPLICIT_USER_BLOCK + "' />" + "</query>" + "</iq>";

        String response = ConnectionService.sendCustomXMLPacket(xml, packetId).replaceAll("\\r\\n|\\r|\\n", " ");
        Pattern p = Pattern.compile("\\[\"(.*?)\"\\]");
        Matcher m = p.matcher(response);

        while (m.find()) {
            blocked.add(m.group(1));
        }

        return blocked;

    }

    public void unblockUserExplicit(String username) {
        unblock(username, null, EXPLICIT_USER_BLOCK);
    }

    public void unblockUserInGroup(String username, String chatId) {
        unblock(username, chatId, USER_IN_GROUP_BLOCK);
    }

    private void unblock(String username, String chatId, String type) {

        String packetId = "unblock";
        String xml;
        if (chatId == null) {
            xml = "<iq type='set' to='" + ConnectionManager.SERVER_IP_ADDRESS + "' id='" + packetId + "'><query xmlns='who:iq:block'><unblock><username>" + username + "</username><type>" + type + "</type></unblock></query></iq>";
        } else {
            xml = "<iq type='set' to='" + ConnectionManager.SERVER_IP_ADDRESS + "' id='" + packetId + "'><query xmlns='who:iq:block'><unblock><username>" + username + "</username><chat_id>" + chatId + "</chat_id><type>" + type
                    + "</type></unblock></query></iq>";
        }

        ConnectionService.sendCustomXMLPacket(xml, packetId);

    }

}
