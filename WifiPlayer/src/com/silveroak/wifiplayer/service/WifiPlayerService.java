package com.silveroak.wifiplayer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import com.silveroak.wifiplayer.service.tcpserver.TcpServer;
import com.silveroak.wifiplayer.utils.LogUtils;

/**
 * Created by zliu on 14/12/8.
 */
public class WifiPlayerService extends Service {
    private final static String TAG = WifiPlayerService.class.getSimpleName();
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);

        try {
            HttpServer.start();
        } catch (Exception e) {
            LogUtils.error(TAG,e);
        }

        try{
            TcpServer.init();
        }catch (Exception e){
            LogUtils.error(TAG,e);
            throw new RuntimeException(e);
        }
        try{
            new Thread(UDPService.init(getApplicationContext())).start();
        }catch (Exception e){
            LogUtils.error(TAG,e);
            throw new RuntimeException(e);
        }
    }




    //网络开关状态改变的监听
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                LogUtils.debug(TAG, "网络状态切换...");
                //注册接口
                new Thread(){
                    @Override
                    public void run(){
                        //todo 启动server

                    }
                }.start();
            }
        }
    };
}
