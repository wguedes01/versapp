package com.versapp.chat;

import android.content.Context;

import com.versapp.database.ParticipantsDAO;

import java.util.ArrayList;

/**
 * Created by william on 25/09/14.
 */
public class GroupChat extends Chat {

    public static final String TYPE = "group";

    private String ownerId;
    private ArrayList<Participant> participants;

    public GroupChat(String uuid, String name, String ownerId, ArrayList<Participant> participants) {
        super(uuid, name);
        this.ownerId = ownerId;
        this.participants = participants;
    }

    /*
    public GroupChat(String uuid, String name, String ownerId, ArrayList<Participant> participants, long lastOpenedTimestamp) {
        super(uuid, name, lastOpenedTimestamp);
        this.ownerId = ownerId;
        this.participants = participants;
    }
    */

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public ArrayList<Participant> getParticipants(Context context) {
        // Lazy initialization allows us to save memory. This action doesn't happen often enough to
        // makes us want to store this in memory.
        return new ParticipantsDAO(context).getAll(getUuid());
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
