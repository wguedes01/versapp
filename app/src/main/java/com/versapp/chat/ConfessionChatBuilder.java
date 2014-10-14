package com.versapp.chat;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by william on 27/09/14.
 */
public class ConfessionChatBuilder extends ChatBuilder {

    public ConfessionChatBuilder(long cid) {
        super(ConfessionChat.TYPE, null, cid, "");
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put(ChatBuilder.POST_PARAM_CHAT_TYPE, getType());
            jsonObj.put(ChatBuilder.POST_PARAM_CHAT_CID, getCid());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;
    }
}
