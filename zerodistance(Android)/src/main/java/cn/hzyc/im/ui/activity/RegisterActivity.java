package cn.hzyc.im.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.hzyc.im.R;
import cn.hzyc.im.po.IpAddress;
import cn.hzyc.im.ui.BaseActivity;
import cn.hzyc.im.ui.view.EditLayout;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends BaseActivity {

	private EditLayout mELPassword;
	private EditLayout mELPassword2;
	private EditLayout registerEmail;
	private EditText registerYzm;
	private Button registerGetEmailYz;

	private ProgressDialog progressDialog;

	private String email=null,tjYzm=null,password;

	private String yzm=null;

	private String yzmPath="http://"+ IpAddress.ipaddress+":8080/ZeroDistance/user/getYzm.action";

	private String registerPath="http://"+ IpAddress.ipaddress+":8080/ZeroDistance/user/register.action";




	private static final int PHOTO_CAPTURE = 0x11;
	private static String photoPath = "/sdcard/AnBo/";
	private static String photoName = photoPath + "laolisb.jpg";

	private String newName = "laoli.jpg";
	private String uploadFile = "/sdcard/AnBo/laolisb.jpg";
	private String actionUrl = "http://192.168.1.154:8080/faceTest/photoServlet";
	private String InfoUrl = "http://192.168.1.154:8080/faceTest/InfoServlet";
	Uri imageUri = Uri.fromFile(new File(Environment
			.getExternalStorageDirectory(), "image.jpg"));
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);
		setTitle("注册");
		registerEmail = (EditLayout) findViewById(R.id.registerEmail);
		mELPassword = (EditLayout) findViewById(R.id.el_password);
		mELPassword2 = (EditLayout) findViewById(R.id.el_password2);
		registerYzm=(EditText) findViewById(R.id.registerYzm);
		registerGetEmailYz=(Button)findViewById(R.id.registerGetEmailYz);
		progressDialog=new ProgressDialog(RegisterActivity.this);
		progressDialog.setTitle("请稍等！");
		progressDialog.setMessage("Loading......");
		registerEmail.setHint("请输入你的电子邮箱");
		mELPassword.setHint("请输入密码");
		mELPassword2.setHint("请再次输入密码");

		//获取验证码的事件
		registerGetEmailYz.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				email=registerEmail.getText().toString().trim();
				new yzmAsyncTask().execute(yzmPath);
			}
		});
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads()
				.detectDiskWrites()
				.detectNetwork()
				.penaltyLog()
				.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects()
				.penaltyLog()
				.penaltyDeath()
				.build());
		password = mELPassword.getText();

	}
	public void onClick(View view) {
		switch (view.getId()) {
			//注册事件
			case R.id.btn_register:
				register();
				break;
			case R.id.btn_upload_photo:
				// TODO Auto-generated method stu
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				File file = new File(photoPath);
				if (!file.exists()) { // 检查图片存放的文件夹是否存在
					file.mkdir(); // 不存在的话 创建文件夹
				}
				File photo = new File(photoName);
				imageUri = Uri.fromFile(photo);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
				startActivityForResult(intent, PHOTO_CAPTURE);
				break;

		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		animateOnExit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		String sdStatus = Environment.getExternalStorageState();
		switch (requestCode) {
			case PHOTO_CAPTURE:
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
					Log.i("内存卡错误", "请检查您的内存卡");
				} else {
					BitmapFactory.Options op = new BitmapFactory.Options();
					// 设置图片的大小
					Bitmap bitMap = BitmapFactory.decodeFile(photoName);
					int width = bitMap.getWidth();
					int height = bitMap.getHeight();
					// 设置想要的大小
					int newWidth = 480;
					int newHeight = 640;
					// 计算缩放比例
					float scaleWidth = ((float) newWidth) / width;
					float scaleHeight = ((float) newHeight) / height;
					// 取得想要缩放的matrix参数
					Matrix matrix = new Matrix();
					matrix.postScale(scaleWidth, scaleHeight);
					// 得到新的图片
					bitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height,
							matrix, true);
					// canvas.drawBitmap(bitMap, 0, 0, paint)
					// 防止内存溢出
					op.inSampleSize = 4; // 这个数字越大,图片大小越小.
					Bitmap pic = null;
					pic = BitmapFactory.decodeFile(photoName, op);
//					face.setImageBitmap(pic); // 这个ImageView是拍照完成后显示图片

					FileOutputStream b = null;
					try {
						b = new FileOutputStream(photoName);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					if (pic != null) {
						pic.compress(Bitmap.CompressFormat.JPEG, 50, b);
					}

					dialog();
				}
				break;
			default:
				return;
		}
	}
	/**
	 * 上传脸部识别信息
	 */
	protected void dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("确认注册吗？");

		builder.setTitle("提示");

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				uploadFile();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.create().show();
	}
	/* 上传文件至Server的方法 */
	private void uploadFile() {
		System.out.print("正在发送请求！");
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		try {

			String urlpost=actionUrl;
			URL url = new URL(urlpost);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; "
					+ "name=\"file1\";filename=\"" + newName + "\"" + end);
			ds.writeBytes(end);
			/* 取得文件的FileInputStream */
			FileInputStream fStream = new FileInputStream(uploadFile);
			/* 设置每次写入1024bytes */
			System.out.print("已经找到数据正在发送！");
			int bufferSize = 1024*10;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			/* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

			/* close streams */
			fStream.close();
			ds.flush();
			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			/* 将Response显示于Dialog */

			/* 关闭DataOutputStream */
			ds.close();
			try {
				String password = mELPassword.getText();
				Log.i("system--------------", "uploadFile: "+password);
				InfoUrl=InfoUrl+"?username="+email+"&password="+password+"&img="+b.toString();

				Log.i("login", "uploadFile: "+InfoUrl);
				HttpURLConnection huc= (HttpURLConnection) new URL(InfoUrl).openConnection();
				huc.setDoInput(true);
				huc.setDoOutput(true);
				huc.setUseCaches(false);
				/* 设置传送的method=POST */
				huc.setRequestMethod("GET");
				huc.connect();
				InputStream is1 = huc.getInputStream();
				int ch1;
				StringBuffer b1 = new StringBuffer();
				while ((ch1 = is1.read()) != -1) {
					b1.append((char) ch1);
				}
				int result=Integer.parseInt(b1.toString());

				if(result>0) {
					showDialog("脸部信息存储成功,请继续！");

				}else {
					showDialog("脸部信息存储失败,请检查！");
				}



			} catch (Exception e) {
				e.printStackTrace();
			}


		} catch (Exception e) {
			System.out.print("网络出现异常！");
			showDialog("上传失败");
			e.printStackTrace();
		}
	}
	/* 显示Dialog的method */
	private void showDialog(String mess) {
		new AlertDialog.Builder(this).setTitle("提示")
				.setMessage(mess)
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	/**
	 * 界面退出时添加动画切换效果
	 */
	private void animateOnExit() {
		overridePendingTransition(R.anim.alpha_unchanged,
				R.anim.push_bottom_out);
	}

	/**
	 * 注册方法
	 */
	private void register() {
		//final String userName = mELUserName.getText();
		final String password = mELPassword.getText();
		final String password2 = mELPassword2.getText();

		tjYzm=registerYzm.getText().toString().trim();

		Log.i("msg","注册");

		if (!password.equals(password2)) {
			showToast("两次输入的密码不匹配");
			return;
		}

		if (TextUtils.isEmpty(password)
				|| TextUtils.isEmpty(password2)) {
			showToast("密码不能为空");
			return;
		}

		if(tjYzm.equals(yzm)){

			//信息上传到个人服务器
			new registerAsyncTask(email,password).execute(registerPath);
			Log.i("msg","验证码正确");

		}else {
			Toast.makeText(RegisterActivity.this,"你的验证码错误！",Toast.LENGTH_SHORT).show();
			return;
		}
	}


	/**
	 * 获取邮箱验证码
	 */
	class yzmAsyncTask extends AsyncTask<String,Void,String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			params[0]=params[0]+"?requestData="+email;

			try {

				HttpURLConnection httpURLConnection=(HttpURLConnection)new URL(params[0]).openConnection();
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setRequestMethod("GET");
				httpURLConnection.setReadTimeout(5000);
				BufferedReader bf = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
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

			if(s!=null){
				yzm=s;
				Log.i("msg","验证码为："+yzm);

			}
		}
	}

	/**
	 * 注册，上传信息到服务器
	 */
	class registerAsyncTask extends AsyncTask<String,Void,String>{


		String email1=null,password1=null;
		public registerAsyncTask(String email2,String password2){

			email1=email2;
			password1=password2;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			params[0]=params[0]+"?requestData="+email1+"@@"+password1;
			try {

				HttpURLConnection httpURLConnection=(HttpURLConnection)new URL(params[0]).openConnection();
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setRequestMethod("GET");
				httpURLConnection.setReadTimeout(5000);
				BufferedReader bf = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
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

			final String number=s;

				new Thread(new Runnable() {
					public void run() {
						try {
							// 调用sdk注册方法
							EMChatManager.getInstance().createAccountOnServer(number, password1);
							runOnUiThread(new Runnable() {
								public void run() {
									// 保存用户名
									showToast("注册成功");

									Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
									intent.putExtra("number",number);
									startActivity(intent);
									RegisterActivity.this.finish();
									animateOnExit();
								}
							});
						} catch (final EaseMobException e) {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {// 注册失败
									int errorCode = e.getErrorCode();
									if (errorCode == EMError.NONETWORK_ERROR) {
										showToast("网络异常，请检查网络！");
									} else if (errorCode == EMError.USER_ALREADY_EXISTS) {
										showToast("用户已存在！");
									} else if (errorCode == EMError.UNAUTHORIZED) {
										showToast("注册失败，无权限！");
									} else {
										showToast("注册失败: " + e.getMessage());
									}
								}
							});
						}
					}

				}).start();
		}
	}

}
