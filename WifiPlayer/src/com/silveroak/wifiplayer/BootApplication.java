package com.silveroak.wifiplayer;

import android.app.Application;
import android.util.Log;
import com.silveroak.wifiplayer.exception.AppCrashException;

/**
 * Created by zliu on 2014/6/22.
 */
public class BootApplication extends Application{
    private final static String TAG = BootApplication.class.getSimpleName();

    private static Application BOOT_APP=null;

    public static Application getBootApp(){
        return BOOT_APP;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"Init service BootApplication");



        AppCrashException crashException = AppCrashException.getInstance(getApplicationContext());
//        Intent intent = new Intent(this, WifiPlayerService.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        startService(intent);
    }
}
