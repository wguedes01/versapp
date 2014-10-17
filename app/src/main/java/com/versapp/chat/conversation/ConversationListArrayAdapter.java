package com.versapp.chat.conversation;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.versapp.GCSManager;
import com.versapp.R;

import java.util.ArrayList;

/**
 * Created by william on 29/09/14.
 */
public class ConversationListArrayAdapter extends ArrayAdapter<ChatMessage> {

    private Context context;
    private LruCache<String, Bitmap> cache;

    public ConversationListArrayAdapter(Context context, ArrayList<ChatMessage> messages, LruCache<String, Bitmap> cache) {
        super(context, R.layout.conversation_list_item, messages);
        this.context = context;
        this.cache = cache;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ChatMessage currentChatMessage = getItem(position);
        final Message currentMessage = currentChatMessage.message;

        Animation showAnimation = null;

        final ViewHolder holder = new ViewHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.conversation_list_item, parent, false);

        if (currentMessage.isMine()){
            holder.image = (ImageView) convertView.findViewById(R.id.conversation_list_item_message_image_mine);
            holder.body = (TextView) convertView.findViewById(R.id.conversation_list_item_message_content_mine);
            showAnimation = AnimationUtils.loadAnimation(context, R.anim.chat_bubble_right_anim);
        } else {
            holder.image = (ImageView) convertView.findViewById(R.id.conversation_list_item_message_image_theirs);
            holder.body = (TextView) convertView.findViewById(R.id.conversation_list_item_message_content_theirs);
            showAnimation = AnimationUtils.loadAnimation(context, R.anim.chat_bubble_left_anim);
        }

        if (currentMessage.getBody() != null && !currentMessage.getBody().trim().equals("")){
            holder.body.setVisibility(View.VISIBLE);
            holder.body.setText(currentMessage.getBody());
        }

        String imageUrl = currentMessage.getImageUrl();

        if (currentMessage.getImageUrl() != null  && !currentMessage.getImageUrl().equals("")) {
            holder.image.setVisibility(View.VISIBLE);

            if (cache.get(currentMessage.getImageUrl()) != null){
                holder.image.setImageBitmap(cache.get(currentMessage.getImageUrl()));
            } else {

                new AsyncTask<Void, Void, Bitmap>(){
                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        return GCSManager.getInstance(context).downloadImage(currentMessage.getImageUrl(), holder.image.getWidth(), holder.image.getHeight());
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {

                        if (bitmap != null){
                            holder.image.setImageBitmap(bitmap);
                            cache.put(currentMessage.getImageUrl(), bitmap);
                        }
                        super.onPostExecute(bitmap);
                    }
                }.execute();

            }
        }

        if (currentChatMessage.animate) {
            convertView.startAnimation(showAnimation);
            currentChatMessage.animate = false;
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView body;
        private ImageView image;
    }
}
