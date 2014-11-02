package com.versapp.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.future.ImageViewFuture;
import com.versapp.Logger;
import com.versapp.R;
import com.versapp.chat.conversation.ConversationActivity;
import com.versapp.chat.conversation.Message;
import com.versapp.confessions.Confession;
import com.versapp.confessions.ConfessionManager;
import com.versapp.connection.ConnectionService;
import com.versapp.database.ChatsDAO;
import com.versapp.database.MessagesDAO;

/**
 * Created by william on 06/10/14.
 */
public class ChatDashboardGridAdapter extends BaseAdapter {

    private Context context;
    private MessagesDAO msgDb;
    private ChatsDAO chatsDAO;
    private LruCache<String, Bitmap> cache;

    Display display;
    int width = 0;
    int height = 0;

    public ChatDashboardGridAdapter(Context context, MessagesDAO db, ChatsDAO chatsDAO, Display display, LruCache<String, Bitmap> cache) {
        this.context = context;
        this.msgDb = db;
        this.chatsDAO = chatsDAO;
        this.cache = cache;

        this.display = display;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;

        } else {
            width = display.getWidth();
            height = display.getHeight();
        }

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Chat currentChat = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ViewHolder holder;
        //if (convertView == null) {

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
        //} else {
        //    holder = (ViewHolder) convertView.getTag();

        //    if (holder.task != null) {
         //       holder.task.cancel(true);
        //    }

        //}

        if (holder.imageViewFuture != null) {
            //holder.imageViewFuture.cancel();
            holder.imageViewFuture = null;
        }

        holder.task = null;

        // Ensures old image is not used when recycling a view.
        holder.backgroundImageView.setImageBitmap(null);
        holder.progressBar.setVisibility(View.GONE);

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

                    if (confession != null) {

                        if (confession.getImageUrl().startsWith("#")){

                            holder.backgroundImageView.setBackgroundColor(Color.parseColor(confession.getImageUrl()));

                        } else {
                            holder.backgroundImageView.setImageBitmap(null);
                            Log.d(Logger.CHAT_DEBUG, "Downloading image for: " + confession.getBody() + ": " + confession.getImageUrl());
                            ImageViewFuture imageViewFuture = Ion.with(context)
                                    .load("https://versapp.co/v2/download.php?cache="+confession.getId())//.noCache()
                                    .setBodyParameter("bucket", "msgpics")
                                    .setBodyParameter("name", confession.getImageUrl())
                                    .setBodyParameter("username", ConnectionService.getUser())
                                    .setBodyParameter("session", ConnectionService.getSessionId())
                                    .withBitmap()
                                    .error(android.R.drawable.alert_dark_frame)
                                    .animateIn(android.R.anim.fade_in)
                                    .intoImageView(holder.backgroundImageView);

                            // Attaches to holder so it can be cancelled when holder is "recycled".
                            holder.imageViewFuture = imageViewFuture;
                        }

                    } else {
                        // Set default background.
                        //holder.backgroundImageView.setBackgroundColor(context.getResources().getColor(R.color.confessionBlue));

                        // Indicate failed to retreive this confession, do not try again.
                        ((ConfessionChat) currentChat).setFailedToRetrieveConfession(true);
                    }

                    super.onPostExecute(confession);
                }

            };

            holder.task.execute(((ConfessionChat) currentChat).getCid());

            /*
            holder.iconHolder.setVisibility(View.GONE);
            holder.task = new LoadChatTileBackground(context, (ConfessionChat) currentChat, cache, holder.backgroundImageView, holder.progressBar);
            holder.task.execute();
            */

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

                Intent intent = new Intent(context, ConversationActivity.class);
                intent.putExtra(ConversationActivity.CHAT_UUID_INTENT_EXTRA, currentChat.getUuid());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return convertView;
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
        AsyncTask<Long, Void, Confession> task;
        View newMsgIcon;
        View iconHolder;
        ImageViewFuture imageViewFuture;
    }


}
