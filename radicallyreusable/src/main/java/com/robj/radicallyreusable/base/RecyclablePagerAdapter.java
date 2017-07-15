package com.robj.radicallyreusable.base;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rjoseph on 27/10/2016.
 */

public abstract class RecyclablePagerAdapter<V extends View, T> extends PagerAdapter {

    private final Context context;
    private final List<V> reusableViews = new ArrayList<>();
    private final List<T> items = new ArrayList<>();

    public RecyclablePagerAdapter(final Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(final ViewGroup collection, final int position) {
        final V v = inflateOrRecycleView();
        doWithView(v, position);
        collection.addView(v, 0);
        return v;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public T getItemAt(final int position) {
        return items.get(position);
    }

    public void addAll(final List<T> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public int getItemPosition(Object object) { //So that all views will be refreshed on notifydatasetchanged
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view == object;
    }

    protected Context getContext() {
        return context;
    }

    private V inflateOrRecycleView() {
        final V v;
        if (reusableViews.isEmpty()) {
            v = createView();
            log("Creating new view..");
        } else {
            v = reusableViews.get(0);
            reusableViews.remove(0);
            log("Reused view from cache..");
        }
        return v;
    }

    @Override
    public void destroyItem(final ViewGroup collection, final int position, final Object view) {
        final V recycledView = (V) view;
        collection.removeView((V) view);
        reusableViews.add(recycledView);
        log("Stored view in cache..");
    }

    private void log(final String msg) {
        Log.i(getClass().getSimpleName(), msg);
    }

    protected abstract V createView();
    protected abstract void doWithView(V v, int position);

    public List<T> getItems() {
        return items;
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void add(T t) {
        items.add(t);
        notifyDataSetChanged();
    }
}
