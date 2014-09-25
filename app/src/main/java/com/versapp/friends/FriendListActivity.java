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
    ArrayList<Friend> selectedFriends;
    ArrayList<Friend> blockedFriends;
    ListView friendList;
    ArrayAdapter<Friend> adapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        friends = new ArrayList<Friend>();

        progressBar = (ProgressBar) findViewById(R.id.activity_friend_list_progress_bar);
        friendList = (ListView) findViewById(R.id.activity_friend_list_main_list);
        adapter = new FriendListArrayAdapter(this, friends);
        friendList.setAdapter(adapter);

        new LoadFriendsAT(adapter, progressBar).execute();

        String listMode = getIntent().getStringExtra(LIST_MODE_INTENT_EXTRA);
         if (listMode.equals(SINGLE_SELECTION_MODE)){
            setSingleSelectionMode(friendList);
        } else if (listMode.equals(MULTI_SELECTION_MODE)){
            setMultiSelectionMode(friendList);
        } else { // opts
            setOptionMode(friendList);
        }

    }

    private void setSingleSelectionMode(ListView list){

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(), "Single", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void setMultiSelectionMode(ListView list){

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(), "Multi", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void setOptionMode(ListView list){

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(), "Opts", Toast.LENGTH_SHORT).show();

            }
        });

    }

}
