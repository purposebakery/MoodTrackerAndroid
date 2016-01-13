package com.techlung.moodtracker.greendao.extended;

import com.techlung.moodtracker.greendao.generated.MoodScope;
import com.techlung.moodtracker.greendao.generated.MoodScopeDao;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class ExtendedMoodScopeDao {
    public MoodScopeDao dao;

    public ExtendedMoodScopeDao(MoodScopeDao moodScopeDao) {
        this.dao = moodScopeDao;
    }

    public List<MoodScope> getAllMoodScopes() {
        QueryBuilder<MoodScope> queryBuilder = dao.queryBuilder();
        queryBuilder.orderAsc(MoodScopeDao.Properties.Sequence);
        return queryBuilder.list();
    }

    public MoodScope getMoodScopeById(Long id) {
        QueryBuilder<MoodScope> queryBuilder = dao.queryBuilder();
        queryBuilder.where(MoodScopeDao.Properties.Id.eq(id));
        return queryBuilder.unique();
    }

    public MoodScope getMoodScopeByName(String name) {
        QueryBuilder<MoodScope> queryBuilder = dao.queryBuilder();
        queryBuilder.where(MoodScopeDao.Properties.Name.eq(name));
        return queryBuilder.unique();
    }

    public long getCount() {
        QueryBuilder<MoodScope> queryBuilder = dao.queryBuilder();
        return queryBuilder.count();
    }

    public void insertOrReplace(MoodScope moodScope) {
        dao.insertOrReplace(moodScope);
    }

    public void update(MoodScope moodScope) {
        dao.update(moodScope);
    }

    public void delete(MoodScope moodScope) {
        dao.delete(moodScope);
    }

    public void deleteById(Long id) {
        MoodScope scope = getMoodScopeById(id);
        if (scope != null) {
            dao.delete(scope);
        }
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}
