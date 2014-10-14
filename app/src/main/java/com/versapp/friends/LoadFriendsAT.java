package com.versapp.friends;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by william on 25/09/14.
 */
public class LoadFriendsAT extends AsyncTask<Void, Void, ArrayList<FriendListItem>> {

    private ArrayAdapter<FriendListItem> adapter;
    private ProgressBar progressBar;

    private TextView notEnoughFriendsLabel;

    public LoadFriendsAT(ArrayAdapter<FriendListItem> adapter, ProgressBar progressBar,  TextView notEnoughFriendsLabel) {
        this.adapter = adapter;
        this.progressBar = progressBar;
        this.notEnoughFriendsLabel = notEnoughFriendsLabel;
    }

    @Override
    protected void onPreExecute() {
        this.progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected ArrayList<FriendListItem> doInBackground(Void... params) {

        ArrayList<Friend> allFriends = FriendsManager.getInstance().getFriends();
        Collections.sort(allFriends);


        ArrayList<FriendListItem> items = new ArrayList<FriendListItem>();

        for (Friend f : allFriends){
            items.add(new FriendListItem(f));
        }

        return items;
    }

    @Override
    protected void onPostExecute(final ArrayList<FriendListItem> friendListItems) {
        this.progressBar.setVisibility(View.GONE);

        if (friendListItems.size() < 3){

            notEnoughFriendsLabel.setVisibility(View.VISIBLE);
            int friendsCount = friendListItems.size();

            String message = "";
            switch (friendsCount){
                case 0:
                    message = "No one here yet. Would you like to invite some friends to join you?";
                    break;
                case 1:
                    message = "You currently have 1 friend. \nTo make sure your friend remains anonymous when messaging you, we will only make your friend list visible when you have more than 2 friends.";
                    break;
                case 2:
                    message = "You currently have 2 friends. \nTo make sure they remain anonymous when messaging you, we will only make your friend list visible when you have more than 2 friends.";
                    break;
            }

            notEnoughFriendsLabel.setText(message);
            friendListItems.clear();
        } else {
            adapter.addAll(friendListItems);
            adapter.notifyDataSetChanged();
        }

        new AsyncTask<Void, Void, ArrayList<String>>(){

            @Override
            protected ArrayList<String> doInBackground(Void... params) {
                return Blocker.getExplicitUsersBlocked();
            }

            @Override
            protected void onPostExecute(ArrayList<String> usernames) {

                for (FriendListItem item : friendListItems){
                    if (usernames.contains(item.friend.getUsername())){
                        item.friend.setBlocked(true);
                    }
                }

                adapter.notifyDataSetChanged();
            }
        }.execute();

    }
}
