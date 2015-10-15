package org.heaven7.core.adapter;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by heaven7 on 2015/10/10.
 */
public class HeaderFooterManager {

    private List<View> mHeaders ;
    private List<View> mFooter ;
    private List<Integer> mLayoutIds;

    public void addHeaderView(View v){
        if(mHeaders == null)
            mHeaders = new ArrayList<>();
        if(!mHeaders.contains(v))
             mHeaders.add(v);
    }
    /***
     * @return the index of v ,or -1 if not exist
     */
    public int removeHeaderView(View v){
        if(mHeaders!=null){
            int index = mHeaders.indexOf(v);
            mHeaders.remove(index);
            return index;
        }
        return -1;
    }
    public void addFooterView(View v){
        if(mFooter == null)
            mFooter = new ArrayList<>();
        if(!mFooter.contains(v))
             mFooter.add(v);
    }

    /***
     * @return the index of v or -1  if not exist
     */
    public int removeFooterView(View v){
        if(mFooter!=null){
            int index = mFooter.indexOf(v);
            mFooter.remove(index);
            return index;
        }
        return -1;
    }

    public boolean isInHeader(int rawPosition){
        int headerViewSize = getHeaderViewSize();
        return headerViewSize != 0 && rawPosition < headerViewSize;
    }

    /**
     * @param rawPosition   the raw position
     * @param dataSize     the size of datas ,often is List
     */
    public boolean isInFooter(int rawPosition,int dataSize){
        return getFooterViewSize() != 0 && rawPosition > getHeaderViewSize() + dataSize - 1;
    }

    public int getHeaderViewSize(){
        return mHeaders!=null ? mHeaders.size() :0;
    }
    public int getFooterViewSize(){
        return mFooter!=null ? mFooter.size() :0;
    }

    /**
     * @param index   the index of {@link #mHeaders}
     */
    public View getHeaderView(int index){
         return mHeaders!=null ? mHeaders.get(index) :null;
    }
    /**
     * @param index   the index of {@link #mHeaders}
     */
    public View getFooterView(int index){
         return mFooter!=null ? mFooter.get(index) :null;
    }

    /** record the layout id if need */
    public void recordLayoutId(int layoutId) {
        if(mLayoutIds == null)
            mLayoutIds = new ArrayList<>();
         if(!mLayoutIds.contains(layoutId)){
             mLayoutIds.add(layoutId);
         }
    }
    /** is the recorded layout id  */
    public boolean isLayoutIdInRecord(int layoutId){
        return mLayoutIds!=null && mLayoutIds.contains(layoutId);
    }

    /**
     * find the view in header or footer,or null if not.
     * @param position the position of adapter,must be header  or footer position of view */
    public View findView(int position,int dataSize) {
        if(isInHeader(position))
            return getHeaderView(position);
        if(isInFooter(position,dataSize)){
            int headerSize = getHeaderViewSize();
            // 8 - 2 -  5  =1 means get the second view of footer
            return getFooterView(position - headerSize - dataSize);
        }
        return null;
    }
}
