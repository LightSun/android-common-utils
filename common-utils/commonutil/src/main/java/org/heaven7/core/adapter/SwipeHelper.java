package org.heaven7.core.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.heaven7.core.util.Cacher;

import java.lang.ref.WeakReference;

/**
 * 支持左滑右滑菜单
 * @since 1.8.0
 */
public class SwipeHelper {
    /*** 左滑菜单*/
    public static final int EDGE_LEFT = 1;
    /*** 右滑菜单*/
    public static final int EDGE_RIGHT = 2;

    /*** 打开*/
    public static final int STATE_OPNE = 1;
    /*** 关闭*/
    public static final int STATE_CLOSE = 2;

    /*** real item view*/
    private DragFrameLayout mItemView;

    private final int mMainLayoutId;
    private final int mMenuLayoutId;

    // here resolve the problem : while swipe in adapter. the  outmost view can't reuse. so just cache some. and reuse item and menu view.
    // or else will cause bug
    private static final Cacher<WeakReference<DragFrameLayout>,Context> sCacher = new Cacher<WeakReference<DragFrameLayout>, Context>(5) {
        @Override
        public WeakReference<DragFrameLayout> create(Context context) {
            return new WeakReference<>(new DragFrameLayout(context));
        }

        @Override
        public WeakReference<DragFrameLayout> obtain(Context context) {
            WeakReference<DragFrameLayout>  wrf = super.obtain(context);
            if( wrf != null && wrf.get() != null){
               Context oldContext =  wrf.get().getContext();
                if(oldContext == null || (oldContext instanceof Activity  &&  context instanceof Activity
                        && oldContext != context)  ) {
                    // invalid
                    return super.obtain(context);
                }
            }
            return wrf;
        }

        @Override
        public void recycle(WeakReference<DragFrameLayout> wrf) {
            if(wrf != null && wrf.get() != null){
                Context oldContext =  wrf.get().getContext();
                //no need recycle
                if(oldContext == null || (oldContext instanceof Activity &&
                        ((Activity) oldContext).isFinishing())){
                     return;
                }
                super.recycle(wrf);
            }
        }
    };

    /**
     * called on swipe state change
     */
    public interface OnSwipeStateChangeListener{
        /**
         * @param swipeManager the swipe operate manager
         * @param swipeState  the state of swipe ,must be
         * {@link org.heaven7.core.adapter.SwipeHelper#STATE_CLOSE}  or
         * {@link org.heaven7.core.adapter.SwipeHelper#STATE_OPNE}
         */
          void onSwipeStateChange(ISwipeManager swipeManager ,int swipeState);
    }

    /*public*/ SwipeHelper(ViewGroup parent, @LayoutRes int mainLayoutId,
                           @LayoutRes int menuLayoutId) {
        this(parent, mainLayoutId, menuLayoutId, EDGE_RIGHT);
    }
    /*public*/ SwipeHelper(ViewGroup parent, @LayoutRes int mainLayoutId,
                           @LayoutRes int menuLayoutId, int mTrackingEdges) {
        this.mMainLayoutId = mainLayoutId;
        this.mMenuLayoutId = menuLayoutId;
        final Context context = parent.getContext();
        View mainView = LayoutInflater.from(context).inflate(mainLayoutId, parent,false);
        View menuView = LayoutInflater.from(context).inflate(menuLayoutId, parent,false);
        init(mainView, menuView, mTrackingEdges);
    }

    private void init(View mainView, View menuView,int mTrackingEdges) {
        mItemView = new DragFrameLayout(mainView.getContext());
        //must annotate or else must caused bug in list view
       /*
       mItemView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));*/
        mItemView.setTrackingEdges(mTrackingEdges);
        mItemView.initView(mainView, menuView);
    }

    public void close() {
        mItemView.close();
    }
    public void open() {
        mItemView.open();
    }
    public boolean isOpen() {
        return mItemView.mState == STATE_OPNE;
    }

    public View getItemView(){
        return mItemView;
    }
    public int getMainLayoutId(){
        return mMainLayoutId;
    }
    public int getMenuLayoutId(){
        return mMenuLayoutId;
    }
    public void setOnSwipeStateChangeListener(OnSwipeStateChangeListener l){
        this.mItemView.setOnSwipeStateChangeListener(l);
    }

 /*   public View obtainItemView(){

    }*/

    public static class BaseSwipeViewHolder extends RecyclerView.ViewHolder{

        private final SwipeHelper mSwipeHelper;

        public BaseSwipeViewHolder(ViewGroup parent,@LayoutRes int mainLayoutId,
                           @LayoutRes int menuLayoutId) {
            this(parent, mainLayoutId, menuLayoutId, EDGE_RIGHT);
        }
        public BaseSwipeViewHolder(ViewGroup parent,@LayoutRes int mainLayoutId,
                           @LayoutRes int menuLayoutId, int mTrackingEdges) {
            this(new SwipeHelper(parent,mainLayoutId,menuLayoutId,mTrackingEdges));
        }

        public BaseSwipeViewHolder(SwipeHelper swipeHelper) {
            super(swipeHelper.mItemView);
            this.mSwipeHelper = swipeHelper;
        }
        public BaseSwipeViewHolder(View itemView) {
            super(itemView);
            this.mSwipeHelper = null;
        }

        public SwipeHelper getSwipeHelper() {
            return mSwipeHelper;
        }
    }

    private static class DragFrameLayout extends FrameLayout implements ISwipeManager {

        private static final String TAG = "DragFrameLayout";
        private ViewDragHelper mDragHelper;

         View mMainView;    //主view
         View mMenuView;    //菜单view
        /*** 左侧起点*/
        private int mLeftStartX;
        /*** 背景大小*/
        private int mMenuWidth;
        /*** 默认右滑菜单*/
        private int mTrackingEdges = EDGE_RIGHT;
        /*** 当前是否打开*/
        protected int mState = STATE_CLOSE;

        private OnSwipeStateChangeListener mSwipeListener;
        private int mTouchSlop;

        public DragFrameLayout(Context context) {
            super(context);
            init();
        }

        public DragFrameLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public DragFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        public void initView(View mainView, View menuView) {
            mLeftStartX = 0;
            this.mMainView = mainView;
            this.mMenuView = menuView;
            //remove previous
            removeAllViews();
            addView(createBgView(menuView));
            addView(mainView);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (widthMeasureSpec != 0) {
                mMenuWidth = mMenuView.getWidth();
            }
        }

        private View createBgView(View view) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            linearLayout.setGravity(mTrackingEdges == EDGE_RIGHT ? Gravity.END : Gravity.START);
            linearLayout.addView(view);
            return linearLayout;
        }

        private void init() {
            mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
            mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragCallBack());
            mDragHelper.setEdgeTrackingEnabled(mTrackingEdges == EDGE_RIGHT ?
                    ViewDragHelper.EDGE_RIGHT : ViewDragHelper.EDGE_LEFT);
        }

        private class ViewDragCallBack extends ViewDragHelper.Callback {

            public ViewDragCallBack() {
            }

            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == mMainView;
            }

            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
              //  Logger.i(TAG,"---- onEdgeDragStarted ------");
                mDragHelper.captureChildView(mMainView, pointerId);
                if (mMenuWidth != 0)
                    mMenuView.setVisibility(View.GONE);

            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
             //   Logger.i(TAG,"---- onViewPositionChanged : dx = " + dx);
                if (left != mLeftStartX) {
                    if (mMenuView.getVisibility() == View.GONE)
                        mMenuView.setVisibility(View.VISIBLE);
                } else {
                    if (mMenuView.getVisibility() == View.VISIBLE)
                        mMenuView.setVisibility(View.GONE);

                }
               invalidate();
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
             //   Logger.i(TAG, "---- onViewReleased ------");
                if (releasedChild != mMainView)
                    return;

                int newLeft;
                if (mTrackingEdges == EDGE_LEFT) {
                    if (mMainView.getLeft() < mTouchSlop || mState == STATE_OPNE) {
                        newLeft = mLeftStartX;
                        changeSwipeState(STATE_CLOSE);
                    } else {
                        newLeft = mMenuWidth;
                        changeSwipeState(STATE_OPNE);
                    }
                } else {
                    if (mMainView.getLeft() > - mTouchSlop || mState == STATE_OPNE) {
                        newLeft = mLeftStartX;
                        changeSwipeState(STATE_CLOSE);
                    } else {
                        newLeft = -1 * mMenuWidth;
                        changeSwipeState(STATE_OPNE);
                    }
                }
                if (mDragHelper.smoothSlideViewTo(mMainView, newLeft, 0)) {
                    ViewCompat.postInvalidateOnAnimation(DragFrameLayout.this);
                }
                invalidate();
            }

            /**
             * 水平方向处理
             *
             * @param child 被拖动到view
             * @param left  移动到达的x轴的距离
             * @param dx    建议的移动的x距离
             */
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                if (mTrackingEdges == EDGE_LEFT) {
                    if (left > mMenuWidth && dx > 0) return mMenuWidth;
                    if (left < 0 && dx < 0) return 0;
                } else {
                    if (left > 0 && dx > 0) return 0;
                    if (left < -mMenuWidth && dx < 0) return -mMenuWidth;
                }
                return left;
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return mMainView == child ? child.getWidth() : 0;
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return mMainView == child ? child.getHeight() : 0;
            }
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
           // Logger.i(TAG, "---- onInterceptTouchEvent : MotionEvent = " + ev.toString());
            if (mState == STATE_CLOSE)
                mMenuView.setVisibility(View.GONE);
            return mDragHelper.shouldInterceptTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
          //  Logger.i(TAG, "---- onTouchEvent : MotionEvent = " + event.toString());
            mDragHelper.processTouchEvent(event);
            return true;
            //return super.onTouchEvent(event);
        }

        @Override
        public void computeScroll() {
            super.computeScroll();
            if (mDragHelper.continueSettling(true)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        public void setTrackingEdges(int mTrackingEdges) {
            this.mTrackingEdges = mTrackingEdges;
            mDragHelper.setEdgeTrackingEnabled(mTrackingEdges == EDGE_RIGHT ? ViewDragHelper.EDGE_RIGHT : ViewDragHelper.EDGE_LEFT);
        }

        @Override
        public void open() {
            changeSwipeState(STATE_OPNE);
            int newLeft = (mTrackingEdges == EDGE_LEFT ? mMenuWidth : -1 * mMenuWidth);
            if (mDragHelper.smoothSlideViewTo(mMainView, newLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(DragFrameLayout.this);
            }
            invalidate();
        }

        @Override
        public boolean isOpened() {
            return mState == STATE_OPNE;
        }

        @Override
        public void close() {
            changeSwipeState(STATE_CLOSE);
            if (mDragHelper.smoothSlideViewTo(mMainView, mLeftStartX, 0)) {
                ViewCompat.postInvalidateOnAnimation(DragFrameLayout.this);
            }
            invalidate();
        }

        void changeSwipeState(int newSwipeState){
            mState = newSwipeState;
            if(mSwipeListener!=null){
                mSwipeListener.onSwipeStateChange(this,newSwipeState);
            }
        }
        public void setOnSwipeStateChangeListener(OnSwipeStateChangeListener l){
            this.mSwipeListener  = l;
        }

        @Override
        protected void onDetachedFromWindow() {
            sCacher.recycle(new WeakReference<>(this));
            super.onDetachedFromWindow();
        }
    }

    public interface ISwipeManager{

        /** close the swipe */
        void close();

        /** open the swipe */
        void open();

        /** is the swipe opened */
        boolean isOpened();

    }
}
