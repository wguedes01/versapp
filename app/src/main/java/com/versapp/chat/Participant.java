package com.versapp.chat;

/**
 * Created by william on 25/09/14.
 */
public class Participant {

    private String username;
    private String name;

    public Participant(String username, String name) {
        this.username = username;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
