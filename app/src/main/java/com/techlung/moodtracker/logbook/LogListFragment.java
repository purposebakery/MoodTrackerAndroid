package com.techlung.moodtracker.logbook;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.techlung.moodtracker.R;
import com.techlung.moodtracker.greendao.extended.DaoFactory;
import com.techlung.moodtracker.greendao.extended.ExtendedLogEntryDao;
import com.techlung.moodtracker.greendao.generated.LogEntry;

import java.util.List;

public class LogListFragment extends Fragment {

    UltimateRecyclerView ultimateRecyclerView;
    ExtendedLogEntryDao extendedLogEntryDao;

    LinearLayoutManager linearLayoutManager;
    LogListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        extendedLogEntryDao = DaoFactory.getInstance(getActivity()).getExtendedLogEntryDao();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.log_list_fragment, container, false);

        ultimateRecyclerView = (UltimateRecyclerView) root.findViewById(R.id.ultimate_recycler_view);
        ultimateRecyclerView.setHasFixedSize(false);

        root.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMoodScopeItem();
            }
        });

        updateLogEntryList();

        return root;
    }


    private void updateLogEntryList() {

        List<LogEntry> data = extendedLogEntryDao.getAllLogEntries();

        adapter = new LogListAdapter(ultimateRecyclerView, data);

        linearLayoutManager = new LinearLayoutManager(getActivity());

        ultimateRecyclerView.setLayoutManager(linearLayoutManager);
        ultimateRecyclerView.setAdapter(adapter);
    }
}
