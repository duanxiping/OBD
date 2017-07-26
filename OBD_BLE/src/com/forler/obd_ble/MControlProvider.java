package com.forler.obd_ble;

import org.xutils.x;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.forler.obd_ble.app.MyApplication;
import com.forler.obd_ble.ble.CommandConfig;
import com.forler.obd_ble.ble.SPPAnalysisInterface;
import com.forler.obd_ble.ble.UUIDConfig;
import com.forler.obd_ble.ble.SPPAnalysisInterface.OnDataSetListener;
import com.forler.obd_ble.db.SharedPreferencesSetting;
import com.ritech.forler.itf.OnBleConnectListener;
import com.ritech.forler.itf.OnBleDiscoveredListener;
import com.ritech.forler.itf.OnBleScanListener;
import com.ritech.forler.itf.OnServiceNotifyListener;
import com.ritech.forler.model.ActionBleModel;

public class MControlProvider {
	private String TAG = MControlProvider.class.getSimpleName();
	
	private static MControlProvider instance;	
	public static MControlProvider getInstance() {
		if (instance == null) {
			synchronized (MControlProvider.class) {
				if (instance == null) {
					instance = new MControlProvider();
				}
			}
		}
		return instance;
	}
	
	private Context mContext;
	
	//设备MAC
	private String deviceAddress;
	public String getDeviceAddress() {
		return deviceAddress;
	}
	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
		if(TextUtils.isEmpty(deviceAddress)){
			SharedPreferencesSetting.getInstance(x.app()).remove(SharedPreferencesSetting.deviceAddress);
		} else {
			SharedPreferencesSetting.getInstance(x.app()).setStr(SharedPreferencesSetting.deviceAddress, deviceAddress);
		}
	}

	//蓝牙Model
	private ActionBleModel mActionBleModel;
	public ActionBleModel getActionBleModel() {
		return mActionBleModel;
	}
	
    
	public void initialization(Context context) {
		// TODO Auto-generated method stub
		Log.i(TAG, "initialization");
		mContext = context;
		
		//初始化蓝牙Model
		mActionBleModel = new ActionBleModel();
		mActionBleModel.setupModel(mContext);
		mActionBleModel.setOnServiceNotifyListener(new OnServiceNotifyListener() {
			
			@Override
			public void onServiceConnect() {
				// TODO Auto-generated method stub
				mActionBleModel.setOnBleScanListener(mOnBleScanListener, UUIDConfig.deviceTAG);
				mActionBleModel.setOnBleConnectListener(mOnBleConnectListener);
				mActionBleModel.setOnBleDiscoveredListener(mOnBleDiscoveredListener);
				mActionBleModel.isSetNotify(false);
				SPPAnalysisInterface.getInstance().setOnDataSetListener(mOnDataSetListener);
				
				mActionBleModel.setOnActionBleListener(SPPAnalysisInterface.getInstance());
				mActionBleModel.setOnBleRssiListener(SPPAnalysisInterface.getInstance());
//				mActionBleModel.startA2DPservice(true);

			}
		});
		
	}
	
	public void connect(){
		if(mActionBleModel.isScanning()){
			mActionBleModel.scanBle(false);
		}
		mActionBleModel.connect(deviceAddress);
	}
	
	public void disconnect(){
		if(mActionBleModel.getConnectionState()==2){
			MControlProvider.getInstance().getActionBleModel().disconnect();
		}
		MyApplication.getInstance().pushHandler.sendEmptyMessage(CommandConfig.BTR_STATE_DEVICE_DISCONNECTED);
	}

	public void setUUID(){
		mActionBleModel.setUUID(UUIDConfig.UUID_SERVICE, 
				UUIDConfig.UUID_CHARACTERISTIC_NOTIFI, 
				UUIDConfig.UUID_CHARACTERISTIC_SEND);
	}
	
	//蓝牙搜索监听
	public OnBleScanListener mOnBleScanListener = new OnBleScanListener() {
		
		@Override
		public void scanResult(BluetoothDevice device, int rssi) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.what = CommandConfig.BTR_SCAN_RESULT;
			msg.obj = device;
			msg.arg1 = rssi;
			MyApplication.getInstance().pushHandler.sendMessage(msg);
		}

		@Override
		public void start() {
			// TODO Auto-generated method stub
			Log.e(TAG, "scan start");
			MyApplication.getInstance().pushHandler.sendEmptyMessage(CommandConfig.BTR_SCAN_START);
		}

		@Override
		public void stop() {
			// TODO Auto-generated method stub
			Log.e(TAG, "scan stop");
			MyApplication.getInstance().pushHandler.sendEmptyMessage(CommandConfig.BTR_SCAN_STOP);
		}
	};
	
	//蓝牙连接监听
	private OnBleConnectListener mOnBleConnectListener = new OnBleConnectListener() {
		
		@Override
		public void onConnect(boolean isConnect, BluetoothDevice device) {
			// TODO Auto-generated method stub
			Log.e(TAG, "isConnect = "+isConnect);
			if (isConnect) {
				MyApplication.getInstance().pushHandler.sendEmptyMessage(CommandConfig.BTR_STATE_DEVICE_CONNECTED);
				setDeviceAddress(device.getAddress());
			} else {
				MyApplication.getInstance().pushHandler.sendEmptyMessage(CommandConfig.BTR_STATE_DEVICE_DISCONNECTED);
			}
			
		}
	};
	

	// 迭代服务成功监听
	private OnBleDiscoveredListener mOnBleDiscoveredListener = new OnBleDiscoveredListener(){

		@Override
		public void onDiscoveredServices() {
			// TODO Auto-generated method stub
			Log.e(TAG, "onDiscoveredServices()");
			MyApplication.getInstance().pushHandler.sendEmptyMessage(CommandConfig.BTR_SERVICES_DISCOVERED);
		}
		
		@Override
		public void onNotifySet() {
			// TODO Auto-generated method stub
			Log.e(TAG, "onNotifySet()");
			MyApplication.getInstance().pushHandler.sendEmptyMessage(CommandConfig.BTR_NOTIFY_SET);
		}
		
	};


	//数据监听
	private OnDataSetListener mOnDataSetListener = new OnDataSetListener() {
		
		@Override
		public void onSetTime() {
			// TODO Auto-generated method stub
			Log.i(TAG, "设备时间设置成功");
		}
	};

	
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}

}
