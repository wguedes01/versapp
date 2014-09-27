package com.versapp.chat;

import java.util.ArrayList;

/**
 * Created by william on 27/09/14.
 */
public abstract class ChatBuilder {

    public static final String POST_PARAM_CHAT_TYPE = "type";
    public static final String POST_PARAM_CHAT_PARTICIPANTS = "participants";
    public static final String POST_PARAM_CHAT_CID = "cid";
    public static final String POST_PARAM_CHAT_NAME = "name";

    private String type;
    private ArrayList<String> participants;
    private long cid;
    private String name;

    protected ChatBuilder(String type, ArrayList<String> participants, long cid, String name) {
        this.type = type;
        this.participants = participants;
        this.cid = cid;
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public long getCid() {
        return cid;
    }

    public String getName() {
        return name;
    }

    public abstract String toJson();


}
