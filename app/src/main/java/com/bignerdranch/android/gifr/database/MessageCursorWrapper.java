package com.bignerdranch.android.gifr.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import com.bignerdranch.android.gifr.database.MessageDbSchema.MessageTable;
import com.bignerdranch.android.gifr.model.GifMessage;

import java.net.MalformedURLException;
import java.net.URL;

public class MessageCursorWrapper extends CursorWrapper {
    private static final String TAG = MessageCursorWrapper.class.getSimpleName();

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public MessageCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * map Cursor to POJO
     * @return GifMessage created from database Cursor
     */
    public GifMessage getMessage() {
        String id = getString(getColumnIndex(MessageTable.Cols.ID));
        String username = getString(getColumnIndex(MessageTable.Cols.USERNAME));
        String localFileName = getString(getColumnIndex(MessageTable.Cols.LOCAL_FILE_NAME));
        String displayName = getString(getColumnIndex(MessageTable.Cols.USER_DISPLAY_NAME));
        long millSecondsSinceEpoch = Long.valueOf(getString(getColumnIndex(MessageTable.Cols.MILLISECONDS_SINCE_EPCOCH)));

        String urlAsString = getString(getColumnIndex(MessageTable.Cols.URL));
        URL url;
        try {
            url = new URL(urlAsString);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Failed to create URL from string: " + urlAsString, e);
            return null; // if we can't parse url, there's no point returning this item
        }

        GifMessage message = new GifMessage(id);
        message.setUsername(username);
        message.setLocalFileName(localFileName);
        message.setURL(url);
        message.setDisplayUsername(displayName);
        message.setMillsecondsSinceEpoch(millSecondsSinceEpoch);

        return message;
    }
}
