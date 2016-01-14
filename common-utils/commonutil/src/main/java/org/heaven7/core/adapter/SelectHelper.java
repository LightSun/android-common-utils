package org.heaven7.core.adapter;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heaven7 on 2015/9/3.
 */
public class SelectHelper<T extends ISelectable>{

    private static final String TAG = "SelectHelper";
    private final int mSelectMode;

    private List<Integer> mSelectedPositions;
    private List<Integer> mTempPositions;
    private int mSelectedPosition = -1;

    private List<T> mSelectDatas;
    private Callback<T> mCallback;

    private SelectHelper(int selectMode) {
        if(selectMode == ISelectable.SELECT_MODE_MULTI)
            this.mSelectedPositions = new ArrayList<>();
        if(selectMode!= ISelectable.SELECT_MODE_SINGLE &&
                selectMode != ISelectable.SELECT_MODE_MULTI){
            throw new IllegalArgumentException("invalid select mode = " +selectMode);
        }
        this.mSelectMode = selectMode;
    }
    /** @since 1.7.5 */
    /*public*/ SelectHelper(int selectMode,Callback<T> callback) {
        this(selectMode);
        this.mCallback = callback;
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
        final Callback<T> mCallback = this.mCallback;
        if(mSelectedPosition!= ISelectable.INVALID_POSITION){
            mCallback.getSelectedItemAtPosition(mSelectedPosition).setSelected(false);
            if(mCallback.isRecyclable()){
                mCallback.notifyItemChanged(mSelectedPosition);
            }
        }
        mSelectedPosition = position;
        mCallback.getSelectedItemAtPosition(position).setSelected(true);
        if(mCallback.isRecyclable()){
            mCallback.notifyItemChanged(position);
        }else
            mCallback.notifyDataSetChanged();
    }
    /** {@link ISelectable#SELECT_MODE_MULTI} and {@link ISelectable#SELECT_MODE_SINGLE} both support */
    public void unselect(int position){
        if(mSelectMode == ISelectable.SELECT_MODE_MULTI){
            addUnselected(position);
        }else{
            setUnselected(position);
        }
    }
    /** unselect the position of item ,
     * only support select mode = {@link ISelectable#SELECT_MODE_SINGLE}
     * */
    public void setUnselected(int position){
        if(mSelectMode == ISelectable.SELECT_MODE_MULTI)
            return ;
        //  mSelectedPosition must == position
        if(mSelectedPosition == ISelectable.INVALID_POSITION
                || mSelectedPosition != position){
            return ;
        }
        if(position < 0)
            throw new IllegalArgumentException();

        final Callback<T> mCallback = this.mCallback;
        mCallback.getSelectedItemAtPosition(position).setSelected(false);
        mSelectedPosition = ISelectable.INVALID_POSITION;
        if(mCallback.isRecyclable()){
            mCallback.notifyItemChanged(position);
        }else{
            mCallback.notifyDataSetChanged();
        }
    }
    /** only support select mode = {@link ISelectable#SELECT_MODE_MULTI}**/
    public void addUnselected(int position){
        if(mSelectMode == ISelectable.SELECT_MODE_SINGLE)
            return ;
        if(mSelectedPositions == null)
            throw new IllegalStateException("select mode must be multi");
        if(!mSelectedPositions.contains(position)){
            return ; //not selected
        }
        mSelectedPositions.remove(Integer.valueOf(position));

        final Callback<T> mCallback = this.mCallback;
        mCallback.getSelectedItemAtPosition(position).setSelected(false);
        if(mCallback.isRecyclable()){
            mCallback.notifyItemChanged(position);
        }else
            mCallback.notifyDataSetChanged();
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

        final Callback<T> mCallback = this.mCallback;
        mCallback.getSelectedItemAtPosition(selectPosition).setSelected(true);
        if(mCallback.isRecyclable()){
            mCallback.notifyItemChanged(selectPosition);
        }else
            mCallback.notifyDataSetChanged();
    }
    /** clear the select state but not notify data changed. */
    public void clearSelectedPositions(){
        if(mSelectMode == ISelectable.SELECT_MODE_MULTI){
            mSelectedPositions.clear();
        }else {
            mSelectedPosition = ISelectable.INVALID_POSITION;
        }
    }

    /** clear the all selected state  and notify data change. */
    public void clearAllSelected(){
        final Callback<T> mCallback = this.mCallback;
        final boolean recyclable = mCallback.isRecyclable();
        if(mSelectMode == ISelectable.SELECT_MODE_MULTI) {
            int pos;
            final List<Integer> mSelectedPositions =this.mSelectedPositions;
            for (int i = 0, size = mSelectedPositions.size(); i < size; i++) {
                pos = mSelectedPositions.get(i);
                mCallback.getSelectedItemAtPosition(pos).setSelected(false);
                if(recyclable){
                    mCallback.notifyItemChanged(pos);
                }
            }
            mSelectedPositions.clear();
            if(!recyclable) {
                mCallback.notifyDataSetChanged();
            }
        }else{
            if(mSelectedPosition!= ISelectable.INVALID_POSITION){
                int preSelectPos = mSelectedPosition;
                mSelectedPosition = ISelectable.INVALID_POSITION;
                mCallback.getSelectedItemAtPosition(preSelectPos).setSelected(false);
                if(recyclable){
                    mCallback.notifyItemChanged(preSelectPos);
                }else{
                    mCallback.notifyDataSetChanged();
                }
            }
        }
    }

    /** toogle the all selected state and notify data change. */
    public void toogleSelected(int position){
        if(position < 0){
            throw new IllegalArgumentException(" position can't be negative !");
        }
        if(mSelectMode == ISelectable.SELECT_MODE_MULTI) {
            if(mSelectedPositions.contains(position)){
                addUnselected(position);
            }else{
                addSelected(position);
            }
        }else{
            if( mSelectedPosition == position){
                setUnselected(position);
            }else{
                setSelected(position);
            }
        }
    }

    public  T getSelectedItem(){
        if(mSelectedPosition == ISelectable.INVALID_POSITION)
            return null;
        return mCallback.getSelectedItemAtPosition(mSelectedPosition);
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
        final Callback<T> mCallback = this.mCallback;
        for(int i=0,size= mSelectedPositions.size() ; i<size ;i++){
            mSelectDatas.add(mCallback.getSelectedItemAtPosition(mSelectedPositions.get(i)));
        }
        return mSelectDatas;
    }

    /*public*/ void initSelectPositions(List<T> list){
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
   /* protected boolean isRecyclable(){
        return false;
    }*/
    /** update the datas of adapter ,eg: notifyDataSetChanged*/
  //  protected abstract void notifyAllChanged();

    /** only used for  RecycleViewAdapter  */
   // protected abstract void notifyItemChanged(int itemPosition);

 //   protected abstract T getSelectedItemAtPosition(int position);

    /**
     * @since 1.7.5
     * @param <T>
     */
    /*public*/ interface Callback<T>{
        /** indicate it use BaseAdapter/BaseExpandableListAdapter or QuickRecycleViewAdapter */
         boolean isRecyclable();
        /** update the datas of adapter */
        void notifyDataSetChanged();

        /** only used for  RecycleViewAdapter  */
        void notifyItemChanged(int itemPosition);

        T getSelectedItemAtPosition(int position);
    }

}
