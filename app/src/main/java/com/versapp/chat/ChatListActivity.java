package com.versapp.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.method.CharacterPickerDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.versapp.R;
import com.versapp.chat.conversation.ChatMessage;
import com.versapp.chat.conversation.ConversationActivity;
import com.versapp.database.ChatsDAO;
import com.versapp.database.MessagesDAO;

import java.util.ArrayList;

public class ChatListActivity extends Activity {

    ArrayList<Chat> chats;
    BaseAdapter adapter;
    ChatsDAO chatsDAO;
    MessagesDAO messagesDAO;

    NewMessageOnDashboardBR newMessageBR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        chatsDAO = new ChatsDAO(getApplicationContext());
        messagesDAO = new MessagesDAO(getApplicationContext());
        chats = new ArrayList<Chat>();

        newMessageBR = new NewMessageOnDashboardBR();

        GridView gridView = (GridView) findViewById(R.id.activity_chat_list_grid);
        adapter = new ChatListAdapter(this, chats, getWindowManager().getDefaultDisplay(), messagesDAO);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChatListActivity.this, ConversationActivity.class);
                intent.putExtra(ConversationActivity.CHAT_UUID_INTENT_EXTRA, chats.get(position).getUuid());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(newMessageBR, new IntentFilter(ChatMessageListener.NEW_MESSAGE_ACTION));


        // populate chats.
        new AsyncTask<Void, Void, ArrayList<Chat>>(){

            @Override
            protected ArrayList<Chat> doInBackground(Void... params) {
                //return ChatManager.getInstance().getJoinedChatsFromServer();
                return chatsDAO.getAll();
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

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(newMessageBR);

        // Remove chats from memory explicitly in order to improve memory management.
        chats.clear();

        super.onPause();
    }

    private class NewMessageOnDashboardBR extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String chatIdOnMessage = intent.getStringExtra(ChatMessageListener.CHAT_ID_ON_MESSAGE_INTENT_EXTRA);
            boolean isNewChat = intent.getBooleanExtra(ChatMessageListener.IS_NEW_CHAT_INTENT_EXTRA, false);

            if (isNewChat) {
                chats.add(0, chatsDAO.get(chatIdOnMessage));
            } else {
                moveChatToTop(chatIdOnMessage);
            }
            
            adapter.notifyDataSetChanged();

        }
    }

    private Chat popChat(String chatUUID){

        int i = 0;
        while(i < chats.size()){

            if (chats.get(i).getUuid().equals(chatUUID)){
                Chat chat = chats.get(i);
                chats.remove(i);
                return chat;
            }

            i++;
        }

        return null;
    }

    private void moveChatToTop(String chatUUID){

        Chat chat = popChat(chatUUID);

        // should never be null.
        //if (chat != null) {
          chats.add(0, chat);
        //} else {

        //}



    }


}
