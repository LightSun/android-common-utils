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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

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
 */
/*public*/ abstract class BaseQuickAdapter<T extends ISelectable, H extends BaseAdapterHelper>
		extends BaseAdapter implements AdapterManager.IAdapterManagerCallback{

	protected int layoutResId;
	protected boolean displayIndeterminateProgress = false;

	private final AdapterManager<T> mAdapterManager ;


	/**
	 * Same as QuickAdapter#QuickAdapter(Context,int) but with some
	 * initialization data.
	 * 
	 * @param layoutResId
	 *            The layout resource id of each item.
	 * @param data
	 *            A new list is created out of this one to avoid mutable list
	 */
	public BaseQuickAdapter(int layoutResId, List<T> data,int selectMode) {
		if(layoutResId <0 ){
			throw new IllegalArgumentException("layoutId can't be negative ");
		}
		this.layoutResId = layoutResId;
		mAdapterManager =  createAdapterManager(data,selectMode);
		onFinalInit();
	}

	private AdapterManager<T> createAdapterManager(final List<T> data, int selectMode) {
		return  new AdapterManager<T>(data,selectMode) {
			@Override
			protected void notifyDataSetChangedImpl() {
                BaseQuickAdapter.this.notifyDataSetChanged();
			}

			@Override
			protected boolean isRecyclable() {
				return false;
			}

			@Override
			protected void beforeNotifyDataChanged() {
				BaseQuickAdapter.this.beforeNotifyDataChanged();
			}

			@Override
			protected void afterNotifyDataChanged() {
				BaseQuickAdapter.this.afterNotifyDataChanged();
			}
		};
	}

	protected MultiItemTypeSupport<T> mMultiItemSupport;

	public BaseQuickAdapter(ArrayList<T> data,
			MultiItemTypeSupport<T> multiItemSupport,int selectMode) {
		this.mMultiItemSupport = multiItemSupport;
		mAdapterManager =  createAdapterManager(data, selectMode);
		onFinalInit();
	}

	@Override
	public AdapterManager<T> getAdapterManager(){
		return mAdapterManager;
	}

	@Override
	public int getCount() {
		int extra = displayIndeterminateProgress ? 1 : 0;
		return mAdapterManager.getItemSize() + extra;
	}

	@Override
	public T getItem(int position) {
		if (position >= mAdapterManager.getItemSize())
			return null;
		return mAdapterManager.getItems().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		if (mMultiItemSupport != null)
			return mMultiItemSupport.getViewTypeCount() + 1;
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (displayIndeterminateProgress) {
			if (mMultiItemSupport != null)
				return position >= mAdapterManager.getItemSize() ? 0 : mMultiItemSupport
						.getItemViewType(position, mAdapterManager.getItemAt(position));
		} else {
			if (mMultiItemSupport != null)
				return mMultiItemSupport.getItemViewType(position,
						mAdapterManager.getItems().get(position));
		}
		// if no data. return 0
		return position >= mAdapterManager.getItemSize() ? 0 : 1;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (getItemViewType(position) == 0) {
			return createIndeterminateProgressView(convertView, parent);
		}
		final H helper = getAdapterHelper(position, convertView, parent);
		T item = getItem(position);
		onBindData(parent.getContext(), position, helper.getViewHelper(), helper.getLayoutId(), item);
		return helper.getViewHelper().getRootView();

	}

	protected static View createIndeterminateProgressView(View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			Context context = parent.getContext();
			FrameLayout container = new FrameLayout(context);
			container.setForegroundGravity(Gravity.CENTER);
			ProgressBar progress = new ProgressBar(context);
			container.addView(progress);
			convertView = container;
		}
		return convertView;
	}

	@Override
	public boolean isEnabled(int position) {
		return position < mAdapterManager.getItemSize();
	}

	public void showIndeterminateProgress(boolean display) {
		if (display == displayIndeterminateProgress)
			return;
		displayIndeterminateProgress = display;
		notifyDataSetChanged();
	}


	/**
	 * You can override this method to use a custom BaseAdapterHelper in order
	 * to fit your needs
	 * 
	 * @param position
	 *            The position of the item within the adapter's data set of the
	 *            item whose view we want.
	 * @param convertView
	 *            The old view to reuse, if possible. Note: You should check
	 *            that this view is non-null and of an appropriate type before
	 *            using. If it is not possible to onBindData this view to display
	 *            the correct data, this method can create a new view.
	 *            Heterogeneous lists can specify their number of view types, so
	 *            that this View is always of the right type (see
	 *            {@link #getViewTypeCount()} and {@link #getItemViewType(int)}
	 *            ).
	 * @param parent
	 *            The parent that this view will eventually be attached to
	 * @return An instance of BaseAdapterHelper
	 */
	protected abstract H getAdapterHelper(int position, View convertView,
			ViewGroup parent);

	/**
	 *
	 * Implement this method and use the helper to adapt the view to the given
	 * item.
	 * @param context
	 * @param position the postion of adapterview. such as: listView
	 * @param helper the helper of view help you to fast set view's property
     * @param itemLayoutId  the layout id of item, this is useful if you use muity item.
     *                      {@link MultiItemTypeSupport}
	 * @param item the data
	 */
	protected abstract void onBindData(Context context, int position, ViewHelper helper,
									   int itemLayoutId, T item);

	/** this is callled before data changed */
	protected void beforeNotifyDataChanged(){

	}
	/** this is callled after data changed */
	protected void afterNotifyDataChanged(){

	}
	/** the init operation of the last, called in constructor */
	protected void onFinalInit() {

	}

}
