package com.techlung.moodtracker.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;

import com.techlung.moodtracker.R;

/**
 * Created by metz037 on 18.01.16.
 */
public class ShareUtil {
    public static void showShareAppAlert(Activity a) {
        String message =
                a.getString(R.string.share_app_text) + "\n"
                + a.getString(R.string.share_app_url);
        sendShareMessage(a, message);
    }

    private static void sendShareMessage(Activity a, String message) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);

        String chooserMessage = a.getResources().getString(R.string.share_app_title);
        a.startActivity(Intent.createChooser(share, chooserMessage));
    }
}

