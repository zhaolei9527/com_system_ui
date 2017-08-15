package com.system.ui.util;

import android.content.Context;
import android.os.Environment;

/**
 * SD卡相关操作类
 */
public class SDCard {

	/**
	 * SD卡根目录
	 */
	public static final String SD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	/**
	 * 检测SD卡是否准备就绪
	 */
	public static boolean isPrepared(Context context){
		//SD卡准备就绪
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}
	
}
