package com.techlung.moodtracker.greendao.extended;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.techlung.moodtracker.files.FileHandler;
import com.techlung.moodtracker.greendao.generated.DaoMaster;
import com.techlung.moodtracker.greendao.generated.LogEntryDao;
import com.techlung.moodtracker.greendao.generated.MoodRatingDao;
import com.techlung.moodtracker.greendao.generated.MoodScopeDao;

import java.io.File;

/**
 * Created by metz037 on 02.01.16.
 */
public class DaoFactory {

    protected DaoMaster daoMaster;
    protected SQLiteDatabase db;
    private Context context;


    private ExtendedMoodScopeDao extendedMoodScopeDao;
    private ExtendedMoodRatingDao extendedMoodRatingDao;
    private ExtendedLogEntryDao extendedLogEntryDao;

    private static DaoFactory instance;

    // Bewegungsdaten
    public static DaoFactory getInstance(Context context) {
        if (instance == null) {
            instance = new DaoFactory();
            instance.context = context;
            instance.reinitialiseDb();
        }
        return instance;
    }


    public void reinitialiseDb() {
        if (db == null) {
            makeSureDbIsInitialised();
        }
        else {
            closeDb();
            makeSureDbIsInitialised();
        }
    }

    public void makeSureDbIsInitialised() {
        if (db == null) {

            String dbFilePath = getDbFilePath();
            boolean dbFileExisted = (new File(dbFilePath)).exists();

            /*
            // TODO hook migration
            if (dbFileExisted) {
                migrateIfNecessary();
            }*/

            db = context.openOrCreateDatabase(dbFilePath, SQLiteDatabase.CREATE_IF_NECESSARY, null);
            daoMaster = new DaoMaster(db);

            if (!dbFileExisted) {
                recreateDb();
            }
        }
    }

    /*
    // TODO hook migration
    private void migrateIfNecessary() {
        IsoXmlOpenHelper openHelper = new IsoXmlOpenHelper(context, getDbFilePath(), null);
        SQLiteDatabase databaseTemp = openHelper.getWritableDatabase(); // trigger upgrade
        databaseTemp.close();
    }*/

    private String getDbFilePath() {
        return FileHandler.getDBFile(context).getAbsolutePath();
    }

    public void closeDb() {
        if (db != null) {
            db.close();
            db = null;
            daoMaster = null;

            extendedMoodScopeDao = null;
            extendedMoodRatingDao = null;
            extendedLogEntryDao = null;
        }
    }

    public void clearDb() {
        makeSureDbIsInitialised();

        db.beginTransaction();

        try {
            getExtendedMoodScopeDao().deleteAll();
            getExtendedMoodRatingDao().deleteAll();
            getExtendedLogEntryDao().deleteAll();

            db.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }

        Log.d(DaoFactory.class.getName(), "Cleared all Tables of Database");
    }

    public void recreateDb() {
        DaoMaster.dropAllTables(db, true);
        DaoMaster.createAllTables(db, true);
        db.setVersion(DaoMaster.SCHEMA_VERSION);
    }

    public ExtendedMoodScopeDao getExtendedMoodScopeDao() {
        if (extendedMoodScopeDao == null) {
            MoodScopeDao moodScopeDao = daoMaster.newSession().getMoodScopeDao();
            extendedMoodScopeDao = new ExtendedMoodScopeDao(moodScopeDao);
        }
        return extendedMoodScopeDao;
    }

    public ExtendedMoodRatingDao getExtendedMoodRatingDao() {
        if (extendedMoodRatingDao == null) {
            MoodRatingDao moodRatingDao = daoMaster.newSession().getMoodRatingDao();
            extendedMoodRatingDao = new ExtendedMoodRatingDao(moodRatingDao);
        }
        return extendedMoodRatingDao;
    }

    public ExtendedLogEntryDao getExtendedLogEntryDao() {
        if (extendedLogEntryDao == null) {
            LogEntryDao logEntryDao = daoMaster.newSession().getLogEntryDao();
            extendedLogEntryDao = new ExtendedLogEntryDao(logEntryDao);
        }
        return extendedLogEntryDao;
    }

}
