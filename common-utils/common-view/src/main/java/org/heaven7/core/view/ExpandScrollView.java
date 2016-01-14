package org.heaven7.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

/**
 * 能够兼容ViewPager and 竖直recyclerview  的 ScrollView
 * 解决了ViewPager在ScrollView中的滑动反弹问题
 */
public class ExpandScrollView extends ScrollView {
    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;
    private final int mTouchSlop;

    public ExpandScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                
                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;
                
                if(xDistance > yDistance){
                    return false;
                }
                //解决scrollView嵌套 recyclerview 后 recyclerview 惯性滑动消失的问题. 即拦截child的点击事件
                if(yDistance > mTouchSlop){
                     return true;
                }

        }
        return super.onInterceptTouchEvent(ev);
    }
} 
