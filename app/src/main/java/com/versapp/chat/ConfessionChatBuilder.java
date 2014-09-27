package com.versapp.chat;

/**
 * Created by william on 27/09/14.
 */
public class ConfessionChatBuilder extends ChatBuilder {

    public ConfessionChatBuilder(long cid) {
        super(ConfessionChat.TYPE, null, cid, "");
    }

    @Override
    public String toJson() {
        return String.format("{\"%s\": \"%s\", \"%s\": %s}", ChatBuilder.POST_PARAM_CHAT_TYPE, getType(),  ChatBuilder.POST_PARAM_CHAT_CID, getCid());
    }
}
