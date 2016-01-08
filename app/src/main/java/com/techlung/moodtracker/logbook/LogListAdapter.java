package com.techlung.moodtracker.logbook;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.techlung.moodtracker.MainActivity;
import com.techlung.moodtracker.R;
import com.techlung.moodtracker.greendao.extended.DaoFactory;
import com.techlung.moodtracker.greendao.extended.ExtendedMoodScopeDao;
import com.techlung.moodtracker.greendao.generated.LogEntry;
import com.techlung.moodtracker.modescope.MoodScopeActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogListAdapter extends ArrayAdapter<LogEntry> {


    public LogListAdapter(Context context, int resource, List<LogEntry> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.log_list_item, null, false);
        }

        final LogEntry entry = getItem(position);

        TextView text = (TextView) convertView.findViewById(R.id.text);
        text.setText(entry.getText());

        TextView date = (TextView) convertView.findViewById(R.id.date);
        date.setText(getWeekOfDay(entry.getDay()));

        convertView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DaoFactory.getInstance(getContext()).getExtendedLogEntryDao().delete(entry);
                remove(entry);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public String getWeekOfDay(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.getDefault());
        return format.format(date);
    }


}