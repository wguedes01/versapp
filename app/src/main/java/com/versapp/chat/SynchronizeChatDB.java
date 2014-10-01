package com.versapp.chat;

import android.content.Context;
import android.os.AsyncTask;

import com.versapp.database.ChatsDAO;

import java.util.ArrayList;

/**
 * Created by william on 01/10/14.
 */
public class SynchronizeChatDB extends AsyncTask<Void, Void, Void> {

    private Context context;

    public SynchronizeChatDB(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        ChatsDAO chatsDb = new ChatsDAO(context);

        ArrayList<Chat> chats = ChatManager.getInstance().getChatsFromServer();

        for (Chat c : chats){

            if (chatsDb.get(c.getUuid()) == null){
                chatsDb.insert(c);
            } else {
                chatsDb.update(c);
            }



        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        // send broadcast.
        super.onPostExecute(aVoid);
    }
}
