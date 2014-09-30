package com.versapp.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.versapp.database.MessagesDAO;

import java.util.ArrayList;

public class ChatDashboardActivity extends Activity {

    private LruCache<String, Bitmap> imageCache;
    private ArrayList<Chat> chats;

    private MessagesDAO messagesDAO;

    GridView mainGrid;
    BaseAdapter adapter;

    Display display;
    int width = 0;
    int height = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dashboard);

        messagesDAO = new MessagesDAO(getApplicationContext());

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

        // This here will make chats reload every time the activity is opened.
        new AsyncTask<Void, Void, ArrayList<Chat>>(){

            @Override
            protected ArrayList<Chat> doInBackground(Void... params) {

                return ChatManager.getInstance().getChatsFromServer();
            }

            @Override
            protected void onPostExecute(ArrayList<Chat> result) {

                chats.clear();
                chats.addAll(result);
                adapter.notifyDataSetChanged();
                super.onPostExecute(result);
            }
        }.execute();

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
                holder.oneToOneIcon = (ImageView) convertView.findViewById(R.id.activity_chat_dashboard_one_to_one_tile_icon);
                holder.groupIcon = (ImageView) convertView.findViewById(R.id.activity_chat_dashboard_group_tile_icon);
                holder.backgroundImageView = (ImageView) convertView.findViewById(R.id.activity_chat_dashboard_background_image_view);

                convertView.setTag(holder);

            // Ensures old image is not used when recycling a view.
            //holder.backgroundImageView.setImageBitmap(null);
            //holder.progressBar.setVisibility(View.GONE);

            holder.nameText.setText(currentChat.getName());

            holder.oneToOneIcon.setVisibility(View.GONE);
            holder.groupIcon.setVisibility(View.GONE);


            new AsyncTask<Void, Void, Message>(){

                @Override
                protected Message doInBackground(Void... params) {
                    return msgDb.getLastMessageByChat(currentChat.getUuid());
                }

                @Override
                protected void onPostExecute(Message msg) {

                    String lastMsg = "";

                    if (msg != null){
                        lastMsg = msg.getBody();
                    }

                    holder.lastMsgText.setText(lastMsg);

                    super.onPostExecute(msg);
                }

            }.execute();


            if (currentChat instanceof ConfessionChat){

                holder.task = new LoadChatTileBackground(getApplicationContext(), (ConfessionChat) currentChat, imageCache, holder.backgroundImageView, holder.progressBar);
                holder.task.execute();

            } else if(currentChat instanceof OneToOneChat) {
                holder.oneToOneIcon.setVisibility(View.VISIBLE);

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
       ImageView oneToOneIcon;
       ImageView groupIcon;
       AsyncTask<Void, Void, Void> task;
    }

    @Override
    protected void onResume() {
        adapter.notifyDataSetChanged();
        super.onResume();
    }

}