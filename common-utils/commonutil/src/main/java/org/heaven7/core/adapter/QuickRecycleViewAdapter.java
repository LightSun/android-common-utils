package org.heaven7.core.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.heaven7.core.viewhelper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heaven7 on 2015/8/26.
 * @param <T>  the data
 */
public abstract class QuickRecycleViewAdapter<T extends ISelectable>
        extends RecyclerView.Adapter<QuickRecycleViewAdapter.ViewHolder> {

    private List<T> mDatas ;
    private int mLayoutId = 0;
    private SelectHelper<T> mSelectHelper;

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
        this.mDatas = mDatas == null ? new ArrayList<T>() : new ArrayList<>(mDatas);
        init(selectMode, mDatas);
    }

    private void init(int selectMode,List<T> list){
        mSelectHelper = new SelectHelper<T>(selectMode) {
            @Override
            protected boolean isRecyclable() {
                return true;
            }

            @Override
            protected void notifyAllChanged() {
                notifyDataSetChanged();
            }

            @Override
            protected void notifyItemChanged(int itemPosition) {
                QuickRecycleViewAdapter.this.notifyItemChanged(itemPosition);
            }

            @Override
            protected T getSelectedItemAtPosition(int position) {
                return getItem(position);
            }
        };
        mSelectHelper.initSelectPositions(list);
    }

    public SelectHelper<T> getSelectHelper(){
        return mSelectHelper;
    }

    public final T getItem(int position){
        return mDatas!=null?mDatas.get(position):null;
    }
    /** only support select mode = {@link ISelectable#SELECT_MODE_MULTI}**/
    public void addSelected(int selectPosition){
        mSelectHelper.addSelected(selectPosition);
    }
    public void clearAllSelected(){
        mSelectHelper.clearAllSelected();
    }
    /**
     * select the target position with notify data.if currentPosition  == position.ignore it.
     * <li></>only support select mode = {@link ISelectable#SELECT_MODE_SINGLE} ,this will auto update**/
    public void setSelected(int position){
        mSelectHelper.setSelected(position);
    }

    public T getSelectedData(){
       return mSelectHelper.getSelectedItem();
    }

    public int getSelectedPosition(){
        return mSelectHelper.getSelectedPosition() ;
    }

    public void addItems(List<T> items){
        List<T> mDatas = this.mDatas;
        final int preSize = mDatas.size();
        final int size = items.size();
        for(int i=0 ; i<size ;i++){
            mDatas.add(items.get(i));
        }
        notifyItemRangeInserted(preSize,size);
    }
    public void addItems(T...items){
        List<T> mDatas = this.mDatas;
        final int preSize = mDatas.size();
        final int size = items.length;
        for(int i=0 ; i<size ;i++){
            mDatas.add(items[i]);
        }
        notifyItemRangeInserted(preSize, size);
    }

    public void removeItem(T t){
        int index =  mDatas.indexOf(t);
        if(index == -1) return ;
        mDatas.remove(index);
        notifyItemRemoved(index);
    }

    public void setItem(int position ,T t){
        List<T> mDatas = this.mDatas;
        mDatas.set(position, t);
        notifyItemChanged(position);
    }
    /** change item from startPosition. */
    public void setItems(int startPosition ,List<T> newItems){
        if(newItems ==null || newItems.size() ==0){
            return ;
        }
        List<T> mDatas = this.mDatas;
        if(startPosition > mDatas.size()){
            throw new IllegalArgumentException("startPosition must <=  List.size()");
        }
        final int size = newItems.size();
        for(int i=0; i<size ; i++){
            mDatas.set(startPosition + i, newItems.get(i));
        }
        notifyItemRangeChanged(startPosition, size);
    }

    public void removeItems(int startIndex,int count){
        if(count <=0) return ;
        List<T> mDatas = this.mDatas;
        for(int i=0  ; i< count ;i++){
            mDatas.remove(startIndex);
        }
        notifyItemRangeRemoved(startIndex, count);
    }

    public void resetItems(List<T> items){
        if(items!=null && items.size() > 0) {
            this.mDatas = new ArrayList<>(items);
            notifyDataSetChanged();
        }
    }

    @Override
    public final int getItemViewType(int position) {
        return getItemLayoutId(position, getItem(position));
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(viewType,parent,false);
        return new ViewHolder(root);
    }

    @Override
    public final void onBindViewHolder(ViewHolder holder, int position) {
        onBindData( holder.getContext() ,position, getItem(position), holder.mViewHelper);
    }

    @Override
    public final int getItemCount() {
        return mDatas.size();
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
