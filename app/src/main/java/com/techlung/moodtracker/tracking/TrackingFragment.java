package com.techlung.moodtracker.tracking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.techlung.moodtracker.R;
import com.techlung.moodtracker.enums.TrackingCalculation;
import com.techlung.moodtracker.enums.TrackingMethod;
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

    public static final float LINE_WIDTH = 2f;
    public static final float CIRCLE_SIZE = 5f;
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
    private BarChart chartAverageScopes;

    private TextView titleAverage;

    boolean openTrackingFromExternal;

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
                getTracking(false);
            }
        });

        chartAverage = (LineChart) root.findViewById(R.id.chartAverage);
        chartScopes = (LineChart) root.findViewById(R.id.chartScopes);
        chartAverageScopes = (BarChart) root.findViewById(R.id.chartAverageScopes);

        titleAverage = (TextView) root.findViewById(R.id.titleAverage);

        if (openTrackingFromExternal) {
            getTracking(true);
        }

        updateUi();

        return root;
    }

    private void initChartAverageScopes(BarChart chart) {
        chart.setDrawBarShadow(false);

        chart.setDescription("");

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setScaleEnabled(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);

        YAxis leftAxis = chart.getAxisLeft();
        if (Preferences.getTrackingCalculation() == TrackingCalculation.AVERAGE) {
            leftAxis.setAxisMaxValue(6);
        } else {
            leftAxis.resetAxisMaxValue();
        }
        leftAxis.setAxisMinValue(0);
        leftAxis.setStartAtZero(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend l = chart.getLegend();
        l.setEnabled(false);
    }

    private void initChartAverage(LineChart chart) {

        chart.setDescription("");
        chart.setSelected(false);

        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setScaleEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        if (Preferences.getTrackingCalculation() == TrackingCalculation.AVERAGE) {
            leftAxis.setAxisMaxValue(6);
        } else {
            leftAxis.resetAxisMaxValue();
        }
        leftAxis.setAxisMinValue(0);
        leftAxis.setStartAtZero(false);
        leftAxis.setDrawLimitLinesBehindData(true);

        chart.getAxisRight().setEnabled(false);

        Legend l = chart.getLegend();
        l.setEnabled(false);

        if (Preferences.getTrackingCalculation() == TrackingCalculation.AVERAGE) {
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

            leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
            leftAxis.addLimitLine(ll1);
            leftAxis.addLimitLine(ll2);
        }

        leftAxis.enableGridDashedLine(10f, 10f, 0f);

    }

    private void initChartScopes(LineChart chart) {

        chart.setDescription("");
        chart.setSelected(false);

        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setScaleEnabled(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMaxValue(6);
        leftAxis.setAxisMinValue(0);
        leftAxis.setStartAtZero(false);
        leftAxis.setDrawLimitLinesBehindData(true);

        chart.getAxisRight().setEnabled(false);

        Legend l = chart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.CIRCLE);

    }

    public void updateUi() {

        initChartAverage(chartAverage);
        initChartScopes(chartScopes);
        initChartAverageScopes(chartAverageScopes);

        if (Preferences.getTrackingCalculation() == TrackingCalculation.AVERAGE) {
            titleAverage.setText(R.string.tracking_title_average);
        } else {
            titleAverage.setText(R.string.tracking_title_sum);
        }

        int historyLength = Integer.parseInt(Preferences.getTrackingHistoryLength());
        Date today = Utils.getCurrentDay();
        long timediff = 1000l * 60l * 60l * 24l * (long)historyLength;
        Date historyStart = new Date(today.getTime() - timediff);

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
                    if (Preferences.getTrackingCalculation() == TrackingCalculation.AVERAGE) {
                        average = average / (float) averageCounter;
                    }
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
        averageSet.setDrawCircleHole(true);
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
        int[] colors = new int[dataMap.size()];
        for (Map.Entry<Long, Integer[]> entry : dataMap.entrySet()) {
            MoodScope scope = moodScopeMap.get(entry.getKey());
            ArrayList<Entry> scopeYVals = new ArrayList<Entry>();
            for (int i = 0; i < entry.getValue().length; ++i) {
                scopeYVals.add(new Entry(entry.getValue()[i], i));
            }
            LineDataSet scopeSet = new LineDataSet(scopeYVals, scope.getName());

            int color = rainbow[(int)(((float) rainbow.length / (float) moodScopes.size()) * counter)];
            colors[counter] = color;
            scopeSet.setColor(color);
            scopeSet.setCircleColor(color);
            scopeSet.setLineWidth(LINE_WIDTH);
            scopeSet.setCircleSize(CIRCLE_SIZE);
            scopeSet.setDrawCircleHole(true);
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

        // Scope Averages
        ArrayList<String> scopeAverageXVals = new ArrayList<String>();
        ArrayList<BarEntry> scopeAverageYVals = new ArrayList<BarEntry>();
        counter = 0;
        for (Map.Entry<Long, Integer[]> entry : dataMap.entrySet()) {
            MoodScope scope = moodScopeMap.get(entry.getKey());
            scopeAverageXVals.add(scope.getName());

            int averageCount = 0;
            float averageValue = 0;

            for (Integer integer : entry.getValue()) {
                if (integer != Rating.UNDEFINED) {
                    averageValue += integer;
                    averageCount++;
                }
            }
            if (Preferences.getTrackingCalculation() == TrackingCalculation.AVERAGE) {
                averageValue /= (float) averageCount;
            }
            scopeAverageYVals.add(new BarEntry(averageValue, counter));

            counter++;

        }

        BarDataSet set1 = new BarDataSet(scopeAverageYVals, "Scopes");
        set1.setBarSpacePercent(35f);
        set1.setColors(colors);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(scopeAverageXVals, dataSets);
        data.setValueTextSize(VALUE_TEXT_SIZE);

        chartAverageScopes.setData(data);
    }

    public void getTracking(boolean withExitButton) {
        if (Preferences.getTrackingMethod() == TrackingMethod.ALL_AT_ONCE) {
            getTrackingSummary(withExitButton);
        } else {
            getTrackingWizard(withExitButton);
        }
    }

    private void getTrackingWizard(boolean withExitButton) {
        final ExtendedMoodRatingDao extendedMoodRatingDao = DaoFactory.getInstance(getActivity()).getExtendedMoodRatingDao();
        final List<MoodScope> moodScopes = DaoFactory.getInstance(getActivity()).getExtendedMoodScopeDao().getAllMoodScopes();
        final List<MoodRating> moodRatings = getCurrentDayMoodRatings(moodScopes);

        getTrackingWizardElement(withExitButton, moodRatings);
    }

    private void getTrackingWizardElement(final boolean withExitButton, final List<MoodRating> moodRatings) {
        if (moodRatings.isEmpty()) {
            getTrackingSummary(withExitButton);
        } else {
            MoodRating firstRating = moodRatings.remove(0);

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View root = inflater.inflate(R.layout.tracking_input_summary, null);
            ListView trackingList = (ListView) root.findViewById(R.id.list);
            final TextView date = (TextView) root.findViewById(R.id.date);
            date.setVisibility(View.GONE);

            List<MoodRating> singleRatingList = new ArrayList<MoodRating>();
            singleRatingList.add(firstRating);


            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.tracking_wizard_title);
            builder.setView(root);

            int positiveId = moodRatings.isEmpty() ? R.string.alert_done : R.string.alert_next;
            builder.setPositiveButton(positiveId, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    getTrackingWizardElement(withExitButton, moodRatings);
                }
            });

            builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            final AlertDialog dialog = builder.create();

            final TrackingSummaryAdapter adapter = new TrackingSummaryAdapter(getActivity(), R.layout.tracking_input, singleRatingList, new TrackingSummaryAdapter.OnOneRatingMadeListener() {
                @Override
                public void ratingMade() {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            getTrackingWizardElement(withExitButton, moodRatings);
                        }
                    }, 200);
                }
            });
            trackingList.setAdapter(adapter);
            trackingList.requestFocus();

            dialog.show();
        }
    }

    private void getTrackingSummary(boolean withExitButton) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View root = inflater.inflate(R.layout.tracking_input_summary, null);
        ListView trackingList = (ListView) root.findViewById(R.id.list);

        final ExtendedMoodRatingDao extendedMoodRatingDao = DaoFactory.getInstance(getActivity()).getExtendedMoodRatingDao();
        final List<MoodScope> moodScopes = DaoFactory.getInstance(getActivity()).getExtendedMoodScopeDao().getAllMoodScopes();
        final List<MoodRating> moodRatings = getCurrentDayMoodRatings(moodScopes);
        final TrackingSummaryAdapter adapter = new TrackingSummaryAdapter(getActivity(), R.layout.tracking_input, moodRatings, null);
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

        if (withExitButton) {
            builder.setNegativeButton(R.string.alert_close_app, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
        }

        builder.setView(root);

        builder.show();
    }

    private List<MoodRating> getCurrentDayMoodRatings(List<MoodScope> moodScopes) {
        Date currentDay = Utils.getCurrentDay();
        final List<MoodRating> moodRatings = new ArrayList<MoodRating>();
        moodRatings.addAll(extendedMoodRatingDao.getAllMoodRatingByDay(currentDay));
        if (moodRatings.isEmpty()) {
            moodRatings.addAll(initCurrentDayMoodRatings(getActivity(), moodScopes, currentDay));
        }
        return moodRatings;
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

    public boolean isOpenTrackingFromExternal() {
        return openTrackingFromExternal;
    }

    public void setOpenTrackingFromExternal(boolean openTrackingFromExternal) {
        this.openTrackingFromExternal = openTrackingFromExternal;
    }
}
