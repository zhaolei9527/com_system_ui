package com.system.ui.interpret;

import android.content.Context;

public class DefaultAction extends AbsAction {

	public DefaultAction(Context context) {
		super(context);
	}

	@Override
	public boolean execute() {
		return true;
	}

}
