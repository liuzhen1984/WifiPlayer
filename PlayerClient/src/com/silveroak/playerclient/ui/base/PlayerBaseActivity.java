package com.silveroak.playerclient.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by John on 2015/2/12.
 */
public class PlayerBaseActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle();
        initContentView();
        initFragment();

    }

    protected void initTitle() {
    }

    protected void initContentView(){

    }

    protected void initFragment() {

    }


}