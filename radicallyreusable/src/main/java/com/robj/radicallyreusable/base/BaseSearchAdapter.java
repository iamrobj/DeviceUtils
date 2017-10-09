package com.robj.radicallyreusable.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.robj.radicallyreusable.base.base_list.BaseListRecyclerAdapter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by JJ on 16/06/15.
 */
public abstract class BaseSearchAdapter<T extends Searchable, VH extends RecyclerView.ViewHolder> extends BaseListRecyclerAdapter<T, VH> {

    private ArrayList<T> visibleObjects = new ArrayList<>();
    private ArrayList<T> allObjects = new ArrayList<>();

    private boolean allowSearchInside = false;

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
        super.clear();
        super.addAll(allObjects);
        notifyDataSetChanged();
    }

    public void setFilter(String queryText) {
        visibleObjects.clear();
        for (T t: allObjects)
            if (!TextUtils.isEmpty(t.getName()) &&
                    (allowSearchInside ? t.getName().toLowerCase().contains(queryText) : t.getName().toLowerCase().startsWith(queryText)))
                visibleObjects.add(t);
        super.clear();
        super.addAll(visibleObjects);
        notifyDataSetChanged();
    }

    public void clear() {
        super.clear();
        visibleObjects.clear();
        allObjects.clear();
    }

    public void allowSearchInside(boolean allowSearchInside) {
        this.allowSearchInside = allowSearchInside;
    }

}
