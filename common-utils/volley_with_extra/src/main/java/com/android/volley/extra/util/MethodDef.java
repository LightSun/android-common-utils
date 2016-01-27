package com.android.volley.extra.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef(value = {
        Method.DELETE,
        Method.GET,
        Method.POST,
        Method.PUT,
        Method.HEAD,
        Method.OPTIONS,
        Method.PATCH,
        Method.TRACE,
})
@Retention(RetentionPolicy.CLASS)
public @interface MethodDef {
}
