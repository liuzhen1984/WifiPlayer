package com.silveroak.playerclient.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.silveroak.playerclient.R;

/**
 * Created by John on 2015/2/10.
 */
public class PlayerSearchDeviceActivity extends Activity {

    private TextView tvSearchDevicehint;
    private ImageButton imgBtnRefreshSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_search_device);


        tvSearchDevicehint = (TextView) findViewById(R.id.tvSearchDevicehint);
        imgBtnRefreshSearch = (ImageButton) findViewById(R.id.imgBtnRefreshSearch);
        imgBtnRefreshSearch.setBackgroundResource(R.drawable.refresh);

        imgBtnRefreshSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    v.setBackgroundResource(R.drawable.refresh_push);
                    //TODO 调用查找设备的方法
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    v.setBackgroundResource(R.drawable.refresh);
                }

                return false;
            }
        });


    }
}
