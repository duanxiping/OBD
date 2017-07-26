	package com.forler.obd_ble.ble;

import android.util.Log;

import com.forler.obd_ble.MControlProvider;
import com.forler.obd_ble.utils.Byte2HexUtil;

public class SPPDispatcher {
	private static String TAG = SPPDispatcher.class.getSimpleName();
	
	private static SPPDispatcher instance;
	public static SPPDispatcher getInstance() {
		if (instance == null) {
			synchronized (SPPDispatcher.class) {
				if (instance == null) {
					instance = new SPPDispatcher();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 设备是否连接
	 * @return
	 */
	public boolean isConnected(){
		if(MControlProvider.getInstance().getActionBleModel().getConnectionState() != 2){
			Log.d(TAG, "device is not connected");
			return false;
		}
		return true;
	}
	
	
	/**
	 * 校验和
	 * @param data
	 * @return
	 */
	private byte getChecksum(byte[] data) {
		byte sum = 0;
		for (byte d : data) {
			sum += d;
		}
		sum = (byte) (sum & 0xFF);
		return sum;
	}

	/**
	 * 手环测试
	 * @param context
	 * @param sn 0~15
	 * @param hour 0~3 *6
	 * @return
	 */
	public byte[] getActivityValue(int sn, int hour) {
		byte[] value = new byte[20];
		value[0] = 19;
		value[1] = (byte) 0x00;
		value[2] = (byte) hour;

		Log.i(TAG, Byte2HexUtil.byte2Hex(value));
		return value;
	}

	/**
	 * 2.获取手表时间
	 * @return
	 */
	public byte[] SPP_GET_TIME(){
		byte[] value = new byte[16];
		value[0] = (byte)0x41;		
		value[15] = getChecksum(value);
		Log.i(TAG, Byte2HexUtil.byte2Hex(value));
		return value;
	}
	
	
	public byte[] getValue(String data){
		byte[] value = (data+"\n").getBytes();
		Log.i(TAG, Byte2HexUtil.byte2Hex(value));
    	int n=0;
		for(int i=0;i<value.length;i++){
			if(value[i]==0x0a)n++;
		}
		byte[] value_new = new byte[value.length+n];
		n=0;
		for(int i=0;i<value.length;i++){ //手机中换行为0a,将其改为0d 0a后再发送
			if(value[i]==0x0a){
				value_new[n]=0x0d;
				n++;
				value_new[n]=0x0a;
			}else{
				value_new[n]=value[i];
			}
			n++;
		}
		Log.i(TAG, Byte2HexUtil.byte2Hex(value_new));
		return value_new;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
