package org.heaven7.core.adapter;

/**
 * for base adapter support swipe. this is a expand class of {@link MultiItemTypeSupport}
 * Created by heaven7 on 2016/1/29.
 * @since 1.8.0
 */
public interface ISwipeMultiItemTypeSupport<T>  extends MultiItemTypeSupport<T>{

    /**
     * get the menu layout id o
     * @param position the position
     * @param mainLayoutId the layout id of main  item
     * @param t  the T
     */
    int getMenuLayoutId(int position, int mainLayoutId, T t);
}
