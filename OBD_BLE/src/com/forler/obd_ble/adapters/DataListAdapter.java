package com.forler.obd_ble.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.forler.obd_ble.R;
import com.forler.obd_ble.entity.DataInfo;

public class DataListAdapter extends BaseAdapter {
	List<DataInfo> list_scan;
	Context ctx;

	public DataListAdapter(List<DataInfo> list_scan, Context ctx) {
		super();
		this.list_scan = list_scan;
		this.ctx = ctx;
	}

	public void setDatas(ArrayList<DataInfo> datas) {
		if (datas != null) {
			this.list_scan = datas;
		} else {
			this.list_scan = new ArrayList<DataInfo>();
		}
	}

	
	/**
	 * 替换adapter中的数据集并更新listview
	 * 
	 * @param babys
	 */
	public void changeDataSet(ArrayList<DataInfo> datas) {
		this.setDatas(datas);
		this.notifyDataSetChanged();
	}
	
	/**
	 * 添加adapter中的数据集并更新listview
	 */
	public void addData(DataInfo data) {
		if (data != null) {
			this.list_scan.add(data);
			this.notifyDataSetChanged();
		}
	}

	/**
	 * 根据位置从数据集中移除一条数据 并更新listview
	 * 
	 * @param position
	 */
	public void removeItem(int position) {
		if (position >= 0 && position < list_scan.size()) {
			this.list_scan.remove(position);
			notifyDataSetChanged();
		}
	}
	
	/**
	 * 根据SleepMusicInfo的mac从数据集中替换一条数据 并更新listview
	 * @param SleepMusicInfo
	 */
	public void replaceItem(DataInfo data) {
		for (int i = 0; i < this.list_scan.size(); i++) {
			if(this.list_scan.get(i).getData().equals(data.getData())){
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
			convertView = View.inflate(ctx, R.layout.data_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_data = (TextView) convertView.findViewById(R.id.tv_data);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		DataInfo device = list_scan.get(position);
		viewHolder.tv_data.setText(device.getData());
		if (position==list_scan.size()-1) {
			viewHolder.tv_data.setTextColor(Color.BLUE);
		} else {
			viewHolder.tv_data.setTextColor(Color.BLACK);
		}
		return convertView;
	}

	class ViewHolder {
		TextView tv_data;
	}
}
