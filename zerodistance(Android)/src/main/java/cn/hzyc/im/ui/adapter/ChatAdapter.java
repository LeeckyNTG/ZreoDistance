package cn.hzyc.im.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import cn.hzyc.im.R;
import cn.hzyc.im.base.Global;
import cn.hzyc.im.ui.holder.BaseHolder;
import cn.hzyc.im.ui.holder.ChatHolderMessage;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;

public class ChatAdapter extends MyBaseAdapter<EMMessage> {

	private static final int MESSAGE_TYPE_RECV_TXT = 0;
	private static final int MESSAGE_TYPE_SENT_TXT = 1;

	private int[] layoutRes = new int[] {
			R.layout.item_received_message,
			R.layout.item_sent_message
	};

	public EMConversation conversation;
	private void initData() {
		this.conversation = EMChatManager.getInstance().getConversation(username);
		refreshList();
	}

	public void refreshList() {
		// UI线程不能直接使用conversation.getAllMessages()
		// 否则在UI刷新过程中，如果收到新的消息，会导致并发问题
		List<EMMessage> allMessages = conversation.getAllMessages();
		int msgCount = allMessages.size();
		for (int i = 0; i < msgCount; i++) {
			// getMessage will set message as read status
			conversation.getMessage(i);
		}
		super.setDatas(allMessages);
		Global.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	private String username;
	public ChatAdapter(ArrayList<EMMessage> list, String username) {
		super(list);
		this.username = username;
		initData();
	}

	@Override
	public BaseHolder<EMMessage> getHolder(int position) {
		EMMessage message = (EMMessage) getItem(position);
		if (message.getType() ==  EMMessage.Type.TXT) {
			return new ChatHolderMessage(message.direct);
		}
		return null;
	}

	/**
	 * 列表多样式布局 方法 1
	 *
	 * 根据位置返回列表项布局样式
	 */
	@Override
	public int getItemViewType(int position) {
		EMMessage message = (EMMessage) getItem(position);
		if (message.getType() ==  EMMessage.Type.TXT) {
			return message.direct == EMMessage.Direct.RECEIVE
					? MESSAGE_TYPE_RECV_TXT
					: MESSAGE_TYPE_SENT_TXT;
		}
		return 0;
	}

	/**
	 * 列表多样式布局 方法 2
	 *
	 * 列表项样式有多少种
	 */
	@Override
	public int getViewTypeCount() {
		return 2;
	}
}
