package org.heaven7.core.adapter;

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
        return mItemView.state == STATE_OPNE;
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

        private View topView;   //主view
        private View bgView;    //菜单view
        /*** 左侧起点*/
        private int viewX;
        /*** 背景大小*/
        private int bgWidth;
        /*** 默认右滑菜单*/
        private int mTrackingEdges = EDGE_RIGHT;
        /*** 当前是否打开*/
        protected int state = STATE_CLOSE;

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

        public void initView(View topView, View bgView) {
            viewX = 0;
            this.topView = topView;
            this.bgView = bgView;
            addView(createBgView(bgView));
            addView(topView);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (widthMeasureSpec != 0) {
                bgWidth = bgView.getWidth();
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
                return child == topView;
            }

            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
              //  Logger.i(TAG,"---- onEdgeDragStarted ------");
                mDragHelper.captureChildView(topView, pointerId);
                if (bgWidth != 0)
                    bgView.setVisibility(View.GONE);

            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
             //   Logger.i(TAG,"---- onViewPositionChanged : dx = " + dx);
                if (left != viewX) {
                    if (bgView.getVisibility() == View.GONE)
                        bgView.setVisibility(View.VISIBLE);
                } else {
                    if (bgView.getVisibility() == View.VISIBLE)
                        bgView.setVisibility(View.GONE);

                }
               invalidate();
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
             //   Logger.i(TAG, "---- onViewReleased ------");
                if (releasedChild != topView)
                    return;

                int newLeft;
                if (mTrackingEdges == EDGE_LEFT) {
                    if (topView.getLeft() < mTouchSlop || state == STATE_OPNE) {
                        newLeft = viewX;
                        changeSwipeState(STATE_CLOSE);
                    } else {
                        newLeft = bgWidth;
                        changeSwipeState(STATE_OPNE);
                    }
                } else {
                    if (topView.getLeft() > - mTouchSlop || state == STATE_OPNE) {
                        newLeft = viewX;
                        changeSwipeState(STATE_CLOSE);
                    } else {
                        newLeft = -1 * bgWidth;
                        changeSwipeState(STATE_OPNE);
                    }
                }
                if (mDragHelper.smoothSlideViewTo(topView, newLeft, 0)) {
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
                    if (left > bgWidth && dx > 0) return bgWidth;
                    if (left < 0 && dx < 0) return 0;
                } else {
                    if (left > 0 && dx > 0) return 0;
                    if (left < -bgWidth && dx < 0) return -bgWidth;
                }
                return left;
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return topView == child ? child.getWidth() : 0;
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return topView == child ? child.getHeight() : 0;
            }
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
           // Logger.i(TAG, "---- onInterceptTouchEvent : MotionEvent = " + ev.toString());
            if (state == STATE_CLOSE)
                bgView.setVisibility(View.GONE);
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
            int newLeft = (mTrackingEdges == EDGE_LEFT ? bgWidth : -1 * bgWidth);
            if (mDragHelper.smoothSlideViewTo(topView, newLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(DragFrameLayout.this);
            }
            invalidate();
        }

        @Override
        public boolean isOpened() {
            return state == STATE_OPNE;
        }

        @Override
        public void close() {
            changeSwipeState(STATE_CLOSE);
            if (mDragHelper.smoothSlideViewTo(topView, viewX, 0)) {
                ViewCompat.postInvalidateOnAnimation(DragFrameLayout.this);
            }
            invalidate();
        }

        void changeSwipeState(int newSwipeState){
            state = newSwipeState;
            if(mSwipeListener!=null){
                mSwipeListener.onSwipeStateChange(this,newSwipeState);
            }
        }
        public void setOnSwipeStateChangeListener(OnSwipeStateChangeListener l){
            this.mSwipeListener  = l;
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
