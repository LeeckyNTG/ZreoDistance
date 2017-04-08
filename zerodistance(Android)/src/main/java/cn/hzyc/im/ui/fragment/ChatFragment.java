package cn.hzyc.im.ui.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import cn.em.sdk.base.EaseConstant;
import cn.em.sdk.base.EaseUI;
import cn.hzyc.im.R;
import cn.hzyc.im.base.Global;
import cn.hzyc.im.ui.BaseFragment;
import cn.hzyc.im.ui.activity.ChatActivity;
import cn.hzyc.im.ui.adapter.ChatAdapter;
import cn.hzyc.im.ui.view.chat.ChatMenuPanel;
import cn.hzyc.im.util.LogUtil;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

/**
 * 主界面和谁进行聊天
 */
public class ChatFragment extends BaseFragment {
	
	private ListView mListView;
	private ArrayList<EMMessage> mListDatas = new ArrayList<EMMessage>();

	 //聊天对象
	private String toChatUsername;

	private ChatMenuPanel mChatMenu;
	private ChatAdapter mAdapter;

	private void initDatas() {
		EMMessage message = EMMessage.createTxtSendMessage("xxxx", "jq001");
		mListDatas.add(message);
		message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
		message.setFrom("xiaowang");
		mListDatas.add(message);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getArguments();
		toChatUsername = b.getString(EaseConstant.EXTRA_USER_ID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = View.inflate(getActivity(), R.layout.fragment_chatting, null);
		mListView = (ListView) mRootView.findViewById(R.id.lv_chatting);
		mAdapter = new ChatAdapter(mListDatas, toChatUsername);
		mListView.setAdapter(mAdapter);

		mChatMenu = (ChatMenuPanel) mRootView.findViewById(R.id.cm_chat_menu);
		mChatMenu.setActivity((ChatActivity)mActivity);
		mChatMenu.setOnMessageSendListener(new ChatMenuPanel.OnMessageSendListener() {
			@Override
			public void onMsgSend(String msg) {
				if (TextUtils.isEmpty(msg.trim()))
					return;

				sendTextMessage(msg);
			}
		});
		return mRootView;
	}

	public void scrollToBottom() {
		Global.getMainHandler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mListView.setSelection(mAdapter.getCount() - 1);
			}
		}, 300);
	}

	public boolean onBackPressed() {
		return mChatMenu.onBackPressed();
	}
	//发送消息方法
	//==========================================================================
	protected void sendTextMessage(String content) {
		EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
		sendMessage(message);
	}
	protected void sendMessage(EMMessage message){
		//发送消息
		EMChatManager.getInstance().sendMessage(message, null);
		//刷新ui
		mAdapter.refreshList();
		scrollToBottom();
	}
	//===================================================================================
	@Override
	public void onResume() {
		super.onResume();
		EaseUI.getInstance().pushActivity(getActivity());
		// register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(
				emEventListener,
				new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventDeliveryAck,
						EMNotifierEvent.Event.EventReadAck});
	}

	@Override
	public void onStop() {
		super.onStop();
		EaseUI.getInstance().popActivity(getActivity());
		EMChatManager.getInstance().unregisterEventListener(emEventListener);
	}


	private EMEventListener emEventListener = new EMEventListener() {
		/**
		 * 事件监听,registerEventListener后的回调事件
		 *
		 * see {@link EMNotifierEvent}
		 */
		@Override
		public void onEvent(EMNotifierEvent event) {
			switch (event.getEvent()) {
				case EventNewMessage:
					// 获取到message
					EMMessage message = (EMMessage) event.getData();
					// 单聊消息
					String username = message.getFrom();

					TextMessageBody txtBody = (TextMessageBody) message.getBody();
					LogUtil.w("------------message: " + txtBody.getMessage());

					// 如果是当前会话的消息，刷新聊天页面
					if (username.equals(toChatUsername)) {
						mAdapter.refreshList();
						scrollToBottom();
						// 声音和震动提示有新消息
						EaseUI.getInstance().getNotifier().viberateAndPlayTone(message);
					} else {
						// 如果消息不是和当前聊天ID的消息
						EaseUI.getInstance().getNotifier().onNewMsg(message);
					}
					break;
				case EventDeliveryAck:
				case EventReadAck:
					// 获取到message
					mAdapter.refreshList();
					break;
				case EventOfflineMessage:
					mAdapter.refreshList();
					break;
				default:
					break;
			}
		}
	};
}
