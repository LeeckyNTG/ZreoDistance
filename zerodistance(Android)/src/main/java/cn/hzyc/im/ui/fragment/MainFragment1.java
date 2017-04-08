package cn.hzyc.im.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import cn.em.sdk.base.Constant;
import cn.em.sdk.base.EaseConstant;
import cn.hzyc.im.R;
import cn.hzyc.im.base.Global;
import cn.hzyc.im.ui.BaseFragment;
import cn.hzyc.im.ui.activity.ChatActivity;
import cn.hzyc.im.ui.activity.MainActivity;
import cn.hzyc.im.ui.adapter.ConversationAdapater;
import cn.hzyc.im.util.LogUtil;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;

/**
 * 消息界面
 */
public class MainFragment1 extends BaseFragment {

	private ListView mListView;
	protected List<EMConversation> conversationList = new ArrayList<EMConversation>();
	private ConversationAdapater adapter;
	
	private LinearLayout mLLHeaderLayout;
	
	private boolean isConflict;
    
	protected EMConnectionListener connectionListener = new EMConnectionListener() {
        
		@Override // 注意，此方法会在子线程执行，ui操作请使用handler
        public void onDisconnected(int error) {

			if (EMError.UNABLE_CONNECT_TO_SERVER == error) {
				Global.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mLLHeaderLayout.setVisibility(View.VISIBLE);
					}
				});
				return;
			}

            if (error == EMError.USER_REMOVED || error == EMError.CONNECTION_CONFLICT) {
            	isConflict = true;
            	LogUtil.e("------------------connection_conflict------");
            	Global.runOnUiThread(new Runnable() {
            		
            		@Override
            		public void run() {
            			// MainActivity的启动模式为singTop，
            			// 这里执行了startActivity()后，会销毁栈里MainActivity对象之上的所有的
            			// activity对象，并调用MainActivity的onNewIntent()方法,
            			// 在该方法中弹出提示框强制用户下线,
            			// 这样做以确保不管用户当前在浏览什么界面，都可以立即弹出强制下线对话框。
            			Intent intent = new Intent(Global.mContext, MainActivity.class);
            			intent.putExtra(Constant.ACCOUNT_CONFLICT, true);
            			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            			getActivity().startActivity(intent);
            		}
            	});
            }
        }
        
        @Override
        public void onConnected() {
        	Global.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mLLHeaderLayout.setVisibility(View.GONE);
				}
			});
        }
    };
    
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		EMConversation conversation = conversationList.get(position);
			Intent intent = new Intent(Global.mContext,ChatActivity.class);
			intent.putExtra(EaseConstant.EXTRA_USER_ID,conversation.getUserName());
			startActivity(intent);
    	};
    };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = View.inflate(Global.mContext, R.layout.main_fragment_01, null);
		mListView = (ListView) mRootView.findViewById(R.id.lv_conversation);
		mLLHeaderLayout = (LinearLayout) mRootView.findViewById(R.id.ll_header);
		
		EMChatManager.getInstance().addConnectionListener(connectionListener);
		
		conversationList = loadConversationList();
		adapter = new ConversationAdapater(Global.mContext, conversationList);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(mOnItemClickListener);
		return mRootView;
	}

	
	@Override
	public void onDestroy() {
		EMChatManager.getInstance().removeConnectionListener(connectionListener);
		super.onDestroy();
	}
	
	/**
	 * 刷新页面
	 */
	public void refresh() {
		conversationList.clear();
		conversationList.addAll(loadConversationList());
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	/**
	 * 获取会话列表
	 * 
	 * @param context
	 * @return +
	 */
	protected List<EMConversation> loadConversationList() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager
				.getInstance().getAllConversations();
		// 过滤掉messages size为0的conversation
		/**
		 * 如果在排序过程中有新消息收到，lastMsgTime会发生变化 影响排序过程，Collection.sort会产生异常
		 * 保证Conversation在Sort过程中最后一条消息的时间不变 避免并发问题
		 */
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0) {
					sortList.add(new Pair<Long, EMConversation>(conversation
							.getLastMessage().getMsgTime(), conversation));
				}
			}
		}
		try {
			// Internal is TimSort algorithm, has bug
			sortConversationByLastChatTime(sortList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
		}
		return list;
	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(
			List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList,
				new Comparator<Pair<Long, EMConversation>>() {
					@Override
					public int compare(final Pair<Long, EMConversation> con1,
							final Pair<Long, EMConversation> con2) {

						if (con1.first == con2.first) {
							return 0;
						} else if (con2.first > con1.first) {
							return 1;
						} else {
							return -1;
						}
					}
				});
	}
	
	
	public void hideErrorInfo() {
		if(mLLHeaderLayout != null)
			mLLHeaderLayout.setVisibility(View.VISIBLE);
	}
	
	public void showErrorInfo() {
		if(mLLHeaderLayout != null)
			mLLHeaderLayout.setVisibility(View.VISIBLE);
	}

}
