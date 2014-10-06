package com.versapp.requests;

/**
 * Created by william on 06/10/14.
 */
public class FriendRequest extends Request {

    protected FriendRequest(String title, String message) {
        super(title, message);
    }

    @Override
    public void accept() {

    }

    @Override
    public void deny() {

    }
}
