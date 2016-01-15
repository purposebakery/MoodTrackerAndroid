package com.techlung.moodtracker.tracking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.techlung.moodtracker.R;
import com.techlung.moodtracker.greendao.extended.DaoFactory;
import com.techlung.moodtracker.greendao.extended.ExtendedMoodRatingDao;
import com.techlung.moodtracker.greendao.extended.ExtendedMoodScopeDao;
import com.techlung.moodtracker.greendao.generated.MoodRating;
import com.techlung.moodtracker.greendao.generated.MoodScope;
import com.techlung.moodtracker.settings.Preferences;
import com.techlung.moodtracker.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackingFragment extends Fragment {

    public static final float LINE_WIDTH = 4f;
    public static final float CIRCLE_SIZE = 7f;
    public static final int ANIMATION_DURATION = 2500;
    public static final int FILL_ALPHA = 80;
    public static final float VALUE_TEXT_SIZE = 10f;

    ExtendedMoodRatingDao extendedMoodRatingDao;
    ExtendedMoodScopeDao extendedMoodScopeDao;

   // List<MoodRating> moodRatings;
    List<MoodScope> moodScopes;
    HashMap<Long, MoodScope> moodScopeMap;

    private LineChart chartAverage;
    private LineChart chartScopes;

    boolean openTracking;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        extendedMoodRatingDao = DaoFactory.getInstance(getActivity()).getExtendedMoodRatingDao();
        extendedMoodScopeDao = DaoFactory.getInstance(getActivity()).getExtendedMoodScopeDao();

        //moodRatings = new ArrayList<MoodRating>();
        moodScopes = new ArrayList<MoodScope>();
        moodScopeMap = new HashMap<Long, MoodScope>();

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

        chartAverage = (LineChart) root.findViewById(R.id.chartAverage);
        chartScopes = (LineChart) root.findViewById(R.id.chartScopes);

        if (openTracking) {
            getTrackingSummary();
        }
        initChartAverage(chartAverage);
        initChartScopes(chartScopes);

        updateUi();

        return root;
    }

    private void initChartAverage(LineChart chart) {
        initChartGeneric(chart);

        LimitLine ll1 = new LimitLine(Rating.HIGH, "All Good");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setLineColor(getActivity().getResources().getColor(R.color.green));

        LimitLine ll2 = new LimitLine(Rating.LOW, "Danger");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setLineColor(getActivity().getResources().getColor(R.color.red));

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);

        leftAxis.enableGridDashedLine(10f, 10f, 0f);

        Legend l = chart.getLegend();
        l.setEnabled(false);

    }

    private void initChartScopes(LineChart chart) {
        initChartGeneric(chart);

    }

    private void initChartGeneric(LineChart chart) {
        chart.setDescription("");
        chart.setSelected(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMaxValue(6);
        leftAxis.setAxisMinValue(0);
        leftAxis.setStartAtZero(false);
        //leftAxis.setYOffset(20f);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        chart.getAxisRight().setEnabled(false);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.CIRCLE);


//        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

    }

    public void updateUi() {
        int historyLength = Integer.parseInt(Preferences.getTrackingHistoryLength());
        Date today = Utils.getCurrentDay();
        long timediff = 1000l * 60l * 60l * 24l * (long)historyLength;
        Date historyStart = new Date(today.getTime() - timediff);


        Log.d("TAG1", ""+ historyLength);
        Log.d("TAG2", ""+ today);
        Log.d("TAG3", ""+ timediff);
        Log.d("TAG4", ""+ historyStart);

        moodScopes.clear();
        moodScopes.addAll(extendedMoodScopeDao.getAllMoodScopes());
        moodScopeMap.clear();
        for (MoodScope scope : moodScopes) {
            moodScopeMap.put(scope.getId(), scope);
        }

        List<String> days = new ArrayList<String>();
        // initialize Zero data
        Float[] dataAverage = new Float[historyLength+1];
        for (int i = 0; i < dataAverage.length; ++i) {
            dataAverage[i] = 0f;
        }

        HashMap<Long, Integer[]> dataMap = new HashMap<Long, Integer[]>();
        for (MoodScope scope : moodScopes) {
            Integer[] zeroRatings = new Integer[historyLength+1];
            for (int i = 0; i < zeroRatings.length; ++i) {
                zeroRatings[i] = Rating.UNDEFINED;
            }
            dataMap.put(scope.getId(), zeroRatings);
        }

        // process tracked ratings
        Date tempDay = new Date(historyStart.getTime());
        List<MoodRating> tempRatings = new ArrayList<MoodRating>();
        int counter = 0;
        while (tempDay.getTime() <= today.getTime()) {
            List<MoodRating> newRatings = extendedMoodRatingDao.getAllMoodRatingByDay(tempDay);
            days.add(Utils.formatDateShort(tempDay));

            // if we don't have anything for the current day, use the previous days data!
            if (newRatings == null || newRatings.isEmpty() && !tempRatings.isEmpty()) {
                newRatings.clear();
                newRatings.addAll(tempRatings);
            }
            tempRatings.clear();
            tempRatings.addAll(newRatings);

            // add todays ratings into stats
            if (!newRatings.isEmpty()) {
                float average = 0;
                int averageCounter = 0;
                for (MoodRating rating : newRatings) {
                    // only add rating if the scope still exists
                    if (dataMap.containsKey(rating.getScope())) {
                        dataMap.get(rating.getScope())[counter] = rating.getRating();

                        if (rating.getRating() != Rating.UNDEFINED) {
                            average += rating.getRating();
                            averageCounter++;
                        }
                    }
                }

                if (averageCounter > 0) {
                    average = average / (float) averageCounter;
                } else {
                    average = Rating.NORMAL;
                }

                dataAverage[counter] = average;
            }

            counter++;
            tempDay = Utils.getAddOneDay(tempDay);
        }

        setData(days, dataMap, dataAverage);
    }

    private void setData(List<String> days, HashMap<Long, Integer[]> dataMap, Float[] average) {

        // Average
        ArrayList<Entry> averageYVals = new ArrayList<Entry>();
        for (int i = 0; i < average.length; ++i) {
            averageYVals.add(new Entry(average[i], i));
        }
        LineDataSet averageSet = new LineDataSet(averageYVals, "Average");
        averageSet.setColor(Color.BLACK);
        averageSet.setCircleColor(Color.BLACK);
        averageSet.setLineWidth(LINE_WIDTH);
        averageSet.setCircleSize(CIRCLE_SIZE);
        averageSet.setDrawCircleHole(false);
        averageSet.setValueTextSize(VALUE_TEXT_SIZE);
        averageSet.setFillAlpha(FILL_ALPHA);
        averageSet.setFillColor(Color.BLACK);
        averageSet.setDrawValues(false);

        ArrayList<LineDataSet> averageDataSets = new ArrayList<LineDataSet>();
        averageDataSets.add(averageSet); // add the datasets

        Log.d("DAYS", "" + days.size());
        LineData averageData = new LineData(days, averageDataSets);
        chartAverage.setData(averageData);
        chartAverage.animateX(ANIMATION_DURATION, Easing.EasingOption.EaseInOutQuart);

        // Scopes
        int[] rainbow = getActivity().getResources().getIntArray(R.array.rainbow);
        int counter = 0;
        ArrayList<LineDataSet> scopeDataSets = new ArrayList<LineDataSet>();
        for (Map.Entry<Long, Integer[]> entry : dataMap.entrySet()) {
            MoodScope scope = moodScopeMap.get(entry.getKey());
            ArrayList<Entry> scopeYVals = new ArrayList<Entry>();
            for (int i = 0; i < entry.getValue().length; ++i) {
                scopeYVals.add(new Entry(entry.getValue()[i], i));
            }
            LineDataSet scopeSet = new LineDataSet(scopeYVals, scope.getName());

            int color = rainbow[(int)(((float) rainbow.length / (float) moodScopes.size()) * counter)];
            scopeSet.setColor(color);
            scopeSet.setCircleColor(color);
            scopeSet.setLineWidth(LINE_WIDTH);
            scopeSet.setCircleSize(CIRCLE_SIZE);
            scopeSet.setDrawCircleHole(false);
            scopeSet.setValueTextSize(VALUE_TEXT_SIZE);
            scopeSet.setFillAlpha(FILL_ALPHA);
            scopeSet.setFillColor(color);
            scopeSet.setDrawValues(false);

            scopeDataSets.add(scopeSet);

            counter++;
        }

        LineData scopeData = new LineData(days, scopeDataSets);
        chartScopes.setData(scopeData);
        chartScopes.animateX(ANIMATION_DURATION, Easing.EasingOption.EaseInOutQuart);

/*
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {

            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 3;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleSize(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.BLACK);
        */
//        set1.setDrawFilled(true);
        // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
        // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        /*
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
*/
        // set data
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
                updateUi();
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
