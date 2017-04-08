package cn.hzyc.im.ui;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import cn.hzyc.im.base.Global;
import cn.hzyc.im.base.ImHelper;

public class BaseActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setDisplayHome(true);
	}

	public void showToast(String message) {
		Global.showToast(message);
	}

	/**
	 * 设置左上角是否显示小箭头，以及是否可以点击返回上一页
	 * 
	 * @param enable true表示显示左上的小箭头, 并且可点击返回到上一页 false 不显示，点击不可以返回
	 */
	protected void setDisplayHome(boolean enable) {
		ActionBar actionBar = getActionBar();
		// 不显示logo图标
		actionBar.setDisplayShowHomeEnabled(false);

		// true表示显示左上的小箭头图标, 并且可点击返回到上一页
		actionBar.setDisplayHomeAsUpEnabled(enable);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:// 点击返回图标事件
			this.finish();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private ProgressDialog mProgressDialog;

	/**
	 * 弹窗提示正在进行操作，已作了处理可以在子线程中调用
	 * 
	 * @param mess 要提示的内容
	 * @param cancelable 是否可取消
	 */
	public void showProgressDialog(String mess, boolean cancelable) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setIndeterminate(true); //
		mProgressDialog.setCancelable(cancelable); // 用户是否可以取消
		mProgressDialog.setMessage(mess); // 设置提示的内容
		mProgressDialog.setCanceledOnTouchOutside(false); // 点击外部不会销毁

		// 主线程中操作
		Global.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// activity销毁了弹窗会报错
				if (!isFinishing())
					mProgressDialog.show();
			}
		});
	}

	/** 销毁加载提示框，已作了处理，可以在子线程中调用 */
	public void dismissProgressDialog() {
		Global.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// 主线程中操作
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		ImHelper.getInstance().pushActivity(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		ImHelper.getInstance().popActivity(this);
	}
}
