package org.heaven7.core.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * helper class for SharedPreferences,if you want edit more data once you should 
 * use {@link SPEditor}
 * @author chenjun
 */
public class SPHelper {
	
	private static final String FILENAME="heaven_framework.db";
	
	// ============= 兼容旧的  ================//
	public static void putValue(Context context, String key, Boolean value){
		putBoolean(context, key, value);
	}
	public static void putValue(Context context, String key, String value){
		putString(context, key, value);
	}
	public static String getValue(Context context, String key, String defValue){
		return getString(context, key, defValue);
	}
	public static String getValue(Context context, String key){
		return getString(context, key, "");
	}
	//===============================//
	
	public static void putString(Context context,String key, String value) {
		getSp(context).edit().putString(key, value).apply();
	}
	public static void putInt(Context context,String key, int value) {
		getSp(context).edit().putInt(key, value).apply();
	}
	public static void putFloat(Context context,String key, float value) {
		getSp(context).edit().putFloat(key, value).apply();
	}

	public static void putBoolean(Context context, String key, boolean value) {
		getSp(context).edit().putBoolean(key, value).apply();
	}
	public static void putLong(Context context, String key,long value){
		getSp(context).edit().putLong(key, value).apply();
	}
	// =========== begin  multi progress =================== //
	public static String getStringByMulti(Context context,String key,String defValue){
		return getMultiSp(context).getString(key, defValue);
	}
	public static int getIntByMulti(Context context,String key,int defValue){
		return getMultiSp(context).getInt(key, defValue);
	}
	public static boolean getBooleanByMulti(Context context,String key,boolean defValue){
		return getMultiSp(context).getBoolean(key, defValue);
	}
	public static long getLongByMulti(Context context,String key,long defValue){
		return getMultiSp(context).getLong(key, defValue);
	}
	public static float getLongByMulti(Context context,String key,float defValue){
		return getMultiSp(context).getFloat(key, defValue);
	}
	
	// =========== end  multi =================== //

	public static String getString(Context context,String key) {
		return getSp(context).getString(key, "");
	}
	
	public static String getString(Context context,String key,String defaultValue) {
		return getSp(context).getString(key, defaultValue);
	}
	
	public static boolean getBoolean(Context context,String key,boolean defaultValue) {
		return getSp(context).getBoolean(key, defaultValue);
	}	
	
	public static int getInt(Context context,String key,int defValue) {
		return getSp(context).getInt(key, defValue);
	}	
	public static long getLong(Context context,String key,long defaultValue) {
		return getSp(context).getLong(key, defaultValue);
	}	
	public static float getFloat(Context context,String key,float defaultValue) {
		return getSp(context).getFloat(key, defaultValue);
	}
	
	private static SharedPreferences getSp(Context context) {
		return context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
	}	
	private static SharedPreferences getMultiSp(Context context) {
		return context.getSharedPreferences(FILENAME, Context.MODE_MULTI_PROCESS);
	}	

	public static void clear(Context context) {
		getSp(context).edit().clear().apply();
	}

	public static Map<String,?> getAllByFileName(Context context){
		return getSp(context).getAll();
	}
	
	public static SPEditor newEditor(Context context){
		return new SPEditor(context);
	}
	/**
	 * To improve efficiency for SharedPreferences while you put many value-values to it.
	 * <p>you must call {@link #begin()} to begin edit,and {@link #commit()} to commit</p>
	 * @author heaven
	 */
	public static class SPEditor{
		final Context context;
		final String filename;
		SharedPreferences.Editor mEditor;

		public SPEditor(String filename,Context context) {
			super();
			this.context = context;
			this.filename =filename;
		}
		public SPEditor(Context context) {
			this(FILENAME,context);
		}
		
		public SPEditor beginMultiProgress(){
			mEditor = context.getSharedPreferences(filename, Context.MODE_MULTI_PROCESS).edit();
			return this;
		}
		
		public SPEditor begin(){
			mEditor = context.getSharedPreferences(filename, Context.MODE_PRIVATE).edit();
			return this;
		}
		
		public SPEditor putInt(String key,int value){
			mEditor.putInt(key, value);
			return this;
		}
		public SPEditor putLong(String key,long value){
			mEditor.putLong(key, value);
			return this;
		}
		public SPEditor putString(String key,String value){
			mEditor.putString(key, value);
			return this;
		}
		public SPEditor putFloat(String key,float value){
			mEditor.putFloat(key, value);
			return this;
		}
		public SPEditor putBoolean(String key,boolean value){
			mEditor.putBoolean(key, value);;
			return this;
		}
		/**
		 * @return true , if successed and the editor will be null, so you cann't continue to edit
		 * (you must recall {@link #begin()} to edit it again). 
		 * <li>But, if failed ,the Editor is still exist, so you can continue to put value-values
		 * until {@link #commit()} successed.
		 */
		public boolean commit(){
			boolean result = mEditor.commit();
			if(result) mEditor = null;
			return result;
		}
		
		public void apply(){
			mEditor.apply();
		}
	}
}
