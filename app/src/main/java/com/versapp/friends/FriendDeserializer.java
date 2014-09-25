package com.versapp.friends;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by william on 24/09/14.
 */
public class FriendDeserializer implements JsonDeserializer<Friend> {

    private static final String FRIEND_USERNAME_JSON_KEY = "username";
    private static final String FRIEND_NAME_JSON_KEY = "name";

    @Override
    public Friend deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        final JsonObject jsonObject = json.getAsJsonObject();

        JsonElement usernameElement = jsonObject.get(FRIEND_USERNAME_JSON_KEY);
        JsonElement nameElement = jsonObject.get(FRIEND_NAME_JSON_KEY);

        return new Friend(usernameElement.getAsString(), nameElement.getAsString());
    }

}
