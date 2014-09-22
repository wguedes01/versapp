package com.versapp.confessions;

import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;

/**
 * Created by william on 20/09/14.
 */
public class Confession {

    private long id;
    private String body;
    private long createdTimestamp;
    private String imageUrl;
    private int degree;
    private boolean isFavorited;
    private int numFavorites;

    public Confession(long id, String body, long createdTimestamp, String imageUrl, int degree, boolean isFavorited, int numFavorites) {
        this.id = id;
        this.body = body;
        this.createdTimestamp = createdTimestamp;
        this.imageUrl = imageUrl;
        this.degree = degree;
        this.isFavorited = isFavorited;
        this.numFavorites = numFavorites;
    }

    public Confession() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean isFavorited) {
        this.isFavorited = isFavorited;
    }

    public int getNumFavorites() {
        return numFavorites;
    }

    public void setNumFavorites(int numFavorites) {
        this.numFavorites = numFavorites;
    }

    public void toggleFavorite(){

            if (isFavorited) {
                numFavorites--;
            } else {
                numFavorites++;
            }

            isFavorited = !isFavorited;

            // process request async.
            String packetId = "toggle_favorite";

            String xml = "<iq type='set' to='" + ConnectionManager.SERVER_IP_ADDRESS + "' id='" + packetId + "' from='"
                    + ConnectionService.getConnection().getUser() + "'><confession xmlns='who:iq:confession'><toggle_favorite id='" + getId()
                    + "'/></confession></iq>";

            // Ignore response.
            ConnectionService.sendCustomXMLPacket(xml, packetId);
    }

    public boolean isMine() {
        return false;
    }
}
