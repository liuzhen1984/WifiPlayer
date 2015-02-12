package com.silveroak.playerclient.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.silveroak.playerclient.R;
import com.silveroak.playerclient.constants.MessageConstant;
import com.silveroak.playerclient.domain.WifiConfig;
import com.silveroak.playerclient.service.SearchDeviceService;
import com.silveroak.playerclient.ui.activity.PlayerSearchDeviceActivity;
import com.silveroak.playerclient.utils.LogUtils;
import com.silveroak.playerclient.utils.NetworkUtils;

/**
 * Created by John on 2015/2/11.
 */
public class PlayerConfigureFragment extends PlayerBaseFragment {
    private static final String TAG = PlayerConfigureFragment.class.getSimpleName();

    private EditText etPassword;
    private EditText etSSID;
    private Button buttonConfig;
    private WifiManager wifiManager;

    public static PlayerConfigureFragment newInstance() {
        return new PlayerConfigureFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.player_search_device, null);

        this.wifiManager = (WifiManager) mActivity.getSystemService(Context.WIFI_SERVICE);;

        if(!wifiManager.isWifiEnabled()){
            Intent intent = new Intent();
            intent.setClass(mActivity,PlayerSearchDeviceActivity.class);
            startActivity(intent);
            mActivity.finish();
        }
        etPassword = (EditText) view.findViewById(R.id.et_password);
        etSSID = (EditText) view.findViewById(R.id.et_ssid);

        final WifiConfig wifiConfig = NetworkUtils.getWifiConfig(wifiManager);
        if(wifiConfig!=null){
            etSSID.setText(wifiConfig.getSsid());
        }

        buttonConfig = (Button) view.findViewById(R.id.bt_config);
        buttonConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiConfig.setSsid(etSSID.getText().toString());
                wifiConfig.setPassword(etPassword.getText().toString());
                SearchDeviceService.init(mActivity.getApplication()).configDevice(wifiConfig);
                //todo 弹出一个等待图片，锁屏
            }
        });

        return view;
    }

    @Override
    protected void handleMsg(Message msg) {
        super.handleMsg(msg);
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
                    intent.setClass(mActivity,PlayerSearchDeviceActivity.class);
                    startActivity(intent);
                    mActivity.finish();
                }
                break;
            default:
                break;
        }
    }
}
