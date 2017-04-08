package cn.hzyc.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import cn.em.sdk.manager.PreferenceManager;
import cn.hzyc.im.R;
import cn.hzyc.im.ui.BaseActivity;
import cn.hzyc.im.ui.view.EditLayout;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;

/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity {

	private EditLayout mELPassword;
	private EditLayout mELUserName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("登录");
		setDisplayHome(false);

		setContentView(R.layout.activity_login);

		mELUserName = (EditLayout) findViewById(R.id.et_account);
		mELPassword = (EditLayout) findViewById(R.id.et_password);
		mELPassword.setPasswordStyle();
		mELUserName.setHint("请输入账号");
		mELPassword.setHint("请输入密码");

		String mCurrentUserName = PreferenceManager.getInstance()
				.getCurrentUsername();

		Intent intent=getIntent();


		mELUserName.setText(mCurrentUserName);

		if(intent!=null){
			mELUserName.setText(intent.getStringExtra("number"));
		}
		mELPassword.getEditText().requestFocus();
		mELPassword.getEditText().performClick();
		mELPassword.getEditText().setCursorVisible(false);
	}

	/**
	 * 若登录过, 则直接进入主页面
	 * 
	 * @return true 表示登录过，false表示未登录
	 * 
	 */
	public boolean goToHomeIfLoggedIn() {
		if (EMChat.getInstance().isLoggedIn()) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
			return true;
		}

		return false;
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ib_login:
			login();
			break;

		case R.id.btn_register:
			goToRegisterActivity();
			break;
		}
	}

	/** 跳转到注册界面 */
	private void goToRegisterActivity() {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_bottom_in, R.anim.alpha_unchanged);
	}

	private void login() {
		final String userName = mELUserName.getText();
		final String password = mELPassword.getText();

		if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
			showToast("账号或密码不能为空");
			return;
		}
		showProgressDialog("正在登录", false);

		// 登录
		EMChatManager.getInstance().login(userName, password, new EMCallBack() {

			@Override
			public void onSuccess() {
				dismissProgressDialog();
				onLoginSuccess(userName);
			}

			@Override
			public void onProgress(int progress, String status) {
			}
			@Override
			public void onError(int code, String error) {
				dismissProgressDialog();
				runOnUiThread(new Runnable() {
					public void run() {
						showToast("登录失败");
					}
				});
			}
		});
	}

	protected void onLoginSuccess(String userName) {
		// 登录成功后保存当前登录的用户名
		PreferenceManager.getInstance().setCurrentUserName(userName);
		EMChatManager.getInstance().loadAllConversations();
		goToMainActivity();
	}

	private void goToMainActivity() {
		startActivity(new Intent(LoginActivity.this, MainActivity.class));
		finish();
	}

}
