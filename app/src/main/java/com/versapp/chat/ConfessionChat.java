package com.versapp.chat;

/**
 * Created by william on 25/09/14.
 */
public class ConfessionChat extends Chat {

    public static final String TYPE = "thought";

    private int degree;

    protected ConfessionChat(String uuid, String name, int degree) {
        super(uuid, name);
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }
}
