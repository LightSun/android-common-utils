package org.heaven7.core.adapter;

/**
 * this class just implements the interface ISelectable . not the state selector of android resource .
 * Created by heaven7 on 2016/1/9.
 */
public class BaseSelector implements ISelectable {

    private boolean selected;

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }
}
