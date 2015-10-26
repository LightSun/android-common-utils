package org.heaven7.core.save_state;

/**
 * constant
 */
public interface BundleSupportType {
    int IBINDER = 1;     // must >= api-18
    int BUNDLE = 2;

    int BYTE = 3;
    int BYTE_ARRAY = 4;
    int FLOAT = 5;
    int FLOAT_ARRAY = 6;
    int INT = 7;
    int INT_ARRAY = 8;
    int SHORT = 9;
    int SHORT_ARRAY = 10;
    int CHAR = 11;
    int CHAR_ARRAY = 12;
    int LONG = 13;
    int LONG_ARRAY = 14;
    int BOOLEAN = 16;
    int BOOLEAN_ARRAY = 17;
    int DOUBLE = 18;
    int DOUBLE_ARRAY = 19;

    int INTEGER_ARRAY_lIST = 15;

    int STRING = 20;
    int STRING_ARRAY = 21;
    int STRING_ARRAY_LIST = 22;

    int CHAR_SEQUENCE = 31;
    int CHAR_SEQUENCE_ARRAY = 32;
    int CHAR_SEQUENCE_ARRAY_LIST = 33;

    int PARCELABLE = 34;
    int PARCELABLE_ARRAY_LIST = 35;
    int PARCELABLE_LIST = 36;
    int PARCELABLE_ARRAY = 37;

    int SERIALIZABLE = 38;
    int SPARSE_PARCELABLE_ARRAY = 39;
}
