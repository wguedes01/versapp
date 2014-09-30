package com.versapp.database;

import android.provider.BaseColumns;

/**
 * Created by william on 23/09/14.
 */
public class DBContract {

    private DBContract(){};

    public static abstract class FriendsTable implements BaseColumns {
        public static final String TABLE_NAME = "friends";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_STATUS = "status";
    }

    public static abstract class MessagesTable implements BaseColumns {

        public static final String TABLE_NAME = "messages";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_MESSAGE_ID = "message_id";
        public static final String COLUMN_NAME_IMAGE_URL = "image_url";
        public static final String COLUMN_NAME_MESSAGE_BODY = "message_body";
        public static final String COLUMN_NAME_THREAD = "thread";
        public static final String COLUMN_NAME_IS_MINE = "is_mine";

    }



}
