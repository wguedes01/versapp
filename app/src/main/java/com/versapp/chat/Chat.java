package com.versapp.chat;

/**
 * Created by william on 25/09/14.
 */
public abstract class Chat {

    private String uuid;
    private String name;

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

}
