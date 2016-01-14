package org.heaven7.core.view;


import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;


/**
 * similar to android newest ProgressBar, from internet
 * 必须固定宽高，否则 可能看起来怪异.
 */
public class EasyProgress extends View {

    //private final static String TAG = "EasyProgress.java";
    private static final int DEFAULT_TRAIN_BORDER_WIDTH = 6;
    private static final int DEFULT_ARC_COLOR = Color.BLUE;

    private static final float DEFULT_MAX_ANGLE = -305f;
    private static final float DEFULT_MIN_ANGLE = -19f;

    //默认的动画时间
    private static final int DEFULT_DURATION = 660;

    private Paint arcPaint;
    private RectF arcRectf;
    private float mStartAngle = -45f;
    private float mSweepAngle = -19f;
    private float mIncrementAngele = 0;

    private int mArcColor = DEFULT_ARC_COLOR;
    private int mBorderWidth;
    private AnimatorSet mAnimatorSet;
    private boolean mCancled;
    private int mSize;
    private int mAnimDuration;

    public EasyProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeset(context, attrs, defStyleAttr);
        init(context);
    }

    public EasyProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributeset(context, attrs, 0);
        init(context);
    }

    public EasyProgress(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        arcPaint = new Paint();
        arcPaint.setColor(mArcColor);
        arcPaint.setStrokeWidth(mBorderWidth);
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcRectf = new RectF();
    }

    private void initAttributeset(Context context, AttributeSet attrs, int defStyle) {
        int mTrainBorderWidth = (int) (context.getResources().getDisplayMetrics().density *
                DEFAULT_TRAIN_BORDER_WIDTH);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EasyProgressAttr, defStyle, 0);
        mArcColor = typedArray.getColor(R.styleable.EasyProgressAttr_ep_arcColor, Color.BLUE);
        mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.EasyProgressAttr_ep_borderWidth,
                mTrainBorderWidth);
        mAnimDuration = typedArray.getDimensionPixelSize(R.styleable.EasyProgressAttr_ep_duration,
                DEFULT_DURATION);

        typedArray.recycle();
    }

    private void stupBound() {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        arcRectf.set(paddingLeft + mBorderWidth, paddingTop + mBorderWidth, mSize - paddingLeft - mBorderWidth, mSize - paddingTop - mBorderWidth);
    }

    private void startAnimation() {

        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }

        mAnimatorSet = new AnimatorSet();

        AnimatorSet set = circuAnimator();
        mAnimatorSet.play(set);
        mAnimatorSet.addListener(new AnimatorListener() {
            private boolean isCancel = false;

            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationRepeat(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
                if (!isCancel) {
                    startAnimation();
                }
            }

            public void onAnimationCancel(Animator animation) {
                isCancel = true;
            }
        });
        mAnimatorSet.start();
    }

    /**
     * 循环的动画
     */
    private AnimatorSet circuAnimator() {

        //从小圈到大圈
        ValueAnimator holdAnimator1 = ValueAnimator.ofFloat(mIncrementAngele + DEFULT_MIN_ANGLE,
                mIncrementAngele + 115f);
        holdAnimator1.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mIncrementAngele = (float) animation.getAnimatedValue();
            }
        });
        holdAnimator1.setDuration(mAnimDuration);
        holdAnimator1.setInterpolator(new LinearInterpolator());


        ValueAnimator expandAnimator = ValueAnimator.ofFloat(DEFULT_MIN_ANGLE, DEFULT_MAX_ANGLE);
        expandAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSweepAngle = (float) animation.getAnimatedValue();
                mIncrementAngele -= mSweepAngle;
                invalidate();
            }
        });
        expandAnimator.setDuration(mAnimDuration);
        expandAnimator.setInterpolator(new DecelerateInterpolator(2));


        //从大圈到小圈
        ValueAnimator holdAnimator = ValueAnimator.ofFloat(mStartAngle, mStartAngle + 115f);
        holdAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStartAngle = (float) animation.getAnimatedValue();
            }
        });

        holdAnimator.setDuration(mAnimDuration);
        holdAnimator.setInterpolator(new LinearInterpolator());

        ValueAnimator narrowAnimator = ValueAnimator.ofFloat(DEFULT_MAX_ANGLE, DEFULT_MIN_ANGLE);
        narrowAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSweepAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        narrowAnimator.setDuration(mAnimDuration);
        narrowAnimator.setInterpolator(new DecelerateInterpolator(2));

        AnimatorSet set = new AnimatorSet();
        set.play(holdAnimator1)
                .with(expandAnimator);
        set.play(holdAnimator)
                .with(narrowAnimator)
                .after(holdAnimator1);
        return set;
    }

    public void setArcColor(int color) {
        this.mArcColor = color;
    }
    private void cancelAnimation() {
        mCancled = true;
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
    }

    private void enableAnimation() {
        mCancled = false;
    }
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        switch (visibility) {
            case View.INVISIBLE:
            case View.GONE:
                cancelAnimation();
                break;
            case View.VISIBLE:
                enableAnimation();
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCancled) return;

        canvas.drawArc(arcRectf, mStartAngle + mIncrementAngele, mSweepAngle, false, arcPaint);

        if (mAnimatorSet == null || !mAnimatorSet.isRunning()) {
            startAnimation();
        }
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mSize = (w < h) ? w : h;
        stupBound();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        enableAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnimation();
    }
}
