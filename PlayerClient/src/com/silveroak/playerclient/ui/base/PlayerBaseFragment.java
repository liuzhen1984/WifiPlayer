package com.silveroak.playerclient.ui.base;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.silveroak.playerclient.service.IHandlerWhatAndKey;

import java.util.ArrayList;

/**
 * Created by John on 2015/2/12.
 */
public class PlayerBaseFragment extends Fragment  implements IHandlerWhatAndKey {
    public PlayerBaseActivity mActivity;

    public boolean isHandlerNeeded = true;
    private Handler handler = null;

    protected void msg(String msg) {
        try {
            Looper.prepare();
            Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
            Looper.loop();
        }catch (Exception ex){}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (isHandlerNeeded) {
            initHandler();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initHandler() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                handleMsg(msg);
            }
        };
        handlers.add(handler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handlers.remove(handler);
            handler = null;
        }
    }

    private static ArrayList<Handler> handlers = new ArrayList<Handler>();

    public static void sendMessages(Message msg) {
        for (Handler h : handlers) {
            h.handleMessage(msg);
        }
    }

    protected void handleMsg(Message msg) {

    }
}
