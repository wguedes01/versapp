package com.versapp.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.versapp.R;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by william on 24/09/14.
 */
public class FriendListArrayAdapter extends ArrayAdapter<FriendListItem> {

    private ArrayList<FriendListItem> allFriendItems =  new ArrayList<FriendListItem>();
    private ArrayList<FriendListItem> friendListItems;
    private String listMode;

    public FriendListArrayAdapter(Context context, ArrayList<FriendListItem> friends, String listMode) {
        super(context, R.layout.friend_list_item, friends);
        this.friendListItems = friends;
        this.listMode = listMode;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final FriendListItem friendListItem = friendListItems.get(position);
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
          // selectionIcon.setImageResource(R.drawable.friend_blocked_circle);
        } else if (friendListItem.isSelected()) {
            if (listMode.equals(FriendListActivity.MULTI_SELECTION_MODE)) {
                selectionIcon.setImageResource(R.drawable.friend_item_selected);
            } else {
                selectionIcon.setVisibility(View.GONE);
            }
        } else {
            if (listMode.equals(FriendListActivity.MULTI_SELECTION_MODE)) {
                selectionIcon.setImageResource(R.drawable.friend_item_unselected);
            } else {
                selectionIcon.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                ArrayList<FriendListItem> resultItems = new ArrayList<FriendListItem>();

                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0){
                    results.count = allFriendItems.size();
                    results.values = allFriendItems;
                } else {

                    if (allFriendItems != null){

                        System.out.println("Friends...");

                        for (FriendListItem item : allFriendItems){

                            if (item.friend.getName().toUpperCase().contains(constraint.toString().toUpperCase())){
                                System.out.println("Friend: " + item.friend.getName().toUpperCase());

                                resultItems.add(item);
                            }
                        }

                    }

                    results.count = resultItems.size();
                    results.values = resultItems;

                }


                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                friendListItems.clear();
                friendListItems.addAll((java.util.Collection<? extends FriendListItem>) results.values);
                notifyDataSetChanged();


            }
        };

        return filter;
    }

    @Override
    public void addAll(Collection<? extends FriendListItem> collection) {
        allFriendItems.addAll(collection);
        super.addAll(collection);
    }

}
