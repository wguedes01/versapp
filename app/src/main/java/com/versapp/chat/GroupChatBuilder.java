package com.versapp.chat;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by william on 27/09/14.
 */
public class GroupChatBuilder extends ChatBuilder {

    public GroupChatBuilder(ArrayList<String> participants, String name) {
        super(GroupChat.TYPE, participants, -1, name);
    }

    @Override
    public String toJson() {
        return String.format("{\"%s\": \"%s\", \"%s\": %s, \"%s\":\"%s\"}", ChatBuilder.POST_PARAM_CHAT_TYPE, getType(), ChatBuilder.POST_PARAM_CHAT_PARTICIPANTS, new JSONArray(getParticipants()), ChatBuilder.POST_PARAM_CHAT_NAME, getName());
    }

}
