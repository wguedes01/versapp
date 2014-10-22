package com.versapp.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.LruCache;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.versapp.MainApplication;
import com.versapp.R;
import com.versapp.database.ChatsDAO;
import com.versapp.database.MessagesDAO;

import java.util.ArrayList;

public class ChatDashboardActivity extends Activity {

    private LruCache<String, Bitmap> imageCache;

    private MessagesDAO messagesDAO;
    private ChatsDAO chatsDAO;

    private NewMessageOnDashboardBR newMessageBR;
    private ReloadChatFromDBBR reloadChatsBR;

    GridView mainGrid;
    BaseAdapter adapter;

    TextView noConversationsLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dashboard);

        Tracker tracker = ((MainApplication) getApplicationContext()).getTracker();
        tracker.setScreenName("ChatDashboardActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());

        noConversationsLabel = (TextView) findViewById(R.id.activity_chat_dashboard_no_chats_message);

        imageCache = new LruCache<String, Bitmap>(5);

        messagesDAO = new MessagesDAO(getApplicationContext());
        chatsDAO = new ChatsDAO(getApplicationContext());

        adapter = new ChatDashboardGridAdapter(getApplicationContext(), messagesDAO, chatsDAO, getWindowManager().getDefaultDisplay(), imageCache);

        newMessageBR = new NewMessageOnDashboardBR();
        reloadChatsBR = new ReloadChatFromDBBR(getApplicationContext(), adapter);

        mainGrid = (GridView) findViewById(R.id.activity_chat_dashboard_main_grid);

        mainGrid.setAdapter((android.widget.ListAdapter) adapter);
    }


    @Override
    protected void onResume()
    {
        // This here will make chats reload every time the activity is opened.
        new AsyncTask<Void, Void, ArrayList<Chat>>(){

            @Override
            protected ArrayList<Chat> doInBackground(Void... params) {

                return chatsDAO.getAll();
            }

            @Override
            protected void onPostExecute(ArrayList<Chat> result) {

                ChatManager.getInstance().clearChats();

                if (result != null){
                    if (result.size() == 0) {
                        noConversationsLabel.setVisibility(View.VISIBLE);
                    } else {
                        noConversationsLabel.setVisibility(View.GONE);
                        ChatManager.getInstance().addAll(result);
                        super.onPostExecute(result);
                    }
                }

                adapter.notifyDataSetChanged();


            }
        }.execute();


        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(newMessageBR, new IntentFilter(ChatMessageListener.NEW_MESSAGE_ACTION));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(reloadChatsBR, new IntentFilter(SynchronizeChatDB.CHAT_SYNCED_INTENT_ACTION));
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(newMessageBR);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(reloadChatsBR);
        super.onPause();
    }

    private class NewMessageOnDashboardBR extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String chatIdOnMessage = intent.getStringExtra(ChatMessageListener.CHAT_ID_ON_MESSAGE_INTENT_EXTRA);

            if (chatIdOnMessage != null){
                ChatManager.getInstance().moveToTop(chatIdOnMessage);
                adapter.notifyDataSetChanged();
            }

        }
    }

    private class ReloadChatFromDBBR extends BroadcastReceiver{

        private ChatsDAO db;
        private BaseAdapter adapter;

        private ReloadChatFromDBBR(Context context, BaseAdapter adapter) {
            this.db = new ChatsDAO(context);
            this.adapter = adapter;
        }

        @Override
        public void onReceive(Context context, Intent intent) {


            new AsyncTask<Void, Void, ArrayList<Chat>>(){

                @Override
                protected ArrayList<Chat> doInBackground(Void... params) {
                    return db.getAll();
                }

                @Override
                protected void onPostExecute(ArrayList<Chat> result) {
                    ChatManager.getInstance().clearChats();
                    ChatManager.getInstance().addAll(result);
                    adapter.notifyDataSetChanged();
                    super.onPostExecute(result);
                }
            }.execute();

        }
    }
}
