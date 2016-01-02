package com.techlung.moodtracker.modescope;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.techlung.moodtracker.R;
import com.techlung.moodtracker.greendao.extended.DaoFactory;
import com.techlung.moodtracker.greendao.generated.MoodScope;

public class MoodScopeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mood_scope_activity);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMoodScopeItem();
            }
        });
    }

    private void updateMoodScopeList() {

    }

    private void addMoodScopeItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inputLayout = LayoutInflater.from(this).inflate(R.layout.alert_input, null);
        final EditText input = (EditText) inputLayout.findViewById(R.id.alert_input);
        input.setHint(R.string.moodscope_add_input_hint);

        builder.setView(inputLayout);
        builder.setTitle(R.string.moodscope_add_title);
        builder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();

                if (validateMoodScope(text, input))  {
                    MoodScope scope = new MoodScope();
                    int sequence = (int) DaoFactory.getInstance(MoodScopeActivity.this).getExtendedMoodScopeDao().getCount();
                    scope.setName(text);
                    scope.setSequence(sequence + 1);
                    dialog.dismiss();
                    updateMoodScopeList();
                }
            }
        });
        builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private boolean validateMoodScope(String text, EditText input) {
        if (text == null || text.trim().equals("")) {
            input.setError(getString(R.string.moodscope_add_input_error_empty));
            return false;
        } else if (DaoFactory.getInstance(this).getExtendedMoodScopeDao().getMoodScopeByName(text) != null) {
            input.setError(getString(R.string.moodscope_add_input_error_exists));
            return false;
        } else {
            input.setError(null);
        }

        return true;
    }
}
