package com.system.ui.socket;

import android.content.Context;
import android.util.Log;

import com.system.ui.MainActivity;
import com.system.ui.util.HeartBeat;

/**
 * 持续发送心跳包的线程
 */
public class HeartBeatThread implements Runnable {

    private static SocketThread mSocketThread;
    Context mContext;
    public final String TAG = this.getClass().getSimpleName();
    public static boolean isExit = true;

    public HeartBeatThread(SocketThread socketThread, Context context) {

        if (!HeartBeatThread.isExit) {
            Log.i("hyx", "线程HeartBeatThread正在运行，禁止启动多个...");
            return;
        }

        HeartBeatThread.mSocketThread = socketThread;
        this.mContext = context;
        Log.i("hyx", "线程HeartBeatThread启动完毕...");
    }

    @Override
    public void run() {
        boolean bIsOk = true;
        String msg = "";
        int iReconnSum = 0;        //  重连次数
        String networkType = "";

        while (true) {

            try {

                if (MainActivity.isStopThread) {
                    Log.w("hyx", "BaseApplication.isStopThread=true, 退出线程...");
                    break;
                }
                HeartBeatThread.isExit = false;
                /*
                if(iReconnSum >= 5)
				{
					BaseApplication.isStopThread = true;
					Log.w("hyx", "多次重连失败，退出线程...");
					break;
				}
				*/
                networkType = HeartBeat.getNetworkType(mContext).trim();

                if (!bIsOk || !SocketThread.isConnection || networkType.contains("未连接") || networkType.length() <= 0) {
                    Thread.sleep(1000 * 3);
                    Log.i("hyx", "开始重连服务器...");
                    Log.i("hyx", "Thread Id=" + Thread.currentThread().getId());
                    iReconnSum++;
                    bIsOk = mSocketThread.connect(HeartBeat.get(mContext));
                }
                // 连接成功 发送心跳包
                if (bIsOk) {
                    iReconnSum = 0;
                    Log.i("hyx", "开始发送心跳包...");
                    bIsOk = mSocketThread.sendHeartBeat(HeartBeat.get(mContext));
                }
                Thread.sleep(1000 * 15);
                msg = String.format("------------> HeartBeatThread runing...bIsOk=%b", bIsOk);
                Log.i("hyx", msg);
            } catch (Exception ex) {
                bIsOk = false;
                ex.printStackTrace();
            }
        }

        Log.i("hyx", "线程HeartBeatThread退出...");
        HeartBeatThread.isExit = true;
    }

}
