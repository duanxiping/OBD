package com.forler.obd_ble.activitys;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forler.obd_ble.MControlProvider;
import com.forler.obd_ble.R;
import com.forler.obd_ble.adapters.ScanListAdapter;
import com.forler.obd_ble.app.MyApplication;
import com.forler.obd_ble.ble.CommandConfig;
import com.forler.obd_ble.ble.SPPDispatcher;
import com.forler.obd_ble.views.PromptDialog;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DeviceActivity extends Activity implements OnClickListener {
	private String TAG = DeviceActivity.class.getSimpleName();
	private RelativeLayout rl_dialog;
	private ListView lv_scan_device;
	private ScanListAdapter mScanListAdapter;
	private List<BluetoothDevice> list_scan;
	private Button btn_scan;
	private TextView tv_in_scan;

	private LinearLayout ll_in_scan;
	private TextView tv_in_search;
	private final long SCAN_PERIOD = 60*1000;//扫描时间
	private int DURATION = 1000 * 2;// 动画持续时间
	private Animation animation;//动画
	private String deviceAddress;
    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MyApplication.getInstance().pushHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (!Thread.currentThread().isInterrupted()) {
					handleOtherMessage(msg);
				}
			}
		};
		MyApplication.getInstance().pushHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				MControlProvider.getInstance().getActionBleModel().scanBle(true);
			}
		}, 300);
	}

	/**
	 * 消息
	 */
	protected void handleOtherMessage(Message msg) {
		switch (msg.what) {
		case CommandConfig.BTR_SCAN_START:
			tv_in_search.startAnimation(animation);
			Log.e(TAG, "xx开始扫描");
//			list_scan.clear();
			mScanListAdapter.notifyDataSetChanged();
			tv_in_scan.setText(getResources().getString(R.string.tv_in_scan));
			btn_scan.setText(getResources().getString(R.string.btn_cancel));
			break;
		case CommandConfig.BTR_SCAN_STOP:
			tv_in_search.clearAnimation();
			tv_in_search.setEnabled(false); 
			Log.e(TAG, "xx停止扫描");
			if(list_scan.size()==0){
				tv_in_scan.setText(getResources().getString(R.string.tv_no_devices_found));
			}
			btn_scan.setText(getResources().getString(R.string.btn_scan));
			break;
		case CommandConfig.BTR_SCAN_RESULT:
			BluetoothDevice device = (BluetoothDevice) msg.obj;
			int rssi = msg.arg1;
			addDevice(device);
			break;
		case CommandConfig.BTR_STATE_DEVICE_CONNECTED:
			Log.e(TAG, "BTR_STATE_DEVICE_CONNECTED");
			break;
		case CommandConfig.BTR_STATE_DEVICE_DISCONNECTED:
			Log.e(TAG, "BTR_STATE_DEVICE_DISCONNECTED");
			mScanListAdapter.notifyDataSetChanged();
//			PromptDialog.closeDialog();
//			mHandler.removeCallbacks(mRunnable);
//			PromptDialog.showProgressDialog(DeviceActivity.this, getResources().getString(R.string.device_disconnect), 0, false, null);
			break;
		case CommandConfig.BTR_SERVICES_DISCOVERED:
			Log.e(TAG, "BTR_SERVICES_DISCOVERED");
			PromptDialog.closeDialog();
			Intent data = new Intent(DeviceActivity.this, GattServicesActivity.class);
			startActivity(data);
			finish();
			break;
		case CommandConfig.BTR_NOTIFY_SET:
			Log.e(TAG, "BTR_NOTIFY_SET");
			break;
		default:
			break;
		}
	}
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);
		init();
		setListener();
	}

	private void init() {
		// TODO Auto-generated method stub
		rl_dialog=(RelativeLayout) findViewById(R.id.rl_dialog);
		lv_scan_device=(ListView) findViewById(R.id.lv_scan);
		list_scan=new ArrayList<BluetoothDevice>();
		mScanListAdapter=new ScanListAdapter(list_scan,this);
		lv_scan_device.setAdapter(mScanListAdapter);
		btn_scan=(Button) findViewById(R.id.btn_scan);
		tv_in_scan=(TextView) findViewById(R.id.tv_in_scan);

		ll_in_scan = (LinearLayout) findViewById(R.id.ll_in_scan);
		tv_in_search = (TextView) findViewById(R.id.tv_in_search);
		animation = AnimationUtils.loadAnimation(this, R.anim.scan_anim);
		/** 设置旋转动画 */
		animation.setDuration(DURATION);// 设置动画持续时间
		animation.setInterpolator(new LinearInterpolator());
		animation.setRepeatCount((int) ((SCAN_PERIOD / DURATION) - 1));// 设置重复次数
		animation.setFillAfter(true);// 动画执行完后是否停留在执行完的状态
	}
	
	private void setListener() {
		// TODO Auto-generated method stub
		rl_dialog.setOnClickListener(this);
		btn_scan.setOnClickListener(this);
		lv_scan_device.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				mHandler.removeCallbacks(mRunnable);
				BluetoothDevice device = list_scan.get(position);
				deviceAddress = device.getAddress();
				if (MControlProvider.getInstance().getActionBleModel().isScanning()) {
					MControlProvider.getInstance().getActionBleModel().scanBle(false);
				}
				MControlProvider.getInstance().getActionBleModel().connect(deviceAddress);
				PromptDialog.showProgressDialog(DeviceActivity.this, "", R.drawable.loading, true, null);
				mHandler.postDelayed(mRunnable, 20000);
			}
		});
	}

	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			PromptDialog.closeDialog();
			PromptDialog.showProgressDialog(DeviceActivity.this, getResources().getString(R.string.device_connect_timeout), 0, false, null);
			if(SPPDispatcher.getInstance().isConnected()){
				MControlProvider.getInstance().getActionBleModel().disconnect();
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_dialog:
			
			break;
		case R.id.btn_scan:
			String btn_scan_state=btn_scan.getText().toString();
			if(btn_scan_state.equals(getResources().getString(R.string.btn_scan))){
				//开始扫描
				if (MControlProvider.getInstance().getActionBleModel().getBleState()==0)
					MControlProvider.getInstance().getActionBleModel().scanBle(true);
			}else if(btn_scan_state.equals(getResources().getString(R.string.btn_cancel))){
				//停止扫描
				if (MControlProvider.getInstance().getActionBleModel().getBleState()==0)
					MControlProvider.getInstance().getActionBleModel().scanBle(false);
			} 
			break;

		default:
			break;
		}
	}

	private void addDevice(BluetoothDevice device) {
		// TODO Auto-generated method stub
		boolean deviceFound = false; 
		for(BluetoothDevice mDevice:list_scan) {
			if(mDevice.getAddress().equals(device.getAddress())){
				deviceFound=true;
			}
		}
		if(!deviceFound){
			Log.e("TAG", "list_scan+扫描到蓝牙"+device.getAddress());
			Log.e("TAG", "list_scan.size="+list_scan.size());
			list_scan.add(device);
			mScanListAdapter.notifyDataSetChanged();
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		finish(); 
		return super.onTouchEvent(event);
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHandler.removeCallbacks(mRunnable);
		if (MControlProvider.getInstance().getActionBleModel().isScanning())
			MControlProvider.getInstance().getActionBleModel().scanBle(false);
	}
	
}
