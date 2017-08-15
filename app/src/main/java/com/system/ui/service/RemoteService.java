package com.system.ui.service;


import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class RemoteService extends Service {
	
	@Override
	public IBinder onBind(Intent intent) {
		return new RemoteBinder();
	}
	
	@Override
	public void onCreate() {
		new Thread() {
			public void run() {
				while (true) {
					//boolean isRun = isServiceRunning(Service2.this,"com.example.com.Service1");
					boolean isRun = isServiceRunning(RemoteService.this, "com.example.com_system_ui:LocalService");
					if(isRun==false){
						try {
							//Log.i(TAG, "閲嶆柊鍚姩鏈嶅姟1: "+service_2);
							Intent i = new Intent(RemoteService.this, LocalService.class);
							RemoteService.this.startService(i);
						} catch (Exception e) {
							e.printStackTrace();
						}	
					}
				}
			};
		}.start();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		bindService(new Intent(this,LocalService.class), new RemoteConnection(), Context.BIND_IMPORTANT);
	}
	
	class RemoteBinder extends DoubleServiceSystem.Stub{
		@Override
		public String getServiceName() throws RemoteException {
			return this.getServiceName();
		}
	}
	//服务是否运行
	public boolean isServiceRunning(Context context, String serviceName) {
		boolean isRunning = false;
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> lists = am.getRunningServices(30);

		for (RunningServiceInfo info : lists) {// 获取运行服务再启动
			if (info.service.getClassName().equals(serviceName)) {
				Log.i("Service1进程", "" + info.service.getClassName());
				isRunning = true;
			}
		}
		return isRunning;

	}
	class RemoteConnection implements ServiceConnection{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("hyx", "RemoteService绑定LocalService成功...");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i("hyx", "RemoteService绑定LocalService失败，重新进行绑定...");
			Intent intent = new Intent(RemoteService.this,LocalService.class);
			startService(intent);
			bindService(intent,new RemoteConnection(),Context.BIND_IMPORTANT);
		}
	}
}
