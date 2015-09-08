package org.heaven7.core.adapter;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heaven7 on 2015/9/3.
 */
public abstract class SelectHelper<T extends ISelectable>{

    private static final String TAG = "SelectHelper";
    private int mSelectMode;

    private List<Integer> mSelectedPositions;
    private List<Integer> mTempPositions;
    private int mSelectedPosition = -1;

    private List<T> mSelectDatas;

    public SelectHelper(int selectMode) {
        if(selectMode == ISelectable.SELECT_MODE_MULTI)
            this.mSelectedPositions = new ArrayList<>();
        if(selectMode!= ISelectable.SELECT_MODE_SINGLE &&
                selectMode != ISelectable.SELECT_MODE_MULTI){
            throw new IllegalArgumentException("invalid select mode = " +selectMode);
        }
        this.mSelectMode = selectMode;
    }

    /**
     * select the target position with notify data.if currentPosition  == position.ignore it.
     * <li></>only support select mode = {@link ISelectable#SELECT_MODE_SINGLE} ,this will auto update**/
    public void setSelected(int position){
        if(mSelectMode == ISelectable.SELECT_MODE_MULTI)
            return ;
        if(mSelectedPosition == position){
            return ;
        }
        if(position < 0)
            throw new IllegalArgumentException();
        if(mSelectedPosition!= ISelectable.INVALID_POSITION){
            getSelectedItemAtPosition(mSelectedPosition).setSelected(false);
            if(isRecyclable()){
                notifyItemChanged(mSelectedPosition);
            }
        }
        mSelectedPosition = position;
        getSelectedItemAtPosition(position).setSelected(true);
        if(isRecyclable()){
            notifyItemChanged(position);
        }else
            notifyAllChanged();
    }

    /** only support select mode = {@link ISelectable#SELECT_MODE_MULTI}**/
    public void addSelected(int selectPosition){
        if(mSelectMode == ISelectable.SELECT_MODE_SINGLE)
            return ;
        if(mSelectedPositions == null)
            throw new IllegalStateException("select mode must be multi");
        if(mSelectedPositions.contains(selectPosition)){
            Log.i(TAG, "the selectPosition = " + selectPosition + " is already selected!");
            return ;
        }
        mSelectedPositions.add(selectPosition);
        getSelectedItemAtPosition(selectPosition).setSelected(true);
        if(isRecyclable()){
            notifyItemChanged(selectPosition);
        }else
            notifyAllChanged();
    }

    public void clearAllSelected(){
        final boolean recyclable = isRecyclable();
        if(mSelectMode == ISelectable.SELECT_MODE_MULTI) {
            int pos;
            final List<Integer> mSelectedPositions =this.mSelectedPositions;
            for (int i = 0, size = mSelectedPositions.size(); i < size; i++) {
                pos = mSelectedPositions.get(i);
                getSelectedItemAtPosition(pos).setSelected(false);
                if(recyclable){
                    notifyItemChanged(pos);
                }
            }
            mSelectedPositions.clear();
            if(!recyclable) {
                notifyAllChanged();
            }
        }else{
            if(mSelectedPosition!= ISelectable.INVALID_POSITION){
                int preSelectPos = mSelectedPosition;
                mSelectedPosition = ISelectable.INVALID_POSITION;
                getSelectedItemAtPosition(preSelectPos).setSelected(false);
                if(recyclable){
                    notifyItemChanged(preSelectPos);
                }else{
                    notifyAllChanged();
                }
            }
        }
    }

    public  T getSelectedItem(){
        if(mSelectedPosition == ISelectable.INVALID_POSITION)
            return null;
        return getSelectedItemAtPosition(mSelectedPosition);
    }

    public int getSelectedPosition(){
        return mSelectedPosition ;
    }

    public List<Integer> getSelectedPositions(){
        if(mTempPositions == null) {
             mTempPositions = new ArrayList<>() ;
        }
        mTempPositions.clear();
        mTempPositions.addAll(mSelectedPositions);
        return mTempPositions;
    }

    public  List<T> getSelectedItems(){
        if(mSelectedPositions == null || mSelectedPositions.size() == 0 )
            return null;
        if(mSelectDatas == null){
            mSelectDatas = new ArrayList<>();
        }
        final List<T> mSelectDatas = this.mSelectDatas;
        final List<Integer> mSelectedPositions = this.mSelectedPositions;

        mSelectDatas.clear();
        for(int i=0,size= mSelectedPositions.size() ; i<size ;i++){
            mSelectDatas.add(getSelectedItemAtPosition(mSelectedPositions.get(i)));
        }
        return mSelectDatas;
    }

    public void initSelectPositions(List<T> list){
        if(list==null || list.size() ==0){
            return;
        }
        for(int i=0 ,size =list.size() ;i<size ; i++){
            if(list.get(i).isSelected()){
                initSelectPosition(i);
            }
        }
    }

    /** this will only called once. */
    private void initSelectPosition(int position) {
        if (mSelectMode == ISelectable.SELECT_MODE_SINGLE) {
            if (mSelectedPosition == ISelectable.INVALID_POSITION) {
                mSelectedPosition = position;
            }
        } else if (mSelectMode == ISelectable.SELECT_MODE_MULTI) {
            if(!mSelectedPositions.contains(position))
                mSelectedPositions.add(position);
        } else {
            //can't reach here
            throw new RuntimeException();
        }
    }
    /** indicate it use BaseAdapter/BaseExpandableListAdapter or QuickRecycleViewAdapter */
    protected boolean isRecyclable(){
        return false;
    }
    /** update the datas of adapter ,eg: notifyDataSetChanged*/
    protected abstract void notifyAllChanged();

    /** only used for  RecycleViewAdapter  */
    protected abstract void notifyItemChanged(int itemPosition);

    protected abstract T getSelectedItemAtPosition(int position);

}
