package com.haofeng.preresearch.satellitemodemclient.config;

import com.haofeng.preresearch.satellitemodemclient.utils.ModemUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ModemConfig {
	public static String serverIp = "192.168.43.1";
	public static int port = 2333;
	public static int timeOut = 60*1000;
	public static String authenMsg = "authen_myself";
	/** 本机IP */
	public static String localIP;
	/** 本机Mac地址 */
	public static String localMacAddr;
	
	private Context mContext;
	private SharedPreferences mSharedPreferences;
	
	
	public ModemConfig(Context context) {
		this.mContext = context;
		this.mSharedPreferences = mContext.getSharedPreferences(ModemConst.PREFERENCE_NAME, mContext.MODE_PRIVATE);
		
		serverIp = mSharedPreferences.getString(ModemConst.PREFERENCE_NAME, serverIp);
		port = mSharedPreferences.getInt(ModemConst.PREFERENCE_PORT, port);
		timeOut = mSharedPreferences.getInt(ModemConst.PREFERENCE_TIMEOUT, timeOut);
		authenMsg = mSharedPreferences.getString(ModemConst.PREFERENCE_AUTHEN, authenMsg);
		
		localIP = ModemUtils.getLocalWifiIP(mContext);
		localMacAddr = ModemUtils.getMacAddress();
		
	}
	
	public void recordConfig(){
		if(mSharedPreferences == null){
			mSharedPreferences = mContext.getSharedPreferences(ModemConst.PREFERENCE_NAME, mContext.MODE_PRIVATE);
		}
		
		Editor editor = mSharedPreferences.edit();
		editor.putString(ModemConst.PREFERENCE_NAME, serverIp);
		editor.putInt(ModemConst.PREFERENCE_PORT, port);
		editor.putInt(ModemConst.PREFERENCE_TIMEOUT, timeOut);
		editor.putString(ModemConst.PREFERENCE_AUTHEN, authenMsg);
		
		editor.commit();
	}
	
}
