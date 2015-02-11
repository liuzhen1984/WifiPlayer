package com.silveroak.wifiplayer.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

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
}
