package com.forler.obd_ble.entity;

import java.io.Serializable;

public class DataInfo implements Serializable {
	String data;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "DataInfo [data=" + data + "]";
	}
}
