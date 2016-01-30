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
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.heaven7.core.viewhelper.ViewHelper;

import java.util.List;

/**
 * Created by heaven7 on 2015/8/26.
 * @param <T>  the data
 */
public abstract class QuickRecycleViewAdapter<T extends ISelectable>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements AdapterManager.IAdapterManagerCallback, AdapterManager.IHeaderFooterManager ,
        AdapterManager.IAdapterManagerCallback2{

    private int mLayoutId = 0;
    private HeaderFooterHelper mHeaderFooterHelper;
    private AdapterManager<T> mAdapterManager;

    /**
     * create QuickRecycleViewAdapter with the layout id. if layoutId==0, the method
     * {@link #getItemLayoutId(int, ISelectable)} will be called.
     * @param layoutId the layout id you want to inflate, or 0 if you want multi item.
     * @param mDatas
     */
    public QuickRecycleViewAdapter(int layoutId, List<T> mDatas) {
       this(layoutId, mDatas, ISelectable.SELECT_MODE_SINGLE);
    }
    /**
     * create QuickRecycleViewAdapter with the layout id. if layoutId==0, the method
     * {@link #getItemLayoutId(int, ISelectable)} will be called.
     * @param layoutId the layout id you want to inflate, or 0 if you want multi item.
     * @param mDatas
     * @param selectMode  select mode
     */
    public QuickRecycleViewAdapter(int layoutId, List<T> mDatas, int selectMode) {
         this(layoutId,mDatas,selectMode,true);
    }

    /**internal */
    QuickRecycleViewAdapter(int layoutId, List<T> mDatas, int selectMode,boolean callFinalInit) {
       if(layoutId <0 ){
           throw new IllegalArgumentException("layoutId can't be negative ");
       }
        this.mLayoutId = layoutId;
       // mAdapterManager = createAdapterManager(mDatas,selectMode);
        mAdapterManager = new AdapterManager<T>(mDatas,selectMode,this){
            @Override
            public IHeaderFooterManager getHeaderFooterManager() {
                return QuickRecycleViewAdapter.this;
            }
        };
        if(callFinalInit) {
            onFinalInit();
        }
    }


    /** called before {@link #notifyDataSetChanged()} */
    @Override
    public void beforeNotifyDataChanged() {

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
                    mHeaderFooterHelper.isInFooter(position,mAdapterManager.getItemSize()))
                return position;

            position -= mHeaderFooterHelper.getHeaderViewSize();
        }
        return getItemViewTypeImpl(mHeaderFooterHelper, position);
    }

    //extract for swipe adapter
    protected int getItemViewTypeImpl(HeaderFooterHelper hfHelper, int position) {
        int layoutId = getItemLayoutId(position, getItem(position));
        if(hfHelper != null)
            hfHelper.recordLayoutId(layoutId);
        return layoutId;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateViewHolderImpl(mHeaderFooterHelper, parent, viewType);
    }

    @NonNull
    protected RecyclerView.ViewHolder onCreateViewHolderImpl(HeaderFooterHelper hfHelper,
                                                           ViewGroup parent, int viewType) {
        if(this.mHeaderFooterHelper == null || this.mHeaderFooterHelper.isLayoutIdInRecord(viewType)){
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    viewType,parent,false), viewType);
        }else{
            return new ViewHolder(this.mHeaderFooterHelper.findView(viewType, mAdapterManager.getItemSize()));
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder,int position) {
        if(mHeaderFooterHelper !=null) {
            if ( mHeaderFooterHelper.isInHeader(position)
                    || mHeaderFooterHelper.isInFooter(position,mAdapterManager.getItemSize())){
                return ;
            }
            position -= mHeaderFooterHelper.getHeaderViewSize();
        }
        if(! (holder instanceof IRecyclerViewHolder)){
             throw new RuntimeException("all quick adapter's viewHolder must implement" +
                     " the interface IRecyclerViewHolder");
        }
        //not in header or footer populate it
        final T item = getItem(position);
        final int layoutId = ((IRecyclerViewHolder) holder).getLayoutId();
        final ViewHelper helper = ((IRecyclerViewHolder) holder).getViewHelper();

        onBindDataImpl(holder, position, item);

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

    protected void onBindDataImpl(RecyclerView.ViewHolder holder, int position, T item) {
        final ViewHolder vh = (ViewHolder) holder;
        onBindData(vh.getContext(), position, item,  vh.getLayoutId(),
                vh.getViewHelper());
    }

    @Override
    public final int getItemCount() {
        return mHeaderFooterHelper == null ? mAdapterManager.getItemSize() :
                mAdapterManager.getItemSize() + mHeaderFooterHelper.getHeaderViewSize() +
                        mHeaderFooterHelper.getFooterViewSize();
    }

    // may use
    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if(holder instanceof IRecyclerViewHolder){
            ((IRecyclerViewHolder) holder).getViewHelper().getRootView().clearAnimation();
        }
    }

    /** if you use multi item ,override this */
    protected int getItemLayoutId(int position,T t) {
        return mLayoutId;
    }

    protected abstract void onBindData(Context context, int position,  T item,
                                       int itemLayoutId, ViewHelper helper);

    /*public*/ static class ViewHolder extends RecyclerView.ViewHolder implements IRecyclerViewHolder {

        public final ViewHelper mViewHelper;
        /** if is in header or footer ,mLayoutId = 0 */
        public final int mLayoutId;

        public ViewHolder(View itemView,int layoutId){
            super(itemView);
            this.mLayoutId = layoutId;
            this.mViewHelper = new ViewHelper(itemView);
        }

        public ViewHolder(View itemView) {
            this(itemView, 0);
        }
        public Context getContext(){
            return mViewHelper.getContext();
        }

        @Override
        public int getLayoutId() {
            return mLayoutId;
        }

        @Override
        public ViewHelper getViewHelper() {
            return mViewHelper;
        }
    }

    public interface IRecyclerViewHolder {
        /** get the item layout id*/
        int getLayoutId();
        /** get the ViewHelper */
        ViewHelper getViewHelper();
    }
}
