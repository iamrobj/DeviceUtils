package com.robj.radicallyreusable.base.mvp.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.robj.radicallyreusable.R;
import com.robj.radicallyreusable.base.mvp.fragment.BaseMvpFragment;
import com.robj.radicallyreusable.base.mvp.fragment.BaseMvpPresenter;
import com.robj.radicallyreusable.base.mvp.fragment.BaseMvpView;

/**
 * Created by jj on 19/02/17.
 */

public abstract class BaseMvpActivity<V extends BaseMvpView, P extends BaseMvpPresenter<V>> extends MvpActivity<V, P> {

    protected final String TAG = getClass().getSimpleName();

    Toolbar toolbar;

    private BaseMvpFragment currentFragment;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    protected void showHomeAsUp() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    protected void pushFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if(addToBackStack)
            ft.addToBackStack(fragment.getClass().getName());
        setCurrentVisibleFragment((BaseMvpFragment) fragment);
        ft.replace(R.id.content_frame, fragment, fragment.getClass().getName());
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if(currentFragment == null || !currentFragment.isBackPressed())
            super.onBackPressed();
    }

    /**
     * Pops a fragment off the backstack
     *
     * @return flag to tell the caller if there are more items.
     */
    protected boolean popFragments() {
        final FragmentManager manager = getSupportFragmentManager();
        manager.popBackStackImmediate();
        final int previousEntryIndex = manager.getBackStackEntryCount();
        if (previousEntryIndex > 0) {
            final FragmentManager.BackStackEntry previousBackStackEntry = manager.getBackStackEntryAt(previousEntryIndex);
            if (previousBackStackEntry.getName() != null) {
                setCurrentVisibleFragment((BaseMvpFragment) manager.findFragmentByTag(previousBackStackEntry.getName()));
            }
            return manager.getBackStackEntryCount() > 0;
        } else {
            return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public BaseMvpFragment getCurrentFragment() {
        return currentFragment;
    }

    protected void setCurrentVisibleFragment(BaseMvpFragment currentVisibleFragment) {
        this.currentFragment = currentVisibleFragment;
    }

    protected Toolbar getToolbar() {
        return toolbar;
    }

    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    public void showProgressDialog(int errorResId) {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(getString(errorResId));
        progressDialog.show();
    }

    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

}
