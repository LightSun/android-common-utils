package org.heaven7.core.layoutmanager;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * GridLayoutManager和ScrollView进行嵌套
 */
public class FullyGridLayoutManager extends GridLayoutManager {
    public FullyGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public FullyGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    private int[] mMeasuredDimension = new int[2];

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        final int widthMode = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);
        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);

        int width = 0;
        int height = 0;
        int count = getItemCount();
        int span = getSpanCount();
        for (int i = 0; i < count; i++) {
            measureScrapChild(recycler, i,
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    mMeasuredDimension);

            if (getOrientation() == HORIZONTAL) {
                if (i % span == 0) {
                    width = width + mMeasuredDimension[0];
                }
                if (i == 0) {
                    height = mMeasuredDimension[1];
                }
            } else {
                if (i % span == 0) {
                    height = height + mMeasuredDimension[1];
                }
                if (i == 0) {
                    width = mMeasuredDimension[0];
                }
            }
        }

        switch (widthMode) {
            case View.MeasureSpec.EXACTLY:
                width = widthSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }

        switch (heightMode) {
            case View.MeasureSpec.EXACTLY:
                height = heightSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }
        setMeasuredDimension(width, height);
    }

    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
                                   int heightSpec, int[] measuredDimension) {

        if (position < getItemCount()) {
            try {
                View view = recycler.getViewForPosition(position); //fix 动态添加时报IndexOutOfBoundsException
                if (view.getVisibility() == View.GONE) {
                    measuredDimension[0] = 0;
                    measuredDimension[1] = 0;
                    return;
                }
                RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
                // For adding Item Decor Insets to view
                super.measureChildWithMargins(view, 0, 0);
                  /*  全部 0
                  final int l = getLeftDecorationWidth(view);
                    final int r = getRightDecorationWidth(view);
                    final int t = getTopDecorationHeight(view);
                    final int b = getBottomDecorationHeight(view);
                    System.out.println("measureScrapChild  Decoration [ position_"+position+"]:  left = "
                            + l +" ,right = " + r +" ,top = " + t +" ,bottom = " + b);*/
                //  measureChild(view, widthSpec, heightSpec);
                // measureChildWithMargins(view,0,0);
                int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                        getPaddingLeft() + getPaddingRight()
                        + getLeftDecorationWidth(view) + getRightDecorationWidth(view)
                        , p.width
                );
                int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                        getPaddingTop() + getPaddingBottom()
                        + getTopDecorationHeight(view) + getBottomDecorationHeight(view)
                        , p.height
                );
                view.measure(childWidthSpec, childHeightSpec);

                measuredDimension[0] = getDecoratedMeasuredWidth(view)  + p.leftMargin + p.rightMargin;
                measuredDimension[1] = getDecoratedMeasuredHeight(view) + p.bottomMargin + p.topMargin;
               // measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
               // measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
                recycler.recycleView(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
    }
}
