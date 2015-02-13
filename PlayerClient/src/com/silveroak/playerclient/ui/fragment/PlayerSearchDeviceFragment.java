package com.silveroak.playerclient.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.silveroak.playerclient.R;
import com.silveroak.playerclient.constants.MessageConstant;
import com.silveroak.playerclient.service.SearchDeviceService;
import com.silveroak.playerclient.ui.activity.PlayerConfigureActivity;
import com.silveroak.playerclient.ui.activity.PlayerDeviceMusicListActivity;
import com.silveroak.playerclient.ui.base.PlayerBaseFragment;
import com.silveroak.playerclient.utils.LogUtils;

/**
 * Created by John on 2015/2/12.
 */
public class PlayerSearchDeviceFragment extends PlayerBaseFragment {
    private static final String TAG = PlayerSearchDeviceFragment.class.getSimpleName();

    private TextView tvSearchDevicehint;
    private ImageView imgBtnRefreshSearch;

    private boolean isSearching;

    public static PlayerBaseFragment newInstance() {
        return new PlayerSearchDeviceFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        searchDevice();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search_device, null);

        tvSearchDevicehint = (TextView) view.findViewById(R.id.tvSearchDevicehint);
        imgBtnRefreshSearch = (ImageView) view.findViewById(R.id.imgBtnRefreshSearch);
        imgBtnRefreshSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDevice();
            }
        });

        return view;
    }

    private void searchDevice() {
        if (!isSearching && mActivity != null) {
            isSearching = true;
            ((AnimationDrawable) imgBtnRefreshSearch.getDrawable()).start();
            SearchDeviceService.init(mActivity.getApplicationContext()).search();
        }
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
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    mActivity.finish();
                    msg("Config device ...");
                } else
                    //todo 进入列表页面
                    if (cmd.equals(MessageConstant.SEARCH_DEVICE_CMD.COMPLETE.getCmd())) {
                        intent.setClass(mActivity, PlayerDeviceMusicListActivity.class);
                        SearchDeviceService.init(mActivity.getApplicationContext()).closeListen();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        msg("Connect device successful");
                        isSearching = false;
                        ((AnimationDrawable)imgBtnRefreshSearch.getDrawable()).stop();
                    } else if (cmd.equals(MessageConstant.SEARCH_DEVICE_CMD.DO_CONFIG_WIFI.getCmd())) {
                        msg("正在配置稍后重试");
                        isSearching = false;
                        searchDevice();
                    } else {
                        //todo 没有找到设备，及热点，停留在次页面
                        msg("No find device");
                        isSearching = false;
                        ((AnimationDrawable)imgBtnRefreshSearch.getDrawable()).stop();
                    }
                break;
            default:
                break;
        }
    }
}
