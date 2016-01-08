package com.techlung.moodtracker.tracking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.techlung.moodtracker.R;
import com.techlung.moodtracker.greendao.extended.DaoFactory;
import com.techlung.moodtracker.greendao.generated.MoodRating;

import java.util.List;

public class TrackingSummaryAdapter extends ArrayAdapter<MoodRating> {

    public static final float FADED = 0.1f;
    
    public TrackingSummaryAdapter(Context context, int textViewResourceId, List<MoodRating> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.tracking_input, parent, false);
        }

        final MoodRating item = getItem(position);
        TextView textView = (TextView) convertView.findViewById(R.id.scope);

        textView.setText(item.getMoodScope().getName());

        final ImageView veryLow = (ImageView) convertView.findViewById(R.id.mood_very_low_img);
        final ImageView low = (ImageView) convertView.findViewById(R.id.mood_low_img);
        final ImageView normal = (ImageView) convertView.findViewById(R.id.mood_normal_img);
        final ImageView high = (ImageView) convertView.findViewById(R.id.mood_high_img);
        final ImageView veryHigh = (ImageView) convertView.findViewById(R.id.mood_very_high_img);

        veryLow.setAlpha(FADED);
        low.setAlpha(FADED);
        normal.setAlpha(FADED);
        high.setAlpha(FADED);
        veryHigh.setAlpha(FADED);
        /*
        veryLow.setScaleX(FADED);
        veryLow.setScaleY(FADED);
        low.setScaleX(FADED);
        low.setScaleY(FADED);
        normal.setScaleX(FADED);
        normal.setScaleY(FADED);
        high.setScaleX(FADED);
        high.setScaleY(FADED);
        veryHigh.setScaleX(FADED);
        veryHigh.setScaleY(FADED);
*/

        /*
        if (item.getRating() == Rating.VERY_LOW) {
            veryLow.startAnimation(createFadeinAnimation(veryLow));
        }
        if (item.getRating() == Rating.LOW) {
            low.startAnimation(createFadeinAnimation(low));
        }
        if (item.getRating() == Rating.NORMAL) {
            normal.startAnimation(createFadeinAnimation(normal));
        }
        if (item.getRating() == Rating.HIGH) {
            high.startAnimation(createFadeinAnimation(high));
        }
        if (item.getRating() == Rating.VERY_HIGH) {
            veryHigh.startAnimation(createFadeinAnimation(veryHigh));
        }*/

        if (item.getRating() == Rating.VERY_LOW) {
            veryLow.setAlpha(1.0f);
        }
        if (item.getRating() == Rating.LOW) {
            low.setAlpha(1.0f);
        }
        if (item.getRating() == Rating.NORMAL) {
            normal.setAlpha(1.0f);
        }
        if (item.getRating() == Rating.HIGH) {
            high.setAlpha(1.0f);
        }
        if (item.getRating() == Rating.VERY_HIGH) {
            veryHigh.setAlpha(1.0f);
        }

        final View moodVeryLow = convertView.findViewById(R.id.mood_very_low);
        final View moodLow = convertView.findViewById(R.id.mood_low);
        final View moodNormal = convertView.findViewById(R.id.mood_normal);
        final View moodHigh = convertView.findViewById(R.id.mood_high);
        final View moodVeryHigh = convertView.findViewById(R.id.mood_very_high);

        moodVeryLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeoutSelected(veryLow, low, normal, high, veryHigh, item.getRating());
                item.setRating(Rating.VERY_LOW);
                DaoFactory.getInstance(getContext()).getExtendedMoodRatingDao().update(item);
                //veryLow.startAnimation(createFadeinAnimation(veryLow));
                veryLow.setAlpha(1.0f);
                YoYo.with(Techniques.Tada).duration(500).playOn(veryLow);
            }
        });

        moodLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeoutSelected(veryLow, low, normal, high, veryHigh, item.getRating());
                item.setRating(Rating.LOW);
                DaoFactory.getInstance(getContext()).getExtendedMoodRatingDao().update(item);
                //low.startAnimation(createFadeinAnimation(low));
                low.setAlpha(1.0f);
                YoYo.with(Techniques.Tada).duration(500).playOn(low);
            }
        });

        moodNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeoutSelected(veryLow, low, normal, high, veryHigh, item.getRating());
                item.setRating(Rating.NORMAL);
                DaoFactory.getInstance(getContext()).getExtendedMoodRatingDao().update(item);
                //normal.startAnimation(createFadeinAnimation(normal));
                normal.setAlpha(1.0f);
                YoYo.with(Techniques.Tada).duration(500).playOn(normal);
            }
        });

        moodHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeoutSelected(veryLow, low, normal, high, veryHigh, item.getRating());
                item.setRating(Rating.HIGH);
                DaoFactory.getInstance(getContext()).getExtendedMoodRatingDao().update(item);
                //high.startAnimation(createFadeinAnimation(high));
                high.setAlpha(1.0f);
                YoYo.with(Techniques.Tada).duration(500).playOn(high);
            }
        });

        moodVeryHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeoutSelected(veryLow, low, normal, high, veryHigh, item.getRating());
                item.setRating(Rating.VERY_HIGH);
                DaoFactory.getInstance(getContext()).getExtendedMoodRatingDao().update(item);
                //veryHigh.startAnimation(createFadeinAnimation(veryHigh));
                veryHigh.setAlpha(1.0f);
                YoYo.with(Techniques.Tada).duration(500).playOn(veryHigh);
            }
        });

        return convertView;
    }

    private void fadeoutSelected(View moodVeryLow, View moodLow, View moodNormal, View moodHigh, View moodVeryHigh, Integer rating) {
        switch (rating) {
            case Rating.VERY_LOW:
                moodVeryLow.setAlpha(FADED);
                break;
            case Rating.LOW:
                moodLow.setAlpha(FADED);
                break;
            case Rating.NORMAL:
                moodNormal.setAlpha(FADED);
                break;
            case Rating.HIGH:
                moodHigh.setAlpha(FADED);
                break;
            case Rating.VERY_HIGH:
                moodVeryHigh.setAlpha(FADED);
                break;
        }
    }
/*
    private void fadeoutSelected(View moodVeryLow, View moodLow, View moodNormal, View moodHigh, View moodVeryHigh, Integer rating) {
        switch (rating) {
            case Rating.VERY_LOW:
                moodVeryLow.startAnimation(createFadeoutAnimation(moodVeryLow));
                break;
            case Rating.LOW:
                moodLow.startAnimation(createFadeoutAnimation(moodLow));
                break;
            case Rating.NORMAL:
                moodNormal.startAnimation(createFadeoutAnimation(moodNormal));
                break;
            case Rating.HIGH:
                moodHigh.startAnimation(createFadeoutAnimation(moodHigh));
                break;
            case Rating.VERY_HIGH:
                moodVeryHigh.startAnimation(createFadeoutAnimation(moodVeryHigh));
                break;
        }
    }*/

    private Animation createFadeoutAnimation(final View view) {
        view.setAlpha(1.0f);
        Animation animation = new AlphaAnimation(1.0f, FADED);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setAlpha(FADED);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animation;
    }

    private Animation createFadeinAnimation(final View view) {
        view.setAlpha(FADED);
        Animation animation = new AlphaAnimation(FADED, 1.0f);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setAlpha(1.0f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animation;
    }
/*
    private void scaleDownSelected(View moodVeryLow, View moodLow, View moodNormal, View moodHigh, View moodVeryHigh, Integer rating) {
        switch (rating) {
            case Rating.VERY_LOW:
                moodVeryLow.startAnimation(createScaleDownAnimation(moodVeryLow));
                break;
            case Rating.LOW:
                moodLow.startAnimation(createScaleDownAnimation(moodLow));
                break;
            case Rating.NORMAL:
                moodNormal.startAnimation(createScaleDownAnimation(moodNormal));
                break;
            case Rating.HIGH:
                moodHigh.startAnimation(createScaleDownAnimation(moodHigh));
                break;
            case Rating.VERY_HIGH:
                moodVeryHigh.startAnimation(createScaleDownAnimation(moodVeryHigh));
                break;
        }
    }*/

    /*
    private ScaleAnimation createScaleUpAnimation(final View view) {
        ScaleAnimation scale = new ScaleAnimation((float)0.3, (float)1.0, (float)0.3, (float)1.0);
        scale.setFillAfter(true);
        scale.setDuration(500);
        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return scale;
    }

    private ScaleAnimation createScaleDownAnimation(final View view) {
        ScaleAnimation scale = new ScaleAnimation((float)1, (float)0.3, (float)1, (float)0.3);
        scale.setFillAfter(true);
        scale.setDuration(500);
        scale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setScaleX(FADED);
                view.setScaleY(FADED);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return scale;
    }*/

}
