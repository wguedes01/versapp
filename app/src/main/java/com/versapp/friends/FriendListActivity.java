package com.versapp.friends;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.versapp.R;
import com.versapp.chat.CreateChatAT;
import com.versapp.chat.GroupChatBuilder;
import com.versapp.chat.OneToOneChatBuilder;

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
    EditText searchEdit;
    ImageButton backBtn;
    TextView createGroupBtn;

    TextView notEnoughFriendsLabel;

    // used to store the username of selected friends when creating groups
    private ArrayList<String> selectedUsers = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        notEnoughFriendsLabel = (TextView) findViewById(R.id.activity_friend_list_not_enough_friends_message);

        friendListItems = new ArrayList<FriendListItem>();

        backBtn = (ImageButton) findViewById(R.id.activity_friend_back_btn);
        createGroupBtn = (TextView) findViewById(R.id.activity_friend_create_group_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchEdit = (EditText) findViewById(R.id.search_friend_edit);


        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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


        new LoadFriendsAT(adapter, progressBar, notEnoughFriendsLabel).execute();

    }

    private void setSingleSelectionMode(ListView list){

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if (friendListItems.get(position).friend.isBlocked()){

                    Toast.makeText(getApplicationContext(), "You can't interact with blocked friend(s)", Toast.LENGTH_SHORT).show();

                } else {

                    new CreateChatAT(FriendListActivity.this, new OneToOneChatBuilder(friendListItems.get(position).friend.getUsername())).execute();

                }

            }
        });

    }

    private void setMultiSelectionMode(ListView list){

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // If there are 2 or more friends selected, show option to create group.
                createGroupBtn.setVisibility(View.VISIBLE);

                if (friendListItems.get(position).friend.isBlocked()){

                    Toast.makeText(getApplicationContext(), "You can't interact with blocked friends", Toast.LENGTH_SHORT).show();

                } else {

                    if (friendListItems.get(position).isSelected()){
                        selectedUsers.remove(friendListItems.get(position).friend.getUsername());
                    } else {
                        selectedUsers.add(friendListItems.get(position).friend.getUsername());
                    }

                    friendListItems.get(position).setSelected(!friendListItems.get(position).isSelected());
                    adapter.notifyDataSetChanged();

                }


            }
        });


        createGroupBtn.setVisibility(View.VISIBLE);
        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedUsers.size() > 1){


                    final EditText chatNameEdit = (EditText) new EditText(getApplicationContext());

                    AlertDialog.Builder createGroupChatDialog = new AlertDialog.Builder(FriendListActivity.this);
                    createGroupChatDialog.setView(chatNameEdit);
                    createGroupChatDialog.setTitle("Group Name");
                    createGroupChatDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final String chatName = chatNameEdit.getText().toString();

                            if (chatName.length() > 0){
                                new CreateChatAT(FriendListActivity.this, new GroupChatBuilder(new ArrayList<String>(selectedUsers), chatName)).execute();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid name", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
                    createGroupChatDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    createGroupChatDialog.show();

                } else {
                    Toast.makeText(getApplicationContext(), "Select at least 2 friends", Toast.LENGTH_SHORT).show();
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

                                                FriendsManager.getInstance().removeFriend(friendListItems.get(position).friend.getUsername());

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
