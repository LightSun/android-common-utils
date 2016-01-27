package com.android.volley.extra.util;

import android.util.SparseIntArray;

import java.util.List;

public final class RestfulHelper {

    static final SparseIntArray sMap;

    static {
        sMap = new SparseIntArray();
    }

    private RestfulHelper() {
    }

    public static void regist(String urlPattern, @MethodDef int method) {
        sMap.put(urlPattern.hashCode(), method);
    }

    /**
     * like http://192.168.0.125:8080/lifeTime-Web/api/province/{provinceId}.json
     *
     * @param params 有序
     */
    //like http://192.168.0.125:8080/lifeTime-Web/api/province/{provinceId}.json
    public static String buildRestfulUrl(String urlPattern, Pairs.Pair... params) {
        if (params == null || params.length == 0)
            return urlPattern;

        for (int i = 0, size = params.length; i < size; i++) {
            urlPattern = urlPattern.replace("{" + params[i].name + "}", params[i].value);
        }
        return urlPattern;
    }

    public static String buildRestfulUrl(String urlPattern, List<Pairs.Pair> params) {
        if (params == null || params.size() == 0)
            return urlPattern;

        Pairs.Pair pair;
        for (int i = 0, size = params.size(); i < size; i++) {
            pair = params.get(i);
            urlPattern = urlPattern.replace("{" + pair.name + "}", pair.value);
        }
        return urlPattern;
    }
}
