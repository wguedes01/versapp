package com.versapp.chat;

import android.app.ActionBar;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;
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

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.future.ImageViewFuture;
import com.versapp.Logger;
import com.versapp.R;
import com.versapp.chat.conversation.Message;
import com.versapp.confessions.Confession;
import com.versapp.confessions.ConfessionManager;
import com.versapp.connection.ConnectionService;
import com.versapp.database.MessagesDAO;

import java.util.ArrayList;

/**
 * Created by william on 10/31/14.
 */
public class ChatListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Chat> chats;
    MessagesDAO messagesDAO;

    Display display;
    int width = 0;

    public ChatListAdapter(Context context, ArrayList<Chat> chats, Display display, MessagesDAO messagesDAO) {
        this.context = context;
        this.chats = chats;
        this.messagesDAO = messagesDAO;


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

        final ViewHolder holder;
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

        if (holder.task != null){
            holder.task.cancel(true);
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

        // Populate last message.
        new AsyncTask<Void, Void, Message>(){

            @Override
            protected Message doInBackground(Void... params) {
                return messagesDAO.getLastMessageByChat(currentChat.getUuid());
            }

            @Override
            protected void onPostExecute(Message msg) {


                if (msg != null){

                    holder.lastMsg.setText(msg.getBody());

                    // if last message timestamp is larger than last time opened. Highlight
                    if (currentChat.hasNewMessage(msg)){
                        //holder.lastMsg.setVisibility(View.VISIBLE);
                    } else {
                        //holder.lastMsg.setVisibility(View.GONE);
                    }

                } else {
                    holder.lastMsg.setText("");
                }

                super.onPostExecute(msg);
            }

        }.execute();

        holder.backgroundImage.setImageBitmap(null);
        holder.backgroundImage.setBackgroundColor(context.getResources().getColor(R.color.confessionBlue));
        //holder.backgroundImage
        if (currentChat instanceof ConfessionChat){

            holder.task = new AsyncTask<Long, Void, Confession>(){

                @Override
                protected Confession doInBackground(Long... params) {
                    long confessionId = params[0];

                    if (((ConfessionChat) currentChat).getConfession() != null) {
                        return ((ConfessionChat) currentChat).getConfession();
                    }

                    if (!((ConfessionChat) currentChat).isFailedToRetrieveConfession()){
                        return ConfessionManager.getInstance().getConfessionFromServer(context, confessionId);
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Confession confession) {

                    if (isCancelled())
                        return;

                    if (confession != null) {

                        if (confession.getImageUrl().startsWith("#")){

                            holder.backgroundImage.setBackgroundColor(Color.parseColor(confession.getImageUrl()));

                        } else {
                            holder.backgroundImage.setImageBitmap(null);
                            ImageViewFuture imageViewFuture = Ion.with(context)
                                    .load("https://versapp.co/v2/download.php?cache="+confession.getId())//.noCache()
                                    .setBodyParameter("bucket", "msgpics")
                                    .setBodyParameter("name", confession.getImageUrl())
                                    .setBodyParameter("username", ConnectionService.getUser())
                                    .setBodyParameter("session", ConnectionService.getSessionId())
                                    .withBitmap()
                                    .error(android.R.drawable.alert_dark_frame)
                                    .animateIn(android.R.anim.fade_in)
                                    .intoImageView(holder.backgroundImage);
                        }

                    } else {
                        // Indicate failed to retreive this confession, do not try again.
                        ((ConfessionChat) currentChat).setFailedToRetrieveConfession(true);
                    }

                    super.onPostExecute(confession);
                }

            };

            holder.task.execute(((ConfessionChat) currentChat).getCid());


        }

        // Adjust square.
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
        AsyncTask<Long, Void, Confession> task;
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
