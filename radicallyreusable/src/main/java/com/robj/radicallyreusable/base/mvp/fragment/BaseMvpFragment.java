package com.robj.radicallyreusable.base.mvp.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.robj.radicallyreusable.R;

/**
 * Created by Rob J on 05/02/17.
 */

public abstract class BaseMvpFragment<V extends BaseMvpView, P extends BaseMvpPresenter<V>> extends com.hannesdorfmann.mosby.mvp.MvpFragment<V, P> implements BaseMvpView {

    public final String TAG = getClass().getSimpleName();

    private ProgressDialog progressDialog;
    private AlertDialog errorDialog;
    private BaseMvpFragment currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstance) {
        super.onCreateView(inflater, container, savedInstance);
        final View v = inflater.inflate(getLayoutResId(), container, false);
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getPresenter() != null)
            getPresenter().onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getPresenter() != null)
            getPresenter().onResume();
    }

    public void showProgressDialog(int errorResId) {
        progressDialog.setMessage(getString(errorResId));
        progressDialog.show();
    }

    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void showErrorDialog(int errorResId) {
        showErrorDialog(getString(errorResId));
    }

    public void showErrorDialog(String error) {
        hideProgressDialog();
        if(errorDialog == null)
            errorDialog = new AlertDialog.Builder(getActivity()).create();
        errorDialog.setMessage(error);
    }

    public void showToast(int stringResId) {
        Toast.makeText(getActivity(), stringResId, Toast.LENGTH_SHORT).show();
    }

    protected void pushChildFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if(addToBackStack)
            ft.addToBackStack(fragment.getClass().getName());
        setCurrentVisibleFragment((BaseMvpFragment) fragment);
        ft.replace(R.id.content_frame, fragment, fragment.getClass().getName());
        ft.commit();
    }

    /**
     * Pops a fragment off the backstack
     *
     * @return flag to tell the caller if there are more items.
     */
    protected boolean popFragments() {
        final FragmentManager manager = getChildFragmentManager();
        manager.popBackStackImmediate();
        final int previousEntryIndex = manager.getBackStackEntryCount();
        if (previousEntryIndex > 0) {
            final FragmentManager.BackStackEntry previousBackStackEntry = manager.getBackStackEntryAt(previousEntryIndex);
            if (previousBackStackEntry.getName() != null) {
                setCurrentVisibleFragment((BaseMvpFragment) manager.findFragmentByTag(previousBackStackEntry.getName()));
            }
            return manager.getBackStackEntryCount() > 0;
        } else {
            setCurrentVisibleFragment(null);
            return false;
        }

    }

    protected void setCurrentVisibleFragment(BaseMvpFragment currentVisibleFragment) {
        this.currentFragment = currentVisibleFragment;
    }

    protected abstract int getLayoutResId();

    public boolean isBackPressed() {
        if(currentFragment != null && currentFragment.isBackPressed())
            return true;
        return false;
    }
}
