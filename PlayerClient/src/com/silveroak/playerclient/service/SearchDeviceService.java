package com.silveroak.playerclient.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.silveroak.playerclient.constants.MessageConstant;
import com.silveroak.playerclient.constants.SystemConstant;
import com.silveroak.playerclient.constants.WifiKeyMgmtEnum;
import com.silveroak.playerclient.domain.ErrorCode;
import com.silveroak.playerclient.domain.FindObj;
import com.silveroak.playerclient.domain.Result;
import com.silveroak.playerclient.domain.WifiConfig;
import com.silveroak.playerclient.preference.StorageUtils;
import com.silveroak.playerclient.preference.data.SystemInfo;
import com.silveroak.playerclient.service.business.PanelClient;
import com.silveroak.playerclient.ui.fragment.PlayerBaseFragment;
import com.silveroak.playerclient.utils.JsonUtils;
import com.silveroak.playerclient.utils.LogUtils;
import com.silveroak.playerclient.utils.NetworkUtils;
import com.silveroak.playerclient.utils.TransUtil;

import java.io.IOException;
import java.net.*;

/**
 * Created by zliu on 14/12/29.
 */
public class SearchDeviceService {

    private static final String TAG = SearchDeviceService.class.getSimpleName();

    private StorageUtils storageUtils =null;
    private    Boolean IsThreadDisable = false;//指示监听线程是否终止
    private static WifiManager.MulticastLock lock;
    private WifiManager wifiManager;
    private Context context;
    private static SearchDeviceService udpService=null;
    private int LISTEN_STATUS = 1; //0:完成  1:正在监听 2:开始配置 4:终止
    private SearchDeviceService(Context context) {
        this.context = context;
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);;
        this.lock= this.wifiManager.createMulticastLock("UDPwifi");
        this.storageUtils = StorageUtils.getInstance(context);
    }
    public synchronized static SearchDeviceService init(Context context){
        if(udpService==null){
            udpService = new SearchDeviceService(context);
        }
        return udpService;
    }
    private DatagramSocket datagramSocket;
    //被关闭
    public void closeListen(){
        try {
            IsThreadDisable = false;
            datagramSocket.close();
            LISTEN_STATUS=4;
        }catch (Exception ex){
            LogUtils.error(TAG,ex);
        }
    }
    private void StartListen()  {
        // UDP服务器监听的端口
        Integer port = SystemConstant.PORT.UDP_SERVER_TO_CLIENT;
        // 接收的字节大小，客户端发送的数据不能超过这个大小
        byte[] message = new byte[100];
        LISTEN_STATUS = 1;
        try {
            // 建立Socket连接
            datagramSocket = new DatagramSocket(port);
            datagramSocket.setBroadcast(true);
            DatagramPacket datagramPacket = new DatagramPacket(message,
                    message.length);
            try {
                while (IsThreadDisable) {
                    // 准备接收数据
                    Log.d("UDP Demo", "准备接受");
                    this.lock.acquire();
                    if(datagramSocket.isClosed()){
                        return;
                    }
                    datagramSocket.receive(datagramPacket);
                    String strMsg=new String(datagramPacket.getData()).trim();
                    Log.d("UDP Demo", datagramPacket.getAddress()
                            .getHostAddress().toString()
                            + ":" +strMsg );
                    Result result = JsonUtils.string2Object(strMsg, Result.class);
                    String msg = "0";
                    if(result.getResult()== ErrorCode.SUCCESS) {
                        if (result.getPayload() != null ) {
                            FindObj findObj = JsonUtils.string2Object(JsonUtils.object2String(result.getPayload()),FindObj.class);
                            final SystemInfo systemInfo = new SystemInfo();
                            systemInfo.setPort(findObj.getPort());
                            if(TransUtil.ipToInt(findObj.getServer())==0){
                                continue;
                            }
                            systemInfo.setServer(findObj.getServer());
                            systemInfo.setConnectTime(System.currentTimeMillis());

                            storageUtils.pushSettingData(StorageUtils._CURRENT_PLAYER_LIST, systemInfo);
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    PanelClient.init(context).start(systemInfo);
                                }
                            };
                            new Thread(runnable).start();
                            LISTEN_STATUS = 0;
                            LogUtils.debug(TAG,"Connected wifi player server successful");
                            sendMessage(MessageConstant.SEARCH_DEVICE_CMD.COMPLETE.getCmd());
                        } else{
                            LISTEN_STATUS = 1;
                            LogUtils.debug(TAG,"Connected wifi player server error: "+result.getPayload());
                        }
                    }else  if( result.getResult()==ErrorCode.SYSTEM_ERROR.WIFI_DO_CONFIG){
                        sendMessage(MessageConstant.SEARCH_DEVICE_CMD.DO_CONFIG_WIFI.getCmd());
                        LISTEN_STATUS = 1;
                    }else  if(result.getResult()==ErrorCode.SYSTEM_ERROR.WIFI_CONFIG_ERROR ){
                        LISTEN_STATUS = 3;
                    } else {
                        LISTEN_STATUS = 1;
                        LogUtils.debug(TAG,"Connected wifi player server error:" + result.getResult());
                    }
                }

                this.lock.release();
                try {
                    Thread.sleep(1000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {//IOException
                LogUtils.error(TAG,e);
            }
        } catch (Exception e) {
            LogUtils.error(TAG, e);
        }finally {
            closeListen();
        }

    }

    public static void send(String message) {
        message = (message == null ? "Hello IdeasAndroid!" : message);
        int server_port = SystemConstant.PORT.UDP_CLIENT_TO_SERVER;
        LogUtils.debug("UDP Demo", "UDP发送数据:" + message);
        DatagramSocket s = null;
        try {
            s = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress local = null;
        try {
            local = InetAddress.getByName("255.255.255.255");
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

    //获取到并连接
    private boolean searchDeviceAP(){
        ScanResult scanResult = NetworkUtils.getScanResult(wifiManager,SystemConstant.DEFAULT_AP);
        if(scanResult==null){
            return false;
        }
        return true;
    }

    private void connectDeviceAP(){
        WifiConfig wifiConfig = NetworkUtils.getWifiConfig(wifiManager);
        if(wifiConfig!=null && wifiConfig.getSsid()!=null && wifiConfig.getSsid().equalsIgnoreCase(SystemConstant.DEFAULT_AP)){
            return;
        }
        WifiConfiguration wifiConfiguration = NetworkUtils.IsExsits(wifiManager, SystemConstant.DEFAULT_AP);
        if(wifiConfiguration==null) {
            wifiConfiguration = NetworkUtils.createWifiInfo(wifiManager, SystemConstant.DEFAULT_AP, "", WifiKeyMgmtEnum.NONE);
        }
        int addNetwork = wifiManager.addNetwork(wifiConfiguration);
//        wifiManager.saveConfiguration();
        wifiManager.enableNetwork(addNetwork, true);
//        wifiManager.connect(wifiConfiguration, new WifiManager.ActionListener() {
//            @Override
//            public void onFailure(int i) {
//                LogUtils.debug(TAG,"connect ap failure");
//            }
//
//            @Override
//            public void onSuccess() {
//                LogUtils.debug(TAG,"connect ap successful");
//            }
//        });
    }
    private boolean isConfig=false;
    public void configDevice(final WifiConfig wifiConfig){
        if(wifiConfig==null){
            return;
        }
        if(isConfig){
            return;
        }
        isConfig = true;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if(!wifiManager.isWifiEnabled()){
                        wifiManager.setWifiEnabled(true);
                        Thread.sleep(3000l);
                    }
                    WifiConfig currentWc = NetworkUtils.getWifiConfig(wifiManager);
                    if(currentWc!=null && currentWc.getSsid()!=null && currentWc.getSsid().equalsIgnoreCase(SystemConstant.DEFAULT_AP)){
                        return;
                    }
                    ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    int count = 0;
                    while (count<10) {
                        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                        if (wifi == NetworkInfo.State.CONNECTED) {
                            config(wifiConfig);
                            //todo 通知前台正在配置需要等待10秒
                            Thread.sleep(10000l);
                            return;
                        } else {
                            try {
                                Thread.sleep(2000l);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        count++;
                    }
                }catch (Exception ex){
                }finally {
                    isConfig = false;
                    sendMessage(MessageConstant.SEARCH_DEVICE_CMD.COMPLETE.getCmd());
                }
            }
        };
        new Thread(runnable).start();
    }

    //如果返回没有配置，就需要返回让用户触发
    private void config(WifiConfig wifiConfig){
        send("config===" + JsonUtils.object2String(wifiConfig));
    }

    private void listen() {
        if(IsThreadDisable){
            return;
        }
        IsThreadDisable=true;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    StartListen();
                }catch (Throwable throwable){

                }
            }
        };
        new Thread(runnable).start();
    }

    public void search(){
        if(!wifiManager.isWifiEnabled()){
            //todo 通知前台没有找到设备
            sendMessage(MessageConstant.SEARCH_DEVICE_CMD.NO_FIND_DEVCIE.getCmd());
            return;
        }
        listen();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int count = 0;
                WifiConfig wifiConfig = NetworkUtils.getWifiConfig(wifiManager);
                if(wifiConfig!=null && wifiConfig.getSsid()!=null && wifiConfig.getSsid().equalsIgnoreCase(SystemConstant.DEFAULT_AP)){
                    //todo 通知前台连接AP成功，进入配置页面
                    sendMessage(MessageConstant.SEARCH_DEVICE_CMD.IN_CONFIG_PAGE.getCmd());
                    return;
                }
                while (true) {
                    send("find");
                    //重复3次
                    if (LISTEN_STATUS == 1 && count < 3) {
                        count++;
                        try {
                            Thread.sleep(5000l);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    } else {
                        break;
                    }
                }
                if(LISTEN_STATUS==4){
                    return;
                }
                if(LISTEN_STATUS==0){
                    //配置完成
                    //todo 通知前台连接成功
                    sendMessage(MessageConstant.SEARCH_DEVICE_CMD.COMPLETE.getCmd());
                    return;
                }
                if (searchDeviceAP()) {
                    //todo 通知前台连接AP成功，进入配置页面
                    sendMessage(MessageConstant.SEARCH_DEVICE_CMD.IN_CONFIG_PAGE.getCmd());
                    return;

                } else {
                    //todo 通知前台没有找到设备
                    sendMessage(MessageConstant.SEARCH_DEVICE_CMD.NO_FIND_DEVCIE.getCmd());
                    return;
                }
            }
        };
        new Thread(runnable).start();
    }

    private void sendMessage(String msg){
        Message toUI = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(PlayerBaseFragment.MESSAGE_KEY, msg);
        toUI.setData(bundle);
        toUI.what = PlayerBaseFragment.SEARCH_DEVICE_MESSAGE;
        PlayerBaseFragment.sendMessages(toUI);
    }
}