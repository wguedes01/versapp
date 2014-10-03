package com.versapp.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.LruCache;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.versapp.R;
import com.versapp.chat.conversation.ConversationActivity;
import com.versapp.chat.conversation.Message;
import com.versapp.database.ChatsDAO;
import com.versapp.database.MessagesDAO;

import java.util.ArrayList;

public class ChatDashboardActivity extends Activity {

    private LruCache<String, Bitmap> imageCache;
    private ArrayList<Chat> chats;

    private MessagesDAO messagesDAO;
    private ChatsDAO chatsDAO;

    private NewMessageOnDashboardBR newMessageBR;

    GridView mainGrid;
    BaseAdapter adapter;

    Display display;
    int width = 0;
    int height = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dashboard);

        newMessageBR = new NewMessageOnDashboardBR();

        messagesDAO = new MessagesDAO(getApplicationContext());
        chatsDAO = new ChatsDAO(getApplicationContext());

        imageCache = new LruCache<String, Bitmap>(5);

        display = getWindowManager().getDefaultDisplay();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;

        } else {
            width = display.getWidth();
            height = display.getHeight();
        }


        chats = ChatManager.getInstance().getChats();

        mainGrid = (GridView) findViewById(R.id.activity_chat_dashboard_main_grid);

        adapter = new GridAdapter(getApplicationContext(), chats, messagesDAO);

        mainGrid.setAdapter((android.widget.ListAdapter) adapter);

    }

    private class GridAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<Chat> chats;
        private MessagesDAO msgDb;

        private GridAdapter(Context context, ArrayList<Chat> chats, MessagesDAO db) {
            this.context = context;
            this.chats = chats;
            this.msgDb = db;
        }

        @Override
        public int getCount() {
            return chats.size();
        }

        @Override
        public Object getItem(int position) {
            return chats.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Chat currentChat = ChatDashboardActivity.this.chats.get(position);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

            final ViewHolder holder;
            //if (convertView == null){

                convertView = inflater.inflate(R.layout.chat_dashboard_item, parent, false);

                holder = new ViewHolder();
                holder.nameText = (TextView) convertView.findViewById(R.id.activity_chat_dashboard_chat_name);
                holder.lastMsgText = (TextView) convertView.findViewById(R.id.activity_chat_dashboard_last_message);
                holder.progressBar = convertView.findViewById(R.id.activity_chat_dashboard_progress_bar);
                holder.oneToOneUnknownIcon = (ImageView) convertView.findViewById(R.id.activity_chat_dashboard_one_to_one_tile_icon_unknown);
                holder.oneToOneOwnerIcon = (ImageView) convertView.findViewById(R.id.activity_chat_dashboard_one_to_one_tile_icon_owner);
                holder.groupIcon = (ImageView) convertView.findViewById(R.id.activity_chat_dashboard_group_tile_icon);
                holder.backgroundImageView = (ImageView) convertView.findViewById(R.id.activity_chat_dashboard_background_image_view);
            holder.newMsgIcon = convertView.findViewById(R.id.activity_chat_dashboard_new_message_icon);
            holder.iconHolder = convertView.findViewById(R.id.activity_chat_dashboard_ic_holder);

                convertView.setTag(holder);

            // Ensures old image is not used when recycling a view.
            //holder.backgroundImageView.setImageBitmap(null);
            //holder.progressBar.setVisibility(View.GONE);

            if (currentChat.getName().length() <= 20){
                holder.nameText.setTextSize(25);
            } else if((currentChat.getName().length() > 20) && (currentChat.getName().length() <= 35)) {
                holder.nameText.setTextSize(18);
            } else if((currentChat.getName().length() > 35) && (currentChat.getName().length() <= 50)) {
                holder.nameText.setTextSize(15);
            } else {
                holder.nameText.setTextSize(12);
            }

            holder.nameText.setText(currentChat.getName());

            holder.oneToOneOwnerIcon.setVisibility(View.GONE);
            holder.oneToOneUnknownIcon.setVisibility(View.GONE);
            holder.groupIcon.setVisibility(View.GONE);

            new AsyncTask<Void, Void, Message>(){

                @Override
                protected Message doInBackground(Void... params) {
                    return msgDb.getLastMessageByChat(currentChat.getUuid());
                }

                @Override
                protected void onPostExecute(Message msg) {


                    if (msg != null){

                        holder.lastMsgText.setText(msg.getBody());

                        // if last message timestamp is larger than last time opened. Highlight
                        if (currentChat.hasNewMessage(msg)){
                            holder.newMsgIcon.setVisibility(View.VISIBLE);
                        } else {
                            holder.newMsgIcon.setVisibility(View.GONE);
                        }

                    } else {
                        holder.lastMsgText.setText("");
                    }

                    super.onPostExecute(msg);
                }

            }.execute();

            holder.iconHolder.setVisibility(View.VISIBLE);

            if (currentChat instanceof ConfessionChat){

                holder.iconHolder.setVisibility(View.GONE);
                holder.task = new LoadChatTileBackground(getApplicationContext(), (ConfessionChat) currentChat, imageCache, holder.backgroundImageView, holder.progressBar);
                holder.task.execute();

            } else if(currentChat instanceof OneToOneChat) {

                if (((OneToOneChat) currentChat).isOwner()){
                    holder.oneToOneOwnerIcon.setVisibility(View.VISIBLE);
                } else {
                    holder.oneToOneUnknownIcon.setVisibility(View.VISIBLE);
                }

            } else {
                holder.groupIcon.setVisibility(View.VISIBLE);
            }

            adjustTitleSize(convertView);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                    intent.putExtra(ConversationActivity.CHAT_UUID_INTENT_EXTRA, currentChat.getUuid());
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }


    private void adjustTitleSize(View tile){

        GridView.LayoutParams params = (GridView.LayoutParams) tile.getLayoutParams();
        params.height = width/2;
        tile.setLayoutParams(params);

    }

    private class ViewHolder {

       TextView nameText;
       TextView lastMsgText;
       ImageView backgroundImageView;
       View progressBar;
       ImageView oneToOneUnknownIcon;
       ImageView oneToOneOwnerIcon;
       ImageView groupIcon;
       AsyncTask<Void, Void, Void> task;
       View newMsgIcon;
       View iconHolder;
    }

    @Override
    protected void onResume()
    {
        // This here will make chats reload every time the activity is opened.
        new AsyncTask<Void, Void, ArrayList<Chat>>(){

            @Override
            protected ArrayList<Chat> doInBackground(Void... params) {

                //return ChatManager.getInstance().getChatsFromServer();
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
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(newMessageBR);
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
}
