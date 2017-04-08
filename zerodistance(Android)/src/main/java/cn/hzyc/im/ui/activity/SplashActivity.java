package cn.hzyc.im.ui.activity;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import cn.hzyc.im.R;
import cn.hzyc.im.base.ImHelper;
import cn.hzyc.im.po.IpAddress;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.techshino.eyekeysdk.api.CheckAPI;
import com.techshino.eyekeysdk.entity.FaceAttrs;
import com.techshino.eyekeysdk.entity.MatchCompare;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 开屏页
 */
public class SplashActivity extends Activity {

	private static final int sleepTime = 2000;



//	人脸上识别的变量
private static final int PHOTO_CAPTURE = 0x11;
	private static String photoPath = "/sdcard/AnBo/";
	private static String photoName = photoPath + "laolisb.jpg";
	Uri imageUri = Uri.fromFile(new File(Environment
			.getExternalStorageDirectory(), "image.jpg"));
	private String newName = "laoli.jpg";
	private String uploadFile = "/sdcard/AnBo/laolisb.jpg";
	private String InfoUrl = "http://"+ IpAddress.ipaddress+":8080/faceTest/LoginServlet";
	private String getEmailUrl = "http://"+ IpAddress.ipaddress+":8080/ZeroDistance/user/getEmailToUsername.action";
	private String mFaceId1;
	private String mFaceId2;
	private String mImgBase641;
	private String mImgBase642;
	private String email;
	private static double simple = 0;

	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.activity_splash);
		super.onCreate(arg0);

		//人脸识别信息加载
		// 初始化eyekey接口 （需在AndroidManifest.xml中添加appid和appkey）
		CheckAPI.init(this);

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

		// 进入此界面后有一个渐变的效果
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1000);
		findViewById(R.id.root).startAnimation(animation);
		String currentUserName = ImHelper.getInstance().getCurrentUsernName();
		new GetEmailToUsernameAsyncTask(currentUserName).execute(getEmailUrl);


		//在这个地方加入人脸识别功能
		uploadFile();

	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	private void goToMainActivity() {
		startActivity(new Intent(SplashActivity.this,MainActivity.class));
		finish();
	}

	private void goToLoginActivity() {
		startActivity(new Intent(SplashActivity.this,
				LoginActivity.class));
		finish();
	}




	/**
	 * 根据url从网络获取图片
	 * @param url 网络图片地址
	 * @return Bitmap
	 */
	public Bitmap getBitmapToUrl(String url){

		InputStream is = null;
		try {
			HttpURLConnection connection= (HttpURLConnection) new URL(url).openConnection();
			is=connection.getInputStream();
			Bitmap bitmap= BitmapFactory.decodeStream(is);
			connection.disconnect();
			return bitmap;

		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 获取本地图片
	 */
	private void getLocalIImg() {

		try {
			FileInputStream fStream = new FileInputStream(uploadFile);
			Bitmap bitmap= BitmapFactory.decodeStream(fStream);
//			imgtest.setImageBitmap(bitmap);
			mImgBase641 = bitmapToBase64(bitmap);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/* 上传文件至Server的方法 */
	private void uploadFile() {
		//调用摄像头的意图
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		//将获取的图片放到photoPath文件下
		File file = new File(photoPath);
		if (!file.exists()) { // 检查图片存放的文件夹是否存在
			file.mkdir(); // 不存在的话 创建文件夹
		}
		File photo = new File(photoName);
		imageUri = Uri.fromFile(photo);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
		startActivityForResult(intent, PHOTO_CAPTURE);
	}

	//意图响应结果
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
					try{
						InfoUrl=InfoUrl+"?loginname="+email;
						Log.i("login", "uploadFile: "+InfoUrl);
						HttpURLConnection huc= (HttpURLConnection) new URL(InfoUrl).openConnection();
						huc.setDoInput(true);
						huc.setDoOutput(true);
						huc.setUseCaches(false);
			            /* 设置传送的method=GET */
						huc.setRequestMethod("GET");
						huc.connect();
						InputStream is1 = huc.getInputStream();
						int ch1;
						StringBuffer b1 = new StringBuffer();
						while ((ch1 = is1.read()) != -1) {
							b1.append((char) ch1);
						}
						//获取网络图片位置
						String imgurl=b1.toString();

						//根据网络位置获取bitmap图片
						Bitmap bitmap= getBitmapToUrl(imgurl);
						mImgBase642=bitmapToBase64(bitmap);
//						imgtest1.setImageBitmap(bitmap);
						//获取本地图片
						getLocalIImg();
						//开始比较
						startCompare();
						if(imgurl!=null) {
						}else {
							showDialog("获取图片失败");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					FileOutputStream b = null;

					try {
						b = new FileOutputStream(photoName);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					if (pic != null) {
						pic.compress(Bitmap.CompressFormat.JPEG, 50, b);
					}
				}
				break;
			default:
				return;
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

	private void startCompare() {
		if ("".equals(mImgBase641) || mImgBase641 == null || "".equals(mImgBase642) || mImgBase642 == null) {
			Toast.makeText(this, "请选择图片再比对", Toast.LENGTH_SHORT).show();
			return;
		}
		getFaceId1();
	}
	void getFaceId1() {
		CheckAPI.checkingImageData(mImgBase641, null, null).enqueue(new Callback<FaceAttrs>() {
			@Override
			public void onResponse(Call<FaceAttrs> call, Response<FaceAttrs> response) {
				FaceAttrs faceAttrs = response.body();
				if (faceAttrs != null && "0000".equals(faceAttrs.getRes_code())) {
					mFaceId1 = faceAttrs.getFace().get(0).getFace_id();
					getFaceId2();
				} else {
					showDialog("人脸1检测失败...");
				}
			}

			@Override
			public void onFailure(Call<FaceAttrs> call, Throwable t) {
				showDialog("网络出错...");
			}
		});
	}

	void getFaceId2() {
		CheckAPI.checkingImageData(mImgBase642, null, null).enqueue(new Callback<FaceAttrs>() {
			@Override
			public void onResponse(Call<FaceAttrs> call, Response<FaceAttrs> response) {
				FaceAttrs faceAttrs = response.body();
				if (faceAttrs != null && "0000".equals(faceAttrs.getRes_code())) {
					mFaceId2 = faceAttrs.getFace().get(0).getFace_id();
					compare();
				} else {
					showDialog("人脸2检测失败......");
				}
			}

			@Override
			public void onFailure(Call<FaceAttrs> call, Throwable t) {
				showDialog("网络出错...");
			}
		});
	}

	void compare() {
		CheckAPI.matchCompare(mFaceId1, mFaceId2).enqueue(new Callback<MatchCompare>() {
			@Override
			public void onResponse(Call<MatchCompare> call, Response<MatchCompare> response) {
				MatchCompare compare = response.body();
				if (compare != null && "0000".equals(compare.getRes_code())) {
					//mResultText.setText(compare.toString());

					Log.i("test",compare.toString());
					simple = compare.getSimilarity();
					showDialog("相识度为："+simple);
					new Thread(new Runnable() {
						public void run() {
							if (EMChat.getInstance().isLoggedIn() && simple>85) {
								// ** 免登陆情况 加载所有本地群和会话
								// 不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
								// 加上的话保证进了主页面会话和群组都已经load完毕
								long start = System.currentTimeMillis();
								EMChatManager.getInstance().loadAllConversations();

								long costTime = System.currentTimeMillis() - start;
								if (sleepTime - costTime > 0) {// 等待sleeptime时长
									SystemClock.sleep(sleepTime - costTime);
								}
//					// 进入主页面
								goToMainActivity();
							} else {
								SystemClock.sleep(sleepTime);
								goToLoginActivity();
							}
						}
					}).start();
				} else {
					//mResultText.setText("比对失败...");
				}
			}

			@Override
			public void onFailure(Call<MatchCompare> call, Throwable t) {
				showDialog("网络出错！");
			}
		});
	}

	private String bitmapToBase64(Bitmap bitmap) {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bStream);
		return Base64.encodeToString(bStream.toByteArray(), 0);
	}



	class GetEmailToUsernameAsyncTask extends AsyncTask<String,Void,String> {
		String username="";
		public GetEmailToUsernameAsyncTask(String username){

			this.username=username;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {

			try {
				HttpURLConnection hc = (HttpURLConnection) new URL(getEmailUrl + "?requestData=" + username).openConnection();
				hc.setRequestMethod("GET");
				hc.setDoOutput(true);
				hc.setReadTimeout(5000);
				BufferedReader bf = new BufferedReader(new InputStreamReader(hc.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String str = "";
				while ((str = bf.readLine()) != null) {
					sb.append(str);
				}
				email = sb.toString();
				return sb.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "";
		}
		}

}
