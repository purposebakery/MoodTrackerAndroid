package com.techlung.moodtracker.io;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

import com.techlung.moodtracker.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by metz037 on 18.01.16.
 */
public class CsvHandler {
    private static final String OUT_FILE_NAME = "mood_tracker_export";

    public void exportDbToCsv(Activity activity) {
        File trgDir = activity.getExternalFilesDir(null);

        if (trgDir == null) {
            exportFail(activity);
            return;
        }

        if (!trgDir.exists()) {
            if (!trgDir.mkdirs()) {
                exportFail(activity);
                return;
            }
        }

        SimpleDateFormat s = new SimpleDateFormat("yy.MM.dd-hh.mm.ss", Locale.US);
        String time = s.format(new Date());

        File outFile = new File(trgDir.getAbsolutePath() + "/" + time + "_"+OUT_FILE_NAME +".csv");
        if (!outFile.exists()) {
            try {
                if (!outFile.createNewFile()) {
                    exportFail(activity);
                    return;
                }
            } catch (IOException e) {
                exportFail(activity);
                e.printStackTrace();
                return;
            }
        }

        PrintWriter out;
        try {
            out = new PrintWriter(outFile.getAbsolutePath());

            writeDbToFile(activity, out);

            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void writeDbToFile(Activity activity, PrintWriter out) {
        // TODO
    }

    private void exportFail(Activity activity) {
        Toast.makeText(activity, R.string.io_export_fail, Toast.LENGTH_LONG).show();
    }

    private void exportSuccess(Activity activity) {
        Toast.makeText(activity, R.string.io_export_success, Toast.LENGTH_LONG).show();
    }

    private void importFail(Activity activity) {
        Toast.makeText(activity, R.string.io_import_fail, Toast.LENGTH_LONG).show();
    }

    private void importSuccess(Activity activity) {
        Toast.makeText(activity, R.string.io_import_success, Toast.LENGTH_LONG).show();
    }
}
