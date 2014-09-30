package com.versapp.chat;

import com.versapp.confessions.Confession;

/**
 * Created by william on 25/09/14.
 */
public class ConfessionChat extends Chat {

    public static final String TYPE = "thought";

    private long cid;
    private int degree;
    private Confession confession;

    protected ConfessionChat(String uuid, String name, long cid, int degree) {
        super(uuid, name);
        this.cid = cid;
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public Confession getConfession() {
        return confession;
    }

    public void setConfession(Confession confession) {
        this.confession = confession;
    }
}
