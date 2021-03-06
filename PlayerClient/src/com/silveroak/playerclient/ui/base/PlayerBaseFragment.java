package com.silveroak.playerclient.ui.base;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.silveroak.playerclient.R;
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
        Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    public void onResume() {
        super.onResume();
        if (isHandlerNeeded) {
            initHandler();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (handler != null) {
            handlers.remove(handler);
            handler = null;
        }
    }

    private Dialog mProgressDialog = null;

    public void showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = new Dialog(getActivity(), R.style.CustomDialog);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setContentView(R.layout.widget_progress_dialog);
        }
        mProgressDialog.show();
    }

    public void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgress();
    }

    private static ArrayList<Handler> handlers = new ArrayList<Handler>();

    public static void sendMessages(Message msg) {
        for (Handler h : handlers) {
            h.sendMessage(msg);
        }
    }

    protected void handleMsg(Message msg) {

    }
}
