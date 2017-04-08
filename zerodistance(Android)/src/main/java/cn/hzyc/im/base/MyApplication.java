package cn.hzyc.im.base;

import android.app.Application;

import com.techshino.eyekeysdk.api.CheckAPI;


public class MyApplication extends Application {
	
    @Override
    public void onCreate() {
        super.onCreate();
        Global.initialize(this);
        //初始化环信SDK,一定要先调用init()
        ImHelper.getInstance().initialize();
        // 初始化eyekey接口 （需在AndroidManifest.xml中添加appid和appkey）
        CheckAPI.init(getApplicationContext());
    }
    
}
