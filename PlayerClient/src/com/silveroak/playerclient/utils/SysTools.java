package com.silveroak.playerclient.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Xml;
import com.silveroak.playerclient.domain.MusicObj;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zliu on 14/12/10.
 */
public class SysTools {
    public static String getLocalIP(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
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
    public static Integer getServerPort(){
        return 8080;
    }


}



