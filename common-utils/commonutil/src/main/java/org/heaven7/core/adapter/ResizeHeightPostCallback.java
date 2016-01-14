package org.heaven7.core.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseIntArray;

import org.heaven7.core.viewhelper.ViewHelper;

import java.lang.ref.WeakReference;

/**
 * <p>
 * this callback help you resize the height of RecyclerView. this is useful while ScrollView nested
 * RecyclerView with GridLayoutManager or StaggeredGridLayoutManager. but in layout xml you should define
 * a opportune height to RecyclerView, not wrap_content.
 * </p>
 * Created by heaven7 on 2016/1/14.
 * @since 1.7.5
 */
public class ResizeHeightPostCallback<T extends ISelectable> implements AdapterManager.IPostRunnableCallback<T>{

    private final SparseIntArray mSizeMap = new SparseIntArray();
    private final WeakReference<RecyclerView> mWeakRecyclerView;

    public ResizeHeightPostCallback(RecyclerView rv){
        mWeakRecyclerView = new WeakReference<RecyclerView>(rv);
    }


    @Override
    public void onPostCallback(int position, T item, int itemLayoutId, ViewHelper helper) {
        RecyclerView rv ;
        if( ( rv = mWeakRecyclerView.get()) == null){
            return;
        }
        mSizeMap.put(position,rv.getLayoutManager().getDecoratedMeasuredHeight(helper.getRootView()));
        rv.getLayoutParams().height = calculateHeight(rv.getLayoutManager(),mSizeMap);
        rv.requestLayout();
    }

    /**
     * @param lm the LayoutManager of LayoutManager
     * @param heightMap  the height map , key is position,value is the height of position
     * @return the new height of RecyclerView
     */
    protected int calculateHeight(RecyclerView.LayoutManager lm, SparseIntArray heightMap){
        int spanCount = 1;
        if(lm instanceof GridLayoutManager){
            spanCount = ((GridLayoutManager) lm).getSpanCount();
        }else if(lm instanceof StaggeredGridLayoutManager){
            spanCount = ((StaggeredGridLayoutManager) lm).getSpanCount();
        }
        final int mapSize = heightMap.size();
        int total = 0;
        for(int i = 0 ,size = mapSize % spanCount == 0 ? mapSize / spanCount:
                mapSize /spanCount + 1; i < size ; i++){
            total = total + heightMap.valueAt(i);
        }
        return total;
    }


}
