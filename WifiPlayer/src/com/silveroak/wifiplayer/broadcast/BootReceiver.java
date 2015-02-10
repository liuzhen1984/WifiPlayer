package com.silveroak.wifiplayer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by haoquanqing on 14-6-4.
 */
public class BootReceiver extends BroadcastReceiver {
    public void onReceive(Context ctx, Intent intent) {
        String action = "android.intent.action.BOOT_COMPLETED";
        if (intent.getAction().equals(action)) {
            startApplication(ctx);
            startAppService(ctx);
        }
    }

    protected void startApplication(Context ctx){
        if(ctx == null){
            return;
        }

    }

    protected void startAppService(Context ctx){
        if(ctx == null){
            return;
        }

        Intent tcIntent = new Intent();
        tcIntent.setAction("com.silveroak.wifiplayer.service.WifiPlayerService");
        tcIntent.addCategory(Intent.CATEGORY_DEFAULT);
        ctx.startService(tcIntent);
    }
}
