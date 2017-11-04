package com.robj.radicallyreusable.base.mvp.fragment;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by jj on 05/02/17.
 */

public abstract class BaseMvpPresenter<V extends BaseMvpView> extends MvpBasePresenter<V> {

    public final String TAG = getClass().getSimpleName();
    private CompositeDisposable subscriptions = new CompositeDisposable();

    public void onResume() { }

    public void onPause() {
        unsubscribeAll();
    }

    protected void unsubscribeAll() {
        if(!subscriptions.isDisposed())
            subscriptions.dispose();
        subscriptions = new CompositeDisposable(); //TODO: Is this required??
    }

    public void addSubscription(Disposable disposable) {
        subscriptions.add(disposable);
    }

}
