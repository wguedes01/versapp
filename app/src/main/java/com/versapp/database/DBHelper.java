package com.versapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by william on 23/09/14.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "versapp_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ", ";

    private static final String SQL_CREATE_FRIENDS_TABLE = "CREATE TABLE " + DBContract.FriendsTable.TABLE_NAME + " (" + DBContract.FriendsTable.COLUMN_NAME_USERNAME
            + " TEXT PRIMARY KEY, " + DBContract.FriendsTable.COLUMN_NAME_NAME + TEXT_TYPE + ", " + DBContract.FriendsTable.COLUMN_NAME_STATUS + INTEGER_TYPE + ")";

    private static final String SQL_DELETE_FRIENDS = "DROP TABLE IF EXISTS " + DBContract.FriendsTable.TABLE_NAME;

    private static DBHelper instance;

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBHelper getInstance(Context context) {

        if (instance == null) {
            instance = new DBHelper(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FRIENDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_FRIENDS);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
