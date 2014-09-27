package com.versapp.chat;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by william on 27/09/14.
 */
public class OneToOneChatBuilder extends ChatBuilder {

    public OneToOneChatBuilder(String inviteUsername) {
        super(OneToOneChat.TYPE, null, -1, "");

        ArrayList<String> participants = new ArrayList<String>();
        participants.add(inviteUsername);
        setParticipants(participants);
    }

    @Override
    public String toJson() {
        return String.format("{\"%s\": \"%s\", \"%s\": %s}", ChatBuilder.POST_PARAM_CHAT_TYPE, getType(), ChatBuilder.POST_PARAM_CHAT_PARTICIPANTS, new JSONArray(getParticipants()));
    }
}
