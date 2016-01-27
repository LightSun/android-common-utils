package com.android.volley.extra;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
 /** 
  * MultipartRequest，返回的结果是String格式的
 * @author mrsimple
 */
public class MultipartRequest extends Request<String> {
 
	private MultipartEntity mMultiPartEntity = new MultipartEntity();
	private Response.Listener<String> l;
	private boolean mDebug;
 
    public MultipartRequest(int method, String url,Response.Listener<String> l, Response.ErrorListener listener) {
        super(method, url, listener);
        this.l = l;
    }
 
    public boolean isDebug() {
		return mDebug;
	}
	public void setDebug(boolean mDebug) {
		this.mDebug = mDebug;
	}

	public MultipartEntity getMultiPartEntity() {
        return mMultiPartEntity;
    }
    @Override
    public String getBodyContentType() {
        return mMultiPartEntity.getContentType().getValue();
    }
 
    @Override
    public byte[] getBody() {
 
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            // 将mMultiPartEntity中的参数写入到bos中
            mMultiPartEntity.writeTo(bos);
            if(mDebug){
            	Log.i("getBody", "[ body ]: "+bos.toString());
            }
        } catch (IOException e) {
            Log.e("getBody", "IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }
 
    @Override
    protected void deliverResponse(String response) {
        l.onResponse(response);
    }
 
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
 
}