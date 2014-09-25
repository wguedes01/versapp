package com.versapp.friends;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
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

    ArrayList<FriendListItem> friendListItems;
    ArrayList<Friend> blockedFriends;
    ListView friendList;
    ArrayAdapter<FriendListItem> adapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

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

        setOnLongClickListener(friendList);

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

    private void setOnLongClickListener(ListView list) {
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(FriendListActivity.this);

                String[] options = { (friendListItems.get(position).friend.isBlocked() ? "Unblock" : "Block"), "Delete" };

                dialog.setItems(options, new Dialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0: // block/unblock

                                new AsyncTask<Void, Void, Void>() {

                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        friendListItems.get(position).friend.toggleBlock();
                                        return null;
                                    }

                                    protected void onPostExecute(Void result) {
                                        adapter.notifyDataSetChanged();
                                    };
                                }.execute();

                                break;
                            case 1: // remove

                                AlertDialog.Builder confirmDialog = new AlertDialog.Builder(FriendListActivity.this);
                                confirmDialog.setTitle("Delete Friend");
                                confirmDialog.setMessage(String.format("Are you sure you'd like to remove %s from your friends?", friendListItems.get(position).friend.getName()));
                                confirmDialog.setPositiveButton("Delete Friend", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        new AsyncTask<Void, Void, Void>() {

                                            @Override
                                            protected Void doInBackground(Void... params) {

                                                FriendsManager.getInstance().removeFriend(FriendListActivity.this, friendListItems.get(position).friend.getUsername());

                                                return null;
                                            }

                                            protected void onPostExecute(Void result) {
                                                friendListItems.remove(friendListItems.get(position));
                                                adapter.notifyDataSetChanged();
                                            };
                                        }.execute();

                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub

                                    }
                                }).show();

                                break;

                        }

                    }

                }).setCancelable(true).show();

                return true;
            }
        });
    }
}
