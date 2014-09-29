package com.versapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.versapp.chat.conversation.Message;

import java.util.ArrayList;

/**
 * Created by william on 29/09/14.
 */
public class MessagesDAO {

    private Context context;
    private DBHelper helper;

    public MessagesDAO(Context context) {
        this.context = context;
        this.helper =  DBHelper.getInstance(context);
    }

    public long insert(Message message){

        ContentValues messageValues = new ContentValues();

        messageValues.put(DBContract.MessagesTable.COLUMN_NAME_MESSAGE_BODY, message.getBody());
        messageValues.put(DBContract.MessagesTable.COLUMN_NAME_TIMESTAMP, message.getTimestmap());
        messageValues.put(DBContract.MessagesTable.COLUMN_NAME_IMAGE_URL, message.getImageUrl());
        messageValues.put(DBContract.MessagesTable.COLUMN_NAME_THREAD, message.getThread());
        messageValues.put(DBContract.MessagesTable.COLUMN_NAME_IS_MINE, (message.isMine() ? 1 : 0));

        return this.helper.getWritableDatabase().insert(DBContract.MessagesTable.TABLE_NAME, null, messageValues);
    }

    public Message get(long id){

        String[] projection = { DBContract.MessagesTable.COLUMN_NAME_IS_MINE,
                DBContract.MessagesTable.COLUMN_NAME_THREAD,
                DBContract.MessagesTable.COLUMN_NAME_IMAGE_URL,
                DBContract.MessagesTable.COLUMN_NAME_MESSAGE_BODY,
                DBContract.MessagesTable.COLUMN_NAME_TIMESTAMP};

        String selection = DBContract.MessagesTable.COLUMN_NAME_MESSAGE_ID + " = ? ";

        String[] selectionArgs = { String.valueOf(id) };

        SQLiteDatabase dbReadable = this.helper.getReadableDatabase();

        Cursor cursor = dbReadable.query(DBContract.MessagesTable.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        Message message = null;

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            boolean isMine = (cursor.getInt(cursor.getColumnIndex(DBContract.MessagesTable.COLUMN_NAME_IS_MINE)) == 1) ? true : false;
            String thread = cursor.getString(cursor.getColumnIndex(DBContract.MessagesTable.COLUMN_NAME_THREAD));
            String imageUrl = cursor.getString(cursor.getColumnIndex(DBContract.MessagesTable.COLUMN_NAME_IMAGE_URL));
            String body = cursor.getString(cursor.getColumnIndex(DBContract.MessagesTable.COLUMN_NAME_MESSAGE_BODY));
            String timestamp = cursor.getString(cursor.getColumnIndex(DBContract.MessagesTable.COLUMN_NAME_TIMESTAMP));

            message = new Message(thread, body, imageUrl, timestamp, isMine);
        }

        cursor.close();

        return message;

    }

    public ArrayList<Message> getAll(String chatUUID){

        ArrayList<Message> messages = new ArrayList<Message>();

        String[] projection = { DBContract.MessagesTable.COLUMN_NAME_IS_MINE,
                DBContract.MessagesTable.COLUMN_NAME_THREAD,
                DBContract.MessagesTable.COLUMN_NAME_IMAGE_URL,
                DBContract.MessagesTable.COLUMN_NAME_MESSAGE_BODY,
                DBContract.MessagesTable.COLUMN_NAME_TIMESTAMP};

        String selection = DBContract.MessagesTable.COLUMN_NAME_THREAD + " = ? ";

        String[] selectionArgs = { chatUUID };

        SQLiteDatabase dbReadable = this.helper.getReadableDatabase();

        Cursor cursor = dbReadable.query(DBContract.MessagesTable.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        if (cursor.getCount() > 0){
            cursor.moveToFirst();
        }

        while(!cursor.isAfterLast()) {

            boolean isMine = (cursor.getInt(cursor.getColumnIndex(DBContract.MessagesTable.COLUMN_NAME_IS_MINE)) == 1) ? true : false;
            String thread = cursor.getString(cursor.getColumnIndex(DBContract.MessagesTable.COLUMN_NAME_THREAD));
            String imageUrl = cursor.getString(cursor.getColumnIndex(DBContract.MessagesTable.COLUMN_NAME_IMAGE_URL));
            String body = cursor.getString(cursor.getColumnIndex(DBContract.MessagesTable.COLUMN_NAME_MESSAGE_BODY));
            String timestamp = cursor.getString(cursor.getColumnIndex(DBContract.MessagesTable.COLUMN_NAME_TIMESTAMP));

            messages.add(new Message(thread, body, imageUrl, timestamp, isMine));

            cursor.moveToNext();
        }

        cursor.close();

        return messages;
    }

}
