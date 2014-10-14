package com.versapp.chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public JSONObject toJson() {

        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put(ChatBuilder.POST_PARAM_CHAT_TYPE, getType());
            jsonObj.put(ChatBuilder.POST_PARAM_CHAT_PARTICIPANTS, new JSONArray(getParticipants()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;

    }
}
