package com.versapp.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

        final JsonObject jsonObject = json.getAsJsonObject();
        JsonElement usernameElement = jsonObject.get(USERNAME_JSON_KEY);
        JsonElement nameElement = jsonObject.get(NAME_JSON_KEY);

        String username = usernameElement.getAsString();
        String name = nameElement.getAsString();

        return new Participant(username, name);
    }
}
