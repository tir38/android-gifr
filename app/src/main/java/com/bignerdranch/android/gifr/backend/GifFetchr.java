package com.bignerdranch.android.gifr.backend;


import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GifFetchr {

    private static final String TAG = GifFetchr.class.getSimpleName();

    public static void download(URL url, File file) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {

            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        url.toString());
            }

            FileOutputStream out = new FileOutputStream(file);

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            Log.d(TAG, "successfully downloaded: " + url.toString() + " into " + file.getAbsolutePath());
        } finally {
            connection.disconnect();
        }
    }
}
