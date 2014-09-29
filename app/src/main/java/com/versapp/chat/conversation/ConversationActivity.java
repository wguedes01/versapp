package com.versapp.chat.conversation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.versapp.R;
import com.versapp.chat.ChatMessageListener;
import com.versapp.connection.ConnectionManager;
import com.versapp.database.MessagesDAO;

import java.util.ArrayList;

public class ConversationActivity extends Activity {

    public static final String CHAT_UUID_INTENT_EXTRA = "CHAT_UUID_INTENT_EXTRA";
    private String chatUUID;

    BroadcastReceiver newMessageBR;

    private ArrayList<Message> messages;

    private ListView messagesListView;
    private ArrayAdapter adapter;
    private EditText messageEditText;
    private ImageButton sendMessageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        messages = new ArrayList<Message>();

        chatUUID = getIntent().getStringExtra(CHAT_UUID_INTENT_EXTRA);

        messagesListView = (ListView) findViewById(R.id.activity_conversation_main_list);
        messageEditText = (EditText) findViewById(R.id.activity_conversation_message_edit_text);
        sendMessageBtn = (ImageButton) findViewById(R.id.activity_conversation_send_msg_btn);

        adapter = new ConversationListArrayAdapter(getApplicationContext(), messages);
        messagesListView.setAdapter(adapter);

        newMessageBR = new MessageReceivedBR(chatUUID, adapter);

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Message message = new Message(chatUUID, messageEditText.getText().toString(), null, null, true);
                messages.add(message);
                adapter.notifyDataSetChanged();

                messageEditText.setText("");

                new AsyncTask<Void, Void, Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {

                        ConversationManager.getInstance(getApplicationContext()).sendMessage(message, chatUUID + "@" + ConnectionManager.SERVER_IP_ADDRESS);

                        return null;
                    }

                }.execute();


            }
        });

        new AsyncTask<Void, Void, ArrayList<Message>>(){

            @Override
            protected ArrayList<Message> doInBackground(Void... params) {
                return new MessagesDAO(getApplicationContext()).getAll(chatUUID);
            }

            @Override
            protected void onPostExecute(ArrayList<Message> msgs) {
                messages.addAll(msgs);
                super.onPostExecute(msgs);
            }
        }.execute();

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(newMessageBR);
        super.onPause();
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(newMessageBR, new IntentFilter(ChatMessageListener.NEW_MESSAGE_ACTION));
        super.onResume();
    }

}
