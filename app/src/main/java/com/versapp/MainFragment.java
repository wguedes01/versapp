package com.versapp;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.versapp.friends.FriendListActivity;

/**
 * Created by william on 20/09/14.
 */
public class MainFragment extends Fragment {

    LinearLayout buttonsHolder;
    ImageButton newOneToOneBtn;
    ImageButton newGroupBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.fragment_main, container, false);

        buttonsHolder = (LinearLayout) convertView.findViewById(R.id.fragment_main_buttons_holder);
        newOneToOneBtn = (ImageButton) convertView.findViewById(R.id.fragment_main_new_one_to_one_btn);
        newGroupBtn = (ImageButton) convertView.findViewById(R.id.fragment_main_new_group_btn);

        newOneToOneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendListActivity.class);
                intent.putExtra(FriendListActivity.LIST_MODE_INTENT_EXTRA, FriendListActivity.MULTI_SELECTION_MODE);
                startActivity(intent);
            }
        });

        newGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendListActivity.class);
                intent.putExtra(FriendListActivity.LIST_MODE_INTENT_EXTRA, FriendListActivity.MULTI_SELECTION_MODE);
                startActivity(intent);
            }
        });

        Display display = getActivity().getWindowManager().getDefaultDisplay();

        int width = 0;
        int height = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;

        } else {
            width = display.getWidth();
            height = display.getHeight();
        }


        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) buttonsHolder.getLayoutParams();
        params.width = width;
        params.height = width;
        buttonsHolder.setLayoutParams(params);

        return convertView;
    }
}
