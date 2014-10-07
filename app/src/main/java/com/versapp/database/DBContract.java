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

    public static abstract class ChatsTable implements BaseColumns {

        public static final String TABLE_NAME = "chats";
        public static final String COLUMN_NAME_UUID = "chat_uuid";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_IS_OWNER = "is_owner";
        public static final String COLUMN_NAME_OWNER_ID = "owner_id";
        public static final String COLUMN_NAME_DEGREE = "degree";
        public static final String COLUMN_NAME_CID = "cid";
        public static final String COLUMN_NAME_LAST_OPENED_TIMESTAMP = "last_opened_timestamp";

    }

    public static abstract class ParticipantsTable implements BaseColumns {

        public static final String TABLE_NAME = "participants";
        public static final String COLUMN_NAME_ID = "participant_id";
        public static final String COLUMN_NAME_UUID = "chat_uuid";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_NAME = "name";

    }



}
