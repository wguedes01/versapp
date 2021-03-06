package com.versapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.versapp.chat.Chat;
import com.versapp.chat.ChatManager;
import com.versapp.chat.conversation.ConversationActivity;
import com.versapp.confessions.ViewSingleConfessionActivity;
import com.versapp.connection.ConnectionListener;
import com.versapp.connection.ConnectionService;
import com.versapp.connection.CredentialsManager;
import com.versapp.connection.LoginAT;
import com.versapp.database.ChatsDAO;
import com.versapp.friends.FriendListActivity;
import com.versapp.requests.RequestsActivity;

import java.util.ArrayList;


public class OpenActivityFromNotification extends Activity {

    public static final String OPEN_ACTIVITY_INTENT_EXTRA = "OPEN_ACTIVITY_INTENT_EXTRA";
    private boolean close = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_activity_from_notification);

        if (getIntent().getStringExtra(OPEN_ACTIVITY_INTENT_EXTRA).equals(ViewSingleConfessionActivity.class.getName())){

            final long confessionId = getIntent().getLongExtra(ViewSingleConfessionActivity.CONFESSION_ID_INTENT_EXTRA, -1);

            Logger.log(Logger.CONNECTION_DEBUG, "Confession Id" + confessionId);

            String username = CredentialsManager.getInstance(getApplicationContext()).getValidUsername();
            String password = CredentialsManager.getInstance(getApplicationContext()).getValidPassword();

            Logger.log(Logger.CONNECTION_DEBUG, "About to login");

            if (ConnectionService.getConnection() != null && ConnectionService.getConnection().isAuthenticated()){

                Intent intent = new Intent(getApplicationContext(), ViewSingleConfessionActivity.class);
                intent.putExtra(ViewSingleConfessionActivity.CONFESSION_ID_INTENT_EXTRA, confessionId);
                startActivity(intent);

                close = true;

            } else {

                new LoginAT(this, new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(getApplicationContext(), ViewSingleConfessionActivity.class);
                        intent.putExtra(ViewSingleConfessionActivity.CONFESSION_ID_INTENT_EXTRA, confessionId);
                        startActivity(intent);

                        close = true;
                    }
                }, null, null).execute(username, password);

            }


        } else if(getIntent().getStringExtra(OPEN_ACTIVITY_INTENT_EXTRA).equals(RequestsActivity.class.getName())){

            String username = CredentialsManager.getInstance(getApplicationContext()).getValidUsername();
            String password = CredentialsManager.getInstance(getApplicationContext()).getValidPassword();

            if (ConnectionService.getConnection() != null && ConnectionService.getConnection().isAuthenticated()){

                Intent intent = new Intent(getApplicationContext(), RequestsActivity.class);
                startActivity(intent);

                close = true;

            } else {

                new LoginAT(this, new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(getApplicationContext(), RequestsActivity.class);
                        startActivity(intent);

                        close = true;
                    }
                }, null, null).execute(username, password);

            }

        } else if(getIntent().getStringExtra(OPEN_ACTIVITY_INTENT_EXTRA).equals(ConversationActivity.class.getName())){

            String username = CredentialsManager.getInstance(getApplicationContext()).getValidUsername();
            String password = CredentialsManager.getInstance(getApplicationContext()).getValidPassword();

            if (ConnectionListener.reconnecting ||(ConnectionService.getConnection() != null && ConnectionService.getConnection().isAuthenticated())){

                Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                intent.putExtra(ConversationActivity.CHAT_UUID_INTENT_EXTRA, getIntent().getStringExtra(ConversationActivity.CHAT_UUID_INTENT_EXTRA));
                intent.putExtra(ConversationActivity.FROM_NOTIFICATION_INTENT_EXTRA, true);
                startActivity(intent);

                close = true;

            } else {

                new LoginAT(this, new Runnable() {
                    @Override
                    public void run() {

/*
                        if (ChatManager.getInstance().chatCount() == 0){

                            new AsyncTask<Void, Void, ArrayList<Chat>>(){

                                @Override
                                protected ArrayList<Chat> doInBackground(Void... params) {
                                    return new ChatsDAO(getApplicationContext()).getAll();
                                }

                                @Override
                                protected void onPostExecute(ArrayList<Chat> result) {
                                    ChatManager.getInstance().clearChats();
                                    ChatManager.getInstance().addAll(result);

                                    Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                                    intent.putExtra(ConversationActivity.CHAT_UUID_INTENT_EXTRA, getIntent().getStringExtra(ConversationActivity.CHAT_UUID_INTENT_EXTRA));
                                    intent.putExtra(ConversationActivity.FROM_NOTIFICATION_INTENT_EXTRA, true);
                                    startActivity(intent);

                                    super.onPostExecute(result);
                                }
                            }.execute();

                        } else {
*/
                            Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                            intent.putExtra(ConversationActivity.CHAT_UUID_INTENT_EXTRA, getIntent().getStringExtra(ConversationActivity.CHAT_UUID_INTENT_EXTRA));
                            intent.putExtra(ConversationActivity.FROM_NOTIFICATION_INTENT_EXTRA, true);
                            startActivity(intent);

                        //}



                        close = true;
                    }
                }, null, null).execute(username, password);

            }

        } else if(getIntent().getStringExtra(OPEN_ACTIVITY_INTENT_EXTRA).equals(FriendListActivity.class.getName())){


            String username = CredentialsManager.getInstance(getApplicationContext()).getValidUsername();
            String password = CredentialsManager.getInstance(getApplicationContext()).getValidPassword();

            if (ConnectionListener.reconnecting ||(ConnectionService.getConnection() != null && ConnectionService.getConnection().isAuthenticated())){

                Intent intent = new Intent(getApplicationContext(), FriendListActivity.class);
                intent.putExtra(FriendListActivity.LIST_MODE_INTENT_EXTRA, FriendListActivity.OPTS_MODE);
                startActivity(intent);

                close = true;

            } else {

                new LoginAT(this, new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(getApplicationContext(), FriendListActivity.class);
                        intent.putExtra(FriendListActivity.LIST_MODE_INTENT_EXTRA, FriendListActivity.OPTS_MODE);
                        startActivity(intent);

                        close = true;
                    }
                }, null, null).execute(username, password);

            }

        } else {

        }

    }

    @Override
    protected void onResume() {

        if (close){
            finish();
        }

        super.onResume();
    }
}
