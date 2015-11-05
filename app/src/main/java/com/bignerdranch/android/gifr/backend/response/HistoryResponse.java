package com.bignerdranch.android.gifr.backend.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * GSON response object from Slack API
 */
public class HistoryResponse {

    @SerializedName("ok")
    protected boolean ok;
    @SerializedName("messages")
    protected List<MessageResponse> messages;

    public boolean isOk() {
        return ok;
    }

    public List<MessageResponse> getMessages() {
        return messages;
    }
}
