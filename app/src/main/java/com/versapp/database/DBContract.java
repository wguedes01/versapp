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

}
