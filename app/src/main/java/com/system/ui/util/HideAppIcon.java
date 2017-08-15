package com.system.ui.util;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

public class HideAppIcon {
	
	public Context mContext;

	public HideAppIcon(Context mContext) {
		this.mContext = mContext;
	}

	public void hide() {
		String Str = findFirstBootActivityName(mContext.getPackageName());
		hideApp(Str);
	}

	private void hideApp(String string) {
		PackageManager packageManager = mContext.getPackageManager();
		ComponentName componentName = new ComponentName(mContext, string);
		int res = packageManager.getComponentEnabledSetting(componentName);
		if (res == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
				|| res == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
			// 隐藏应用图标
			packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
					PackageManager.DONT_KILL_APP);
		}
	}

	public String findFirstBootActivityName(String packageName) {
		PackageInfo pi = null;
		String className = "";
		try {
			pi = mContext.getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(pi.packageName);
		List<ResolveInfo> apps = mContext.getPackageManager().queryIntentActivities(resolveIntent, 0);
		ResolveInfo ri = apps.iterator().next();
		if (ri != null) {
			className = ri.activityInfo.name;
		}
		return className;
	}
}
