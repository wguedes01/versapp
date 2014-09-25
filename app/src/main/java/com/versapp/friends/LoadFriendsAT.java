package com.versapp.friends;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by william on 25/09/14.
 */
public class LoadFriendsAT extends AsyncTask<Void, Void, ArrayList<FriendListItem>> {

    private ArrayAdapter<FriendListItem> adapter;
    private ProgressBar progressBar;

    public LoadFriendsAT(ArrayAdapter<FriendListItem> adapter, ProgressBar progressBar) {
        this.adapter = adapter;
        this.progressBar = progressBar;
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
        adapter.addAll(friendListItems);
        adapter.notifyDataSetChanged();


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
