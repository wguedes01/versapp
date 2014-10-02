package com.versapp.chat;

/**
 * Created by william on 25/09/14.
 */
public class OneToOneChat extends Chat {

    public static final String TYPE = "121";

    private boolean isOwner;

    public OneToOneChat(String uuid, String name, boolean isOwner) {
        super(uuid, name);
        this.isOwner = isOwner;
    }

    /*
    public OneToOneChat(String uuid, String name, boolean isOwner, long lastOpenedTimestamp) {
        super(uuid, name, lastOpenedTimestamp);
        this.isOwner = isOwner;
    }
    */

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
