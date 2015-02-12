package com.silveroak.playerclient.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.silveroak.playerclient.R;

/**
 * Created by John on 2015/2/11.
 */
public class PlayerTopBarFragment extends Fragment {

    private ImageButton imgBtnBack,imgBtnConfig;
    private TextView tvPlayerName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.top_bar,null);

        imgBtnBack = (ImageButton) v.findViewById(R.id.imgBtnBack);
        imgBtnConfig = (ImageButton) v.findViewById(R.id.imgBtnConfig);
        tvPlayerName = (TextView) v.findViewById(R.id.tvPlayerName);


        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 切换到PlayMusicListFragment


            }
        });

        imgBtnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 切换到PlayerConfigureActivity


            }
        });



        return v;
    }

    public static PlayerTopBarFragment newInstance(){
        return new PlayerTopBarFragment();
    }

}
