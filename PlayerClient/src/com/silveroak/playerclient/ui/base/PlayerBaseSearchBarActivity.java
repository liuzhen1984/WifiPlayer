package com.silveroak.playerclient.ui.base;

import android.app.FragmentManager;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import com.silveroak.playerclient.R;
import com.silveroak.playerclient.domain.TcpRequest;
import com.silveroak.playerclient.service.business.PanelClient;
import com.silveroak.playerclient.utils.JsonUtils;

/**
 * Created by John on 2015/2/12.
 */
public class PlayerBaseSearchBarActivity extends PlayerBaseActivity {
    protected SearchView mSearchView;
    protected Button mSearchBtn;

    protected PlayerBaseSearchBarFragment mFragment;

    @Override
    protected void initContentView() {
        super.initContentView();
        setContentView(R.layout.activity_search_bar);

        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchBtn = (Button) findViewById(R.id.imgBtnSearch);
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragment.handleSearch(mSearchView.getQuery().toString());
            }
        });
    }

    @Override
    protected void initFragment() {
        super.initFragment();

        FragmentManager fm = getFragmentManager();
        PlayerBaseSearchBarFragment fragment = (PlayerBaseSearchBarFragment) fm.findFragmentById(R.id.container);
        if (fragment == null) {
            fragment = createFragment();
            fragment.mActivity = this;
            fm.beginTransaction().add(R.id.contentContainer, fragment).commit();
            mFragment = fragment;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (PanelClient.getClient() == null) {

            msg("Panel client to wifi player server");
            return false;
        }
        if (PanelClient.getClient().getChannel() == null || !PanelClient.getClient().getChannel().isOpen()) {
            msg("Panel client to wifi player server");
            return false;
        }
        TcpRequest tcpRequest = new TcpRequest();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                tcpRequest.setUrl("/play/volume");
                tcpRequest.setPayload(String.valueOf(AudioManager.ADJUST_RAISE));
                PanelClient.getClient().getChannel().writeAndFlush(JsonUtils.object2String(tcpRequest));
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                tcpRequest.setUrl("/play/volume");
                tcpRequest.setPayload(String.valueOf(AudioManager.ADJUST_LOWER));
                PanelClient.getClient().getChannel().writeAndFlush(JsonUtils.object2String(tcpRequest));
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected PlayerBaseSearchBarFragment createFragment() {
        return null;
    }
}
