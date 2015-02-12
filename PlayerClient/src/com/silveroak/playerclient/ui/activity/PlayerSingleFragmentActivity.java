package com.silveroak.playerclient.ui.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import com.silveroak.playerclient.R;
import com.silveroak.playerclient.ui.fragment.PlayerBaseFragment;

/**
 * Created by John on 2015/2/12.
 */
public class PlayerSingleFragmentActivity extends PlayerBaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
    }

    @Override
    protected void initFragment() {
        super.initFragment();

        FragmentManager fm = getFragmentManager();
        PlayerBaseFragment fragment = (PlayerBaseFragment) fm.findFragmentById(R.id.container);
        if (fragment == null) {
            fragment = createFragment();
            fragment.mActivity = this;
            fm.beginTransaction().add(R.id.container, fragment);
        }
    }

    protected PlayerBaseFragment createFragment() {
        return null;
    }
}
