package com.silveroak.playerclient.ui;

import android.app.Application;
import android.util.Log;
import com.silveroak.playerclient.exception.AppCrashException;

/**
 * Created by zliu on 2014/6/22.
 */
public class BootApplication extends Application{
    private final static String TAG = "BootApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"Init service BootApplication");
        AppCrashException crashException = AppCrashException.getInstance();
        crashException.init(getApplicationContext());
    }

}
