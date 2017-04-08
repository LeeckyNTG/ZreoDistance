package cn.hzyc.im.ui.holder;

import com.easemob.chat.EMMessage;

/**
 * 聊天BaseHolder基类
 */
public abstract class ChatBaseHolder<T> extends BaseHolder{

	protected EMMessage.Direct direct;

	public ChatBaseHolder(EMMessage.Direct direct) {
		this.direct = direct;
		mRootView = initView();// 初始化布局
		mRootView.setTag(this);// 给view设置tag
	}
}
