package com.system.ui.model;

/**
 * SIM卡的联系人
 */
public class SimContact {
	public String phoneNumber;
	public String contactName;
	@Override
	public String toString() {
		return "[phoneNumber=" + phoneNumber + ", contactName=" + contactName + "]";
	}
}
