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
        extends RecyclerView.Adapter<QuickRecycleViewAdapter.ViewHolder> {

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
       if(layoutId <0 ){
           throw new IllegalArgumentException("layoutId can't be negative ");
       }
        this.mLayoutId = layoutId;
        mAdapterManager = createAdapterManager(mDatas,selectMode);
    }

    private AdapterManager<T> createAdapterManager(List<T> mDatas, int selectMode) {
        return new AdapterManager<T>(mDatas,selectMode) {
            @Override
            protected void notifyDataSetChangedImpl() {
                QuickRecycleViewAdapter.this.notifyDataSetChanged();
            }

            @Override
            protected boolean isRecyclable() {
                return true;
            }

            @Override
            public void notifyItemInserted(int position) {
                QuickRecycleViewAdapter.this.notifyItemInserted(position);
            }

            @Override
            public void notifyItemChanged(int position) {
                QuickRecycleViewAdapter.this.notifyItemChanged(position);
            }

            @Override
            public void notifyItemMoved(int fromPosition, int toPosition) {
                QuickRecycleViewAdapter.this.notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void notifyItemRemoved(int position) {
                QuickRecycleViewAdapter.this.notifyItemRemoved(position);
            }

            @Override
            public void notifyItemRangeChanged(int positionStart, int itemCount) {
                QuickRecycleViewAdapter.this.notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void notifyItemRangeInserted(int positionStart, int itemCount) {
                QuickRecycleViewAdapter.this.notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void notifyItemRangeRemoved(int positionStart, int itemCount) {
                QuickRecycleViewAdapter.this.notifyItemRangeRemoved(positionStart, itemCount);
            }

            @Override
            protected void beforeNotifyDataChanged() {
                QuickRecycleViewAdapter.this.beforeNotifyDataChanged();
            }

            @Override
            public boolean isHeaderFooterSupport() {
                return true;
            }

            @Override
            protected IHeaderFooterManager createHeaderFooterManager() {
               return QuickRecycleViewAdapter.this.createHeaderFooterManager();
            }
        };
    }
    private AdapterManager.IHeaderFooterManager createHeaderFooterManager() {
        return new AdapterManager.IHeaderFooterManager() {
            @Override
            public void addHeaderView(View v) {
                QuickRecycleViewAdapter.this.addHeaderView(v);
            }

            @Override
            public void removeHeaderView(View v) {
                QuickRecycleViewAdapter.this.removeHeaderView(v);
            }

            @Override
            public void addFooterView(View v) {
                QuickRecycleViewAdapter.this.addFooterView(v);
            }

            @Override
            public void removeFooterView(View v) {
                QuickRecycleViewAdapter.this.removeFooterView(v);
            }

            @Override
            public int getHeaderSize() {
                return  QuickRecycleViewAdapter.this.getHeaderSize();
            }

            @Override
            public int getFooterSize() {
                return  QuickRecycleViewAdapter.this.getFooterSize();
            }
        };
    }

    /** called before {@link AdapterManager#notifyDataSetChanged()} */
    protected void beforeNotifyDataChanged() {

    }

    //=================== start header footer view ======================= //
    public void addHeaderView(View v){
        if(mHeaderFooterHelper == null)
            mHeaderFooterHelper = new HeaderFooterHelper();
        int headerSize = getHeaderSize();
        mHeaderFooterHelper.addHeaderView(v);
        notifyItemInserted(headerSize);
    }
    public void removeHeaderView(View v){
        if(mHeaderFooterHelper !=null){
            int index = mHeaderFooterHelper.removeHeaderView(v);
            if(index != -1){
                notifyItemRemoved(index);
            }
        }
    }
    public void addFooterView(View v){
        if(mHeaderFooterHelper ==null)
            mHeaderFooterHelper = new HeaderFooterHelper();
        int itemCount = getItemCount();
        mHeaderFooterHelper.addFooterView(v);
        notifyItemInserted(itemCount);
    }
    public void removeFooterView(View v){
        if(mHeaderFooterHelper !=null){
            int index = mHeaderFooterHelper.removeFooterView(v);
            if(index != -1){
                notifyItemRemoved(index + getHeaderSize() + mAdapterManager.getItemSize());
            }
        }
    }
    public int getHeaderSize() {
        return mHeaderFooterHelper ==null ? 0 : mHeaderFooterHelper.getHeaderViewSize();
    }
    public int getFooterSize() {
        return mHeaderFooterHelper ==null ? 0 : mHeaderFooterHelper.getFooterViewSize();
    }
    // =================== end header footer view ======================= //

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
        int layoutId = getItemLayoutId(position, getItem(position));
        if(mHeaderFooterHelper != null)
            mHeaderFooterHelper.recordLayoutId(layoutId);
        return layoutId;
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderFooterHelper == null || mHeaderFooterHelper.isLayoutIdInRecord(viewType)){
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType,parent,false));
        }else{
            return new ViewHolder(mHeaderFooterHelper.findView(viewType,mAdapterManager.getItemSize()));
        }
    }

    @Override
    public final void onBindViewHolder(ViewHolder holder, int position) {
        if(mHeaderFooterHelper !=null) {
            if ( mHeaderFooterHelper.isInHeader(position)
                    || mHeaderFooterHelper.isInFooter(position,mAdapterManager.getItemSize())){
                return ;
            }
            position -= mHeaderFooterHelper.getHeaderViewSize();
        }
        //not in header or footer populate it
        onBindData(holder.getContext(), position, getItem(position), holder.mViewHelper);
    }

    @Override
    public final int getItemCount() {
        return mHeaderFooterHelper == null ? mAdapterManager.getItemSize() :
                mAdapterManager.getItemSize() + mHeaderFooterHelper.getHeaderViewSize() +
                        mHeaderFooterHelper.getFooterViewSize();
    }
    /** if you use multi item ,override this */
    protected int getItemLayoutId(int position,T t) {
        return mLayoutId;
    }

    protected abstract void onBindData(Context ctx,int position, T t, ViewHelper helper);

    /*public*/ static class ViewHolder extends RecyclerView.ViewHolder{

        public final ViewHelper mViewHelper;

        public ViewHolder(View itemView) {
            super(itemView);
            mViewHelper = new ViewHelper(itemView);
        }
        public Context getContext(){
            return mViewHelper.getContext();
        }
    }
}
