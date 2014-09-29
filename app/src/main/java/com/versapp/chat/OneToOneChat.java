package com.versapp.chat;

/**
 * Created by william on 25/09/14.
 */
public class OneToOneChat extends Chat {

    public static final String TYPE = "121";

    private boolean isOwner;

    protected OneToOneChat(String uuid, String name, boolean isOwner) {
        super(uuid, name);
        this.isOwner = isOwner;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }
}
