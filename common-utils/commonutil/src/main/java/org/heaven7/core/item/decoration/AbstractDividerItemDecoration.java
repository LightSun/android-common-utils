package org.heaven7.core.item.decoration;

import android.support.v7.widget.RecyclerView;

/**
 * Created by heaven7 on 2016/1/6.
 */
public  abstract class AbstractDividerItemDecoration extends RecyclerView.ItemDecoration{

    private final IDividerManager mDividerManager;

    public AbstractDividerItemDecoration() {
        mDividerManager = new DividerManagerImpl();
    }

    public IDividerManager getDividerManager() {
        return mDividerManager;
    }

}
