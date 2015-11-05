package com.bignerdranch.android.gifr.database;

public class MessageDbSchema {

    public static final class MessageTable {
        public static final String NAME ="messages";

        public static final class Cols {
            public static final String ID = "id";
            public static final String USERNAME = "username";
            public static final String MILLISECONDS_SINCE_EPCOCH = "millisecondssinceepoch";
            public static final String URL = "url";
            public static final String LOCAL_FILE_NAME = "filename";
            public static final String USER_DISPLAY_NAME = "displayname";
        }
    }
}
