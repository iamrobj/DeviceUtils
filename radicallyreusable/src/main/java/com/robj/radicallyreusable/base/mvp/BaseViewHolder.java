package com.robj.radicallyreusable.base.mvp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by jj on 31/01/17.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    protected Context getContext() {
        return itemView.getContext();
    }

}
