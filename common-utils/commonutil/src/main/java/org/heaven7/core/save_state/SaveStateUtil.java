package org.heaven7.core.save_state;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.SparseArray;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

/*public*/ class SaveStateUtil {

        /** default is null / 0 */
        public static void doRestoreState(Bundle b, SaveFieldInfo info, Object holder) {
            try {  //default 0
                //no mapping return
                 if(b.get(info.saveField.value()) ==null){
                     return ;
                 }
                switch (info.type) {
                    //primetive
                    case BundleSupportType.BYTE:
                        info.field.set(holder, b.getByte(info.saveField.value()));
                        break;
                    case BundleSupportType.BYTE_ARRAY:
                        info.field.set(holder, b.getByteArray(info.saveField.value()));
                        break;
                    case BundleSupportType.SHORT:
                        info.field.set(holder, b.getShort(info.saveField.value()));
                        break;
                    case BundleSupportType.SHORT_ARRAY:
                        info.field.set(holder, b.getShortArray(info.saveField.value()));
                        break;
                    case BundleSupportType.INT:
                        info.field.set(holder, b.getInt(info.saveField.value()));
                        break;
                    case BundleSupportType.INT_ARRAY:
                        info.field.set(holder, b.getIntArray(info.saveField.value()));
                        break;
                    case BundleSupportType.BOOLEAN:
                        info.field.set(holder, b.getBoolean(info.saveField.value()));
                        break;
                    case BundleSupportType.BOOLEAN_ARRAY:
                        info.field.set(holder, b.getBooleanArray(info.saveField.value()));
                        break;
                    case BundleSupportType.DOUBLE:
                        info.field.set(holder, b.getDouble(info.saveField.value()));
                        break;
                    case BundleSupportType.DOUBLE_ARRAY:
                        info.field.set(holder, b.getDoubleArray(info.saveField.value()));
                        break;
                    case BundleSupportType.CHAR:
                        info.field.set(holder, b.getChar(info.saveField.value()));
                        break;
                    case BundleSupportType.CHAR_ARRAY:
                        info.field.set(holder, b.getCharArray(info.saveField.value()));
                        break;
                    case BundleSupportType.FLOAT:
                        info.field.set(holder, b.getFloat(info.saveField.value()));
                        break;
                    case BundleSupportType.FLOAT_ARRAY:
                        info.field.set(holder, b.getFloatArray(info.saveField.value()));
                        break;
                    case BundleSupportType.LONG:
                        info.field.set(holder, b.getLong(info.saveField.value()));
                        break;
                    case BundleSupportType.LONG_ARRAY:
                        info.field.set(holder, b.getLongArray(info.saveField.value()));
                        break;
                    //==================================

                    case BundleSupportType.STRING:
                        info.field.set(holder, b.getString(info.saveField.value()));
                        break;
                    case BundleSupportType.STRING_ARRAY:
                        info.field.set(holder, b.getStringArray(info.saveField.value()));
                        break;
                    case BundleSupportType.CHAR_SEQUENCE:
                        info.field.set(holder, b.getCharSequence(info.saveField.value()));
                        break;
                    case BundleSupportType.CHAR_SEQUENCE_ARRAY:
                        info.field.set(holder, b.getCharSequenceArray(info.saveField.value()));
                        break;
                    case BundleSupportType.PARCELABLE:
                        info.field.set(holder, b.getParcelable(info.saveField.value()));
                        break;
                    case BundleSupportType.PARCELABLE_ARRAY:
                        info.field.set(holder, b.getParcelableArray(info.saveField.value()));
                        break;
                    //================================
                     /*  BundleSupportType.IBINDER,
                            BundleSupportType.BUNDLE,
                            BundleSupportType.SERIALIZABLE,
                            BundleSupportType.SPARSE_PARCELABLE_ARRAY,*/
                    case BundleSupportType.IBINDER:
                        //api-18
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                            info.field.set(holder, b.getBinder(info.saveField.value()));
                        break;
                    case BundleSupportType.BUNDLE:
                        info.field.set(holder, b.getBundle(info.saveField.value()));
                        break;
                    case BundleSupportType.SERIALIZABLE:
                        info.field.set(holder, b.getSerializable(info.saveField.value()));
                        break;
                    case BundleSupportType.SPARSE_PARCELABLE_ARRAY:
                        info.field.set(holder, b.getSparseParcelableArray(info.saveField.value()));
                        break;
                    // ----------------------------------------------
                 /*   BundleSupportType.INTEGER_ARRAY_lIST,
                            BundleSupportType.STRING_ARRAY_LIST,
                            BundleSupportType.PARCELABLE_ARRAY_LIST,
                            BundleSupportType.PARCELABLE_LIST,
                            BundleSupportType.CHAR_SEQUENCE_ARRAY_LIST,*/
                    case BundleSupportType.INTEGER_ARRAY_lIST:
                        info.field.set(holder, b.getIntegerArrayList(info.saveField.value()));
                        break;
                    case BundleSupportType.STRING_ARRAY_LIST:
                        info.field.set(holder, b.getStringArrayList(info.saveField.value()));
                        break;
                    case BundleSupportType.PARCELABLE_ARRAY_LIST:
                        info.field.set(holder, b.getParcelableArrayList(info.saveField.value()));
                        break;
                    case BundleSupportType.PARCELABLE_LIST:
                        info.field.set(holder, b.getParcelableArrayList(info.saveField.value()));
                        break;
                    case BundleSupportType.CHAR_SEQUENCE_ARRAY_LIST:
                        info.field.set(holder, b.getCharSequenceArrayList(info.saveField.value()));
                        break;

                }
            }catch (Exception e) {
                throw new RuntimeException("Error to restore instance state ---> key = " + info.saveField.value(),e);
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public static void doSaveState(Bundle outState, SaveFieldInfo info, Object holder){
            try {
                if(info.field.get(holder) == null)
                    return;
                switch (info.type) {
                    //primetive
                    case BundleSupportType.BYTE:
                        outState.putByte(info.saveField.value(), info.field.getByte(holder));
                        break;
                    case BundleSupportType.BYTE_ARRAY:
                        outState.putByteArray(info.saveField.value(), (byte[]) info.field.get(holder));
                        break;
                    case BundleSupportType.SHORT:
                        outState.putShort(info.saveField.value(), info.field.getShort(holder));
                        break;
                    case BundleSupportType.SHORT_ARRAY:
                        outState.putShortArray(info.saveField.value(), (short[]) info.field.get(holder));
                        break;
                    case BundleSupportType.INT:
                        outState.putInt(info.saveField.value(), info.field.getInt(holder));
                        break;
                    case BundleSupportType.INT_ARRAY:
                        outState.putIntArray(info.saveField.value(), (int[]) info.field.get(holder));
                        break;
                    case BundleSupportType.BOOLEAN:
                        outState.putBoolean(info.saveField.value(), info.field.getBoolean(holder));
                        break;
                    case BundleSupportType.BOOLEAN_ARRAY:
                        outState.putBooleanArray(info.saveField.value(), (boolean[]) info.field.get(holder));
                        break;
                    case BundleSupportType.DOUBLE:
                        outState.putDouble(info.saveField.value(), info.field.getDouble(holder));
                        break;
                    case BundleSupportType.DOUBLE_ARRAY:
                        outState.putDoubleArray(info.saveField.value(), (double[]) info.field.get(holder));
                        break;
                    case BundleSupportType.CHAR:
                        outState.putChar(info.saveField.value(), info.field.getChar(holder));
                        break;
                    case BundleSupportType.CHAR_ARRAY:
                        outState.putCharArray(info.saveField.value(), (char[]) info.field.get(holder));
                        break;
                    case BundleSupportType.FLOAT:
                        outState.putFloat(info.saveField.value(), info.field.getFloat(holder));
                        break;
                    case BundleSupportType.FLOAT_ARRAY:
                        outState.putFloatArray(info.saveField.value(), (float[]) info.field.get(holder));
                        break;
                    case BundleSupportType.LONG:
                        outState.putLong(info.saveField.value(), info.field.getLong(holder));
                        break;
                    case BundleSupportType.LONG_ARRAY:
                        outState.putLongArray(info.saveField.value(), (long[]) info.field.get(holder));
                        break;
                    //==================================

                    case BundleSupportType.STRING:
                        outState.putString(info.saveField.value(), (String) info.field.get(holder));
                        break;
                    case BundleSupportType.STRING_ARRAY:
                        outState.putStringArray(info.saveField.value(), (String[]) info.field.get(holder));
                        break;
                    case BundleSupportType.CHAR_SEQUENCE:
                        outState.putCharSequence(info.saveField.value(), (CharSequence) info.field.get(holder));
                        break;
                    case BundleSupportType.CHAR_SEQUENCE_ARRAY:
                        outState.putCharSequenceArray(info.saveField.value(), (CharSequence[]) info.field.get(holder));
                        break;
                    case BundleSupportType.PARCELABLE:
                        outState.putParcelable(info.saveField.value(), (Parcelable) info.field.get(holder));
                        break;
                    case BundleSupportType.PARCELABLE_ARRAY:
                        outState.putParcelableArray(info.saveField.value(), (Parcelable[]) info.field.get(holder));
                        break;
                    //================================
                     /*  BundleSupportType.IBINDER,
                            BundleSupportType.BUNDLE,
                            BundleSupportType.SERIALIZABLE,
                            BundleSupportType.SPARSE_PARCELABLE_ARRAY,*/
                    case BundleSupportType.IBINDER:
                        //api-18
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                            outState.putBinder(info.saveField.value(), (IBinder) info.field.get(holder));
                        break;
                    case BundleSupportType.BUNDLE:
                        outState.putBundle(info.saveField.value(), (Bundle) info.field.get(holder));
                        break;
                    case BundleSupportType.SERIALIZABLE:
                        outState.putSerializable(info.saveField.value(), (Serializable) info.field.get(holder));
                        break;
                    case BundleSupportType.SPARSE_PARCELABLE_ARRAY:
                        outState.putSparseParcelableArray(info.saveField.value(),
                                (SparseArray<? extends Parcelable>) info.field.get(holder));
                        break;
                    // ----------------------------------------------
                 /*   BundleSupportType.INTEGER_ARRAY_lIST,
                            BundleSupportType.STRING_ARRAY_LIST,
                            BundleSupportType.PARCELABLE_ARRAY_LIST,
                            BundleSupportType.PARCELABLE_LIST,
                            BundleSupportType.CHAR_SEQUENCE_ARRAY_LIST,*/
                    case BundleSupportType.INTEGER_ARRAY_lIST:
                        outState.putIntegerArrayList(info.saveField.value(), (ArrayList<Integer>) info.field.get(holder));
                        break;
                    case BundleSupportType.STRING_ARRAY_LIST:
                        outState.putStringArrayList(info.saveField.value(), (ArrayList<String>) info.field.get(holder));
                        break;
                    case BundleSupportType.PARCELABLE_ARRAY_LIST:
                        outState.putParcelableArrayList(info.saveField.value(),
                                (ArrayList<? extends Parcelable>) info.field.get(holder));
                        break;
                    case BundleSupportType.PARCELABLE_LIST:
                        outState.putParcelableArrayList(info.saveField.value(), new ArrayList<Parcelable>(
                                (Collection<? extends Parcelable>) info.field.get(holder)));
                        break;
                    case BundleSupportType.CHAR_SEQUENCE_ARRAY_LIST:
                        outState.putCharSequenceArrayList(info.saveField.value(), (ArrayList<CharSequence>) info.field.get(holder));
                        break;

                }
            }catch (Exception e) {
                throw new RuntimeException("Error to save instance state: key = " + info.saveField.value(),e);
            }
        }
        @BundleSupportTypeFlag
        public static int getFlag(Field f , @BundleSupportTypeFlag int flag) {
            Class<?> clazz = f.getType();
            if(Byte.TYPE.isAssignableFrom(clazz)){
                return  BundleSupportType.BYTE;
            }else if(Short.TYPE.isAssignableFrom(clazz)){
                return BundleSupportType.SHORT;
            } else if(Integer.TYPE.isAssignableFrom(clazz)){
                return BundleSupportType.INT;
            }else if(Boolean.TYPE.isAssignableFrom(clazz)){
                return BundleSupportType.BOOLEAN;
            }else if(Double.TYPE.isAssignableFrom(clazz)){
                return BundleSupportType.DOUBLE;
            }else if(Long.TYPE.isAssignableFrom(clazz)){
                return BundleSupportType.LONG;
            }else if(Character.TYPE.isAssignableFrom(clazz)){
                return BundleSupportType.CHAR;
            }else if(Float.TYPE.isAssignableFrom(clazz)){
                return BundleSupportType.FLOAT;
            }
            else if(String.class.isAssignableFrom(clazz)){
                return BundleSupportType.STRING;
            }else if(CharSequence.class.isAssignableFrom(clazz)){
                return BundleSupportType.CHAR_SEQUENCE;
            }else if(Parcelable.class.isAssignableFrom(clazz)){
                return BundleSupportType.PARCELABLE;
            }else if(Serializable.class.isAssignableFrom(clazz)){
                return BundleSupportType.SERIALIZABLE;
            }
            else if(IBinder.class.isAssignableFrom(clazz)){
                return BundleSupportType.IBINDER;
            }else if(Bundle.class.isAssignableFrom(clazz)){
                return BundleSupportType.BUNDLE;
            }
            // array
            else if(clazz.isArray()){
                Class<?> clazz2 = clazz.getComponentType();
                if (Byte.TYPE.isAssignableFrom(clazz2)){
                    return  BundleSupportType.BYTE_ARRAY;
                }else if (Short.TYPE.isAssignableFrom(clazz2)){
                    return BundleSupportType.SHORT_ARRAY;
                } else if(Integer.TYPE.isAssignableFrom(clazz2)){
                    return BundleSupportType.INT_ARRAY;
                }else if(Boolean.TYPE.isAssignableFrom(clazz2)){
                    return BundleSupportType.BOOLEAN_ARRAY;
                }else if(Double.TYPE.isAssignableFrom(clazz2)){
                    return BundleSupportType.DOUBLE_ARRAY;
                }else if(Long.TYPE.isAssignableFrom(clazz2)){
                    return BundleSupportType.LONG_ARRAY;
                }else if(Character.TYPE.isAssignableFrom(clazz2)){
                    return BundleSupportType.CHAR_ARRAY;
                }else if(Float.TYPE.isAssignableFrom(clazz2)){
                    return BundleSupportType.FLOAT_ARRAY;
                }
                else if(String.class.isAssignableFrom(clazz2)){
                    return BundleSupportType.STRING_ARRAY;
                }else if(CharSequence.class.isAssignableFrom(clazz2)){
                    return BundleSupportType.CHAR_SEQUENCE_ARRAY;
                }else if(Parcelable.class.isAssignableFrom(clazz2)){
                    return BundleSupportType.PARCELABLE_ARRAY;
                }
              /*  else{
                    Class<?>[] interfaces = clazz2.getInterfaces();
                    if(contains(interfaces, CharSequence.class)){
                        return BundleSupportType.CHAR_SEQUENCE_ARRAY;
                    }else if(contains(interfaces, Parcelable.class)){
                        return BundleSupportType.PARCELABLE_ARRAY;
                    }
                }*/
            }/*else {
                Class<?>[] interfaces = clazz.getInterfaces();
                if(contains(interfaces, Parcelable.class)){
                    return BundleSupportType.PARCELABLE;
                }else if(contains(interfaces, CharSequence.class)){
                    return BundleSupportType.CHAR_SEQUENCE;
                }else if(contains(interfaces, IBinder.class)){
                    return BundleSupportType.IBINDER;
                }else if(contains(interfaces, Serializable.class)){
                    return BundleSupportType.SERIALIZABLE;
                }
            }*/
            //list ?  must assign flag or throw RuntimeException
            switch (flag) {
                case BundleSupportType.INTEGER_ARRAY_lIST:
                case BundleSupportType.STRING_ARRAY_LIST:
                case BundleSupportType.PARCELABLE_ARRAY_LIST:
                case BundleSupportType.PARCELABLE_LIST:
                case BundleSupportType.CHAR_SEQUENCE_ARRAY_LIST:
                    // SparseArray<? extends Parcelable>
                case BundleSupportType.SPARSE_PARCELABLE_ARRAY:
                    return flag;
                default:
                    String extra = "flag = " + flag + " ,field_name = " + f.getName();
                    System.err.println(extra);
                    throw new RuntimeException("flag only can be the value in interface BundleSupportType." + extra);
            }
        }

        private static boolean contains(Class<?>[] interfaces, Class<?> clzz) {
            if(interfaces==null || interfaces.length==0)
                return false;
            for(Class<?> clazz : interfaces){
                if(clazz == clzz)
                    return true;
            }
            return false;
        }

    }
