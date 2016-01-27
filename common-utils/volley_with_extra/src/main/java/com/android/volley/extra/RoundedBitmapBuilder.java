package com.android.volley.extra;

import com.android.volley.data.RequestManager;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;

public final class RoundedBitmapBuilder {

	// private final Resources mResources;
	private final DisplayMetrics mDisplayMetrics;

	private float[] mCornerRadii = new float[] { 0, 0, 0, 0 };

	private boolean mOval = false;
	private float mBorderWidth = 0;
	private ColorStateList mBorderColor = ColorStateList
			.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
	private ImageView.ScaleType mScaleType = ImageView.ScaleType.CENTER_CROP;
	
	private Bitmap mSourceBitmap;
	private boolean mCircle;
	
	private String mUrl;
	private int mErrorResId;
	private int mPlaceHolderResId;

	private TileMode mTileModeX = TileMode.CLAMP;
	private TileMode mTileModeY = TileMode.CLAMP;

	public RoundedBitmapBuilder() {
		mDisplayMetrics = Resources.getSystem().getDisplayMetrics();
	}

	public RoundedBitmapBuilder scaleType(ImageView.ScaleType scaleType) {
		mScaleType = scaleType;
		return this;
	}

	/**
	 * Set corner radius for all corners in px.
	 * 
	 * @param radius
	 *            the radius in px
	 * @return the builder for chaining.
	 */
	public RoundedBitmapBuilder cornerRadius(float radius) {
		mCornerRadii[Corner.TOP_LEFT] = radius;
		mCornerRadii[Corner.TOP_RIGHT] = radius;
		mCornerRadii[Corner.BOTTOM_RIGHT] = radius;
		mCornerRadii[Corner.BOTTOM_LEFT] = radius;
		return this;
	}

	/**
	 * Set corner radius for a specific corner in px.
	 * 
	 * @param corner
	 *            the corner to set.
	 * @param radius
	 *            the radius in px.
	 * @return the builder for chaning.
	 */
	public RoundedBitmapBuilder cornerRadius(int corner, float radius) {
		mCornerRadii[corner] = radius;
		return this;
	}

	/**
	 * Set corner radius for all corners in density independent pixels.
	 * 
	 * @param radius
	 *            the radius in density independent pixels.
	 * @return the builder for chaining.
	 */
	public RoundedBitmapBuilder cornerRadiusDp(float radius) {
		return cornerRadius(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, radius, mDisplayMetrics));
	}

	/**
	 * Set corner radius for a specific corner in density independent pixels.
	 * 
	 * @param corner
	 *            the corner to set
	 * @param radius
	 *            the radius in density independent pixels.
	 * @return the builder for chaining.
	 */
	public RoundedBitmapBuilder cornerRadiusDp(int corner, float radius) {
		return cornerRadius(corner, TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, radius, mDisplayMetrics));
	}

	/**
	 * Set the border width in pixels.
	 * 
	 * @param width
	 *            border width in pixels.
	 * @return the builder for chaining.
	 */
	public RoundedBitmapBuilder borderWidth(float width) {
		mBorderWidth = width;
		return this;
	}

	/**
	 * Set the border width in density independent pixels.
	 * 
	 * @param width
	 *            border width in density independent pixels.
	 * @return the builder for chaining.
	 */
	public RoundedBitmapBuilder borderWidthDp(float width) {
		mBorderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				width, mDisplayMetrics);
		return this;
	}

	/**
	 * Set the border color.
	 * 
	 * @param color
	 *            the color to set.
	 * @return the builder for chaining.
	 */
	public RoundedBitmapBuilder borderColor(int color) {
		mBorderColor = ColorStateList.valueOf(color);
		return this;
	}

	/**
	 * Set the border color as a {@link ColorStateList}.
	 * 
	 * @param colors
	 *            the {@link ColorStateList} to set.
	 * @return the builder for chaining.
	 */
	public RoundedBitmapBuilder borderColor(ColorStateList colors) {
		mBorderColor = colors;
		return this;
	}

	/**
	 * Sets whether the image should be oval or not.
	 * 
	 * @param oval
	 *            if the image should be oval.
	 * @return the builder for chaining.
	 */
	public RoundedBitmapBuilder oval(boolean oval) {
		mOval = oval;
		return this;
	}
	/** if set this to true , {@link #cornerRadius(float)} will be ignored */
	public RoundedBitmapBuilder circle(boolean circle){
		this.mCircle = circle;
		return this;
	}
	
	public RoundedBitmapBuilder from(Bitmap source){
		this.mSourceBitmap = source;
		return this;
	}
	
	public RoundedBitmapBuilder from(Drawable drawable){
		if(drawable instanceof RoundedDrawable){
			this.mSourceBitmap = ((RoundedDrawable)drawable).getSourceBitmap();
		}else{
		   this.mSourceBitmap = RoundedDrawable.drawableToBitmap(drawable);
		}
		return this;
	}
	
    public RoundedBitmapBuilder url(String url){
    	this.mUrl = url; 
    	return this;
    }
    
    public RoundedBitmapBuilder placeholder(int placeHolderResId){
    	this.mPlaceHolderResId = placeHolderResId;
    	return this;
    }
    /**
     * use {@link #placeholder(int)} instead
     * @param placeHolderResId
     * @return
     */
    @Deprecated
    public RoundedBitmapBuilder placeHolder(int placeHolderResId){
    	this.mPlaceHolderResId = placeHolderResId;
    	return this;
    }
    public RoundedBitmapBuilder error(int errorResId){
    	this.mErrorResId = errorResId;
    	return this;
    }
    
    public RoundedBitmapBuilder tileMode(TileMode titleModeX,TileMode titleModeY){
    	if(titleModeX!=null)
    	    this.mTileModeX = titleModeX;
    	if(titleModeY!=null)
    	    this.mTileModeY = titleModeY;
    	return this;
    }
	
	public RoundedBitmapBuilder into(ExpandNetworkImageView iv){
		iv.setScaleType(mScaleType);
		iv.setBorderColor(mBorderColor);
		iv.setBorderWidth(mBorderWidth);
		iv.setCornerRadius(mCornerRadii[0], mCornerRadii[1],
				mCornerRadii[2], mCornerRadii[3]);
		iv.setOval(mOval);
		iv.setCircle(mCircle);
		iv.setTileModeX(mTileModeX);
		iv.setTileModeY(mTileModeY);
		if(mSourceBitmap!=null)
		    iv.setImageBitmap(mSourceBitmap);
		else{
			if(mErrorResId!=0)
			    iv.setErrorImageResId(mErrorResId);
			if(mPlaceHolderResId!=0)
				iv.setDefaultImageResId(mPlaceHolderResId);
			if(mUrl != null){
				iv.setImageUrl(mUrl, RequestManager.getImageLoader());
			}
		}
		return this;
	}

	public Bitmap buildBitmap() {
		return RoundedDrawable
				.fromBitmap(mSourceBitmap)
				.setScaleType(mScaleType)
				.setCornerRadius(mCornerRadii[0], mCornerRadii[1],
						mCornerRadii[2], mCornerRadii[3])
				.setBorderWidth(mBorderWidth).setBorderColor(mBorderColor)
				.setTileModeX(mTileModeX)
				.setTileModeY(mTileModeY)
				.setOval(mOval).toBitmap();
	}
	public RoundedDrawable buildDrawable() {
		return RoundedDrawable
				.fromBitmap(mSourceBitmap)
				.setScaleType(mScaleType)
				.setCornerRadius(mCornerRadii[0], mCornerRadii[1],
						mCornerRadii[2], mCornerRadii[3])
						.setBorderWidth(mBorderWidth).setBorderColor(mBorderColor)
						.setOval(mOval)
						.setTileModeX(mTileModeX)
						.setTileModeY(mTileModeY);
	}

}
