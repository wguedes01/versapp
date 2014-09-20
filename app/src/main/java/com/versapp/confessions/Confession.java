package com.versapp.confessions;

/**
 * Created by william on 20/09/14.
 */
public class Confession {

    private long id;
    private String body;
    private long createdTimestamp;
    private String imageUrl;
    private String degree;
    private boolean isFavorited;
    private int numFavorites;

    public Confession(long id, String body, long createdTimestamp, String imageUrl, String degree, boolean isFavorited, int numFavorites) {
        this.id = id;
        this.body = body;
        this.createdTimestamp = createdTimestamp;
        this.imageUrl = imageUrl;
        this.degree = degree;
        this.isFavorited = isFavorited;
        this.numFavorites = numFavorites;
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

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
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
}
