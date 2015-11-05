package com.bignerdranch.android.gifr.backend;

import com.bignerdranch.android.gifr.model.GifMessage;

import java.util.List;

public interface MessageStore {

    /**
     * Add any new messages that are not already in the store.
     * This does not update existing messages.
     * @param messages
     */
    void addAnyNewMessages(List<GifMessage> messages);

    List<GifMessage> getMessages();

    void updateMessage(GifMessage message);
}
