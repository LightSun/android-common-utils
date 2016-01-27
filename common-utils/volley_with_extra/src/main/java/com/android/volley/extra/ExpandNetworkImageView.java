package com.android.volley.extra;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

/**
 * Handles fetching an image from a URL as well as the life-cycle of the
 * associated request. <li>based on volley NetWorkImageView <li>note method:
 * {@linkplain #setDefaultImageResId(int)},
 * {@linkplain #setErrorImageResId(int)} ,
 * {@linkplain #setImageParam(ImageParam)},
 * {@linkplain #setImageUrl(String, ImageLoader)}
 * 
 * @author heaven7
 */
public class ExpandNetworkImageView extends ImageView {

	/** The URL of the network image to load */
	private String mUrl;

	/**
	 * Resource ID of the image to be used as a placeholder until the network
	 * image is loaded.
	 */
	private int mDefaultImageId;

	/**
	 * Resource ID of the image to be used if the network response fails.
	 */
	private int mErrorImageId;

	/** Local copy of the ImageLoader. */
	private ImageLoader mImageLoader;

	/** Current ImageContainer. (either in-flight or finished) */
	private ImageContainer mImageContainer;


	// ========== from https://github.com/vinc3m1/RoundedImageView
	// (he override from imageView)=========
	// Constants for tile mode attributes
	@SuppressWarnings("unused")
	private static final int TILE_MODE_UNDEFINED = -2;
	private static final int TILE_MODE_CLAMP = 0;
	private static final int TILE_MODE_REPEAT = 1;
	private static final int TILE_MODE_MIRROR = 2;

	public static final String TAG = "RoundedImageView";

	public static final float DEFAULT_RADIUS = 0f;
	public static final float DEFAULT_BORDER_WIDTH = 0f;
	
	public static final Shader.TileMode DEFAULT_TILE_MODE = Shader.TileMode.CLAMP;

	@SuppressWarnings("unused")
	private static final ScaleType[] SCALE_TYPES = { ScaleType.MATRIX,
			ScaleType.FIT_XY, ScaleType.FIT_START, ScaleType.FIT_CENTER,
			ScaleType.FIT_END, ScaleType.CENTER, ScaleType.CENTER_CROP,
			ScaleType.CENTER_INSIDE };

	private final float[] mCornerRadii = new float[] { DEFAULT_RADIUS,
			DEFAULT_RADIUS, DEFAULT_RADIUS, DEFAULT_RADIUS };

	private Drawable mBackgroundDrawable;
	private ColorStateList mBorderColor = ColorStateList
			.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
	private float mBorderWidth = DEFAULT_BORDER_WIDTH;
	private ColorFilter mColorFilter = null;
	private boolean mColorMod = false;
	private Drawable mDrawable;
	private boolean mHasColorFilter = false;
	private boolean mIsOval = false;
	private boolean mMutateBackground = false;
	private int mResource;
	private ScaleType mScaleType = ScaleType.FIT_CENTER;
	private Shader.TileMode mTileModeX = DEFAULT_TILE_MODE;
	private Shader.TileMode mTileModeY = DEFAULT_TILE_MODE;
	private boolean mCircle;
	
	
	// ================= end ====================

	public ExpandNetworkImageView(Context context) {
		this(context, null);
	}

	public ExpandNetworkImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ExpandNetworkImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context,attrs);
	}

	private void init(Context context, AttributeSet attrs) {
	      // default scaletype to FIT_CENTER
	     setScaleType(ScaleType.FIT_CENTER);
	}
	

	public boolean isCircle() {
		return mCircle;
	}

	public void setCircle(boolean circle) {
		this.mCircle = circle;
		if(circle){
			getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
				@Override
				public boolean onPreDraw() {
					setCornerRadius(getWidth()/2);
					getViewTreeObserver().removeOnPreDrawListener(this);
					return true;
				}
			});
		}
		
	}

	/**
	 * Sets URL of the image that should be loaded into this view. Note that
	 * calling this will immediately either set the cached image (if available)
	 * or the default image specified by
	 * {@link ExpandNetworkImageView#setDefaultImageResId(int)} on the view.
	 * 
	 * NOTE: If applicable,
	 * {@link ExpandNetworkImageView#setDefaultImageResId(int)} and
	 * {@link ExpandNetworkImageView#setErrorImageResId(int)} should be called
	 * prior to calling this function.
	 * 
	 * @param url
	 *            The URL that should be loaded into this ImageView.
	 * @param imageLoader
	 *            ImageLoader that will be used to make the request.
	 */
	public void setImageUrl(String url, ImageLoader imageLoader) {
		mUrl = url;
		mImageLoader = imageLoader;
		// The URL has potentially changed. See if we need to load it.
		loadImageIfNecessary(false);
	}

	/**
	 * Sets the default image resource ID to be used for this view until the
	 * attempt to load it completes.(placeHolder)
	 */
	public void setDefaultImageResId(int defaultImage) {
		mDefaultImageId = defaultImage;
	}

	/**
	 * Sets the error image resource ID to be used for this view in the event
	 * that the image requested fails to load.
	 */
	public void setErrorImageResId(int errorImage) {
		mErrorImageId = errorImage;
	}

	/**
	 * Loads the image for the view if it isn't already loaded.
	 * 
	 * @param isInLayoutPass
	 *            True if this was invoked from a layout pass, false otherwise.
	 */
	void loadImageIfNecessary(final boolean isInLayoutPass) {
		/*
		 *  may cause some bug . so comment it.
		 *  eg: in PinnedHeaderExpandableListView's HeaderView
		 */
		/*try {
			new URL(mUrl);
		} catch (MalformedURLException e) {
			setImageResource(mErrorImageId);
			return;
		}*/
		int width = getWidth();
		int height = getHeight();

		boolean wrapWidth = false, wrapHeight = false;
		if (getLayoutParams() != null) {
			wrapWidth = getLayoutParams().width == LayoutParams.WRAP_CONTENT;
			wrapHeight = getLayoutParams().height == LayoutParams.WRAP_CONTENT;
		}

		// if the view's bounds aren't known yet, and this is not a
		// wrap-content/wrap-content
		// view, hold off on loading the image.
		boolean isFullyWrapContent = wrapWidth && wrapHeight;
		if (width == 0 && height == 0 && !isFullyWrapContent) {
			return;
		}

		// if the URL to be loaded in this view is empty, cancel any old
		// requests and clear the
		// currently loaded image.
		if (TextUtils.isEmpty(mUrl)) {
			setDefaultImageIfNeed();
			return;
		}

		// if there was an old request in this view, check if it needs to be
		// canceled.
		if (mImageContainer != null && mImageContainer.getRequestUrl() != null) {
			if (mImageContainer.getRequestUrl().equals(mUrl)) {
				// if the request is from the same URL, return.
				return;
			} else {
				// if there is a pre-existing request, cancel it if it's
				// fetching a different URL.
				mImageContainer.cancelRequest();
				setDefaultImageOrNull();
			}
		}

		// Calculate the max image width / height to use while ignoring
		// WRAP_CONTENT dimens.
		int maxWidth = wrapWidth ? 0 : width;
		int maxHeight = wrapHeight ? 0 : height;

		// The pre-existing content of this view didn't match the current URL.
		// Load the new image
		// from the network.
		ImageContainer newContainer = mImageLoader.get(mUrl,
				new ImageListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (mErrorImageId != 0) {
							setImageResource(mErrorImageId);
						}
					}

					@Override
					public void onResponse(final ImageContainer response,
							boolean isImmediate) {
						// If this was an immediate response that was delivered
						// inside of a layout
						// pass do not set the image immediately as it will
						// trigger a requestLayout
						// inside of a layout. Instead, defer setting the image
						// by posting back to
						// the main thread.
						if (isImmediate && isInLayoutPass) {
							post(new Runnable() {
								@Override
								public void run() {
									onResponse(response, false);
								}
							});
							return;
						}

						if (response.getBitmap() != null) {
							setImageBitmap(response.getBitmap());
						} else if (mDefaultImageId != 0) {
							 setImageResource(mDefaultImageId);
						}
					}
				}, maxWidth, maxHeight);

		// update the ImageContainer to be the new bitmap container.
		mImageContainer = newContainer;
	}

	private void setDefaultImageIfNeed() {
		if (mImageContainer != null) {
			mImageContainer.cancelRequest();
			mImageContainer = null;
		}
		setDefaultImageOrNull();
	}

	protected void applyBitmap(Bitmap bitmap) {
		setImageBitmap(bitmap);
	}

	private void setDefaultImageOrNull() {
		if (mDefaultImageId != 0) {
			setImageResource(mDefaultImageId);
		} else {
			setImageBitmap(null);
		}
	}
	
     

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if(!TextUtils.isEmpty(mUrl))
		      loadImageIfNecessary(true);
	}

	@Override
	protected void onDetachedFromWindow() {
		if (mImageContainer != null) {
			// If the view was bound to an image request, cancel it and clear
			// out the image from the view.
			mImageContainer.cancelRequest();
			setImageBitmap(null);
			// also clear out the container so we can reload the image if
			// necessary.
			mImageContainer = null;
		}
		super.onDetachedFromWindow();
	}
	
	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		invalidate();
	}

	public static Shader.TileMode parseTileMode(int tileMode) {
		switch (tileMode) {
		case TILE_MODE_CLAMP:
			return Shader.TileMode.CLAMP;
		case TILE_MODE_REPEAT:
			return Shader.TileMode.REPEAT;
		case TILE_MODE_MIRROR:
			return Shader.TileMode.MIRROR;
		default:
			return null;
		}
	}

	@Override
	public ScaleType getScaleType() {
		return mScaleType;
	}

	@Override
	public void setScaleType(ScaleType scaleType) {
		assert scaleType != null;

		if (mScaleType != scaleType) {
			mScaleType = scaleType;

			switch (scaleType) {
			case CENTER:
			case CENTER_CROP:
			case CENTER_INSIDE:
			case FIT_CENTER:
			case FIT_START:
			case FIT_END:
			case FIT_XY:
				super.setScaleType(ScaleType.FIT_XY);
				break;
			default:
				super.setScaleType(scaleType);
				break;
			}

			updateDrawableAttrs();
			updateBackgroundDrawableAttrs(false);
			invalidate();
		}
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		mResource = 0;
		mDrawable = RoundedDrawable.fromDrawable(drawable);
		updateDrawableAttrs();
		super.setImageDrawable(mDrawable);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		mResource = 0;
		mDrawable = RoundedDrawable.fromBitmap(bm);
		updateDrawableAttrs();
		super.setImageDrawable(mDrawable);
	}

	@Override
	public void setImageResource(int resId) {
		if (mResource != resId) {
			mResource = resId;
			mDrawable = resolveResource();
			updateDrawableAttrs();
			super.setImageDrawable(mDrawable);
		}
	}

	@Override
	public void setImageURI(Uri uri) {
		super.setImageURI(uri);
		setImageDrawable(getDrawable());
	}

	private Drawable resolveResource() {
		Resources rsrc = getResources();
		if (rsrc == null) {
			return null;
		}

		Drawable d = null;

		if (mResource != 0) {
			try {
				d = rsrc.getDrawable(mResource);
			} catch (Exception e) {
				Log.w(TAG, "Unable to find resource: " + mResource, e);
				// Don't try again.
				mResource = 0;
			}
		}
		return RoundedDrawable.fromDrawable(d);
	}

	@Override
	public void setBackground(Drawable background) {
		setBackgroundDrawable(background);
	}

	private void updateDrawableAttrs() {
		updateAttrs(mDrawable);
	}

	private void updateBackgroundDrawableAttrs(boolean convert) {
		if (mMutateBackground) {
			if (convert) {
				mBackgroundDrawable = RoundedDrawable
						.fromDrawable(mBackgroundDrawable);
			}
			updateAttrs(mBackgroundDrawable);
		}
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		if (mColorFilter != cf) {
			mColorFilter = cf;
			mHasColorFilter = true;
			mColorMod = true;
			applyColorMod();
			invalidate();
		}
	}

	private void applyColorMod() {
		// Only mutate and apply when modifications have occurred. This should
		// not reset the mColorMod flag, since these filters need to be
		// re-applied if the Drawable is changed.
		if (mDrawable != null && mColorMod) {
			mDrawable = mDrawable.mutate();
			if (mHasColorFilter) {
				mDrawable.setColorFilter(mColorFilter);
			}
			// TODO: support, eventually...
			// mDrawable.setXfermode(mXfermode);
			// mDrawable.setAlpha(mAlpha * mViewAlphaScale >> 8);
		}
	}

	private void updateAttrs(Drawable drawable) {
		if (drawable == null) {
			return;
		}

		if (drawable instanceof RoundedDrawable) {
			((RoundedDrawable) drawable).setScaleType(mScaleType)
					.setBorderWidth(mBorderWidth).setBorderColor(mBorderColor)
					.setOval(mIsOval).setTileModeX(mTileModeX)
					.setTileModeY(mTileModeY);

			if (mCornerRadii != null) {
				((RoundedDrawable) drawable).setCornerRadius(
						mCornerRadii[Corner.TOP_LEFT],
						mCornerRadii[Corner.TOP_RIGHT],
						mCornerRadii[Corner.BOTTOM_RIGHT],
						mCornerRadii[Corner.BOTTOM_LEFT]);
			}

			applyColorMod();
		} else if (drawable instanceof LayerDrawable) {
			// loop through layers to and set drawable attrs
			LayerDrawable ld = ((LayerDrawable) drawable);
			for (int i = 0, layers = ld.getNumberOfLayers(); i < layers; i++) {
				updateAttrs(ld.getDrawable(i));
			}
		}
	}

	@Override
	@Deprecated
	public void setBackgroundDrawable(Drawable background) {
		mBackgroundDrawable = background;
		updateBackgroundDrawableAttrs(true);
		super.setBackgroundDrawable(mBackgroundDrawable);
	}

	/**
	 * @return the largest corner radius.
	 */
	public float getCornerRadius() {
		return getMaxCornerRadius();
	}

	/**
	 * @return the largest corner radius.
	 */
	public float getMaxCornerRadius() {
		float maxRadius = 0;
		for (float r : mCornerRadii) {
			maxRadius = Math.max(r, maxRadius);
		}
		return maxRadius;
	}

	/**
	 * Get the corner radius of a specified corner.
	 * 
	 * @param corner
	 *            the corner.
	 * @return the radius.
	 */
	public float getCornerRadius(@Corner int corner) {
		return mCornerRadii[corner];
	}

	/**
	 * Set all the corner radii from a dimension resource id.
	 * 
	 * @param dimenResId
	 *            dimension resource id of radii.
	 */
	public void setCornerRadiusDimen(int dimenResId) {
		float radius = getResources().getDimension(dimenResId);
		setCornerRadius(radius, radius, radius, radius);
	}

	/**
	 * Set the corner radius of a specific corner from a dimension resource id.
	 * 
	 * @param corner
	 *            the corner to set.
	 * @param dimenResId
	 *            the dimension resource id of the corner radius.
	 */
	public void setCornerRadiusDimen(@Corner int corner, int dimenResId) {
		setCornerRadius(corner, getResources()
				.getDimensionPixelSize(dimenResId));
	}

	/**
	 * Set the corner radii of all corners in px.
	 * 
	 * @param radius
	 *            the radius to set.
	 */
	public void setCornerRadius(float radius) {
		setCornerRadius(radius, radius, radius, radius);
	}

	/**
	 * Set the corner radius of a specific corner in px.
	 * 
	 * @param corner
	 *            the corner to set.
	 * @param radius
	 *            the corner radius to set in px.
	 */
	public void setCornerRadius(@Corner int corner, float radius) {
		if (mCornerRadii[corner] == radius) {
			return;
		}
		mCornerRadii[corner] = radius;

		updateDrawableAttrs();
		updateBackgroundDrawableAttrs(false);
		invalidate();
	}

	/**
	 * Set the corner radii of each corner individually. Currently only one
	 * unique nonzero value is supported.
	 * 
	 * @param topLeft
	 *            radius of the top left corner in px.
	 * @param topRight
	 *            radius of the top right corner in px.
	 * @param bottomRight
	 *            radius of the bottom right corner in px.
	 * @param bottomLeft
	 *            radius of the bottom left corner in px.
	 */
	public void setCornerRadius(float topLeft, float topRight,
			float bottomLeft, float bottomRight) {
		if (mCornerRadii[Corner.TOP_LEFT] == topLeft
				&& mCornerRadii[Corner.TOP_RIGHT] == topRight
				&& mCornerRadii[Corner.BOTTOM_RIGHT] == bottomRight
				&& mCornerRadii[Corner.BOTTOM_LEFT] == bottomLeft) {
			return;
		}

		mCornerRadii[Corner.TOP_LEFT] = topLeft;
		mCornerRadii[Corner.TOP_RIGHT] = topRight;
		mCornerRadii[Corner.BOTTOM_LEFT] = bottomLeft;
		mCornerRadii[Corner.BOTTOM_RIGHT] = bottomRight;

		updateDrawableAttrs();
		updateBackgroundDrawableAttrs(false);
		invalidate();
	}

	public float getBorderWidth() {
		return mBorderWidth;
	}

	public void setBorderWidth(int dimenResId) {
		setBorderWidth(getResources().getDimension(dimenResId));
	}

	public void setBorderWidth(float width) {
		if (mBorderWidth == width) {
			return;
		}

		mBorderWidth = width;
		updateDrawableAttrs();
		updateBackgroundDrawableAttrs(false);
		invalidate();
	}

	@ColorInt
	public int getBorderColor() {
		return mBorderColor.getDefaultColor();
	}

	public void setBorderColor(@ColorInt int color) {
		setBorderColor(ColorStateList.valueOf(color));
	}

	public ColorStateList getBorderColors() {
		return mBorderColor;
	}

	public void setBorderColor(ColorStateList colors) {
		if (mBorderColor.equals(colors)) {
			return;
		}

		mBorderColor = (colors != null) ? colors : ColorStateList
				.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
		updateDrawableAttrs();
		updateBackgroundDrawableAttrs(false);
		if (mBorderWidth > 0) {
			invalidate();
		}
	}

	public boolean isOval() {
		return mIsOval;
	}

	public void setOval(boolean oval) {
		mIsOval = oval;
		updateDrawableAttrs();
		updateBackgroundDrawableAttrs(false);
		invalidate();
	}

	public Shader.TileMode getTileModeX() {
		return mTileModeX;
	}

	public void setTileModeX(Shader.TileMode tileModeX) {
		if (tileModeX==null || this.mTileModeX == tileModeX) {
			return;
		}

		this.mTileModeX = tileModeX;
		updateDrawableAttrs();
		updateBackgroundDrawableAttrs(false);
		invalidate();
	}

	public Shader.TileMode getTileModeY() {
		return mTileModeY;
	}

	public void setTileModeY(Shader.TileMode tileModeY) {
		if (tileModeY==null || this.mTileModeY == tileModeY) {
			return;
		}

		this.mTileModeY = tileModeY;
		updateDrawableAttrs();
		updateBackgroundDrawableAttrs(false);
		invalidate();
	}

	public boolean mutatesBackground() {
		return mMutateBackground;
	}

	public void mutateBackground(boolean mutate) {
		if (mMutateBackground == mutate) {
			return;
		}

		mMutateBackground = mutate;
		updateBackgroundDrawableAttrs(true);
		invalidate();
	}
	
	public static interface LoadListener{
		
	}

}
