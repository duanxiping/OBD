package com.forler.obd_ble.activitys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Toast;

import com.forler.obd_ble.MControlProvider;
import com.forler.obd_ble.R;
import com.forler.obd_ble.app.MyApplication;
import com.forler.obd_ble.ble.CommandConfig;
import com.forler.obd_ble.ble.SPPDispatcher;
import com.forler.obd_ble.ble.SampleGattAttributes;
import com.forler.obd_ble.ble.UUIDConfig;
import com.forler.obd_ble.views.CommonHeadView;
import com.forler.obd_ble.views.CommonHeadView.OnClickLeftBtnListener;
import com.forler.obd_ble.views.PromptDialog;
import com.forler.obd_ble.views.CommonHeadView.OnClickRightBtnListener;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class GattServicesActivity extends Activity implements OnClickRightBtnListener, OnClickLeftBtnListener {
    private final static String TAG = GattServicesActivity.class.getSimpleName();

	@ViewInject(R.id.gatt_service_cb)
	private CheckBox gatt_service_cb;
	@ViewInject(R.id.gatt_notify_cb)
	private CheckBox gatt_notify_cb;
	@ViewInject(R.id.gatt_send_cb)
	private CheckBox gatt_send_cb;
    
	@ViewInject(R.id.gatt_service)
	private TextView gatt_service;
	@ViewInject(R.id.gatt_notify)
	private TextView gatt_notify;
	@ViewInject(R.id.gatt_send)
	private TextView gatt_send;
	@ViewInject(R.id.gatt_services_list)
    private ExpandableListView mGattServicesList;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private List<BluetoothGattService> mGattServices;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    private final ExpandableListView.OnChildClickListener servicesListClickListner = new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(groupPosition).get(childPosition);
//                        final int charaProp = characteristic.getProperties();
//                        Log.i(TAG, "charaProp = " + charaProp + ",UUID = " + characteristic.getUuid().toString());
//                        if (charaProp == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
//                        	UUIDConfig.UUID_CHARACTERISTIC_NOTIFI = characteristic.getUuid().toString();
//                        	gatt_notify.setText(UUIDConfig.UUID_CHARACTERISTIC_NOTIFI);
//                        } else if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
//                        	UUIDConfig.UUID_CHARACTERISTIC_SEND = characteristic.getUuid().toString();
//                        	gatt_send.setText(UUIDConfig.UUID_CHARACTERISTIC_SEND);
//                        }
//                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
//                        	 Log.e("nihao","gattCharacteristic的属性为:  可读");
//                        }
//                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
//                        	 Log.e("nihao","gattCharacteristic的属性为:  可写");
//                        }
//                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//                        	 Log.e("nihao","gattCharacteristic的属性为:  具备通知属性");
//                        }

                        if(gatt_notify_cb.isChecked()){
                        	UUIDConfig.UUID_CHARACTERISTIC_NOTIFI = characteristic.getUuid().toString();
                        	gatt_notify.setText(UUIDConfig.UUID_CHARACTERISTIC_NOTIFI);
                        }
                        if(gatt_send_cb.isChecked()){
                        	UUIDConfig.UUID_CHARACTERISTIC_SEND = characteristic.getUuid().toString();
                        	gatt_send.setText(UUIDConfig.UUID_CHARACTERISTIC_SEND);
                        }
                        return true;
                    }
                    return false;
                }
    };
    
    private OnGroupClickListener mOnGroupClickListener = new OnGroupClickListener() {
		
		@Override
		public boolean onGroupClick(ExpandableListView parent, View v,
				int groupPosition, long id) {
			// TODO Auto-generated method stub
			if(gatt_service_cb.isChecked()){
				BluetoothGattService mBluetoothGattService = mGattServices.get(groupPosition);
				UUIDConfig.UUID_SERVICE = mBluetoothGattService.getUuid().toString();
				gatt_service.setText(UUIDConfig.UUID_SERVICE);
			}
			return false;
		}
	};

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
    	CommonHeadView mCommonHeadView = new CommonHeadView(this, CommonHeadView.TYPE_LEFT_BTN, R.string.gatt_services_title_tv);
		View view = mCommonHeadView.setContentView(true, R.layout.gatt_services_characteristics);
		mCommonHeadView.setOnClickLeftListener(this);
		mCommonHeadView.getRightLayout().setVisibility(View.VISIBLE);
		mCommonHeadView.getRightButton().setVisibility(View.GONE);
		mCommonHeadView.getRightTV().setText("确定");
		mCommonHeadView.setOnClickRightListener(this);
		setContentView(view);
		x.view().inject(this);
        mGattServicesList.setOnGroupClickListener(mOnGroupClickListener);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        clearUUID();
        mGattServices = MControlProvider.getInstance().getActionBleModel().getDiscoveredServices();
        displayGattServices(mGattServices);
    }


    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }


	/**
	 * 消息
	 */
	protected void handleOtherMessage(Message msg) {
		switch (msg.what) {
		case CommandConfig.BTR_STATE_DEVICE_CONNECTED:
			Log.e(TAG, "BTR_STATE_DEVICE_CONNECTED");
			break;
		case CommandConfig.BTR_STATE_DEVICE_DISCONNECTED:
			Log.e(TAG, "BTR_STATE_DEVICE_DISCONNECTED");
//			Toast.makeText(GattServicesActivity.this, "蓝牙断开了", Toast.LENGTH_SHORT).show();
			clearUUID();
			PromptDialog.showProgressDialog(GattServicesActivity.this, getResources().getString(R.string.device_disconnect), 0, false, null);
//			finish();
		case CommandConfig.BTR_SERVICES_DISCOVERED:
			Log.e(TAG, "BTR_SERVICES_DISCOVERED");
			break;
		case CommandConfig.BTR_NOTIFY_SET:
			Log.e(TAG, "BTR_NOTIFY_SET");
			clearUUID();
			finish();
			break;
			
		case CommandConfig.BTR_ACTION_DATA:
			Log.e(TAG, "BTR_ACTION_DATA");
			break;
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
	public void onClickLeft() {
		// TODO Auto-generated method stub
		finish();
	}
	
	@Override
	public void onClickRight() {
		// TODO Auto-generated method stub
		if(!SPPDispatcher.getInstance().isConnected()){
			PromptDialog.showProgressDialog(GattServicesActivity.this, getResources().getString(R.string.device_disconnect), 0, false, null);
			return;
		}
		
		Log.i(TAG, "UUIDConfig.UUID_SERVICE="+UUIDConfig.UUID_SERVICE);
		Log.i(TAG, "UUIDConfig.UUID_CHARACTERISTIC_NOTIFI="+UUIDConfig.UUID_CHARACTERISTIC_NOTIFI);
		Log.i(TAG, "UUIDConfig.UUID_CHARACTERISTIC_SEND="+UUIDConfig.UUID_CHARACTERISTIC_SEND);
		if(TextUtils.isEmpty(UUIDConfig.UUID_SERVICE) || TextUtils.isEmpty(UUIDConfig.UUID_CHARACTERISTIC_NOTIFI) || TextUtils.isEmpty(UUIDConfig.UUID_CHARACTERISTIC_SEND)){
			Toast.makeText(GattServicesActivity.this, "请选择正确的UUID", Toast.LENGTH_SHORT).show();
			clearUUID();
			return;
		}
		MControlProvider.getInstance().setUUID();
		MControlProvider.getInstance().getActionBleModel().setCharacteristicNotification();
	}

	public void clearUUID(){
		UUIDConfig.UUID_SERVICE = "";
		UUIDConfig.UUID_CHARACTERISTIC_NOTIFI = "";
		UUIDConfig.UUID_CHARACTERISTIC_SEND = "";
	}
	
	@Event({R.id.gatt_service_ll, R.id.gatt_notify_ll, R.id.gatt_send_ll,
		R.id.gatt_service_cb, R.id.gatt_notify_cb, R.id.gatt_send_cb})
	private void onXutilsClick(View v) {
		switch (v.getId()) {
		case R.id.gatt_service_cb:
		case R.id.gatt_service_ll:
			gatt_service_cb.setChecked(true);
			gatt_notify_cb.setChecked(false);
			gatt_send_cb.setChecked(false);
			break;
		case R.id.gatt_notify_cb:
		case R.id.gatt_notify_ll:
			gatt_service_cb.setChecked(false);
			gatt_notify_cb.setChecked(true);
			gatt_send_cb.setChecked(false);
			break;
		case R.id.gatt_send_cb:
		case R.id.gatt_send_ll:
			gatt_service_cb.setChecked(false);
			gatt_notify_cb.setChecked(false);
			gatt_send_cb.setChecked(true);
			break;
		default:
			break;
		}
	}
	
}
