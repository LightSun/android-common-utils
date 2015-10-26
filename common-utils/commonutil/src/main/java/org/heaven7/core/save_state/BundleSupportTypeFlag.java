package org.heaven7.core.save_state;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef(value = {
        BundleSupportType.BYTE,
        BundleSupportType.BYTE_ARRAY,
        BundleSupportType.FLOAT,
        BundleSupportType.FLOAT_ARRAY,
        BundleSupportType.INT,
        BundleSupportType.INT_ARRAY,
        BundleSupportType.LONG,
        BundleSupportType.LONG_ARRAY,
        BundleSupportType.SHORT,
        BundleSupportType.SHORT_ARRAY,
        BundleSupportType.CHAR,
        BundleSupportType.CHAR_ARRAY,
        BundleSupportType.BOOLEAN,
        BundleSupportType.BOOLEAN_ARRAY,
        BundleSupportType.DOUBLE,
        BundleSupportType.DOUBLE_ARRAY,

        BundleSupportType.STRING,
        BundleSupportType.STRING_ARRAY,
        BundleSupportType.CHAR_SEQUENCE,
        BundleSupportType.CHAR_SEQUENCE_ARRAY,
        BundleSupportType.PARCELABLE,
        BundleSupportType.PARCELABLE_ARRAY,

        BundleSupportType.IBINDER,
        BundleSupportType.BUNDLE,
        BundleSupportType.SERIALIZABLE,
        BundleSupportType.SPARSE_PARCELABLE_ARRAY,

        BundleSupportType.INTEGER_ARRAY_lIST,
        BundleSupportType.STRING_ARRAY_LIST,
        BundleSupportType.PARCELABLE_ARRAY_LIST,
        BundleSupportType.PARCELABLE_LIST,
        BundleSupportType.CHAR_SEQUENCE_ARRAY_LIST,
})
@Retention(RetentionPolicy.CLASS)
public @interface BundleSupportTypeFlag {
}
