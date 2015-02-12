package com.silveroak.wifiplayer.service;

import android.content.Context;
import android.net.wifi.WifiManager;
import com.silveroak.wifiplayer.constants.ConfigStatusEnum;
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

            // 建立Socket连接
            datagramSocket = new DatagramSocket(port);
            datagramSocket.setBroadcast(true);
            DatagramPacket datagramPacket = new DatagramPacket(message,
                    message.length);
            try {
                while (IsThreadDisable) {
                    findObj.setServer(SysTools.getLocalIP(this.wifiManager));
                    // 准备接收数据
                    LogUtils.debug("UDP Demo", "准备接受");
                    this.lock.acquire();

                    datagramSocket.receive(datagramPacket);
                    String strMsg=new String(datagramPacket.getData()).trim();
                    String srcIp = datagramPacket.getAddress()
                            .getHostAddress().toString();
                    LogUtils.debug("UDP Demo", srcIp
                            + ":" + strMsg);
                    if(strMsg!=null&& strMsg.startsWith("find")) {
                        if(ConfigService.getConfigService(context).STATUS.equals(ConfigStatusEnum.NONE) || ConfigService.getConfigService(context).STATUS.equals(ConfigStatusEnum.DO_CONFIG)){
                            if (ServerCache.getAliveSet().size() > 5) {
                                result.setResult(ErrorCode.SYSTEM_ERROR.MAX_CONNECT);
                                result.setPayload(null);
                            } else {
                                result.setResult(ErrorCode.SUCCESS);
                                result.setPayload(findObj);
                            }
                        }  else{
                            // 判断当前wifi是否配好，并开启
                            result.setResult(ErrorCode.SYSTEM_ERROR.WIFI_CONFIG_ERROR);
                        }
                        send(srcIp, JsonUtils.object2String(result));


                    } else if(strMsg!=null && strMsg.startsWith("config===")){
                        // 获取配置wifi 的ssid 和pasword
                        String[] msgs = strMsg.split("===");
                        if(msgs.length>1){
                            WifiConfig wifiConfig = JsonUtils.string2Object(msgs[1],WifiConfig.class);
                            ConfigService.getConfigService(context).configWifi(wifiConfig);
                            result.setResult(ErrorCode.SYSTEM_ERROR.WIFI_DO_CONFIG);
                            send(srcIp, JsonUtils.object2String(result));
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
}