package com.silveroak.wifiplayer.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import com.silveroak.wifiplayer.constants.ConfigStatusEnum;
import com.silveroak.wifiplayer.constants.WifiKeyMgmtEnum;
import com.silveroak.wifiplayer.domain.WifiConfig;
import com.silveroak.wifiplayer.utils.LogUtils;
import com.silveroak.wifiplayer.utils.NetworkUtils;

/**
 * Created by zliu on 15/2/11.
 * 监控状态
 */
public class ConfigService {
    private final static String TAG = ConfigService.class.getSimpleName();
    public static ConfigStatusEnum STATUS=ConfigStatusEnum.NONE;

    private static ConfigService configService=null;
    private Context context;
    private WifiManager wifiManager;
    private boolean isCheckWifi=false;
    private ConfigService(){}
    public static ConfigService getConfigService(Context context){
        if(configService==null){
            configService = new ConfigService();
            configService.context = context;
            configService.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        }
        return configService;
    }

    public synchronized void monitor(){
          if(isCheckWifi){
              return;
          }
        isCheckWifi=true;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                long sleepTime=5* 1000l;
                while(true){
                    if(checkWifi().equals(ConfigStatusEnum.NONE)){
                        sleepTime = 10*60*1000l;
                    } else {
                        sleepTime = 5*1000l;
                    }
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(runnable).start();

    }

    /**
     * 当状态 为NONE时做的检测
     * return
     *
     *  NONE 正常结束
     *  AP_STATUS 启动热点
     *  DO_CONFIG
     */
    private int checkDoConfigCount=0;
    public ConfigStatusEnum checkWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        if (STATUS.equals(ConfigStatusEnum.NONE)) {
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                STATUS = ConfigStatusEnum.NONE;
                return ConfigStatusEnum.NONE;
            } else {
                STATUS = ConfigStatusEnum.AP_STATUS;
                //todo 开启热点
                wifiManager.setWifiEnabled(false);
                WifiConfiguration wifiConfig = NetworkUtils.createWifiInfo(this.wifiManager, "SilverOak-AP", "", WifiKeyMgmtEnum.NONE);
                wifiManager.setWifiApEnabled(wifiConfig, true);
                return ConfigStatusEnum.AP_STATUS;
            }
        } else {
            if(STATUS.equals(ConfigStatusEnum.DO_CONFIG)) {
                if(checkDoConfigCount>3){
                    checkDoConfigCount = 0;
                    STATUS = ConfigStatusEnum.NONE;
                    checkWifi();
                    return STATUS;
                }
                checkDoConfigCount = checkDoConfigCount+1;
                ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                    STATUS = ConfigStatusEnum.NONE;
                    return ConfigStatusEnum.NONE;
                }
            }
        }
        return STATUS;
    }

    public ConfigStatusEnum configWifi(final WifiConfig wifiConfig){
        if(wifiConfig!=null){
            STATUS = ConfigStatusEnum.DO_CONFIG;
            final WifiConfiguration wc= NetworkUtils.createWifiInfo(this.wifiManager, "SilverOak-AP", "", WifiKeyMgmtEnum.NONE);
            wifiManager.setWifiApEnabled(wc,false);
            // 设置wifi
            wifiManager.setWifiEnabled(true);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    while(!wifiManager.isWifiEnabled()) {
                        LogUtils.debug(TAG,"Wifi status is close");
                        try {
                            Thread.sleep(3000l);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    WifiConfiguration wifi= NetworkUtils.createWifiInfo(wifiManager, wifiConfig.getSsid(), wifiConfig.getPassword(), wifiConfig.getKeyMgmt());
                    int addNetwork = wifiManager.addNetwork(wifi);
                    wifiManager.enableNetwork(addNetwork,true);
                    wifiManager.saveConfiguration();
                }
            };
            new Thread(runnable).start();

        }
        return STATUS;
    }

}
