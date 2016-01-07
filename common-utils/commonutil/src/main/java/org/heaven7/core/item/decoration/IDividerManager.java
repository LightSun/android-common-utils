package org.heaven7.core.item.decoration;

import android.graphics.drawable.Drawable;

/**
 * Created by heaven7 on 2016/1/6.
 */
public interface IDividerManager {

    /**
     * set the divider
     */
    void setDivider(Drawable divider);

    /**
     * set the divider
     * @param color the divider color
     * @param widthInVertical  divider width in vertical layout
     * @param heightInHorizontal divider height in horizontal layout
     */
    void setDivider(int color, int widthInVertical, int heightInHorizontal);

    Drawable getDivider();

    int getDividerWidth();

    int getDividerHeight();
}
