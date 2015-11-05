package com.bignerdranch.android.gifr.backend;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.bignerdranch.android.gifr.model.GifMessage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class CacheUpdateHandlerThread extends HandlerThread {

    private static final String TAG = CacheUpdateHandlerThread.class.getSimpleName();
    private static final int MESSAGE_DELETE = 0;
    private static final int MESSAGE_DOWNLOAD = 1;

    private Handler mHandler;
    private final Context mApplicationContext;
    private Handler mUpdateUiHandler;

    public CacheUpdateHandlerThread(Context context, Handler updateUiHandler) {
        super(TAG);

        mUpdateUiHandler = updateUiHandler;
        mApplicationContext = context.getApplicationContext();

    }

    @Override
    protected void onLooperPrepared() {

        // ignore warning, there is a bug with @SuppressLint("HandlerLeak")
        // we can suppress this error because all messages are handled by this thread, no risk of a leak.
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                GifMessage gifMessage = (GifMessage) msg.obj;

                switch (msg.what) {
                    case MESSAGE_DELETE:
                        delete(gifMessage);
                        break;

                    case MESSAGE_DOWNLOAD:
                        download(gifMessage);
                        break;

                    default:
                        break;
                }
            }
        };
    }

    public void deleteLocalFile(GifMessage message) {
        String fileName = message.getLocalFileName();
        if (TextUtils.isEmpty(fileName)) {
            return;
        }

        // TODO figure out why this is null even though looper is ready
        if (mHandler == null) {
            return;
        }

        mHandler.obtainMessage(MESSAGE_DELETE, message)
                .sendToTarget();
    }

    public void downloadFile(GifMessage message) {
        URL url = message.getURL();
        if (url == null) {
            return;
        }

        // TODO figure out why this is null even though looper is ready
        if (mHandler == null) {
            return;
        }

        mHandler.obtainMessage(MESSAGE_DOWNLOAD, message)
                .sendToTarget();
    }


    // do the heavy lifting
    private void delete(GifMessage message) {
        String filename = message.getLocalFileName();
        Log.d(TAG, "DELETE : " + filename);

        File path = new File(filename);
        boolean success = path.delete();
        if (success) {
            // update file name and send back to store
            message.setLocalFileName(null);
            if (mUpdateUiHandler != null) {
                mUpdateUiHandler.obtainMessage(0, message)
                        .sendToTarget();
            }

        } else  {
            Log.w(TAG, "failed to delete: " + filename);
        }
    }

    // do the heavy lifting
    private void download(GifMessage message) {
        Log.d(TAG, "DOWNLOAD : " + message.getURL().toString());

        // TODO make up a better file name
        String filename = message.getUsername() + "____" + message.getId() + ".gif";

        File filesDir = mApplicationContext.getFilesDir();
        File file = new File(filesDir, filename);

        try {
            GifFetchr.download(message.getURL(), file);
        } catch (IOException e) {
            Log.e(TAG, "", e);
            return;
        }

        // if successful, update filename and send back to store
        message.setLocalFileName(file.getAbsolutePath());

        if (mUpdateUiHandler != null) {
                mUpdateUiHandler.obtainMessage(0, message)
                        .sendToTarget();
        } else {
            Log.w(TAG, "UI update handler is null. Can't pass back to UI thread.");
        }
    }
}
