package com.robj.radicallyreusable.base.base_list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.robj.radicallyreusable.R;
import com.robj.radicallyreusable.base.BaseRecyclerAdapter;
import com.robj.radicallyreusable.base.mvp.BaseViewHolder;

import java.util.Collection;

/**
 * Created by Rob J on 12/03/17.
 */

public abstract class BaseListRecyclerAdapter<O, VH extends RecyclerView.ViewHolder> extends BaseRecyclerAdapter<O, RecyclerView.ViewHolder> {

    private final static int TYPE_SPINNER = -1;

    public BaseListRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(TYPE_SPINNER == viewType) {
            View v = inflate(R.layout.row_spinner, parent);
            EmptyViewHolder vh = new EmptyViewHolder(v);
            return vh;
        }
        return createVH(parent, viewType);
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if(type != TYPE_SPINNER)
            onBindViewHolder((VH) holder, position, type);
    }

    @Override
    public final int getItemViewType(int position) {
        if(getItemAtPosition(position) == null)
            return TYPE_SPINNER;
        return getViewType(position);
    }

    protected int getViewType(int position) {
        return super.getItemViewType(position);
    }

    public void setHasMoreToLoad(boolean hasMoreToLoad) {
        int size = getItemCount();
        if(size > 0) {
            if (hasMoreToLoad && getItemViewType(size - 1) != TYPE_SPINNER)
                addLoadingProgress();
            else if(!hasMoreToLoad && getItemViewType(size - 1) == TYPE_SPINNER)
                removeItemAt(size - 1);
        } else if(hasMoreToLoad)
            addLoadingProgress();
    }

    @Override
    public void add(O o) {
        if(o == null)
            throw new RuntimeException("Progress loader uses null as it's value, don't insert null here");
        int size = getItemCount();
        if(size > 0 && getItemViewType(size - 1) == TYPE_SPINNER)
            super.addItemAt(getItemCount() - 1, o);
        else
            super.add(o);
    }

    @Override
    public void addAll(Collection<O> items) {
        int size = getItemCount();
        if(size > 0 && getItemViewType(size - 1) == TYPE_SPINNER)
            super.addAllAt(size - 1, items);
        else
            super.addAll(items);
    }

    @Override
    public void addItemAt(int index, O o) {
        if(o == null)
            throw new RuntimeException("Progress loader uses null as it's value, don't insert null here");
        super.addItemAt(index, o);
    }

    protected O getViewHolderItem(BaseViewHolder vh) {
        int pos = vh.getAdapterPosition();
        return getItemAtPosition(pos);
    }

    public boolean isEmpty() {
        return getItems().isEmpty();
    }

    protected void addLoadingProgress() {
        super.add(null);
    }

    protected abstract VH createVH(ViewGroup parent, int viewType);
    protected abstract void onBindViewHolder(VH holder, int position, int type);

}
