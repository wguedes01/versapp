package com.versapp.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.versapp.R;

import java.util.ArrayList;

/**
 * Created by william on 24/09/14.
 */
public class FriendListArrayAdapter extends ArrayAdapter<Friend> {

    private ArrayList<Friend> friends;

    public FriendListArrayAdapter(Context context, ArrayList<Friend> friends) {
        super(context, R.layout.friend_list_item, friends);
        this.friends = friends;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Friend friend = friends.get(position);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.friend_list_item, parent, false);

        TextView nameText = (TextView) convertView.findViewById(R.id.friend_list_item_name);
        TextView usernameText = (TextView) convertView.findViewById(R.id.friend_list_item_username);

        nameText.setText(friend.getName());
        usernameText.setText(friend.getUsername());

        return convertView;
    }
}
