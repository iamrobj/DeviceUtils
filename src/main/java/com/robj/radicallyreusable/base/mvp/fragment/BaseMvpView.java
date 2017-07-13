package com.robj.radicallyreusable.base.mvp.fragment;


/**
 * Created by jj on 05/02/17.
 */

public interface BaseMvpView extends com.hannesdorfmann.mosby.mvp.MvpView {

    void showProgressDialog(int stringResId);

    void hideProgressDialog();

    void showErrorDialog(int errorResId);

    void showToast(int stringResId);
}
