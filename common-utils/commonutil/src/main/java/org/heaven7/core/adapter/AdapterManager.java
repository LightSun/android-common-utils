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

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by heaven7 on 2015/11/29.
 */
public abstract class AdapterManager<T extends ISelectable> {

    private List<T> mDatas;
    private final SelectHelper<T> mSelectHelper;

    /**
     * @param selectMode  see {@link ISelectable#SELECT_MODE_MULTI} or {@link ISelectable#SELECT_MODE_MULTI}
     */
    public AdapterManager(List<T> data,int selectMode) {
        this.mDatas = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
        mSelectHelper = createSelectHelper(selectMode, data);
    }

    private SelectHelper<T> createSelectHelper(int selectMode,List<T> list){
        SelectHelper<T> selectHelper = new SelectHelper<T>(selectMode) {
            @Override
            protected void notifyAllChanged() {
                notifyDataSetChanged();
            }
            @Override
            protected void notifyItemChanged(int itemPosition) {
                AdapterManager.this.notifyItemChanged(itemPosition);
            }
            @Override
            protected T getSelectedItemAtPosition(int position) {
                return getItemAt(position); //in recyclerView position is handled
            }

            @Override
            protected boolean isRecyclable() {
                return AdapterManager.this.isRecyclable();
            }
        };
        selectHelper.initSelectPositions(list);
        return selectHelper;
    }
    protected int getHeaderSize(){
        return  getHeaderFooterManager()!=null ? getHeaderFooterManager().getHeaderSize() :0;
    }

    public void addItem(T item){
        mDatas.add(item);
        if( isRecyclable()){
            notifyItemInserted(mDatas.size() - 1 + getHeaderSize());
        } else {
            notifyDataSetChanged();
        }
    }

    public void addItems(T...items){
        if( items == null || items.length ==0)
            return;
        addItems(Arrays.asList(items));
    }
    public void addItems(Collection<T> items){
        final int preSize = mDatas.size();
        mDatas.addAll(items);
        if(isRecyclable()){
            notifyItemRangeInserted(preSize + getHeaderSize(), items.size());
        } else {
            notifyDataSetChanged();
        }
    }

    public void setItem(T oldItem, T newItem){
        setItem(mDatas.indexOf(oldItem), newItem);
    }

    public void setItem(int index, T newItem) {
        mDatas.set(index, newItem);
        if(isRecyclable()){
            notifyItemChanged(index + getHeaderSize());
        } else {
            notifyDataSetChanged();
        }
    }

    public void removeItem(T item){
       removeItem(mDatas.indexOf(item));
    }

    public void removeItem(int index){
        mDatas.remove(index);
        if(isRecyclable()){
            notifyItemRemoved(index + getHeaderSize());
        }else {
            notifyDataSetChanged();
        }
    }
    public void removeItems(List<T> ts){
        if(ts == null || ts.size() == 0)
            return;
        List<T> mDatas = this.mDatas;
        for(int i=0,size = ts.size() ;i<size ; i++){
            mDatas.remove(ts.get(i));
        }
        notifyDataSetChanged();
    }
    public void removeItemsByPosition(List<Integer> positions){
        if(positions == null || positions.size()==0)
            return;
        List<T> mDatas = this.mDatas;
        int pos ;
        for(int i=0,size = positions.size() ;i<size ; i++){
            pos = positions.get(i);
            mDatas.remove(pos);
        }
        notifyDataSetChanged();
    }

    public void replaceAllItems(List<T> items) {
        mDatas.clear();
        mDatas.addAll(items);
        mSelectHelper.initSelectPositions(items);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    public List<T> getItems(){
        return mDatas;
    }
    public boolean containsItem(T item){
        return mDatas.contains(item);
    }

    public final void notifyDataSetChanged(){
        beforeNotifyDataChanged();
        notifyDataSetChangedImpl();
        afterNotifyDataChanged();
    }

    // =========== begin recycleview ==============//

    private void checkIfSupport() {
        if(!isRecyclable()){
            throw new UnsupportedOperationException("only recycle view support");
        }
    }

    public final void notifyItemInserted(int position) {
        checkIfSupport();
        position += getHeaderSize();
        notifyItemInsertedImpl(position);
    }
    public final void notifyItemChanged(int position) {
        checkIfSupport();
        position += getHeaderSize();
        notifyItemChangedImpl(position);
    }
    public final void notifyItemRemoved(int position) {
        checkIfSupport();
        position += getHeaderSize();
        notifyItemRemovedImpl(position);
    }

    public final void notifyItemMoved(int fromPosition, int toPosition){
        checkIfSupport();
        fromPosition += getHeaderSize();
        toPosition += getHeaderSize();
        notifyItemMovedImpl(fromPosition, toPosition);
    }

    public final void notifyItemRangeChanged(int positionStart, int itemCount) {
        checkIfSupport();
        positionStart += getHeaderSize();
        notifyItemRangeChangedImpl(positionStart, itemCount);
    }

    public final void notifyItemRangeInserted(int positionStart, int itemCount){
        checkIfSupport();
        positionStart += getHeaderSize();
        notifyItemRangeInsertedImpl(positionStart, itemCount);
    }

    public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
        checkIfSupport();
        positionStart += getHeaderSize();
        notifyItemRangeRemovedImpl(positionStart, itemCount);
    }

    //================== ========================//

    protected void notifyItemInsertedImpl(int position) {}

    protected void notifyItemChangedImpl(int position) {}

    protected void notifyItemRemovedImpl(int position) {}

    protected void notifyItemMovedImpl(int fromPosition, int toPosition){}

    protected void notifyItemRangeChangedImpl(int positionStart, int itemCount){}

    protected void notifyItemRangeInsertedImpl(int positionStart, int itemCount){}

    protected void notifyItemRangeRemovedImpl(int positionStart, int itemCount){}

    // =========== end recycleview ==============//

    protected abstract void notifyDataSetChangedImpl();

    protected abstract boolean isRecyclable();

    /** this called before {@link #notifyDataSetChangedImpl()} */
    protected abstract void beforeNotifyDataChanged();
    /** this called after {@link #notifyDataSetChangedImpl()} */
    protected abstract void afterNotifyDataChanged();

    //================== ========================//

    public IHeaderFooterManager getHeaderFooterManager(){
        throw new UnsupportedOperationException();
    }

    public int getItemSize() {
        return mDatas.size();
    }
    public T getItemAt(int index){
        return mDatas.get(index);
    }

    public SelectHelper<T> getSelectHelper(){
        return mSelectHelper;
    }

    /**
     * ths supporter of header and footer, call any method will automatic call {@link QuickRecycleViewAdapter#notifyDataSetChanged()}.
     */
    public interface IHeaderFooterManager{

        void addHeaderView(View v);
        void removeHeaderView(View v);
        void addFooterView(View v);
        void removeFooterView(View v);
        int getHeaderSize();
        int getFooterSize();
    }

    public interface IAdapterManagerCallback<T extends ISelectable>{

        AdapterManager<T> getAdapterManager();
    }

}
