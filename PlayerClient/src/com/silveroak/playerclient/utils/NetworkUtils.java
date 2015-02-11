package com.silveroak.playerclient.utils;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import com.silveroak.playerclient.constants.WifiKeyMgmtEnum;
import com.silveroak.playerclient.domain.WifiConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by zliu on 15/2/9.
 */
public class NetworkUtils {
    public static String requestUrlGet(String url){
        String result=null;//访问返回结果
        BufferedReader read=null;//读取访问结果
        try {
            //创建url
            URL realurl=new URL(url);
            //打开连接
            URLConnection connection=realurl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //建立连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段，获取到cookies等

            // 定义 BufferedReader输入流来读取URL的响应
            read = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"UTF-8"));
            String line;//循环读取
            while ((line = read.readLine()) != null) {
                if(result==null){
                    result = line;
                }else {
                    result += line;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(read!=null){//关闭流
                try {
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static WifiConfig getWifiConfig(WifiManager wifiManager){
        if(wifiManager==null || !wifiManager.isWifiEnabled()){
            return null;
        }
        WifiConfig wifiConfig =new WifiConfig();
        wifiConfig.setSsid(wifiManager.getConnectionInfo().getSSID());
        ScanResult scanResult = getScanResult(wifiManager,wifiConfig.getSsid());
        if(scanResult!=null && !TextUtils.isEmpty(scanResult.capabilities)){
            if (scanResult.capabilities.contains("WPA") || scanResult.capabilities.contains("wpa")) {
                wifiConfig.setKeyMgmt(WifiKeyMgmtEnum.WPA);
            } else if (scanResult.capabilities.contains("WEP") || scanResult.capabilities.contains("wep")) {
                wifiConfig.setKeyMgmt(WifiKeyMgmtEnum.WEP);
            } else {
                wifiConfig.setKeyMgmt(WifiKeyMgmtEnum.NONE);
            }
        }
        return wifiConfig;
    }

    public static WifiConfiguration createWifiInfo(WifiManager wifiManager,String SSID, String Password, WifiKeyMgmtEnum type) {
        if(wifiManager==null){
            return null;
        }
        if(SSID==null||"".equals(SSID)){
            return null;
        }
        if(Password==null){
            Password = "";
        }
        if(type==null){
            type= WifiKeyMgmtEnum.WPA;
        }

        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = IsExsits(wifiManager,SSID);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }
// 没有密码：
        if (type.equals(WifiKeyMgmtEnum.NONE)) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
// WEP方式加密
        if (type.equals(WifiKeyMgmtEnum.WEP)) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
// WPA方式加密
        if (type.equals(WifiKeyMgmtEnum.WPA)) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    public static WifiConfiguration IsExsits(WifiManager wifiManager,String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if(existingConfigs==null){
            return null;
        }
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    public static ScanResult getScanResult(WifiManager wifiManager,String SSID) {
        List<ScanResult> existingConfigs = wifiManager.getScanResults();
        if(existingConfigs==null){
            return null;
        }
        for (ScanResult scanResult : existingConfigs) {
            if (scanResult.SSID.equals("\"" + SSID + "\"")) {
                return scanResult;
            }
        }
        return null;
    }
}
