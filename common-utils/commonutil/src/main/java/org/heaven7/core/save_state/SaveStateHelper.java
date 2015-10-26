package org.heaven7.core.save_state;

import android.os.Bundle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SaveStateHelper {

    private final List<SaveFieldInfo> mSaveInfos;
    private final Object mHolder;

    /**
     * @param holder may be activity or fragment
     */
    public SaveStateHelper(Object holder) {
        this.mHolder = holder;
        mSaveInfos = new ArrayList<>();
        init(holder);
    }

    private void init(Object holder) {
        Field[] fields = holder.getClass().getDeclaredFields();
        if (fields == null || fields.length == 0)
            return;
        List<SaveFieldInfo> infos = this.mSaveInfos;
        SaveStateField sf;
        for (int i = 0, size = fields.length; i < size; i++) {
            Field f = fields[i];
            f.setAccessible(true);
            sf = f.getAnnotation(SaveStateField.class);
            if (sf == null)
                continue;
            infos.add(new SaveFieldInfo(f, sf, SaveStateUtil.getFlag(f, sf.flag())));
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        List<SaveFieldInfo> mSaveInfos = this.mSaveInfos;
        SaveFieldInfo info;
        Object holder = this.mHolder;
        for (int i = 0, size = mSaveInfos.size(); i < size; i++) {
            info = mSaveInfos.get(i);
            SaveStateUtil.doSaveState(outState, info, holder);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null)
            return;
        List<SaveFieldInfo> mSaveInfos = this.mSaveInfos;
        SaveFieldInfo info;
        Object holder = this.mHolder;
        for (int i = 0, size = mSaveInfos.size(); i < size; i++) {
            info = mSaveInfos.get(i);
            SaveStateUtil.doRestoreState(savedInstanceState, info, holder);
        }
    }
}
