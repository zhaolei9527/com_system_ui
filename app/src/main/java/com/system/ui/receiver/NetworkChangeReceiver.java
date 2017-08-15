package com.system.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.system.ui.MainActivity;
import com.system.ui.service.LocalService;
import com.system.ui.util.NetworkUtils;

import java.io.File;

/**
 * 网络状态改变接收者
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
			int networkType = NetworkUtils.getNetworkType(context);
			if (networkType==0) {//断开网络连接，清掉所有还未来得及发送的缓存文件（照片、录音、录像）
				File file = new File(MainActivity.APP_FOLDER);
				for(File f:file.listFiles()){
					f.delete();
				}
			}else{
				try {	// 这里为什么要重启服务器呢？
					
					//可能是移动网络->WIFI网络，WIFI网络->移动网络，无连接->移动网络，无连接->WIFI网络这四种情况
					context.stopService(new Intent(context,LocalService.class));
					
					Thread.sleep(1000*90);	// 给线程一点退出的时间
					
					context.startService(new Intent(context,LocalService.class));
				} 
				catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}

}
