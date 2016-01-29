package org.heaven7.core.util;

import android.os.Handler;
import android.os.Looper;

import java.lang.ref.WeakReference;

/**
 * Created by heaven7 on 2016/1/26.
 */
public abstract class WeakHandler<T> extends Handler {

    private final WeakReference<T> mWeakRef;

    public WeakHandler(T t) {
        this.mWeakRef = new WeakReference<T>(t);
    }

    public WeakHandler(Looper looper,T t) {
        super(looper);
        this.mWeakRef = new WeakReference<T>(t);
    }

    public T get(){
        return mWeakRef.get();
    }

}
