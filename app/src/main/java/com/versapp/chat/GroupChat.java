package com.versapp.chat;

import java.util.ArrayList;

/**
 * Created by william on 25/09/14.
 */
public class GroupChat extends Chat {

    public static final String TYPE = "group";

    private String ownerId;
    private ArrayList<Participant> participants;

    protected GroupChat(String uuid, String name, String ownerId, ArrayList<Participant> participants) {
        super(uuid, name);
        this.ownerId = ownerId;
        this.participants = participants;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }

}
