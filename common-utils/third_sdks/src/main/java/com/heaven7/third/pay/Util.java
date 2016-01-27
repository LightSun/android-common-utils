package com.heaven7.third.pay;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by heaven7 on 2015/9/16.
 */

/*public*/ class Util {

    public static String urlEncode(String src){
        try {
            return URLEncoder.encode(src, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 将Bitmap转化为字节数组
     * @param bitmap  源图片
     * @return byte[] 字节数组
     */
    public static byte[] bitampToByteArray(Bitmap bitmap) {
        byte[] array = null;
        ByteArrayOutputStream os = null;
        try {
            if (null != bitmap) {
                os = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
                array = os.toByteArray();
            }
        } finally {
            IoUtil.closeQuietly(os);
        }
        return array;
    }
}
