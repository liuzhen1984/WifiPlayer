package com.silveroak.playerclient.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.silveroak.playerclient.R;
import com.silveroak.playerclient.constants.MessageConstant;
import com.silveroak.playerclient.domain.WifiConfig;
import com.silveroak.playerclient.service.IHandlerWhatAndKey;
import com.silveroak.playerclient.service.SearchDeviceService;
import com.silveroak.playerclient.utils.LogUtils;
import com.silveroak.playerclient.utils.NetworkUtils;

/**
 * Created by John on 2015/2/11.
 */
public class PlayerConfigureActivity extends Activity implements IHandlerWhatAndKey {
    private static final String TAG = PlayerConfigureActivity.class.getSimpleName();

    public static PlayerConfigureActivity THIS=null;
    private Handler handler = null;
    private EditText etPassword;
    private EditText etSSID;
    private Button buttonConfig;
    private WifiManager wifiManager;

    public static Handler getHandler(){
        if(THIS!=null) {
            return THIS.handler;
        }
        return null;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_configure_wifi);
        THIS = this;
        this.wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);;

        if(!wifiManager.isWifiEnabled()){
            Intent intent = new Intent();
            intent.setClass(PlayerConfigureActivity.this,PlayerSearchDeviceActivity.class);
            startActivity(intent);
            finish();
        }
        etPassword = (EditText) findViewById(R.id.et_password);
        etSSID = (EditText) findViewById(R.id.et_ssid);

        final WifiConfig wifiConfig = NetworkUtils.getWifiConfig(wifiManager);
        if(wifiConfig!=null){
            etSSID.setText(wifiConfig.getSsid());
        }

        buttonConfig = (Button) findViewById(R.id.bt_config);
        buttonConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            wifiConfig.setSsid(etSSID.getText().toString());
            wifiConfig.setPassword(etPassword.getText().toString());
            SearchDeviceService.init(getApplication()).configDevice(wifiConfig);
                //todo 弹出一个等待图片，锁屏
            }
        });


        handler =  new Handler(){
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CONFIG_DEVICE_MESSAGE: // 命令处理
                        LogUtils.debug(TAG, msg.getData().getString(MESSAGE_KEY));
                        String cmd = msg.getData().getString(MESSAGE_KEY);
                        if (cmd == null) {
                            return;
                        }
                        //todo 进入配置页面
                        if (cmd.equals(MessageConstant.SEARCH_DEVICE_CMD.COMPLETE.getCmd())) {
                            Intent intent = new Intent();
                            intent.setClass(PlayerConfigureActivity.this,PlayerSearchDeviceActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }
    private void msg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


}
