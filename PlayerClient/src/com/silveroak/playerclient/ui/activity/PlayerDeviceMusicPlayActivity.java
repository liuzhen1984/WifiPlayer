package com.silveroak.playerclient.ui.activity;

import com.silveroak.playerclient.ui.base.PlayerBaseSearchBarActivity;
import com.silveroak.playerclient.ui.base.PlayerBaseSearchBarFragment;
import com.silveroak.playerclient.ui.fragment.PlayerMusicPlayFragment;

/**
 * Created by John on 2015/2/12.
 */
public class PlayerDeviceMusicPlayActivity extends PlayerBaseSearchBarActivity {
    @Override
    protected PlayerBaseSearchBarFragment createFragment() {
        return PlayerMusicPlayFragment.newInstance();
    }
}
