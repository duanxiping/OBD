package com.forler.obd_ble.ble;

public class CommandConfig {
	//命令返回
	public final static byte BTR_SCAN_START = (byte)0xa1;//开始搜索设备
	public final static byte BTR_SCAN_STOP = (byte)(BTR_SCAN_START+1);//停止搜索设备
	public final static byte BTR_SCAN_RESULT = (byte)(BTR_SCAN_STOP+1);//搜索到设备
	public final static byte BTR_STATE_DEVICE_CONNECTED = (byte)(BTR_SCAN_RESULT+1);//蓝牙连接成功
	public final static byte BTR_STATE_DEVICE_DISCONNECTED = (byte)(BTR_STATE_DEVICE_CONNECTED+1);//蓝牙连接失败
	public final static byte BTR_SERVICES_DISCOVERED = (byte)(BTR_STATE_DEVICE_DISCONNECTED+1);//迭代服务成功
	public final static byte BTR_NOTIFY_SET = (byte)(BTR_SERVICES_DISCOVERED+1);//通知成功
	public final static byte BTR_ACTION_DATA = (byte)(BTR_NOTIFY_SET+1);//获取到数据
	

}
