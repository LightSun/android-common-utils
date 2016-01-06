package org.heaven7.core.item.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import java.util.ArrayList;

/**
 * ItemDecoration内的方法，每个item都会调用
 * <li>RecyclerView.addItemDecoration must call after RecyclerView.setLayoutManager</li>
 * <li>ps: 主要适用于GridLayoutManager,不适用于不均匀的 StaggeredGridLayoutManager </li>
 */
public class DividerGridItemDecoration extends AbstractDividerItemDecoration {

   // private static final String TAG = "DividerGridItemDecoration";

    private  ArrayList<Integer> mHorizontalExcludePositions ;
    private  ArrayList<Integer> mVerticalExcludePositions ;

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    public DividerGridItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        Drawable mDivider = a.getDrawable(0);
        a.recycle();
        getDividerManager().setDivider(mDivider);
    }

    //called before recyclerView's onDraw
    @Override
    public void onDraw(Canvas c, RecyclerView parent, State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    /**
     * get the column count or the raw count (only layout is HORIZONTAL is raw count)
     */
    private int getSpanCount(RecyclerView parent) {
        // 列数 或者 行数
        int spanCount = -1;
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        final ArrayList<Integer> mHorizontalExcludePositions = this.mHorizontalExcludePositions;
        final int w = this.getDividerManager().getDividerWidth() ;
        final int h = this.getDividerManager().getDividerHeight() ;

        final Drawable mDivider = this.getDividerManager().getDivider();

        for (int i = 0; i < childCount; i++) {
            if( mHorizontalExcludePositions.contains(i) ){
                continue ;
            }
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin + w;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + h;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        final ArrayList<Integer> mVerticalExcludePositions = this.mVerticalExcludePositions;
        final int w = this.getDividerManager().getDividerWidth() ;
        final Drawable mDivider = this.getDividerManager().getDivider();

        for (int i = 0; i < childCount; i++) {
            if( mVerticalExcludePositions.contains(i) ){
                continue ;
            }
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + w;

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private boolean isVertical( RecyclerView parent){
        final LayoutManager manager = parent.getLayoutManager();
        if(manager instanceof GridLayoutManager){
             return ((GridLayoutManager) manager).getOrientation() == GridLayoutManager.VERTICAL;
        }else if(manager instanceof StaggeredGridLayoutManager){
            return ((StaggeredGridLayoutManager) manager).getOrientation()
                    == StaggeredGridLayoutManager.VERTICAL;
        }else{
            return false;
        }
    }
    private boolean isHorizontal( RecyclerView parent){
        final LayoutManager manager = parent.getLayoutManager();
        if(manager instanceof GridLayoutManager){
             return ((GridLayoutManager) manager).getOrientation() == GridLayoutManager.HORIZONTAL;
        }else if(manager instanceof StaggeredGridLayoutManager){
            return ((StaggeredGridLayoutManager) manager).getOrientation()
                    == StaggeredGridLayoutManager.HORIZONTAL;
        }else{
            return false;
        }
    }

    @Override
    public final void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        super.getItemOffsets(outRect, itemPosition, parent);
    }

    //每个item都会调用
    @Override // 获取装饰品( 这里是分割线 )的边距信息 到outRect,类似padding and margin
    public final void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        // itemPosition from 0
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();

       // outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0); //  -> similar to width padding

        boolean called = false ;
        boolean drawBottom = false ;
        boolean drawRight = false ;

        if(isVertical(parent)){
            drawRight =  !isLastColumn(itemPosition, spanCount, childCount, false);
            drawBottom = !isLastRaw(itemPosition, spanCount, childCount,false);
            called = true;
        }else if(isHorizontal(parent)){
            // 整除or not
            drawRight =  ! isLastColumn(itemPosition, spanCount, childCount, true);
            drawBottom = ! isLastRaw(itemPosition, spanCount, childCount,true);
            called = true;
        }else{
            getItemOffsets(outRect,view,parent,state,itemPosition,childCount);
        }
        if(called) {
            if (!drawRight) {
                if(mVerticalExcludePositions == null){
                    mVerticalExcludePositions  = new ArrayList<>(5);
                }
                mVerticalExcludePositions.add(itemPosition);
            }
            if (!drawBottom) {
                if(mHorizontalExcludePositions == null){
                    mHorizontalExcludePositions = new ArrayList<>(5);
                }
                mHorizontalExcludePositions.add(itemPosition);
            }
            outRect.set(0, 0,
                    drawRight ? getDividerManager().getDividerWidth()  : 0 ,
                    drawBottom ? getDividerManager().getDividerHeight()  : 0
            );
        }
    }

    /***
     * 是否最后一行
     * @param horizontal  the oritension
     */
    protected boolean isLastRaw(int itemPosition, int spanCount, int childCount,
                              boolean horizontal) {
        if(horizontal){
             return ( itemPosition + 1)  % spanCount == 0;
        }else{
            //竖直布局
           int residue = childCount % spanCount; //余数
           if(residue == 0){ //8 , 2  -> 6
               return  itemPosition >= (childCount/spanCount - 1) * spanCount;
           }else{
               // 7, 5  -> 3
               // 7, 2  -> 7 -1
               return itemPosition >= childCount / spanCount * spanCount;
           }
        }
    }

    // horizontal 是否水平布局
    protected boolean isLastColumn(int itemPosition, int spanCount, int childCount,
                                 boolean horizontal) {
        if(horizontal){
            int residue = childCount % spanCount; //余数
            if(residue == 0){ //8 , 2  -> 6
                return  itemPosition >= (childCount/spanCount - 1) * spanCount;
            }else{
                // 7, 5  -> 3
                // 7, 2  -> 7 -1
                return itemPosition >= childCount / spanCount * spanCount;
            }
        }else{
            return ( itemPosition + 1)  % spanCount == 0;
        }
    }

    /** when you use self-layoutmanager to RecyclerView. and want divide,you may want to override this method  */
    protected void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state,
                                int itemPosition, int childCount) {
           throw new UnsupportedOperationException("unsupport layoutmanager: " +
                   parent.getLayoutManager().getClass().getName());
    }
}
