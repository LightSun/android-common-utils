package com.heaven7.core;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

/**
 * Created by heaven7 on 2015/9/15.
 */
public class ImageParser {

    public interface IDecoder{
        Bitmap decode(DecodeParam param, BitmapFactory.Options options);
    }
    /** decode param */
    public class DecodeParam{
        public String pathName;
        public byte[] imageArray;
        public int resId ; //image resource id
        public Resources resources;

        public DecodeParam(String pathName) {
            this.pathName = pathName;
        }
        public DecodeParam(byte[] imageArray) {
            this.imageArray = imageArray;
        }
        public DecodeParam(Resources resources,int resId) {
            this.resId = resId;
            this.resources = resources;
        }
    }
    private static final IDecoder sPathDecoder = new IDecoder() {
        @Override
        public Bitmap decode(DecodeParam param, BitmapFactory.Options options) {
            return BitmapFactory.decodeFile(param.pathName,options);
        }
    };
    private static final IDecoder sResourceDecoder = new IDecoder() {
        @Override
        public Bitmap decode(DecodeParam param, BitmapFactory.Options options) {
            return BitmapFactory.decodeResource(param.resources,param.resId,options);
        }
    };
    private static final IDecoder sByteArrayDecoder = new IDecoder() {
        @Override
        public Bitmap decode(DecodeParam param, BitmapFactory.Options options) {
            final byte[] bytes = param.imageArray;
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
        }
    };

    /** Decoding lock so that we don't decode more than one image at a time (to avoid OOM's) */
    private static final Object sDecodeLock = new Object();

    private final int mMaxWidth;
    private final int mMaxHeight;
    private final Bitmap.Config mDecodeConfig;

    public ImageParser(int mMaxWidth, int mMaxHeight){
        this(mMaxWidth,mMaxHeight, Bitmap.Config.RGB_565);
    }
    public ImageParser(int mMaxWidth, int mMaxHeight, Bitmap.Config config) {
        this.mMaxWidth = mMaxWidth;
        this.mMaxHeight = mMaxHeight;
        this.mDecodeConfig = config;
    }

    public Bitmap parseToBitmap(String pathName){
        File file = new File(pathName);
        if (!file.exists()) {
            return null;
        }
        return decodeToBitmap(sPathDecoder,new DecodeParam(pathName));
    }
    private Bitmap decodeToBitmap(IDecoder decoder ,DecodeParam param){
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        Bitmap bitmap;
        if (mMaxWidth == 0 && mMaxHeight == 0) {
            decodeOptions.inPreferredConfig = mDecodeConfig;
            synchronized (sDecodeLock) {
                bitmap = decoder.decode(param, decodeOptions);
            }
        } else {
            // If we have to resize this image, first get the natural bounds.
            decodeOptions.inJustDecodeBounds = true;
            synchronized (sDecodeLock) {
                decoder.decode(param, decodeOptions); //just decode bounds
            }
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;

            // Then compute the dimensions we would ideally like to decode to.
            int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
                    actualWidth, actualHeight);
            int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
                    actualHeight, actualWidth);

            // Decode to the nearest power of two scaling factor.
            decodeOptions.inJustDecodeBounds = false;
            // TODO(ficus): Do we need this or is it okay since API 8 doesn't support it?
            // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
            decodeOptions.inSampleSize =
                    findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
            Bitmap tempBitmap;
            synchronized (sDecodeLock) {
                tempBitmap = decoder.decode(param, decodeOptions);
            }
            // If necessary, scale down to the maximal acceptable size.
            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap,
                        desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }
        return bitmap;
    }

    public Bitmap parseToBitmap(byte[] data){
        return decodeToBitmap(sByteArrayDecoder,new DecodeParam(data));
    }
    public Bitmap parseToBitmap(Context context, int resId){
        return decodeToBitmap(sResourceDecoder,new DecodeParam(context.getResources(),resId));
    }

    /**
     * Scales one side of a rectangle to fit aspect ratio.
     *
     * @param maxPrimary Maximum size of the primary dimension (i.e. width for
     *        max width), or zero to maintain aspect ratio with secondary
     *        dimension
     * @param maxSecondary Maximum size of the secondary dimension, or zero to
     *        maintain aspect ratio with primary dimension
     * @param actualPrimary Actual size of the primary dimension
     * @param actualSecondary Actual size of the secondary dimension
     */
    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
                                           int actualSecondary) {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    /**
     * Returns the largest power-of-two divisor for use in downscaling a bitmap
     * that will not result in the scaling past the desired dimensions.
     *
     * @param actualWidth Actual width of the bitmap
     * @param actualHeight Actual height of the bitmap
     * @param desiredWidth Desired width of the bitmap
     * @param desiredHeight Desired height of the bitmap
     */
    // Visible for testing.
    static int findBestSampleSize(
            int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }
}
