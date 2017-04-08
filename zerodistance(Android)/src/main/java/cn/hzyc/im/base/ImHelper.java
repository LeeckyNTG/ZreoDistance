package cn.hzyc.im.base;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import cn.em.sdk.base.EaseUI;
import cn.em.sdk.manager.EaseNotifier;
import cn.em.sdk.manager.PreferenceManager;
import cn.hzyc.im.manager.ConnectionManager;
import cn.hzyc.im.manager.ContactManager;
import cn.hzyc.im.manager.IMEventManager;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;

/**
 * 环信im帮助类
 */
public class ImHelper {
	private static ImHelper instance = new ImHelper();
	private boolean alreadyNotified;
	
	private static EaseNotifier mEaseNotifier;

	private ImHelper() {
	}

	public static ImHelper getInstance() {
		return instance;
	}


	//初始化环信
	public void initialize() {
		if (EaseUI.getInstance().init(Global.mContext) ) {
			// debugMode == true 时为打开，sdk 会在log里输入调试信息
			// 在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题
			EMChat.getInstance().setDebugMode(true);
			mEaseNotifier = new EaseNotifier().init(Global.mContext);
			PreferenceManager.init(Global.mContext);
			ContactManager.getInstance().initialize();
			ConnectionManager.getInstance().initialize();
			IMEventManager.getInstance().initialize();
		}
	}
	
	
	public static EaseNotifier getEaseNotifier() {
		return mEaseNotifier;
	}
	
	/**
	 * 是否登录成功过
	 *
	 * @return
	 */
	public boolean isLoggedIn() {
		return EMChat.getInstance().isLoggedIn();
	}
	/**
	 * 获取当前用户的环信id
	 */
	public String getCurrentUsernName() {
		return PreferenceManager.getInstance()
					.getCurrentUsername();
	}

	/**
	 * 设置当前用户的环信id
	 * 
	 * @param username
	 */
	public void setCurrentUserName(String username) {
		PreferenceManager.getInstance().setCurrentUserName(username);
	}

	/**
	 * 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
	 */
	public synchronized void notifyForRecevingEvents() {
		if (alreadyNotified) {
			return;
		}
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
		alreadyNotified = true;
	}
	
    /**
     * 用来记录注册了eventlistener的foreground Activity
     */
    private List<Activity> activityList = new ArrayList<Activity>();
    
    public void pushActivity(Activity activity){
        if(!activityList.contains(activity)){
            activityList.add(0,activity); 
        }
    }
    
    public void popActivity(Activity activity){
        activityList.remove(activity);
    }
    
    /**
     * 是否是在前台运行，元素个数不为空说明在前台运行
     */
    public boolean hasForegroundActivies(){
        return activityList.size() != 0;
    }
	
	synchronized void reset() {
	}
	
	/**
	 * 退出登录
	 * 
	 * @param unbindDeviceToken 是否解绑设备token(使用GCM才有)
	 * @param callback callback
	 */
	public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
		EMChatManager.getInstance().logout(unbindDeviceToken, new EMCallBack() {

			@Override
			public void onSuccess() {
				reset();
				if (callback != null) {
					callback.onSuccess();
				}
			}

			@Override
			public void onProgress(int progress, String status) {
				if (callback != null) {
					callback.onProgress(progress, status);
				}
			}

			@Override
			public void onError(int code, String error) {
				if (callback != null) {
					callback.onError(code, error);
				}
			}
		});
	}

}
