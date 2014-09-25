package com.versapp.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.versapp.R;

import java.util.ArrayList;

/**
 * Created by william on 24/09/14.
 */
public class FriendListArrayAdapter extends ArrayAdapter<FriendListItem> {

    private ArrayList<FriendListItem> friends;
    private String listMode;


    public FriendListArrayAdapter(Context context, ArrayList<FriendListItem> friends, String listMode) {
        super(context, R.layout.friend_list_item, friends);
        this.friends = friends;
        this.listMode = listMode;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final FriendListItem friendListItem = friends.get(position);
        final Friend friend = friendListItem.friend;

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.friend_list_item, parent, false);

        TextView nameText = (TextView) convertView.findViewById(R.id.friend_list_item_name);
        TextView usernameText = (TextView) convertView.findViewById(R.id.friend_list_item_username);
        ImageView selectionIcon = (ImageView) convertView.findViewById(R.id.friend_list_select_friend_icon);

        nameText.setText(friend.getName());
        usernameText.setText(friend.getUsername());

        // Always set text purple.
        nameText.setTextColor(getContext().getResources().getColor(R.color.purpleBeauty));

        if (friend.isBlocked()){
            nameText.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
            selectionIcon.setImageResource(R.drawable.friend_blocked_circle);
        } else if (friendListItem.isSelected()) {
            if (listMode.equals(FriendListActivity.MULTI_SELECTION_MODE)) {
                selectionIcon.setImageResource(R.drawable.select_friend_filled);
            } else {
                selectionIcon.setVisibility(View.GONE);
            }
        } else {
            if (listMode.equals(FriendListActivity.MULTI_SELECTION_MODE)) {
                selectionIcon.setImageResource(R.drawable.select_friend_outline);
            } else {
                selectionIcon.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

}
