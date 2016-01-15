package com.techlung.moodtracker.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.techlung.moodtracker.settings.Preferences;

public class RebootReceiver extends BroadcastReceiver {

    public RebootReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Preferences.initPreferences(context);

        NotificationManager.setNextNotification(context, false);
    }
}
