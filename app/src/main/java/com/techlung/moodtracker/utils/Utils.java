package com.techlung.moodtracker.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by metz037 on 08.01.16.
 */
public class Utils {

    public static Date getCurrentDay() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date getAddOneDay(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        calendar.add(Calendar.HOUR, 24);

        return calendar.getTime();
    }

    public static long getCurrentTimestamp() {
        return (new Date()).getTime();
    }

    public static String formatDate(Date date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        return format.format(date);} catch (Exception e) {
            e.printStackTrace();
            return "N/A";
        }

    }

    public static String formatDateShort(Date date) {
        try {SimpleDateFormat format = new SimpleDateFormat("dd.MM");
        return format.format(date);} catch (Exception e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    public static Date parseDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return format.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }
    }
}
