package com.techlung.moodtracker;

import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pixplicity.easyprefs.library.Prefs;
import com.techlung.moodtracker.settings.Preferences;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preferences.initPreferences(this);
    }
}
