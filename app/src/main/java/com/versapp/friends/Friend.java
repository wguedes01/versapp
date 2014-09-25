package com.versapp.friends;

/**
 * Created by william on 23/09/14.
 */
public class Friend implements Comparable<Friend>{

    public static final String ACCEPTED = "accepted";
    public static final String PENDING_RECEIVED = "pending_received";
    public static final String PENDING_SENT = "pending_sent";
    public static final String BLOCKED = "blocked";

    private boolean isBlocked;
    private String username;
    private String name;

    public Friend(String username, String name) {
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

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    @Override
    public int compareTo(Friend another) {
        return name.compareTo(another.name);
    }
}
