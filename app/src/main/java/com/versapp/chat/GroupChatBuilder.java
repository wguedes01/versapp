package com.versapp.chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by william on 27/09/14.
 */
public class GroupChatBuilder extends ChatBuilder {

    public GroupChatBuilder(ArrayList<String> participants, String name) {
        super(GroupChat.TYPE, participants, -1, name);
    }

    @Override
    public JSONObject toJson() {

        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put(ChatBuilder.POST_PARAM_CHAT_TYPE, getType());
            jsonObj.put(ChatBuilder.POST_PARAM_CHAT_PARTICIPANTS, new JSONArray(getParticipants()));
            jsonObj.put(ChatBuilder.POST_PARAM_CHAT_NAME, getName());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

}
