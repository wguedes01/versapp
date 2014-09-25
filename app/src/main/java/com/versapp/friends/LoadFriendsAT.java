package com.versapp.friends;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Created by william on 25/09/14.
 */
public class LoadFriendsAT extends AsyncTask<Void, Void, ArrayList<Friend>> {

    private ArrayAdapter<Friend> adapter;
    private ProgressBar progressBar;

    public LoadFriendsAT(ArrayAdapter<Friend> adapter, ProgressBar progressBar) {
        this.adapter = adapter;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        this.progressBar.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected ArrayList<Friend> doInBackground(Void... params) {

        ArrayList<Friend> allFriends = FriendsManager.getInstance().getFriends();

        return allFriends;
    }

    @Override
    protected void onPostExecute(final ArrayList<Friend> friends) {
        this.progressBar.setVisibility(View.GONE);
        adapter.addAll(friends);
        adapter.notifyDataSetChanged();


        new AsyncTask<Void, Void, ArrayList<String>>(){

            @Override
            protected ArrayList<String> doInBackground(Void... params) {
                return Blocker.getExplicitUsersBlocked();
            }

            @Override
            protected void onPostExecute(ArrayList<String> usernames) {

                for (Friend f : friends){
                    if (usernames.contains(f.getUsername())){
                        f.setBlocked(true);
                    }
                }

                adapter.notifyDataSetChanged();
            }
        }.execute();

    }
}
