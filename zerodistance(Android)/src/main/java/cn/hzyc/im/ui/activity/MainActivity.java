package cn.hzyc.im.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import cn.em.sdk.base.Constant;
import cn.em.sdk.db.InviteMessgeDao;
import cn.hzyc.im.R;
import cn.hzyc.im.base.Const;
import cn.hzyc.im.base.Global;
import cn.hzyc.im.base.ImHelper;
import cn.hzyc.im.manager.ContactManager;
import cn.hzyc.im.ui.BaseActivity;
import cn.hzyc.im.ui.fragment.MainFragment1;
import cn.hzyc.im.ui.fragment.MainFragment2;
import cn.hzyc.im.ui.fragment.MainFragment3;
import cn.hzyc.im.ui.fragment.MainFragment4;
import cn.hzyc.im.ui.view.GradientTab;
import cn.hzyc.im.util.LogUtil;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage;

public class MainActivity extends BaseActivity {

    private static final String[] TAB_ITEMS = new String[] {"消息", "通讯录", "发现", "我"};

    private static final int[] TAB_ICONS = new int[] {
    		R.drawable.icon_tab_1,
            R.drawable.icon_tab_2, 
            R.drawable.icon_tab_3, 
            R.drawable.icon_tab_4 };

    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private List<GradientTab> mTabs = new ArrayList<GradientTab>();
    
    private ViewPager mViewPager;
    private LinearLayout mTabLayout;
    private FragmentPagerAdapter mAdapter;
    private int currentTabIndex; 
    
    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {



        //侧滑事件
        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            LogUtil.d("position = " + position + " ,positionOffset =  "
                    + positionOffset);
            // positionOffset的值为： 0 --> 1
            if (positionOffset > 0) {
                GradientTab left = mTabs.get(position);
                GradientTab right = mTabs.get(position + 1);
                left.updateTabAlpha(1 - positionOffset);
                right.updateTabAlpha(positionOffset);
            }
        }

        @Override
        public void onPageSelected(int position) {
            currentTabIndex = position;
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            onTabChanged(v);
        }
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		EMChatManager.getInstance().addConnectionListener(connectionListener);
		registerBroadcastReceiver();
		
        initActivityTitle();
        initView();
        
		// 注册联系人变动监听
		ContactManager.getInstance().initialize();
        // 通知sdk，UI 已经初始化完毕, 可以接收事件了
        // 调用了此方法后，才会接收离线信息
		EMChat.getInstance().setAppInited();
	}

	private void initActivityTitle() {
		// 左上角不可以点和返回
        setDisplayHome(false);
        setTitle("零距离");
	}
	
	private void initView() {
		initDatas();
		initBottomTabs();
		initViewPager();
	}
	
    private void initBottomTabs() {
        mTabLayout = (LinearLayout) findViewById(R.id.ll_tabs);
        for (int i = 0; i < TAB_ITEMS.length; i++) {
            GradientTab tab = new GradientTab(this);
            tab.setTag(i);// 设置标记
            mTabs.add(tab);
            tab.setTextAndIcon(TAB_ITEMS[i], TAB_ICONS[i]);
            tab.setOnClickListener(mOnClickListener);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            p.weight = 1;
            int padding = (int) (getResources().getDisplayMetrics().density * 5);
            tab.setPadding(0, padding, 0, padding);
            mTabLayout.addView(tab, p);
        }

        // 默认选中第一项
        mTabs.get(0).setTabSelected(true);
    }
    
    private MainFragment2 mainFragment2;
    private MainFragment1 conversationListFragment;
    
    private void initDatas() {
        conversationListFragment = new MainFragment1();
        mFragments.add(conversationListFragment);
    	
        mainFragment2 = new MainFragment2();
        mFragments.add(mainFragment2);
        
        mFragments.add(new MainFragment3());
        mFragments.add(new MainFragment4());
    }
    
    private void initViewPager() {
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }
        };

        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mViewPager.setAdapter(mAdapter); 
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
    }
    
    /**
     * 点击Tab按钮
     *
     * @param tabSelected
     */
    private void onTabChanged(View tabSelected) {
        resetAllTabsColor2Normal();
        // 当前选中项
        int index = (int) tabSelected.getTag();
        // 之前选中项
        mTabs.get(currentTabIndex).setTabSelected(false);
        mTabs.get(index).setTabSelected(true);
        mViewPager.setCurrentItem(index, false);
        currentTabIndex = index;

    }

    private void resetAllTabsColor2Normal() {
        for (int i = 0; i < mTabs.size(); i++) {
            mTabs.get(i).setTabSelected(false);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_friend) {
            Intent intent = new Intent(this, AddContactActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    private BroadcastReceiver broadcastReceiver;
    
    protected void showContactRedDot() {
    	mTabs.get(1).setRedDotVisible(true);
    	MainFragment2 frag = (MainFragment2) mFragments.get(1);
    	frag.showRedDot(true);
    }
    
    protected void hideContactRedDot() {
        mTabs.get(1).setRedDotVisible(false);
        MainFragment2 frag = (MainFragment2) mFragments.get(1);
        frag.showRedDot(false);
    }
    
    /**
     * 刷新未读消息数
     */
    public void updateBottomUnreadCountLabel() {
        int count = getUnreadMsgCountTotal();
        setUnreadMsgCount(count);
    }
    
    public void setUnreadMsgCount(int unreadCount) {
        mTabs.get(0).setUnreadCount(unreadCount);
    }
    
    /**
     * 获取未读消息数
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        for (EMConversation conversation : EMChatManager.getInstance()
                .getAllConversations().values()) {
            if (conversation.getType() == EMConversationType.ChatRoom)
                chatroomUnreadMsgCount = chatroomUnreadMsgCount
                        + conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal - chatroomUnreadMsgCount;
    }
    
    private void updateContactPage() {
    	updateBottomUnreadCountLabel();
    	hideContactRedDot();
        if (mainFragment2 != null) {
            mainFragment2.loadListViewDatas();
        }
    }
    
    /**
     * 刷新申请与通知消息数
     */
    public void updateUnreadContactLable() {
        runOnUiThread(new Runnable() {
            public void run() {
                int count = getUnreadAddressCountTotal();
                if (count > 0) {
                    mainFragment2.showRedDot(true);
                } else {
                    mainFragment2.showRedDot(false);
                }
            }
        });
    }
    
    /**
     * 获取未读申请与通知消息
     */
    private InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(this);

    public int getUnreadAddressCountTotal() {
        int unreadAddressCountTotal = 0;
        unreadAddressCountTotal = inviteMessgeDao.getUnreadMessagesCount();
        return unreadAddressCountTotal;
    }
    
    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.ACTION_RECEIVER_INVITED_MSG);
        intentFilter.addAction(Const.ACTION_CONTACT_CHANAGED);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (Const.ACTION_RECEIVER_INVITED_MSG.equals(intent.getAction())) {
                    showContactRedDot();
                } else if (Const.ACTION_CONTACT_CHANAGED.equals(intent.getAction())) {
                    updateContactPage();
                }
            }
        };
        
        Global.mBroadcastManager.registerReceiver(broadcastReceiver,intentFilter);
    }
    
	
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	EMChatManager.getInstance().removeConnectionListener(connectionListener);
    	 Global.mBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
    
    
    protected EMConnectionListener connectionListener = new EMConnectionListener() {
        
		@Override // 注意，此方法会在子线程执行，ui操作请使用handler
        public void onDisconnected(int error) {

			if (EMError.UNABLE_CONNECT_TO_SERVER == error) {
				Global.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						((MainFragment1)mFragments.get(0)).showErrorInfo();
					}
				});
				return;
			}

            if (error == EMError.USER_REMOVED || error == EMError.CONNECTION_CONFLICT) {
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
            			startActivity(intent);
            		}
            	});
            }
        }
        
        @Override
        public void onConnected() {
        	Global.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// 网络正常时，隐藏主界面列表顶部的连接出错提示
					((MainFragment1)mFragments.get(0)).hideErrorInfo();
				}
			});
        }
    };
    
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			LogUtil.d("----------------------------onActivityResult()");
			updateContactPage();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	private android.app.AlertDialog.Builder conflictBuilder;
    private android.app.AlertDialog.Builder accountRemovedBuilder;
    private boolean isConflictDialogShow;

    // 账号在别处登录
    public boolean isConflict = false;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false)
                && !isConflictDialogShow) {
            showConflictDialog();
        }
    }

    /**
     * 显示帐号在别处登录dialog
     */
    public void showConflictDialog() {
        isConflictDialogShow = true;
        ImHelper.getInstance().logout(false, null);
        String st = getResources().getString(R.string.Logoff_notification);
        
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (conflictBuilder == null)
                    conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                
                conflictBuilder.setTitle(st);
                conflictBuilder.setMessage(R.string.connect_conflict);
                conflictBuilder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                conflictBuilder = null;
                                finish();
                                startActivity(new Intent(MainActivity.this,
                                        LoginActivity.class));
                            }
                        });
                conflictBuilder.setCancelable(false);
                conflictBuilder.create().show();
                isConflict = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    protected void onResume() {
        // 刷新bottom bar消息未读数
        updateBottomUnreadCountLabel();

        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(
                mEMEventListener,
                new EMNotifierEvent.Event[]{
                        EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventConversationListChanged});
        super.onResume();
    }
    
    
    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {

            public void run() {
                // 刷新bottom bar消息未读数
                updateBottomUnreadCountLabel();
                // 当前页面如果为聊天历史页面，刷新此页面
                if (conversationListFragment != null) {
                    conversationListFragment.refresh();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        EMChatManager.getInstance().unregisterEventListener(mEMEventListener);
        super.onStop();
    }
    
    private EMEventListener mEMEventListener = new EMEventListener() {

        @Override
        public void onEvent(EMNotifierEvent event) {
            switch (event.getEvent()) {
                case EventNewMessage: // 普通消息
                {
                    EMMessage message = (EMMessage) event.getData();
                    // 提示新消息
                    ImHelper.getInstance().getEaseNotifier().onNewMsg(message);
                    refreshUIWithMessage();
                    break;
                }

                case EventOfflineMessage: {
                    refreshUIWithMessage();
                    break;
                }

                case EventConversationListChanged: {
                    refreshUIWithMessage();
                    break;
                }
            }
        }
    };
}
