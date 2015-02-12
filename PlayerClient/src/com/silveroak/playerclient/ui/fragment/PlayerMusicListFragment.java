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
public class PlayerMusicListFragment extends PlayerBaseSearchBarFragment {
    public static PlayerMusicListFragment newInstance() {
        return new PlayerMusicListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_music_list, null);
        return v;
    }

    @Override
    public void handleSearch(String searchTxt) {

    }
}
