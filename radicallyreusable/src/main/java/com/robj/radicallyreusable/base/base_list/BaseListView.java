package com.robj.radicallyreusable.base.base_list;

import com.robj.radicallyreusable.base.mvp.fragment.BaseMvpView;

import java.util.Collection;

/**
 * Created by Rob J on 05/02/17.
 */
public interface BaseListView<T> extends BaseMvpView {

    void showProgress();

    void addResults(Collection<T> results);

    void hideProgress();

    void addResult(T place);

    void showError(int errorResId);

    boolean isRefreshing();

    void clear();

}
