package com.versapp.chat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.versapp.DashboardActivity;

import java.io.IOException;

/**
 * Created by william on 27/09/14.
 */
public class CreateChatAT extends AsyncTask<Void, Void, Chat>{

    Context context;
    ChatBuilder builder;

    public CreateChatAT(Context context, ChatBuilder builder) {
        this.context = context;
        this.builder = builder;
    }


    @Override
    protected Chat doInBackground(Void... params) {

        Chat chat = null;
        try {
            chat = ChatManager.getInstance().createChat(builder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return chat;
    }

    @Override
    protected void onPostExecute(Chat chat) {

        if (chat == null) {
            // error occurred.
            Toast.makeText(context, "Oops.. something went wrong. Please try again.", Toast.LENGTH_LONG).show();
        } else {
            // go to chat.
            Intent intent = new Intent(context, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }

        super.onPostExecute(chat);
    }

}
