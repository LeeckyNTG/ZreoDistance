package cn.hzyc.im.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import cn.hzyc.im.R;
import cn.hzyc.im.base.ImHelper;
import cn.hzyc.im.po.IpAddress;
import cn.hzyc.im.ui.BaseActivity;
import cn.hzyc.im.ui.view.EditLayout;

import com.easemob.chat.EMContactManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddContactActivity extends BaseActivity {
	
	private Button mBtnSearch;
	
	private ProgressDialog progressDialog;

	private String username;
	
	private EditLayout mELUsername;

	private String path="http://"+ IpAddress.ipaddress+":8080/ZeroDistance/friend/addFriend.action";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_add_contact);
		setTitle("添加联系人");
		
		mELUsername = (EditLayout) findViewById(R.id.el_username);
		mELUsername.setHint("请输入要添加的用户名");
		mBtnSearch = (Button) findViewById(R.id.btn_add_friend);
		mBtnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addUser();
			}
		});
		username = ImHelper.getInstance().getCurrentUsernName();
	}
	
	private void addUser() {
		progressDialog = new ProgressDialog(this);
		String stri = getResources().getString(R.string.Is_sending_a_request);
		progressDialog.setMessage(stri);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
		
		final String toAddUsername = mELUsername.getText().trim();

		new MyAsyncTask(toAddUsername,username).execute(path);

		new Thread(new Runnable() {
			public void run() {
				try {
					// 备注信息，实际应该让用户手动填入
					 String reason = "我是XX";
					EMContactManager.getInstance().addContact(toAddUsername, reason);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							showToast("发送请求成功，等待对方确认");
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							showToast("请求添加好友失败！");
						}
					});
				}
			}
		}).start();
	}

	class MyAsyncTask extends AsyncTask<String,Void,String> {

		String friendNumber="";

		String selfNumber="";

		public MyAsyncTask(String fN,String sN){

			friendNumber=fN;

			selfNumber=sN;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			params[0]=params[0]+"?requestData="+friendNumber+"@@"+selfNumber;
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

			if ("".equals(s)){
				Toast.makeText(AddContactActivity.this,"对不起，添加好友失败！",Toast.LENGTH_SHORT).show();
			}
			if("查无此人".equals(s)){
				Toast.makeText(AddContactActivity.this,s+"!",Toast.LENGTH_SHORT).show();
			}
			if("添加成功".equals(s)){

				Intent intent=new Intent(AddContactActivity.this,MainActivity.class);
				startActivity(intent);
				AddContactActivity.this.finish();
			}
		}
	}

}
