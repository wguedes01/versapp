package com.versapp.confessions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.net.URLDecoder;

/**
 * Created by william on 20/09/14.
 */
public class ConfessionDeserializer implements JsonDeserializer<Confession> {

    private static final String CONFESSION_ID_JSON_KEY = "id";
    private static final String CONFESSION_BODY_JSON_KEY = "body";
    private static final String CONFESSION_CREATED_TIMESTAMP_JSON_KEY = "timestamp";
    private static final String CONFESSION_IMAGE_URL_JSON_KEY = "imageUrl";
    private static final String CONFESSION_DEGREE_JSON_KEY = "degree";
    private static final String CONFESSION_IS_FAVORITED_JSON_KEY = "hasFavorited";
    private static final String CONFESSION_FAVORITE_COUNT_JSON_KEY = "numFavorites";


    @Override
    public Confession deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        final JsonObject jsonObject = json.getAsJsonObject();
        JsonElement idElement = jsonObject.get(CONFESSION_ID_JSON_KEY);
        JsonElement bodyElement = jsonObject.get(CONFESSION_BODY_JSON_KEY);
        JsonElement createdTimestampElement = jsonObject.get(CONFESSION_CREATED_TIMESTAMP_JSON_KEY);
        JsonElement imageUrlElement = jsonObject.get(CONFESSION_IMAGE_URL_JSON_KEY);
        JsonElement degreeElement = jsonObject.get(CONFESSION_DEGREE_JSON_KEY);
        JsonElement isFavoritedElement = jsonObject.get(CONFESSION_IS_FAVORITED_JSON_KEY);
        JsonElement numFavoritesElement = jsonObject.get(CONFESSION_FAVORITE_COUNT_JSON_KEY);

        final Confession confession = new Confession();
        confession.setId(idElement.getAsLong());
        confession.setBody(URLDecoder.decode(bodyElement.getAsString()));
        confession.setCreatedTimestamp(createdTimestampElement.getAsLong());
        confession.setImageUrl(imageUrlElement.getAsString());
        confession.setDegree(degreeElement.getAsInt());
        confession.setFavorited(isFavoritedElement.getAsBoolean());
        confession.setNumFavorites(numFavoritesElement.getAsInt());

        return confession;
    }
}
