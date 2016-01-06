package org.heaven7.core.item.decoration;

import android.graphics.drawable.Drawable;

/**
 * Created by heaven7 on 2016/1/6.
 */
public interface IDividerManager {

    void setDivider(Drawable divider);

    void setDivider(int color, int widthInVertical, int heightInHorizontal);

    Drawable getDivider();

    int getDividerWidth();

    int getDividerHeight();
}
