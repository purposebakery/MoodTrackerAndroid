package com.techlung.moodtracker.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.techlung.moodtracker.settings.Preferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class NotificationManager {

    public static void setNextNotification(Context context, boolean showToast) {
        if (!Preferences.isNotificationEnabled()) {
            return;
        }

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long nextNotificationTime = getNextNotificationTime();

        alarmManager.set(AlarmManager.RTC, nextNotificationTime, pendingIntent);
        if (showToast) {
            toastNextNotificationTime(nextNotificationTime, context);
        }
    }

    private static void toastNextNotificationTime(long time, Context context) {
        DateFormat format = new SimpleDateFormat("EEEE dd.MM.yyyy", Locale.ENGLISH);
        Date date = new Date();
        date.setTime(time);

        Toast.makeText(context, "Next Notification: " + format.format(date), Toast.LENGTH_SHORT);
    }

    private static long getNextNotificationTime() {
        Calendar calendar = new GregorianCalendar();
        long now = (new Date()).getTime();
        long tomorrow = 1000 * 60 * 60 * 24;
        calendar.setTime(new Date(tomorrow));
        calendar.set(Calendar.HOUR_OF_DAY, Preferences.getNotificationTimeHour());
        calendar.set(Calendar.MINUTE, Preferences.getNotificationTimeMinute());
        return calendar.getTimeInMillis();
        //return (new Date()).getTime() + 1000*60*2;
    }





}