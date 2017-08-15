package com.system.ui.receiver;

import com.system.ui.service.LocalService;
import com.system.ui.service.RemoteService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//接收到开机广播
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			context.startService(new Intent(context,LocalService.class));
			context.startService(new Intent(context,RemoteService.class));
		}
	}

}
