package com.silveroak.playerclient;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.silveroak.playerclient.domain.TcpRequest;
import com.silveroak.playerclient.preference.StorageUtils;
import com.silveroak.playerclient.preference.data.SystemInfo;
import com.silveroak.playerclient.service.HttpServer.LocalHttpServer;
import com.silveroak.playerclient.service.IHandlerWhatAndKey;
import com.silveroak.playerclient.service.UDPService;
import com.silveroak.playerclient.service.business.PanelClient;
import com.silveroak.playerclient.service.business.SearchMusic;
import com.silveroak.playerclient.utils.JsonUtils;
import com.silveroak.playerclient.utils.LogUtils;
import io.netty.channel.Channel;

import java.util.Map;

public class ClientActivity extends Activity implements IHandlerWhatAndKey {
    private static final String TAG = ClientActivity.class.getSimpleName();


    private EditText searchText; // url地址
    private EditText pathText; // url地址
    private TextView resultView;
    private ProgressBar progressBar;
    private Button searchBtn;
    private Button playBtn;
    private Button startBtn;
    private Button pausedBtn;
    private Button stopBtn;
    private Button pBtn;
    private Button nBtn;
    private Button findBtn;
    private SeekBar musicProgress; // 音乐进度

    private StorageUtils storageUtils;

    public static ClientActivity THIS=null;
    private Handler handler = null;

    public static Handler getHandler(){
        if(THIS!=null) {
            return THIS.handler;
        }
        return null;
    }
    private TextView showIp;

    public static boolean isFind = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        THIS = this;
        setContentView(R.layout.main);
        handler =  new Handler(){
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case PROCESSING: // 更新进度
                        try {
                            LogUtils.debug(TAG,"processing");
                            String strMsg = msg.getData().getString(MESSAGE_KEY);
                            Map<String, Object> seekMap = JsonUtils.string2Map(strMsg);
                            long pos = 0l;
                            int duration = Integer.parseInt(String.valueOf(seekMap.get("duration")));
                            int position = Integer.parseInt(String.valueOf(seekMap.get("position")));
                            if (duration > 0) {
                                // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
                                pos = musicProgress.getMax() * position / duration;
                                musicProgress.setProgress((int) pos);
                            }
                        }catch (Exception ex){
                            ex.toString();
                        }
                        break;
                    case UPDATE_INFO:
                        LogUtils.debug(TAG,"UPDATE_INFO");
                        break;
                    case MESSAGE: // 消息显示
                        LogUtils.debug(TAG,msg.getData().getString(MESSAGE_KEY));
                        resultView.setText(String.valueOf(msg.getData().getString(MESSAGE_KEY)));
                        break;
                    default:
                        break;
                }
            }
        };

        storageUtils =  StorageUtils.getInstance(getApplicationContext());
        searchText = (EditText) findViewById(R.id.search);

        pathText = (EditText) findViewById(R.id.path);
        resultView = (TextView) findViewById(R.id.resultView);

        searchBtn = (Button) findViewById(R.id.bt_search);


        playBtn = (Button) findViewById(R.id.btn_online_play);
        startBtn = (Button) findViewById(R.id.btn_online_start);
        pausedBtn = (Button) findViewById(R.id.btn_online_paused);
        stopBtn = (Button) findViewById(R.id.btn_online_stop);
        musicProgress = (SeekBar) findViewById(R.id.music_progress);
        pBtn = (Button) findViewById(R.id.btn_online_p);
        nBtn = (Button) findViewById(R.id.btn_online_n);
        findBtn = (Button) findViewById(R.id.b_find_wifi_player_host);


        ButtonClickListener listener = new ButtonClickListener();
        playBtn.setOnClickListener(listener);
        startBtn.setOnClickListener(listener);
        pausedBtn.setOnClickListener(listener);
        stopBtn.setOnClickListener(listener);
        pBtn.setOnClickListener(listener);
        nBtn.setOnClickListener(listener);
        searchBtn.setOnClickListener(listener);
        findBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isFind = true;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                new Thread(UDPService.init(getApplication())).start();
                                while(isFind) {
                                    UDPService.init(getApplicationContext()).send("find");
                                    try {
                                        Thread.sleep(10000l);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //移动
                        break;
                    case MotionEvent.ACTION_UP:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                isFind = false;
                                UDPService.init(getApplicationContext()).closeListen();
                            }
                        }).start();
                        break;
                }
                return true;            }
        });

        musicProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String msg = "";
                SystemInfo systemInfo = storageUtils.pullSettingData(StorageUtils._CURRENT_PLAYER_LIST,SystemInfo.key(),SystemInfo.class);
                if(systemInfo!=null && systemInfo.getPort()!=null&&systemInfo.getServer()!=null) {
                    if (!PanelClient.init(getApplicationContext()).start(systemInfo)) {
                        msg = "Not connected to wifi player server";
                    } else {
                        msg = "Already connected to wifi player server:" + systemInfo.getServer() + ":" + systemInfo.getPort();
                    }
                } else{
                    msg = "Not connected to wifi player server";
                }
                Message toUI = new Message();
                Bundle bundle = new Bundle();
                bundle.putString(ClientActivity.MESSAGE_KEY,msg);
                toUI.setData(bundle);
                toUI.what = ClientActivity.MESSAGE;
                ClientActivity.getHandler().sendMessage(toUI);
//                resultView.setText(msg);
            }
        };
        new Thread(runnable).start();


        try {
            LocalHttpServer.start();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.error(TAG,e);
        }

    }

    private final class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Channel channel = PanelClient.init(getApplicationContext()).getChannel();
            switch (v.getId()) {
                case R.id.bt_search:
                    final String searchStr = searchText.getText().toString();
                    if(searchStr.length()>0) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                               //todo 调用百度获取接口
                                String url = SearchMusic.getSearchMusic(getApplicationContext()).getUrl(searchStr);
                                if(url!=null){
                                    LogUtils.debug(TAG, url);
                                    if(channel!=null){
                                        TcpRequest  tcpRequest = new TcpRequest();
                                        tcpRequest.setUrl("/play/play");
                                        tcpRequest.setPayload(url);
                                        channel.writeAndFlush(JsonUtils.object2String(tcpRequest));
                                    }
                                } else{
                                    LogUtils.debug(TAG,"No find " + searchStr);
                                }
                            }
                        }).start();
                    }
                    break;
                case R.id.btn_online_play:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            if(channel!=null){
                                TcpRequest  tcpRequest = new TcpRequest();
                                tcpRequest.setUrl("/play/play");
                                tcpRequest.setPayload(pathText.getText().toString());
                                channel.writeAndFlush(JsonUtils.object2String(tcpRequest));
                            }
                        }
                    }).start();
                    break;
                case R.id.btn_online_paused:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            if(channel!=null){
                                TcpRequest  tcpRequest = new TcpRequest();
                                tcpRequest.setUrl("/play/paused");
                                channel.writeAndFlush(JsonUtils.object2String(tcpRequest));
                            }
                        }
                    }).start();
                    break;
                case R.id.btn_online_stop:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            if(channel!=null){
                                TcpRequest  tcpRequest = new TcpRequest();
                                tcpRequest.setUrl("/play/stop");
                                channel.writeAndFlush(JsonUtils.object2String(tcpRequest));
                            }
                        }
                    }).start();
                    break;
                case R.id.btn_online_start:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            if(channel!=null){
                                TcpRequest  tcpRequest = new TcpRequest();
                                tcpRequest.setUrl("/play/start");
                                channel.writeAndFlush(JsonUtils.object2String(tcpRequest));
                            }
                        }
                    }).start();
                    break;
                case R.id.btn_online_n:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            if(channel!=null){
                                TcpRequest  tcpRequest = new TcpRequest();
                                tcpRequest.setUrl("/play/next");
                                channel.writeAndFlush(JsonUtils.object2String(tcpRequest));
                            }
                        }
                    }).start();
                    break;
                case R.id.btn_online_p:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            if(channel!=null){
                                TcpRequest  tcpRequest = new TcpRequest();
                                tcpRequest.setUrl("/play/previous");
                                channel.writeAndFlush(JsonUtils.object2String(tcpRequest));
                            }
                        }
                    }).start();
                    break;
            }
        }
    }

    // 进度改变
    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
//            this.progress = progress * player.getCurrentRate()
//                    / seekBar.getMax();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
//            player.seekTo(progress);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(PanelClient.getClient()==null){

            LogUtils.warn(TAG,"Panel client to wifi player server");
            msg("Panel client to wifi player server");
             return false;
        }
        if(PanelClient.getClient().getChannel()==null||!PanelClient.getClient().getChannel().isOpen()){
            LogUtils.warn(TAG,"Panel client to wifi player server");
            msg("Panel client to wifi player server");
            return false;

        }
        TcpRequest tcpRequest = new TcpRequest();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                tcpRequest.setUrl("/play/volume");
                tcpRequest.setPayload(String.valueOf(AudioManager.ADJUST_RAISE));
                PanelClient.getClient().getChannel().writeAndFlush(JsonUtils.object2String(tcpRequest));
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                tcpRequest.setUrl("/play/volume");
                tcpRequest.setPayload(String.valueOf(AudioManager.ADJUST_LOWER));
                PanelClient.getClient().getChannel().writeAndFlush(JsonUtils.object2String(tcpRequest));
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void msg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
