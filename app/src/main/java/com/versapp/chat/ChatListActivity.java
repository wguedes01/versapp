package com.versapp.chat;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.versapp.R;

import java.util.ArrayList;

public class ChatListActivity extends Activity {

    ArrayList<Chat> chats;
    BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        chats = new ArrayList<Chat>();

        GridView gridView = (GridView) findViewById(R.id.activity_chat_list_grid);
        adapter = new ChatListAdapter(this, chats, getWindowManager().getDefaultDisplay());
        gridView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {

        // populate chats.
        new AsyncTask<Void, Void, ArrayList<Chat>>(){

            @Override
            protected ArrayList<Chat> doInBackground(Void... params) {
                return ChatManager.getInstance().getJoinedChatsFromServer();
            }

            @Override
            protected void onPostExecute(ArrayList<Chat> result) {

                chats.addAll(result);
                adapter.notifyDataSetChanged();

                super.onPostExecute(result);
            }
        }.execute();

        super.onResume();
    }

    @Override
    protected void onPause() {

        // Remove chats from memory explicitly in order to improve memory management.
        chats.clear();

        super.onPause();
    }
}
