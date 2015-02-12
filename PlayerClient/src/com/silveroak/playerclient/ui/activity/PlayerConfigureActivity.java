package com.silveroak.playerclient.ui.activity;

import com.silveroak.playerclient.ui.base.PlayerSingleFragmentActivity;
import com.silveroak.playerclient.ui.base.PlayerBaseFragment;
import com.silveroak.playerclient.ui.fragment.PlayerConfigureFragment;

/**
 * Created by John on 2015/2/11.
 */
public class PlayerConfigureActivity extends PlayerSingleFragmentActivity {
    @Override
    protected PlayerBaseFragment createFragment() {
        return PlayerConfigureFragment.newInstance();
    }
}
