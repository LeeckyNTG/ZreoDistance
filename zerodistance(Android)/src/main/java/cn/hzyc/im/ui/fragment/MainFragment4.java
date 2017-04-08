package cn.hzyc.im.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.hzyc.im.R;
import cn.hzyc.im.base.Global;
import cn.hzyc.im.base.ImHelper;
import cn.hzyc.im.ui.BaseFragment;
import cn.hzyc.im.ui.activity.LoginActivity;
import cn.hzyc.im.ui.activity.MainActivity;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;

public class MainFragment4 extends BaseFragment {
	
	private Button mBtnLogout;
	private ImageView ivAvatar;
	private TextView tvNickname;
	private TextView tvUsername;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.mRootView = inflater.inflate(R.layout.main_fragment_04, null, false);
		initView();
		return mRootView;
	}

	private void initView() {
		ivAvatar = (ImageView) mRootView.findViewById(R.id.iv_avatar);
		tvNickname = (TextView) mRootView.findViewById(R.id.tv_nick_name);
		tvUsername = (TextView) mRootView.findViewById(R.id.tv_username);
		
		String currentUserName = ImHelper.getInstance().getCurrentUsernName();
		tvNickname.setText(currentUserName);
		tvUsername.setText("用户名: " + currentUserName);
		
		mBtnLogout = (Button) mRootView.findViewById(R.id.btn_logout);
		mBtnLogout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				logout();
			}
		});
	}

	protected void logout() {
		final MainActivity activity = (MainActivity) getActivity();
		activity.showProgressDialog("正在注销...", false);
		
		EMChatManager.getInstance().logout(new EMCallBack() {
            
            @Override
            public void onSuccess() { 
            	activity.dismissProgressDialog();
            	// 注意这里为子线程，直接操作没有动画效果
            	Global.runOnUiThread(new Runnable() {
					
					@Override
					public void run() { // 要在主线程中操作才会有动画效果
		            	Intent intent = new Intent(mActivity, LoginActivity.class);
		            	mActivity.startActivity(intent);
		            	mActivity.finish();
		            	mActivity.overridePendingTransition(R.anim.push_bottom_in, 
		                		R.anim.alpha_unchanged);
					}
				});
            }
            
            @Override
            public void onProgress(int progress, String status) {
            }
            
            @Override
            public void onError(int code, String error) {
            	activity.dismissProgressDialog();
            }
        });
	}
}
