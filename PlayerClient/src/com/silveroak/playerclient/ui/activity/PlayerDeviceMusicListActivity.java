package com.silveroak.playerclient.ui.activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import com.silveroak.playerclient.R;
import com.silveroak.playerclient.ui.base.PlayerBaseSearchBarActivity;
import com.silveroak.playerclient.ui.base.PlayerBaseSearchBarFragment;
import com.silveroak.playerclient.ui.fragment.PlayerMusicListFragment;

/**
 * Created by John on 2015/2/12.
 */
public class PlayerDeviceMusicListActivity extends PlayerBaseSearchBarActivity {
    @Override
    protected PlayerBaseSearchBarFragment createFragment() {
        return PlayerMusicListFragment.newInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_music_list, menu);
        return true;
    }

    @Override
    protected void initTitle() {
        super.initTitle();
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, PlayerDeviceMusicPlayActivity.class);
                startActivity(intent);
                break;
            case R.id.action_config:
                intent = new Intent(this, PlayerConfigureActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
