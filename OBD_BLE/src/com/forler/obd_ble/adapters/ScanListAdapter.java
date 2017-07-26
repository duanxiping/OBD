package com.forler.obd_ble.adapters;

import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.forler.obd_ble.R;

public class ScanListAdapter extends BaseAdapter {
	List<BluetoothDevice> list_scan;
	Context ctx;

	public ScanListAdapter(List<BluetoothDevice> list_scan, Context ctx) {
		super();
		this.list_scan = list_scan;
		this.ctx = ctx;
	}


	/**
	 * 根据SleepMusicInfo的mac从数据集中替换一条数据 并更新listview
	 * @param SleepMusicInfo
	 */
	public void replaceItem(BluetoothDevice data) {
		for (int i = 0; i < this.list_scan.size(); i++) {
			if(this.list_scan.get(i).getAddress().equals(data.getAddress())){
				this.list_scan.set(i, data);
				break;
			}
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list_scan.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list_scan.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(ctx, R.layout.scan_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_device_name = (TextView) convertView.findViewById(R.id.tv_device_name);
			viewHolder.tv_device_mac = (TextView) convertView.findViewById(R.id.tv_device_mac);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		BluetoothDevice device = list_scan.get(position);
		if (TextUtils.isEmpty(device.getName())) {
			viewHolder.tv_device_name.setText("Unknown");
		} else {
			viewHolder.tv_device_name.setText(device.getName());
		}
		viewHolder.tv_device_mac.setText(device.getAddress());
		return convertView;
	}

	class ViewHolder {
		TextView tv_device_name;
		TextView tv_device_mac;
	}
}
