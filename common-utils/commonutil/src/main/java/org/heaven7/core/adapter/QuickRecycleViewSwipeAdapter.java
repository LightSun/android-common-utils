/*
 * Copyright (C) 2015
 *            heaven7(donshine723@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.heaven7.core.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.heaven7.core.viewhelper.ViewHelper;

import java.util.Arrays;
import java.util.List;

/**
 * Created by heaven7 on 2015/8/26.
 * @param <T>  the data
 * @since 1.8.0
 */
public abstract class QuickRecycleViewSwipeAdapter<T extends ISelectable>
        extends QuickRecycleViewAdapter<T>{

    private static final int MAX_VIEW_TYPE = 0;

    private int mMenuLayoutId = 0 ;

    /** value is the view type */
    private final ArrayMap<LayoutIdHolder,Integer> mViewTypeMap = new ArrayMap<>(5);
    private int mMinValueType = MAX_VIEW_TYPE;

    private final SimpleSwipeStateChangeListener mSwipeListener = new SimpleSwipeStateChangeListener();
    /**
     * create QuickRecycleViewAdapter with the layout id. if layoutId==0, the method
     * {@link #getItemLayoutId(int, ISelectable)} will be called.
     * @param layoutId the layout id you want to inflate, or 0 if you want multi item.
     * @param menuLayoutId the menulayout id of swipe
     * @param mDatas the datas
     */
    public QuickRecycleViewSwipeAdapter(int layoutId,int menuLayoutId, List<T> mDatas) {
       this(layoutId, menuLayoutId, mDatas, ISelectable.SELECT_MODE_SINGLE);
    }
    /**
     * create QuickRecycleViewAdapter with the layout id. if layoutId==0, the method
     * {@link #getItemLayoutId(int, ISelectable)} will be called.
     * @param layoutId the layout id you want to inflate, or 0 if you want multi item.
     * @param menuLayoutId the menulayout id of swipe
     * @param mDatas the data
     * @param selectMode  select mode
     */
    public QuickRecycleViewSwipeAdapter(int layoutId,int menuLayoutId ,List<T> mDatas, int selectMode) {
        super(layoutId,mDatas,selectMode,false);
        this.mMenuLayoutId =  menuLayoutId;
        onFinalInit();
    }

    /** called before {@link #notifyDataSetChanged()} */
    @Override
    public void beforeNotifyDataChanged() {
        super.beforeNotifyDataChanged();
        mViewTypeMap.clear();
        mMinValueType = MAX_VIEW_TYPE;
    }

    //====================== end items ========================//


    @Override
    protected int getItemViewTypeImpl(HeaderFooterHelper hfHelper, int position) {
        final T t = getItem(position);
        int layoutId = getItemLayoutId(position, t);
        int menuLayoutId = getItemMenuLayoutId(position, layoutId, t);

        int viewType ;
        final LayoutIdHolder holder = new LayoutIdHolder(layoutId, menuLayoutId);
        // same layoutId && same  menuLayoutId ?  indicate -> same viewType
        Integer val = mViewTypeMap.get(holder);
        if(val == null){
            viewType =  --mMinValueType;
            holder.mViewType = viewType;
            mViewTypeMap.put(holder,viewType);
            if(hfHelper != null)
                hfHelper.recordLayoutId(viewType);
        }else{
            viewType = val;
        }
        return viewType;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolderImpl(HeaderFooterHelper hfHelper,
                                                             ViewGroup parent, int viewType) {
        if(hfHelper == null || hfHelper.isLayoutIdInRecord(viewType)){
            LayoutIdHolder holder = findLayoutIdHolder(viewType);
            return new ViewHolder(parent, holder.mMainLayoutId, holder.mMenuLayoutId,
                    getTrackingEdge(), mSwipeListener);
        }else{
            return new ViewHolder(hfHelper.findView(viewType,getAdapterManager().getItemSize()));
        }
    }

    private LayoutIdHolder findLayoutIdHolder(int viewType) {
        for(LayoutIdHolder holder : mViewTypeMap.keySet()){
            if(holder.mViewType == viewType){
                return holder;
            }
        }
        throw new RuntimeException("can't find LayoutIdHolder by viewType = " + viewType);
    }

    @Override
    protected void onBindDataImpl(RecyclerView.ViewHolder holder, int position, T item) {
        ViewHolder vh = (ViewHolder) holder;
        onBindData(vh.getContext(), position, item, vh.getLayoutId(),
                vh.getSwipeHelper().getMenuLayoutId(), vh.getViewHelper());
    }

    /**
     * when we opened a swipe item. we need to close the previous.so call this to ensure your event
     * must be called right now.
     * @return true when there is a item swiped and close it success. false otherwise.
     */
    public boolean shouldIgnoreTouchEvent(){
        return mSwipeListener.closeSwipeIfNeed();
    }

    /**
     * see {@link SwipeHelper#EDGE_LEFT} and {@link SwipeHelper#EDGE_RIGHT}
     */
    protected int getTrackingEdge(){
        return SwipeHelper.EDGE_RIGHT;
    }
    /** if you use multi item ,override this */
    protected  @LayoutRes int getItemMenuLayoutId(int position, int itemLayoutId, T t) {
        return mMenuLayoutId;
    }

    @Override
    protected void onBindData(Context context, int position, T item, int itemLayoutId, ViewHelper helper) {
        // there is no need to implements this method
    }

    protected abstract void onBindData(Context context, int position,  T item,
                                       int itemLayoutId,int menuLayoutId, ViewHelper helper);

    /*public*/ static class ViewHolder extends SwipeHelper.BaseSwipeViewHolder implements
            QuickRecycleViewAdapter.IRecyclerViewHolder{

        public final ViewHelper mViewHelper;
        /** if is in header or footer ,mLayoutId = 0 */

        public ViewHolder(ViewGroup parent, @LayoutRes int mainLayoutId, @LayoutRes int menuLayoutId) {
            super(parent, mainLayoutId, menuLayoutId);
            this.mViewHelper = new ViewHelper(itemView);
        }

        public ViewHolder(ViewGroup parent, @LayoutRes int mainLayoutId, @LayoutRes int menuLayoutId,
                          int mTrackingEdges,SwipeHelper.OnSwipeStateChangeListener l) {
            super(parent, mainLayoutId, menuLayoutId, mTrackingEdges);
            this.mViewHelper = new ViewHelper(itemView);
            getSwipeHelper().setOnSwipeStateChangeListener(l);
        }

        /** only used for header/footer */
        public ViewHolder(View itemView) {
            super(itemView);
            this.mViewHelper = null;
        }
        public Context getContext(){
            return mViewHelper.getContext();
        }

        @Override
        public int getLayoutId() {
            return getSwipeHelper().getMainLayoutId();
        }

        @Override
        public ViewHelper getViewHelper() {
            return mViewHelper;
        }
    }

    static class LayoutIdHolder {
        int mMainLayoutId;
        int mMenuLayoutId;
        int mViewType;

        public LayoutIdHolder(int mMainLayoutId, int mMenuLayoutId) {
            this.mMainLayoutId = mMainLayoutId;
            this.mMenuLayoutId = mMenuLayoutId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LayoutIdHolder layoutIds = (LayoutIdHolder) o;
            return mMainLayoutId == layoutIds.mMainLayoutId &&
                    mMenuLayoutId == layoutIds.mMenuLayoutId;
        }
        @Override
        public int hashCode() {
            return hash(mMainLayoutId, mMenuLayoutId);
        }
        static int hash(Object... values) {
            return Arrays.hashCode(values);
        }
    }

}
