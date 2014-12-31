package com.silveroak.wifiplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.silveroak.wifiplayer.constants.SystemConstant;
import com.silveroak.wifiplayer.service.WifiPlayerService;
import com.silveroak.wifiplayer.utils.SysTools;

public class WifiPlayerActivity extends Activity {
    private TextView showIp;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent intent = new Intent(getApplicationContext(), WifiPlayerService.class);
        startService(intent);

        showIp = (TextView) findViewById(R.id.show_local_ip);

        showIp.setText(SysTools.getLocalIP(getApplicationContext())+":"+String.valueOf(SystemConstant.PORT.HTTP_SERVER_PORT));


    }
}
