package com.versapp;

import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by william on 27/09/14.
 */
public class UserInfoManager {

    private static UserInfoManager instance;

    private UserInfoManager() {

    }

    public static UserInfoManager getInstance() {

        if (instance == null){
            instance = new UserInfoManager();
        }

        return instance;
    }

    public String getEmail() {

        String packetId = "user_info";

        String xml = "<iq id='" + packetId + "' type='get' to='" + ConnectionManager.SERVER_IP_ADDRESS + "'><query xmlns='who:iq:info'></query></iq>";

        String response = ConnectionService.sendCustomXMLPacket(xml, packetId);

        Pattern p = Pattern.compile("\\[\"(\\d+)?\",\"(\\d+)?\",\"(.*)?\"\\]");
        Matcher m = p.matcher(response);

        if (m.find()) {
            return m.group(3);
        }

        return "";

    }
}
