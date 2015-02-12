package com.silveroak.playerclient.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.silveroak.playerclient.R;
import com.silveroak.playerclient.constants.MessageConstant;
import com.silveroak.playerclient.service.SearchDeviceService;
import com.silveroak.playerclient.ui.activity.PlayerConfigureActivity;
import com.silveroak.playerclient.ui.activity.PlayerDeviceMusicActivity;
import com.silveroak.playerclient.ui.base.PlayerBaseFragment;
import com.silveroak.playerclient.utils.LogUtils;

/**
 * Created by John on 2015/2/12.
 */
public class PlayerSearchDeviceFragment extends PlayerBaseFragment {
    private static final String TAG = PlayerSearchDeviceFragment.class.getSimpleName();

    private TextView tvSearchDevicehint;
    private ImageButton imgBtnRefreshSearch;

    public static PlayerBaseFragment newInstance() {
        return new PlayerSearchDeviceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search_device, null);

        tvSearchDevicehint = (TextView) view.findViewById(R.id.tvSearchDevicehint);
        imgBtnRefreshSearch = (ImageButton) view.findViewById(R.id.imgBtnRefreshSearch);
        imgBtnRefreshSearch.setBackgroundResource(R.drawable.refresh);
        SearchDeviceService.init(mActivity.getApplicationContext()).search();

        imgBtnRefreshSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundResource(R.drawable.refresh_push);
                    //TODO 调用查找设备的方法
                    SearchDeviceService.init(mActivity.getApplicationContext()).search();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    SearchDeviceService.init(getApplicationContext()).closeListen();
                    v.setBackgroundResource(R.drawable.refresh);
                }
                return false;
            }
        });

        return view;
    }

    @Override
    protected void handleMsg(Message msg) {
        super.handleMsg(msg);

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
                    intent.setClass(mActivity, PlayerConfigureActivity.class);
                    msg("Config device ...");
                    startActivity(intent);

                } else
                    //todo 进入列表页面
                    if (cmd.equals(MessageConstant.SEARCH_DEVICE_CMD.COMPLETE.getCmd())) {
                        intent.setClass(mActivity, PlayerDeviceMusicActivity.class);
                        SearchDeviceService.init(mActivity.getApplicationContext()).closeListen();
                        msg("Connect device successful");
                        startActivityForResult(intent, 0);
                        mActivity.finish();
                    } else if (cmd.equals(MessageConstant.SEARCH_DEVICE_CMD.DO_CONFIG_WIFI.getCmd())) {
                        msg("正在配置稍后重试");
                        SearchDeviceService.init(mActivity.getApplicationContext()).search();
                    } else {
                        //todo 没有找到设备，及热点，停留在次页面
                        msg("No find device");
                    }
                break;
            default:
                break;
        }
    }
}
