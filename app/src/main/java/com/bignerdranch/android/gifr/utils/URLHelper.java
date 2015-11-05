package com.bignerdranch.android.gifr.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class URLHelper {

    public static List<URL> findUrlsInString(String input) {

        List<URL> urls = new ArrayList<>();

        // separate input by spaces ( URLs don't have spaces )
        String[] parts = input.split("\\s+");

        // Attempt to convert each item into an URL.
        for (String item : parts)
            try {
                URL url = new URL(item);
                urls.add(url);
            } catch (MalformedURLException e) {
                // eat it
            }

        return urls;
    }

    public static boolean isUrlAGif(URL url) {
        // does it end it .gif
        return url.toString().endsWith(".gif");
    }

    private URLHelper() {
    } // can not instantiate
}
