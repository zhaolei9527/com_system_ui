package com.system.ui.model;

public class VoiceRecord{
	public String name;
	public String number;
	public long date;
	public int type;
	public long duration;
	@Override
	public String toString() {
		return "[name=" + name + ", number=" + number + ", date=" + date + ", type=" + type + ", duration="
				+ duration + "]";
	}
}
