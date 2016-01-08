package com.techlung.moodtracker.logbook;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.techlung.moodtracker.R;
import com.techlung.moodtracker.enums.LogCategory;
import com.techlung.moodtracker.greendao.extended.DaoFactory;
import com.techlung.moodtracker.greendao.extended.ExtendedLogEntryDao;
import com.techlung.moodtracker.greendao.generated.LogEntry;
import com.techlung.moodtracker.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LogListFragment extends Fragment {

    ExtendedLogEntryDao extendedLogEntryDao;

    ListView list;
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

        list = (ListView) root.findViewById(R.id.list);

        root.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editLogEntry(null);
            }
        });

        updateLogEntryList();

        return root;
    }


    private void updateLogEntryList() {

        final List<LogEntry> data = extendedLogEntryDao.getAllLogEntries();

        adapter = new LogListAdapter(getActivity(), R.layout.alert_input, data);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editLogEntry(adapter.getItem(position));
            }
        });
    }

    public void editLogEntry(@Nullable LogEntry entryOld) {
        final LogEntry entry;

        if (entryOld == null) {
            entry = new LogEntry();
            entry.setCategory(LogCategory.OTHER.name());
            entry.setDay(Utils.getCurrentDay());
        } else {
            entry = entryOld;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View inputLayout = LayoutInflater.from(getActivity()).inflate(R.layout.alert_input, null);

        final EditText input = (EditText) inputLayout.findViewById(R.id.alert_input);
        input.setHint(R.string.log_add_input_hint);
        input.setLines(4);
        input.setGravity(Gravity.START);
        input.setSingleLine(false);
        input.setText(entry.getText());
        input.requestFocus();
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                if (hasFocus) {
                    imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                }
                //imm.toggleSoftInput(0, 0);
            }
        });


        final TextView date = (TextView) inputLayout.findViewById(R.id.alert_date);
        date.setText(Utils.formatDate(entry.getDay()));
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
                    }
                }, year, month, day);

                mDatePicker.show();
            }
        });

        builder.setView(inputLayout);
        builder.setTitle(R.string.log_add_title);
        builder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();

                entry.setText(text);
                entry.setDay(Utils.parseDate(date.getText().toString()));
                entry.setTimestamp(Utils.getCurrentTimestamp());
                extendedLogEntryDao.insertOrReplace(entry);

                dialog.dismiss();
                updateLogEntryList();
            }
        });

        builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Handler hander = new Handler();
        hander.postDelayed(new Runnable() {
            @Override
            public void run() {
                input.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 200);


    }

}
