package com.bignerdranch.android.gifr.model;

import org.joda.time.DateTime;

import java.net.URL;

public class GifMessage {

    private String mUsername; // name on server
    private String mDisplayUsername; // human readable name
    private long mMillsecondsSinceEpoch;
    private URL mURL;
    private String messageText;
    private final String mId;
    private String mLocalFileName;

    public GifMessage(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getDisplayUsername() {
        return mDisplayUsername;
    }

    public void setDisplayUsername(String displayUsername) {
        mDisplayUsername = displayUsername;
    }

    public long getMillsecondsSinceEpoch() {
        return mMillsecondsSinceEpoch;
    }

    public void setMillsecondsSinceEpoch(long millsecondsSinceEpoch) {
        mMillsecondsSinceEpoch = millsecondsSinceEpoch;
    }

    public DateTime getDateTime() {
        return new DateTime(mMillsecondsSinceEpoch);
    }

//    public void setDateTime(DateTime dateTime) {
//        mDateTime = dateTime;
//    }

    public URL getURL() {
        return mURL;
    }

    public void setURL(URL URL) {
        mURL = URL;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getLocalFileName() {
        return mLocalFileName;
    }

    public void setLocalFileName(String localFileName) {
        mLocalFileName = localFileName;
    }
}
