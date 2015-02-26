package com.silveroak.wifiplayer.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

/**
 * Created by zliu on 14/12/10.
 */
public class SysTools {
    public static String getLocalIP(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return getLocalIP(wifiManager);
    }
    public static String getLocalIP(WifiManager wifiManager){
        String ip = null;
        if(wifiManager!=null && wifiManager.isWifiEnabled()){
            if(wifiManager.getDhcpInfo()!=null){
                DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                ip = TransUtil.intToIp(dhcpInfo.ipAddress);
            }
        }
        if(ip==null){
            ip = "Network is unconnected!";
        }
        return ip;
    }

    public static String getVersion(PackageManager pm ){
        try {
            PackageInfo packageInfo = pm.getPackageInfo("com.silveroak.wifiplayer",0);
            if(TextUtils.isEmpty(packageInfo.versionName)){
                return "-.-";
            }
            return packageInfo.versionName+"."+packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "-.-";
    }
}
