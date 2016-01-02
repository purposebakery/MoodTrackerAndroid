package com.techlung.moodtracker.logbook;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.techlung.moodtracker.R;

public class LogListFragment extends Fragment {

    CalendarView calendarView;

    public LogListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.log_list_fragment, container, false);

        calendarView = (CalendarView) root.findViewById(R.id.calendarView);

        return root;
    }
}
