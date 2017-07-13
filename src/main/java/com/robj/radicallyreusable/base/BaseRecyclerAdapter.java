package com.robj.radicallyreusable.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by rjoseph on 27/10/2016.
 */

public abstract class BaseRecyclerAdapter<O extends Object, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private final Context context;
    private final List<O> items = new ArrayList<>();
    private boolean isEnabled = true;

    public BaseRecyclerAdapter(final Context context) {
        this.context = context;
    }

    protected Context getContext() {
        return context;
    }

    protected View inflate(int layoutResId, ViewGroup parent) {
        return LayoutInflater.from(getContext()).inflate(layoutResId, parent, false);
    }

    public void add(final O o) {
        int size = getItemCount();
        items.add(o);
        notifyItemInserted(size);
    }

    public void addAll(final Collection<O> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public O getItemAtPosition(final int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void removeItemAt(final int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void removeRemoveRange(int startPosition, final int endPosition) {
        for(int i = endPosition; i >= startPosition; i--)
            items.remove(i);
        notifyItemRangeRemoved(startPosition, endPosition);
    }

    public void addItemAt(final int index, final O o) {
        items.add(index, o);
        notifyItemInserted(index);
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public List<O> getItems() {
        return items;
    }

    public void addAllAt(int index, Collection<O> items) {
        this.items.addAll(index, items);
        notifyItemRangeChanged(index, index + items.size());
    }

    protected boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addOrReplaceAll(Collection<O> results) {
        for(O o : results)
            addOrReplace(o);
    }

    public void replaceAt(int indexOf, O o) {
        getItems().set(indexOf, o);
        notifyItemChanged(indexOf);
    }

    public void addOrReplace(O o) {
        int indexOf = -1;
        if ((indexOf = getItems().indexOf(o)) > -1)
            replaceAt(indexOf, o);
        else
            add(o);
    }
}
