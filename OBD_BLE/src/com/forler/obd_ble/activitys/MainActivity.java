package com.forler.obd_ble.activitys;

import java.util.ArrayList;
import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.forler.obd_ble.MControlProvider;
import com.forler.obd_ble.R;
import com.forler.obd_ble.adapters.DataListAdapter;
import com.forler.obd_ble.app.MyApplication;
import com.forler.obd_ble.ble.CommandConfig;
import com.forler.obd_ble.ble.SPPDispatcher;
import com.forler.obd_ble.entity.DataInfo;
import com.forler.obd_ble.views.CommonHeadView;
import com.forler.obd_ble.views.CommonHeadView.OnClickLeftBtnListener;
import com.forler.obd_ble.views.CommonHeadView.OnClickRightBtnListener;
import com.forler.obd_ble.views.DashboardView;
import com.forler.obd_ble.views.HighlightCR;
import com.forler.obd_ble.views.PromptDialog;
import com.forler.obd_ble.views.PromptDialog.OnDialogClickListener;

public class MainActivity extends Activity implements OnClickLeftBtnListener, OnClickRightBtnListener {
	private String TAG = MainActivity.class.getSimpleName();
	private Button leftBtn, rightBtn;

	@ViewInject(R.id.dashboard_view)
	private DashboardView dashboardView;
	@ViewInject(R.id.main_speed)
	private TextView main_speed;//车辆速度/KM
	@ViewInject(R.id.main_voltage)
	private TextView main_voltage;//电瓶电压/V
	@ViewInject(R.id.main_temp)
	private TextView main_temp;//水温/°C
	@ViewInject(R.id.main_displacement_ll)
	private LinearLayout main_displacement_ll;
	@ViewInject(R.id.main_displacement)
	private TextView main_displacement;
	@ViewInject(R.id.main_acceleration)
	private TextView main_acceleration;//瞬时耗油/G
	@ViewInject(R.id.main_acceleration_unit)
	private TextView main_acceleration_unit;//瞬时耗油单位
	@ViewInject(R.id.main_load)
	private TextView main_load;//发动机负荷
	@ViewInject(R.id.main_throttle_percentage)
	private TextView main_throttle_percentage;//节气门开度
	@ViewInject(R.id.main_mileage)
	private TextView main_mileage;//此次行车里程
	@ViewInject(R.id.main_oil_wear)
	private TextView main_oil_wear;//此次油耗
	@ViewInject(R.id.main_speed_num)
	private TextView main_speed_num;//急加速次数
	@ViewInject(R.id.main_brake_num)
	private TextView main_brake_num;//急刹车次数
	@ViewInject(R.id.main_drive_time)
	private TextView main_drive_time;//此次行车时间
	@ViewInject(R.id.main_device_temp)
	private TextView main_device_temp;//车内设备温度
	@ViewInject(R.id.main_bug_num)
	private TextView main_bug_num;//故障码个数
	@ViewInject(R.id.main_bug_content)
	private TextView main_bug_content;//故障码
	@ViewInject(R.id.main_bug_data)
	private TextView main_bug_data;//故障码具体含义
	@ViewInject(R.id.main_lv_data)
	private ListView mListView;//数据详情
	@ViewInject(R.id.main_lv_btn1)
	private TextView main_lv_btn1;//滚动ListView
	@ViewInject(R.id.main_lv_btn2)
	private TextView main_lv_btn2;//停止滚动ListView
	private DataListAdapter mAdapter;
	private ArrayList<DataInfo> mDatas;
	private double displacement = 1.6;
	private boolean isBegin, isATDTC, isATFCDTC;
	private String dataStr;
	private String atdtcStr;
	/**
	 * 消息
	 */
	protected void handleOtherMessage(Message msg) {
		switch (msg.what) {
		case CommandConfig.BTR_STATE_DEVICE_CONNECTED:
			Log.e(TAG, "BTR_STATE_DEVICE_CONNECTED");
			rightBtn.setBackgroundResource(R.drawable.btn_bluetooth_pressed);
			break;
		case CommandConfig.BTR_STATE_DEVICE_DISCONNECTED:
			Log.e(TAG, "BTR_STATE_DEVICE_DISCONNECTED");
			rightBtn.setBackgroundResource(R.drawable.btn_bluetooth_normal);
			PromptDialog.showProgressDialog(MainActivity.this, getResources().getString(R.string.device_disconnect), 0, false, null);
			break;
		case CommandConfig.BTR_SERVICES_DISCOVERED:
			Log.e(TAG, "BTR_SERVICES_DISCOVERED");
			break;
			
		case CommandConfig.BTR_ACTION_DATA:
			byte[] value = (byte[]) msg.obj;
			String s = new String(value,0,value.length);
			Log.e(TAG, "消息s : "+s);
			
			if(s.contains("BD$")){
				isBegin = true;
				dataStr = "";
				if(isATDTC){
					atdtcStr += s;
					setAtdtcData();
					isATDTC = false;
				}
			} 
			if(!isBegin){
				return;
			}
			dataStr += s;
			if(s.contains("@")){
				Log.i(TAG, "数据dataStr : "+dataStr);
				setData();
			}
			
			if(s.contains("#")){
				isATDTC = true;
				atdtcStr = "";
			}
			if(isATDTC){
				atdtcStr += s;
			}

//BD$V12.3;R00676;S000;P001.1;O073.3;C105;L050.1;XH001.071;M000000;F000.356;T0001197;A00;B00;D00;U36.0;GX-3;GY-66;GZ2;@1
//BD$V12.3;R00676;S000;P001.1;O073.3;C105;L050.1;XH001.071;M000000;F000.356;T0001198;A00;B00;D00;U36.5;GX-3;GY-66;GZ2;@2
//BD$V12.3;R00680;S000;P001.1;O073.3;C105;L050.1;XH001.071;M000000;F000.356;T0001199;A00;B00;D00;U36.0;GX-3;GY-66;GZ2;@3
//BD$V12.3;R00680;S000;P001.1;O073.3;C105;L050.1;XH001.071;M000000;F000.356;T0001200;A00;B00;D00;U36.0;GX-3;GY-66;GZ2;@4

			break;
		}
	}
	
	private void setAtdtcData(){
		if(!isATFCDTC){
			String data = "0000";
			String atdtc[] = atdtcStr.split("&", -1);
			if(atdtc.length>1){
				data = "";
				for (int i = 1; i < atdtc.length; i++) {
					if(atdtc[i].contains("P")){
						data = data+"&"+atdtc[i].substring(0, 5);
					}
				}
			}
			main_bug_data.setText(data);
		}
	}
	
	private boolean isScroll = true;
	private int dataNum;
	public void setData(){
//		if(dataNum%3==0)
//			dataStr = "BD$V12.3;R00676;S000;P001.1;O073.3;C105;L050.1;XH001.071;M000000;F000.356;T0001197;A00;B00;D00;U36.0;GX-3;GY-66;GZ2;@1";
//		else if(dataNum%3==1)
//			dataStr = "BD$V12.6;R00987;S002;P003.1;O088.5;C125;L050.1;XM001.771;M000000;F000.466;T0001927;A01;B02;D03;U36.3;GX-3;GY-66;GZ2;@1";
//		else
////			dataStr = "RDTC:,&P0162;PENDING DTC:&P0102";
//			dataStr = "BD$V12.6;R01234;S002;P003.1;O088.5;C125;L050.1;YM001.771;M000000;F000.466;T0001927;A01;B02;D03;U36.3;GX-3;GY-66;GZ2;@1";
		
		if(!TextUtils.isEmpty(dataStr) && dataStr.contains("BD$") && dataStr.contains("@")){
			dataNum++;
			DataInfo info = new DataInfo();
			info.setData(dataNum+"."+dataStr);
			mDatas.add(info);
			if(mDatas.size()>1000){
				mDatas.remove(0);
			}
			mAdapter.changeDataSet(mDatas);
			if(isScroll){
				mListView.setSelection(mListView.getCount() - 1);
			}
			
			String[] dataStrs = dataStr.split(";", -1);
			if(dataStrs.length<17){
				return;
			}
			String data = dataStrs[0];
			if(data.contains("BD$V")){
				String str = data.substring(4);
				main_voltage.setText(str);
			} else {
				Log.e(TAG, "data = "+data);
			}
			data = dataStrs[1];
			if(data.contains("R")){
				String str = data.substring(1);
				int r = Integer.valueOf(str);
//				dashboardView.setRealTimeValue(r, true, 500);
				dashboardView.setRealTimeValue(r, false, 500);
			} else {
				Log.e(TAG, "data = "+data);
			}
			data = dataStrs[2];
			if(data.contains("S")){
				String str = data.substring(1);
				int s = Integer.valueOf(str);
				main_speed.setText(String.valueOf(s));
			} else {
				Log.e(TAG, "data = "+data);
			}
			data = dataStrs[3];
			if(data.contains("P")){
				String str = data.substring(1);
				double p = Double.valueOf(str);
				main_throttle_percentage.setText(String.valueOf(p)+getResources().getString(R.string.main_load_unit));
			} else {
				Log.e(TAG, "data = "+data);
			}
			data = dataStrs[4];
			if(data.contains("O")){
				String str = data.substring(1);
				double o = Double.valueOf(str);
				main_load.setText(String.valueOf(o)+getResources().getString(R.string.main_load_unit));
			} else {
				Log.e(TAG, "data = "+data);
			}
			data = dataStrs[5];
			if(data.contains("C")){
				String str = data.substring(1);
				int c = Integer.valueOf(str);
				main_temp.setText(String.valueOf(c));
			} else {
				Log.e(TAG, "data = "+data);
			}
			data = dataStrs[6];
			if(data.contains("L")){
				String str = data.substring(1);
				double l = Double.valueOf(str);
	//			main_oil_wear.setText(String.valueOf(l));
			} else {
				Log.e(TAG, "data = "+data);
			}
			data = dataStrs[7];
			if(data.contains("XH")){
				String str = data.substring(2);
				double xh = Double.valueOf(str);
				main_acceleration.setText(String.valueOf(xh));
				main_acceleration_unit.setText(getResources().getString(R.string.main_acceleration_unit));
			} else if(data.contains("YH")){
				String str = data.substring(2);
				double yh = Double.valueOf(str)*displacement;
				main_acceleration.setText(String.valueOf(yh));
				main_acceleration_unit.setText(getResources().getString(R.string.main_acceleration_unit));
			} else if(data.contains("XM")){
				String str = data.substring(2);
				double xm = Double.valueOf(str);
				main_acceleration.setText(String.valueOf(xm));
				main_acceleration_unit.setText(getResources().getString(R.string.main_acceleration_unit_1));
			} else if(data.contains("YM")){
				String str = data.substring(2);
				double ym = Double.valueOf(str)*displacement;
				main_acceleration.setText(String.valueOf(ym));
				main_acceleration_unit.setText(getResources().getString(R.string.main_acceleration_unit_1));
			} else {
				Log.e(TAG, "data = "+data);
			}
			data = dataStrs[8];
			if(data.contains("M")){
				String str = data.substring(1);
				int m = Integer.valueOf(str);
				main_mileage.setText(String.valueOf(m)+getResources().getString(R.string.main_mileage_unit));
			} else {
				Log.e(TAG, "data = "+data);
			}
			data = dataStrs[9];
			if(data.contains("F")){
				String str = data.substring(1);
				double f = Double.valueOf(str);
				main_oil_wear.setText(String.valueOf(f)+getResources().getString(R.string.main_oil_wear_unit));
			} else {
				Log.e(TAG, "data = "+data);
			}
			data = dataStrs[10];
			if(data.contains("T")){
				String str = data.substring(1);
				int t = Integer.valueOf(str);
				main_drive_time.setText(String.valueOf(t)+getResources().getString(R.string.main_drive_time_unit));
			} else {
				Log.e(TAG, "data = "+data);
			}
			data = dataStrs[11];
			if(data.contains("A")){
				String str = data.substring(1);
				int a = Integer.valueOf(str);
				main_speed_num.setText(String.valueOf(a)+getResources().getString(R.string.main_speed_num_unit));
			} else {
				Log.e(TAG, "data = "+data);
			}
			data = dataStrs[12];
			if(data.contains("B")){
				String str = data.substring(1);
				int b = Integer.valueOf(str);
				main_brake_num.setText(String.valueOf(b)+getResources().getString(R.string.main_speed_num_unit));
			} else {
				Log.e(TAG, "data = "+data);
			}
			data = dataStrs[13];
			if(data.contains("D")){
				String str = data.substring(1, 3);
				int d = Integer.valueOf(str);
				main_bug_num.setText(String.valueOf(d)+getResources().getString(R.string.main_bug_num_unit));
			} else {
				Log.e(TAG, "data = "+data);
			}
			data = dataStrs[14];
			if(data.contains("U")){
				String str = data.substring(1);
				double u = Double.valueOf(str);
				main_device_temp.setText(String.valueOf(u)+getResources().getString(R.string.main_device_temp_unit));
			} else {
				Log.e(TAG, "data = "+data);
			}
	//		data = dataStrs[15];
	//		if(data.contains("GX")){
	//			String str = data.substring(1);
	//		} else {
	//			Log.e(TAG, "data = "+data);
	//		}
	//		data = dataStrs[16];
	//		if(data.contains("GY")){
	//			String str = data.substring(1);
	//		} else {
	//			Log.e(TAG, "data = "+data);
	//		}
	//		data = dataStrs[17];
	//		if(data.contains("GZ")){
	//			String str = data.substring(1);
	//		} else {
	//			Log.e(TAG, "data = "+data);
	//		}
		} 
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MyApplication.getInstance().pushHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (!Thread.currentThread().isInterrupted()) {
					handleOtherMessage(msg);
				}
			}
		};
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		if(SPPDispatcher.getInstance().isConnected()){
			rightBtn.setBackgroundResource(R.drawable.btn_bluetooth_pressed);
		} else {
			rightBtn.setBackgroundResource(R.drawable.btn_bluetooth_normal);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		CommonHeadView mCommonHeadView = new CommonHeadView(this, CommonHeadView.TYPE_RIGHT_BTN, R.string.setting_title_tv);
		View view = mCommonHeadView.setContentView(true, R.layout.activity_main);
		mCommonHeadView.setOnClickLeftListener(this);
		mCommonHeadView.setOnClickRightListener(this);
		leftBtn = mCommonHeadView.getLeftButton();
//		leftBtn.setBackgroundResource(R.drawable.btn_bluetooth_normal);
		rightBtn = mCommonHeadView.getRightButton();
		rightBtn.setBackgroundResource(R.drawable.btn_bluetooth_normal);
		setContentView(view);
		x.view().inject(this);
		initView();
		
	}

	private void initView() {
		main_displacement.setText(String.valueOf(displacement));
		
		dashboardView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Random random = new Random();
//				int ranValue = random.nextInt(dashboardView.getMaxValue()
//						- dashboardView.getMinValue())
//						+ dashboardView.getMinValue();
//
//				dashboardView.setRealTimeValue(ranValue, true, 500);
				setData();
			}
		});

		dashboardView.setRadius(85);
		dashboardView.setArcColor(ContextCompat.getColor(this,android.R.color.black));
		dashboardView.setTextColor(Color.parseColor("#000000"));
		// dashboardView.setBgColor(ContextCompat.getColor(this,android.R.color.white));
		dashboardView.setStartAngle(150);
		dashboardView.setPointerRadius(65);
		dashboardView.setCircleRadius(3);
		dashboardView.setSweepAngle(240);
		dashboardView.setBigSliceCount(10);
		dashboardView.setMaxValue(16500);
		dashboardView.setRealTimeValue(1200);
		dashboardView.setMeasureTextSize(6);
		dashboardView.setHeaderRadius(30);
		dashboardView.setHeaderTitle("RPM");
		dashboardView.setHeaderTextSize(16);
		dashboardView.setStripeWidth(20);
		dashboardView.setStripeMode(DashboardView.StripeMode.NORMAL);
		List<HighlightCR> highlight3 = new ArrayList<HighlightCR>();
		highlight3.add(new HighlightCR(150, 140, Color.parseColor("#4CAF50")));
		highlight3.add(new HighlightCR(290, 60, Color.parseColor("#FFEB3B")));
		highlight3.add(new HighlightCR(350, 40, Color.parseColor("#F44336")));
		dashboardView.setStripeHighlightColorAndRange(highlight3);
		
		mDatas = new ArrayList<DataInfo>();
		mAdapter = new DataListAdapter(mDatas, this);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onClickLeft() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onClickRight() {
		// TODO Auto-generated method stub
		if(SPPDispatcher.getInstance().isConnected()){
			MControlProvider.getInstance().disconnect();
			return;
		}
		Intent intent = new Intent(this, DeviceActivity.class);
		startActivityForResult(intent, 11);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
            return;
		}
	 
		switch (requestCode) {
		case 11:
			rightBtn.setBackgroundResource(R.drawable.btn_bluetooth_pressed);
			break;
		}
	}

	
	@Event({R.id.main_displacement_ll, R.id.main_bug_content, R.id.main_clear, R.id.main_lv_btn1, R.id.main_lv_btn2})
	private void onXutilsClick(View v) {
		switch (v.getId()) {
		case R.id.main_displacement_ll://排量
			PromptDialog.showEditTextDialog(this, getResources().getString(R.string.main_displacement_dialog_title), main_displacement.getText().toString(), new OnDialogClickListener() {
				
				@Override
				public void onSavd(String text) {
					// TODO Auto-generated method stub
					displacement = Double.valueOf(text);
					main_displacement.setText(text);
				}
				
				@Override
				public void onCancel() {
					// TODO Auto-generated method stub
					
				}
			});
			break;
		case R.id.main_bug_content://读取故障码
			if(SPPDispatcher.getInstance().isConnected()){
				main_bug_data.setText("");
				MControlProvider.getInstance().getActionBleModel().sendValue(SPPDispatcher.getInstance().getValue("ATDTC"));
				isATFCDTC = false;
			}
			break;
		case R.id.main_clear://清除故障码
			if(SPPDispatcher.getInstance().isConnected()){
				MControlProvider.getInstance().getActionBleModel().sendValue(SPPDispatcher.getInstance().getValue("ATFCDTC"));
				main_bug_data.setText("");
				isATFCDTC = true;
			}
			break;
		case R.id.main_lv_btn1:
			isScroll = true;
			main_lv_btn1.setVisibility(View.GONE);
			main_lv_btn2.setVisibility(View.VISIBLE);
			break;
		case R.id.main_lv_btn2:
			isScroll = false;
			main_lv_btn1.setVisibility(View.VISIBLE);
			main_lv_btn2.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}
	


}
