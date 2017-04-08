package cn.hzyc.im.ui.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.em.sdk.base.EaseConstant;
import cn.em.sdk.base.EaseUI;
import cn.hzyc.im.R;
import cn.hzyc.im.base.Global;
import cn.hzyc.im.base.ImHelper;
import cn.hzyc.im.po.IpAddress;
import cn.hzyc.im.ui.BaseActivity;
import cn.hzyc.im.ui.adapter.ChatAdapter;
import cn.hzyc.im.ui.fragment.ChatFragment;
import cn.hzyc.im.ui.view.chat.ChatMenuPanel;
import cn.hzyc.im.util.LogUtil;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

public class ChatActivity extends BaseActivity {

    private ChatFragment chatFragment;
    private String toChatUsername;
	private String username;
	private ProgressDialog progressDialog;
	private String path="http://"+ IpAddress.ipaddress+":8080/ZeroDistance/chattranscripts/addChattranscripts.action";
    private ListView mListView;
	private ArrayList<EMMessage> mListDatas = new ArrayList<EMMessage>();

	private ChatMenuPanel mChatMenu;
	private ChatAdapter mAdapter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        
        setContentView(R.layout.activity_chat);
		progressDialog=new ProgressDialog(ChatActivity.this);
		progressDialog.setMessage("Loading......");
        
        // 聊天人或群id
        toChatUsername = getIntent().getExtras().getString(EaseConstant.EXTRA_USER_ID);
		username = ImHelper.getInstance().getCurrentUsernName();
        setTitle(toChatUsername);
        
        initView();
    }
	private void initDatas() {
		EMMessage message = EMMessage.createTxtSendMessage("xxxx", "jq001");
		mListDatas.add(message);
		message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
		message.setFrom("xiaowang");
		mListDatas.add(message);
	}

	public void initView() {
//		initDatas();
		mListView = (ListView) findViewById(R.id.lv_chatting);
		mAdapter = new ChatAdapter(mListDatas, toChatUsername);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mChatMenu.hideEmojiPanel();
			}
		});
		mChatMenu = (ChatMenuPanel) findViewById(R.id.cm_chat_menu);
		mChatMenu.setActivity(this);
		mChatMenu.setOnMessageSendListener(new ChatMenuPanel.OnMessageSendListener() {
			@Override
			public void onMsgSend(String msg) {
				if (TextUtils.isEmpty(msg.trim()))
					return;

				sendTextMessage(msg);
			}
		});
		scrollToBottom();
	}

	public void scrollToBottom() {
		Global.getMainHandler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mListView.setSelection(mAdapter.getCount() - 1);
			}
		}, 100);
	}
	//发送消息方法
	//==========================================================================
	protected void sendTextMessage(String content) {
		EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
		sendMessage(message);
		new sendMessageAsycnTask(content).execute(path);
	}
	class sendMessageAsycnTask extends AsyncTask<String,Void,String> {
		String message="";
		public sendMessageAsycnTask(String message){

			this.message=message;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			params[0]=params[0]+"?requestData="+username+"@@"+toChatUsername+"@@"+message;
			try {
				HttpURLConnection hc=(HttpURLConnection)new URL(params[0]).openConnection();
				hc.setRequestMethod("GET");
				hc.setDoOutput(true);
				hc.setReadTimeout(5000);
				BufferedReader bf = new BufferedReader(new InputStreamReader(hc.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String str = "";
				while ((str = bf.readLine()) != null) {
					sb.append(str);
				}
				return sb.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			progressDialog.dismiss();
		}
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
		EaseUI.getInstance().pushActivity(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(
				emEventListener,
				new EMNotifierEvent.Event[] {
						EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventDeliveryAck,
						EMNotifierEvent.Event.EventReadAck });
	}

	@Override
	public void onStop() {
		super.onStop();
		EaseUI.getInstance().popActivity(this);
	}

	private EMEventListener emEventListener = new EMEventListener() {
		/**
		 * 事件监听,registerEventListener后的回调事件
		 *
		 * see {@link EMNotifierEvent}
		 */
		@Override
		public void onEvent(EMNotifierEvent event) { 
			// 注意：该onEvent方法会在子线程中调用，所以如果要做界面刷新操作，
			// 需要使用Handler在主线程中进行：比如通过notifyDatasetChanged()
			switch (event.getEvent()) {
				case EventNewMessage:
					// 获取到message
					EMMessage message = (EMMessage) event.getData();
					// 单聊消息
					String username = message.getFrom();

					TextMessageBody txtBody = (TextMessageBody) message.getBody();
					boolean mainThread = Global.isMainThread();
					LogUtil.w(mainThread + "------------message: " + txtBody.getMessage());
					
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
					// a mListDatas of offline messages
					// List<EMMessage> offlineMessages = (List<EMMessage>)
					// event.getData();
					mAdapter.refreshList();
					break;
			}
		}
	};
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMChatManager.getInstance().unregisterEventListener(emEventListener);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (mChatMenu.onBackPressed())
            return;
        super.onBackPressed();
    }

    public String getToChatUsername(){
        return toChatUsername;
    }
    
    
}