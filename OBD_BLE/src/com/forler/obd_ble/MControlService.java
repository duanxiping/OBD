package com.forler.obd_ble;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MControlService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		MControlProvider.getInstance().initialization(this);
			
	}
}
