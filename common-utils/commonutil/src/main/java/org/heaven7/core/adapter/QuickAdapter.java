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

	private SelectHelper<T> mSelectHelper;

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
		init(selectMode, data);
	}

	private void init(int selectMode,List<T> list){
		mSelectHelper = new SelectHelper<T>(selectMode) {
			@Override
			protected void notifyAllChanged() {
				notifyDataSetChanged();
			}

			@Override
			protected void notifyItemChanged(int itemPosition) {
				throw new UnsupportedOperationException();
			}

			@Override
			protected T getSelectedItemAtPosition(int position) {
				return getItem(position);
			}
		};
		mSelectHelper.initSelectPositions(list);
	}

	/**
	 * default select mode is {@link ISelectable#SELECT_MODE_SINGLE}
	 * @param data the data to populate
	 * @param multiItemSupport the multi itemsupport
	 */
	public QuickAdapter(ArrayList<T> data,
			MultiItemTypeSupport<T> multiItemSupport) {
		this(data, multiItemSupport, ISelectable.SELECT_MODE_SINGLE);
	}

	/**
	 * @param data the data to populate
	 * @param multiItemSupport the multi itemsupport
	 * @param selectMode  the select mode ,{@link ISelectable#SELECT_MODE_SINGLE} or {@link ISelectable#SELECT_MODE_MULTI}
	 */
	public QuickAdapter(ArrayList<T> data,
			MultiItemTypeSupport<T> multiItemSupport,int selectMode) {
		super(data, multiItemSupport);
		init(selectMode, data);
	}

	public SelectHelper<T> getSelectHelper(){
		return mSelectHelper;
	}

	/**
	 * select the target position with notify data.if currentPosition  == position.ignore it.
	 * <li></>only support select mode = {@link ISelectable#SELECT_MODE_SINGLE} ,this will auto update**/
	public void setSelected(int position){
		mSelectHelper.setSelected(position);
	}

	/** only support select mode = {@link ISelectable#SELECT_MODE_MULTI}**/
	public void addSelected(int selectPosition){
		mSelectHelper.addSelected(selectPosition);
	}

	public void clearAllSelected(){
		mSelectHelper.clearAllSelected();
	}

	public T getSelectedData(){
		return mSelectHelper.getSelectedItem();
	}

	public int getSelectedPosition(){
		return mSelectHelper.getSelectedPosition() ;
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

	@Override
	public void replaceAll(List<T> elem) {
		if(elem == null)
			throw new NullPointerException();
		mSelectHelper.initSelectPositions(elem);
		super.replaceAll(elem);
	}
}
