package org.heaven7.core.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import org.heaven7.core.viewhelper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heaven7 on 2015/8/27.
 */
public abstract class QuickExpandListAdapter<Group,Child> extends BaseExpandableListAdapter {

    private List<IExpandListItem<Group,Child>> mItems;
    private int mGroupLayoutId;
    private int mChildLayoutId;

    public QuickExpandListAdapter(int groupLayoutId, int childLayoutId, List<IExpandListItem<Group, Child>> mItems) {
        this.mItems = mItems!=null ? new ArrayList<>(mItems) :new ArrayList<IExpandListItem<Group,Child>>();
        if(groupLayoutId <=0 || childLayoutId <=0)
            throw new IllegalArgumentException();
        this.mGroupLayoutId = groupLayoutId;
        this.mChildLayoutId = childLayoutId;
    }
    @Override
    public int getGroupCount() {
        return mItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mItems.get(groupPosition).getChildren().size();
    }

    public void setData(List<IExpandListItem<Group,Child>> mItems){
        this.mItems  = mItems;
        notifyDataSetChanged();
    }

    @Override
    public Group getGroup(int groupPosition) {
        return mItems.get(groupPosition).getGroup();
    }

    @Override
    public Child getChild(int groupPosition, int childPosition) {
        return mItems.get(groupPosition).getChildren().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        ViewHelper helper;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(mGroupLayoutId,parent,false);
            helper = new ViewHelper(convertView);
            convertView.setTag(helper);
        }
        helper = (ViewHelper)convertView.getTag();
        onBindGroupData(context, groupPosition, getGroup(groupPosition), isExpanded, helper);
        return convertView;
    }

    protected abstract void onBindGroupData(Context context, int groupPosition,
                                            Group group, boolean isExpanded, ViewHelper helper);

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        ViewHelper helper;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(mChildLayoutId,parent,false);
            helper = new ViewHelper(convertView);
            convertView.setTag(helper);
        }
        helper = (ViewHelper)convertView.getTag();
        onBindChildData(context, groupPosition, childPosition, isLastChild,
                getChild(groupPosition, childPosition), helper);
        return convertView;
    }

    protected abstract void onBindChildData(Context context, int groupPosition, int childPosition,
                                            boolean isLastChild, Child child, ViewHelper helper);

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }

    public static interface IExpandListItem<Group,Child>{
        List<Child> getChildren();
        Group getGroup();
    }
}
