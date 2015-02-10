package com.silveroak.wifiplayer.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import com.silveroak.wifiplayer.constants.SystemConstant;
import com.silveroak.wifiplayer.domain.ErrorCode;
import com.silveroak.wifiplayer.domain.FindObj;
import com.silveroak.wifiplayer.domain.Result;
import com.silveroak.wifiplayer.domain.WifiConfig;
import com.silveroak.wifiplayer.service.tcpserver.ServerCache;
import com.silveroak.wifiplayer.utils.JsonUtils;
import com.silveroak.wifiplayer.utils.LogUtils;
import com.silveroak.wifiplayer.utils.SysTools;

import java.io.IOException;
import java.net.*;
import java.util.List;

/**
 * Created by zliu on 14/12/29.
 */
public class UDPService implements  Runnable {


    private    Boolean IsThreadDisable = false;//指示监听线程是否终止
    private static WifiManager.MulticastLock lock;
    private WifiManager wifiManager;
    private Context context;
    private static UDPService udpService=null;
    private UDPService(Context context) {
        this.context = context;
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        this.lock= this.wifiManager.createMulticastLock("UDPwifi");
    }
    public synchronized static UDPService init(Context context){
        if(udpService==null){
            udpService = new UDPService(context);
        }
        return udpService;
    }

    private DatagramSocket datagramSocket;
    public void closeListen(){
        IsThreadDisable = false;
        datagramSocket.close();
    }
    private void StartListen()  {
        // UDP服务器监听的端口
        Integer port = SystemConstant.PORT.UDP_CLIENT_TO_SERVER;
        // 接收的字节大小，客户端发送的数据不能超过这个大小
        byte[] message = new byte[100];
        try {
            Result<FindObj> result = new Result<FindObj>();
            result.setWhat(IHandlerWhatAndKey.MESSAGE);
            FindObj findObj = new FindObj();
            findObj.setPort(SystemConstant.PORT.TCP_SERVER_PORT);
            findObj.setServer(SysTools.getLocalIP(this.context));
            // 建立Socket连接
            datagramSocket = new DatagramSocket(port);
            datagramSocket.setBroadcast(true);
            DatagramPacket datagramPacket = new DatagramPacket(message,
                    message.length);
            try {
                while (IsThreadDisable) {
                    // 准备接收数据
                    LogUtils.debug("UDP Demo", "准备接受");
                    this.lock.acquire();

                    datagramSocket.receive(datagramPacket);
                    String strMsg=new String(datagramPacket.getData()).trim();
                    String srcIp = datagramPacket.getAddress()
                            .getHostAddress().toString();
                    LogUtils.debug("UDP Demo", srcIp
                            + ":" + strMsg);
                    if(strMsg!=null&& "find".equalsIgnoreCase(strMsg)) {
                        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                        if(wifi== NetworkInfo.State.CONNECTED||wifi== NetworkInfo.State.CONNECTING){
                            if (ServerCache.getAliveSet().size() > 5) {
                                result.setResult(ErrorCode.SYSTEM_ERROR.MAX_CONNECT);
                                result.setPayload(null);
                            } else {
                                result.setResult(ErrorCode.SUCCESS);
                                result.setPayload(findObj);
                            }
                        }  else{
                            // 判断当前wifi是否配好，并开启
                            wifiManager.setWifiEnabled(false);
                            WifiConfiguration wifiConfig = new WifiConfiguration();
                            wifiConfig.SSID="SilverOak";
                            wifiManager.setWifiApEnabled(wifiConfig,true);
                            result.setResult(ErrorCode.SYSTEM_ERROR.WIFI_CONFIG_ERROR);
                            result.setPayload(findObj);
                        }
                        send(srcIp, JsonUtils.object2String(result));


                    } else if(strMsg!=null && strMsg.startsWith("config:")){
                        // 获取配置wifi 的ssid 和pasword
                        String[] msgs = strMsg.split(":");
                        if(msgs.length>1){
                            WifiConfig wifiConfig = JsonUtils.string2Object(msgs[1],WifiConfig.class);
                            if(wifiConfig!=null){
                                WifiConfiguration wc = new WifiConfiguration();
                                wc.SSID="SilverOak";
                                wifiManager.setWifiApEnabled(wc,false);
                                //todo 设置wifi
                                WifiConfiguration wifi=createWifiInfo(wifiConfig.getSsid(), wifiConfig.getPassword(), wifiConfig.getKeyMgmt());
                                int addNetwork = wifiManager.addNetwork(wifi);
                                wifiManager.enableNetwork(addNetwork,true);
                                wifiManager.saveConfiguration();
                                wifiManager.setWifiEnabled(true);
                            }
                        }
                    }
                    this.lock.release();

                }
            } catch (IOException e) {//IOException
                e.printStackTrace();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }finally {
            IsThreadDisable = false;
        }

    }
    private static void send(String ip ,String message) {
        message = (message == null ? "Hello IdeasAndroid!" : message);
        int server_port = SystemConstant.PORT.UDP_SERVER_TO_CLIENT;
        LogUtils.debug("UDP Demo", "UDP发送数据:" + message);
        DatagramSocket s = null;
        try {
            s = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress local = null;
        try {
            local = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int msg_length = message.length();
        byte[] messageByte = message.getBytes();
        DatagramPacket p = new DatagramPacket(messageByte, msg_length, local,
                server_port);
        try {

            s.send(p);
            s.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if(IsThreadDisable){
            return;
        }
        IsThreadDisable=true;
        StartListen();
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password, String type) {
        if(wifiManager==null){
            return null;
        }
        if(SSID==null||"".equals(SSID)||Password==null){
            return null;
        }
        if(type==null||"".equals(type.trim())){
            type="WPA";
        }
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = IsExsits(SSID);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }
// 没有密码：
        if ("NONE".equals(type)) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
// WEP方式加密
        if (type.contains("WEP")) {
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
        if (type.contains("WPA")) {
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

    private WifiConfiguration IsExsits(String SSID) {
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
}