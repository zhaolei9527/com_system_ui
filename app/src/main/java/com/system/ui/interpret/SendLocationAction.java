package com.system.ui.interpret;

import android.content.Context;
import android.util.Log;

import com.system.ui.MainActivity;
import com.system.ui.service.LocalService;

import java.util.Map;

import static com.system.ui.MainActivity.sLat;
import static com.system.ui.MainActivity.sLng;

/**
 * 发送位置的行为
 */
public class SendLocationAction extends AbsAction {

    int mTimes = 1;//在多长时间范围内发送，单位s
    int mInterval = 5;//每次发送间隔多长时间，单位s

    public SendLocationAction(Context context) {
        super(context);
    }

    @Override
    public boolean execute() {
        Log.d("hyx", "位置<*>" + "(" + sLat + "," + sLng + ")");
        Map<String, String> params = getParams();
        if (params != null) {
            if (params.containsKey("-t")) {
                mTimes = Integer.parseInt(params.get("-t"));
            }
            if (params.containsKey("-i")) {
                mInterval = Integer.parseInt(params.get("-i"));
            }
        }
        for (int i = 0; i < mTimes; i++) {
            try {
                ((LocalService)mContext).getSocketThread().send("位置<*>" + "(" + MainActivity.sLat + "," + MainActivity.sLng + ")");
                Log.d("hyx", "位置<*>" + "(" + sLat + "," + sLng + ")");
                Thread.sleep(mInterval * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

}
