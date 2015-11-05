package com.bignerdranch.android.gifr.backend.response;

import android.support.annotation.Nullable;

import com.bignerdranch.android.gifr.model.GifMessage;
import com.bignerdranch.android.gifr.utils.URLHelper;
import com.google.gson.annotations.SerializedName;

import java.net.URL;
import java.util.List;

/**
 * GSON response object from Slack API
 */
public class MessageResponse {

    @SerializedName("user")
    protected String user;

    @SerializedName("text")
    protected String text;

    @SerializedName("ts")
    protected String timeStamp; // Slack-specific time stamp of the form "1432325916.000007"

    public String getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * Create POJO from GSON object
     *
     * @param messageResponse GSON object
     * @return GifMessage a POJO
     */
    @Nullable
    public static GifMessage createGifMessageFromResponse(MessageResponse messageResponse) {
        String text = messageResponse.getText();

        if (text.contains(".gif")) {

            // TODO right now we can't handle gifs uploaded to slack, only urls posted
            if (text.contains("bignerdranch.slack.com/files")) {
                return null;
            }

            // Slack wants to wrap a url in < > so lets remove those before trying to parse url
            text = text.replace("<", "");
            text = text.replace(">", "");

            List<URL> urls = URLHelper.findUrlsInString(text);

            for (URL url : urls) {
                if (URLHelper.isUrlAGif(url)) { // return the first url that points to a gif in the message

                    // build a POJO
                    // use the Slack timestamp as the id, for now
                    GifMessage gifMessage = new GifMessage(messageResponse.getTimeStamp());
                    gifMessage.setURL(url);
                    gifMessage.setUsername(messageResponse.getUser());

                    long millSecondsSinceEpoch = parseSlackTimeStamp(messageResponse.getTimeStamp());
                    gifMessage.setMillsecondsSinceEpoch(millSecondsSinceEpoch);

                    return gifMessage;
                }
            }
        }

        return null;
    }

    private static long parseSlackTimeStamp(String input) {
        // Slack-specific time stamp of the form "1432325916.000007"

        // according to Slack:
        // Our message timestamps (returned in any of the *.history methods) are a standard UNIX timestamp
        // followed by a dot and six numbers to ensure uniqueness within a team.

        // split into two strings at "."
        String[] split = input.split("\\.");
        int length = split.length;
        if (length > 0) {
            String datesString = split[0];
            long secondsSinceEpoch = Long.valueOf(datesString);
            return 1000 * secondsSinceEpoch; // milliseconds since epoch
        }
        return 0;
    }
}
