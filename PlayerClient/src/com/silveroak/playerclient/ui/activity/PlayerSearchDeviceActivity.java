package com.silveroak.playerclient.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.silveroak.playerclient.R;
import com.silveroak.playerclient.constants.MessageConstant;
import com.silveroak.playerclient.service.IHandlerWhatAndKey;
import com.silveroak.playerclient.service.SearchDeviceService;
import com.silveroak.playerclient.utils.LogUtils;

/**
 * Created by John on 2015/2/10.
 */
public class PlayerSearchDeviceActivity extends Activity implements IHandlerWhatAndKey {
    private static final String TAG = PlayerSearchDeviceActivity.class.getSimpleName();

    private TextView tvSearchDevicehint;
    private ImageButton imgBtnRefreshSearch;

    public static PlayerSearchDeviceActivity THIS = null;
    private Handler handler = null;

    public static Handler getHandler() {
        if (THIS != null) {
            return THIS.handler;
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_search_device);
        THIS = this;

        tvSearchDevicehint = (TextView) findViewById(R.id.tvSearchDevicehint);
        imgBtnRefreshSearch = (ImageButton) findViewById(R.id.imgBtnRefreshSearch);
        imgBtnRefreshSearch.setBackgroundResource(R.drawable.refresh);
        SearchDeviceService.init(getApplicationContext()).search();

        imgBtnRefreshSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundResource(R.drawable.refresh_push);
                    //TODO 调用查找设备的方法
                    SearchDeviceService.init(getApplicationContext()).search();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    SearchDeviceService.init(getApplicationContext()).closeListen();
                    v.setBackgroundResource(R.drawable.refresh);
                }
                return false;
            }
        });


        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SEARCH_DEVICE_MESSAGE: // 命令处理
                        LogUtils.debug(TAG, msg.getData().getString(MESSAGE_KEY));
                        String cmd = msg.getData().getString(MESSAGE_KEY);
                        if (cmd == null) {
                            return;
                        }
                        Intent intent = new Intent();
                        //todo 进入配置页面
                        if (cmd.equals(MessageConstant.SEARCH_DEVICE_CMD.IN_CONFIG_PAGE.getCmd())) {
                            intent.setClass(PlayerSearchDeviceActivity.this, PlayerConfigureActivity.class);
                            msg("Config device ...");
                            startActivity(intent);

                        } else
                            //todo 进入列表页面
                            if (cmd.equals(MessageConstant.SEARCH_DEVICE_CMD.COMPLETE.getCmd())) {
                                intent.setClass(PlayerSearchDeviceActivity.this, PlayerDeviceMusicActivity.class);
                                SearchDeviceService.init(getApplicationContext()).closeListen();
                                msg("Connect device successful");
                                startActivityForResult(intent, 0);
                                finish();
                            } else if (cmd.equals(MessageConstant.SEARCH_DEVICE_CMD.DO_CONFIG_WIFI.getCmd())) {
                                msg("正在配置稍后重试");
                                SearchDeviceService.init(getApplicationContext()).search();
                            } else {
                                //todo 没有找到设备，及热点，停留在次页面
                                msg("No find device");
                            }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void msg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
