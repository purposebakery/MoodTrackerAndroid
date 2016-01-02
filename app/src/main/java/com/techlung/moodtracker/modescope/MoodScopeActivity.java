package com.techlung.moodtracker.modescope;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.techlung.moodtracker.R;
import com.techlung.moodtracker.greendao.extended.DaoFactory;
import com.techlung.moodtracker.greendao.extended.ExtendedMoodScopeDao;
import com.techlung.moodtracker.greendao.generated.MoodScope;

import java.util.List;

public class MoodScopeActivity extends AppCompatActivity {

    UltimateRecyclerView ultimateRecyclerView;
    MoodScopeDragAdapter adapter = null;
    LinearLayoutManager linearLayoutManager;

    ExtendedMoodScopeDao extendedMoodScopeDao;

    private static MoodScopeActivity instance;

    public static MoodScopeActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        setContentView(R.layout.mood_scope_activity);

        extendedMoodScopeDao = DaoFactory.getInstance(this).getExtendedMoodScopeDao();

        ultimateRecyclerView = (UltimateRecyclerView) findViewById(R.id.ultimate_recycler_view);
        ultimateRecyclerView.setHasFixedSize(false);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMoodScopeItem();
            }
        });

        updateMoodScopeList();
    }

    private void updateMoodScopeList() {

        List<MoodScope> data = extendedMoodScopeDao.getAllMoodScopes();

        adapter = new MoodScopeDragAdapter(ultimateRecyclerView, data);

        linearLayoutManager = new LinearLayoutManager(this);

        ultimateRecyclerView.setLayoutManager(linearLayoutManager);
        ultimateRecyclerView.setAdapter(adapter);
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

                if (validateAddMoodScope(text, input)) {
                    MoodScope scope = new MoodScope();
                    int sequence = (int) extendedMoodScopeDao.getCount();
                    scope.setName(text);
                    scope.setSequence(sequence + 1);

                    extendedMoodScopeDao.insertOrReplace(scope);

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

        builder.show();
    }

    private boolean validateAddMoodScope(String text, EditText input) {
        if (text == null || text.trim().equals("")) {

            input.setError(getString(R.string.moodscope_add_input_error_empty));
            return false;
        } else if (extendedMoodScopeDao.getMoodScopeByName(text) != null) {
            input.setError(getString(R.string.moodscope_add_input_error_exists));
            return false;
        } else {
            input.setError(null);
        }

        return true;
    }
}
