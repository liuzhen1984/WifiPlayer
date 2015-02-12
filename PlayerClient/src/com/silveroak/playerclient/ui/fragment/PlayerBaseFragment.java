package com.silveroak.playerclient.ui.fragment;

import android.app.Fragment;
import android.widget.Toast;
import com.silveroak.playerclient.ui.activity.PlayerBaseActivity;

/**
 * Created by John on 2015/2/12.
 */
public class PlayerBaseFragment extends Fragment {
    public PlayerBaseActivity mActivity;

    protected void msg(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
    }
}
