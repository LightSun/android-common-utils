package org.heaven7.core.adapter;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.extra.ExpandNetworkImageView;
import com.android.volley.extra.RoundedBitmapBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * this is a troll image pager adapter .
 * Created by heaven7 on 2016/2/1.
 * @since 1.8.1
 */
public class NetworkImagePagerAdapter<T extends NetworkImagePagerAdapter.IImageLinkGetter>
        extends PagerAdapter {

    private List<T> datas;
    private List<ExpandNetworkImageView> mImageViews;
    private final int mPlaceHolder;
    private final int mErrorRes;

    public NetworkImagePagerAdapter(Context context, List<T> datas,
                                    @DrawableRes int placeHolderRes){
        this(context,datas,placeHolderRes,placeHolderRes);
    }

    public NetworkImagePagerAdapter(Context context, List<T> datas,
                                    @DrawableRes int placeHolderRes,@DrawableRes int errorRes) {
        this.mPlaceHolder = placeHolderRes;
        this.mErrorRes = errorRes;
        this.datas = datas;
        this.mImageViews = new ArrayList<>();
        for(int i=0,size = datas.size() ; i<size ;i++){
            final ExpandNetworkImageView iv = new ExpandNetworkImageView(context);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mImageViews.add(iv);
        }
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int index = position % datas.size();
        final ExpandNetworkImageView iv = mImageViews.get(index);
        String url = datas.get(index).getImageUrl();
        new RoundedBitmapBuilder().url(url)
                .placeholder(mPlaceHolder)
                .error(mErrorRes)
                .into(iv);
        container.addView(iv);
        return iv;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
       // container.removeView((View) object);
        ExpandNetworkImageView iv = mImageViews.get(position % datas.size());
        container.removeView(iv);
    }

    public interface IImageLinkGetter{
        String getImageUrl();
    }
}
