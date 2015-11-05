package com.bignerdranch.android.gifr.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.android.gifr.database.MessageDbSchema.MessageTable;

public class MessageSQLHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "messageDatabase.db";
    private static final int VERSION = 1;

    public MessageSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCommand = "create table " + MessageTable.NAME
                + "("
                + MessageTable.Cols.ID + " text primary key, "
                + MessageTable.Cols.MILLISECONDS_SINCE_EPCOCH + " text, "
                + MessageTable.Cols.URL + " text, "
                + MessageTable.Cols.USERNAME + " text, "
                + MessageTable.Cols.LOCAL_FILE_NAME + " text, "
                + MessageTable.Cols.USER_DISPLAY_NAME + " text"
                + ")";

        db.execSQL(sqlCommand);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
