package cn.hzyc.im.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreUtil {
	private static final String FILE_NAME = "config";
	
	/**
	 * 获取布局型变量的值
	 * @param context 
	 * @param key 
	 * @param defValue 获取不到时，给定的默认的值
	 * @return
	 */
	public static boolean getBoolean(Context context, String key, boolean defValue) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);	
		return sp.getBoolean(key, defValue);
	}
	
	/**
	 * 保存boolean变量
	 */
	public static void saveBoolean(Context context, String key, boolean value) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);	
		sp.edit().putBoolean(key, value).commit();
	}
	
	
	/**
	 * 获取字符串型变量的值
	 * @param context 
	 * @param key 
	 * @param defValue 获取不到时，给定的默认的值
	 * @return
	 */
	public static String getString(Context context, String key, String defValue) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);	
		return sp.getString(key, defValue);
	}

	/**
	 * 保存字符串变量
	 */
	public static void saveString(Context context, String key, String value) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);	
		sp.edit().putString(key, value).commit();
	}
}
