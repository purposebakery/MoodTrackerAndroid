package com.techlung.moodtracker.files;

import android.content.Context;

import java.io.File;

public class FileHandler {
    private static final boolean IS_DB_SAVED_EXTERN = true;


    private static File getDbFolder(Context context) {
        if (IS_DB_SAVED_EXTERN) {
            return context.getExternalFilesDir(null);
        }
        else {
            return context.getFilesDir();
        }
    }

    public static File getDBFile(Context context) {
        File file = new File(getDbFolder(context), "moodtracker.db");
        return file;
    }
}
