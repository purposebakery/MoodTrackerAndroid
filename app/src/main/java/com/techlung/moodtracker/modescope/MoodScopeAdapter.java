package com.techlung.moodtracker.modescope;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.dragsortadapter.DragSortAdapter;
import com.marshalchen.ultimaterecyclerview.dragsortadapter.NoForegroundShadowBuilder;
import com.techlung.moodtracker.R;
import com.techlung.moodtracker.greendao.extended.DaoFactory;
import com.techlung.moodtracker.greendao.extended.ExtendedMoodScopeDao;
import com.techlung.moodtracker.greendao.generated.MoodScope;

import java.util.List;

public class MoodScopeAdapter extends DragSortAdapter<MoodScopeAdapter.MainViewHolder> {
    private List<MoodScope> data;

    public MoodScopeAdapter(UltimateRecyclerView recyclerView, List<MoodScope> data) {
        super(recyclerView.mRecyclerView);
        this.data = data;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.mood_scope_item, parent, false);
        MainViewHolder holder = new MainViewHolder(this, view);
        view.setOnClickListener(holder);
        view.setOnLongClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {
        MoodScope itemName = data.get(position);
        holder.sequence.setText(itemName.getSequence() + "");
        holder.name.setText(itemName.getName());
        holder.delete.setTag(itemName.getId());

        // NOTE: check for getDraggingId() match to set an "invisible space" while dragging
//        holder.container.setVisibility(getDraggingId() == itemId ? View.INVISIBLE : View.VISIBLE);
//        holder.container.postInvalidate();
    }

    @Override
    public long getItemId(int position) {
        // URLogs.d("hashcode---"+data.get(position).hashCode()+"    "+position);
        return data.get(position).getId();
    }

    protected static int convertToOriginalPosition(int position, int dragInitial, int dragCurrent) {
        if (dragInitial < 0 || dragCurrent < 0) {
            // not dragging
            return position;
        } else {
            if ((dragInitial == dragCurrent) ||
                    ((position < dragInitial) && (position < dragCurrent)) ||
                    (position > dragInitial) && (position > dragCurrent)) {
                return position;
            } else if (dragCurrent < dragInitial) {
                if (position == dragCurrent) {
                    return dragInitial;
                } else {
                    return position - 1;
                }
            } else { // if (dragCurrent > dragInitial)
                if (position == dragCurrent) {
                    return dragInitial;
                } else {
                    return position + 1;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getPositionForId(long id) {
        for (int i = 0; i < data.size(); ++i) {
            if (data.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean move(int fromPosition, int toPosition) {
        data.add(toPosition, data.remove(fromPosition));

        resequenceData();

        updateUi();

        return true;
    }

    public void delete(long id) {
        DaoFactory.getInstance(MoodScopeActivity.getInstance()).getExtendedMoodScopeDao().deleteById(id);

        updateUi();

        resequenceData();

        updateUi();


    }

    private void resequenceData() {
        ExtendedMoodScopeDao extendedMoodScopeDao = DaoFactory.getInstance(MoodScopeActivity.getInstance()).getExtendedMoodScopeDao();

        for (int i = 0; i < data.size(); ++i) {
            MoodScope scope = data.get(i);
            scope.setSequence(i+1);
            extendedMoodScopeDao.update(scope);
        }
    }

    private void updateUi() {
        ExtendedMoodScopeDao extendedMoodScopeDao = DaoFactory.getInstance(MoodScopeActivity.getInstance()).getExtendedMoodScopeDao();

        data.clear();
        data.addAll(extendedMoodScopeDao.getAllMoodScopes());

        this.notifyDataSetChanged();
    }

    class MainViewHolder extends DragSortAdapter.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        TextView sequence;
        TextView name;
        View delete;

        public MainViewHolder(DragSortAdapter adapter, View itemView) {
            super(adapter, itemView);
            sequence = (TextView) itemView.findViewById( R.id.sequence);
            name = (TextView) itemView.findViewById(R.id.name);
            delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long id = (long) delete.getTag();
                    MoodScopeAdapter.this.delete(id);

                }
            });
        }

        @Override
        public void onClick(@NonNull View v) {
            Log.d("MOODSCOPE", sequence.getText() + " clicked!");
        }

        @Override
        public boolean onLongClick(@NonNull View v) {
            startDrag();
            return true;
        }

        @Override
        public View.DragShadowBuilder getShadowBuilder(View itemView, Point touchPoint) {
            return new NoForegroundShadowBuilder(itemView, touchPoint);
        }
    }
}