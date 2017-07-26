package com.forler.obd_ble.ble;

import android.os.Message;
import android.util.Log;

import com.forler.obd_ble.app.MyApplication;
import com.forler.obd_ble.utils.Byte2HexUtil;
import com.ritech.forler.itf.OnActionBleListener;
import com.ritech.forler.itf.OnBleRssiListener;

public class SPPAnalysisInterface implements OnActionBleListener, OnBleRssiListener{
	private String TAG = SPPAnalysisInterface.class.getSimpleName();
	private static SPPAnalysisInterface instance;
	public static SPPAnalysisInterface getInstance() {
		if (instance == null) {
			synchronized (SPPAnalysisInterface.class) {
				if (instance == null) {
					instance = new SPPAnalysisInterface();
				}	
			}
		}
		return instance;
	}

	@Override
	public void action(byte[] value, String uuid, String mac) {
		// TODO Auto-generated method stub
		Log.i(TAG, "接收数据："+Byte2HexUtil.byte2Hex(value));

		Message msg = new Message();
		msg.what = CommandConfig.BTR_ACTION_DATA;
		msg.obj = value;
		MyApplication.getInstance().pushHandler.sendMessage(msg);
		
	}

	@Override
	public void onRssi(int rssi) {
		// TODO Auto-generated method stub
		Log.i(TAG, "rssi="+rssi);
		Message msg = new Message();
		msg.what = 88;
		msg.arg1 = -1;
		msg.obj = "rssi="+rssi;
		MyApplication.getInstance().pushHandler.sendMessage(msg);
	}
	
	public interface OnDataSetListener{
		void onSetTime();
	}
	private OnDataSetListener mOnDataSetListener;
	public void setOnDataSetListener(OnDataSetListener listener){
		mOnDataSetListener = listener;
	}
	
	
}
