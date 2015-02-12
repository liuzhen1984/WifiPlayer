package com.silveroak.playerclient.ui.activity;

import com.silveroak.playerclient.ui.fragment.PlayerBaseFragment;
import com.silveroak.playerclient.ui.fragment.PlayerSearchDeviceFragment;

/**
 * Created by John on 2015/2/10.
 */
public class PlayerSearchDeviceActivity extends PlayerSingleFragmentActivity {
    @Override
    protected PlayerBaseFragment createFragment() {
        return PlayerSearchDeviceFragment.newInstance();
    }
}
