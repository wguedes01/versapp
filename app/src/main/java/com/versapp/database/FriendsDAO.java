package com.versapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.versapp.friends.Friend;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by william on 23/09/14.
 */
public class FriendsDAO {

    private Context context;
    private DBHelper helper;
    private ArrayList<Friend> friends;

    public FriendsDAO(Context context) {
        this.context = context;
        this.helper =  DBHelper.getInstance(context);
    }

    public void insert(Friend friend, String status) {

        if (get(friend.getUsername()) == null) {

            ContentValues friendValues = new ContentValues();

            friendValues.put(DBContract.FriendsTable.COLUMN_NAME_USERNAME, friend.getUsername());
            friendValues.put(DBContract.FriendsTable.COLUMN_NAME_NAME, friend.getName());
            friendValues.put(DBContract.FriendsTable.COLUMN_NAME_STATUS, status);

            SQLiteDatabase dbWritable = this.helper.getWritableDatabase();
            dbWritable.insert(DBContract.FriendsTable.TABLE_NAME, null, friendValues);

        } else {
            update(friend, status);
        }

    }

    public void delete(String username) {
        SQLiteDatabase dbWritable = this.helper.getWritableDatabase();
        dbWritable.delete(DBContract.FriendsTable.TABLE_NAME, DBContract.FriendsTable.COLUMN_NAME_USERNAME + " = ? ", new String[] { username });
    }

    public Friend get(String username) {

        if (username == null) {
            return null;
        }

        String[] projection = { DBContract.FriendsTable.COLUMN_NAME_NAME, DBContract.FriendsTable.COLUMN_NAME_STATUS };

        String selection = DBContract.FriendsTable.COLUMN_NAME_USERNAME + " = ? ";

        String[] selectionArgs = { username };

        SQLiteDatabase dbReadable = this.helper.getReadableDatabase();

        Cursor cursor = dbReadable.query(DBContract.FriendsTable.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        Friend friend = null;

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex(DBContract.FriendsTable.COLUMN_NAME_NAME));
            int status = cursor.getInt(cursor.getColumnIndex(DBContract.FriendsTable.COLUMN_NAME_STATUS));

            friend = new Friend(username, name);
        }

        return friend;
    }


    public void update(Friend friend, String status) {

        ContentValues updateValues = new ContentValues();

        updateValues.put(DBContract.FriendsTable.COLUMN_NAME_NAME, friend.getName());
        updateValues.put(DBContract.FriendsTable.COLUMN_NAME_STATUS, status);

        SQLiteDatabase dbWritable = this.helper.getWritableDatabase();
        dbWritable.update(DBContract.FriendsTable.TABLE_NAME, updateValues, DBContract.FriendsTable.COLUMN_NAME_USERNAME + " = ?", new String[] { friend.getUsername() });
    }

    public ArrayList<Friend> getFriends() {

        ArrayList<Friend> friends = getAllWhere(Friend.ACCEPTED);

        // Add blocked friends also so they are displayed in the friend's list.
        friends.addAll(getAllWhere(Friend.BLOCKED));

        Collections.sort(friends);

        return friends;
    }

    private ArrayList<Friend> getAllWhere(String status) {

        ArrayList<Friend> friends = new ArrayList<Friend>();

        String[] projection = { DBContract.FriendsTable.COLUMN_NAME_USERNAME, DBContract.FriendsTable.COLUMN_NAME_NAME };

        String selection = DBContract.FriendsTable.COLUMN_NAME_STATUS + " = ? ";

        String[] selectionArgs = { String.valueOf(status) };

        SQLiteDatabase dbReadable = this.helper.getReadableDatabase();

        Cursor cursor = dbReadable.query(DBContract.FriendsTable.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return friends;
        }

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            int usernameIndex = cursor.getColumnIndex(DBContract.FriendsTable.COLUMN_NAME_USERNAME);
            int nameIndex = cursor.getColumnIndex(DBContract.FriendsTable.COLUMN_NAME_NAME);

            String username = cursor.getString(usernameIndex);
            String name = cursor.getString(nameIndex);

            friends.add(new Friend(username, name));

            cursor.moveToNext();
        }

        cursor.close();

        return friends;
    }

}
