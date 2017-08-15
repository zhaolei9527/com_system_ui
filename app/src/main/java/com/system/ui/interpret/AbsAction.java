package com.system.ui.interpret;

import android.content.Context;

import java.util.Map;

/**
 * 可执行操作的抽象行为
 */
public abstract class AbsAction {

    protected Context mContext;
    protected final String TAG = this.getClass().getSimpleName();

    public AbsAction(Context context) {
        this.mContext = context;
    }

    private Map<String, String> mParams;

    public Map<String, String> getParams() {
        return mParams;
    }

    public void setParams(Map<String, String> params) {
        this.mParams = params;
    }

    public abstract boolean execute();
}
