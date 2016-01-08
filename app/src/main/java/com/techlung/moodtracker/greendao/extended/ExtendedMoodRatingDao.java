package com.techlung.moodtracker.greendao.extended;

import com.techlung.moodtracker.greendao.generated.MoodRating;
import com.techlung.moodtracker.greendao.generated.MoodRatingDao;

import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class ExtendedMoodRatingDao {
    public MoodRatingDao dao;

    public ExtendedMoodRatingDao(MoodRatingDao moodRatingDao) {
        this.dao = moodRatingDao;
    }

    public List<MoodRating> getAllMoodRatings() {
        QueryBuilder<MoodRating> queryBuilder = dao.queryBuilder();
        queryBuilder.orderDesc(MoodRatingDao.Properties.Day);
        return queryBuilder.list();
    }


    public List<MoodRating> getAllMoodRatingByDay(Date day) {
        QueryBuilder<MoodRating> queryBuilder = dao.queryBuilder();
        queryBuilder.where(MoodRatingDao.Properties.Day.eq(day));
        return queryBuilder.list();
    }

    public MoodRating getMoodRatingById(Long id) {
        QueryBuilder<MoodRating> queryBuilder = dao.queryBuilder();
        queryBuilder.where(MoodRatingDao.Properties.Id.eq(id));
        return queryBuilder.unique();
    }


    public long getCount() {
        QueryBuilder<MoodRating> queryBuilder = dao.queryBuilder();
        return queryBuilder.count();
    }

    public void insertOrReplace(MoodRating moodRating) {
        dao.insertOrReplace(moodRating);
    }

    public void update(MoodRating moodRating) {
        dao.update(moodRating);
    }

    public void delete(MoodRating moodRating) {
        dao.delete(moodRating);
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}
