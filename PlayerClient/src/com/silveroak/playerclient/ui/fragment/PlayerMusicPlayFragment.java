package com.silveroak.playerclient.ui.fragment;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.silveroak.playerclient.R;
import com.silveroak.playerclient.constants.SystemConstant;
import com.silveroak.playerclient.domain.Music;
import com.silveroak.playerclient.domain.PlayerInfo;
import com.silveroak.playerclient.service.business.PanelClient;
import com.silveroak.playerclient.service.business.SearchMusic;
import com.silveroak.playerclient.ui.base.PlayerBaseFragment;
import com.silveroak.playerclient.ui.base.PlayerBaseSearchBarFragment;
import com.silveroak.playerclient.utils.JsonUtils;
import com.silveroak.playerclient.utils.LogUtils;
import io.netty.channel.Channel;

import java.util.Map;

/**
 * Created by John on 2015/2/11.
 */
public class PlayerMusicPlayFragment extends PlayerBaseSearchBarFragment {
    private final static String TAG = PlayerMusicPlayFragment.class.getSimpleName();

    public static PlayerMusicPlayFragment newInstance() {
        return new PlayerMusicPlayFragment();
    }

    private SeekBar processSeekBar;
    private TextView musicInfo;

    private ImageButton nextButton;
    private ImageButton previousButton;
    private ImageButton playButton;

    private SystemConstant.PLAYER_STATUS PLAYER_STATUS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_music_play, null);
        processSeekBar = (SeekBar) v.findViewById(R.id.sbSongProcess);
        musicInfo = (TextView) v.findViewById(R.id.tvSongName);
        nextButton = (ImageButton) v.findViewById(R.id.imgBtnPreviousSong);
        previousButton = (ImageButton) v.findViewById(R.id.imgBtnPreviousSong);
        playButton = (ImageButton) v.findViewById(R.id.imgBtnPlayPause);

        ButtonClickListener buttonClickListener = new ButtonClickListener();
        nextButton.setOnClickListener(buttonClickListener);
        previousButton.setOnClickListener(buttonClickListener);
        playButton.setOnClickListener(buttonClickListener);

        musicInfo.setText("No music");
        processSeekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        syncInfo();
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isSync=false;
    }

    @Override
    public void handleSearch(final String searchTxt) {
        if (searchTxt.length() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //todo 调用百度获取接口
                    Music music = SearchMusic.getSearchMusic(mActivity.getApplicationContext()).getMusic(searchTxt);
                    if (music != null) {
                        PanelClient.init(mActivity.getApplicationContext()).sendTo("/play/add",JsonUtils.object2String(music));
                    } else {
                        LogUtils.debug(TAG, "No find ");
                        sendToUIMessage("No find " + searchTxt);
                    }
                }
            }).start();
        }
    }
    private void sendToUIMessage(String msg){
        Message toUI = new Message();
        Bundle bundle = new Bundle();
        bundle.putString(PlayerBaseFragment.MESSAGE_KEY, msg);
        toUI.setData(bundle);
        toUI.what = PlayerBaseFragment.MESSAGE;
        PlayerBaseFragment.sendMessages(toUI);
    }
    @Override
    protected void handleMsg(Message message) {
        super.handleMsg(message);

        switch (message.what) {
            case PROCESSING: // 更新进度
                try {
                    String strMsg = message.getData().getString(MESSAGE_KEY);
                    Map<String, Object> seekMap = JsonUtils.string2Map(strMsg);
                    long pos = 0l;
                    int duration = Integer.parseInt(String.valueOf(seekMap.get("duration")));
                    int position = Integer.parseInt(String.valueOf(seekMap.get("position")));
                    if (duration > 0) {
                        // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
                        pos = processSeekBar.getMax() * position / duration;
                        processSeekBar.setProgress((int) pos);
                    }
                } catch (Exception ex) {
                    ex.toString();
                }
                break;
            case UPDATE_PLAY_INFO:
                LogUtils.debug(TAG, "UPDATE_INFO");
                PlayerInfo playerInfo = JsonUtils.string2Object(message.getData().getString(MESSAGE_KEY), PlayerInfo.class);
                if(playerInfo!=null){
                    musicInfo.setText(playerInfo.getMusic().getSongName()+"  Artiste:"+playerInfo.getMusic().getArtistName());
                    PLAYER_STATUS = playerInfo.getStatus();
                    if(playerInfo.getStatus().equals(SystemConstant.PLAYER_STATUS.PLAYER)){
                        //todo 把图片改为暂停的
                        playButton.setImageDrawable(getResources().getDrawable(R.drawable.pause_song));
                    } else{
                        //todo 把图片改为播放的
                        playButton.setImageDrawable(getResources().getDrawable(R.drawable.play_song));
                    }
                }
                break;
            case MESSAGE: // 消息显示
                LogUtils.debug(TAG, message.getData().getString(MESSAGE_KEY));
                msg(message.getData().getString(MESSAGE_KEY));
                break;
            default:
                break;
        }
    }

    private boolean isSync=false;
    private void syncInfo(){
        if(isSync){
            return;
        }
        isSync = true;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                 while (isSync){
                     PanelClient.init(mActivity.getApplicationContext()).sendTo("/play/sync","");
                     try {
                         Thread.sleep(5000l);
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                 }
            }
        };
        new Thread(runnable).start();
    }

    private final class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Channel channel = PanelClient.init(mActivity.getApplicationContext()).getChannel();
            switch (v.getId()) {
                case R.id.imgBtnPlayPause:
                    if(PLAYER_STATUS.equals(SystemConstant.PLAYER_STATUS.PAUSED)){
                        PanelClient.getClient().sendTo("/play/start",null);
                    } else{
                        PanelClient.getClient().sendTo("/play/paused",null);
                    }
                    break;
                case R.id.imgBtnNextSong:
                    PanelClient.getClient().sendTo("/play/next",null);
                    break;
                case R.id.imgBtnPreviousSong:
                    PanelClient.getClient().sendTo("/play/previous",null);
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
}
