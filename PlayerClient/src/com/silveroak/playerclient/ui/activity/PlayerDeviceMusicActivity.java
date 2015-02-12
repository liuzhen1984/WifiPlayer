package com.silveroak.playerclient.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import com.silveroak.playerclient.R;
import com.silveroak.playerclient.service.IHandlerWhatAndKey;

/**
 * Created by John on 2015/2/12.
 */
public class PlayerDeviceMusicActivity extends Activity  implements IHandlerWhatAndKey {

    private static final String TAG = PlayerDeviceMusicActivity.class.getSimpleName();


    public static PlayerDeviceMusicActivity THIS=null;
    private Handler handler = null;

    public static Handler getHandler(){
        if(THIS!=null) {
            return THIS.handler;
        }
        return null;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_device_music);
        THIS = this;

        handler =  new Handler(){
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    default:
                        break;
                }
            }
        };
    }
    private void msg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }



}