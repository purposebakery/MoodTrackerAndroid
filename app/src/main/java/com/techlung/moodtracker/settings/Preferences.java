package com.techlung.moodtracker.settings;

import com.pixplicity.easyprefs.library.Prefs;

public class Preferences {

    private static final String FIRST_START = "FIRST_START";

    public static final String USER_NAME = "USER_NAME";

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
}
