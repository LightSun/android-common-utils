package org.heaven7.core.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import org.heaven7.core.util.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * thanks to 小凡
 */
public class  BaseFragmentPagerAdapter  extends PagerAdapter {

	private static final String TAG = "FragmentStatePagerAdapter";
	private static final boolean DEBUG = false;

	private final FragmentManager mFragmentManager;
	private FragmentTransaction mCurTransaction = null;

	private SparseArray<Fragment.SavedState> mSavedState = new SparseArray<Fragment.SavedState>();
	private Map<FragmentData, ItemData> cache = new HashMap<FragmentData, ItemData>();

	public static class FragmentData {
		public String title;
		public Class<?> fragmentClass;
		public Bundle bundle;

		public FragmentData(String title, Class<?> fragmentClass, Bundle bundle) {
			super();
			this.title = title;
			this.fragmentClass = fragmentClass;
			this.bundle = bundle;
		}
	}

	private static class ItemData {
		FragmentData data;
		Fragment fragment;
		int position;

		public ItemData(FragmentData data, Fragment fragment, int position) {
			super();
			this.fragment = fragment;
			this.data = data;
			this.position = position;
		}
	}

	private List<FragmentData> fragmentDatas;

	private Fragment mCurrentPrimaryItem = null;

	public BaseFragmentPagerAdapter(FragmentManager fm, List<FragmentData> fragmentDatas) {
		mFragmentManager = fm;
		this.fragmentDatas = fragmentDatas;
	}

	@Override
	public final int getCount() {
		return fragmentDatas.size();
	}

	@Override
	public final int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void startUpdate(ViewGroup container) {
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// If we already have this item instantiated, there is nothing
		// to do. This can happen when we are restoring the entire pager
		// from its saved state, where the fragment manager has already
		// taken care of restoring the fragments we previously had instantiated.
		FragmentData data = fragmentDatas.get(position);
		ItemData item = cache.get(data);
		if (item != null) {
			item.position = position;
			if (item.fragment.getView().getParent() == null)
				container.addView(item.fragment.getView());
			return item;
		}

		Fragment fragment = Fragment.instantiate(container.getContext(),
				data.fragmentClass.getName(), data.bundle);
		item = new ItemData(data, fragment, position);
		cache.put(data, item);
		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}
		if (DEBUG)
			Logger.v(TAG, "Adding item #" + position + ": f=" + fragment);
		if (mSavedState.size() > position) {
			Fragment.SavedState fss = mSavedState.get(position);
			if (fss != null) {
				fragment.setInitialSavedState(fss);
			}
		}
		fragment.setMenuVisibility(false);
		fragment.setUserVisibleHint(false);
		mCurTransaction.add(container.getId(), fragment);
		return item;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		ItemData itemData = (ItemData) object;
		if (dispatchNeedDestroy(position, itemData.data, itemData.fragment)) {
			if (mCurTransaction == null) {
				mCurTransaction = mFragmentManager.beginTransaction();
			}
			if (DEBUG)
				Logger.v(TAG, "Removing item #" + position + ": f=" + object +
						" v=" + ((Fragment) object).getView());
			mSavedState.put(position, mFragmentManager.saveFragmentInstanceState(itemData.fragment));
			mCurTransaction.remove(itemData.fragment);
			cache.remove(itemData.data);
		} else {
			container.removeView(itemData.fragment.getView());
		}
	}

	protected boolean dispatchNeedDestroy(int position, FragmentData data, Fragment fragment) {
		return !fragmentDatas.contains(data) || needDestroy(position, data, fragment);
	}

	protected boolean needDestroy(int position, FragmentData data, Fragment fragment) {
		return false;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		ItemData itemData = (ItemData) object;
		if (itemData == null) {
			return;
		}
		Fragment fragment = itemData.fragment;
		if (fragment != mCurrentPrimaryItem) {
			if (mCurrentPrimaryItem != null) {
				mCurrentPrimaryItem.setMenuVisibility(false);
				mCurrentPrimaryItem.setUserVisibleHint(false);
			}
			if (fragment != null) {
				fragment.setMenuVisibility(true);
				fragment.setUserVisibleHint(true);
			}
			mCurrentPrimaryItem = fragment;
		}
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		if (mCurTransaction != null) {
			mCurTransaction.commitAllowingStateLoss();
			mCurTransaction = null;
			mFragmentManager.executePendingTransactions();
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return ((ItemData) object).fragment.getView() == view;
	}
}
