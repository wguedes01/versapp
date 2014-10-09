package com.versapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.versapp.chat.ChatManager;
import com.versapp.connection.CredentialsManager;
import com.versapp.connection.LoginAT;

/**
 * Created by william on 25/09/14.
 */
public class GCMIntentService extends IntentService  {

    private static final String PUSH_TYPE_CONFESSION = "confession";
    private static final String PUSH_TYPE_MESSAGE = "message";
    private static final String PUSH_TYPE_BLM_NEW_FRIEND = "blacklist";
    private static final String PUSH_TYPE_GROUP_INVITATION = "invitation";

    // General params
    private static final String GCM_INTENT_EXTRA_MESSAGE_CONTENT = "message";

    // Confession params
    private static final String GCM_CONFESSION_ID_INTENT_EXTRA = "confession_id";

    // BLM params
    private static final String GCM_INTENT_EXTRA_NEW_FRIEND_USERNAME= "username";

    public GCMIntentService() {
        super("GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(Logger.GCM_DEBUG, "Received GCM PUSH");

        if (CredentialsManager.getInstance(getApplicationContext()).getValidUsername() == null){
            return ;
        }

        String pushType = intent.getStringExtra("type");

        System.out.print("Extras: ");
        Bundle b = intent.getExtras();

        for (String key : b.keySet()) {
            Object value = b.get(key);
            Log.d(Logger.GCM_DEBUG, String.format("%s %s (%s)", key,
                    value.toString(), value.getClass().getName()));
        }

        if (pushType == null) {

        } else if (pushType.equals(PUSH_TYPE_CONFESSION)) {
            Log.d(Logger.GCM_DEBUG, "Received Confession Push. Type:" + pushType);

            long confessionId = Long.valueOf(intent.getStringExtra(GCM_CONFESSION_ID_INTENT_EXTRA));

            NotificationManager.getInstance(getApplicationContext()).displayConfessionFavoritedNotification(confessionId);

        } else if(pushType.equals(PUSH_TYPE_MESSAGE)) {


            if (CredentialsManager.getInstance(getApplicationContext()).getValidUsername() != null){

                String username = CredentialsManager.getInstance(getApplicationContext()).getValidUsername();
                String password = CredentialsManager.getInstance(getApplicationContext()).getValidPassword();

                new LoginAT(getApplicationContext(), new Runnable() {
                    @Override
                    public void run() {

                    }
                }, null).execute(username, password);

            }

            String body = intent.getStringExtra("body");

            Log.d(Logger.GCM_DEBUG, "Received Message Push. Type:" + pushType + ". Body: " + body);
        } else if(pushType.equals(PUSH_TYPE_BLM_NEW_FRIEND)){

            String username = intent.getStringExtra(GCM_INTENT_EXTRA_NEW_FRIEND_USERNAME);
            String messageContent = intent.getStringExtra(GCM_INTENT_EXTRA_MESSAGE_CONTENT);

            NotificationManager.getInstance(getApplicationContext()).displayBLMNewFriendNotification(messageContent, username);

        } else if(pushType.equals(PUSH_TYPE_GROUP_INVITATION)){

            ChatManager.getInstance().invalidatePendingChatCache();

            String messageContent = intent.getStringExtra(GCM_INTENT_EXTRA_MESSAGE_CONTENT);

            Log.d(Logger.GCM_DEBUG, "Received Message Push. Type:" + pushType + ". Message: " + messageContent);

            NotificationManager.getInstance(getApplicationContext()). displayGroupInvitationNotification(messageContent);

        } else {
            Log.d(Logger.GCM_DEBUG, "ERROR. INVALID PUSH TYPE. Type: " + pushType);
        }


    }
}
