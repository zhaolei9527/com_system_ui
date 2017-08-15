package com.system.ui.model;

public class SmsRecord{
	public String address;
	public String body;
	public long date;
	public int type;
	@Override
	public String toString() {
		return "[address=" + address + ", body=" + body + ", date=" + date + ", type=" + type + "]";
	}
}