package org.heaven7.core.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * usage like this:
 *
 * <pre>
 <com.yyh.singcat.view.PasswordInputView
 android:id="@+id/password_input_view"
 android:layout_width="match_parent"
 android:layout_height="56dp"
 android:layout_marginLeft="5dp"
 android:layout_marginRight="5dp"
 android:layout_marginTop="20dp"
 android:inputType="number"
 android:cursorVisible="false"
 android:padding="0dp"
 android:maxLength="6"
 app:piv_borderRadius="@dimen/custom_ev_border_radius"
 app:piv_borderWidth="0.5dp"
 app:piv_passwordLength="6"
 app:piv_passwordColor="@color/mublack"
 app:piv_passwordWidth="@dimen/custom_ev_password_width"
 />
 </pre>
 */
public class PasswordInputView extends EditText {
    
    private int textLength;

    private int borderColor;
    private float borderWidth;
    private float borderRadius;

    public  boolean isClear;

    private int passwordLength;
    private int passwordColor;
    private int passwordColorTwo;
    private float passwordWidth;
    private float passwordRadius;

    private Paint passwordPaint = new Paint(ANTI_ALIAS_FLAG);
    private Paint passwortwodPaint = new Paint(ANTI_ALIAS_FLAG);
    private Paint borderPaint = new Paint(ANTI_ALIAS_FLAG);

    private final int defaultContMargin = 2;
    private final int defaultSplitLineWidth = 2;

    private final RectF mBorderRect = new RectF();
    private final RectF mContentRect = new RectF();

    public PasswordInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final Resources res = getResources();

        final int defaultBorderColor = res.getColor(R.color.piv_default_border_color);
        final int defaultPasswordColor = res.getColor(R.color.piv_default_password_color);

        final float defaultBorderWidth = res.getDimension(R.dimen.piv_default_border_width);
        final float defaultBorderRadius = res.getDimension(R.dimen.piv_default_border_radius);
        final int defaultPasswordLength = res.getInteger(R.integer.piv_default_password_length);
        final float defaultPasswordWidth = res.getDimension(R.dimen.piv_default_password_width);
        final float defaultPasswordRadius = res.getDimension(R.dimen.piv_default_password_radius);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PasswordInputView, 0, 0);
        try {
           //passwordColorTwo = getResources().getColor(R.color.white_two);
            borderColor = a.getColor(R.styleable.PasswordInputView_piv_borderColor, defaultBorderColor);
            borderWidth = a.getDimension(R.styleable.PasswordInputView_piv_borderWidth, defaultBorderWidth);
            borderRadius = a.getDimension(R.styleable.PasswordInputView_piv_borderRadius, defaultBorderRadius);
            passwordLength = a.getInt(R.styleable.PasswordInputView_piv_passwordLength, defaultPasswordLength);
            passwordColor = a.getColor(R.styleable.PasswordInputView_piv_passwordColor, defaultPasswordColor);
            passwordColorTwo = a.getColor(R.styleable.PasswordInputView_piv_passwordColor2, defaultPasswordColor);
            passwordWidth = a.getDimension(R.styleable.PasswordInputView_piv_passwordWidth, defaultPasswordWidth);
            passwordRadius = a.getDimension(R.styleable.PasswordInputView_piv_passwordRadius, defaultPasswordRadius);
        } finally {
            a.recycle();
        }

        borderPaint.setStrokeWidth(defaultSplitLineWidth);
        borderPaint.setColor(borderColor);
        passwordPaint.setStrokeWidth(passwordWidth);
        passwordPaint.setStyle(Paint.Style.FILL);
        passwordPaint.setColor(passwordColor);

        passwortwodPaint.setStrokeWidth(passwordWidth);
        passwortwodPaint.setStyle(Paint.Style.FILL);
        passwortwodPaint.setColor(passwordColorTwo);

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                final int width = getWidth();
                final int height = getHeight();
                mBorderRect.set(0,0, width,height);
                mContentRect.set(mBorderRect.left + defaultContMargin, mBorderRect.top + defaultContMargin,
                        mBorderRect.right - defaultContMargin, mBorderRect.bottom - defaultContMargin);
                getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        // 外边框
        borderPaint.setColor(borderColor);
        canvas.drawRoundRect(mBorderRect, borderRadius, borderRadius, borderPaint);

        // 内容区
        borderPaint.setColor(Color.WHITE);
        canvas.drawRoundRect(mContentRect, borderRadius, borderRadius, borderPaint);

        // 分割线
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(defaultSplitLineWidth);
        for (int i = 1; i < passwordLength; i++) {
            float x = width * i / passwordLength;
            canvas.drawLine(x, 0, x, height, borderPaint);
        }


        if(isClear){
            // 密码
            float cx, cy = height / 2;
            float half = width / passwordLength / 2;
            for (int i = 0; i < passwordLength; i++) {
                cx = width * i / passwordLength + half;
                canvas.drawCircle(cx, cy, passwordWidth, passwortwodPaint);
            }
            isClear = false;
        }else {
            // 密码
            float cx, cy = height / 2;
            float half = width / passwordLength / 2;
            for (int i = 0; i < textLength; i++) {
                cx = width * i / passwordLength + half;
                canvas.drawCircle(cx, cy, passwordWidth, passwordPaint);
            }
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        this.textLength = text.toString().length();
        invalidate();
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        borderPaint.setColor(borderColor);
        invalidate();
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        borderPaint.setStrokeWidth(defaultSplitLineWidth);
        invalidate();
    }

    public float getBorderRadius() {
        return borderRadius;
    }

    public void setBorderRadius(float borderRadius) {
        this.borderRadius = borderRadius;
        invalidate();
    }

    public int getPasswordLength() {
        return passwordLength;
    }

    public void setPasswordLength(int passwordLength) {
        this.passwordLength = passwordLength;
        invalidate();
    }

    public int getPasswordColor() {
        return passwordColor;
    }

    public void setPasswordColor(int passwordColor) {
        this.passwordColor = passwordColor;
        passwordPaint.setColor(passwordColor);
        invalidate();
    }

    public float getPasswordWidth() {
        return passwordWidth;
    }

    public void setPasswordWidth(float passwordWidth) {
        this.passwordWidth = passwordWidth;
        passwordPaint.setStrokeWidth(passwordWidth);
        invalidate();
    }

    public float getPasswordRadius() {
        return passwordRadius;
    }

    public void setPasswordRadius(float passwordRadius) {
        this.passwordRadius = passwordRadius;
        invalidate();
    }

    public void setTextLength(int textLength) {
        this.textLength = textLength;
    }

    public void setIsClear(boolean isClear) {
        this.isClear = isClear;
    }
}
