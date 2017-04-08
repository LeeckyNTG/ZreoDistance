package cn.hzyc.im.manager;

import cn.hzyc.im.util.LogUtil;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;

public class ConnectionManager extends BaseManager {

	private static ConnectionManager instance = new ConnectionManager();

	private ConnectionManager() {
	}

	public static ConnectionManager getInstance() {
		return instance;
	}

	private EMConnectionListener connectionListener;

	@Override
	public void initialize() {
		// create the global connection listener
		connectionListener = new EMConnectionListener() {


			@Override
			public void onDisconnected(int error) {
				if (error == EMError.USER_REMOVED) {
					LogUtil.e("------------onCurrentAccountRemoved()");

				} else if (error == EMError.CONNECTION_CONFLICT) {
					LogUtil.e("------------onConnectionConflict()");
				}
			}

			@Override
			public void onConnected() {
				LogUtil.i("------------onConnected()");

			}
		};

		// 注册连接监听
		EMChatManager.getInstance().addConnectionListener(connectionListener);
	}

}
