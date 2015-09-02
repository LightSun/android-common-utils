package org.heaven7.demo;

import android.app.Application;

import com.android.volley.data.RequestManager;

/**
 * Created by heaven7 on 2015/9/2.
 */
public class TestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RequestManager.init(this);
    }
}
