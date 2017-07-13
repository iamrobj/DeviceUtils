package com.robj.radicallyreusable.base.mvp.fragment;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by jj on 05/02/17.
 */

public abstract class BaseMvpPresenter<V extends BaseMvpView> extends MvpBasePresenter<V> {

    public final String TAG = getClass().getSimpleName();
    private CompositeSubscription subscriptions = new CompositeSubscription();

    public void onResume() { }

    public void onPause() {
        unsubscribeAll();
    }

    protected void unsubscribeAll() {
        if(!subscriptions.isUnsubscribed())
            subscriptions.unsubscribe();
        subscriptions = new CompositeSubscription(); //TODO: Is this required??
    }

    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }

}
