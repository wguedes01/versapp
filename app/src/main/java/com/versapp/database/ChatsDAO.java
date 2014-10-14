package com.versapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.versapp.Logger;
import com.versapp.chat.Chat;
import com.versapp.chat.ConfessionChat;
import com.versapp.chat.GroupChat;
import com.versapp.chat.OneToOneChat;
import com.versapp.chat.Participant;

import java.util.ArrayList;

/**
 * Created by william on 01/10/14.
 */
public class ChatsDAO {

    private Context context;
    private DBHelper helper;

    private ParticipantsDAO participantsDAO;

    public ChatsDAO(Context context) {
        this.context = context;
        this.helper =  DBHelper.getInstance(context);
        this.participantsDAO = new ParticipantsDAO(context);
    }

    public long insert(Chat chat){

            ContentValues chatValues = chatToContentValues(chat);

            SQLiteDatabase dbWritable = this.helper.getWritableDatabase();
            return dbWritable.insert(DBContract.ChatsTable.TABLE_NAME, null, chatValues);
    }

    public int update(Chat chat){

        ContentValues chatValues = chatToContentValues(chat);

        SQLiteDatabase dbWritable = this.helper.getWritableDatabase();
        return dbWritable.update(DBContract.ChatsTable.TABLE_NAME, chatValues, DBContract.ChatsTable.COLUMN_NAME_UUID + " = ?", new String[] { chat.getUuid() });
    }

    public void delete(String chatUUID){

        SQLiteDatabase dbWritable = this.helper.getWritableDatabase();
        dbWritable.delete(DBContract.ChatsTable.TABLE_NAME, DBContract.ChatsTable.COLUMN_NAME_UUID + " = ? ", new String[] { chatUUID });

    }

    public Chat get(String chatUUID){

        String[] projection = {
                DBContract.ChatsTable.COLUMN_NAME_UUID,
                DBContract.ChatsTable.COLUMN_NAME_TYPE,
                DBContract.ChatsTable.COLUMN_NAME_NAME,
                DBContract.ChatsTable.COLUMN_NAME_IS_OWNER,
                DBContract.ChatsTable.COLUMN_NAME_OWNER_ID,
                DBContract.ChatsTable.COLUMN_NAME_DEGREE,
                DBContract.ChatsTable.COLUMN_NAME_CID,
                DBContract.ChatsTable.COLUMN_NAME_LAST_OPENED_TIMESTAMP
        };

        String selection = DBContract.ChatsTable.COLUMN_NAME_UUID + " = ? ";

        String[] selectionArgs = { chatUUID };

        SQLiteDatabase dbReadable = this.helper.getReadableDatabase();
        Cursor cursor = dbReadable.query(DBContract.ChatsTable.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        if (cursor.getCount() <= 0){
            return null;
        } else {
            cursor.moveToFirst();
            return cursorToChat(cursor);
        }

    }

    public ArrayList<Chat> getAll(){

        ArrayList<Chat> chats = new ArrayList<Chat>();

        SQLiteDatabase dbReadable = this.helper.getReadableDatabase();

        //Cursor cursor = dbReadable.query(DBContract.ChatsTable.TABLE_NAME, projection, null, null, null, null, null);

        Cursor cursor = dbReadable.rawQuery("SELECT * from "
                + DBContract.ChatsTable.TABLE_NAME + " c LEFT JOIN "
                + DBContract.MessagesTable.TABLE_NAME + " m ON c." + DBContract.ChatsTable.COLUMN_NAME_UUID
                + " = m." + DBContract.MessagesTable.COLUMN_NAME_THREAD + " GROUP BY c."
                + DBContract.ChatsTable.COLUMN_NAME_UUID + " ORDER BY m."
                + DBContract.MessagesTable.COLUMN_NAME_TIMESTAMP + " DESC", null);

        if (cursor == null) {
            return chats;
        }

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            Chat chat = cursorToChat(cursor);

            chats.add(chat);

            cursor.moveToNext();
        }

        cursor.close();

        return chats;
    }

    private Chat cursorToChat(Cursor cursor){

        int uuidIndex = cursor.getColumnIndex(DBContract.ChatsTable.COLUMN_NAME_UUID);
        int typeIndex = cursor.getColumnIndex(DBContract.ChatsTable.COLUMN_NAME_TYPE);
        int nameIndex = cursor.getColumnIndex(DBContract.ChatsTable.COLUMN_NAME_NAME);
        int isOwnerIndex = cursor.getColumnIndex(DBContract.ChatsTable.COLUMN_NAME_IS_OWNER);
        int ownerIdIndex = cursor.getColumnIndex(DBContract.ChatsTable.COLUMN_NAME_OWNER_ID);
        int degreeIndex = cursor.getColumnIndex(DBContract.ChatsTable.COLUMN_NAME_DEGREE);
        int cidIndex = cursor.getColumnIndex(DBContract.ChatsTable.COLUMN_NAME_CID);
        int lastOpenedTimestampIndex = cursor.getColumnIndex(DBContract.ChatsTable.COLUMN_NAME_LAST_OPENED_TIMESTAMP);

        String uuid = cursor.getString(uuidIndex);
        String type = cursor.getString(typeIndex);
        String name = cursor.getString(nameIndex);
        long lastOpenedTimestamp = cursor.getLong(lastOpenedTimestampIndex);

        Chat chat = null;

        if (type.equals(OneToOneChat.TYPE)){

            boolean isOwner = cursor.getInt(isOwnerIndex) > 0 ? true : false;

            chat = new OneToOneChat(uuid, name, isOwner);
        } else if(type.equals(GroupChat.TYPE)){

            String ownerId = cursor.getString(ownerIdIndex);

            ArrayList<Participant> participants = new ParticipantsDAO(context).getAll(uuid);

            chat = new GroupChat(uuid, name, ownerId, participants);
        } else if(type.equals(ConfessionChat.TYPE)) {

            int degree = cursor.getInt(degreeIndex);
            long cid = cursor.getLong(cidIndex);

            chat = new ConfessionChat(uuid, name, cid, degree);
        } else {
            // invalid type.
            Logger.log(Logger.CHAT_DEBUG, "Trying to get chat of invalid type.");
        }

        chat.setLastOpenedTimestamp(lastOpenedTimestamp);
        return chat;
    }

    private ContentValues chatToContentValues(Chat chat){

        ContentValues chatValues = new ContentValues();

        // Generic attributes of a chat.
        chatValues.put(DBContract.ChatsTable.COLUMN_NAME_UUID, chat.getUuid());
        chatValues.put(DBContract.ChatsTable.COLUMN_NAME_TYPE, chat.getType());
        chatValues.put(DBContract.ChatsTable.COLUMN_NAME_NAME, chat.getName());

        if (chat.getType().equals(OneToOneChat.TYPE)){

            chatValues.put(DBContract.ChatsTable.COLUMN_NAME_IS_OWNER, ((OneToOneChat)chat).isOwner());

        } else if(chat.getType().equals(GroupChat.TYPE)) {

            chatValues.put(DBContract.ChatsTable.COLUMN_NAME_OWNER_ID, ((GroupChat)chat).getOwnerId());


            // Deleting all participants is faster than checking if it exists and then updating.
            participantsDAO.deleteAll(chat.getUuid());
            for (Participant p : ((GroupChat) chat).getParticipants()){
                participantsDAO.insert(chat.getUuid(), p);
            }

        } else if(chat.getType().equals(ConfessionChat.TYPE)) {

            chatValues.put(DBContract.ChatsTable.COLUMN_NAME_DEGREE, ((ConfessionChat)chat).getDegree());
            chatValues.put(DBContract.ChatsTable.COLUMN_NAME_CID, ((ConfessionChat)chat).getCid());

        } else {
            Logger.log(Logger.CHAT_DEBUG, "Trying to insert invalid type of chat.");
        }

        return chatValues;
    }

    public int updateLastOpenedTimestamp(String chatUUID){

        ContentValues values = new ContentValues();

        values.put(DBContract.ChatsTable.COLUMN_NAME_LAST_OPENED_TIMESTAMP, System.currentTimeMillis());

        SQLiteDatabase dbWritable = this.helper.getWritableDatabase();
        return dbWritable.update(DBContract.ChatsTable.TABLE_NAME, values, DBContract.ChatsTable.COLUMN_NAME_UUID + " = ?", new String[] { chatUUID });
    }

}
