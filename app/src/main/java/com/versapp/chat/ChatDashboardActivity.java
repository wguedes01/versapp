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
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.versapp.R;
import com.versapp.database.ChatsDAO;
import com.versapp.database.MessagesDAO;

import java.util.ArrayList;

public class ChatDashboardActivity extends Activity {

    private LruCache<String, Bitmap> imageCache;
    private ArrayList<Chat> chats;

    private MessagesDAO messagesDAO;
    private ChatsDAO chatsDAO;

    private NewMessageOnDashboardBR newMessageBR;
    private ReloadChatFromDBBR reloadChatsBR;

    GridView mainGrid;
    BaseAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dashboard);

        messagesDAO = new MessagesDAO(getApplicationContext());
        chatsDAO = new ChatsDAO(getApplicationContext());

        chats = ChatManager.getInstance().getChats();
        adapter = new ChatDashboardGridAdapter(getApplicationContext(), chats, messagesDAO, chatsDAO, getWindowManager().getDefaultDisplay(), imageCache);

        newMessageBR = new NewMessageOnDashboardBR();
        reloadChatsBR = new ReloadChatFromDBBR(getApplicationContext(), chats, adapter);

        imageCache = new LruCache<String, Bitmap>(5);

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

                chats.clear();
                chats.addAll(result);
                adapter.notifyDataSetChanged();
                super.onPostExecute(result);
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
        private ArrayList<Chat> chatList;
        private BaseAdapter adapter;

        private ReloadChatFromDBBR(Context context, ArrayList<Chat> chatList, BaseAdapter adapter) {
            this.db = new ChatsDAO(context);
            this.chatList = chatList;
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
                    chatList.clear();
                    chatList.addAll(result);
                    adapter.notifyDataSetChanged();
                    super.onPostExecute(result);
                }
            }.execute();

        }
    }
}
