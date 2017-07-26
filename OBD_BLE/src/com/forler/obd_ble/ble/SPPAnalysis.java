package com.forler.obd_ble.ble;

import android.util.Log;

import com.forler.obd_ble.utils.Byte2HexUtil;

public class SPPAnalysis {
	private static String TAG = SPPAnalysis.class.getSimpleName();
	
	public static boolean abc(){
		
		return false;
	}
	
	/**
	 * 校验
	 * @param buffer
	 * @return
	 */
	public static boolean checksum(byte[] buffer) {
		int len = buffer.length;
		//校验和验证
		byte sum = 0;
		for (int i = 0; i < len-1; i++) {
			sum += buffer[i];
		}
		int _sum = sum & 0xff;
		int checksum = buffer[15] & 0xff;
		Log.d(TAG, "校验和checksum = "+checksum+"，总和_sum = "+_sum);
		if (_sum != checksum) {
			return false;
		}
		return true;
	}

	/**
	 * 2.获取手环时间
	 * @param value
	 * @return
	 */
	public static String SPP_GET_TIME(byte[] value){
		String valueStr = Byte2HexUtil.byte2Hex(value);
		String[] values = valueStr.split(" ", -1);
		String date = "20"+values[1]+"-"+values[2]+"-"+values[3]+" "+values[4]+":"+values[5]+":"+values[6];
		return date;
	}

	
}
