package com.silveroak.playerclient.service;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import com.silveroak.playerclient.ClientActivity;
import com.silveroak.playerclient.constants.SystemConstant;
import com.silveroak.playerclient.domain.ErrorCode;
import com.silveroak.playerclient.domain.FindObj;
import com.silveroak.playerclient.domain.Result;
import com.silveroak.playerclient.preference.StorageUtils;
import com.silveroak.playerclient.preference.data.SystemInfo;
import com.silveroak.playerclient.service.business.PanelClient;
import com.silveroak.playerclient.utils.JsonUtils;
import com.silveroak.playerclient.utils.LogUtils;

import java.io.IOException;
import java.net.*;

/**
 * Created by zliu on 14/12/29.
 */
public class UDPService implements  Runnable {

    private static final String TAG = UDPService.class.getSimpleName();

    private StorageUtils storageUtils =null;
    private    Boolean IsThreadDisable = false;//指示监听线程是否终止
    private static WifiManager.MulticastLock lock;
    private WifiManager manager;
    private Context context;
    private static UDPService udpService=null;
    private UDPService(Context context) {
        this.context = context;
        this.manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);;
        this.lock= this.manager.createMulticastLock("UDPwifi");
        this.storageUtils = StorageUtils.getInstance(context);
    }
    public synchronized static UDPService init(Context context){
        if(udpService==null){
            udpService = new UDPService(context);
        }
        return udpService;
    }
    private DatagramSocket datagramSocket;
    public void closeListen(){
        try {
            IsThreadDisable = false;
            datagramSocket.close();
        }catch (Exception ex){
            LogUtils.error(TAG,ex);
        }
    }
    private void StartListen()  {
        // UDP服务器监听的端口
        Integer port = SystemConstant.PORT.UDP_SERVER_TO_CLIENT;
        // 接收的字节大小，客户端发送的数据不能超过这个大小
        byte[] message = new byte[100];
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
                            SystemInfo systemInfo = new SystemInfo();
                            systemInfo.setPort(findObj.getPort());
                            systemInfo.setServer(findObj.getServer());
                            systemInfo.setConnectTime(System.currentTimeMillis());

                            storageUtils.pushSettingData(StorageUtils._CURRENT_PLAYER_LIST, systemInfo);
                            PanelClient.init(context).start(systemInfo);
                            IsThreadDisable = false;
                            msg = "Connected wifi player server successful";

                        } else{
                            msg = "Connected wifi player server error: "+result.getPayload();
                        }
                    }else{
                        if(result.getResult()==ErrorCode.SYSTEM_ERROR.WIFI_CONFIG_ERROR){
                            msg="Wifi no configuration";
                        } else {
                            msg = "Connected wifi player server error:" + result.getResult();
                        }
                    }
                    Message toUI = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString(ClientActivity.MESSAGE_KEY, msg);
                    toUI.setData(bundle);
                    toUI.what = result.getWhat();
                    ClientActivity.getHandler().sendMessage(toUI);
                    this.lock.release();
                    try {
                        Thread.sleep(3000l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {//IOException
                closeListen();
                e.printStackTrace();
                LogUtils.error(TAG,e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error(TAG,e);
        }finally {
            IsThreadDisable = false;
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

    @Override
    public void run() {
        if(IsThreadDisable){
            return;
        }
        IsThreadDisable=true;
        StartListen();
    }
}