package com.versapp.friends;

/**
 * Created by william on 25/09/14.
 */
public class FriendListItem {

    public Friend friend;
    private boolean isSelected = false;

    public FriendListItem(Friend friend) {
        this.friend = friend;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}
