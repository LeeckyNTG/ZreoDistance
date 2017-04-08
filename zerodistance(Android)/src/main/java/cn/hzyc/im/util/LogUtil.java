package cn.hzyc.im.util;

import android.util.Log;

public class LogUtil {
	
	private static final boolean mDebug = true;
	
	private static final String TAG = "msg";

	/** 在控制台打印信息 */
	public static void i(String msg) {
		i(TAG, msg);
	}

	public static void d(String msg) {
		d(TAG, msg);
	}

	public static void w(String msg) {
		w(TAG, msg);
	}

	public static void v(String msg) {
		v(TAG, msg);
	}

	public static void e(String msg) {
		e(TAG, msg);
	}

	public static void i(String tag, String msg) {
		String mess = getLogPrefix() + msg;
		if (mDebug)
			Log.i(tag, mess);
	}

	public static void d(String tag, String msg) {
		String mess = getLogPrefix() + msg;
		if (mDebug)
			Log.d(tag, mess);
	}

	public static void w(String tag, String msg) {
		String mess = getLogPrefix() + msg;
		if (mDebug)
			Log.w(tag, mess);
	}

	public static void v(String tag, String msg) {
		String mess = getLogPrefix() + msg;
		if (mDebug)
			Log.v(tag, mess);
	}

	public static void e(String tag, String msg) {
		String mess = getLogPrefix() + msg;
		if (mDebug)
			Log.e(tag, mess);
	}

	/** 为了方便查看，在打印的日志信息内容前添加一个序号前缀 */
	private static String getLogPrefix() {
		logId++;
		if (logId >= 1000)
			logId = 1;
		return "(" + logId + "). ";
	}

	private static int logId = 0;
}
