package com.versapp.chat.conversation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.versapp.R;

import java.util.ArrayList;

/**
 * Created by william on 29/09/14.
 */
public class ConversationListArrayAdapter extends ArrayAdapter<Message> {

    private Context context;
    private ArrayList<Message> messages;

    public ConversationListArrayAdapter(Context context, ArrayList<Message> messages) {
        super(context, R.layout.conversation_list_item, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Message currentMessage = messages.get(position);

        ViewHolder holder;
        //if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.conversation_list_item, parent, false);

            convertView.setTag(holder);
        //} else {
           // holder = (ViewHolder) convertView.getTag();
       // }

        if (currentMessage.isMine()){
            holder.body = (TextView) convertView.findViewById(R.id.conversation_list_item_message_content_mine);
        } else {
            holder.body = (TextView) convertView.findViewById(R.id.conversation_list_item_message_content_theirs);
        }

        holder.body.setVisibility(View.VISIBLE);
        holder.body.setText(currentMessage.getBody());

        return convertView;
    }

    private class ViewHolder {
        private TextView body;
    }
}
