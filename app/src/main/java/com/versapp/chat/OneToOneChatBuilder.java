package com.versapp.chat;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by william on 27/09/14.
 */
public class OneToOneChatBuilder extends ChatBuilder {


    protected OneToOneChatBuilder(String type, ArrayList<String> participants) {
        super(type, participants, "", "");
    }

    @Override
    public String toJson() {
        return String.format("{}", getType(), new JSONArray(getParticipants()));
    }
}
