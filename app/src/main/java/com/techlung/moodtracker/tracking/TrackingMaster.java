package com.techlung.moodtracker.tracking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.techlung.moodtracker.R;
import com.techlung.moodtracker.greendao.extended.DaoFactory;
import com.techlung.moodtracker.greendao.extended.ExtendedMoodRatingDao;
import com.techlung.moodtracker.greendao.generated.MoodRating;
import com.techlung.moodtracker.greendao.generated.MoodScope;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by metz037 on 02.01.16.
 */
public class TrackingMaster {

    public static void getTracking(Context context) {

    }

    public static void getTrackingSummary(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View root = inflater.inflate(R.layout.tracking_input_summary, null);
        ListView trackingList = (ListView) root.findViewById(R.id.list);

        ExtendedMoodRatingDao extendedMoodRatingDao = DaoFactory.getInstance(context).getExtendedMoodRatingDao();
        List<MoodScope> moodScopes = DaoFactory.getInstance(context).getExtendedMoodScopeDao().getAllMoodScopes();
        Date currentDay = getCurrentDay();
        List<MoodRating> moodRatings = extendedMoodRatingDao.getAllMoodRatingByDay(currentDay);
        if (moodRatings == null || moodRatings.isEmpty()) {
            moodRatings = initCurrentDayMoodRatings(context, moodScopes, currentDay);
        }
        TrackingSummaryAdapter adapter = new TrackingSummaryAdapter(context, R.layout.tracking_input, moodRatings);
        trackingList.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.tracking_summary);
        builder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setView(root);

        builder.show();
    }

    private static List<MoodRating> initCurrentDayMoodRatings(Context context, List<MoodScope> moodScopes, Date currentDay) {
        ExtendedMoodRatingDao extendedMoodRatingDao = DaoFactory.getInstance(context).getExtendedMoodRatingDao();

        List<MoodRating> ratings = new ArrayList<MoodRating>();
        for (MoodScope scope : moodScopes) {
            MoodRating rating = new MoodRating();
            rating.setMoodScope(scope);
            rating.setDay(currentDay);
            rating.setRating(Rating.NORMAL);
            rating.setTimestamp((new Date()).getTime());

            ratings.add(rating);

            extendedMoodRatingDao.insertOrReplace(rating);
        }

        return ratings;
    }

    private static Date getCurrentDay() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }


}
