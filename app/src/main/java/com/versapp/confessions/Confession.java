package com.versapp.confessions;

import android.os.AsyncTask;

import com.versapp.connection.ConnectionManager;
import com.versapp.connection.ConnectionService;

/**
 * Created by william on 20/09/14.
 */
public class Confession {

    public static final int GLOBAL_DEGREE = 7;
    public static final int FRIEND_DEGREE = 1;
    public static final int FRIEND_OF_FRIEND_DEGREE = 2;

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
        return degree == 0;
    }

    public String getReadableDegree(){

        switch(getDegree()) {
            case 0:
                return "Yours";
            case 1:
                return "Friend";
            case 2:
                return "Friend of friend";
            default:
                return "Global";
        }

    }

    public void destroy() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                System.out.println("Sending remove packet.");

                String packetId = "destroy_confession";

                String xml = "<iq type='set' to='" + ConnectionManager.SERVER_IP_ADDRESS + "' id='" + packetId + "' from='"
                        + ConnectionService.getConnection().getUser() + "'><confession xmlns='who:iq:confession'><destroy id='" + getId()
                        + "'/></confession></iq>";

                ConnectionService.sendCustomXMLPacket(xml, packetId);

                return null;
            }
        }.execute();

    }

    @Override
    public String toString() {
        return String.format("{%d, %d, %s}", getId(), getDegree(), getBody());
    }
}
