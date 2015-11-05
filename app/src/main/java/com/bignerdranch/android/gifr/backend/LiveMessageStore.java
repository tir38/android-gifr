package com.bignerdranch.android.gifr.backend;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.gifr.database.MessageCursorWrapper;
import com.bignerdranch.android.gifr.database.MessageDbSchema.MessageTable;
import com.bignerdranch.android.gifr.database.MessageSQLHelper;
import com.bignerdranch.android.gifr.model.GifMessage;

import java.util.ArrayList;
import java.util.List;

public class LiveMessageStore implements MessageStore {

    private static final String TAG = LiveMessageStore.class.getSimpleName();

    private SQLiteDatabase mDatabase;

    private static ContentValues getContentValues(GifMessage message) {
        // store long as String
        String milliSecondsAsString = Long.toString(message.getMillsecondsSinceEpoch());

        ContentValues values = new ContentValues();
        values.put(MessageTable.Cols.ID, message.getId());
        values.put(MessageTable.Cols.URL, message.getURL().toString());
        values.put(MessageTable.Cols.USERNAME, message.getUsername());
        values.put(MessageTable.Cols.LOCAL_FILE_NAME, message.getLocalFileName());
        values.put(MessageTable.Cols.USER_DISPLAY_NAME, message.getDisplayUsername());
        values.put(MessageTable.Cols.MILLISECONDS_SINCE_EPCOCH, milliSecondsAsString);
        // TODO properly store DateTime

        return values;
    }

    public LiveMessageStore(Context context) {
        mDatabase = new MessageSQLHelper(context.getApplicationContext()).getWritableDatabase();
    }

    @Override
    public void addAnyNewMessages(List<GifMessage> messages) {

        for (GifMessage message : messages) {
            insert(message);
        }
    }

    @Override
    public List<GifMessage> getMessages() {

        List<GifMessage> messages = new ArrayList<>();

        MessageCursorWrapper cursor = queryMessages(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                GifMessage message = cursor.getMessage();
                if (message != null) {
                    messages.add(message);
                }
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return messages;
    }

    @Override
    public void updateMessage(GifMessage message) {
        String id = message.getId();
        ContentValues contentValues = getContentValues(message);

        mDatabase.update(MessageTable.NAME,
                contentValues,
                MessageTable.Cols.ID + " = ?",
                new String[]{id});
    }

    private void insert(GifMessage message) {
        ContentValues contentValues = getContentValues(message);
        try {
            mDatabase.insertOrThrow(MessageTable.NAME, null, contentValues);
        }   catch (SQLException e) {
            // eat it. i purposely don't want to allow repeat entries but i might try.
        }
    }

    private MessageCursorWrapper queryMessages(String whereClause, String[] whereArgs) {
        @SuppressLint("Recycle") // suppress because we manually close cursor when we get message
        Cursor cursor = mDatabase.query(
                MessageTable.NAME,
                null, // columns - if null, return all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new MessageCursorWrapper(cursor);
    }
}
