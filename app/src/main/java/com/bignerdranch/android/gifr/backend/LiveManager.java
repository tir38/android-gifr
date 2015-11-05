package com.bignerdranch.android.gifr.backend;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bignerdranch.android.gifr.backend.response.HistoryResponse;
import com.bignerdranch.android.gifr.backend.response.MessageResponse;
import com.bignerdranch.android.gifr.backend.response.UserResponse;
import com.bignerdranch.android.gifr.event.MessagesUpdatedEvent;
import com.bignerdranch.android.gifr.model.GifMessage;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LiveManager implements Manager {

    private static final String TAG = LiveManager.class.getSimpleName();
    private static final String GIF_9002_CHANNEL_ID = "C0489TGGR";
    private static final String SLACK_AUTH_TOKEN = "xoxp-2151342987-3842312016-5019750910-2e484f";

    private SlackService mSlackService;
    private MessageStore mStore;
    private List<GifMessage> mToDelete; // items to be deleted from disk
    private List<GifMessage> mToDownload; // items to be downloaded to disk
    private CacheUpdateHandlerThread mCacheUpdateHandlerThread;
    private Context mContext;

    public LiveManager(Context context,
                       SlackService slackService,
                       MessageStore store) {

        mContext = context.getApplicationContext();
        mSlackService = slackService;
        mStore = store;

        mToDelete = new ArrayList<>();
        mToDownload = new ArrayList<>();
    }

    @Override
    public void loadMessages() {
        mSlackService.getHistory(SLACK_AUTH_TOKEN, GIF_9002_CHANNEL_ID, new Callback<HistoryResponse>() {
            @Override
            public void success(HistoryResponse historyResponse, Response response) {
                List<MessageResponse> messageResponses = historyResponse.getMessages();
                Log.d(TAG, "success: is ok? " + historyResponse.isOk() + ". message count: " + messageResponses.size());

                List<GifMessage> gifMessages = new ArrayList<>();

                for (MessageResponse messageResponse : messageResponses) {

                    GifMessage gifMessage = MessageResponse.createGifMessageFromResponse(messageResponse);
                    if (gifMessage != null) {
                        gifMessages.add(gifMessage);
                    }
                }

                mStore.addAnyNewMessages(gifMessages);
                setCacheUpdates();
                EventBus.getDefault().post(new MessagesUpdatedEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "failure", error);
            }
        });
    }

    @Override
    public List<GifMessage> getMessages() {
        return mStore.getMessages();
    }

    @Override
    public void loadUsername(final GifMessage message) {

        mSlackService.getUser(SLACK_AUTH_TOKEN, message.getUsername(), new Callback<UserResponse>() {
            @Override
            public void success(UserResponse userResponse, Response response) {
                String displayName = userResponse.getDisplayName();
                message.setDisplayUsername(displayName);
                mStore.updateMessage(message);
                Log.d(TAG, "updated message with name: " + displayName);
                EventBus.getDefault().post(new MessagesUpdatedEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Failure downloading username. ", error);
            }
        });
    }

    // scan through each item in the list and mark things as to-download or to-delete
    private void setCacheUpdates() {
        List<GifMessage> allMessages = mStore.getMessages();

        // TODO sort messages by date, newest first

        mToDelete.clear();
        mToDownload.clear();

        int i = 0;
        for (GifMessage message : allMessages) {
            // TODO fix this. for now lets just cache the first 10 gifs to disk and delete the rest

            if (i < 10) {
                if (message.getLocalFileName() == null) { // if local file is not null then already downloaded
                    mToDownload.add(message);
                    mStore.updateMessage(message);
                }

                i++;

            } else {
                if (message.getLocalFileName() != null) { // if local file is null then nothing to delete
                    mToDelete.add(message);
                }
            }
        }

        executeCacheUpdates();
    }

    // actually schedule cache updates by passing off to background thread
    private void executeCacheUpdates() {

        if (mCacheUpdateHandlerThread == null) {

            // ignore warning, there is a bug with @SuppressLint("HandlerLeak")
            // we can suppress because we handle weak refernce when we pass this off
            Handler updateStoreHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    GifMessage gifMessage = (GifMessage) msg.obj;
                    mStore.updateMessage(gifMessage);
                    EventBus.getDefault().post(new MessagesUpdatedEvent());
                }
            };

            mCacheUpdateHandlerThread = new CacheUpdateHandlerThread(mContext, updateStoreHandler);
            mCacheUpdateHandlerThread.start();
            mCacheUpdateHandlerThread.getLooper(); // block until looper is ready
        }

        deleteFiles();
        downloadFiles();

        // TODO figure out when to shut down background thread
    }

    private void deleteFiles() {
        Log.d(TAG, "scheduling items for deletion: " + mToDelete.size());

        for (GifMessage message : mToDelete) {
            mCacheUpdateHandlerThread.deleteLocalFile(message);
        }
    }

    private void downloadFiles() {
        Log.d(TAG, "scheduling items for download: " + mToDownload.size());
        for (GifMessage message : mToDownload) {
            mCacheUpdateHandlerThread.downloadFile(message);
        }
    }
}
