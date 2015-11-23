package org.heaven7.demo.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.ScrollView;

import org.heaven7.demo.R;

/**
 * 回弹效果的view, ---- not done --------
 * Created by heaven7 on 2015/10/16.
 */
public class ReboundView  extends LinearLayout{

    private static final int FLAG_LIST_VIEW      = 1;
    private static final int FLAG_SCROLL_VIEW    = 2;
    private static final int FLAG_RECYCLER_VIEW  = 3;
    private static final int FLAG_VIEW_PAGER     = 4;

    private static final int REBOUND_HEIGT = 30;

    private View mHeadView;
    private View mBottomView;
    private OverScroller mScroller;
    private VelocityTracker mTracker;

    private int mScrollableFlag ;
    private int mInnerScrollViewId;

    private int mTouchSlop;

    private int mMaximumVelocity, mMinimumVelocity;
    private float mfirstY;

    private View mScrollableView;

    public ReboundView(Context context) {
        super(context);
        init(context,null);
    }

    public ReboundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ReboundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @TargetApi(21)
    public ReboundView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
       // attrs == null
        if(attrs!=null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ReboundView);
           // mScrollableFlag = a.getInt(R.styleable.ReboundView_scrollableChild,FLAG_SCROLL_VIEW);
            mInnerScrollViewId =  a.getInt(R.styleable.ReboundView_viewPager_inner_scroll_view_id,0 );
            a.recycle();
        }
        findScrollableView();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context)
                .getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context)
                .getScaledMinimumFlingVelocity();

        mScroller = new OverScroller(context);
        mHeadView = new View(context);
        mBottomView = new View(context);
        mHeadView.setLayoutParams(new LinearLayout.LayoutParams(1,1));
        mBottomView.setLayoutParams(new LinearLayout.LayoutParams(1,1));
        this.addView(mHeadView, 0);
        this.addView(mBottomView);
    }

    private void findScrollableView() {
        for(int i=0 ,count = getChildCount(); i< count; i++){
            final View view = getChildAt(i);
            if(view instanceof ListView){
                mScrollableView = view;
                mScrollableFlag = FLAG_LIST_VIEW;
                break;
            }else if(view instanceof ScrollView){
                mScrollableView = view;
                mScrollableFlag = FLAG_SCROLL_VIEW;
                break;
            }else if(view instanceof RecyclerView){
                mScrollableView = view;
                mScrollableFlag = FLAG_RECYCLER_VIEW;
                break;
            }else if(view instanceof ViewPager){
                mScrollableView = view;
                mScrollableFlag = FLAG_VIEW_PAGER;
                break;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // mLastY = ev.getRawY();
                mfirstY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // final float deltaY = ev.getRawY() - mLastY;
                float dy = ev.getY() - mfirstY;
                if(Math.abs(dy) >mTouchSlop){
                     switch (mScrollableFlag){
                         case FLAG_LIST_VIEW:
                             ListView lv = (ListView) mScrollableView;
                             final ListAdapter adapter = lv.getAdapter();
                             if(adapter !=null && adapter.getCount()>0){
                                 final View firstVisibleView = lv.getChildAt(lv.getFirstVisiblePosition());
                             }
                             break;
                         case FLAG_SCROLL_VIEW:
                             break;
                         case FLAG_RECYCLER_VIEW:
                             break;
                         case FLAG_VIEW_PAGER:

                             break;
                     }
                }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // final float deltaY = ev.getRawY() - mLastY;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onTouchEvent(ev);
    }
}
