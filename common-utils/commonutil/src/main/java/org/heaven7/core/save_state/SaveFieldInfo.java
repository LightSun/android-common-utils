package org.heaven7.core.save_state;

import java.lang.reflect.Field;

/*public*/ class SaveFieldInfo {
    Field field;
    SaveStateField saveField;
    int type;

    public SaveFieldInfo(Field f, SaveStateField saveField, int type) {
        this.field = f;
        this.saveField = saveField;
        this.type = type;
    }
}
