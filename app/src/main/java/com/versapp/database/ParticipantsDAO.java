package com.versapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.versapp.chat.Participant;

import java.util.ArrayList;

/**
 * Created by william on 07/10/14.
 */
public class ParticipantsDAO {

    private Context context;
    private DBHelper helper;

    public ParticipantsDAO(Context context) {
        this.context = context;
        this.helper =  DBHelper.getInstance(context);
    }

    public long insert(String uuid, Participant participant){

        ContentValues participantValues = new ContentValues();

        participantValues.put(DBContract.ParticipantsTable.COLUMN_NAME_UUID, uuid);
        participantValues.put(DBContract.ParticipantsTable.COLUMN_NAME_USERNAME, participant.getUsername());
        participantValues.put(DBContract.ParticipantsTable.COLUMN_NAME_NAME, participant.getName());

        return this.helper.getWritableDatabase().insert(DBContract.ParticipantsTable.TABLE_NAME, null, participantValues);
    }

    //update

    //delete

    public ArrayList<Participant> getAll(String chatUUID){

        ArrayList<Participant> participants = new ArrayList<Participant>();

        String[] projection = {
                DBContract.ParticipantsTable.COLUMN_NAME_USERNAME,
                DBContract.ParticipantsTable.COLUMN_NAME_NAME,
        };

        String selection = DBContract.ParticipantsTable.COLUMN_NAME_UUID + " = ? ";

        String[] selectionArgs = { chatUUID };

        SQLiteDatabase dbReadable = this.helper.getReadableDatabase();

        Cursor cursor = dbReadable.query(DBContract.ParticipantsTable.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        if (cursor.getCount() > 0){
            cursor.moveToFirst();
        }

        while(!cursor.isAfterLast()) {

            String username = cursor.getString(cursor.getColumnIndex(DBContract.ParticipantsTable.COLUMN_NAME_USERNAME));
            String name = cursor.getString(cursor.getColumnIndex(DBContract.ParticipantsTable.COLUMN_NAME_NAME));

            participants.add(new Participant(username, name));

            cursor.moveToNext();

        }

        cursor.close();

        return participants;
    }


    public void deleteAll(String uuid) {

        SQLiteDatabase dbWritable = this.helper.getWritableDatabase();
        dbWritable.delete(DBContract.ParticipantsTable.TABLE_NAME, DBContract.ParticipantsTable.COLUMN_NAME_UUID + " = ? ", new String[] { uuid });

    }
}
