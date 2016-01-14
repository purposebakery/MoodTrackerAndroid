package com.techlung.moodtracker.greendao.extended;

import com.techlung.moodtracker.greendao.generated.MoodRating;
import com.techlung.moodtracker.greendao.generated.MoodRatingDao;

import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class ExtendedMoodRatingDao {
    private static final long MOOD_RATING_AVERAGE_SCOPE_ID = 0;

    public MoodRatingDao dao;

    public ExtendedMoodRatingDao(MoodRatingDao moodRatingDao) {
        this.dao = moodRatingDao;
    }

    public List<MoodRating> getAllMoodRatings() {
        QueryBuilder<MoodRating> queryBuilder = dao.queryBuilder();
        queryBuilder.orderDesc(MoodRatingDao.Properties.Day);
        return queryBuilder.list();
    }

    public Date getLastTrackedDayBefore(Date day) {
        QueryBuilder<MoodRating> queryBuilder = dao.queryBuilder();
        queryBuilder.where(MoodRatingDao.Properties.Day.lt(day));
        queryBuilder.orderDesc(MoodRatingDao.Properties.Day);
        List<MoodRating> ratings = queryBuilder.list();

        if (ratings == null || ratings.isEmpty()) {
            return null;
        } else {
            Date lastTrackedDay = ratings.get(0).getDay();
            return lastTrackedDay;
        }
    }

    public List<MoodRating> getAllMoodRatingByDay(Date day) {
        QueryBuilder<MoodRating> queryBuilder = dao.queryBuilder();
        queryBuilder.where(MoodRatingDao.Properties.Day.eq(day));
        return queryBuilder.list();
    }

    public List<MoodRating> getMoodRatingRange(Date start, Date end) {
        QueryBuilder<MoodRating> queryBuilder = dao.queryBuilder();
        queryBuilder.where(MoodRatingDao.Properties.Day.ge(start));
        queryBuilder.where(MoodRatingDao.Properties.Day.le(end));
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
