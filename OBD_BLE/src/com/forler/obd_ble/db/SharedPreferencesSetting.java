package com.forler.obd_ble.db;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesSetting {
	private static Context mContext;
	private final String NAME = "QBD_SPSetting";

	public final static String deviceAddress = "deviceAddress";//设备MAC

	private static SharedPreferencesSetting instance;	
	public static SharedPreferencesSetting getInstance(Context context) {
		mContext = context;
		if (instance == null) {
			synchronized (SharedPreferencesSetting.class) {
				if (instance == null) {
					instance = new SharedPreferencesSetting();
				}
			}
		}
		return instance;
	}
	    
    private SharedPreferences.Editor edit(){
        return mContext.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS).edit();
    }

    private SharedPreferences getPref(){
        return mContext.getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
    }

    public String getStr(String name, String defVal) {
        return getPref().getString(name, defVal);
    }

    public String getStr(String name) {
        return getPref().getString(name, "");
    }

    public void setStr(String name, String val) {
        edit().remove(name).putString(name, val).commit();
    }

    public int getInt(String name, int defVal) {
        return getPref().getInt(name, defVal);
    }

    public void setInt(String name, int val) {
        edit().remove(name).putInt(name, val).commit();
    }

    public float getFloat(String name, float defVal) {
        return getPref().getFloat(name, defVal);
    }

    public void setFloat(String name, float val) {
        edit().remove(name).putFloat(name, val).commit();
    }

    public boolean getBoolean(String name, boolean defVal) {
        return getPref().getBoolean(name, defVal);
    }

    public void setBoolean(String name, boolean val) {
        edit().remove(name).putBoolean(name, val).commit();
    }

    public long getLong(String name, long defVal) {
        return getPref().getLong(name, defVal);
    }

    public void setLong(String name, long val) {
        edit().remove(name).putLong(name, val).commit();
    }

    public void remove(String name){
        edit().remove(name).commit();
    }

}
