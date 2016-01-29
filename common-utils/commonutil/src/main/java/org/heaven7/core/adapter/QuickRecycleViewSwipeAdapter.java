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
        extends RecyclerView.Adapter<QuickRecycleViewSwipeAdapter.ViewHolder>
        implements AdapterManager.IAdapterManagerCallback, AdapterManager.IHeaderFooterManager ,
        AdapterManager.IAdapterManagerCallback2{

    private static final int MAX_VIEW_TYPE = 0;

    private final AdapterManager<T> mAdapterManager;
    private HeaderFooterHelper mHeaderFooterHelper;

    private int mLayoutId     = 0 ;
    private int mMenuLayoutId = 0 ;

    /** value is the view type */
    private final ArrayMap<LayoutIdHolder,Integer> mViewTypeMap = new ArrayMap<>(5);
    private int mMinValueType = MAX_VIEW_TYPE;

    private final SimpleSwipeStateChangeListener mSwipeListener = new SimpleSwipeStateChangeListener();
    /**
     * create QuickRecycleViewAdapter with the layout id. if layoutId==0, the method
     * {@link #getItemLayoutId(int, ISelectable)} will be called.
     * @param layoutId the layout id you want to inflate, or 0 if you want multi item.
     * @param mDatas the datas
     */
    public QuickRecycleViewSwipeAdapter(int layoutId,int menuLayoutId, List<T> mDatas) {
       this(layoutId, menuLayoutId, mDatas, ISelectable.SELECT_MODE_SINGLE);
    }
    /**
     * create QuickRecycleViewAdapter with the layout id. if layoutId==0, the method
     * {@link #getItemLayoutId(int, ISelectable)} will be called.
     * @param layoutId the layout id you want to inflate, or 0 if you want multi item.
     * @param mDatas
     * @param selectMode  select mode
     */
    public QuickRecycleViewSwipeAdapter(int layoutId,int menuLayoutId ,List<T> mDatas, int selectMode) {
       if(layoutId <0 ){
           throw new IllegalArgumentException("layoutId can't be negative ");
       }
        this.mLayoutId     =  layoutId;
        this.mMenuLayoutId =  menuLayoutId;
       // mAdapterManager = createAdapterManager(mDatas,selectMode);
        mAdapterManager = new AdapterManager<T>(mDatas,selectMode,this){
            @Override
            public IHeaderFooterManager getHeaderFooterManager() {
                return QuickRecycleViewSwipeAdapter.this;
            }
        };
        onFinalInit();
    }

    /** called before {@link #notifyDataSetChanged()} */
    @Override
    public void beforeNotifyDataChanged() {
        mViewTypeMap.clear();
        mMinValueType = MAX_VIEW_TYPE;
    }
    /** this is callled after data {@link #notifyDataSetChanged()} */
    @Override
    public void afterNotifyDataChanged(){

    }
    /** the init operation of the last, called in constructor */
    protected void onFinalInit() {

    }

    //=================== start header footer view ======================= //
    @Override
    public void addHeaderView(View v){
        if(mHeaderFooterHelper == null)
            mHeaderFooterHelper = new HeaderFooterHelper();
        int headerSize = getHeaderSize();
        mHeaderFooterHelper.addHeaderView(v);
        notifyItemInserted(headerSize);
    }
    @Override
    public void removeHeaderView(View v){
        if(mHeaderFooterHelper !=null){
            int index = mHeaderFooterHelper.removeHeaderView(v);
            if(index != -1){
                notifyItemRemoved(index);
            }
        }
    }
    @Override
    public void addFooterView(View v){
        if(mHeaderFooterHelper ==null)
            mHeaderFooterHelper = new HeaderFooterHelper();
        int itemCount = getItemCount();
        mHeaderFooterHelper.addFooterView(v);
        notifyItemInserted(itemCount);
    }
    @Override
    public void removeFooterView(View v){
        if(mHeaderFooterHelper !=null){
            int index = mHeaderFooterHelper.removeFooterView(v);
            if(index != -1){
                notifyItemRemoved(index + getHeaderSize() + mAdapterManager.getItemSize());
            }
        }
    }
    @Override
    public int getHeaderSize() {
        return mHeaderFooterHelper ==null ? 0 : mHeaderFooterHelper.getHeaderViewSize();
    }
    @Override
    public int getFooterSize() {
        return mHeaderFooterHelper ==null ? 0 : mHeaderFooterHelper.getFooterViewSize();
    }
    // =================== end header footer view ======================= //


    @Override
    public final boolean isRecyclable() {
        return true;
    }

    public SelectHelper<T> getSelectHelper(){
        return getAdapterManager().getSelectHelper();
    }

    public final T getItem(int position){
        return mAdapterManager.getItems().get(position);
    }
    /**
     * select the target position
     * only support select mode = {@link ISelectable#SELECT_MODE_MULTI}**/
    public void addSelected(int selectPosition){
        getSelectHelper().addSelected(selectPosition);
    }

    /**  un select the target position  .
     * <li>only support select mode = {@link ISelectable#SELECT_MODE_MULTI}*/
    public void addUnselected(int position){
        getSelectHelper().addUnselected(position);
    }

    /**
     * un select the all selected position.
     * mode single or multi all supoorted */
    public void clearAllSelected(){
        getSelectHelper().clearAllSelected();
    }
    /**
     * select the target position with notify data.if currentPosition  == position.ignore it.
     * <li></>only support select mode = {@link ISelectable#SELECT_MODE_SINGLE} ,this will auto update**/
    public void setSelected(int position){
        getSelectHelper().setSelected(position);
    }
    /** un select the target position
     * <li>only support select mode = {@link ISelectable#SELECT_MODE_SINGLE} */
    public void setUnselected(int position){
        getSelectHelper().setUnselected(position);
    }

    /** clear selected positions  . this just clear record. bu not notify item change
     * <li> support select mode = {@link ISelectable#SELECT_MODE_SINGLE} or {@link ISelectable#SELECT_MODE_MULTI}*/
    public void clearSelectedPositions(){
        getSelectHelper().clearSelectedPositions();
    }

    public T getSelectedData(){
       return getSelectHelper().getSelectedItem();
    }

    public List<T> getSelectedItems(){
        return getSelectHelper().getSelectedItems();
    }

    public int getSelectedPosition(){
        return getSelectHelper().getSelectedPosition() ;
    }

    //====================== begin items ========================//

    @Override
    public AdapterManager<T> getAdapterManager() {
        return mAdapterManager;
    }

    //====================== end items ========================//

    @Override
    public final int getItemViewType(int position) {
        if(mHeaderFooterHelper !=null){
            //in header or footer
            if(mHeaderFooterHelper.isInHeader(position) ||
                    mHeaderFooterHelper.isInFooter(position,mAdapterManager.getItemSize())) {
                return position;
            }
            position -= mHeaderFooterHelper.getHeaderViewSize();
        }
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
            if(mHeaderFooterHelper != null)
                mHeaderFooterHelper.recordLayoutId(viewType);
        }else{
            viewType = val;
        }
        return viewType;
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderFooterHelper == null || mHeaderFooterHelper.isLayoutIdInRecord(viewType)){
            LayoutIdHolder holder = findLayoutIdHolder(viewType);
            return new ViewHolder(parent, holder.mMainLayoutId, holder.mMenuLayoutId,
                    getTrackingEdge(), mSwipeListener);
        }else{
            return new ViewHolder(mHeaderFooterHelper.findView(viewType,mAdapterManager.getItemSize()));
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
    public final void onBindViewHolder(ViewHolder holder,int position) {
        if(mHeaderFooterHelper !=null) {
            if ( mHeaderFooterHelper.isInHeader(position)
                    || mHeaderFooterHelper.isInFooter(position,mAdapterManager.getItemSize())){
                return ;
            }
            position -= mHeaderFooterHelper.getHeaderViewSize();
        }
        //not in header or footer populate it
        final T item = getItem(position);
        final int layoutId = holder.getSwipeHelper().getMainLayoutId();
        final ViewHelper helper = holder.mViewHelper;

       onBindData(holder.getContext(), position, item, layoutId,
               holder.getSwipeHelper().getMenuLayoutId() , helper );

        if(getAdapterManager().getPostRunnableCallbacks() != null){
            final int pos = position;
            for(final AdapterManager.IPostRunnableCallback<T> callback : getAdapterManager()
                    .getPostRunnableCallbacks()){
                holder.itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onPostCallback(pos, item, layoutId , helper);
                    }
                });
            }
        }
    }

    @Override
    public final int getItemCount() {
        return mHeaderFooterHelper == null ? mAdapterManager.getItemSize() :
                mAdapterManager.getItemSize() + mHeaderFooterHelper.getHeaderViewSize() +
                        mHeaderFooterHelper.getFooterViewSize();
    }

    // may use
    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.mViewHelper.getRootView().clearAnimation();
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
    protected int getItemLayoutId(int position,T t) {
        return mLayoutId;
    }
    /** if you use multi item ,override this */
    protected int getItemMenuLayoutId(int position, int itemLayoutId, T t) {
        return mMenuLayoutId;
    }

    protected abstract void onBindData(Context context, int position,  T item,
                                       int itemLayoutId,int menuLayoutId, ViewHelper helper);

    /*public*/ static class ViewHolder extends SwipeHelper.BaseSwipeViewHolder{

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
