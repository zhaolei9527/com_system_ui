package com.system.ui.interpret;

import android.content.Context;

import com.system.ui.MainActivity;
import com.system.ui.service.LocalService;

import java.io.File;
import java.io.FileInputStream;

public class SendAction extends AbsAction {

	public SendAction(Context context) {
		super(context);
	}

	@Override
	public boolean execute() {
		File file = new File(MainActivity.APP_FOLDER);
		if (file.exists()&&file.list()!=null) {
			try {
				((LocalService)mContext).getSocketThread().send(new FileInputStream(file.listFiles()[0]));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

}
