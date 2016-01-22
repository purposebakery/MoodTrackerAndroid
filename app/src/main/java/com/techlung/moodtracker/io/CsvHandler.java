package com.techlung.moodtracker.io;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.techlung.moodtracker.R;
import com.techlung.moodtracker.greendao.extended.DaoFactory;
import com.techlung.moodtracker.greendao.extended.ExtendedLogEntryDao;
import com.techlung.moodtracker.greendao.extended.ExtendedMoodRatingDao;
import com.techlung.moodtracker.greendao.extended.ExtendedMoodScopeDao;
import com.techlung.moodtracker.greendao.generated.LogEntry;
import com.techlung.moodtracker.greendao.generated.MoodRating;
import com.techlung.moodtracker.greendao.generated.MoodScope;
import com.techlung.moodtracker.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CsvHandler {
    private enum DBFormat {MOOD_RATINGS, LOG_ENTRIES}

    public enum IOAction {IMPORT, EXPORT}

    private static final String OUT_FILE_NAME = "mood_tracker_export";

    ProgressDialog progressDialog;

    private void showProgressDialog(Activity activity) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void io(final Activity activity, final IOAction action, final File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        switch (action) {
            case EXPORT:
                builder.setTitle(R.string.io_export);
                break;
            case IMPORT:
                builder.setTitle(R.string.io_import);
                break;
            default:
                throw new IllegalArgumentException("Unsupported IOAction " + action.name());

        }
        builder.setItems(R.array.io_db_format, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (action) {
                    case EXPORT:
                        if (which == 0) {
                            exportAndSendDb(activity, DBFormat.MOOD_RATINGS);
                        } else if (which == 1) {
                            exportAndSendDb(activity, DBFormat.LOG_ENTRIES);
                        }
                        break;
                    case IMPORT:
                        if (which == 0) {
                            importDb(activity, DBFormat.MOOD_RATINGS, file);
                        } else if (which == 1) {
                            importDb(activity, DBFormat.LOG_ENTRIES, file);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported IOAction " + action.name());

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

    private void exportAndSendDb(Activity activity, DBFormat format) {
        showProgressDialog(activity);
        File exportFile = exportDbToCsv(activity, format);
        dismissProgressDialog();

        if (exportFile != null && exportFile.exists()) {
            Intent intentShareFile = new Intent(Intent.ACTION_SEND);

            intentShareFile.setType("text/plain");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + exportFile.getAbsolutePath()));

            activity.startActivity(Intent.createChooser(intentShareFile, activity.getString(R.string.io_export)));

        } else {
            exportFail(activity);
        }

    }

    private File exportDbToCsv(Activity activity, DBFormat format) {
        File trgDir = activity.getExternalFilesDir(null);

        if (trgDir == null) {
            return null;
        }

        if (!trgDir.exists()) {
            if (!trgDir.mkdirs()) {
                return null;
            }
        }

        SimpleDateFormat s = new SimpleDateFormat("yyyy.MM.dd-hh.mm.ss", Locale.US);
        String time = s.format(new Date());
        String fileName = (time + "_" + OUT_FILE_NAME + "_" + format.name() + ".csv").toLowerCase();

        File outFile = new File(trgDir.getAbsolutePath() + "/" + fileName);
        if (!outFile.exists()) {
            try {
                if (!outFile.createNewFile()) {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        PrintWriter out;

        try {
            out = new PrintWriter(outFile.getAbsolutePath());

            switch (format) {
                case LOG_ENTRIES:
                    writeLogEntriesDbToFile(activity, out);
                    break;
                case MOOD_RATINGS:
                    writeMoodRatingsDbToFile(activity, out);
                    break;
                default:
                    throw new IllegalArgumentException(format.name() + " not supported.");
            }

            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return outFile;


    }

    private void writeLogEntriesDbToFile(Activity activity, PrintWriter out) {
        List<LogEntry> logEntries = DaoFactory.getInstance(activity).getExtendedLogEntryDao().getAllLogEntries();

        out.println("#DATE;CATEGORY;TEXT");
        for (LogEntry entry : logEntries) {
            out.println(Utils.formatDate(entry.getDay()) + ";" + entry.getCategory() + ";" + entry.getText());
        }

        exportSuccess(activity);
    }

    private void writeMoodRatingsDbToFile(Activity activity, PrintWriter out) {
        List<MoodScope> scopes = DaoFactory.getInstance(activity).getExtendedMoodScopeDao().getAllMoodScopes();
        List<MoodRating> ratings = DaoFactory.getInstance(activity).getExtendedMoodRatingDao().getAllMoodRatings();

        out.print("#DATE;");
        int counter = 0;
        HashMap<Long, Integer> scopeIndexMap = new HashMap<Long, Integer>();
        for (MoodScope scope : scopes) {
            out.print(scope.getName() + ";");
            scopeIndexMap.put(scope.getId(), counter);
            counter++;
        }
        long currentDay = 0;
        int scopeCount = scopes.size();
        int[] scopeRatings = new int[scopeCount];
        for (MoodRating rating : ratings) {
            if (currentDay != rating.getDay().getTime()) {
                out.println();

                currentDay = rating.getDay().getTime();
                out.print(Utils.formatDate(rating.getDay()) + ";");

                for (int i = 0; i < scopeRatings.length; ++i) {
                    out.print(scopeRatings[i]);
                    if (i < scopeRatings.length - 1) {
                        out.print(";");
                    }
                }

                scopeRatings = new int[scopeCount];
            } else if (scopeIndexMap.containsKey(rating.getScope())) {
                scopeRatings[scopeIndexMap.get(rating.getScope())] = rating.getRating();
            }
        }

        exportSuccess(activity);
    }

    private void importDb(final Activity activity, final DBFormat format, final File file) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.alert_warning);
        builder.setMessage(R.string.io_import_delete_warning);
        builder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                importCsvToDb(activity, file, format);
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

    private void importCsvToDb(Activity activity, File file, DBFormat format) {

        try {
            Log.d("CSVHandler", file.getAbsolutePath());
            FileReader r = new FileReader(file);
            BufferedReader b = new BufferedReader(r);

            switch (format) {
                case LOG_ENTRIES:
                    writeLogEntriesFileToDb(activity, b);
                    break;
                case MOOD_RATINGS:
                    writeMoodRatingsFileToDb(activity, b);
                    break;
                default:
                    throw new IllegalArgumentException(format.name() + " not supported.");
            }

            b.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            importFail(activity);
        } catch (IOException e) {
            e.printStackTrace();
            importFail(activity);
        }
    }

    private void writeLogEntriesFileToDb(Activity activity, BufferedReader b) throws IOException {

        ExtendedLogEntryDao extendedLogEntryDao = DaoFactory.getInstance(activity).getExtendedLogEntryDao();
        extendedLogEntryDao.deleteAll();

        String line;

        while ((line = b.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            } else {
                String[] logEntry = line.split(";");
                if (logEntry.length != 3) {
                    importFail(activity);
                    return;
                } else {
                    LogEntry entry = new LogEntry();

                    entry.setDay(Utils.parseDate(logEntry[0]));
                    entry.setCategory(logEntry[1]);
                    entry.setText(logEntry[2]);
                    entry.setTimestamp((new Date()).getTime());

                    extendedLogEntryDao.insertOrReplace(entry);
                }
            }
        }

        importSuccess(activity);
        activity.recreate();
    }

    private void writeMoodRatingsFileToDb(Activity activity, BufferedReader b) throws IOException {
        ExtendedMoodScopeDao extendedMoodScopeDao = DaoFactory.getInstance(activity).getExtendedMoodScopeDao();
        ExtendedMoodRatingDao extendedMoodRatingDao = DaoFactory.getInstance(activity).getExtendedMoodRatingDao();

        extendedMoodScopeDao.deleteAll();
        extendedMoodRatingDao.deleteAll();

        String line;

        while ((line = b.readLine()) != null) {
            if (line.startsWith("#")) {
                String[] scopes = line.split(";");
                for (int i = 1; i < scopes.length; ++i) {
                    MoodScope scope = new MoodScope();
                    scope.setSequence(i);
                    scope.setId((long) i);
                    scope.setName(scopes[i]);

                    extendedMoodScopeDao.insertOrReplace(scope);
                }
            } else {
                String[] ratings = line.split(";");
                Date day = Utils.parseDate(ratings[0]);
                for (int i = 1; i < ratings.length; ++i) {
                    MoodRating rating = new MoodRating();
                    rating.setRating(Integer.parseInt(ratings[i]));
                    rating.setScope((long)i);
                    rating.setDay(day);
                    rating.setTimestamp((new Date()).getTime());

                    extendedMoodRatingDao.insertOrReplace(rating);

                }
            }
        }

        importSuccess(activity);
        activity.recreate();


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
