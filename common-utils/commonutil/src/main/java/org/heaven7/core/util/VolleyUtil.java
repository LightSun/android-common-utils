package org.heaven7.core.util;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.data.ApiParams;
import com.android.volley.data.RequestManager;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * Created by heaven7 on 2015/8/12.
 */
public class VolleyUtil {

    private static final String TAG = "VolleyUtil";
    private static final boolean sDebug = true;

    /**
     * Created by heaven7 on 2015/8/12.
     */
    public static class HttpExecutor {
        public HttpExecutor() {
        }
        public void get(final String url,ApiParams params,final VolleyUtil.HttpCallback callback){
            VolleyUtil.get(url,params,this,callback);
        }
        public void get(final String url,final VolleyUtil.HttpCallback callback){
            VolleyUtil.get(url,this,callback);
        }
        public void post(final String url,ApiParams params,final VolleyUtil.HttpCallback callback){
            VolleyUtil.post(url, params, this, callback);
        }
        public void cancelAll(){
            VolleyUtil.cancelAll(this);
        }
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
        executeRequest(new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        logIfNeed(url, response);
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
        }, tag);
    }
    public static void get(final String url,ApiParams params,Object tag,final HttpCallback callback){
        if(params == null ||params.isEmpty()){
            get(url, tag, callback);
        }else{
            get(buildGetUrl(url, params), tag, callback);
        }
    }

    /**
     * get 请求返回 string
     */
    public static void get(final String url, Object tag, final HttpCallback callback) {
        //	ImageRequest
        executeRequest(new StringRequest(Request.Method.GET, url,
                                   new Response.Listener<String>() {
                                       @Override
                                       public void onResponse(String response) {
                                           logIfNeed(url, response);
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
                       }, tag);
    }

    private static void logIfNeed(String url, String response) {
        if (sDebug)
            Logger.w(TAG, "url = " + url, "response =" + response);
    }

    /**
     * @param tag used to cancel request
     */
    private static <T> void executeRequest(Request<T> request, Object tag) {
        RequestManager.addRequest(request, tag);
    }

    public static void cancelAll(Object tag) {
        RequestManager.cancelAll(tag);
    }


    public interface HttpCallback {
        /**
         * http 访问成功
         */
        void onResponse(String url, String response);

        void onErrorResponse(String url, VolleyError error);

    }

}
