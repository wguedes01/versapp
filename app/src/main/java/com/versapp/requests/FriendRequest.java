package com.versapp.requests;

import android.content.Context;

import com.versapp.friends.Friend;
import com.versapp.friends.FriendsManager;

/**
 * Created by william on 06/10/14.
 */
public class FriendRequest extends Request {

    private Context context;
    private Friend friend;

    protected FriendRequest(Context context, Friend friend) {
        super(friend.getName(), "wants to be your friend.");
        this.context = context;
        this.friend = friend;
    }

    @Override
    public void accept() {
        FriendsManager.getInstance().acceptFriend(friend.getUsername());
    }

    @Override
    public void deny() {
        FriendsManager.getInstance().removeFriend(friend.getUsername());
    }

    public Friend getFriend() {
        return friend;
    }
}
