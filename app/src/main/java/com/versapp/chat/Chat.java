package com.versapp.chat;

import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;

/**
 * Created by william on 25/09/14.
 */
public abstract class Chat {

    private static final String STATUS_INACTIVE = "inactive";
    private static final String STATUS_ACTIVE = "active";

    private String uuid;
    private String name;
    private long lastOpenedTimestamp;

    protected Chat(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void leave(){
        // send inactive request to server.
        String packetId = "leave_chat";
        String xml = "<iq type='set' to='" + ConnectionManager.SERVER_IP_ADDRESS + "' id='" + packetId + "' from='"
                + ConnectionService.getConnection().getUser() + "'><chat xmlns='who:iq:chat'><participant><chat_id>" + getUuid() + "</chat_id><status>"
                + STATUS_INACTIVE + "</status></participant></chat></iq>";

        ConnectionService.sendCustomXMLPacket(xml, packetId);
    }

    public abstract String getType();

    public long getLastOpenedTimestamp() {
        return lastOpenedTimestamp;
    }

    public void setLastOpenedTimestamp(long lastOpenedTimestamp) {
        this.lastOpenedTimestamp = lastOpenedTimestamp;
    }
}
