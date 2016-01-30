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
import android.view.View;
import android.view.ViewGroup;

import org.heaven7.core.viewhelper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstraction class of a BaseAdapter in which you only need to provide the
 * onBindData() implementation.<br/>
 * Using the provided BaseAdapterHelper, your code is minimalist.
 * 
 * @param <T>
 *            The type of the items in the list.
 * @since 1.8.0
 */
public abstract class QuickSwipeAdapter<T extends ISelectable> extends
		BaseQuickAdapter<T, BaseSwipeAdapterHelper> {

	private final SimpleSwipeStateChangeListener mSwipeListener = new SimpleSwipeStateChangeListener();
	private int mMenuLayoutId;

	/**
	 * Same as QuickAdapter#QuickAdapter(Context,int) but with some
	 * initialization data.
	 * @param layoutResId
	 *            The layout resource id of each item.
	 * @param data
	 *            A new list is created out of this one to avoid mutable list
	 */
	public QuickSwipeAdapter(int layoutResId,int menuLayoutId, List<T> data) {
		this(layoutResId, menuLayoutId  ,data, ISelectable.SELECT_MODE_SINGLE);
	}
	public QuickSwipeAdapter(int layoutResId,int menuLayoutId, List<T> data, int selectMode) {
		super(layoutResId, data, selectMode);
		this.mMenuLayoutId = menuLayoutId;
	}

	/**
	 * default select mode is {@link ISelectable#SELECT_MODE_SINGLE}
	 * @param data the data to populate
	 * @param multiItemSupport the multi itemsupport
	 */
	public QuickSwipeAdapter(ArrayList<T> data,
							 ISwipeMultiItemTypeSupport<T> multiItemSupport) {
		this(data, multiItemSupport, ISelectable.SELECT_MODE_SINGLE);
	}

	/**
	 * @param data the data to populate
	 * @param multiItemSupport the multi itemsupport
	 * @param selectMode  the select mode ,{@link ISelectable#SELECT_MODE_SINGLE} or {@link ISelectable#SELECT_MODE_MULTI}
	 */
	public QuickSwipeAdapter(ArrayList<T> data,
							 ISwipeMultiItemTypeSupport<T> multiItemSupport, int selectMode) {
		super(data, multiItemSupport, selectMode);
	}

	@Override
	protected void bindDataImpl(ViewGroup parent, int position, T item, BaseSwipeAdapterHelper helper) {
		onBindData(parent.getContext(), position, item, helper.getLayoutId(),
				helper.getMenuLayoutId(), helper.getViewHelper());
	}


	@Override
	protected BaseSwipeAdapterHelper getAdapterHelper(int position,
			View convertView, ViewGroup parent) {

		BaseSwipeAdapterHelper tag = convertView == null? null : (BaseSwipeAdapterHelper) convertView.getTag();

		final T t = getAdapterManager().getItemAt(position);
		int mainLayoutId = this.layoutResId;
		int menuLayoutId = this.mMenuLayoutId;

		if(mMultiItemSupport!=null){
			ISwipeMultiItemTypeSupport<T> mts = (ISwipeMultiItemTypeSupport<T>) this.mMultiItemSupport;
			mainLayoutId = mts.getLayoutId(position, t);
			menuLayoutId = mts.getMenuLayoutId(position, mainLayoutId, t);
		}

		if(tag == null || !isSameItem(mainLayoutId, menuLayoutId, tag.getLayoutId(),tag.getMenuLayoutId())){
			return new BaseSwipeAdapterHelper(parent,mainLayoutId,menuLayoutId,
					getTrackingEdge(), position ,mSwipeListener);
		}else{
			tag.position = position;
			return tag;
		}
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

	/**
	 * indicate is the same item. by the mainLayoutIds and menuLayoutIds.
	 * @param mainLayoutId the mainLayoutId of current
	 * @param menuLayoutId the menuLayoutId of current
	 * @param preMainLayoutId the mainLayoutId of previous
	 * @param preMenuLayoutId the menuLayoutId of previous
	 * @return true  if is the same item.
	 */
	protected boolean isSameItem(int mainLayoutId,int menuLayoutId, int preMainLayoutId,int preMenuLayoutId){
		return  mainLayoutId == preMainLayoutId && menuLayoutId == preMenuLayoutId;
	}

	/**
	 *
	 * Implement this method and use the helper to adapt the view to the given
	 * item.
	 * @param context the context
	 * @param position the postion of adapterview. such as: listView
	 * @param helper the helper of view help you to fast set view's property
	 * @param itemLayoutId  the layout id of item, this is useful if you use muity item.
	 *                      {@link MultiItemTypeSupport}
	 * @param menuLayoutId the layout id of swipe menu.
	 * @param item the data
	 */
	protected abstract void onBindData(Context context, int position,  T item,
							  int itemLayoutId, int menuLayoutId, ViewHelper helper);

	@Override
	protected void onBindData(Context context, int position, T item, int itemLayoutId, ViewHelper helper) {
		// there is no need to implement this method.
	}
}
