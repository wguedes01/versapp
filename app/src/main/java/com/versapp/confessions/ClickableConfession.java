package com.versapp.confessions;

/**
 * Created by william on 15/10/14.
 */
public class ClickableConfession extends Confession {

    private Runnable action;

    public ClickableConfession(String body, Runnable action) {
        super(1, body, 0, null, 0, false, 0);
        this.action = action;
    }


    public Runnable getAction() {
        return action;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }
}
