package org.heaven7.core.adapter;

/** this class help to make single swipe operation.
 * Created by heaven7 on 2016/1/29.
 * @since 1.8.0
 */
/*public*/ class SimpleSwipeStateChangeListener implements SwipeHelper.OnSwipeStateChangeListener {

    private SwipeHelper.ISwipeManager mLastSm;

    public SimpleSwipeStateChangeListener() {
    }

    @Override
    public void onSwipeStateChange(SwipeHelper.ISwipeManager swipeManager, int swipeState) {
          switch (swipeState){
              case SwipeHelper.STATE_CLOSE:
                  //ignore
                  break;

              case SwipeHelper.STATE_OPNE:
                  //close previous
                 if(mLastSm != null){
                     mLastSm.close();
                 }
                  mLastSm = swipeManager;
                  break;

              default:
                  throw  new RuntimeException("can't reach here");
          }
    }

    /**
     * return true when try to close the opened swipe item success . */
    public boolean closeSwipeIfNeed(){
        if(mLastSm != null){
            mLastSm.close();
            mLastSm = null;
            return true;
        }
        return false;
    }
}
