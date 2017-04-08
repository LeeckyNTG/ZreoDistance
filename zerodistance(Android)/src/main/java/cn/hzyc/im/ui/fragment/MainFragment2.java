package cn.hzyc.im.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cn.em.sdk.base.EaseConstant;
import cn.em.sdk.bean.EaseUser;
import cn.em.sdk.bean.pinyin.PinyinComparator;
import cn.em.sdk.db.UserDao;
import cn.em.sdk.util.EaseCommonUtils;
import cn.hzyc.im.R;
import cn.hzyc.im.base.Const;
import cn.hzyc.im.base.Global;
import cn.hzyc.im.manager.ContactManager;
import cn.hzyc.im.ui.BaseFragment;
import cn.hzyc.im.ui.activity.ChatActivity;
import cn.hzyc.im.ui.activity.InviteMsgActivity;
import cn.hzyc.im.ui.adapter.ContactAdapter;
import cn.hzyc.im.ui.view.LetterBar;
import cn.hzyc.im.ui.view.LetterBar.OnPressedLetterChangedListener;
import cn.hzyc.im.util.LogUtil;

import com.easemob.chat.EMContactManager;

public class MainFragment2 extends BaseFragment {

	private ListView listView;
	private LetterBar sideBar;
	private TextView dialog;
	private ContactAdapter mListAdapter;
	private UserDao mUserDao = new UserDao(Global.mContext);

	private boolean mShowRedDot;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	/**
	 * 获取联系人列表，并排序
	 */
    protected EaseUser toBeProcessUser;
    protected String toBeProcessUsername;
	
	protected List<EaseUser> mListDatas = new ArrayList<EaseUser>();


	//进入聊天界面
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {

			if (position == 0) {
				openNewFrinedInviteUI();
				return;
			}
			
			if (position == 1) {
				return;
			}

			// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
			String username = ((EaseUser) mListAdapter.getItem(position)).getUsername();
			
			startActivity(new Intent(mActivity,
					ChatActivity.class).putExtra(
					EaseConstant.EXTRA_USER_ID,
					username));
		}
	};
	
	private OnPressedLetterChangedListener mOnPressedLetterChangedListener 
			= new OnPressedLetterChangedListener() {

		@Override
		public void onTouchingLetterChanged(String s) {
			// 该字母首次出现的位置
			int position = mListAdapter.getPositionForSection(s.charAt(0));
			if (position != -1) {
				listView.setSelection(position);
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.mRootView = inflater.inflate(R.layout.main_fragment_02, null);
		initViews();
		return super.mRootView;
	}

	private void initViews() {
		mListDatas = new ArrayList<EaseUser>();
		pinyinComparator = new PinyinComparator();

		sideBar = (LetterBar) mRootView.findViewById(R.id.sidrbar);
		dialog = (TextView) mRootView.findViewById(R.id.tv_show_pressed_letter);
		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(mOnPressedLetterChangedListener);

		listView = (ListView) mRootView.findViewById(R.id.country_lvcountry);
		listView.setOnItemClickListener(mOnItemClickListener);
		
		refreshListViewDatas(getUsersFromDb());
		mListAdapter = new ContactAdapter(getActivity(), mListDatas);
		listView.setAdapter(mListAdapter);
		mListAdapter.showRedDot(mShowRedDot);
		loadListViewDatas();
		
		//注册上下文菜单
        registerForContextMenu(listView);
	}
	
	
	// 刷新列表显示的数据
	private synchronized void refreshListViewDatas(final List<EaseUser> users) {
			mActivity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					mListDatas.clear();
					mListDatas.addAll(users);
					// 根据a-z进行排序源数据
					Collections.sort(mListDatas, pinyinComparator);
					// 排完序后，再往集合的初始位置添加固定的元素
					addHeaderItem(mListDatas);
					if (mListAdapter != null)
						mListAdapter.notifyDataSetChanged();
					else {
						mListAdapter = new ContactAdapter(getActivity(), mListDatas);
						listView.setAdapter(mListAdapter);
					}
				}
			});	
	}
	
	
	public void loadListViewDatas() {
        new Thread (new Runnable() {
            @Override
            public void run() {
                try {
                	List<String> userList = null;
					
                	// 需异步执行 查找好友。
                	// 通过此接口获取的数据并不是实时准确的：
                	// 比如，给用户A发起了好友添加申请，用户A同意了，
                	// 此时调用此方法返回的好友列表中并不包含A，环信sdk是通过
                	// 联系人事件监听EMContactListener的onContactAdded()方法
                	// 告知开发者，好友发生变化了。
//					userList = EMContactManager.getInstance()
//							.getContactUserNames();
                	
					// 加载本地的模拟数据
//					String[] users = getResources().getStringArray(R.array.contacts);
//					userList = Arrays.asList(users);
					// List<EaseUser> newUsers = toEaseUsers(userList);
                	// System.out.println("--------usernames: " + userList);

					List<EaseUser> allUsers = ContactManager.getInstance()
							.getContactList();
					LogUtil.w("---------------allUsers: " + allUsers.size());
                	refreshListViewDatas(allUsers);
            		
            		// 保存数据到数据库
            		// mUserDao.saveContactList(allUsers);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		int position = ((AdapterContextMenuInfo) menuInfo).position;
		toBeProcessUser = (EaseUser) listView.getItemAtPosition(position);
	    toBeProcessUsername = toBeProcessUser.getUsername();
		getActivity().getMenuInflater().inflate(R.menu.contact_list_menu, menu);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_contact) {
			try {
				// 删除此联系人
				deleteContact(toBeProcessUser);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}

	/*	if (item.getItemId() == R.id.setNick) {
			try {
				EaseUser easeUser=new EaseUser(toBeProcessUser.getUsername());
				easeUser.setUsername(toBeProcessUser.getUsername());
				easeUser.setAvatar(toBeProcessUser.getAvatar());
				easeUser.setInitialLetter(toBeProcessUser.getInitialLetter());
				easeUser.setEid(toBeProcessUser.getEid());
				easeUser.setNick("我的好友");
				setNick(easeUser);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}*/
		return super.onContextItemSelected(item);
	}


	/**
	 * 设置好友备注
	 */

/*

	public void setNick(final EaseUser toSetNickUser) {

		new Thread(new Runnable() {
			public void run() {
				try {
					//EMContactManager.getInstance().deleteContact(toBeProcessUsername);
					// 删除db和内存中此用户的数据
					UserDao dao = new UserDao(getActivity());
					dao.saveContact(toSetNickUser);

					// 并删除与他的聊天信息
					// ...

					// 删除相关的邀请消息
					// InviteMessgeDao dao = new InviteMessgeDao(getActivity());
					// dao.deleteMessage(toBeProcessUser.getUsername());


				} catch (final Exception e) {
					mActivity.dismissProgressDialog();

				}
			}
		}).start();
	}
*/


	
	/**
	 * 删除联系人
	 */
	public void deleteContact(final EaseUser tobeDeleteUser) {
		String st1 = getResources().getString(R.string.deleting);
		final String st2 = getResources().getString(R.string.Delete_failed);
		mActivity.showProgressDialog(st1, false);
		new Thread(new Runnable() {
			public void run() {
				try {
					EMContactManager.getInstance().deleteContact(toBeProcessUsername);
					
					// 删除db和内存中此用户的数据
					UserDao dao = new UserDao(getActivity());
					dao.deleteContact(toBeProcessUsername);
	                
					// 并删除与他的聊天信息
	                // ...

	                // 删除相关的邀请消息
	                // InviteMessgeDao dao = new InviteMessgeDao(getActivity());
	                // dao.deleteMessage(toBeProcessUser.getUsername());
					
					Global.runOnUiThread(new Runnable() {
						public void run() {
							mActivity.dismissProgressDialog();
							mListDatas.remove(tobeDeleteUser);
							mListAdapter.notifyDataSetChanged();
						}
					});
				} catch (final Exception e) {
					mActivity.dismissProgressDialog();
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							mActivity.showToast(st2 + e.getMessage());
						}
					});
				}
			}
		}).start();
	}

	protected void openNewFrinedInviteUI() {
		Intent intent = new Intent(mActivity, InviteMsgActivity.class);
		mActivity.startActivityForResult(intent, 0);
	}

	private List<EaseUser> getUsersFromDb() {
		Map<String, EaseUser> map = mUserDao.getContactList();
		ArrayList<EaseUser> users = new ArrayList<EaseUser>();
		
		// 获取联系人列表
		if (map == null) {
			return users;
		}

		Iterator<Entry<String, EaseUser>> iterator = map.entrySet()
				.iterator();

		while (iterator.hasNext()) {
			Entry<String, EaseUser> entry = iterator.next();
			EaseUser user = entry.getValue();
			EaseCommonUtils.setUserInitialLetter(user);
			users.add(user);
		}
		return users;
	}
	
	public void showRedDot(boolean visible) {
		mShowRedDot = visible;
		if (mListAdapter != null)
			mListAdapter.showRedDot(visible);
	}

	private void addHeaderItem(List<EaseUser> datas) {
		EaseUser user = new EaseUser(Const.ITEM_IM_HELPER);
		user.setInitialLetter(Const.ITEM_ARROW_UP);
		datas.add(0, user);

		user = new EaseUser(Const.ITEM_INVITE_AND_NOTIFICATION);
		user.setInitialLetter(Const.ITEM_ARROW_UP);
		datas.add(0, user);
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param
	 * @return
	 */
	private List<EaseUser> toEaseUsers(List<String> usersList) {
		List<EaseUser> users = new ArrayList<EaseUser>();

		for (int i = 0; i < usersList.size(); i++) {
			String username = usersList.get(i);
			EaseUser user = new EaseUser(username);
			
			EaseCommonUtils.setUserInitialLetter(user);
			users.add(user);
		}
		return users;
	}
}
