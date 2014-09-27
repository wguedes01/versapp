package com.versapp.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by william on 25/09/14.
 */
public class ParticipantDeserializer implements JsonDeserializer<Participant> {

    private static final String USERNAME_JSON_KEY = "username";
    private static final String NAME_JSON_KEY = "name";

    @Override
    public Participant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return null;
    }
}
