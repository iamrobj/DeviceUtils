package com.robj.radicallyreusable.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.robj.radicallyreusable.base.base_list.BaseListRecyclerAdapter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by JJ on 16/06/15.
 */
public abstract class BaseSearchAdapter<T extends Searchable, VH extends RecyclerView.ViewHolder> extends BaseListRecyclerAdapter<T, VH> {

    private ArrayList<T> visibleObjects = new ArrayList<>();
    private ArrayList<T> allObjects = new ArrayList<>();

    public BaseSearchAdapter(Context context) {
        super(context);
    }

    @Override
    public void addAll(Collection<T> items) {
        super.addAll(items);
        allObjects.addAll(items);
    }

    public void flushFilter(){
        visibleObjects.clear();
        clear();
        super.addAll(allObjects);
        notifyDataSetChanged();
    }

    public void setFilter(String queryText) {
        visibleObjects.clear();
        for (T app: allObjects)
            if (app.getName().toLowerCase().startsWith(queryText))
                visibleObjects.add(app);
        clear();
        super.addAll(visibleObjects);
        notifyDataSetChanged();
    }

}
