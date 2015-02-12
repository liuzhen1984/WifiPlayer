package com.silveroak.playerclient.ui.base;

import android.app.FragmentManager;
import com.silveroak.playerclient.R;

/**
 * Created by John on 2015/2/12.
 */
public class PlayerSingleFragmentActivity extends PlayerBaseActivity {
    @Override
    protected void initContentView() {
        super.initContentView();
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
            fm.beginTransaction().add(R.id.container, fragment).commit();
        }
    }

    protected PlayerBaseFragment createFragment() {
        return null;
    }
}
