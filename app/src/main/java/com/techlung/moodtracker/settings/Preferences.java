package com.techlung.moodtracker.settings;

import android.content.Context;
import android.content.ContextWrapper;

import com.pixplicity.easyprefs.library.Prefs;

public class Preferences {

    private static final String FIRST_START = "FIRST_START";

    public static final String USER_NAME = "USER_NAME";
    public static final String NOTIFICATION_ENABLED = "NOTIFICATION_ENABLED";
    public static final String NOTIFICATION_TIME = "NOTIFICATION_TIME";
    public static final String NOTIFICATION_TIME_HOUR = "NOTIFICATION_TIME_HOUR";
    public static final String NOTIFICATION_TIME_MINUTE = "NOTIFICATION_TIME_MINUTE";
    public static final String TRACKING_HISTORY_LENGTH = "TRACKING_HISTORY_LENGTH";

    private static boolean isInited = false;

    public static boolean isFirstStartup() {
        return Prefs.getBoolean(FIRST_START, true);
    }
    public static void setFirstStart(boolean firstStart) {
        Prefs.putBoolean(FIRST_START, firstStart);
    }

    public static String getUserName() {
        return Prefs.getString(USER_NAME, "");
    }
    public static void setUserName(String userName) {
        Prefs.putString(USER_NAME, userName);
    }


    public static boolean isNotificationEnabled() {
        return Prefs.getBoolean(NOTIFICATION_ENABLED, true);
    }
    public static void setNotificationEnabled(boolean notificationEnabled) {
        Prefs.putBoolean(NOTIFICATION_ENABLED, notificationEnabled);
    }

    public static String getNotificationTime() {
        return Prefs.getString(NOTIFICATION_TIME, "18:00");
    }
    public static void setNotificationTime(String notificationTime) {
        Prefs.putString(NOTIFICATION_TIME, notificationTime);
    }

    public static int getNotificationTimeHour() {
        return Prefs.getInt(NOTIFICATION_TIME_HOUR, 18);
    }
    public static void setNotificationTimeHour(int notificationTimeHour) {
        Prefs.putInt(NOTIFICATION_TIME_HOUR, notificationTimeHour);
    }

    public static int getNotificationTimeMinute() {
        return Prefs.getInt(NOTIFICATION_TIME_MINUTE, 0);
    }
    public static void setNotificationTimeMinute(int notificationTimeMinute) {
        Prefs.putInt(NOTIFICATION_TIME_MINUTE, notificationTimeMinute);
    }

    public static String getTrackingHistoryLength() {
        return Prefs.getString(TRACKING_HISTORY_LENGTH, "10");
    }
    public static void setTrackingHistoryLength(String length) {
        Prefs.putString(TRACKING_HISTORY_LENGTH, length);
    }

    public static void initPreferences(Context context) {
        if (isInited) {
            return;
        }

        isInited = true;

        new Prefs.Builder()
                .setContext(context)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(context.getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

}
