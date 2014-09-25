package com.versapp.friends;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.versapp.R;

import java.util.ArrayList;

public class FriendListActivity extends Activity {

    ArrayList<Friend> friends;
    ListView friendList;
    ArrayAdapter<Friend> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        friends = new ArrayList<Friend>();

        friendList = (ListView) findViewById(R.id.activity_friend_list_main_list);
        adapter = new FriendListArrayAdapter(this, friends);
        friendList.setAdapter(adapter);

        new AsyncTask<Void, Void, ArrayList<Friend>>(){

            @Override
            protected ArrayList<Friend> doInBackground(Void... params) {
                return FriendsManager.getInstance().getFriends();
            }

            @Override
            protected void onPostExecute(ArrayList<Friend> result) {

                friends.addAll(result);
                adapter.notifyDataSetChanged();

                super.onPostExecute(result);
            }

        }.execute();



    }

}
