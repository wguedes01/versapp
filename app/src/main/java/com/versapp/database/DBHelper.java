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

    private static final String SQL_CREATE_MESSAGES_TABLE = "CREATE TABLE " +  DBContract.MessagesTable.TABLE_NAME + " (" + DBContract.MessagesTable.COLUMN_NAME_MESSAGE_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DBContract.MessagesTable.COLUMN_NAME_TIMESTAMP + TEXT_TYPE + COMMA_SEP
            + DBContract.MessagesTable.COLUMN_NAME_MESSAGE_BODY + TEXT_TYPE + COMMA_SEP + DBContract.MessagesTable.COLUMN_NAME_IMAGE_URL + TEXT_TYPE + COMMA_SEP
            + DBContract.MessagesTable.COLUMN_NAME_THREAD + TEXT_TYPE + COMMA_SEP + DBContract.MessagesTable.COLUMN_NAME_IS_MINE + INTEGER_TYPE + ")";

    private static final String SQL_CREATE_CHATS_TABLE = "CREATE TABLE " + DBContract.ChatsTable.TABLE_NAME + " (" +
            DBContract.ChatsTable.COLUMN_NAME_UUID + TEXT_TYPE + " PRIMARY KEY " + COMMA_SEP +
            DBContract.ChatsTable.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
            DBContract.ChatsTable.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
            DBContract.ChatsTable.COLUMN_NAME_IS_OWNER + INTEGER_TYPE + COMMA_SEP +
            DBContract.ChatsTable.COLUMN_NAME_OWNER_ID + TEXT_TYPE + COMMA_SEP +
            DBContract.ChatsTable.COLUMN_NAME_DEGREE + INTEGER_TYPE + COMMA_SEP +
            DBContract.ChatsTable.COLUMN_NAME_CID + INTEGER_TYPE + COMMA_SEP +
            DBContract.ChatsTable.COLUMN_NAME_LAST_OPENED_TIMESTAMP + INTEGER_TYPE + " DEFAULT 0" + ")";


    private static final String SQL_CREATE_PARTICIPANTS_TABLE = "CREATE TABLE " + DBContract.ParticipantsTable.TABLE_NAME + " (" +
            DBContract.ParticipantsTable.COLUMN_NAME_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT " + COMMA_SEP +
            DBContract.ParticipantsTable.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
            DBContract.ParticipantsTable.COLUMN_NAME_USERNAME + TEXT_TYPE + COMMA_SEP +
            DBContract.ParticipantsTable.COLUMN_NAME_NAME + TEXT_TYPE + ")";

    private static final String SQL_CREATE_REPORTED_CONFESSIONS_TABLE = "CREATE TABLE " + DBContract.ReportedConfessionsTable.TABLE_NAME + " (" +
            DBContract.ReportedConfessionsTable.COLUMN_NAME_ID + INTEGER_TYPE + " PRIMARY KEY " + ")";

    private static final String SQL_DELETE_FRIENDS = "DROP TABLE IF EXISTS " + DBContract.FriendsTable.TABLE_NAME;

    private static final String SQL_DELETE_MESSAGES = "DROP TABLE IF EXISTS " + DBContract.MessagesTable.TABLE_NAME;

    private static final String SQL_DELETE_CHATS = "DROP TABLE IF EXISTS " + DBContract.ChatsTable.TABLE_NAME;

    private static final String SQL_DELETE_PARTICIPANTS = "DROP TABLE IF EXISTS " + DBContract.ParticipantsTable.TABLE_NAME;

    private static final String SQL_DELETE_REPORTED_CONFESSIONS = "DROP TABLE IF EXISTS " + DBContract.ReportedConfessionsTable.TABLE_NAME;

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
        db.execSQL(SQL_CREATE_MESSAGES_TABLE);
        db.execSQL(SQL_CREATE_CHATS_TABLE);
        db.execSQL(SQL_CREATE_PARTICIPANTS_TABLE);
        db.execSQL(SQL_CREATE_REPORTED_CONFESSIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_FRIENDS);
        db.execSQL(SQL_DELETE_MESSAGES);
        db.execSQL(SQL_DELETE_CHATS);
        db.execSQL(SQL_DELETE_PARTICIPANTS);
        db.execSQL(SQL_DELETE_REPORTED_CONFESSIONS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
