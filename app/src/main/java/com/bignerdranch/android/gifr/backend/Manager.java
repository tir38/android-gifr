package com.bignerdranch.android.gifr.backend;

import com.bignerdranch.android.gifr.model.GifMessage;

import java.util.List;

public interface Manager {

    void loadMessages();

    List<GifMessage> getMessages();

    void loadUsername(GifMessage message);
}
