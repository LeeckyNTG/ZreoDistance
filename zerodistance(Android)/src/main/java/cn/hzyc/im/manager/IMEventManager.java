package cn.hzyc.im.manager;

import java.util.List;

import cn.em.sdk.manager.EaseNotifier;
import cn.hzyc.im.base.ImHelper;
import cn.hzyc.im.util.LogUtil;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;

public class IMEventManager extends BaseManager {

	protected static final String TAG = "IMEventManager";
	private static IMEventManager instance = new IMEventManager();

	private IMEventManager() {
	}

	public static IMEventManager getInstance() {
		return instance;
	}

	private EMEventListener eventListener;

	@Override
	public void initialize() {
		registerEventListener();
	}

	/**
	 * 全局事件监听 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理 activityList.size()
	 * <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
	 */
	protected void registerEventListener() {
		eventListener = new EMEventListener() {

			@Override
			public void onEvent(EMNotifierEvent event) {
				EMMessage message = null;
				if (event.getData() instanceof EMMessage) {
					message = (EMMessage) event.getData();
					LogUtil.d("receive the event : " + event.getEvent()
							+ ",id : " + message.getMsgId());
				}

				switch (event.getEvent()) {
				case EventNewMessage:
					LogUtil.d("---------后台----EventNewMessage");
					// 应用在后台，不需要刷新UI,通知栏提示新消息
					if (!ImHelper.getInstance().hasForegroundActivies()) {
						getNotifier().onNewMsg(message);
					}
					break;
				case EventOfflineMessage:
					if (!ImHelper.getInstance().hasForegroundActivies()) {
						LogUtil.d("received offline messages");
						List<EMMessage> messages = (List<EMMessage>) event.getData();
						getNotifier().onNewMesg(messages);
					}
					break;
					
				// below is just giving a example to show a cmd toast, the
				// app should not follow this
				// so be careful of this
				case EventNewCMDMessage: {
					LogUtil.d("-------------收到透传消息");
					break;
				}
				case EventDeliveryAck:
					message.setDelivered(true);
					break;
				case EventReadAck:
					message.setAcked(true);
					break;
				}
			}
		};

		EMChatManager.getInstance().registerEventListener(eventListener);
	}

	protected EaseNotifier getNotifier() {
		return ImHelper.getEaseNotifier();
	}
}
