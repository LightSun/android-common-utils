package com.android.volley.extra.util;

import com.android.volley.Request;

/***
 * wrap volley's request method
 */
public interface Method{
        int GET     = Request.Method.GET + 1;
        int POST    = Request.Method.POST + 1;
        int PUT     = Request.Method.PUT + 1;
        int DELETE  = Request.Method.DELETE + 1;
        int HEAD    = Request.Method.HEAD + 1;
        int OPTIONS = Request.Method.OPTIONS + 1;
        int TRACE   = Request.Method.TRACE + 1;
        int PATCH   = Request.Method.PATCH + 1;
}
