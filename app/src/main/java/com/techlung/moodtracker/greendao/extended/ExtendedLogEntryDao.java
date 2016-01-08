package com.techlung.moodtracker.greendao.extended;

import com.techlung.moodtracker.greendao.generated.LogEntry;
import com.techlung.moodtracker.greendao.generated.LogEntryDao;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class ExtendedLogEntryDao {
    public LogEntryDao dao;

    public ExtendedLogEntryDao(LogEntryDao LogEntryDao) {
        this.dao = LogEntryDao;
    }

    public LogEntry getLogEntryById(Long id) {
        QueryBuilder<LogEntry> queryBuilder = dao.queryBuilder();
        queryBuilder.where(LogEntryDao.Properties.Id.eq(id));
        return queryBuilder.unique();
    }
    public List<LogEntry> getAllLogEntries() {
        QueryBuilder<LogEntry> queryBuilder = dao.queryBuilder();
        queryBuilder.orderDesc(LogEntryDao.Properties.Day);
        queryBuilder.orderDesc(LogEntryDao.Properties.Timestamp);
        return queryBuilder.list();
    }

    public long getCount() {
        QueryBuilder<LogEntry> queryBuilder = dao.queryBuilder();
        return queryBuilder.count();
    }

    public void insertOrReplace(LogEntry LogEntry) {
        dao.insertOrReplace(LogEntry);
    }

    public void update(LogEntry LogEntry) {
        dao.update(LogEntry);
    }

    public void delete(LogEntry LogEntry) {
        dao.delete(LogEntry);
    }

    public void deleteAll() {
        dao.deleteAll();
    }

    public void deleteById(Long id) {
        LogEntry scope = getLogEntryById(id);
        if (scope != null) {
            dao.delete(scope);
        }
    }
}
