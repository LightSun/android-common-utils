package com.android.volley.extra;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;

/*public*/ class Util {
	
	/** 圆角 */
	public static Bitmap getRCB(Bitmap bitmap, float roundPX) {
		if (bitmap == null || roundPX <= 0) {
			return bitmap;
		}
		Bitmap dstbmp = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(dstbmp);
		Paint paint = new Paint();
		RectF rectF = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		Path mPath = new Path();
		float[] mCorner = new float[] { roundPX, roundPX, roundPX, roundPX,
				roundPX, roundPX, roundPX, roundPX };
		mPath.addRoundRect(rectF, mCorner, Path.Direction.CW);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawPath(mPath, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return dstbmp;
	}

	/** 圆形图片 */
	public static Bitmap toCircleBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}
	
	/*public static Bitmap toCircleBitmap(Bitmap bitmap,int borderColor,float borderWidth) {
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx; //中心点
		float left, top, right, bottom, 
		dst_left, dst_top, dst_right, dst_bottom;
		
		if (width <= height) {
			roundPx = width / 2;
			
			top = 0;
			left = 0;
			bottom = width;
			right = width;
			
			height = width;
			
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			
			left = clip;
			top = 0;
			right = width - clip;
			bottom = height;
			
			width = height;
			
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		if(borderWidth != 0){
			paint.setColor(borderColor);
			//paint.setStrokeWidth(borderWidth);
			//canvas.drawCircle(roundPx, roundPx, roundPx, paint);
			canvas.drawCircle(roundPx, roundPx, roundPx + borderWidth, paint);
		}else{
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		}
 
		//paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		//canvas.drawBitmap(bitmap, src, dst, paint);
		
		BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
		paint.setShader(shader);
		canvas.drawCircle(roundPx, roundPx, roundPx - borderWidth, paint);
		
		return output;
	}*/
}
