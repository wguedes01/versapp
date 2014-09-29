package com.versapp.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.versapp.Logger;
import com.versapp.R;
import com.versapp.confessions.Confession;
import com.versapp.confessions.ConfessionManager;

import java.util.ArrayList;

public class ChatDashboardActivity extends Activity {

    private LruCache<String, Bitmap> imageCache;
    ArrayList<ChatTile> chatTiles;

    GridView mainGrid;
    BaseAdapter adapter;

    Display display;
    int width = 0;
    int height = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dashboard);

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


        chatTiles = new ArrayList<ChatTile>();

        mainGrid = (GridView) findViewById(R.id.activity_chat_dashboard_main_grid);

        adapter = new GridAdapter(getApplicationContext(), chatTiles);

        mainGrid.setAdapter((android.widget.ListAdapter) adapter);

        new AsyncTask<Void, Void, ArrayList<ChatTile>>(){

            @Override
            protected ArrayList<ChatTile> doInBackground(Void... params) {

                ArrayList<Chat> chats = ChatManager.getInstance().getChatsFromServer();

                ArrayList<ChatTile> tiles = new ArrayList<ChatTile>();

                Log.d(Logger.CHAT_DEBUG, "Got chats. Count: " + chats.size());
                for(Chat c : chats){
                    tiles.add(new ChatTile(c));
                }

                return tiles;
            }

            @Override
            protected void onPostExecute(ArrayList<ChatTile> tiles) {

                chatTiles.addAll(tiles);
                adapter.notifyDataSetChanged();
                super.onPostExecute(tiles);
            }
        }.execute();

    }

    private class GridAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<ChatTile> tiles;

        private GridAdapter(Context context, ArrayList<ChatTile> tiles) {
            this.context = context;
            this.tiles = tiles;
        }

        @Override
        public int getCount() {
            return tiles.size();
        }

        @Override
        public Object getItem(int position) {
            return tiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ChatTile currentChatTile = chatTiles.get(position);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

            final ViewHolder holder;
            if (convertView == null){

                convertView = inflater.inflate(R.layout.chat_dashboard_item, parent, false);

                holder = new ViewHolder();
                holder.nameText = (TextView) convertView.findViewById(R.id.activity_chat_dashboard_chat_name);
                holder.lastMsgText = (TextView) convertView.findViewById(R.id.activity_chat_dashboard_last_message);
                holder.backgroundImageView = (ImageView) convertView.findViewById(R.id.activity_chat_dashboard_background_image_view);
                holder.progressBar = (ProgressBar) convertView.findViewById(R.id.activity_chat_dashboard_progress_bar);
                holder.oneToOneIcon = (ImageView) convertView.findViewById(R.id.activity_chat_dashboard_one_to_one_tile_icon);
                holder.groupIcon = (ImageView) convertView.findViewById(R.id.activity_chat_dashboard_group_tile_icon);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.nameText.setText(currentChatTile.chat.getName());
            holder.lastMsgText.setText("Stub..");

            holder.oneToOneIcon.setVisibility(View.GONE);
            holder.groupIcon.setVisibility(View.GONE);

            if (currentChatTile.chat instanceof ConfessionChat){

                Confession confession = null;

                new AsyncTask<Void, Void, Confession>(){

                    @Override
                    protected Confession doInBackground(Void... params) {

                        return ConfessionManager.getInstance().getConfessionFromServer(getApplicationContext(), ((ConfessionChat) currentChatTile.chat).getCid());

                    }

                    @Override
                    protected void onPostExecute(Confession confession) {

                        if (confession != null) {

                            if (confession.getImageUrl().startsWith("#")) {
                                holder.backgroundImageView.setBackgroundColor(Color.parseColor(confession.getImageUrl()));
                            } else {
                                new LoadChatTileBackground(getApplicationContext(), imageCache,  confession.getImageUrl(), holder.backgroundImageView, holder.progressBar).execute();
                            }

                        } else {

                        }

                        super.onPostExecute(confession);
                    }

                }.execute();


            } else if(currentChatTile.chat instanceof OneToOneChat) {
                holder.oneToOneIcon.setVisibility(View.VISIBLE);

            } else {
                holder.groupIcon.setVisibility(View.VISIBLE);
            }

            adjustTitleSize(convertView);

            return convertView;
        }
    }

    private void adjustTitleSize(View tile){


        GridView.LayoutParams params = (GridView.LayoutParams) tile.getLayoutParams();
        params.height = width/2;
        tile.setLayoutParams(params);

    }

    private class ChatTile {

        public Chat chat;

        private ChatTile(Chat chat) {
            this.chat = chat;
        }
    }

    private class ViewHolder {

       TextView nameText;
       TextView lastMsgText;
       ImageView backgroundImageView;
       ProgressBar progressBar;
       ImageView oneToOneIcon;
       ImageView groupIcon;

    }



}
