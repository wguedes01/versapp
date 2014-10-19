package com.versapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by william on 19/10/14.
 */
public class ReportedConfessionsDAO {

    private Context context;
    private DBHelper helper;

    public ReportedConfessionsDAO(Context context) {
        this.context = context;
        this.helper = DBHelper.getInstance(context);
    }

    public void insert(long confessionId){

        ContentValues values = new ContentValues();

        values.put(DBContract.ReportedConfessionsTable.COLUMN_NAME_ID, confessionId);

        SQLiteDatabase dbWritable = this.helper.getWritableDatabase();
        dbWritable.insert(DBContract.ReportedConfessionsTable.TABLE_NAME, null, values);
    }

    public ArrayList<Long> getAll(){

        ArrayList<Long> blockedIds = new ArrayList<Long>();

        String[] projection = { DBContract.ReportedConfessionsTable.COLUMN_NAME_ID };

        SQLiteDatabase dbReadable = this.helper.getReadableDatabase();

        Cursor cursor = dbReadable.query(DBContract.ReportedConfessionsTable.TABLE_NAME, projection, null, null, null, null, null);

        if (cursor == null) {
            return blockedIds;
        }

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            int idIndex = cursor.getColumnIndex(DBContract.ReportedConfessionsTable.COLUMN_NAME_ID);

            long id = cursor.getLong(idIndex);

            blockedIds.add(id);

            cursor.moveToNext();
        }

        cursor.close();

        return blockedIds;
    }

    public void delete(long confessionId){
        SQLiteDatabase dbWritable = this.helper.getWritableDatabase();
        dbWritable.delete(DBContract.ReportedConfessionsTable.TABLE_NAME, DBContract.ReportedConfessionsTable.COLUMN_NAME_ID + " = ? ", new String[] { String.valueOf(confessionId) });
    }
}
