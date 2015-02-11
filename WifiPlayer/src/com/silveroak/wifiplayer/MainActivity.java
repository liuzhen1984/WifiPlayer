package com.silveroak.wifiplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.silveroak.wifiplayer.constants.SystemConstant;
import com.silveroak.wifiplayer.service.InitService;
import com.silveroak.wifiplayer.service.WifiPlayerService;
import com.silveroak.wifiplayer.service.business.MusicPlayerServer;
import com.silveroak.wifiplayer.utils.SysTools;

public class MainActivity extends Activity {
    private static final int PROCESSING = 1;
    private static final int FAILURE = -1;

    private TextView resultView;
    private ProgressBar progressBar;
    private Button startBtn;
    private Button pausedBtn;
    private Button stopBtn;
    private Button pBtn;
    private Button nBtn;
    private MusicPlayerServer player; // 播放器
    private SeekBar musicProgress; // 音乐进度

    private Handler handler = new UIHandler();

    private final class UIHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROCESSING: // 更新进度
                    progressBar.setProgress(msg.getData().getInt("size"));
                    float num = (float) progressBar.getProgress()
                            / (float) progressBar.getMax();
                    int result = (int) (num * 100); // 计算进度
                    resultView.setText(result + "%");
                    if (progressBar.getProgress() == progressBar.getMax()) { // 下载完成
                        Toast.makeText(getApplicationContext(), R.string.success,
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case FAILURE: // 下载失败
                    Toast.makeText(getApplicationContext(), R.string.error,
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
    private TextView showIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        showIp = (TextView) findViewById(R.id.show_local_ip);

        showIp.setText(SysTools.getLocalIP(getApplicationContext())+":"+ SystemConstant.PORT.HTTP_SERVER_PORT+"\n");
        showIp.append(SysTools.getLocalIP(getApplicationContext()) + ":" + SystemConstant.PORT.TCP_SERVER_PORT);

        resultView = (TextView) findViewById(R.id.resultView);
        startBtn = (Button) findViewById(R.id.btn_online_start);
        pausedBtn = (Button) findViewById(R.id.btn_online_paused);
        stopBtn = (Button) findViewById(R.id.btn_online_stop);
        musicProgress = (SeekBar) findViewById(R.id.music_progress);
        pBtn = (Button) findViewById(R.id.btn_online_p);
        nBtn = (Button) findViewById(R.id.btn_online_n);


        ButtonClickListener listener = new ButtonClickListener();
        startBtn.setOnClickListener(listener);
        pausedBtn.setOnClickListener(listener);
        stopBtn.setOnClickListener(listener);
        pBtn.setOnClickListener(listener);
        nBtn.setOnClickListener(listener);
        player = MusicPlayerServer.getService(musicProgress,getApplicationContext());

        musicProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        InitService.init(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), WifiPlayerService.class);
        startService(intent);
    }

    private final class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_online_paused:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            player.paused();
                        }
                    }).start();
                    break;
                case R.id.btn_online_stop:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            player.stop();
                        }
                    }).start();
                    break;
                case R.id.btn_online_start:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            player.start();
                        }
                    }).start();
                    break;
                case R.id.btn_online_n:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            player.next();
                        }
                    }).start();
                    break;
                case R.id.btn_online_p:
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            player.previous();
                        }
                    }).start();
                    break;
            }
        }
    }

    // 进度改变
    class SeekBarChangeEvent implements OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
            this.progress = progress * player.getCurrentRate()
                    / seekBar.getMax();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
            player.seekTo(progress);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player = null;
        }
    }

}
