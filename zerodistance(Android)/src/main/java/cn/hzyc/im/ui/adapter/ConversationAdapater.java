package cn.hzyc.im.ui.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.em.sdk.util.EaseCommonUtils;
import cn.hzyc.im.R;
import cn.hzyc.im.util.EmojiUtil;
import cn.hzyc.im.util.LogUtil;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.util.DateUtils;

/**
 * 会话列表adapter
 */
public class ConversationAdapater extends ArrayAdapter<EMConversation> {
	
	private List<EMConversation> conversationList = new ArrayList<EMConversation>();

	protected int primaryColor;
	protected int secondaryColor;
	protected int timeColor;
	protected int primarySize;
	protected int secondarySize;
	protected float timeSize;

	public ConversationAdapater(Context context,List<EMConversation> objects) {
		super(context, 0, objects);
		conversationList = objects;
	}

	@Override
	public int getCount() {
		return conversationList == null ? 0 : conversationList.size();
	}

	@Override
	public EMConversation getItem(int arg0) {
		if (arg0 < conversationList.size()) {
			return conversationList.get(arg0);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_chat_history, parent, false);
		}
		
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.unreadLabel = (TextView) convertView
					.findViewById(R.id.unread_msg_number);
			holder.message = (TextView) convertView.findViewById(R.id.message);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
			holder.msgState = convertView.findViewById(R.id.msg_state);
			convertView.setTag(holder);
		}

		// 获取与此用户/群组的会话
		EMConversation conversation = getItem(position);
		// 获取用户username或者群组groupid
		String username = conversation.getUserName();

		holder.name.setText(username);

		holder.name.setText(username);
		
		if (conversation.getUnreadMsgCount() > 0) {
			// 显示与此用户的消息未读数
			holder.unreadLabel.setText(String.valueOf(conversation
					.getUnreadMsgCount()));
			holder.unreadLabel.setVisibility(View.VISIBLE);
		} else {
			holder.unreadLabel.setVisibility(View.INVISIBLE);
		}

		if (conversation.getMsgCount() != 0) {
			// 把最后一条消息的内容作为item的message内容
			EMMessage lastMessage = conversation.getLastMessage();

			String mess = EaseCommonUtils.getMessageDigest(lastMessage, this.getContext());
			EmojiUtil.setTextWithEmoji(getContext(), holder.message, mess);

			String time = DateUtils.getTimestampString(new Date(
					lastMessage.getMsgTime()));
			holder.time.setText(time);
			LogUtil.d("----------time: " + time);
		}

		return convertView;
	}

	private static class ViewHolder {
		/** 和谁的聊天记录 */
		TextView name;
		/** 消息未读数 */
		TextView unreadLabel;
		/** 最后一条消息的内容 */
		TextView message;
		/** 最后一条消息的时间 */
		TextView time;
		/** 用户头像 */
		ImageView avatar;
		/** 最后一条消息的发送状态 */
		View msgState;
	}
}
