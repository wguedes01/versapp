package com.versapp.chat;

import android.app.ActionBar;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.versapp.R;

import java.util.ArrayList;

/**
 * Created by william on 10/31/14.
 */
public class ChatListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Chat> chats;

    Display display;
    int width = 0;

    public ChatListAdapter(Context context, ArrayList<Chat> chats, Display display) {
        this.context = context;
        this.chats = chats;


        this.display = display;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
        } else {
            width = display.getWidth();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Chat currentChat = chats.get(position);

        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_grid_item, parent, false);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.chat_grid_item_chat_name);
            holder.lastMsg = (TextView) convertView.findViewById(R.id.chat_grid_item_last_msg);
            holder.backgroundImage = (ImageView) convertView.findViewById(R.id.chat_grid_item_background_image);
            holder.container = convertView.findViewById(R.id.chat_grid_item_container);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (currentChat.getName().length() <= 20){
            holder.name.setTextSize(25);
        } else if((currentChat.getName().length() > 20) && (currentChat.getName().length() <= 35)) {
            holder.name.setTextSize(18);
        } else if((currentChat.getName().length() > 35) && (currentChat.getName().length() <= 50)) {
            holder.name.setTextSize(15);
        } else {
            holder.name.setTextSize(12);
        }

        holder.name.setText(currentChat.getName());
        holder.lastMsg.setText("test last message");
        //holder.backgroundImage

        GridView.LayoutParams params = (GridView.LayoutParams) convertView.getLayoutParams();
        params.height = width/2;
        convertView.setLayoutParams(params);

        if (position == 0){
            holder.container.setPadding(4, 4, 2, 2);
        } else if(position == 1){
            holder.container.setPadding(2, 4, 4, 2);
        } else if (position % 2 == 0){
            holder.container.setPadding(4, 2, 2, 2);
        } else {
            holder.container.setPadding(2, 2, 4, 2);
        }

        return convertView;
    }

    private class ViewHolder {
        TextView name;
        TextView lastMsg;
        ImageView backgroundImage;
        View container;
    }

    @Override
    public int getCount() {

        if (chats != null){
            return chats.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {

        if (chats != null) {
            return chats.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
