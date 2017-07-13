// Generated code from Butter Knife. Do not modify!
package com.robj.radicallyreusable.base.base_list;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.robj.radicallyreusable.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BaseListFragment_ViewBinding<T extends BaseListFragment> implements Unbinder {
  protected T target;

  @UiThread
  public BaseListFragment_ViewBinding(T target, View source) {
    this.target = target;

    target.list = Utils.findRequiredViewAsType(source, R.id.list, "field 'list'", RecyclerView.class);
    target.progressContainer = Utils.findRequiredView(source, R.id.progress_container, "field 'progressContainer'");
    target.progress = Utils.findRequiredViewAsType(source, R.id.progress, "field 'progress'", ProgressBar.class);
    target.progressLabel = Utils.findRequiredViewAsType(source, R.id.progress_label, "field 'progressLabel'", TextView.class);
    target.swipeToRefresh = Utils.findRequiredViewAsType(source, R.id.swipe_to_refresh, "field 'swipeToRefresh'", SwipeRefreshLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.list = null;
    target.progressContainer = null;
    target.progress = null;
    target.progressLabel = null;
    target.swipeToRefresh = null;

    this.target = null;
  }
}
