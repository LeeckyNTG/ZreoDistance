package cn.hzyc.im.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 全局变量或操作
 * 
 * @author ntg
 * 
 */
public class Global {

	/** 全局上下文对象 */
	public static Context mContext;
	
	/** 屏幕密度 */
	public static float mDensity;
	
	/** 屏幕宽度 */
	public static int mScreenWidth;
	
	/** 屏幕高度 */
	public static int mScreenHeight;
	
	/** 用来发送本地广播 */
	public static LocalBroadcastManager mBroadcastManager;
	
	/** Handler对象 */
	private static Handler mHandler = new Handler(Looper.getMainLooper()) {};
	
	/** 全局变量初始化 */
	public static void initialize(Context context) {
		mContext = context;
		initScreenSize();
		
		mBroadcastManager = LocalBroadcastManager.getInstance(mContext);
	}
	
	private static void initScreenSize() {
		DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
		mDensity = displayMetrics.density;
		mScreenWidth = displayMetrics.widthPixels;
		mScreenHeight = displayMetrics.heightPixels;
	}
	
	public static Handler getMainHandler() {
		return mHandler;
	}
	
	public static void runOnUiThread(Runnable runnable) {
		mHandler.post(runnable);
	}
	
	/**
	 * 判断当前是否在ui主线程中运行
	 */
	public static boolean isMainThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}

	public static int dp2px(int dp) {
		return (int) (mDensity * dp);
	}
	
	private static Toast mToast;
	
	public static void showToast(String message) {
		if (mToast == null) {
			mToast = Toast.makeText(mContext, "", Toast.LENGTH_LONG);
		}
		mToast.setText(message);
		mToast.show();
	}

	public static View inflate(int layoutId) {
		return LayoutInflater.from(mContext).inflate(layoutId, null);
	}

	/** 隐藏输入法面板 */
	public static void hideInputMethod(View view) {
		Context context = view.getContext();
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/** 显示输入法面板 */
	public static void showInputMethod(final EditText editText) {
		Global.getMainHandler().post(new Runnable() {

			@Override
			public void run() {
				editText.requestFocus();
				InputMethodManager inputManager = (InputMethodManager) editText
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(editText, 0);
			}
		});
	}
}
