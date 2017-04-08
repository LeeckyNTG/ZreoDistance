package cn.hzyc.im.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.util.Log;
import cn.em.sdk.bean.EaseUser;
import cn.em.sdk.bean.InviteMessage;
import cn.em.sdk.bean.InviteMessage.InviteMesageStatus;
import cn.em.sdk.db.InviteMessgeDao;
import cn.em.sdk.db.UserDao;
import cn.em.sdk.util.EaseCommonUtils;
import cn.hzyc.im.base.Const;
import cn.hzyc.im.base.Global;
import cn.hzyc.im.util.LogUtil;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;

public class ContactManager extends BaseManager {

	private static ContactManager instance = new ContactManager();

	private UserDao mUserDao;
	private InviteMessgeDao inviteMessgeDao;

	private ContactManager() {
		mUserDao = new UserDao(Global.mContext);
		inviteMessgeDao = new InviteMessgeDao(Global.mContext);
	}

	public static ContactManager getInstance() {
		return instance;
	}

	private MyContactListener mMyContactListener = new MyContactListener();

	/**
	 * 注册群组和联系人监听，由于logout的时候会被sdk清除掉，再次登录的时候需要再注册一下
	 */
	@Override
	public void initialize() {
		EMContactManager.getInstance().setContactListener(mMyContactListener);
		loadContacts();
	}
	
	private boolean mIsContactLoaded = false;

	private void loadContacts() {
//		if (!mIsContactLoaded) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					getAllContactsFromServerAndSave();
				}
			}).start();
//		}
	}


	//获取联系人并保存到SQLite
	protected void getAllContactsFromServerAndSave() {
		List<String> usernames = null;
		try {
			usernames = EMContactManager.getInstance()
					.getContactUserNames();
			
			LogUtil.i("-------------usernames:" + usernames);
			
			// in case that logout already before server returns, we
			// should return immediately
			if (!EMChat.getInstance().isLoggedIn()) {
				return;
			}

			Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
			for (String username : usernames) {
				EaseUser user = new EaseUser(username);
				EaseCommonUtils.setUserInitialLetter(user);
				userlist.put(username, user);
			}
			
			// 存入内存
			getAllContacts().clear();
			getAllContacts().putAll(userlist);
			
			// 存入db
			UserDao dao = new UserDao(Global.mContext);
			List<EaseUser> users = new ArrayList<EaseUser>(userlist.values());
			dao.saveContactList(users);
			
			mIsContactLoaded = true;
			// 发送好友变动广播
			Global.mBroadcastManager.sendBroadcast(new Intent(
					Const.ACTION_CONTACT_CHANAGED));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 联系人数据
	 */
	private Map<String, EaseUser> contactList = new HashMap<String, EaseUser>();
	
	public Map<String, EaseUser> getAllContacts() {
		if (contactList == null) {
			UserDao dao = new UserDao(Global.mContext);
			contactList = dao.getContactList();
		}
		
		return contactList;
	}
	
	public ArrayList<EaseUser> getContactList() {
		Map<String, EaseUser> users = getAllContacts();
		ArrayList<EaseUser> results = new ArrayList<>();
		results.addAll(users.values());
		return results;
	}
	
	/***
	 * 好友变化listener
	 */
	public class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {
			LogUtil.d("------------------onContactAdded:" + usernameList.size() + "   " + usernameList.get(0));
			// 保存增加的联系人
			Map<String, EaseUser> localUsers = getAllContacts();
			Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
			for (String username : usernameList) {
				EaseUser user = new EaseUser(username);
				EaseCommonUtils.setUserInitialLetter(user);
				// 添加好友时可能会回调added方法两次
				if (!localUsers.containsKey(username)) {
					mUserDao.saveContact(user);
				}
				toAddUsers.put(username, user);
			}
			localUsers.putAll(toAddUsers);
			
			// 发送好友变动广播
			Global.mBroadcastManager.sendBroadcast(new Intent(
					Const.ACTION_CONTACT_CHANAGED));
		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
			LogUtil.d("------------------onContactDeleted:" + usernameList.size());
			Map<String, EaseUser> localUsers = getAllContacts();
			for (String username : usernameList) {
				localUsers.remove(username);
				mUserDao.deleteContact(username);
				inviteMessgeDao.deleteMessage(username);
			}
			Global.mBroadcastManager.sendBroadcast(new Intent(
					Const.ACTION_CONTACT_CHANAGED));
		}

		@Override
		public void onContactInvited(String username, String reason) {
			LogUtil.d(username + "-------------onContactInvited请求加你为好友,reason: " + reason);

			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getGroupId() == null
						&& inviteMessage.getFrom().equals(username)) {
					inviteMessgeDao.deleteMessage(username);
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			msg.setReason(reason);
			// 设置相应status
			msg.setStatus(InviteMesageStatus.BEINVITEED);
			notifyNewIviteMessage(msg);

			Intent intent = new Intent(Const.ACTION_RECEIVER_INVITED_MSG);
			intent.putExtra("msg", msg);
			Global.mBroadcastManager.sendBroadcast(intent);
		}

		@Override
		public void onContactAgreed(String username) {
			LogUtil.d(username + "------------onContactAgreed: " + username);
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getFrom().equals(username)) {
					return;
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			msg.setStatus(InviteMesageStatus.BEAGREED);
			notifyNewIviteMessage(msg);
			Global.mBroadcastManager.sendBroadcast(new Intent(
					Const.ACTION_CONTACT_CHANAGED));
		}

		@Override
		public void onContactRefused(String username) {
			// 参考同意，被邀请实现此功能,demo未实现
			Log.d(username, username + "拒绝了你的好友请求");
		}
	}

	/**
	 * 保存并提示消息的邀请消息
	 *
	 * @param msg
	 */
	private void notifyNewIviteMessage(InviteMessage msg) {
		if (inviteMessgeDao == null) {
			inviteMessgeDao = new InviteMessgeDao(Global.mContext);
		}
		inviteMessgeDao.saveMessage(msg);
		// 保存未读数，这里没有精确计算
		inviteMessgeDao.saveUnreadMessageCount(1);
		// 提示有新消息
		// getNotifier().viberateAndPlayTone(null);
	}
}
