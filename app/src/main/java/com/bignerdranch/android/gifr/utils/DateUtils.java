package com.bignerdranch.android.gifr.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtils {

    private DateUtils() {
    }

    public static boolean isToday(DateTime dateTime) {
        DateTime now = new DateTime();
        LocalDate today = now.toLocalDate();
        LocalDate tomorrow = today.plusDays(1);

        DateTime startOfToday = today.toDateTimeAtStartOfDay(now.getZone());
        DateTime startOfTomorrow = tomorrow.toDateTimeAtStartOfDay(now.getZone());

        return (dateTime.isAfter(startOfToday) && dateTime.isBefore(startOfTomorrow));
    }

    public static boolean isYesterday(DateTime dateTime) {
        DateTime now = new DateTime();
        LocalDate today = now.toLocalDate();
        LocalDate yesterday = today.minusDays(1);

        DateTime startOfToday = today.toDateTimeAtStartOfDay(now.getZone());
        DateTime startOfYesterday = yesterday.toDateTimeAtStartOfDay(now.getZone());

        return (dateTime.isAfter(startOfYesterday) && dateTime.isBefore(startOfToday));
    }

    public static String beautify(DateTime dateTime) {
        // todo convert to local time

        DateTimeFormatter fmt = DateTimeFormat.forPattern("MMMM dd, HH:mm");
        return fmt.print(dateTime);

    }
}
