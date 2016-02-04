package com.android.volley.extra.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.data.ApiParams;
import com.android.volley.data.RequestManager;
import com.android.volley.extra.ExpandNetworkImageView;
import com.android.volley.extra.RoundedBitmapBuilder;
import com.android.volley.toolbox.StringRequest;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by heaven7 on 2015/8/12.
 */
public class VolleyUtil {

    private static final String TAG = "VolleyUtil";
    private static final boolean sDebug = true;
    static final RetryPolicy sPolicy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    /**
     * Created by heaven7 on 2015/8/12.
     */
    public static class HttpExecutor {
        public HttpExecutor() {
        }

        public void get(final String url, ApiParams params, final VolleyUtil.HttpCallback callback) {
            VolleyUtil.get(url, params, this, callback);
        }

        /** server use --- > web service */
        public void getByWebService(String url, String[] params, VolleyUtil.HttpCallback callback) {
            VolleyUtil.get(buildWebServiceUrl(url, params), this, callback);
        }
        /** server use --- > web service */
        public void getByWebService(String url, List<String> params, VolleyUtil.HttpCallback callback) {
            VolleyUtil.get(buildWebServiceUrl(url, params), this, callback);
        }

        public void get(final String url, final VolleyUtil.HttpCallback callback) {
            VolleyUtil.get(url, this, callback);
        }

        public void post(final String url, ApiParams params, final VolleyUtil.HttpCallback callback) {
            VolleyUtil.post(url, params, this, callback);
        }

        public void loadImage(String url, ExpandNetworkImageView imageView, RoundedBitmapBuilder builder) {
            VolleyUtil.loadImage(url, imageView, builder);
        }
        /**
         * send  a request to server and callback. this only can be used by restful style.
         * <li>call this method you need to regist the url's method {@link RestfulHelper#regist(String, int)}</li>
         * @param urlPattern the url like "http://xxx:0808/{province}.json"
         * @param pairs the url pairs,can be null if not need
         * @param bodyParams  the request body params, can be null.
         * @param callback the callback
         */
       public void sendRequest(String urlPattern, List<Pairs.Pair> pairs ,ApiParams bodyParams,
                                VolleyUtil.HttpCallback callback){
            int wrapedMethod;
            if((wrapedMethod = RestfulHelper.sMap.get(urlPattern.hashCode())) == 0 ){
                throw new RuntimeException("you must regist method by call #regist(String,String) method");
            }
            VolleyUtil.sendRequest(wrapedMethod - 1, RestfulHelper.buildRestfulUrl(urlPattern, pairs),
                      bodyParams, this, callback);
        }
        /**
         * send  a request to server and callback.this only can be used by restful style.
         * @param method see {@link Method}
         * @param urlPattern the url like "http://xxx:0808/{province}.json"
         * @param pairs the url pairs,can be null if not need
         * @param bodyParams  the request body params, can be null.
         * @param callback the callback
         */
       public void sendRequest(@MethodDef int method, String urlPattern, List<Pairs.Pair> pairs ,ApiParams bodyParams,
                                VolleyUtil.HttpCallback callback){
            VolleyUtil.sendRequest(method - 1, RestfulHelper.buildRestfulUrl(urlPattern, pairs),
                    bodyParams, this, callback);
        }

        public void cancelAll() {
            VolleyUtil.cancelAll(this);
        }
    }

    /**
     *  baseurl/value1/value2...
     * 由于后台才用restful 框架(post和get 处理相同)，get请求的url拼接不能才用传统的方式。
     */
    public static String buildWebServiceUrl(String baseUrl, String... params) {
        if (params == null || params.length == 0) {
            return baseUrl;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);
        for (int i = 0, size = params.length; i < size; i++) {
            String param = params[i];
            param = URLEncoder.encode(param);
            sb.append("/")
                    .append(TextUtils.isEmpty(param) ? "%20" : param);
        }
        return sb.toString();
    }

    public static String buildWebServiceUrl(String baseUrl, List<String> params) {
        if (params == null || params.size() == 0) {
            return baseUrl;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);
        for (int i = 0, size = params.size(); i < size; i++) {
            String param = params.get(i);
            param =  URLEncoder.encode(param);
            sb.append("/")
                    .append(TextUtils.isEmpty(param) ? "%20" : param);
        }
        return sb.toString();
    }

    public static String buildGetUrl(String baseUrl, ApiParams params) {
        if (params.isEmpty())
            return baseUrl;
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        for (Map.Entry<String, String> en : params.entrySet()) {
            sb.append(en.getKey())
                    .append("=")
                    .append(en.getValue())
                    .append("&");
        }
        sb.deleteCharAt(sb.length() - 1);//delete last "&"
        return baseUrl + sb.toString();
    }

    public static void post(final String url, final ApiParams params, Object tag, final HttpCallback callback) {
        sendRequest(Request.Method.POST, url, params, tag, callback);
    }

    public static void get(final String url, ApiParams params, Object tag, final HttpCallback callback) {
        if (params == null || params.isEmpty()) {
            get(url, tag, callback);
        } else {
            get(buildGetUrl(url, params), tag, callback);
        }
    }

    /**
     * send  a request to server and callback
     *
     * @param method   the method ,see {@link com.android.volley.Request.Method}
     * @param url      the url
     * @param params   the request params, can be null.
     * @param tag      the tag to cancel
     * @param callback the callback
     */
    public static void sendRequest(int method, final String url, final ApiParams params, Object tag,
                                   final VolleyUtil.HttpCallback callback) {
        StringRequest request = new StringRequest(method, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        logIfNeed(url,response);
                        callback.onResponse(url, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        logIfNeed(url, error.getMessage());
                        callback.onErrorResponse(url, error);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        executeRequest(request,tag);
    }

    /**
     * get 请求返回 string
     */
    public static void get(final String url, Object tag, final HttpCallback callback) {
       sendRequest(Request.Method.GET, url, null, tag, callback);
    }

    public static void loadImage(String url, ExpandNetworkImageView imageView, RoundedBitmapBuilder builder) {
        builder.url(url).into(imageView);
    }

    private static void logIfNeed(String url, String response) {
        if (sDebug)
            Log.w(TAG, "url = " + url + ", response =" + response);
    }

    /**
     * @param tag used to cancel request
     */
    private static <T> void executeRequest(Request<T> request, Object tag) {
        request.setRetryPolicy(sPolicy);
        RequestManager.addRequest(request, tag);
    }

    public static void cancelAll(Object tag) {
        RequestManager.cancelAll(tag);
    }
    public static void init(Context context){
        RequestManager.init(context);
    }

    public interface HttpCallback {
        /**
         * http 访问成功
         */
        void onResponse(String url, String response);

        void onErrorResponse(String url, VolleyError error);

    }

}
