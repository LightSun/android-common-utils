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
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstraction class of a BaseAdapter in which you only need to provide the
 * onBindData() implementation.<br/>
 * Using the provided BaseAdapterHelper, your code is minimalist.
 * 
 * @param <T>
 *            The type of the items in the list.
 */
public abstract class QuickAdapter<T extends ISelectable> extends
		BaseQuickAdapter<T, BaseAdapterHelper> {

	private int mSelectMode;
	private List<Integer> mSelectedPositions;
	private int mSelectedPosition = -1;
	private boolean mInited;

	/**
	 * Same as QuickAdapter#QuickAdapter(Context,int) but with some
	 * initialization data.
	 * 
	 * @param layoutResId
	 *            The layout resource id of each item.
	 * @param data
	 *            A new list is created out of this one to avoid mutable list
	 */
	public QuickAdapter(int layoutResId, List<T> data) {
		this(layoutResId, data, ISelectable.SELECT_MODE_SINGLE);
	}
	public QuickAdapter(int layoutResId, List<T> data,int selectMode) {
		super(layoutResId, data);
		if(selectMode == ISelectable.SELECT_MODE_MULTI)
			this.mSelectedPositions = new ArrayList<>();
		if(selectMode!= ISelectable.SELECT_MODE_SINGLE && selectMode != ISelectable.SELECT_MODE_MULTI){
			throw new IllegalArgumentException("invalid select mode = " +selectMode);
		}
		this.mSelectMode = selectMode;
	}

	public QuickAdapter(ArrayList<T> data,
			MultiItemTypeSupport<T> multiItemSupport) {
		super(data, multiItemSupport);
	}

	/**
	 * select the target position with notify data.if currentPosition  == position.ignore it.
	 * <li></>only support select mode = {@link ISelectable#SELECT_MODE_SINGLE} ,this will auto update**/
	public void setSelected(int position){
		if(mSelectMode == ISelectable.SELECT_MODE_MULTI)
			return ;
		if(mSelectedPosition == position){
			return ;
		}
		if(position < 0)
			throw new IllegalArgumentException();
		if(mSelectedPosition!= ISelectable.INVALID_POSITION){
			getItem(mSelectedPosition).setSelected(false);
		}
		mSelectedPosition = position;
		getItem(position).setSelected(true);
		notifyDataSetChanged();
	}

	/** only support select mode = {@link ISelectable#SELECT_MODE_MULTI}**/
	public void addSelected(int selectPosition){
		if(mSelectMode == ISelectable.SELECT_MODE_SINGLE)
			return ;
		if(mSelectedPositions ==null)
			throw new IllegalStateException("select mode must be multi");
		mSelectedPositions.add(selectPosition);
		getItem(selectPosition).setSelected(true);
		notifyDataSetChanged();
	}

	public void clearAllSelected(){
		if(mSelectMode == ISelectable.SELECT_MODE_MULTI) {
			int pos;
			for (int i = 0, size = mSelectedPositions.size(); i < size; i++) {
				pos = mSelectedPositions.get(i);
				getItem(pos).setSelected(false);
			}
			mSelectedPositions.clear();
			notifyDataSetChanged();
		}else{
			if(mSelectedPosition!= ISelectable.INVALID_POSITION){
				getItem(mSelectedPosition).setSelected(false);
				notifyDataSetChanged();
			}
		}
	}

	public T getSelectedData(){
		if(mSelectedPosition == ISelectable.INVALID_POSITION)
			return null;
		return getItem(mSelectedPosition);
	}

	public int getSelectedPosition(){
		return mSelectedPosition ;
	}

	@Override
	protected BaseAdapterHelper getAdapterHelper(int position,
			View convertView, ViewGroup parent) {

		if (mMultiItemSupport != null) {
			return BaseAdapterHelper.get(
					convertView,
					parent,
					mMultiItemSupport.getLayoutId(position, data.get(position)),
					position);
		} else {
			return BaseAdapterHelper.get(convertView, parent, layoutResId, position);
		}
	}

}
