package com.versapp.chat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by william on 01/10/14.
 */
public class SynchronizeChatDB extends AsyncTask<Void, Void, Void> {

    public static final String CHAT_SYNCED_INTENT_ACTION = "CHAT_SYNCED_INTENT_ACTION";

    private Context context;

    public SynchronizeChatDB(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        ChatManager.getInstance().syncLocalChatDB(context);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        // send broadcast.
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(CHAT_SYNCED_INTENT_ACTION));
        System.out.println("Sent not br");

        super.onPostExecute(aVoid);
    }
}
