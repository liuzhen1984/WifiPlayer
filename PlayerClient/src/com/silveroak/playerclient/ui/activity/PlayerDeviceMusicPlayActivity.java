package com.silveroak.playerclient.ui.activity;

import android.content.Intent;
import android.view.MenuItem;
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

    @Override
    protected void initTitle() {
        super.initTitle();
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
