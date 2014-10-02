package com.versapp;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

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
        this.context = context;
        this.manager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static NotificationManager getInstance(Context context) {

        if (instance == null) {
            instance = new NotificationManager(context);
        }

        return instance;
    }

    public void displayNewMessageNotification(String chatId, String body) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_new_message_owl).setContentTitle("New message").setContentText(body).setAutoCancel(true);

        int notificationId = chatUUIDNotificaitonIdMap.size();
        chatUUIDNotificaitonIdMap.put(chatId, notificationId);
        manager.notify(notificationId, mBuilder.build());
    }

    public boolean hasNotification(String uuid) {
        return chatUUIDNotificaitonIdMap.containsKey(uuid);
    }

    public void removeNotification(String uuid) {
        manager.cancel(chatUUIDNotificaitonIdMap.get(uuid));
        chatUUIDNotificaitonIdMap.remove(uuid);
    }

}
