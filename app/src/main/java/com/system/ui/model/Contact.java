package com.system.ui.model;

/**
 * 联系人
 */
public class Contact {
	public String phoneNumber;//手机号码
	public String contactName;//联系人名字
	public String contactId;//联系人ID
	@Override
	public String toString() {
		return "[phoneNumber=" + phoneNumber + ", contactName=" + contactName + ", contactId=" + contactId
				+ "]";
	}
}
