package org.heaven7.core.item.decoration;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

/**
 * this is the devider helper ,  help you manage it.
 * Created by heaven7 on 2016/1/6.
 */
/*public*/ class DividerManagerImpl implements IDividerManager {

    private Drawable mDivider;

    private int mDivideWidth ;
    private int mDivideHeight;

    @Override
    public void setDivider(Drawable divider){
        this.mDivider = divider;
        this.mDivideWidth = divider.getIntrinsicWidth();
        this.mDivideHeight = divider.getIntrinsicHeight();
    }

    @Override
    public void setDivider(int color, int widthInVertical, int heightInHorizontal){
        if(widthInVertical <0 || heightInHorizontal < 0 )
            throw new IllegalArgumentException();
        this.mDivider = new ColorDrawable(color);
        this.mDivideWidth = widthInVertical;
        this.mDivideHeight = heightInHorizontal;
    }

    @Override
    public Drawable getDivider(){
        return mDivider;
    }

    @Override
    public int getDividerWidth(){
        return mDivideWidth;
    }
    @Override
    public int getDividerHeight(){
        return mDivideHeight;
    }
}
