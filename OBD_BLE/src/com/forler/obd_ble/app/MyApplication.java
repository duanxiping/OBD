package com.forler.obd_ble.app;

import org.xutils.BuildConfig;
import org.xutils.x;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;

import com.forler.obd_ble.MControlService;

public class MyApplication extends Application {

	private static MyApplication mInstance;
	public static synchronized MyApplication getInstance() {
		return mInstance;
	}
	
	//数据分发
	public Handler pushHandler; 
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		pushHandler = new Handler();
		
		// xutils初始化
		x.Ext.init(this);
		x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
		
		Intent intent = new Intent(this, MControlService.class);
		startService(intent);
	}

}
