package org.heaven7.core.adapter;

import android.support.annotation.LayoutRes;
import android.view.ViewGroup;

/**
 * Created by heaven7 on 2016/1/29.
 * @since 1.8.0
 */
/*public*/ class BaseSwipeAdapterHelper extends BaseAdapterHelper {

    private final SwipeHelper mSwiperHelper;

    public BaseSwipeAdapterHelper(ViewGroup parent,@LayoutRes int mainLayoutId,
                                  @LayoutRes int menuLayoutId, int mTrackingEdges,
                                  int position, SwipeHelper.OnSwipeStateChangeListener l){
         this(new SwipeHelper(parent, mainLayoutId, menuLayoutId, mTrackingEdges), position);
         mSwiperHelper.setOnSwipeStateChangeListener(l);
    }

    private BaseSwipeAdapterHelper(SwipeHelper sh, int mPosition) {
        super(sh.getItemView(), mPosition);
        this.mSwiperHelper = sh;
    }

    @Override
    public int getLayoutId() {
        return mSwiperHelper.getMainLayoutId();
    }

    @Override
    public int getMenuLayoutId() {
        return mSwiperHelper.getMenuLayoutId();
    }

}
