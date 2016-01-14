package org.heaven7.core.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;

import org.heaven7.core.viewhelper.ViewHelper;

import java.lang.ref.WeakReference;

/**
 * Created by heaven7 on 2016/1/14.
 * @since 1.7.5
 */
public abstract class DecoratedItemPostCallback implements AdapterManager.IPostRunnableCallback{

    private final SparseIntArray mSizeMap = new SparseIntArray();
    private final WeakReference<RecyclerView> mWeakRecyclerView;

    public DecoratedItemPostCallback(RecyclerView rv){
        mWeakRecyclerView = new WeakReference<RecyclerView>(rv);
    }


    @Override
    public void onPostCallback(int position, ISelectable item, int itemLayoutId, ViewHelper helper) {
        RecyclerView rv ;
        if( ( rv = mWeakRecyclerView.get()) == null){
            return;
        }
        mSizeMap.put(position,rv.getLayoutManager().getDecoratedMeasuredHeight(helper.getRootView()));
        rv.getLayoutParams().height = calculateHeight(mSizeMap);
        rv.requestLayout();
    }

    /**
     * @param heightMap  the height map , key is position,value is the height of position
     * @return the new height of RecyclerView
     */
    protected abstract int calculateHeight(SparseIntArray heightMap);


}
