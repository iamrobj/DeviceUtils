package com.robj.radicallyreusable.base;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Rob J on 05/11/2014.
 */
public class ScrollToLoadRecylcerView extends RecyclerView {

    private boolean isLoading;
    private OnScrollToLoadListener onScrollToLoadListener;
    private RecyclerView.OnScrollListener onScrollListener;

    public ScrollToLoadRecylcerView(Context context) {
        super(context);
        init();
    }

    public ScrollToLoadRecylcerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollToLoadRecylcerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        super.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = getChildCount();
                int totalItemCount = getLayoutManager().getItemCount();
                int firstVisibleItem = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();

                if (!isLoading && (firstVisibleItem + visibleItemCount) == totalItemCount) {
                    if (onScrollToLoadListener != null && onScrollToLoadListener.hasMoreToLoad()) {
                        isLoading = true;
                        ((ScrollToLoadAdapter) getAdapter()).addFooter();
                        onScrollToLoadListener.onLoadMore();
                    }
                }

                if(onScrollListener != null)
                    onScrollListener.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public boolean canScrollVertically(int direction) {
        // Fixes this bug - http://stackoverflow.com/a/25227797/924231
        if (direction < 1) {
            boolean original = super.canScrollVertically(direction);
            return !original && getChildAt(0) != null && getChildAt(0).getTop() < 0 || original;
        }
        return super.canScrollVertically(direction);

    }

    @Override
    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if(adapter instanceof ScrollToLoadAdapter)
            super.setAdapter(adapter);
        else
            throw new RuntimeException("Your adapter does not implement ScrollToLoadAdapter");
    }

    public void setLoadingComplete() {
        if (isLoading) {
            isLoading = false;
            ((ScrollToLoadAdapter) getAdapter()).removeFooter();
        }

    }

    public void setOnScrollToLoadListener(OnScrollToLoadListener onScrollToLoadListener) {
        this.onScrollToLoadListener = onScrollToLoadListener;
    }

    public interface OnScrollToLoadListener {
        public void onLoadMore();
        public boolean hasMoreToLoad();
    }

    public interface ScrollToLoadAdapter {
        public void addFooter();
        public void removeFooter();
    }

}
