package com.silveroak.playerclient.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.silveroak.playerclient.R;
import com.silveroak.playerclient.ui.base.PlayerBaseSearchBarFragment;

/**
 * Created by John on 2015/2/11.
 */
public class PlayerMusicPlayFragment extends PlayerBaseSearchBarFragment {
    public static PlayerMusicPlayFragment newInstance() {
        return new PlayerMusicPlayFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_music_play, null);
        return v;
    }

    @Override
    public void handleSearch(String searchTxt) {

    }
}
