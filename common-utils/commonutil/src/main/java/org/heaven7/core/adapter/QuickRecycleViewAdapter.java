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
    private HeaderFooterManager mHeaderFooterManager;

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
                if(mHeaderFooterManager!=null)
                    itemPosition += mHeaderFooterManager.getHeaderViewSize();
                QuickRecycleViewAdapter.this.notifyItemChanged(itemPosition);
            }

            @Override
            protected T getSelectedItemAtPosition(int position) {
                return getItem(position);
            }
        };
        mSelectHelper.initSelectPositions(list);
    }

    //=================== start header footer view ======================= //
    public void addHeaderView(View v){
        if(mHeaderFooterManager == null)
            mHeaderFooterManager = new HeaderFooterManager();
        int headerSize = getHeaderSize();
        mHeaderFooterManager.addHeaderView(v);
        notifyItemInserted(headerSize);
    }
    public void removeHeaderView(View v){
        if(mHeaderFooterManager!=null){
            int index = mHeaderFooterManager.removeHeaderView(v);
            if(index != -1){
                notifyItemRemoved(index);
            }
        }
    }
    public void addFooterView(View v){
        if(mHeaderFooterManager==null)
            mHeaderFooterManager = new HeaderFooterManager();
        int itemCount = getItemCount();
        mHeaderFooterManager.addFooterView(v);
        notifyItemInserted(itemCount);
    }
    public void removeFooterView(View v){
        if(mHeaderFooterManager!=null){
            int index = mHeaderFooterManager.removeFooterView(v);
            if(index != -1){
                notifyItemRemoved(index + getHeaderSize() + mDatas.size());
            }
        }
    }
    public int getHeaderSize() {
        return mHeaderFooterManager==null ? 0 : mHeaderFooterManager.getHeaderViewSize();
    }
    public int getFooterSize() {
        return mHeaderFooterManager==null ? 0 : mHeaderFooterManager.getFooterViewSize();
    }
    // =================== end header footer view ======================= //

    public SelectHelper<T> getSelectHelper(){
        return mSelectHelper;
    }

    public final T getItem(int position){
        return mDatas!=null?mDatas.get(position):null;
    }
    /**
     * select the target position
     * only support select mode = {@link ISelectable#SELECT_MODE_MULTI}**/
    public void addSelected(int selectPosition){
        mSelectHelper.addSelected(selectPosition);
    }

    /**  un select the target position  .
     * <li>only support select mode = {@link ISelectable#SELECT_MODE_MULTI}*/
    public void addUnselected(int position){
        mSelectHelper.addUnselected(position);
    }

    /**
     * un select the all selected position.
     * mode single or multi all supoorted */
    public void clearAllSelected(){
        mSelectHelper.clearAllSelected();
    }
    /**
     * select the target position with notify data.if currentPosition  == position.ignore it.
     * <li></>only support select mode = {@link ISelectable#SELECT_MODE_SINGLE} ,this will auto update**/
    public void setSelected(int position){
        mSelectHelper.setSelected(position);
    }
    /** un select the target position
     * <li>only support select mode = {@link ISelectable#SELECT_MODE_SINGLE} */
    public void setUnselected(int position){
        mSelectHelper.setUnselected(position);
    }

    /** clear selected positions  . this just clear record. bu not notify item change
     * <li> support select mode = {@link ISelectable#SELECT_MODE_SINGLE} or {@link ISelectable#SELECT_MODE_MULTI}*/
    public void clearSelectedPositions(){
        mSelectHelper.clearSelectedPositions();
    }

    public T getSelectedData(){
       return mSelectHelper.getSelectedItem();
    }

    public List<T> getSelectedItems(){
        return mSelectHelper.getSelectedItems();
    }

    public int getSelectedPosition(){
        return mSelectHelper.getSelectedPosition() ;
    }

    //=================== begin items ==========================//

    public void addItems(List<T> items){
        List<T> mDatas = this.mDatas;
        final int preSize = mDatas.size();
        final int size = items.size();
        for(int i=0 ; i<size ;i++){
            mDatas.add(items.get(i));
        }
        notifyItemRangeInserted(preSize + getHeaderSize() ,size);
    }

    public void addItems(T...items){
        List<T> mDatas = this.mDatas;
        final int preSize = mDatas.size();
        final int size = items.length;
        for(int i=0 ; i<size ;i++){
            mDatas.add(items[i]);
        }
        notifyItemRangeInserted(preSize + getHeaderSize(), size);
    }

    public void removeItem(T t){
        int index =  mDatas.indexOf(t);
        if(index == -1) return ;
        mDatas.remove(index);
        notifyItemRemoved(index + getHeaderSize());
    }
    public void removeItems(List<T> ts){
        if(ts==null || ts.size()==0)
            return;
        for(int i=0,size=ts.size() ;i<size ; i++){
            removeItem(ts.get(i));
        }
    }
    public void removeItemsByPosition(List<Integer> positions){
        if(positions==null || positions.size()==0)
            return;
        final int headerSize = getHeaderSize();
        List<T> mDatas = this.mDatas;
        int pos ;
        for(int i=0,size=positions.size() ;i<size ; i++){
            pos = positions.get(i).intValue();
            mDatas.remove(pos);
            notifyItemRemoved(pos + headerSize);
        }
    }

    public void setItem(int position ,T t){
        List<T> mDatas = this.mDatas;
        mDatas.set(position, t);
        notifyItemChanged(position + getHeaderSize());
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
        notifyItemRangeChanged(startPosition + getHeaderSize(), size);
    }

    public void removeItems(int startIndex,int count){
        if(count <=0) return ;
        List<T> mDatas = this.mDatas;
        for(int i=0  ; i< count ;i++){
            mDatas.remove(startIndex);
        }
        notifyItemRangeRemoved(startIndex + getHeaderSize(), count);
    }

    public void resetItems(List<T> items){
        if(items!=null && items.size() > 0) {
            this.mDatas = new ArrayList<>(items);
            notifyDataSetChanged();
        }
    }
    //====================== end items ========================//

    @Override
    public final int getItemViewType(int position) {
        if(mHeaderFooterManager!=null){
            //in header or footer
            if(mHeaderFooterManager.isInHeader(position) ||
                    mHeaderFooterManager.isInFooter(position,mDatas.size()))
                return position;

            position -= mHeaderFooterManager.getHeaderViewSize();
        }
        int layoutId = getItemLayoutId(position, getItem(position));
        if(mHeaderFooterManager != null)
            mHeaderFooterManager.recordLayoutId(layoutId);
        return layoutId;
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderFooterManager == null || mHeaderFooterManager.isLayoutIdInRecord(viewType)){
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType,parent,false));
        }else{
            return new ViewHolder(mHeaderFooterManager.findView(viewType,mDatas.size()));
        }
    }

    @Override
    public final void onBindViewHolder(ViewHolder holder, int position) {
        if(mHeaderFooterManager!=null) {
            if ( mHeaderFooterManager.isInHeader(position)
                    || mHeaderFooterManager.isInFooter(position,mDatas.size())){
                return ;
            }
            position -= mHeaderFooterManager.getHeaderViewSize();
        }
        //not in header or footer populate it
        onBindData(holder.getContext(), position, getItem(position), holder.mViewHelper);
    }

    @Override
    public final int getItemCount() {
        return mHeaderFooterManager == null ? mDatas.size() :
                mDatas.size() + mHeaderFooterManager.getHeaderViewSize() +
                        mHeaderFooterManager.getFooterViewSize();
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
