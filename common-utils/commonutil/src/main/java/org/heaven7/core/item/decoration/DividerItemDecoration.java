package org.heaven7.core.item.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * This class is from the v7 samples of the Android SDK. It's not by me!
 * 只适用于 RecyclerView 布局管理为 LinearLayoutManager的
 * <p/>
 * See the license above for details.
 */
public class DividerItemDecoration extends AbstractDividerItemDecoration {

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST   = LinearLayoutManager.VERTICAL;

    private int mOrientation;

    public DividerItemDecoration(Context context, int orientation) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
         Drawable mDivider = a.getDrawable(0);
        a.recycle();
        getDividerManager().setDivider(mDivider);
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
       // super.onDraw(c, parent, state);
      //  Logger.v("recyclerview - itemdecoration", "onDraw()");
        if( ! ( parent.getLayoutManager() instanceof LinearLayoutManager)){
            throw new IllegalStateException("only support LinearLayoutManager");
        }

        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getLeft();
        final int right = parent.getRight();

        final Drawable mDivider = this.getDividerManager().getDivider();
        final int childCount = parent.getChildCount();
        View child;
        RecyclerView.LayoutParams params;
        int top ;
        int bottom ;

        for (int i = 0; i < childCount; i++) {
            child = parent.getChildAt(i);
            params = (RecyclerView.LayoutParams) child.getLayoutParams();
            top = child.getBottom() + params.bottomMargin; //分割线的top
            bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getTop();
        final int bottom = parent.getBottom();

        final Drawable mDivider = this.getDividerManager().getDivider();
        final int childCount = parent.getChildCount();
        View child ;
        RecyclerView.LayoutParams params ;
        int left, right;

        for (int i = 0; i < childCount; i++) {
            child = parent.getChildAt(i);
            params = (RecyclerView.LayoutParams) child.getLayoutParams();
            //分割线的left,right
            left = child.getRight() + params.rightMargin;
            right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override //设置绘制的范围 similar to padding and margin
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, getDividerManager().getDividerHeight());
        } else {
            outRect.set(0, 0, getDividerManager().getDividerWidth(), 0);
        }
    }
}
