package com.silveroak.playerclient.ui.activity;

import android.content.Intent;
import android.view.MenuItem;
import com.silveroak.playerclient.R;
import com.silveroak.playerclient.ui.base.PlayerBaseFragment;
import com.silveroak.playerclient.ui.base.PlayerSingleFragmentActivity;
import com.silveroak.playerclient.ui.fragment.PlayerConfigureFragment;

/**
 * Created by John on 2015/2/11.
 */
public class PlayerConfigureActivity extends PlayerSingleFragmentActivity {
    @Override
    protected PlayerBaseFragment createFragment() {
        return PlayerConfigureFragment.newInstance();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        setTitle(R.string.company);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, PlayerDeviceMusicListActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
