package org.heaven7.core.helper;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heaven7 on 2015/10/10.
 */
public class BundleHelper {

    private final Bundle b = new Bundle();

    public BundleHelper putInt(String key,int val){
        b.putInt(key,val);
        return this;
    }
    public BundleHelper putString(String key,String val){
        b.putString(key, val);
        return this;
    }
    public BundleHelper putFloat(String key,float val){
        b.putFloat(key, val);
        return this;
    }
    public BundleHelper putLong(String key,long val){
        b.putLong(key, val);
        return this;
    }
    public BundleHelper putDouble(String key,double val){
        b.putDouble(key, val);
        return this;
    }

    public BundleHelper putSerializable(String key,Serializable data){
        b.putSerializable(key, data);
        return this;
    }
    public BundleHelper putParcelable(String key,Parcelable data){
        b.putParcelable(key, data);
        return this;
    }

    public Bundle getBundle(){
        return b;
    }

    public <T extends Fragment>T into(T fragment){
        fragment.setArguments(getBundle());
        return fragment;
    }
    public <T extends android.app.Fragment>T into(T fragment){
        fragment.setArguments(getBundle());
        return fragment;
    }

    public BundleHelper putStringArrayList(String key,List<String> imageUrls) {
        b.putStringArrayList(key, imageUrls != null && imageUrls.size() > 0
                ? new ArrayList<>(imageUrls) : null);
        return this;
    }

    public BundleHelper putBoolean(String key , boolean value) {
        b.putBoolean(key ,value);
        return this;
    }
}
