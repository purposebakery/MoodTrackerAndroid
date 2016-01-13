package com.techlung.moodtracker.tracking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.techlung.moodtracker.R;
import com.techlung.moodtracker.greendao.extended.DaoFactory;
import com.techlung.moodtracker.greendao.extended.ExtendedMoodRatingDao;
import com.techlung.moodtracker.greendao.extended.ExtendedMoodScopeDao;
import com.techlung.moodtracker.greendao.generated.MoodRating;
import com.techlung.moodtracker.greendao.generated.MoodScope;
import com.techlung.moodtracker.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TrackingFragment extends Fragment {

    ExtendedMoodRatingDao extendedMoodRatingDao;
    ExtendedMoodScopeDao extendedMoodScopeDao;

    List<MoodRating> moodRatings;
    List<MoodScope> moodScopes;

    int moodScopesCount;

    private LineChart chartAverage;

    TextView infos;
    boolean openTracking;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        extendedMoodRatingDao = DaoFactory.getInstance(getActivity()).getExtendedMoodRatingDao();
        extendedMoodScopeDao = DaoFactory.getInstance(getActivity()).getExtendedMoodScopeDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.tracking_fragment, container, false);

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTrackingSummary();
            }
        });

        infos = (TextView) root.findViewById(R.id.infos);

        if (openTracking) {
            getTrackingSummary();
        }



        return root;
    }

    private void updateUi() {
        moodRatings = extendedMoodRatingDao.getAllMoodRatings();
        moodScopes = extendedMoodScopeDao.getAllMoodScopes();

        moodScopesCount = moodScopes.size();
    }

    public void getTrackingSummary() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View root = inflater.inflate(R.layout.tracking_input_summary, null);
        ListView trackingList = (ListView) root.findViewById(R.id.list);

        final ExtendedMoodRatingDao extendedMoodRatingDao = DaoFactory.getInstance(getActivity()).getExtendedMoodRatingDao();
        final List<MoodScope> moodScopes = DaoFactory.getInstance(getActivity()).getExtendedMoodScopeDao().getAllMoodScopes();
        Date currentDay = Utils.getCurrentDay();
        final List<MoodRating> moodRatings = new ArrayList<MoodRating>();
        moodRatings.addAll(extendedMoodRatingDao.getAllMoodRatingByDay(currentDay));
        if (moodRatings.isEmpty()) {
            moodRatings.addAll(initCurrentDayMoodRatings(getActivity(), moodScopes, currentDay));
        }
        final TrackingSummaryAdapter adapter = new TrackingSummaryAdapter(getActivity(), R.layout.tracking_input, moodRatings);
        trackingList.setAdapter(adapter);
        trackingList.requestFocus();

        final TextView date = (TextView) root.findViewById(R.id.date);
        date.setText(Utils.formatDate(moodRatings.get(0).getDay()));
        date.setVisibility(View.VISIBLE);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Display Selected date in textbox
                        c.set(year, monthOfYear, dayOfMonth);

                        date.setText(Utils.formatDate(c.getTime()));
                        Date selectedDay = Utils.parseDate(date.getText().toString());
                        moodRatings.clear();
                        moodRatings.addAll(extendedMoodRatingDao.getAllMoodRatingByDay(selectedDay));
                        if (moodRatings.isEmpty()) {
                            moodRatings.addAll(initCurrentDayMoodRatings(getActivity(), moodScopes, selectedDay));
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, year, month, day);

                mDatePicker.show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    private List<MoodRating> initCurrentDayMoodRatings(Context context, List<MoodScope> moodScopes, Date currentDay) {
        ExtendedMoodRatingDao extendedMoodRatingDao = DaoFactory.getInstance(context).getExtendedMoodRatingDao();

        // Init previous days
        Date lastTrackedDay = extendedMoodRatingDao.getLastTrackedDayBefore(currentDay);
        if (lastTrackedDay != null) {
            List<MoodRating> lastRatings = extendedMoodRatingDao.getAllMoodRatingByDay(lastTrackedDay);

            Date nextDay = lastTrackedDay;
            nextDay = Utils.getAddOneDay(nextDay);
            int counter = 0;

            while (nextDay.getTime() != currentDay.getTime()) {
                counter++;
                for (MoodRating rating : lastRatings) {
                    MoodRating newRating = new MoodRating();
                    newRating.setScope(rating.getScope());
                    newRating.setDay(nextDay);
                    newRating.setRating(rating.getRating());
                    newRating.setTimestamp(Utils.getCurrentTimestamp());

                    extendedMoodRatingDao.insertOrReplace(newRating);
                }

                nextDay = Utils.getAddOneDay(nextDay);
            }

            if (counter < 0) {
                Toast.makeText(getActivity(), String.format(getString(R.string.tracking_initialized_missed_days), counter), Toast.LENGTH_SHORT).show();
            }
        }

        // Init current day
        List<MoodRating> ratings = new ArrayList<MoodRating>();
        for (MoodScope scope : moodScopes) {
            MoodRating rating = new MoodRating();
            rating.setMoodScope(scope);
            rating.setDay(currentDay);
            rating.setRating(Rating.NORMAL);
            rating.setTimestamp(Utils.getCurrentTimestamp());

            ratings.add(rating);

            extendedMoodRatingDao.insertOrReplace(rating);
        }

        return ratings;
    }

    public boolean isOpenTracking() {
        return openTracking;
    }

    public void setOpenTracking(boolean openTracking) {
        this.openTracking = openTracking;
    }
}
