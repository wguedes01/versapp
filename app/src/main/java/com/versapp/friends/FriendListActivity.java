package com.versapp.friends;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.versapp.R;

import java.util.ArrayList;

public class FriendListActivity extends Activity {

    public static final String LIST_MODE_INTENT_EXTRA = "list_mode";
    public static final String SINGLE_SELECTION_MODE = "single_selection_mode";
    public static final String MULTI_SELECTION_MODE = "MULTI_selection_mode";
    public static final String OPTS_MODE = "opts_mode";

    ArrayList<Friend> friends;
    ArrayList<FriendListItem> friendListItems;
    ArrayList<Friend> blockedFriends;
    ListView friendList;
    ArrayAdapter<FriendListItem> adapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        friends = new ArrayList<Friend>();
        friendListItems = new ArrayList<FriendListItem>();

        progressBar = (ProgressBar) findViewById(R.id.activity_friend_list_progress_bar);
        friendList = (ListView) findViewById(R.id.activity_friend_list_main_list);

        String listMode = getIntent().getStringExtra(LIST_MODE_INTENT_EXTRA);
         if (listMode.equals(SINGLE_SELECTION_MODE)){
            setSingleSelectionMode(friendList);
        } else if (listMode.equals(MULTI_SELECTION_MODE)){
            setMultiSelectionMode(friendList);
        } else { // opts
            setOptionMode(friendList);
        }



        adapter = new FriendListArrayAdapter(this, friendListItems, listMode);
        friendList.setAdapter(adapter);

        new LoadFriendsAT(adapter, progressBar).execute();

    }

    private void setSingleSelectionMode(ListView list){

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                if (friendListItems.get(position).friend.isBlocked()){

                    Toast.makeText(getApplicationContext(), "You can't interact with blocked friends", Toast.LENGTH_SHORT).show();

                } else {

                    // Start one-to-one conversation

                }

            }
        });

    }

    private void setMultiSelectionMode(ListView list){

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (friendListItems.get(position).friend.isBlocked()){

                    Toast.makeText(getApplicationContext(), "You can't interact with blocked friends", Toast.LENGTH_SHORT).show();

                } else {

                    friendListItems.get(position).setSelected(!friendListItems.get(position).isSelected());
                    adapter.notifyDataSetChanged();

                }


            }
        });

    }

    private void setOptionMode(ListView list){

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

}
