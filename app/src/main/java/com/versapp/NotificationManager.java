package com.versapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.versapp.chat.ChatDashboardActivity;
import com.versapp.chat.ChatManager;
import com.versapp.chat.conversation.ConversationActivity;
import com.versapp.confessions.ViewSingleConfessionActivity;
import com.versapp.friends.FriendListActivity;
import com.versapp.requests.RequestsActivity;
import com.versapp.settings.SettingsActivity;

import java.util.HashMap;

/**
 * Created by william on 02/10/14.
 */
public class NotificationManager {

    private static NotificationManager instance;
    private Context context;
    android.app.NotificationManager manager;

    private HashMap<String, Integer> chatUUIDNotificaitonIdMap = new HashMap<String, Integer>();

    private NotificationManager(Context context) {
        this.context = context.getApplicationContext();
        this.manager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static NotificationManager getInstance(Context context) {

        if (instance == null) {
            instance = new NotificationManager(context);
        }

        return instance;
    }

    public void displayConfessionFavoritedNotification(final long confessionId){

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setContentTitle("Yay!").setContentText("Someone liked your thought!").setAutoCancel(true);

        Intent homeIntent = new Intent(context, DashboardActivity.class);

        Intent intent = new Intent(context, OpenActivityFromNotification.class);
        intent.putExtra(ViewSingleConfessionActivity.CONFESSION_ID_INTENT_EXTRA, confessionId);
        intent.putExtra(OpenActivityFromNotification.OPEN_ACTIVITY_INTENT_EXTRA, ViewSingleConfessionActivity.class.getName());

        PendingIntent pendingIntent = PendingIntent.getActivities(context, 0, new Intent[] {homeIntent, intent}, PendingIntent.FLAG_ONE_SHOT);

        mBuilder.setContentIntent(pendingIntent);
        if (SettingsActivity.isNotificationEnabled(context)){
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.setVibrate(new long[] { 0, 100, 200, 300 });
        }

        manager.notify(100, mBuilder.build());

    }

    public void displayNewMessageNotification(final String chatId, String body) {

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setContentTitle("New message").setContentText(body).setAutoCancel(true);

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {

                if (ChatManager.getInstance().chatCount() == 0){
                    ChatManager.getInstance().addAll(ChatManager.getInstance().syncLocalChatDB(context));
                } else {

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                Intent homeIntent = new Intent(context, DashboardActivity.class);

                Intent backIntent = new Intent(context, ChatDashboardActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

                Intent intent = new Intent(context, OpenActivityFromNotification.class);
                intent.putExtra(ConversationActivity.CHAT_UUID_INTENT_EXTRA, chatId);
                intent.putExtra(ConversationActivity.FROM_NOTIFICATION_INTENT_EXTRA, true);
                intent.putExtra(OpenActivityFromNotification.OPEN_ACTIVITY_INTENT_EXTRA, ConversationActivity.class.getName());

                PendingIntent pendingIntent = PendingIntent.getActivities(context, 0, new Intent[] {homeIntent, backIntent, intent}, PendingIntent.FLAG_ONE_SHOT);

                mBuilder.setContentIntent(pendingIntent);
                if (SettingsActivity.isNotificationEnabled(context)){
                    mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    mBuilder.setVibrate(new long[] { 0, 100, 200, 300 });
                }


                int notificationId = chatUUIDNotificaitonIdMap.size();

                if (chatUUIDNotificaitonIdMap.get(chatId) == null){
                    chatUUIDNotificaitonIdMap.put(chatId, notificationId);
                    manager.notify(notificationId, mBuilder.build());
                } else {
                    manager.notify(chatUUIDNotificaitonIdMap.get(chatId), mBuilder.build());
                }



                super.onPostExecute(aVoid);
            }
        }.execute();

    }

    public boolean hasNotification(String uuid) {
        return chatUUIDNotificaitonIdMap.containsKey(uuid);
    }

    public void removeNotification(String uuid) {
        manager.cancel(chatUUIDNotificaitonIdMap.get(uuid));
        chatUUIDNotificaitonIdMap.remove(uuid);
    }

    public void displayBLMNewFriendNotification(String message, String username) {

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setContentTitle("You've got a new friend").setContentText(message).setAutoCancel(true);

        Intent homeIntent = new Intent(context, DashboardActivity.class);

        Intent intent = new Intent(context, OpenActivityFromNotification.class);
        intent.putExtra(OpenActivityFromNotification.OPEN_ACTIVITY_INTENT_EXTRA, FriendListActivity.class.getName());

        PendingIntent pendingIntent = PendingIntent.getActivities(context, 0, new Intent[] {homeIntent, intent}, PendingIntent.FLAG_ONE_SHOT);

        mBuilder.setContentIntent(pendingIntent);
        if (SettingsActivity.isNotificationEnabled(context)){
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.setVibrate(new long[] { 0, 100, 200, 300 });
        }

        manager.notify(500, mBuilder.build());
    }

    public void displayGroupInvitationNotification(String message) {

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setContentTitle("Group Invitation").setContentText(message).setAutoCancel(true);

        Intent homeIntent = new Intent(context, DashboardActivity.class);

        Intent intent = new Intent(context, OpenActivityFromNotification.class);
        intent.putExtra(OpenActivityFromNotification.OPEN_ACTIVITY_INTENT_EXTRA, RequestsActivity.class.getName());

        PendingIntent pendingIntent = PendingIntent.getActivities(context, 0, new Intent[] {homeIntent, intent}, PendingIntent.FLAG_ONE_SHOT);

        mBuilder.setContentIntent(pendingIntent);
        if (SettingsActivity.isNotificationEnabled(context)){
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.setVibrate(new long[] { 0, 100, 200, 300 });
        }

        manager.notify(300, mBuilder.build());

    }

}
