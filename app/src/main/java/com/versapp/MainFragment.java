package com.versapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.versapp.chat.Chat;
import com.versapp.chat.ChatDashboardActivity;
import com.versapp.chat.ChatListActivity;
import com.versapp.chat.ChatManager;
import com.versapp.chat.ChatMessageListener;
import com.versapp.chat.SynchronizeChatDB;
import com.versapp.database.ChatsDAO;
import com.versapp.database.MessagesDAO;
import com.versapp.friends.Friend;
import com.versapp.friends.FriendListActivity;
import com.versapp.friends.FriendRequestListener;
import com.versapp.friends.FriendsManager;
import com.versapp.requests.RequestsActivity;
import com.versapp.settings.SettingsActivity;

import java.util.ArrayList;

/**
 * Created by william on 20/09/14.
 */
public class MainFragment extends Fragment {

    View goToMessagesBtn;
    LinearLayout buttonsHolder;
    ImageButton newOneToOneBtn;
    ImageButton newGroupBtn;
    ImageButton settingsBtn;
    ImageView newMessageIcon;
    ImageView notificationsBtnBackground;
    ImageView notificationsBtn;

    MessagesDAO messagesDAO;

    private BroadcastReceiver updateNotificationCountBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Received not br");
            displayNewMessageNotificationCount();
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        messagesDAO = new MessagesDAO(getActivity().getApplicationContext());

        View convertView = inflater.inflate(R.layout.fragment_main, container, false);

        goToMessagesBtn = convertView.findViewById(R.id.go_to_msg_btn);
        settingsBtn = (ImageButton) convertView.findViewById(R.id.fragment_main_settings_btn);
        buttonsHolder = (LinearLayout) convertView.findViewById(R.id.fragment_main_buttons_holder);
        newOneToOneBtn = (ImageButton) convertView.findViewById(R.id.fragment_main_new_one_to_one_btn);
        newGroupBtn = (ImageButton) convertView.findViewById(R.id.fragment_main_new_group_btn);
        newMessageIcon = (ImageView) convertView.findViewById(R.id.fragment_main_new_message_icon);
        notificationsBtn= (ImageView) convertView.findViewById(R.id.fragment_main_notifications_btn);
        notificationsBtnBackground = (ImageView) convertView.findViewById(R.id.fragment_main_notifications_image);

        goToMessagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getActivity(), ChatDashboardActivity.class));
                startActivity(new Intent(getActivity(), ChatListActivity.class));
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });

        notificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RequestsActivity.class));
            }
        });

        newOneToOneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendListActivity.class);
                intent.putExtra(FriendListActivity.LIST_MODE_INTENT_EXTRA, FriendListActivity.SINGLE_SELECTION_MODE);
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

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateNotificationCountBR, new IntentFilter(ChatMessageListener.NEW_MESSAGE_ACTION));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateNotificationCountBR, new IntentFilter(SynchronizeChatDB.CHAT_SYNCED_INTENT_ACTION));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(friendRequestReceivedBR, new IntentFilter(FriendRequestListener.FRIEND_REQUEST_RECEIVED_INTENT_ACTION));

        displayNewMessageNotificationCount();

        displayNotificationCount();

        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateNotificationCountBR);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(friendRequestReceivedBR);

        super.onPause();
    }

    private void displayNewMessageNotificationCount(){


        int newMsgCount = 0;
        for(Chat chat : new ChatsDAO(getActivity()).getAll()){
            if (chat.hasNewMessage(messagesDAO.getLastMessageByChat(chat.getUuid()))){
                newMsgCount++;
            }
        }

        newMessageIcon.setVisibility(View.VISIBLE);
        switch (newMsgCount){
            case 0:
                newMessageIcon.setVisibility(View.GONE);
                break;
            case 1:
                newMessageIcon.setImageResource(R.drawable.new_message_icon_1);
                break;
            case 2:
                newMessageIcon.setImageResource(R.drawable.new_message_icon_2);
                break;
            case 3:
                newMessageIcon.setImageResource(R.drawable.new_message_icon_3);
                break;
            case 4:
                newMessageIcon.setImageResource(R.drawable.new_message_icon_4);
                break;
            case 5:
                newMessageIcon.setImageResource(R.drawable.new_message_icon_5);
                break;
            default:
                newMessageIcon.setImageResource(R.drawable.new_message_icon_5_plus);
                break;
        }

    }

    private void displayNotificationCount(){

        new AsyncTask<Void, Void, Integer>(){

            @Override
            protected Integer doInBackground(Void... params) {

                ArrayList<Friend> pendingFriends = FriendsManager.getInstance().getPendingFriends();

                ArrayList<Chat> pendingChat = ChatManager.getInstance().getPendingChats();

                if (pendingChat != null && pendingFriends != null){
                    return pendingChat.size() + pendingFriends.size();
                }

                return 0;
            }

            @Override
            protected void onPostExecute(Integer integer) {

                switch (integer){
                    case 0:
                        notificationsBtnBackground.setImageResource(R.drawable.dashboard_notification_count_0);
                        break;
                    case 1:
                        notificationsBtnBackground.setImageResource(R.drawable.dashboard_notification_count_1);
                        break;
                    case 2:
                        notificationsBtnBackground.setImageResource(R.drawable.dashboard_notification_count_2);
                        break;
                    case 3:
                        notificationsBtnBackground.setImageResource(R.drawable.dashboard_notification_count_3);
                        break;
                    case 4:
                        notificationsBtnBackground.setImageResource(R.drawable.dashboard_notification_count_4);
                        break;
                    case 5:
                        notificationsBtnBackground.setImageResource(R.drawable.dashboard_notification_count_5);
                        break;
                    default:
                        notificationsBtnBackground.setImageResource(R.drawable.dashboard_notification_count_5_plus);
                        break;
                }

                super.onPostExecute(integer);
            }

        }.execute();


    }


    private BroadcastReceiver friendRequestReceivedBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            displayNotificationCount();
        }
    };

}
